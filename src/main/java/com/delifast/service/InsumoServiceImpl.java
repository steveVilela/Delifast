package com.delifast.service;

import com.delifast.dao.InsumoDAO;
import com.delifast.model.Insumo;
import com.delifast.service.InsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InsumoServiceImpl implements InsumoService {

    @Autowired
    private InsumoDAO insumoDAO; // 👈 Conexión directa a tu DAO con EntityManager

    @Override
    @Transactional(readOnly = true)
    public List<Insumo> listarTodos() {
        return insumoDAO.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Insumo buscarPorId(int id) {
        return insumoDAO.obtenerPorId(id);
    }
    
    @Override
    @Transactional // 👈 CRUCIAL: Abre el contexto de transacción de Hibernate
    public void guardar(Insumo insumo) {
        // Al ejecutarse bajo transacción, JPA modifica el objeto asignándole la PK real al instante
        insumoDAO.guardar(insumo);
    }
    
 // ==========================================================
    // ➕ NUEVOS MÉTODOS OPERATIVOS DE INSUMOS (Adaptados de Producto)
    // ==========================================================
    
    @Override
    @Transactional // 👈 REQUERIDO: Habilita la escritura en BD para el merge
    public void actualizarStockMinimo(int id, int stockMinimo) {
        Insumo insumo = insumoDAO.obtenerPorId(id);
        if (insumo != null) {
            insumo.setStockMinimo(stockMinimo);
            // Si tu InsumoDAOImpl hereda o implementa un actualizar genérico lo usas, 
            // sino, como estás bajo transacciones, el EntityManager actualiza solo al mutar el objeto.
            // Si creaste un método actualizar en InsumoDAO puedes descomentar la línea de abajo:
            // insumoDAO.actualizar(insumo);
        }
    }

    @Override
    @Transactional
    public void incrementarStock(int id, int cantidad) {
        Insumo insumo = insumoDAO.obtenerPorId(id);
        if (insumo != null) {
            insumo.setStock(insumo.getStock() + cantidad);
        }
    }

    @Override
    @Transactional
    public void decrementarStock(int id, int cantidad) {
        Insumo insumo = insumoDAO.obtenerPorId(id);
        if (insumo != null) {
            insumo.setStock(insumo.getStock() - cantidad);
        }
    }
    
 
    // 🟢 En tu implementación InsumoServiceImpl.java lo desarrollas así:
    @Override
    @Transactional
    public void actualizarParametrosTecnicos(int insumoId, int stockMinimo, int stockBloqueo) {
        // 1. Buscamos el insumo existente usando el método de tu DAO
        Insumo ins = insumoDAO.obtenerPorId(insumoId);
        
        if (ins != null) {
            // 2. Modificamos los valores en memoria con los nuevos datos del formulario
            ins.setStockMinimo(stockMinimo);
            ins.setStockBloqueo(stockBloqueo); 
            
            // 3. Reutilizamos tu método 'guardar' para que ejecute el UPDATE en MySQL
            insumoDAO.guardar(ins);
            
            System.out.println("💾 Parámetros técnicos actualizados en BD para: " + ins.getNombre());
        }
    }
}