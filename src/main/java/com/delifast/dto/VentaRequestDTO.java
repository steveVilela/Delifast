package com.delifast.dto;

import java.util.List;
import lombok.Data;

@Data
public class VentaRequestDTO {
    
    // 🔥 CORREGIDO: Cambiamos de String a int para atrapar el ID real que viene del POS
    private int clienteId; 
    private String metodoPago;
    private List<DetalleVentaDTO> detalles;
}
