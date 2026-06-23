package com.delifast.dao;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import com.delifast.model.MovimientoInventario;

@Repository
public class MovimientoDAO {

    @PersistenceContext
    private EntityManager em;

    // Para insertar un nuevo registro en el Kardex (Ingresos, Egresos, Ventas)
    public void insertar(MovimientoInventario movimiento) {
        em.persist(movimiento);
    }

    // Para listar cronológicamente todo el historial del Kardex
    public List<MovimientoInventario> listar() {
        String jpql = "SELECT m FROM MovimientoInventario m ORDER BY m.fecha DESC";
        return em.createQuery(jpql, MovimientoInventario.class).getResultList();
    }
}