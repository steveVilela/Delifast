package com.delifast.dao;

import org.springframework.stereotype.Repository;
import com.delifast.model.Administrador;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Repository
public class AdministradorDAOImpl implements AdministradorDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Administrador validar(String email, String password) {
        String jpql = "FROM Administrador a WHERE a.email = :email AND a.password = :pass AND a.activo = true";
        try {
            return entityManager.createQuery(jpql, Administrador.class)
                    .setParameter("email", email)
                    .setParameter("pass", password)
                    .getSingleResult(); // Retorna el registro único si coincide
        } catch (NoResultException e) {
            return null; // Si las credenciales no existen, devuelve null de forma segura
        }
    }
}