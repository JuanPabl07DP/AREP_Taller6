package co.edu.escuelaing.arep.securing_web;

import co.edu.escuelaing.arep.securing_web.controller.UserDto;
import co.edu.escuelaing.arep.securing_web.data.User;
import co.edu.escuelaing.arep.securing_web.exception.InvalidCredentialsException;
import co.edu.escuelaing.arep.securing_web.exception.UserAlreadyExistException;
import co.edu.escuelaing.arep.securing_web.exception.UserBadRequestException;
import co.edu.escuelaing.arep.securing_web.exception.UserNotFoundException;
import co.edu.escuelaing.arep.securing_web.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCreateAndFindUser() throws UserAlreadyExistException, UserBadRequestException, UserNotFoundException {
        String testEmail = "test-create@example.com";
        String testPassword = "testPassword123";
        UserDto userDto = new UserDto(testEmail, testPassword);

        // Intentar eliminar usuario si existe
        try {
            User user = userService.findUserByMail(testEmail);
        } catch (UserNotFoundException e) {
            userService.createUser(userDto);
        }

        // Buscar el usuario
        User foundUser = userService.findUserByMail(testEmail);

        // Verificaciones
        assertNotNull(foundUser);
        assertEquals(testEmail, foundUser.getMail());

        // Verificar que la contraseña está hasheada
        assertTrue(passwordEncoder.matches(testPassword, foundUser.getPasswordHash()));
    }

    @Test
    public void testAuthentication() throws UserAlreadyExistException, UserBadRequestException, UserNotFoundException, InvalidCredentialsException {
        // Preparar datos de prueba
        String testEmail = "test-auth@example.com";
        String testPassword = "authPassword123";
        UserDto userDto = new UserDto(testEmail, testPassword);

        // Crear usuario si no existe
        try {
            userService.createUser(userDto);
        } catch (UserAlreadyExistException e) {
            // Usuario ya existe, continuamos
        }

        // Autenticar con credenciales correctas
        User authenticatedUser = userService.authenticate(userDto);

        // Verificar autenticación exitosa
        assertNotNull(authenticatedUser);
        assertEquals(testEmail, authenticatedUser.getMail());

        // Verificar autenticación fallida con contraseña incorrecta
        UserDto wrongPasswordDto = new UserDto(testEmail, "wrongPassword");
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.authenticate(wrongPasswordDto);
        });

        // Verificar autenticación fallida con usuario inexistente
        UserDto nonExistentUserDto = new UserDto("nonexistent@example.com", "anyPassword");
        assertThrows(UserNotFoundException.class, () -> {
            userService.authenticate(nonExistentUserDto);
        });
    }
}