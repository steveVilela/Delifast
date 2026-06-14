package com.delifast.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.delifast.model.Categoria;
import com.delifast.model.Producto;
import com.delifast.service.CategoriaService;
import com.delifast.service.ProductoService;

@Controller
@RequestMapping("/administrador/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    // Ruta absoluta en disco para salvar las imágenes de forma persistente
    private static final String UPLOAD_DIR = "C:/delifast_uploads/productos/";

 
 // 1. LISTAR Y MOSTRAR FORMULARIO VACÍO
    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("producto", new Producto()); // Objeto vacío para el formulario de inserción
            cargarComponentesComunes(model); // Carga las listas de productos y categorías de forma segura
        } catch (Exception e) {
            e.printStackTrace(); // Registra en la consola si hubo problemas al conectar con la BD
        }
        return "administrador/productos/productos";
    }
    
    // 2. RECUPERAR DATOS PARA EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") int id, Model model) {
        try {
            Producto p = productoService.buscarPorId(id);
            model.addAttribute("producto", p); // Objeto cargado para mutar el formulario a modo edición
            cargarComponentesComunes(model);
        } catch (Exception e) {
            e.printStackTrace(); // Registra en la consola si falló la BD al buscar o cargar listas
        }
        return "administrador/productos/productos";
    }

    // 3. GUARDAR O ACTUALIZAR
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("producto") Producto producto,
                          @RequestParam("fileImagen") MultipartFile file,
                          @RequestParam("imagenActual") String imagenActual) {
        try {
            if (!file.isEmpty()) {
                // Si el usuario subió un archivo nuevo
                String nombreArchivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File subCarpeta = new File(UPLOAD_DIR);
                if (!subCarpeta.exists()) subCarpeta.mkdirs();

                Path rutaCompleta = Paths.get(UPLOAD_DIR + nombreArchivo);
                Files.write(rutaCompleta, file.getBytes());
                producto.setImagen(nombreArchivo);
            } else {
                // Si no se subió ningún archivo, conservamos la imagen previa o la genérica
                producto.setImagen(producto.getProductoId() > 0 ? imagenActual : "sin-imagen.png");
            }
            
            productoService.guardar(producto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String parametroMsj = (producto.getProductoId() > 0) ? "update" : "create";
        return "redirect:/administrador/productos?msj=" + parametroMsj;
    }

    // 4. ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id) {
        try {
            productoService.eliminar(id);
            return "redirect:/administrador/productos?msj=delete";
        } catch (Exception e) {
            // Si el producto ya cuenta con registros asociados en el carrito o pedidos, salta la excepción FK
            return "redirect:/administrador/productos?msj=error_fk";
        }
    }

    // Método auxiliar para evitar duplicar cargas de catálogos en el modelo
    private void cargarComponentesComunes(Model model) { // 👈 ¡Limpio de throws!
        List<Producto> listaProductos = productoService.listarTodos();
        List<Categoria> listaCategorias = categoriaService.listarTodas(); 
        model.addAttribute("productos", listaProductos);
        model.addAttribute("categorias", listaCategorias);
    }
}