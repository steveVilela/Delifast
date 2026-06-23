package com.delifast.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "insumos")
@Data
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insumo_id")
    private int insumoId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;

    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    //
    @Column(name = "stock_bloqueo")
    private int stockBloqueo;
    
}
