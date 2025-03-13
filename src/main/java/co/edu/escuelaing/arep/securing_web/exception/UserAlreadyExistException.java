package co.edu.escuelaing.arep.securing_web.exception;

public class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String message) {
        super("User mail " + message + " already exist");
    }
}