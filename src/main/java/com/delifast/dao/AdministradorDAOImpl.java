package com.delifast.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.delifast.model.Administrador;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Repository
public class AdministradorDAOImpl implements AdministradorDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // 🔐 Tu método original de Login (intacto para que no se rompa nada)
    @Override
    public Administrador validar(String email, String password) {
        // Nota: cambiamos 'a.activo = true' a 'a.activo = 1' ya que lo estamos mapeando como Integer (tinyint)
        String jpql = "FROM Administrador a WHERE a.email = :email AND a.password = :pass AND a.activo = 1";
        try {
            return entityManager.createQuery(jpql, Administrador.class)
                    .setParameter("email", email)
                    .setParameter("pass", password)
                    .getSingleResult(); 
        } catch (NoResultException e) {
            return null; 
        }
    }

    // 🛠️ Implementación de los nuevos métodos del CRUD
    @Override
    public List<Administrador> listarTodos() {
        return entityManager.createQuery("FROM Administrador", Administrador.class).getResultList();
    }

    @Override
    public Administrador buscarPorId(int id) {
        return entityManager.find(Administrador.class, id);
    }

    @Override
    public void guardar(Administrador administrador) {
        entityManager.persist(administrador);
    }

    @Override
    public void actualizar(Administrador administrador) {
        entityManager.merge(administrador);
    }

    @Override
    public void eliminar(int id) {
        Administrador admin = buscarPorId(id);
        if (admin != null) {
            entityManager.remove(admin);
        }
    }
}