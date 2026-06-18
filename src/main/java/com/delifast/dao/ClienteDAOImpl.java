package com.delifast.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.delifast.model.Cliente;

@Repository
public class ClienteDAOImpl implements ClienteDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Cliente> listar() {
        return em.createQuery("FROM Cliente", Cliente.class).getResultList();
    }

    @Override
    public Cliente obtenerPorId(int id) {
        return em.find(Cliente.class, id);
    }

    @Override
    public void insertar(Cliente c) {
        em.persist(c);
    }

    @Override
    public void actualizar(Cliente c) {
        em.merge(c);
    }

    @Override
    public void eliminar(int id) {
        Cliente c = obtenerPorId(id);
        if (c != null) {
            em.remove(c);
        }
    }
}