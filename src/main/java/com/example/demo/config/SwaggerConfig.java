package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        Server httpsServer = new Server();
        httpsServer.setUrl("https://9188.408procr.amypo.ai/");
        httpsServer.setDescription("Amypo HTTPS Server");

        // JWT Security Scheme
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .servers(List.of(httpsServer))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", jwtScheme))
                .addSecurityItem(new SecurityRequirement()
                        .addList("BearerAuth"));
    }
}
