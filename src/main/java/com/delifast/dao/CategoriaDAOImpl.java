package com.delifast.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.delifast.model.Categoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class CategoriaDAOImpl implements CategoriaDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // ==========================================================
    // LISTAR TODAS LAS CATEGORÍAS
    // ==========================================================
    @Override
    public List<Categoria> listar() {
        // En JPQL apuntamos directamente a la clase Entidad "Categoria"
        return entityManager.createQuery("FROM Categoria", Categoria.class).getResultList();
    }

    // ==========================================================
    // OBTENER CATEGORÍA POR ID
    // ==========================================================
    @Override
    public Categoria obtenerPorId(int id) {
        return entityManager.find(Categoria.class, id);
    }

    // ==========================================================
    // INSERTAR NUEVA CATEGORÍA
    // ==========================================================
    @Override
    public void insertar(Categoria c) {
        entityManager.persist(c);
    }

    // ==========================================================
    // ACTUALIZAR CATEGORÍA EXISTENTE
    // ==========================================================
    @Override
    public void actualizar(Categoria c) {
        entityManager.merge(c);
    }

    // ==========================================================
    // ELIMINAR CATEGORÍA
    // ==========================================================
    @Override
    public void eliminar(int id) {
        Categoria c = obtenerPorId(id);
        if (c != null) {
            entityManager.remove(c);
        }
    }
}