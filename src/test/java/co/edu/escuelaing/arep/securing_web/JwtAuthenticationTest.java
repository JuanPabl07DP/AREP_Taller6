package co.edu.escuelaing.arep.securing_web;

import co.edu.escuelaing.arep.securing_web.controller.UserDto;
import co.edu.escuelaing.arep.securing_web.security.JwtService;
import co.edu.escuelaing.arep.securing_web.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtAuthenticationTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void testJwtTokenGeneration() {
        String testEmail = "test@example.com";
        String testPassword = "test123";

        try {
            // Crear usuario (o usar uno existente)
            try {
                userService.createUser(new UserDto(testEmail, testPassword));
            } catch (Exception e) {}

            // Obtener detalles de usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

            // Generar token
            String token = jwtService.generateToken(userDetails);

            // Verificar que el token no sea nulo o vacío
            assertNotNull(token);
            assertTrue(token.length() > 0);

            // Verificar que podemos extraer el nombre de usuario del token
            String extractedUsername = jwtService.extractUsername(token);
            assertEquals(testEmail, extractedUsername);

            // Verificar que el token es válido
            assertTrue(jwtService.isTokenValid(token, userDetails));
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }
}
