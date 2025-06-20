package elytra.stations_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .ignoreAcceptHeader(true)
                .useRegisteredExtensionsOnly(true);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:5173", 
                "http://frontend:5173",
                "http://127.0.0.1:5173"
            ) 
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")
            .exposedHeaders("Authorization") // In case we need to expose JWT in response headers
            .allowCredentials(false) // False because we use JWT in Authorization header, not cookies
            .maxAge(3600); // 1 hour cache for preflight requests
    }
}