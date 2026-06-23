package com.delifast.dto;
import lombok.Data;

@Data
public class DetalleVentaDTO {
    private int productoId;
    private int cantidad;
    private double precioUnitario;
}