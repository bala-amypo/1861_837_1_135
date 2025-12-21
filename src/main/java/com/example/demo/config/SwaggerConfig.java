package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        Server httpsServer = new Server();
        httpsServer.setUrl("https://9445.pro604cr.amypo.ai");
        httpsServer.setDescription("Amypo HTTPS Server");

        return new OpenAPI()
                .servers(List.of(httpsServer));
    }
}
