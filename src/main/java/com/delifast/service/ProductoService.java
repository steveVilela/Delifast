package com.delifast.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delifast.dao.ProductoDAO;
import com.delifast.dto.ProduccionSimuladaDTO;
import com.delifast.model.Producto;
import com.delifast.model.Receta;

@Service
public class ProductoService implements IProductoService { // 🟢 VÍNCULO CORREGIDO

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private RecetaService recetaService;

    @Override // 🟢 Buenas prácticas de patronaje
    public List<Producto> listarTodos() {
        return productoDAO.listar();
    }

    @Override
    public List<Producto> listarSoloActivos() {
        return productoDAO.listarActivos();
    }

    @Override
    public Producto buscarPorId(int id) {
        return productoDAO.obtenerPorId(id);
    }
    
    @Override
    public Producto obtenerPorId(int id) {
        return productoDAO.obtenerPorId(id);
    }

    @Override
    @Transactional
    public void guardar(Producto p) {
        if (p.getProductoId() > 0) {
            productoDAO.actualizar(p);
        } else {
            productoDAO.insertar(p);
        }
    }

    @Override
    @Transactional
    public void eliminar(int id) {
        productoDAO.eliminar(id);
    }

    @Override
    @Transactional
    public void actualizarStockMinimo(int id, int stockMinimo) {
        Producto p = productoDAO.obtenerPorId(id);
        if (p != null) {
            p.setStockMinimo(stockMinimo);
            productoDAO.actualizar(p);
        }
    }

    @Override
    @Transactional
    public void incrementarStock(int id, int cantidad) {
        Producto p = productoDAO.obtenerPorId(id);
        if (p != null) {
            p.setStock(p.getStock() + cantidad);
            productoDAO.actualizar(p);
        }
    }

    @Override
    @Transactional
    public void decrementarStock(int id, int cantidad) {
        Producto p = productoDAO.obtenerPorId(id);
        if (p != null) {
            p.setStock(p.getStock() - cantidad);
            productoDAO.actualizar(p);
        }
    }
    
    @Override // 🟢 Vinculado dinámicamente a la interfaz
    public boolean tieneInsumosSuficientes(int productoId, int cantidadSolicitada) {
        List<Receta> receta = recetaService.buscarInsumosPorProducto(productoId);
        if (receta == null || receta.isEmpty()) return true; 

        for (Receta item : receta) {
            int stockActualInsumo = item.getInsumo().getStock();
            int cantidadNecesariaTotal = item.getCantidadNecesaria() * cantidadSolicitada;
            int stockBloqueo = item.getInsumo().getStockMinimo(); 

            if (cantidadNecesariaTotal > (stockActualInsumo - stockBloqueo)) return false; 
        }
        return true;
    }

    @Override // 🟢 Vinculado dinámicamente a la interfaz
 // 📋 Modificación en tu ProductoService.java
    public int calcularStockRealPorInsumos(int productoId) {
        List<Receta> receta = recetaService.buscarInsumosPorProducto(productoId);
        
        if (receta == null || receta.isEmpty()) {
            Producto p = productoDAO.obtenerPorId(productoId);
            return (p != null) ? p.getStock() : 0;
        }

        int stockMaximoPosible = Integer.MAX_VALUE;

     // 📋 Modificación definitiva en tu ProductoService.java
        for (Receta item : receta) {
            int stockActualInsumo = item.getInsumo().getStock();
            int porcionRequerida = item.getCantidadNecesaria();

            /* ====================================================================
               🎯 CONFIGURACIÓN 100% DINÁMICA DESDE BASE DE DATOS
               Ya no usamos el 20% hardcodeado. Leemos directamente el valor 
               que el administrador guardó desde la interfaz web.
               ==================================================================== */
            int stockBloqueo = item.getInsumo().getStockBloqueo(); // 👈 Jala el valor del input rojo (0, 5, 10, etc.)

            int stockDisponibleLibre = stockActualInsumo - stockBloqueo;

            if (stockDisponibleLibre <= 0 || porcionRequerida <= 0) {
                return 0; 
            }

            int rendimientoEsteInsumo = stockDisponibleLibre / porcionRequerida;

            if (rendimientoEsteInsumo < stockMaximoPosible) {
                stockMaximoPosible = rendimientoEsteInsumo;
            }
        }

        return stockMaximoPosible;
    }
    
 // =======================================================================
    // 🍳 SIMULACIÓN DE CAPACIDAD DE PRODUCCIÓN PARA REPORTES DE ALMACÉN
    // =======================================================================
    @Override
    public List<ProduccionSimuladaDTO> obtenerSimulacionProduccion() {
        List<ProduccionSimuladaDTO> listaSimulada = new java.util.ArrayList<>();
        
        // 1. Reutilizamos tu método para traer los platos activos
        List<Producto> productosActivos = this.listarSoloActivos();
        
        for (Producto prod : productosActivos) {
            // 2. Reutilizamos tu algoritmo que ya calcula el stock real respetando el stockBloqueo
            int maximoPosible = this.calcularStockRealPorInsumos(prod.getProductoId());
            
            // 3. Construimos el detalle de los insumos leyendo su receta
            List<Receta> receta = recetaService.buscarInsumosPorProducto(prod.getProductoId());
            StringBuilder detalle = new StringBuilder();
            
            if (receta == null || receta.isEmpty()) {
                detalle.append("Sin receta registrada (Usa stock directo)");
            } else {
                for (Receta item : receta) {
                    detalle.append(item.getInsumo().getNombre()).append(" (")
                           .append(item.getCantidadNecesaria()).append(" ")
                           .append(item.getInsumo().getUnidadMedida()).append(") | ");
                }
                // Limpiamos la última barra decorativa " | " si tiene datos
                if (detalle.length() > 3) {
                    detalle.setLength(detalle.length() - 3);
                }
            }
            
            // 4. Mapeamos los datos al DTO para que la tabla HTML lo pinte
            listaSimulada.add(new ProduccionSimuladaDTO(
                prod.getNombre(), 
                detalle.toString(), 
                (long) maximoPosible
            ));
        }
        
        return listaSimulada;
    }
    
}