package com.delifast.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private int pedidoId;

    // Relación directa con la entidad Cliente usando la FK 'cliente_id' (MUL)
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "direccion_entrega")
    private String direccionEntrega;

    @Column(name = "estado")
    private String estado; // Ej: Pendiente, Preparando, Entregado, Cancelado

    @Column(name = "fecha_pedido", insertable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @Column(name = "metodo_pago")
    private String metodoPago; // Ej: Efectivo, Tarjeta, Yape

    @Column(name = "total")
    private BigDecimal total;
}