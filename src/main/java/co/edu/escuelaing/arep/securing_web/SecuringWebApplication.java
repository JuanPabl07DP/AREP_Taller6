package co.edu.escuelaing.arep.securing_web;

import co.edu.escuelaing.arep.securing_web.controller.UserDto;
import co.edu.escuelaing.arep.securing_web.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SecuringWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecuringWebApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("https://myapachearep.duckdns.org")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public CommandLineRunner run(UserService service){
        return (args) -> {
            service.createUser(new UserDto("john_wick@example.com","jhon123"));
            System.out.println("USER DEMO CREATED");
        };
    }
}