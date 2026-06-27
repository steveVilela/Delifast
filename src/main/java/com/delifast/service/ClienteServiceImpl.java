package com.delifast.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delifast.dao.ClienteDAO;
import com.delifast.model.Cliente;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteDAO clienteDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteDAO.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(int id) {
        return clienteDAO.obtenerPorId(id);
    }

    @Override
    @Transactional
    public void registrar(Cliente c) {
        clienteDAO.insertar(c);
    }

    @Override
    @Transactional
    public void modificar(Cliente c) {
        clienteDAO.actualizar(c);
    }

    @Override
    @Transactional
    public void remover(int id) {
        clienteDAO.eliminar(id);
    }
    
    
    @Override
    public Cliente buscarPorDni(String dni) {
        return clienteDAO.buscarPorDni(dni);
    }
    
}