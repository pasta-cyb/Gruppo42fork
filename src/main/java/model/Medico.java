package model;

import java.util.ArrayList;
import java.util.List;

public class Medico extends Utente {
    private String matricola;
    private Reparto reparto;
    private List<Turno> turni;
    private List<Prestazione> prestazioni;

    public Medico(String login, String password) {
        this(login, password, login);
    }

    public Medico(String login, String password, String matricola) {
        super(login, password);
        if (matricola == null || matricola.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire la matricola del medico.");
        }
        this.matricola = matricola.trim();
        this.turni = new ArrayList<>();
        this.prestazioni = new ArrayList<>();
    }

    public String getMatricola() {
        return matricola;
    }

    public Reparto getReparto() {
        return reparto;
    }

    public List<Turno> getTurni() {
        return new ArrayList<>(turni);
    }

    public List<Prestazione> getPrestazioni() {
        return new ArrayList<>(prestazioni);
    }

    void assegnaReparto(Reparto reparto) {
        this.reparto = reparto;
    }

    void aggiungiTurno(Turno turno) {
        turni.add(turno);
    }

    void aggiungiPrestazione(Prestazione prestazione) {
        prestazioni.add(prestazione);
    }
}
