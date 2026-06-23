package com.delifast.controller;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.delifast.service.MovimientoService;
import com.delifast.service.InsumoService;
import com.delifast.service.ProductoService;
import com.delifast.service.RecetaService;
import com.delifast.dto.ProduccionSimuladaDTO;
import com.delifast.model.Receta;

@Controller
@RequestMapping("/administrador/reportes/almacen")
public class ReportesAlmacenController {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private InsumoService insumoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private RecetaService recetaService;

    @GetMapping
    public String verReportesLogisticos(
            @RequestParam(value = "subVista", required = false, defaultValue = "disponibilidad") String subVista, // 👈 Entra directo a tu simulador estrella
            Model model, HttpSession session) {
        
        if (session.getAttribute("admin") == null) {
            return "redirect:/administrador/login";
        }

        model.addAttribute("subVista", subVista);

        if ("kardex".equals(subVista)) {
            model.addAttribute("tituloReporte", "📋 Historial de Kardex de Almacén");
            model.addAttribute("movimientos", movimientoService.listarTodos());

        } else if ("stockActual".equals(subVista)) {
            model.addAttribute("tituloReporte", "📦 Existencias Actuales de Insumos");
            model.addAttribute("insumos", insumoService.listarTodos());

        } else if ("disponibilidad".equals(subVista)) {
            model.addAttribute("tituloReporte", "🍳 Capacidad de Production de Cocina");
            List<ProduccionSimuladaDTO> simulacion = productoService.obtenerSimulacionProduccion();
            model.addAttribute("listaSimulacion", simulacion);

        } else if ("recetas".equals(subVista)) {
            model.addAttribute("tituloReporte", "📜 Composición Estructural de Recetas");
            List<Receta> todasLasRecetas = recetaService.listarTodas(); 
            model.addAttribute("listaRecetas", todasLasRecetas);
        }

        return "administrador/inventario/reportes_almacen"; 
    }
}