package com.delifast.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.delifast.model.Administrador;
import com.delifast.service.AdministradorService;

@Controller
@RequestMapping("/administrador/usuarios")
public class UsuarioController {

    @Autowired
    private AdministradorService adminService;

    // LISTAR Y PREPARAR FORMULARIO (Dos columnas en una sola pantalla)
    @GetMapping
    public String index(Model model) {
        model.addAttribute("usuarios", adminService.listarUsuarios());
        
        // 🛡️ Si no viene un usuario pre-cargado de la edición, inyectamos uno totalmente vacío
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Administrador());
        }
        
        model.addAttribute("modulo", "usuarios"); // Mantiene encendido tu menú en rojo
        return "administrador/usuarios/usuarios";
    }

    // PROCESAR GUARDAR / ACTUALIZAR
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") Administrador administrador, RedirectAttributes redirect) {
        try {
            if (administrador.getAdminId() == 0) {
                adminService.registrarUsuario(administrador);
                // 🔄 CAMBIADO A addFlashAttribute para limpiar la URL y vaciar el formulario
                redirect.addFlashAttribute("msj", "create");
            } else {
                adminService.modificarUsuario(administrador);
                redirect.addFlashAttribute("msj", "update");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("msj", "error");
        }
        return "redirect:/administrador/usuarios";
    }

    // CARGAR DATOS PARA EDICIÓN
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") int id, Model model) {
        model.addAttribute("usuarios", adminService.listarUsuarios());
        model.addAttribute("usuario", adminService.buscarPorId(id)); // Carga el usuario seleccionado
        model.addAttribute("modulo", "usuarios");
        return "administrador/usuarios/usuarios";
    }

    // ELIMINAR USUARIO
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes redirect) {
        try {
            adminService.eliminarUsuario(id);
            // 🔄 CAMBIADO A addFlashAttribute
            redirect.addFlashAttribute("msj", "delete");
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("msj", "error_fk");
        }
        return "redirect:/administrador/usuarios";
    }
}