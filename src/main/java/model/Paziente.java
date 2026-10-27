package model;

public class Paziente {
    private String codiceFiscale;
    private String nome;
    private String cognome;

    public Paziente(String codiceFiscale, String nome, String cognome) {
        if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire il codice fiscale.");
        }
        this.codiceFiscale = codiceFiscale.trim();
        if (nome == null || nome.trim().isEmpty() || cognome == null || cognome.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire nome e cognome.");
        }
        this.nome = nome.trim();
        this.cognome = cognome.trim();
    }

    public void modificaAnagrafica(String nome, String cognome) {
        if (nome == null || nome.trim().isEmpty() || cognome == null || cognome.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire nome e cognome.");
        }
        this.nome = nome.trim();
        this.cognome = cognome.trim();
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }
}
