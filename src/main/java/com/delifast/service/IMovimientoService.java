package com.delifast.service;

import com.delifast.model.MovimientoInventario;
import com.delifast.model.Administrador; // 👈 Clave importar la entidad
import java.util.List;

public interface IMovimientoService {
    
    // Firma nivelada con los 5 parámetros requeridos
    void registrar(int productoId, String tipoMovimiento, int cantidad, String motivo, Administrador admin);
    
    List<MovimientoInventario> listarTodos();
}