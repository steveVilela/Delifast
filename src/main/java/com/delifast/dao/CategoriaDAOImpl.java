package com.delifast.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.delifast.model.Categoria;

@Repository
public class CategoriaDAOImpl implements CategoriaDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Categoria> listar() {
        return em.createQuery("FROM Categoria", Categoria.class).getResultList();
    }

    @Override
    public Categoria obtenerPorId(int id) {
        return em.find(Categoria.class, id);
    }

    @Override
    public void insertar(Categoria c) {
        em.persist(c);
    }

    @Override
    public void actualizar(Categoria c) {
        em.merge(c);
    }

    @Override
    public void eliminar(int id) {
        Categoria c = obtenerPorId(id);
        if (c != null) {
            em.remove(c);
        }
    }
}