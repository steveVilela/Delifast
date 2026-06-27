package com.delifast.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delifast.dao.CategoriaDAO;
import com.delifast.model.Categoria;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaDAO categoriaDAO;

   
    public List<Categoria> listarTodas()  {
        return categoriaDAO.listar();
    }

    // 2. Añadimos "throws Exception" aquí
    public Categoria buscarPorId(int id)  {
        return categoriaDAO.obtenerPorId(id);
    }

    // 3. Añadimos "throws Exception" aquí
    @Transactional
    public void guardar(Categoria c) {
        if (c.getCategoriaId() > 0) {
            categoriaDAO.actualizar(c);
        } else {
            categoriaDAO.insertar(c);
        }
    }

    // 4. Añadimos "throws Exception" aquí
    @Transactional
    public void eliminar(int id) throws Exception {
        categoriaDAO.eliminar(id);
    }
}