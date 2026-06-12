package com.delifast.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.delifast.dao.ProductoDAO;
import com.delifast.model.Producto;

@Service
public class ProductoService {

    @Autowired
    private ProductoDAO productoDAO;

    public List<Producto> listarTodos() {
        return productoDAO.listar();
    }

    public List<Producto> listarSoloActivos() {
        return productoDAO.listarActivos();
    }

    public Producto buscarPorId(int id) {
        return productoDAO.obtenerPorId(id);
    }

    public void guardar(Producto p) {
        if (p.getProductoId() > 0) {
            productoDAO.actualizar(p);
        } else {
            productoDAO.insertar(p);
        }
    }

    public void eliminar(int id) {
        productoDAO.eliminar(id);
    }
}