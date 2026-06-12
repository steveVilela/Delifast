package com.delifast.dao;

import java.util.List;
import com.delifast.model.Producto;

public interface ProductoDAO {
    List<Producto> listar();
    List<Producto> listarActivos();
    Producto obtenerPorId(int id);
    void insertar(Producto p);
    void actualizar(Producto p);
    void eliminar(int id);
    void decrementarStock(int productoId, int cantidad);
}