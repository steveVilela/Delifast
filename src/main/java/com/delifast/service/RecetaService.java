package com.delifast.service;

import com.delifast.model.Receta;
import java.util.List;

public interface RecetaService {
    
    // Devuelve todas las fórmulas de la BD para pintar la tabla derecha
    List<Receta> listarTodas();
    
    // Procesa la lógica de vinculación uniendo Producto + Insumo + Cantidad
    void registrarComponente(int productoId, int insumoId, int cantidadNecesaria);

    // 🚀 NUEVO MÉTODO: Busca los insumos específicos de un producto vendido
    List<Receta> buscarInsumosPorProducto(int productoId);
}