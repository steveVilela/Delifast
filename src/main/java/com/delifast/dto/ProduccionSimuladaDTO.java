package com.delifast.dto;

public class ProduccionSimuladaDTO {

    private String nombreProducto;
    private String detalleReceta;
    private long stockMaximoPosible; // 👈 Aseguramos que sea long para que coincida

    // 1. Constructor Vacío (Obligatorio para que Spring/Jackson no fallen)
    public ProduccionSimuladaDTO() {
    }

    // 2. Constructor con todos los parámetros (El que le falta a tu código)
    public ProduccionSimuladaDTO(String nombreProducto, String detalleReceta, long stockMaximoPosible) {
        this.nombreProducto = nombreProducto;
        this.detalleReceta = detalleReceta;
        this.stockMaximoPosible = stockMaximoPosible;
    }

    // 3. Getters y Setters
    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDetalleReceta() {
        return detalleReceta;
    }

    public void setDetalleReceta(String detalleReceta) {
        this.detalleReceta = detalleReceta;
    }

    public long getStockMaximoPosible() {
        return stockMaximoPosible;
    }

    public void setStockMaximoPosible(long stockMaximoPosible) {
        this.stockMaximoPosible = stockMaximoPosible;
    }
}