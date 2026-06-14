package com.delifast.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Al poner la raíz con /**, Spring buscará automáticamente en todas las subcarpetas internas (productos y categorias)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/delifast_uploads/");
    }
}