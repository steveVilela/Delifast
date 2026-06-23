package com.delifast.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.delifast.dto.DetalleVentaDTO;
import com.delifast.dto.VentaRequestDTO;
import com.delifast.model.Administrador;
import com.delifast.model.Cliente;
import com.delifast.model.DetallePedido;
import com.delifast.model.Pedido;
import com.delifast.model.Producto;
import com.delifast.model.Receta;
import com.delifast.service.InsumoService;
import com.delifast.service.MovimientoService;
import com.delifast.service.PedidoService;
import com.delifast.service.ProductoService;
import com.delifast.service.RecetaService;
import com.delifast.service.ClienteService;
import com.delifast.service.AdministradorService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class VentaWebController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private InsumoService insumoService;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private RecetaService recetaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
   
    private AdministradorService administradorService;

    // 🛒 VALIDAR, REGISTRAR Y PROCESAR EL PAGO AL MISMO TIEMPO
    @PostMapping("/checkout")
    @ResponseBody
    public Map<String, Object> procesarCompraWeb(@RequestBody Map<String, Object> payload, HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            System.out.println("====== PROCESANDO PAGO WEB + VALIDACIÓN DE CLIENTE ======");

            // 1. EXTRAER CLIENTE DE LA SESIÓN O DEL FORMULARIO DEL PASO DE PAGO
            Cliente clienteReal = (Cliente) session.getAttribute("cliente");

            if (clienteReal == null) {
                String dni = (String) payload.get("dni");
                
                if (dni != null && !dni.trim().isEmpty()) {
                    clienteReal = clienteService.buscarPorDni(dni);
                    
                    if (clienteReal != null) {
                        System.out.println("♻️ Cliente antiguo detectado (" + clienteReal.getNombreCompleto() + "). Usando datos existentes sin alterar la BD.");
                    } else {
                        String email = (String) payload.get("email");
                        String nombreCompleto = (String) payload.get("nombreCompleto");
                        String telefono = (String) payload.get("telefono");
                        String direccion = (String) payload.get("direccion");
                        
                        if (nombreCompleto == null || nombreCompleto.trim().isEmpty() ||
                            telefono == null || telefono.trim().isEmpty() ||
                            direccion == null || direccion.trim().isEmpty() ||
                            email == null || email.trim().isEmpty()) {
                            
                            respuesta.put("status", "error");
                            respuesta.put("message", "Todos los campos son obligatorios para registrarse como nuevo cliente.");
                            return respuesta;
                        }

                        System.out.println("💾 Registrando nuevo cliente express en la Base de Datos...");
                        clienteReal = new Cliente();
                        clienteReal.setDni(dni);
                        clienteReal.setNombreCompleto(nombreCompleto);
                        clienteReal.setTelefono(telefono);
                        clienteReal.setDireccion(direccion);
                        clienteReal.setEmail(email);
                        
                        clienteService.registrar(clienteReal);
                    }
                    session.setAttribute("cliente", clienteReal);
                }
            }

            if (clienteReal == null) {
                respuesta.put("status", "error_cliente");
                respuesta.put("message", "Debe ingresar sus datos de identificación para procesar el pedido.");
                return respuesta;
            }

            // 🟢 Recuperamos el Administrador del sistema (ID 1) para el Kardex automatizado
            Administrador adminSistema = administradorService.buscarPorId(1);

            List<Map<String, Object>> detallesRaw = (List<Map<String, Object>>) payload.get("detalles");
            String metodoPago = (String) payload.get("metodoPago");

            /* ====================================================================
               🔒 CANDADO DE SEGURIDAD ABSOLUTO (BACKEND) - CORREGIDO
               Verificamos TODOS los productos antes de ejecutar descuentos en la BD
               ==================================================================== */
            for (Map<String, Object> item : detallesRaw) {
                int prodId = (Integer) item.get("productoId");
                
                // 🟢 SOLUCIÓN AL NULL: Validamos de forma segura qué propiedad trae la cantidad
                int cantVendida = 1; // Fallback seguro
                if (item.get("cantidad") != null) {
                    cantVendida = ((Number) item.get("cantidad")).intValue();
                } else if (item.get("amount") != null) {
                    cantVendida = ((Number) item.get("amount")).intValue();
                }

                boolean verificado = productoService.tieneInsumosSuficientes(prodId, cantVendida);
                if (!verificado) {
                    Producto pAgotado = productoService.buscarPorId(prodId);
                    String nombreP = (pAgotado != null) ? pAgotado.getNombre() : "un producto de tu carrito";
                    
                    respuesta.put("status", "error");
                    respuesta.put("message", "¡Operación rechazada! El producto '" + nombreP + "' se quedó sin insumos suficientes en cocina mientras procesabas tu pago.");
                    return respuesta; 
                }
            }
            
            
            /* ====================================================================
               A) INICIA PROCESO TRANSACCIONAL FINANCIERO Y DE KARDEX
               ==================================================================== */
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setCliente(clienteReal);
            nuevoPedido.setMetodoPago(metodoPago);
            nuevoPedido.setEstado("PAGADO"); 
            nuevoPedido.setFechaPedido(java.time.LocalDateTime.now());
            nuevoPedido.setDireccionEntrega(clienteReal.getDireccion());

            double totalPedido = 0;

            // Bucle 1: Financiero y Descuento por Receta (Kardex seguro)
            for (Map<String, Object> item : detallesRaw) {
                int prodId = (Integer) item.get("productoId");
                int cantVendida = (item.get("cantidad") != null) ? (Integer) item.get("cantidad") : (Integer) item.get("amount");
                double precioUnitario = ((Number) item.get("precioUnitario")).doubleValue();

                totalPedido += (precioUnitario * cantVendida);

                // 🚀 LÓGICA DE DESCUENTO DINÁMICA POR RECETA
                List<Receta> ingredientes = recetaService.buscarInsumosPorProducto(prodId);
                if (ingredientes != null) {
                    for (Receta receta : ingredientes) {
                        int insumoIdReal = receta.getInsumo().getInsumoId();
                        int descuentoTotal = receta.getCantidadNecesaria() * cantVendida;

                        insumoService.decrementarStock(insumoIdReal, descuentoTotal);
                        movimientoService.registrar(insumoIdReal, "SALIDA", descuentoTotal, "Venta Web - Insumo auto", adminSistema);
                    }
                }
            }

            nuevoPedido.setTotal(java.math.BigDecimal.valueOf(totalPedido));
            pedidoService.registrar(nuevoPedido);

            /* ====================================================================
               B) REGISTRAR DETALLES Y BAJAR STOCK COMERCIAL
               ==================================================================== */
            for (Map<String, Object> item : detallesRaw) {
                int prodId = (Integer) item.get("productoId");
                int cantidad = (item.get("cantidad") != null) ? (Integer) item.get("cantidad") : (Integer) item.get("amount");
                double precioUnitario = ((Number) item.get("precioUnitario")).doubleValue();

                Producto prod = productoService.buscarPorId(prodId);
                if (prod != null) {
                    int nuevoStock = prod.getStock() - cantidad;
                    if (nuevoStock < 0) nuevoStock = 0;
                    prod.setStock(nuevoStock);
                    productoService.guardar(prod);

                    DetallePedido detalle = new DetallePedido();
                    detalle.setPedido(nuevoPedido);
                    detalle.setProducto(prod);
                    detalle.setCantidad(cantidad);
                    detalle.setPrecioUnitario(java.math.BigDecimal.valueOf(precioUnitario));

                    pedidoService.registrarDetalle(detalle);
                }
            }

            respuesta.put("status", "success");
            respuesta.put("pedidoId", nuevoPedido.getPedidoId());

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "error");
            respuesta.put("message", "Error crítico en checkout web: " + e.getMessage());
        }

        return respuesta;
    }
    
    @GetMapping("/validar-insumos")
    @ResponseBody
    public Map<String, Object> validarInsumos(
            @RequestParam("productoId") int productoId, 
            @RequestParam("cantidad") int cantidad) {
        
        Map<String, Object> respuesta = new HashMap<>();
        boolean disponible = productoService.tieneInsumosSuficientes(productoId, cantidad);
        
        respuesta.put("disponible", disponible);
        return respuesta;
    }
}