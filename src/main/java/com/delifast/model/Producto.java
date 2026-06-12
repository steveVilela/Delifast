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
@Table(name = "productos") // Nombre exacto de tu tabla en MySQL
@Data // Lombok: Genera automáticamente todos los Getters, Setters, toString, equals y hashCode
@NoArgsConstructor // Lombok: Genera el constructor vacío que requiere JPA
@AllArgsConstructor // Lombok: Genera un constructor con todos los atributos
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id") // Mapea con la columna de tu BD
    private int productoId;
    
    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "precio")
    private double precio;
    
    @Column(name = "stock")
    private int stock;
    
    // Si en tu tabla actual no existe stock_minimo, Hibernate la creará automáticamente al iniciar
    @Column(name = "stock_minimo") 
    private int stockMinimo;
    
    @Column(name = "esta_activo")
    private boolean estaActivo;
    
    @Column(name = "categoria_id")
    private int categoriaId;
    
    @Column(name = "image_url") // Mapea el String para almacenar el nombre de la imagen
    private String imagen;
}