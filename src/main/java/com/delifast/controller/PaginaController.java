package com.delifast.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {


@GetMapping("/inicio")
public String inicio() {
    return "inicio";
}

@GetMapping("/acerca")
public String acerca() {
    return "acerca";
}

@GetMapping("/especialidades")
public String especialidades() {
    return "especialidades";
}

@GetMapping("/contacto")
public String contacto() {
    return "contacto";
}


}
