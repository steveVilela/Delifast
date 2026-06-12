package com.delifast.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Vincula la ruta web /uploads/ con la ubicación física en el disco duro C:
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/delifast_uploads/");
    }
}