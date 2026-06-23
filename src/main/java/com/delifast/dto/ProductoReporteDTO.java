package com.delifast.dto; // O el paquete que uses para DTOs

public class ProductoReporteDTO {
    private Integer id;
    private String nombre;
    private Double precio;
    private Long cantidadVendida; // JPQL SUM suele devolver Long
    private Double totalRecaudado;

    // 🚀 CRUCIAL: El constructor debe ser público y coincidir con la consulta
    public ProductoReporteDTO(Integer id, String nombre, Double precio, Long cantidadVendida, Double totalRecaudado) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadVendida = cantidadVendida;
        this.totalRecaudado = totalRecaudado;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Long getCantidadVendida() { return cantidadVendida; }
    public void setCantidadVendida(Long cantidadVendida) { this.cantidadVendida = cantidadVendida; }

    public Double getTotalRecaudado() { return totalRecaudado; }
    public void setTotalRecaudado(Double totalRecaudado) { this.totalRecaudado = totalRecaudado; }
}