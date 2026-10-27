package model;

public class Utente {
    private String login;
    private String password;

    public Utente(String login, String password) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire la login.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire la password.");
        }
        this.login = login.trim();
        this.password = password;
    }

    public boolean login(String login, String password) {
        return this.login.equals(login) && this.password.equals(password);
    }

    public String getLogin() {
        return login;
    }

    boolean haStessaPassword(Utente altro) {
        return password.equals(altro.password);
    }
}
