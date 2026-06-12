package com.delifast.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional // 👈para asegurarnos de que la base de datos abra el canal de escritura antes de que el DAO intente fusionar
    public void guardar(Producto p) {
        if (p.getProductoId() > 0) {
            productoDAO.actualizar(p);
        } else {
            productoDAO.insertar(p);
        }
    }
    @Transactional // 👈 2. AÑADE ESTA ANOTACIÓN AQUÍ
    public void eliminar(int id) {
        productoDAO.eliminar(id);
    }
}