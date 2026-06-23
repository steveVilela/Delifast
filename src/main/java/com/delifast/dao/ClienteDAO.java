package com.delifast.dao;

import java.util.List;
import com.delifast.model.Cliente;

public interface ClienteDAO {
    List<Cliente> listar();
    List<com.delifast.dto.ClienteReporteDTO> obtenerClientesMasFrecuentes();
    Cliente obtenerPorId(int id);
    Cliente buscarPorDni(String dni);
    void insertar(Cliente c);
    void actualizar(Cliente c);
    void eliminar(int id);
}