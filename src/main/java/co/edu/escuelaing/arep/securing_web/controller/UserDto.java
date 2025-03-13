package co.edu.escuelaing.arep.securing_web.controller;

public class UserDto {
    private String mail;
    private String password;

    public UserDto() {
    }

    public UserDto(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}