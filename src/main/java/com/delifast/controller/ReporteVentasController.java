package com.delifast.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import com.delifast.dao.ProductoDAO; // 👈 Asegúrate de que estos nombres coincidan con tus interfaces
import com.delifast.dao.ClienteDAO;
import com.delifast.dao.PedidoDAO;
import com.delifast.dto.ProductoReporteDTO;
import com.delifast.service.ExcelReporteService;
import com.delifast.service.PdfReporteService;
import com.delifast.dto.ClienteReporteDTO;
import com.delifast.dto.CajaReporteDTO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/administrador/reportes/ventas")
public class ReporteVentasController {

    // ➕ INYECTAMOS LOS COMPONENTES DE ACCESO A DATOS (DAO)
    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private ClienteDAO clienteDAO;

    @Autowired
    private PedidoDAO pedidoDAO;

    @GetMapping
    public String verReportesComerciales(
            @RequestParam(value = "subVista", required = false, defaultValue = "platos") String subVista,
            Model model, HttpSession session) {
        
        if (session.getAttribute("admin") == null) {
            return "redirect:/administrador/login";
        }

        // 🕵️ CONSOLA DEBUG: Auditoría de parámetros en tu consola de Eclipse
        System.out.println("📊 CONTROLADOR REPORTES: Cargando subVista -> [" + subVista + "]");

        model.addAttribute("subVista", subVista);

        if ("platos".equals(subVista)) {
            model.addAttribute("tituloReporte", "🔥 Ranking de Platos Más Vendidos (Top Ventas)");
            
            List<ProductoReporteDTO> platos = productoDAO.obtenerTopPlatosMasVendidos();
            System.out.println("➡️ Cantidad de platos recuperados del DAO: " + (platos != null ? platos.size() : "NULL"));
            
            model.addAttribute("listaPlatos", platos);

        } else if ("clientes".equals(subVista)) {
            model.addAttribute("tituloReporte", "👥 Historial de Clientes Frecuentes (Fidelidad)");
            
            List<ClienteReporteDTO> clientes = clienteDAO.obtenerClientesMasFrecuentes();
            model.addAttribute("listaClientes", clientes);

        } else if ("ingresos".equals(subVista)) {
            model.addAttribute("tituloReporte", "💰 Arqueo de Caja y Canales de Pago");
            
            List<CajaReporteDTO> caja = pedidoDAO.obtenerArqueoCaja();
            model.addAttribute("listaCaja", caja);
        }

        return "administrador/ventas/reportes_ventas";
    }
    
 // 📋 DENTRO DE TU MÉTODO exportarPdf:
    @GetMapping("/exportar/pdf")
    public void exportarPdf(HttpServletResponse response, @RequestParam(name = "tipo") String tipo) throws IOException {
        if ("platos".equals(tipo)) {
            List<ProductoReporteDTO> platos = productoDAO.obtenerTopPlatosMasVendidos();
            PdfReporteService.exportarPlatos(response, platos);
        } 
        else if ("clientes".equals(tipo)) {
            List<com.delifast.dto.ClienteReporteDTO> clientes = clienteDAO.obtenerClientesMasFrecuentes();
            PdfReporteService.exportarClientes(response, clientes);
        } 
        else if ("ingresos".equals(tipo)) { // 👈 SÚMALE ESTE BLOQUE FINANCIERO
            List<com.delifast.dto.CajaReporteDTO> caja = pedidoDAO.obtenerArqueoCaja();
            PdfReporteService.exportarCaja(response, caja);
        }
    }

    // 📋 DENTRO DE TU MÉTODO exportarExcel:
    @GetMapping("/exportar/excel")
    public void exportarExcel(HttpServletResponse response, @RequestParam(name = "tipo") String tipo) throws IOException {
        if ("platos".equals(tipo)) {
            List<ProductoReporteDTO> platos = productoDAO.obtenerTopPlatosMasVendidos();
            ExcelReporteService.exportarPlatos(response, platos);
        } 
        else if ("clientes".equals(tipo)) {
            List<com.delifast.dto.ClienteReporteDTO> clientes = clienteDAO.obtenerClientesMasFrecuentes();
            ExcelReporteService.exportarClientes(response, clientes);
        } 
        else if ("ingresos".equals(tipo)) { // 👈 SÚMALE ESTE BLOQUE FINANCIERO
            List<com.delifast.dto.CajaReporteDTO> caja = pedidoDAO.obtenerArqueoCaja();
            ExcelReporteService.exportarCaja(response, caja);
        }
    }
    
}