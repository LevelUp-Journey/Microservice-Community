package com.levelup.journey.platform;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableDiscoveryClient
public class CommunityApplication implements CommandLineRunner, WebMvcConfigurer {

    @Value("${server.port:8086}")
    private String serverPort;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerPath;

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // Don't crash if .env is not there (e.g., in production)
                .load();

        // Set environment variables from .env as system properties for Spring Boot
        for (DotenvEntry entry : dotenv.entries()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }

        SpringApplication.run(CommunityApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String swaggerUrl = "http://localhost:" + serverPort + swaggerPath;
        System.out.println("Swagger UI is available at: " + swaggerUrl);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
    }
}
