package com.delifast.service;

import com.delifast.dao.RecetaDAO;
import com.delifast.dao.ProductoDAO; // Asumiendo que se llama así tu DAO de productos
import com.delifast.dao.InsumoDAO;   // Asumiendo que se llama así tu DAO de insumos
import com.delifast.model.Insumo;
import com.delifast.model.Producto;
import com.delifast.model.Receta;
import com.delifast.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RecetaServiceImpl implements RecetaService {

    @Autowired
    private RecetaDAO recetaDAO;

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private InsumoDAO insumoDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Receta> listarTodas() {
        return recetaDAO.listarTodas();
    }

    @Override
    @Transactional // 👈 CRUCIAL: Requerido por EntityManager para hacer .persist()
    public void registrarComponente(int productoId, int insumoId, int cantidadNecesaria) {
        // 1. Buscamos las entidades gestionadas por el EntityManager
        Producto producto = productoDAO.obtenerPorId(productoId);
        Insumo insumo = insumoDAO.obtenerPorId(insumoId);

        if (producto == null || insumo == null) {
            throw new RuntimeException("Producto o Insumo no encontrado en el sistema.");
        }

        // 2. Armamos el objeto intermedio usando los setters generados por Lombok
        Receta nuevaReceta = new Receta();
        nuevaReceta.setProducto(producto);
        nuevaReceta.setInsumo(insumo);
        nuevaReceta.setCantidadNecesaria(cantidadNecesaria);

        // 3. Enviamos el registro completo al DAO para persistirlo
        recetaDAO.registrarComponente(nuevaReceta);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Receta> buscarInsumosPorProducto(int productoId) {
        // Trae todas las recetas de la BD usando tu DAO y filtra solo las que coincidan con el productoId buscado
        return recetaDAO.listarTodas().stream()
                .filter(r -> r.getProducto() != null && r.getProducto().getProductoId() == productoId)
                .toList();
    }
}