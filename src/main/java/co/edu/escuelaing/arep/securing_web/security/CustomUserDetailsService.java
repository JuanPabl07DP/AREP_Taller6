package co.edu.escuelaing.arep.securing_web.security;

import co.edu.escuelaing.arep.securing_web.data.User;
import co.edu.escuelaing.arep.securing_web.exception.UserNotFoundException;
import co.edu.escuelaing.arep.securing_web.repository.UserRepository;
import co.edu.escuelaing.arep.securing_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userService.findUserByMail(email);
            return new org.springframework.security.core.userdetails.User(
                    user.getMail(),
                    user.getPasswordHash(),
                    new ArrayList<>()
            );
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }
    }
}