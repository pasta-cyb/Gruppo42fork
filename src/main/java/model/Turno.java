package model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Turno {
    private DayOfWeek giornoSettimana;
    private LocalTime oraInizio;
    private LocalTime oraFine;

    private Medico medico;

    public Turno(DayOfWeek giornoSettimana, LocalTime oraInizio, LocalTime oraFine, Medico medico) {
        this.giornoSettimana = giornoSettimana;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.medico = medico;
    }