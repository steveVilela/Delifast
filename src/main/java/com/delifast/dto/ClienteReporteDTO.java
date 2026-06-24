package com.delifast.dto;

public class ClienteReporteDTO {
    private String dni;
    private String nombreCompleto;
    private long totalPedidos;
    private double totalInvertido;
    //constructor
    public ClienteReporteDTO(String dni, String nombreCompleto, long totalPedidos, double totalInvertido) {
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.totalPedidos = totalPedidos;
        this.totalInvertido = totalInvertido;
    }

    // Getters
    public String getDni() { return dni; }
    public String getNombreCompleto() { return nombreCompleto; }
    public long getTotalPedidos() { return totalPedidos; }
    public double getTotalInvertido() { return totalInvertido; }
}