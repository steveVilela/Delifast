package com.delifast.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.delifast.model.Administrador;
import com.delifast.service.AdministradorService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminLoginController {

    @Autowired
    private AdministradorService administradorService;

    // Muestra la vista del formulario
    @GetMapping("/administrador/login")
    public String mostrarLogin() {
        return "administrador/login";
    }

    // Procesa las credenciales enviadas
    @PostMapping("/administrador/login")
    public String procesarLogin(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                HttpSession session,
                                Model model) {
        
        Administrador admin = administradorService.autenticar(email, password);

        if (admin != null) {
            // Guardamos el objeto en sesión. Esto activa el menú desplegable de tu navbar de Thymeleaf
            session.setAttribute("admin", admin);
            session.setAttribute("adminEmail", admin.getEmail());
            return "redirect:/administrador/productos";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos o cuenta inactiva");
            return "administrador/login";
        }
    }

    // Ruta global para destruir la sesión activa
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // Destruye todos los datos en sesión
        return "redirect:/menu";
    }
    
 // Prueba de enlace con GitHub - Rommel Rosales
}