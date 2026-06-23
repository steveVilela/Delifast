package com.delifast.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delifast.dao.PedidoDAO;
import com.delifast.model.Pedido;
import com.delifast.model.DetallePedido;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoDAO pedidoDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> obtenerHistorialPedidos() {
        return pedidoDAO.obtenerHistorialPedidos();
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido buscarPorId(int id) {
        return pedidoDAO.buscarPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedido> obtenerDetallesDePedido(int pedidoId) {
        return pedidoDAO.obtenerDetallesDePedido(pedidoId);
    }

    @Override
    @Transactional
    public void cambiarEstado(int pedidoId, String nuevoEstado) {
        pedidoDAO.cambiarEstado(pedidoId, nuevoEstado);
    }
    
    @Override
    @Transactional
    public void registrar(Pedido p) {
        pedidoDAO.registrar(p);
    }

    @Override
    @Transactional
    public void registrarDetalle(DetallePedido dp) {
        pedidoDAO.registrarDetalle(dp);
    }
}