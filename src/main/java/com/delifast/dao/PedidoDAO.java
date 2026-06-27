package com.delifast.dao;

import java.util.List;
import com.delifast.model.Pedido;
import com.delifast.model.DetallePedido;

public interface PedidoDAO {
    List<Pedido> obtenerHistorialPedidos();
    Pedido buscarPorId(int id);
    List<DetallePedido> obtenerDetallesDePedido(int pedidoId);
 // 📋 Añade esto en tu archivo PedidoDAO.java
    java.util.List<com.delifast.dto.CajaReporteDTO> obtenerArqueoCaja();
    void cambiarEstado(int pedidoId, String nuevoEstado);
    
    // ➕ NUEVOS MÉTODOS PARA EL ENTITY MANAGER:
    void registrar(Pedido p);
    void registrarDetalle(DetallePedido dp);
}