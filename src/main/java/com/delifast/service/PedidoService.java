package com.delifast.service;

import java.util.List;
import com.delifast.model.Pedido;
import com.delifast.model.DetallePedido;

public interface PedidoService {
    List<Pedido> obtenerHistorialPedidos();
    Pedido buscarPorId(int id);
    List<DetallePedido> obtenerDetallesDePedido(int pedidoId);
    void cambiarEstado(int pedidoId, String nuevoEstado);
    
    // ➕ NUEVOS MÉTODOS DE PERSISTENCIA:
    void registrar(Pedido p);
    void registrarDetalle(DetallePedido dp);
}