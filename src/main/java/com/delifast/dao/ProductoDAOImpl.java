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
    
 // ==========================================================
    // 📊 REPORTE: TOP 5 PLATOS MÁS VENDIDOS (ANALÍTICA COMERCIAL)
    // ==========================================================
    @Override
    @SuppressWarnings("unchecked")
    public List<com.delifast.dto.ProductoReporteDTO> obtenerTopPlatosMasVendidos() {
        // SQL Nativo apuntando a las tablas de MySQL, agrupando por producto
        String sql = "SELECT p.producto_id, p.nombre, p.precio, " +
                     "SUM(dp.cantidad) as cantidad_vendida, " +
                     "SUM(dp.cantidad * dp.precio_unitario) as total_recaudado " +
                     "FROM detalle_pedidos dp " + // 💡 Verifica si tu tabla es 'detalle_pedidos' o 'detalle_ventas'
                     "JOIN productos p ON p.producto_id = dp.producto_id " +
                     "GROUP BY p.producto_id, p.nombre, p.precio " +
                     "ORDER BY cantidad_vendida DESC LIMIT 5";
        
        // Ejecutamos como consulta nativa porque procesa funciones SQL (SUM/GROUP BY)
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> resultados = query.getResultList();
        
        List<com.delifast.dto.ProductoReporteDTO> listaTop = new java.util.ArrayList<>();
        
        // Mapeamos manualmente el arreglo genérico Object[] a tu clase DTO estructurada
        for (Object[] row : resultados) {
            com.delifast.dto.ProductoReporteDTO dto = new com.delifast.dto.ProductoReporteDTO(
                ((Number) row[0]).intValue(),    // producto_id
                (String) row[1],                 // nombre
                ((Number) row[2]).doubleValue(), // precio
                ((Number) row[3]).longValue(),   // cantidad_vendida
                ((Number) row[4]).doubleValue()  // total_recaudado
            );
            listaTop.add(dto);
        }
        
        return listaTop;
    }
    
}