package co.edu.escuelaing.arep.securing_web.controller;

import co.edu.escuelaing.arep.securing_web.data.User;
import co.edu.escuelaing.arep.securing_web.exception.InvalidCredentialsException;
import co.edu.escuelaing.arep.securing_web.exception.UserNotFoundException;
import co.edu.escuelaing.arep.securing_web.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import co.edu.escuelaing.arep.securing_web.exception.UserAlreadyExistException;
import co.edu.escuelaing.arep.securing_web.exception.UserBadRequestException;
import co.edu.escuelaing.arep.securing_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://myapachearep.duckdns.org")
public class UsersController {
    private final UserService userService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public UsersController(
            UserService userService,
            JwtService jwtService,
            UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    public ResponseEntity<?> getUsers(HttpServletRequest request) {
        return ResponseEntity.ok(userService.findUsers());
    }

    @GetMapping("{mail}")
    public ResponseEntity<?> getUserByMail(@PathVariable String mail) {
        try {
            User user = userService.findUserByMail(mail);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto) {
        try {
            userService.createUser(userDto);

            // Generar token JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getMail());
            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(new AuthenticationResponse(token, userDto.getMail()));
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UserBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto loginDto) {
        try {
            // Verificar credenciales y obtener el usuario autenticado
            User user = userService.authenticate(loginDto);

            // Generar token JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getMail());
            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(new AuthenticationResponse(token, user.getMail()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found: " + e.getMessage());
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok().build();
    }
}