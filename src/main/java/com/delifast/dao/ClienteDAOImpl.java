package com.delifast.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
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
    
 // ➕ Implementa la búsqueda por DNI de forma segura con un try-catch
    @Override
    public Cliente buscarPorDni(String dni) {
        try {
            String jpql = "SELECT c FROM Cliente c WHERE c.dni = :dni";
            return em.createQuery(jpql, Cliente.class)
                     .setParameter("dni", dni)
                     .getSingleResult();
        } catch (NoResultException e) {
            // Si no encuentra ningún cliente con ese DNI, retorna null limpiamente
            return null; 
        }
    }
    
    
    @Override
    @SuppressWarnings("unchecked")
    public List<com.delifast.dto.ClienteReporteDTO> obtenerClientesMasFrecuentes() {
        String sql = "SELECT c.dni, c.nombre_completo, COUNT(p.pedido_id) as total_pedidos, " +
                     "SUM(p.total) as total_invertido " +
                     "FROM pedidos p " +
                     "JOIN clientes c ON c.cliente_id = p.cliente_id " +
                     "GROUP BY c.dni, c.nombre_completo " +
                     "ORDER BY total_pedidos DESC LIMIT 5";
        
        jakarta.persistence.Query query = em.createNativeQuery(sql);
        List<Object[]> resultados = query.getResultList();
        List<com.delifast.dto.ClienteReporteDTO> lista = new java.util.ArrayList<>();
        
        for (Object[] row : resultados) {
            lista.add(new com.delifast.dto.ClienteReporteDTO(
                (String) row[0],
                (String) row[1],
                ((Number) row[2]).longValue(),
                ((Number) row[3]).doubleValue()
            ));
        }
        return lista;
    }
}