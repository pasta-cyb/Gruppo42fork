package model;

import java.util.ArrayList;
import java.util.List;

public class Reparto {
    private String nome;
    private List<Stanza> stanze;
    private List<Medico> medici;
    private Ospedale ospedale;

    public Reparto(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire il nome del reparto.");
        }
        this.nome = nome.trim();
        this.stanze = new ArrayList<>();
        this.medici = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public Ospedale getOspedale() {
        return ospedale;
    }

    public List<Stanza> getStanze() {
        return new ArrayList<>(stanze);
    }

    public List<Medico> getMedici() {
        return new ArrayList<>(medici);
    }

    void aggiungiStanza(Stanza stanza) {
        stanze.add(stanza);
    }

    void assegnaOspedale(Ospedale ospedale) {
        this.ospedale = ospedale;
    }

    void aggiungiMedico(Medico medico) {
        medici.add(medico);
    }
}
