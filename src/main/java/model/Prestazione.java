package model;

import java.time.LocalDateTime;

public class Prestazione {

    private String tipo;
    private LocalDateTime dataOraInizio;
    private LocalDateTime dataOraFine;
    private String esito;

    private Medico medico;
    private Ricovero ricovero;


    public Prestazione(String tipo, LocalDateTime dataOraInizio, LocalDateTime dataOraFine, Medico medico, Ricovero ricovero) {
        this.tipo = tipo;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
        this.medico = medico;
        this.ricovero = ricovero;
        this.esito = "In attesa di esito";
    }