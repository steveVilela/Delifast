package com.delifast.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.delifast.service.PedidoService;

@Controller
@RequestMapping("/administrador/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // LISTAR TODOS LOS PEDIDOS EN EL PANEL
    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.obtenerHistorialPedidos());
        model.addAttribute("modulo", "pedidos"); // 👈 Esto activa la pestaña Pedidos
        return "administrador/pedidos/pedidos"; // Vista HTML principal
    }

    // VER DETALLES DE UN PEDIDO ESPECÍFICO (Modal o sección alternativa)
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable("id") int id, Model model) {
        model.addAttribute("pedido", pedidoService.buscarPorId(id));
        model.addAttribute("detalles", pedidoService.obtenerDetallesDePedido(id));
        return "administrador/pedidos/detalle_modal :: contenido_detalle"; // Fragmento asíncrono para ventana modal
    }

    // CAMBIAR EL ESTADO DEL PEDIDO (Manejo rápido desde la tabla)
    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam("id") int id, @RequestParam("estado") String estado, RedirectAttributes redirect) {
        try {
            pedidoService.cambiarEstado(id, estado);
            redirect.addFlashAttribute("msj", "estado_ok");
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("msj", "error");
        }
        return "redirect:/administrador/pedidos";
    }
}