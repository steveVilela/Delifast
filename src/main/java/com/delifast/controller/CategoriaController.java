package com.delifast.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.delifast.model.Categoria;
import com.delifast.service.CategoriaService;

@Controller
@RequestMapping("/administrador/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // ==========================================================
    // LISTAR CATEGORÍAS
    // ==========================================================
    @GetMapping
    public String listarCategorias(@RequestParam(name = "msj", required = false) String msj, Model model) {
        
        List<Categoria> categorias = categoriaService.listar();
        
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoria", new Categoria()); // Objeto vacío para el formulario de registro
        model.addAttribute("msj", msj); // Captura los mensajes de alerta (create, update, delete, error_fk)
        
        // Apunta a la plantilla: src/main/resources/templates/administrador/categorias/categorias.html
        return "administrador/categorias/categorias";
    }

    // ==========================================================
    // MOSTRAR FORMULARIO EDITAR
    // ==========================================================
    @GetMapping("/editar")
    public String mostrarFormularioEditar(@RequestParam("id") int id, Model model) {
        
        Categoria categoria = categoriaService.buscarPorId(id);
        List<Categoria> categorias = categoriaService.listar();
        
        model.addAttribute("categoria", categoria); // Categoría con datos cargados para edición
        model.addAttribute("categorias", categorias); // Mantiene la tabla de categorías en la vista
        
        return "administrador/categorias/categorias";
    }

    // ==========================================================
    // GUARDAR (NUEVA CATEGORÍA)
    // ==========================================================
    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute("categoria") Categoria categoria) {
        
        categoriaService.registrar(categoria);
        
        return "redirect:/administrador/categorias?msj=create";
    }

    // ==========================================================
    // ACTUALIZAR (CATEGORÍA EXISTENTE)
    // ==========================================================
    @PostMapping("/actualizar")
    public String actualizarCategoria(@ModelAttribute("categoria") Categoria categoria) {
        
        categoriaService.actualizar(categoria);
        
        return "redirect:/administrador/categorias?msj=update";
    }

    // ==========================================================
    // ELIMINAR CATEGORÍA (CON CAPTURA DE ERROR DE INTEGRACIÓN / FK)
    // ==========================================================
    @GetMapping("/eliminar")
    public String eliminarCategoria(@RequestParam("id") int id) {
        try {
            categoriaService.eliminar(id);
            return "redirect:/administrador/categorias?msj=delete";
            
        } catch (Exception e) {
            // Si la categoría está asignada a un producto en tb_producto, 
            // JPA lanzará una excepción previniendo la ruptura de integridad.
            return "redirect:/administrador/categorias?msj=error_fk";
        }
    }
}