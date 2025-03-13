package co.edu.escuelaing.arep.securing_web.service;

import co.edu.escuelaing.arep.securing_web.controller.UserDto;
import co.edu.escuelaing.arep.securing_web.data.User;
import co.edu.escuelaing.arep.securing_web.exception.InvalidCredentialsException;
import co.edu.escuelaing.arep.securing_web.exception.UserAlreadyExistException;
import co.edu.escuelaing.arep.securing_web.exception.UserBadRequestException;
import co.edu.escuelaing.arep.securing_web.exception.UserNotFoundException;
import co.edu.escuelaing.arep.securing_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, User> userDatabase = UserRepository.userDatabase;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private void saveUser(UserDto userDto) {
        String passwordEncrypt = passwordEncoder.encode(userDto.getPassword());
        User userSave = new User(userDto.getMail(), passwordEncrypt);
        userDatabase.put(userSave.getMail(), userSave);
    }

    public List<User> findUsers() {
        // Convierte la colección de valores a una lista
        Collection<User> userCollection = userDatabase.values();
        return new ArrayList<>(userCollection); // Convierte a ArrayList
    }

    public User findUserByMail(String mail) throws UserNotFoundException {
        User userFound = userDatabase.get(mail);
        if (userFound == null) {
            throw new UserNotFoundException(mail);
        }
        return userFound;
    }

    public void createUser(UserDto userDto) throws UserAlreadyExistException, UserBadRequestException {
        if (userDto.getMail() == null || userDto.getPassword() == null) {
            throw new UserBadRequestException();
        }
        if (!(userDatabase.get(userDto.getMail()) == null)) {
            throw new UserAlreadyExistException(userDto.getMail());
        }
        saveUser(userDto);
    }

    public User authenticate(UserDto loginDto) throws UserNotFoundException, InvalidCredentialsException {
        User user = findUserByMail(loginDto.getMail());

        // Verificar si el usuario existe
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + loginDto.getMail());
        }

        // Verificar la contraseña
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Devuelve el usuario autenticado
        return user;
    }
}