package com.delifast.service;

import com.delifast.model.Insumo;
import java.util.List;

public interface InsumoService {
    // Para cargar el combo desplegable en la asignación de recetas
    List<Insumo> listarTodos();
    
    // Para buscar un insumo específico por su ID si fuera necesario
    Insumo buscarPorId(int id);
    
 // ➕ NUEVOS MÉTODOS TRASLADADOS PARA EL CONTROL DE INSUMOS
    void actualizarStockMinimo(int id, int stockMinimo);
    void incrementarStock(int id, int cantidad);
    void decrementarStock(int id, int cantidad);
    void guardar(Insumo insumo);
    
 // 🟢 En tu interfaz IInsumoService.java añade:
    void actualizarParametrosTecnicos(int insumoId, int stockMinimo, int stockBloqueo);

}