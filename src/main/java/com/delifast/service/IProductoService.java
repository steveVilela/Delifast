package com.delifast.service;

import com.delifast.dto.ProduccionSimuladaDTO;
import com.delifast.model.Producto;
import java.util.List;

public interface IProductoService {
    
    List<Producto> listarTodos();
    
    // 🟢 Asegúrate de tener este para listar solo los activos en el catálogo web
    List<Producto> listarSoloActivos(); 
    
    Producto obtenerPorId(int id);
    
    // 🟢 Mantén el alias si tu controlador usa buscarPorId
    Producto buscarPorId(int id); 
    
    void guardar(Producto p);
    
    void eliminar(int id);
    
    void actualizarStockMinimo(int id, int stockMinimo);
    
    void incrementarStock(int id, int cantidad);
    
    void decrementarStock(int id, int cantidad);
    
    // 🟢 CRUCIAL: Agrega los dos métodos de control de recetas que usa la web
    boolean tieneInsumosSuficientes(int productoId, int cantidadSolicitada);
    
    int calcularStockRealPorInsumos(int productoId);
    
 // 🌟 AGREGA ESTA LÍNEA AL FINAL DE TU INTERFAZ:
    List<ProduccionSimuladaDTO> obtenerSimulacionProduccion();
}