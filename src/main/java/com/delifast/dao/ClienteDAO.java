package com.delifast.dao;

import java.util.List;
import com.delifast.model.Cliente;

public interface ClienteDAO {
    List<Cliente> listar();
    Cliente obtenerPorId(int id);
    void insertar(Cliente c);
    void actualizar(Cliente c);
    void eliminar(int id);
}