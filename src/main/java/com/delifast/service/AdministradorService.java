package com.delifast.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delifast.dao.AdministradorDAO;
import com.delifast.model.Administrador;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorDAO administradorDAO;

    @Transactional(readOnly = true)
    public Administrador autenticar(String email, String password) {
        return administradorDAO.validar(email, password);
    }
}