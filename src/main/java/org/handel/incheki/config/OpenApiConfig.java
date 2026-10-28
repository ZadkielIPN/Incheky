package org.handel.incheki.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI escuelaOpenAPI() {
        return new OpenAPI( )
                .info( new Info( )
                        .title("API REST - Incheky")
                        .version("1.0.0")
                        .description("Documentación de los servicios REST para la administración de Incheky.")
                        .contact( new Contact( ).name("Equipo de Desarrollo") )
                );
    }
}
