package com.delifast.controller;

import java.io.IOException;
import java.nio.file.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.delifast.model.Categoria;
import com.delifast.service.CategoriaService;

@Controller
@RequestMapping("/administrador/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    private final String UPLOAD_DIR = "C:/delifast_uploads/categorias/";

    @GetMapping
    public String listar(Model model, @ModelAttribute("categoriaObj") Categoria cat) {
        try {
            model.addAttribute("categorias", categoriaService.listarTodas());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Si no viene un objeto de edición cargado, inyectamos uno vacío para el formulario
        if (cat.getCategoriaId() == 0) {
            model.addAttribute("categoria", new Categoria());
            model.addAttribute("titulo", "Nueva Categoría");
            model.addAttribute("boton", "GUARDAR");
            model.addAttribute("color", "success");
        }
        return "administrador/categorias/categorias";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, 
                          @RequestParam("fileImagen") MultipartFile file,
                          RedirectAttributes flash) {
        try {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
                categoria.setImagen(fileName);
            } else if (categoria.getCategoriaId() > 0) {
                // Mantiene la imagen anterior si está editando y no subió una nueva
                Categoria aux = categoriaService.buscarPorId(categoria.getCategoriaId());
                categoria.setImagen(aux.getImagen());
            }

            categoriaService.guardar(categoria);
            flash.addFlashAttribute("msj", categoria.getCategoriaId() > 0 ? "update" : "create");
        } catch (Exception e) { // 👈 Cambiado a Exception general para capturar los errores del Service
            e.printStackTrace();
        }
        return "redirect:/administrador/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") int id, RedirectAttributes flash) {
        try {
            Categoria cat = categoriaService.buscarPorId(id);
            flash.addFlashAttribute("categoria", cat);
            flash.addFlashAttribute("titulo", "Editar Categoría");
            flash.addFlashAttribute("boton", "ACTUALIZAR");
            flash.addFlashAttribute("color", "warning");
            // Truco para reutilizar el GET principal enviando el objeto precargado
            flash.addFlashAttribute("categoriaObj", cat); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/administrador/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes flash) {
        try {
            categoriaService.eliminar(id);
            flash.addFlashAttribute("msj", "delete");
        } catch (Exception e) {
            // Captura de forma segura las restricciones de llave foránea (FK) si la categoría contiene productos
            e.printStackTrace();
            flash.addFlashAttribute("msj", "error_fk");
        }
        return "redirect:/administrador/categorias";
    }
}