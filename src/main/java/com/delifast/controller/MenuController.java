package com.delifast.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.delifast.model.Producto;
import com.delifast.service.ProductoService;

@Controller
public class MenuController {

    @Autowired
    private ProductoService productoService;

    // Intercepta las solicitudes GET dirigidas a la URL "/menu"
    @GetMapping("/menu")
    public String verMenu(Model model) {
        // Ejecuta el método optimizado del DAO que filtra solo productos activos y con stock > 0
        List<Producto> productosActivos = productoService.listarSoloActivos();
        
        // Adjunta la colección al objeto contenedor (Reemplaza a req.setAttribute)
        model.addAttribute("productos", productosActivos);
        
        // Despacha la renderización a: src/main/resources/templates/menu.html
        return "menu";
    }
}