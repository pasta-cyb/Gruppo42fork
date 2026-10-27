package model;

public class Letto {
    private String codiceLetto;
    private Stanza stanza;

    public Letto(String codiceLetto, Stanza stanza) {
        if (codiceLetto == null || codiceLetto.trim().isEmpty() || stanza == null) {
            throw new IllegalArgumentException("Specificare il codice del letto e la stanza.");
        }
        this.codiceLetto = codiceLetto.trim();
        this.stanza = stanza;
    }

    public String getCodiceLetto() {
        return codiceLetto;
    }

    public Stanza getStanza() {
        return stanza;
    }
}
