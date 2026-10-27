package model;

import java.time.LocalDateTime;

public class Prestazione {
    private String tipo;
    private LocalDateTime dataOraInizio;
    private LocalDateTime dataOraFine;
    private String esito;
    private Medico medico;
    private Ricovero ricovero;

    public Prestazione(String tipo, LocalDateTime dataOraInizio, LocalDateTime dataOraFine,
                       Medico medico, Ricovero ricovero) {
        if (tipo == null || (!tipo.equalsIgnoreCase("visita") && !tipo.equalsIgnoreCase("intervento"))) {
            throw new IllegalArgumentException("Il tipo deve essere visita oppure intervento.");
        }
        if (dataOraInizio == null || dataOraFine == null || medico == null || ricovero == null) {
            throw new IllegalArgumentException("Completare i dati della prestazione.");
        }
        if (!dataOraFine.isAfter(dataOraInizio)) {
            throw new IllegalArgumentException("La fine della prestazione deve seguire l'inizio.");
        }
        this.tipo = tipo;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
        this.medico = medico;
        this.ricovero = ricovero;
        this.esito = "In attesa di esito";
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getDataOraInizio() {
        return dataOraInizio;
    }

    public LocalDateTime getDataOraFine() {
        return dataOraFine;
    }

    public String getEsito() {
        return esito;
    }

    public Medico getMedico() {
        return medico;
    }

    public Ricovero getRicovero() {
        return ricovero;
    }

    void aggiornaOrario(LocalDateTime inizio, LocalDateTime fine) {
        this.dataOraInizio = inizio;
        this.dataOraFine = fine;
    }

    void aggiornaEsito(String esito) {
        this.esito = esito;
    }
}
