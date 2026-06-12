package com.delifast.dao;

import java.util.List;
import com.delifast.model.Categoria;

public interface CategoriaDAO {
    List<Categoria> listar();
    Categoria obtenerPorId(int id);
    void insertar(Categoria c);
    void actualizar(Categoria c);
    void eliminar(int id);
}