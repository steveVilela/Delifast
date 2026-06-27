package com.delifast.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/carrito")
public class CarritoWebController {

    @GetMapping
    public String verCarrito() {
        // Renderiza la plantilla templates/carrito.html
        return "carrito"; 
    }
}