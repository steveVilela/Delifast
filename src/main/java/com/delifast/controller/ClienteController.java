package com.delifast.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.delifast.model.Cliente;
import com.delifast.service.ClienteService;

@Controller
@RequestMapping("/administrador/clientes") // 👈 Esta es la ruta base que pusimos en tu Navbar
public class ClienteController {

    @Autowired
    private ClienteService clienteService; // 👈 El controlador Llama al Service, NUNCA al DAO directamente

    // 1. LISTAR CLIENTES Y CARGAR FORMULARIO VACÍO
    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("cliente", new Cliente()); // Objeto vacío para el formulario
            model.addAttribute("clientes", clienteService.listarTodos()); // Carga la tabla
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "administrador/clientes/clientes"; // 👈 Apunta a tu futura vista HTML
    }

    // 2. GUARDAR / ACTUALIZAR CLIENTE
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("cliente") Cliente c, RedirectAttributes redirect) {
        try {
            if (c.getClienteId() > 0) {
                clienteService.modificar(c);
                redirect.addFlashAttribute("msj", "update"); // Desencadena SweetAlert de éxito
            } else {
                clienteService.registrar(c);
                redirect.addFlashAttribute("msj", "create");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("msj", "error");
        }
        return "redirect:/administrador/clientes"; // Redirecciona para refrescar la pantalla
    }

    // 3. CARGAR DATOS PARA EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") int id, Model model) {
        try {
            Cliente c = clienteService.buscarPorId(id);
            model.addAttribute("cliente", c); // Carga el objeto con datos en el formulario
            model.addAttribute("clientes", clienteService.listarTodos()); // Mantiene el listado abajo
            model.addAttribute("titulo", "Editar Cliente");
            model.addAttribute("color", "primary");
            model.addAttribute("boton", "ACTUALIZAR");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "administrador/clientes/clientes";
    }

    // 4. ELIMINAR CLIENTE
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes redirect) {
        try {
            clienteService.remover(id);
            redirect.addFlashAttribute("msj", "delete");
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("msj", "error_fk"); // Por si el cliente ya tiene pedidos amarrados
        }
        return "redirect:/administrador/clientes";
    }
}