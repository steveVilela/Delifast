package com.delifast.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categorias") // Ajusta el nombre si tu tabla en MySQL se llama "categorias" o "tb_categoria"
@Data // Lombok: Genera automáticamente Getters, Setters, toString, equals y hashCode
@NoArgsConstructor // Lombok: Constructor vacío obligatorio para JPA
@AllArgsConstructor // Lombok: Constructor con todos los campos
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id") // Mapea directamente con la columna de clave primaria en tu BD
    private int categoriaId;

    @Column(name = "nombre") // Ajusta a "nombre" o "des_cat" según se llame exactamente la columna en tu MySQL
    private String nombre;
}