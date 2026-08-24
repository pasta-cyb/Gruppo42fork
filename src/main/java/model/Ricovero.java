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

    public Ricovero(Paziente paziente, Letto letto, LocalDateTime dataOraInizio, LocalDateTime dataOraDimissionePrevista) {
        this.paziente = paziente;
        this.letto = letto;
        this.dataOraInizio = dataOraInizio;
        this.dataOraDimissionePrevista = dataOraDimissionePrevista;
        this.prestazioni = new ArrayList<>();
    }
}
