package co.edu.escuelaing.arep.securing_web.repository;

import co.edu.escuelaing.arep.securing_web.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    public static final Map<String, User> userDatabase = new HashMap<>();

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}