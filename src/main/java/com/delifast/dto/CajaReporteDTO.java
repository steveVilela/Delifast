package com.delifast.dto;

public class CajaReporteDTO {
    private String metodoPago;
    private long cantidadTransacciones;
    private double totalRecaudado;
    //Constructor
    public CajaReporteDTO(String metodoPago, long cantidadTransacciones, double totalRecaudado) {
        this.metodoPago = metodoPago;
        this.cantidadTransacciones = cantidadTransacciones;
        this.totalRecaudado = totalRecaudado;
    }

    // Getters
    public String getMetodoPago() { return metodoPago; }
    public long getCantidadTransacciones() { return cantidadTransacciones; }
    public double getTotalRecaudado() { return totalRecaudado; }
}