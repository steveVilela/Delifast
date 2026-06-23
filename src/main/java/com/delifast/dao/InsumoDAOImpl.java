package com.delifast.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.delifast.dao.InsumoDAO;
import com.delifast.model.Insumo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class InsumoDAOImpl implements InsumoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // ==========================================================
    // LISTAR TODOS: Devuelve todos los insumos del almacén
    // ==========================================================
    @Override
    public List<Insumo> listarTodos() {
        // Apuntamos a la Entidad "Insumo" mapeada por JPA
        return entityManager.createQuery("FROM Insumo", Insumo.class).getResultList();
    }

    // ==========================================================
    // OBTENER POR ID: Busca un insumo específico por su PK
    // ==========================================================
    @Override
    public Insumo obtenerPorId(int id) {
        return entityManager.find(Insumo.class, id);
    }
    
 // ==========================================================
    // GUARDAR: Inserta de forma física el nuevo insumo en MySQL
    // ==========================================================
    @Override
    public void guardar(Insumo insumo) {
        // .persist() toma el objeto y ejecuta el 'INSERT INTO insumos...' automático
        entityManager.persist(insumo);
    }
}