package com.delifast.dao;

import com.delifast.model.Insumo;
import java.util.List;

public interface InsumoDAO {
    List<Insumo> listarTodos();
    Insumo obtenerPorId(int id);
    
    // ➕ LINEA AGREGADA
    void guardar(Insumo insumo);
}