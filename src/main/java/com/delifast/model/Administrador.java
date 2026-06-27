package com.delifast.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "administrador")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private int adminId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    // Mapeamos el tinyint(1) de MySQL como un int para el control de estados (1 = Activo, 0 = Inactivo)
    @Column(name = "activo")
    private Integer activo;
    
 // 🔑 El nuevo campo para el control de accesos
    @Column(name = "rol", nullable = false)
    private String rol;
}