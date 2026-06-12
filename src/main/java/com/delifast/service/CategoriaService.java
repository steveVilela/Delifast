package com.delifast.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delifast.dao.CategoriaDAO;
import com.delifast.model.Categoria;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaDAO.listar();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(int id) {
        return categoriaDAO.obtenerPorId(id);
    }

    @Transactional
    public void registrar(Categoria c) {
        categoriaDAO.insertar(c);
    }

    @Transactional
    public void actualizar(Categoria c) {
        categoriaDAO.actualizar(c);
    }

    @Transactional
    public void eliminar(int id) {
        categoriaDAO.eliminar(id);
    }
}