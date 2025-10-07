package com.levelup.journey.platform.community;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@EnableJpaAuditing
@Controller
public class CommunityApplication {

    public static void main(String[] args) {
        // Load .env file before Spring Boot starts
        loadEnvironmentVariables();

        SpringApplication.run(CommunityApplication.class, args);
        System.out.println("Swagger UI available at: http://localhost:8080/swagger-ui/index.html");
    }

    /**
     * Load environment variables from .env file.
     * This method runs before Spring Boot initialization.
     */
    private static void loadEnvironmentVariables() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // Set system properties from .env file if not already set
            dotenv.entries().forEach(entry -> {
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });

            System.out.println("✓ Environment variables loaded successfully from .env file");
        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
            System.err.println("Application will use system environment variables or default values");
        }
    }

    @RequestMapping("/")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html";
    }

}
