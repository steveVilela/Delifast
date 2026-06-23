package com.delifast.dao;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import com.delifast.model.Pedido;
import com.delifast.model.DetallePedido;

@Repository
public class PedidoDAOImpl implements PedidoDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Pedido> obtenerHistorialPedidos() {
        // Jala todos los pedidos ordenados por ID de forma descendente (los más recientes primero)
        return em.createQuery("SELECT p FROM Pedido p ORDER BY p.pedidoId DESC", Pedido.class)
                 .getResultList();
    }

    @Override
    public Pedido buscarPorId(int id) {
        return em.find(Pedido.class, id);
    }

    @Override
    public List<DetallePedido> obtenerDetallesDePedido(int pedidoId) {
        return em.createQuery("SELECT d FROM DetallePedido d WHERE d.pedido.pedidoId = :pedidoId", DetallePedido.class)
                 .setParameter("pedidoId", pedidoId)
                 .getResultList();
    }

    @Override
    public void cambiarEstado(int pedidoId, String nuevoEstado) {
        Pedido p = em.find(Pedido.class, pedidoId);
        if (p != null) {
            p.setEstado(nuevoEstado);
            em.merge(p); // Actualiza el estado en MySQL (Pendiente, Preparación, Entregado)
        }
    }

    @Override
    public void registrar(Pedido p) {
        em.persist(p); // Inserta la cabecera de la venta POS
    }

    @Override
    public void registrarDetalle(DetallePedido dp) {
        em.persist(dp); // Inserta cada ítem del ticket en detalle_pedidos
    }
    
    
 // ==========================================================
 // 💰 REPORTE: ARQUEO DE CAJA POR MÉTODO DE PAGO
 // ==========================================================
 @Override
 @SuppressWarnings("unchecked")
 public List<com.delifast.dto.CajaReporteDTO> obtenerArqueoCaja() {
     // 🗄️ Consulta nativa de MySQL agrupando por pasarela/método de pago
     String sql = "SELECT p.metodo_pago, COUNT(p.pedido_id) as transacciones, " +
                  "SUM(p.total) as total_acumulado " +
                  "FROM pedidos p " +
                  "GROUP BY p.metodo_pago " +
                  "ORDER BY total_acumulado DESC";
     
     // Ejecutamos la consulta usando tu EntityManager 'em'
     jakarta.persistence.Query query = em.createNativeQuery(sql);
     List<Object[]> resultados = query.getResultList();
     
     List<com.delifast.dto.CajaReporteDTO> lista = new java.util.ArrayList<>();
     
     // 🚀 EL BLOQUE QUE FALTABA: Mapeamos el Object[] al DTO de Caja
     for (Object[] row : resultados) {
         lista.add(new com.delifast.dto.CajaReporteDTO(
             (String) row[0],                 // metodo_pago (String)
             ((Number) row[1]).longValue(),   // transacciones (long)
             ((Number) row[2]).doubleValue()  // total_acumulado (double)
         ));
     }
     
     return lista;
 }
}