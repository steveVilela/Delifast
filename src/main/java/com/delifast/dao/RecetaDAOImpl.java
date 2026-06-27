package com.delifast.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.delifast.model.Receta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class RecetaDAOImpl implements RecetaDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // ==========================================================
    // LISTAR TODAS: Usamos JPQL para jalar las recetas y sus relaciones
    // ==========================================================
    @Override
    public List<Receta> listarTodas() {
        // "JOIN FETCH" le dice a Hibernate que cargue de un solo golpe 
        // los datos del producto y el insumo para evitar consultas lentas
        String jpql = "SELECT r FROM Receta r JOIN FETCH r.producto JOIN FETCH r.insumo";
        return entityManager.createQuery(jpql, Receta.class).getResultList();
    }

    // ==========================================================
    // REGISTRAR COMPONENTE: Inserción directa de la entidad mapeada
    // ==========================================================
    @Override
    public void registrarComponente(Receta receta) {
        // Al pasarle el objeto completo, JPA lee las anotaciones @ManyToOne
        // e inserta las llaves foráneas correspondientes en la tabla
        entityManager.persist(receta);
    }
}