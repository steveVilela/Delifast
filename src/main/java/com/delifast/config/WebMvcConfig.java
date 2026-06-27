package com.delifast.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;


import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/delifast_uploads/");
    }

    // ✅ AQUÍ agregas esto
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("inicio");
        registry.addViewController("/inicio").setViewName("inicio");
        registry.addViewController("/acerca").setViewName("acerca");
        registry.addViewController("/especialidades").setViewName("especialidades");
        registry.addViewController("/contacto").setViewName("contacto");
    }


}