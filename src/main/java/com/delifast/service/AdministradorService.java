package com.delifast.service;

import java.util.List;
import com.delifast.model.Administrador;

public interface AdministradorService {
    Administrador login(String email, String password); // 👈 Puente para el Login
    List<Administrador> listarUsuarios();
    Administrador buscarPorId(int id);
    void registrarUsuario(Administrador administrador);
    void modificarUsuario(Administrador administrador);
    void eliminarUsuario(int id);
}