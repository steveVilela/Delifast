package com.delifast.dto;
import lombok.Data;
// DTO para representar el detalle de los productos en una venta
@Data
public class DetalleVentaDTO {
    private int productoId;
    private int cantidad;
    private double precioUnitario;
}