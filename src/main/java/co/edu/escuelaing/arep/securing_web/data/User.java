package co.edu.escuelaing.arep.securing_web.data;

import java.util.UUID;

public class User {

    private String id;
    private String mail;
    private String passwordHash;

    public User(String mail, String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.mail = mail;
        this.passwordHash = passwordHash;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", mail='" + mail + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}