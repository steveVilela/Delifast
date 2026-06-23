package com.delifast.dao;

import com.delifast.model.Receta;
import java.util.List;

public interface RecetaDAO {
    List<Receta> listarTodas();
    void registrarComponente(Receta receta);
}