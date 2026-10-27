package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ricovero {
    private LocalDateTime dataOraInizio;
    private LocalDateTime dataOraDimissionePrevista;
    private LocalDateTime dataOraDimissioneEffettiva;
    private Paziente paziente;
    private Letto letto;
    private List<Prestazione> prestazioni;

    public Ricovero(Paziente paziente, Letto letto, LocalDateTime dataOraInizio,
                    LocalDateTime dataOraDimissionePrevista) {
        if (paziente == null || letto == null || dataOraInizio == null
                || dataOraDimissionePrevista == null) {
            throw new IllegalArgumentException("Completare i dati del ricovero.");
        }
        if (!dataOraDimissionePrevista.isAfter(dataOraInizio)) {
            throw new IllegalArgumentException("La dimissione deve essere successiva all'ingresso.");
        }
        this.paziente = paziente;
        this.letto = letto;
        this.dataOraInizio = dataOraInizio;
        this.dataOraDimissionePrevista = dataOraDimissionePrevista;
        this.prestazioni = new ArrayList<>();
    }

    public Paziente getPaziente() {
        return paziente;
    }

    public Letto getLetto() {
        return letto;
    }

    public LocalDateTime getDataOraInizio() {
        return dataOraInizio;
    }

    public LocalDateTime getDataOraDimissionePrevista() {
        return dataOraDimissionePrevista;
    }

    public LocalDateTime getDataOraDimissioneEffettiva() {
        return dataOraDimissioneEffettiva;
    }

    // Se manca la dimissione effettiva, si considera quella prevista.
    public LocalDateTime getDataOraFine() {
        if (dataOraDimissioneEffettiva != null) {
            return dataOraDimissioneEffettiva;
        } else {
            return dataOraDimissionePrevista;
        }
    }

    public List<Prestazione> getPrestazioni() {
        return new ArrayList<>(prestazioni);
    }

    // L'inizio e' incluso nell'occupazione, la dimissione e' esclusa.
    public boolean eInCorso(LocalDateTime istante) {
        if (istante == null) {
            throw new IllegalArgumentException("Specificare l'istante da controllare.");
        }
        return !istante.isBefore(dataOraInizio) && istante.isBefore(getDataOraFine());
    }

    void aggiorna(Letto letto, LocalDateTime inizio, LocalDateTime prevista, LocalDateTime effettiva) {
        this.letto = letto;
        this.dataOraInizio = inizio;
        this.dataOraDimissionePrevista = prevista;
        this.dataOraDimissioneEffettiva = effettiva;
    }

    void aggiungiPrestazione(Prestazione prestazione) {
        prestazioni.add(prestazione);
    }
}
