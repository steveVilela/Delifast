package com.delifast.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delifast.dao.AdministradorDAO;
import com.delifast.model.Administrador;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorDAO administradorDAO;

    @Override
    @Transactional(readOnly = true)
    public Administrador login(String email, String password) {
        return administradorDAO.validar(email, password); // 👈 Llama de forma segura a tu método
    }

    @Override
    @Transactional(readOnly = true)
    public List<Administrador> listarUsuarios() {
        return administradorDAO.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Administrador buscarPorId(int id) {
        return administradorDAO.buscarPorId(id);
    }

    @Override
    @Transactional
    public void registrarUsuario(Administrador administrador) {
        if (administrador.getActivo() == null) {
            administrador.setActivo(1);
        }
        administradorDAO.guardar(administrador);
    }

    @Override
    @Transactional
    public void modificarUsuario(Administrador administrador) {
        administradorDAO.actualizar(administrador);
    }

    @Override
    @Transactional
    public void eliminarUsuario(int id) {
        administradorDAO.eliminar(id);
    }
}