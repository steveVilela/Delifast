package com.delifast.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.delifast.dto.DetalleVentaDTO;
import com.delifast.model.Producto;
import com.delifast.dto.VentaRequestDTO;
import com.delifast.model.Administrador;
import com.delifast.model.Receta;
import com.delifast.service.CategoriaService;
import com.delifast.service.ProductoService;
import com.delifast.model.Cliente; 
import com.delifast.service.ClienteService;
import com.delifast.service.InsumoService;
import com.delifast.service.MovimientoService;
import com.delifast.service.RecetaService;
import com.delifast.model.Pedido; 
import com.delifast.service.PedidoService;
import com.delifast.model.DetallePedido; 

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/administrador/ventas")
public class VentaPOSController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private PedidoService pedidoService; 

    @Autowired
    private InsumoService insumoService;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private RecetaService recetaService;
    
    // 🍕 1. Muestra la interfaz táctil del POS con los productos
    @GetMapping
    public String mostrarPOS(Model model, HttpSession session) {
        // 🔒 Filtro de seguridad básica: Si no está logueado, al Login
        if (session.getAttribute("admin") == null) {
            return "redirect:/administrador/login";
        }

        // 🚀 SINCRONIZACIÓN DINÁMICA: Traemos los productos activos pero recalculamos su stock según cocina
        List<Producto> productosPOS = productoService.listarTodos();
        for (Producto prod : productosPOS) {
            int stockRealInsumos = productoService.calcularStockRealPorInsumos(prod.getProductoId());
            prod.setStock(stockRealInsumos); // Inyectamos el stock real calculado con el 20% amortiguado
        }

        // Enviamos los productos dinámicos y categorías al modelo
        model.addAttribute("productos", productosPOS); 
        model.addAttribute("categorias", categoriaService.listarTodas());
        
        return "administrador/ventas/pos"; 
    }
    
    // 💵 2. Procesa la venta enviada desde JavaScript (AJAX)
    @PostMapping("/guardar")
    @ResponseBody 
    public Map<String, Object> guardarVentaPOS(@RequestBody VentaRequestDTO request, HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            System.out.println("====== REGISTRANDO PEDIDO FÍSICO EN BD ======");
            
            // Recuperamos el administrador logueado para responsabilizarlo en el Kardex
            Administrador adminLogueado = (Administrador) session.getAttribute("admin");
            
            /* ====================================================================
               🔒 CANDADO DE SEGURIDAD PREVENTIVO EN CAJA (POS)
               Verificamos insumos antes de realizar cualquier descuento o registro
               ==================================================================== */
            for (DetalleVentaDTO item : request.getDetalles()) {
                boolean verificado = productoService.tieneInsumosSuficientes(item.getProductoId(), item.getCantidad());
                if (!verificado) {
                    Producto pAgotado = productoService.buscarPorId(item.getProductoId());
                    String nombreP = (pAgotado != null) ? pAgotado.getNombre() : "Producto";
                    
                    respuesta.put("status", "error");
                    respuesta.put("message", "¡Operación cancelada! El producto '" + nombreP + "' no cuenta con insumos libres suficientes en la cocina para esta venta presencial.");
                    return respuesta; // Cancela la transacción de inmediato de forma limpia
                }
            }

            // 1. Buscamos el objeto Cliente completo que se asoció en el buscador del POS
            Cliente clienteReal = clienteService.buscarPorId(request.getClienteId()); 
            
            /* ====================================================================
               A) INSTANCIAR Y LLENAR LA CABECERA DEL PEDIDO
               ==================================================================== */
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setCliente(clienteReal); 
            nuevoPedido.setMetodoPago(request.getMetodoPago());
            nuevoPedido.setEstado("PENDIENTE"); 
            nuevoPedido.setFechaPedido(java.time.LocalDateTime.now()); 

            if (clienteReal != null) {
                nuevoPedido.setDireccionEntrega(clienteReal.getDireccion());
            }

            double totalPedido = 0;

            // Bucle de descuento físico en Kardex (Seguro gracias al candado previo)
            for (DetalleVentaDTO item : request.getDetalles()) {
                totalPedido += (item.getPrecioUnitario() * item.getCantidad());
                
                int prodId = item.getProductoId(); 
                int cantVendida = item.getCantidad();

                // 🚀 LÓGICA DE DESCUENTO 100% DINÁMICA POR TABLA RECETA
                List<Receta> ingredientes = recetaService.buscarInsumosPorProducto(prodId);

                if (ingredientes != null) {
                    for (Receta receta : ingredientes) {
                        int insumoIdReal = receta.getInsumo().getInsumoId();
                        int descuentoTotal = receta.getCantidadNecesaria() * cantVendida;

                        // Se decrementa el almacén de materias primas automáticamente
                        insumoService.decrementarStock(insumoIdReal, descuentoTotal); 
                        
                        // Genera el movimiento automático en tu historial de Kardex
                        movimientoService.registrar(insumoIdReal, "SALIDA", descuentoTotal, "Venta POS - " + prodId, adminLogueado);
                    }
                }
            }
            
            nuevoPedido.setTotal(java.math.BigDecimal.valueOf(totalPedido));
            pedidoService.registrar(nuevoPedido); 
            
            /* ====================================================================
               B) RECORRER LOS ITEMS DEL TICKET: GUARDAR DETALLES Y BAJAR STOCK COMERCIAL
               ==================================================================== */
            for (DetalleVentaDTO item : request.getDetalles()) {
                Producto prod = productoService.buscarPorId(item.getProductoId());
                if (prod != null) {
                    int nuevoStock = prod.getStock() - item.getCantidad();
                    if (nuevoStock < 0) nuevoStock = 0;
                    prod.setStock(nuevoStock);
                    productoService.guardar(prod); 
                    
                    DetallePedido detalle = new DetallePedido();
                    detalle.setPedido(nuevoPedido); 
                    detalle.setProducto(prod);       
                    detalle.setCantidad(item.getCantidad());
                    detalle.setPrecioUnitario(java.math.BigDecimal.valueOf(item.getPrecioUnitario()));
                    
                    pedidoService.registrarDetalle(detalle); 
                }
            }
            
            respuesta.put("status", "success");
            respuesta.put("pedidoId", nuevoPedido.getPedidoId()); 
            
        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "error");
            respuesta.put("message", "Error al procesar la orden en MySQL: " + e.getMessage());
        }
        
        return respuesta;
    }
    
    /* ====================================================================
       3. ENDPOINT: BUSCAR CLIENTE POR DNI (AJAX) - ACTUALIZADO PARA WEB
       ==================================================================== */
    @GetMapping("/buscar-cliente")
    @ResponseBody
    public Map<String, Object> buscarClientePorDNI(@RequestParam("dni") String dni) {
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            Cliente cli = clienteService.buscarPorDni(dni); 
            
            if (cli != null) {
                respuesta.put("status", "found");
                respuesta.put("id", cli.getClienteId()); 
                respuesta.put("nombre", cli.getNombreCompleto()); 
                respuesta.put("telefono", cli.getTelefono());
                respuesta.put("direccion", cli.getDireccion());
                respuesta.put("email", cli.getEmail());
                
                System.out.println("🔍 POS/Web API: Cliente encontrado -> " + cli.getNombreCompleto());
            } else {
                respuesta.put("status", "not_found");
            }
            
        } catch (Exception e) {
            respuesta.put("status", "error");
            respuesta.put("message", "Error al consultar DNI: " + e.getMessage());
        }
        
        return respuesta;
    }

    /* ====================================================================
       4. ENDPOINT: REGISTRAR CLIENTE NUEVO EXPRESS DESDE EL MODAL (AJAX)
       ==================================================================== */
    @PostMapping("/registrar-cliente")
    @ResponseBody
    public Map<String, Object> registrarClienteExpress(@RequestBody Map<String, String> body) {
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            String dni = body.get("dni");
            String nombreCompleto = body.get("nombreCompleto");
            String telefono = body.get("telefono");
            String direccion = body.get("direccion");
            String email = body.get("email"); 
           
            Cliente nuevoCli = new Cliente();
            nuevoCli.setDni(dni);
            nuevoCli.setNombreCompleto(nombreCompleto);
            nuevoCli.setTelefono(telefono);
            nuevoCli.setDireccion(direccion);
            nuevoCli.setEmail(email);
            
            clienteService.registrar(nuevoCli); 

            respuesta.put("status", "success");
            respuesta.put("id", nuevoCli.getClienteId()); 
            
            System.out.println("💾 ¡Cliente registrado con éxito en MySQL! ID: " + nuevoCli.getClienteId());   
        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "error");
            respuesta.put("message", "Error al insertar cliente en MySQL: " + e.getMessage());
        }
        
        return respuesta;
    }     
}