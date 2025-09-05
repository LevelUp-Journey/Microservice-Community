package com.levelupjourney.microservicecommunity.shared.infrastructure.documentation.openapi.configuration;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SwaggerUiEndpointAnnouncer implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("Swagger UI available at: http://localhost:8080/swagger-ui.html");
    }
}