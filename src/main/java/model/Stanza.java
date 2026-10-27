package model;

import java.util.ArrayList;
import java.util.List;

public class Stanza {
    private int numeroStanza;
    private List<Letto> letti;
    private Reparto reparto;

    public Stanza(int numeroStanza) {
        if (numeroStanza <= 0) {
            throw new IllegalArgumentException("Il numero della stanza deve essere positivo.");
        }
        this.numeroStanza = numeroStanza;
        this.letti = new ArrayList<>();
    }

    public int getNumeroStanza() {
        return numeroStanza;
    }

    public Reparto getReparto() {
        return reparto;
    }

    public List<Letto> getLetti() {
        return new ArrayList<>(letti);
    }

    void assegnaReparto(Reparto reparto) {
        this.reparto = reparto;
    }

    void aggiungiLetto(Letto letto) {
        letti.add(letto);
    }
}
