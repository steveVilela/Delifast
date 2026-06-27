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

    // 1. Muestra la vista del formulario
    @GetMapping("/administrador/login")
    public String mostrarLogin() {
        return "administrador/login";
    }

    // 2. Procesa las credenciales enviadas y enruta según rol
    @PostMapping("/administrador/login")
    public String procesarLogin(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                HttpSession session,
                                Model model) {
        
        Administrador admin = administradorService.login(email, password);

        if (admin != null) {
            // Guardamos el objeto y datos clave en sesión
            session.setAttribute("admin", admin);
            session.setAttribute("adminEmail", admin.getEmail());
            
            // 🚀 ENRUTAMIENTO DINÁMICO SEGÚN EL ROL
            String rolUsuario = admin.getRol();
            
            if ("ALMACEN".equals(rolUsuario)) {
                return "redirect:/administrador/inventario"; // 📦 Almacenero directo a su almacén
            } else if ("VENTAS".equals(rolUsuario)) {
                return "redirect:/administrador/ventas";      // 💰 Vendedor directo a la caja POS
            } else {
                return "redirect:/administrador/productos";   // 👑 Administrador General a Productos
            }
            
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos o cuenta inactiva");
            return "administrador/login";
        }
    }

    // 3. Ruta global para destruir la sesión activa
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // Destruye todos los datos en sesión
        return "redirect:/menu";
    }
    
 // Prueba de enlace con GitHub - Rommel Rosales
}