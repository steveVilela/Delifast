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
        // 1. Recuperamos la lista base de productos activos desde el DAO
        List<Producto> productosActivos = productoService.listarSoloActivos();
        
        /* ====================================================================
           🚀 SINCRONIZACIÓN EN TIEMPO REAL CON LA COCINA (KARDEX)
           Recorremos cada producto para asignarle su stock real según insumos libres
           ==================================================================== */
        for (Producto prod : productosActivos) {
            int stockRealInsumos = productoService.calcularStockRealPorInsumos(prod.getProductoId());
            
            // Reemplazamos temporalmente en memoria el stock estático por el real de la cocina
            prod.setStock(stockRealInsumos);
            
            System.out.println("📊 Sincronización Catálogo -> Producto: " + prod.getNombre() + " | Stock Real calculado: " + stockRealInsumos);
        }
        
        // 2. Adjuntamos la colección ya recalculada y protegida al modelo para Thymeleaf
        model.addAttribute("productos", productosActivos);
        
        // Despacha la renderización a: src/main/resources/templates/menu.html
        return "menu";
    }
}