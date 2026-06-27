package com.delifast.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delifast.dao.MovimientoDAO;
import com.delifast.dao.InsumoDAO; // ➕ Inyectamos el DAO de Insumos
import com.delifast.model.MovimientoInventario;
import com.delifast.model.Insumo;         // ➕ Importamos el Modelo Insumo
import com.delifast.model.Administrador;

@Service
public class MovimientoService implements IMovimientoService {

    @Autowired
    private MovimientoDAO movimientoDAO;

    @Autowired
    private InsumoDAO insumoDAO; // 👈 Requerido para buscar el insumo antes de asociarlo

    public List<MovimientoInventario> listarTodos() {
        return movimientoDAO.listar();
    }

    @Override
    @Transactional // 👈 Mantiene la consistencia atómica de la transacción
    public void registrar(int insumoId, String tipoMovimiento, int cantidad, String motivo, Administrador admin) {
        MovimientoInventario mov = new MovimientoInventario();
        
        // 1. 🔍 Buscamos la entidad Insumo completa en la BD usando el ID del formulario
        Insumo insumo = insumoDAO.obtenerPorId(insumoId);
        
        // 2. 🔗 Seteamos la relación completa al movimiento (adiós int productoId)
        mov.setInsumo(insumo); 
        mov.setTipoMovimiento(tipoMovimiento);
        mov.setCantidad(cantidad);
        mov.setMotivo(motivo);
        mov.setAdministrador(admin);
        
        // 3. 💾 Insertamos el registro a través del DAO relacional
        movimientoDAO.insertar(mov);
    }
}