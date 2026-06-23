package com.delifast.dao;

import java.util.List;
import com.delifast.model.Administrador;

public interface AdministradorDAO {
    // 🔐 Tu método existente para el Login
    Administrador validar(String email, String password);
    
    // 🛠️ Nuevos métodos para el CRUD
    List<Administrador> listarTodos();
    Administrador buscarPorId(int id);
    void guardar(Administrador administrador);
    void actualizar(Administrador administrador);
    void eliminar(int id);
}