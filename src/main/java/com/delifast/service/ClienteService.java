package com.delifast.service;

import java.util.List;
import com.delifast.model.Cliente;

public interface ClienteService {
    List<Cliente> listarTodos();
    Cliente buscarPorId(int id);
    void registrar(Cliente c);
    void modificar(Cliente c);
    void remover(int id);
}