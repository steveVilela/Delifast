package com.delifast.dao;

import com.delifast.model.Administrador;

public interface AdministradorDAO {
    Administrador validar(String email, String password);
}