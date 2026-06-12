package com.delifast.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.delifast.model.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class ProductoDAOImpl implements ProductoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // ==========================================================
    // LISTAR TODOS: Equivalente a tu SELECT original
    // ==========================================================
    @Override
    public List<Producto> listar() {
        // En JPQL apuntamos a la Entidad "Producto" no a la tabla
        return entityManager.createQuery("FROM Producto", Producto.class).getResultList();
    }

    // ==========================================================
    // LISTAR ACTIVOS: Conserva tu filtro "estaActivo = true AND stock > 0"
    // ==========================================================
    @Override
    public List<Producto> listarActivos() {
        // Usamos las propiedades java: estaActivo y stock
        String jpql = "FROM Producto p WHERE p.estaActivo = true AND p.stock > 0";
        return entityManager.createQuery(jpql, Producto.class).getResultList();
    }

    // ==========================================================
    // OBTENER POR ID: Reemplaza tu SELECT ... WHERE id = ?
    // ==========================================================
    @Override
    public Producto obtenerPorId(int id) {
        return entityManager.find(Producto.class, id);
    }

    // ==========================================================
    // INSERTAR: Reemplaza tu bloque INSERT INTO (...) VALUES (...)
    // ==========================================================
    @Override
    public void insertar(Producto p) {
        entityManager.persist(p);
    }

    // ==========================================================
    // ACTUALIZAR: Reemplaza tu sentencia UPDATE de modificación completa
    // ==========================================================
    @Override
    public void actualizar(Producto p) {
        entityManager.merge(p);
    }

    // ==========================================================
    // ELIMINAR: Reemplaza tu comando DELETE FROM
    // ==========================================================
    @Override
    public void eliminar(int id) {
        Producto p = obtenerPorId(id);
        if (p != null) {
            entityManager.remove(p);
        }
    }

    // ==========================================================
    // DISMINUIR STOCK: Ejecución de consultas de modificación personalizadas
    // ==========================================================
    @Override
    public void decrementarStock(int productoId, int cantidad) {
        // Usamos las propiedades java: stock y productoId
        String jpql = "UPDATE Producto p SET p.stock = p.stock - :cant WHERE p.productoId = :id";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("cant", cantidad);
        query.setParameter("id", productoId);
        query.executeUpdate();
    }
}