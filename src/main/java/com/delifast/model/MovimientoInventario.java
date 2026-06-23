package com.delifast.model;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Data
@Entity
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id")
    private int movimientoId;

    // 🔗 ENLACE RELACIONAL FORMAL CON LA ENTIDAD INSUMO (Reemplaza al viejo productoId)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insumo_id") // 👈 Mapea directo contra la FK física 'insumo_id' en MySQL
    private Insumo insumo;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "fecha", insertable = false, updatable = false)
    private Timestamp fecha;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento; // INGRESO / SALIDA

    @Column(name = "motivo")
    private String motivo; // 'Compra Proveedor', 'Ajuste Inventario', etc.

    // 🔗 ENLACE RELACIONAL FORMAL CON LA ENTIDAD ADMINISTRADOR
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "administrador_id") 
    private Administrador administrador; 
}
