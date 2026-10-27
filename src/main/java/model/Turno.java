package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Turno {
    private DayOfWeek giornoSettimana;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private Medico medico;

    public Turno(DayOfWeek giornoSettimana, LocalTime oraInizio, LocalTime oraFine, Medico medico) {
        if (giornoSettimana == null || oraInizio == null || oraFine == null || medico == null) {
            throw new IllegalArgumentException("Completare i dati del turno.");
        }
        if (oraInizio.equals(oraFine)) {
            throw new IllegalArgumentException("Il turno deve avere una durata tra zero e 24 ore, escluse.");
        }
        this.giornoSettimana = giornoSettimana;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.medico = medico;
    }

    public DayOfWeek getGiornoSettimana() {
        return giornoSettimana;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public Medico getMedico() {
        return medico;
    }

    public boolean contiene(LocalDateTime inizio, LocalDateTime fine) {
        if (inizio == null || fine == null || !fine.isAfter(inizio)) {
            return false;
        }

        // Il turno potrebbe essere iniziato il giorno precedente.
        LocalDate giorno = inizio.toLocalDate().minusDays(1);
        for (int i = 0; i < 2; i++) {
            if (giorno.getDayOfWeek() == giornoSettimana) {
                LocalDateTime inizioTurno = giorno.atTime(oraInizio);
                LocalDateTime fineTurno = calcolaFine(giorno);
                if (!inizio.isBefore(inizioTurno) && !fine.isAfter(fineTurno)) {
                    return true;
                }
            }
            giorno = giorno.plusDays(1);
        }
        return false;
    }

    public boolean siSovrappone(Turno altro) {
        if (altro == null) {
            throw new IllegalArgumentException("Specificare il turno da confrontare.");
        }
        // Settimana di riferimento per confrontare anche domenica notte e lunedi'.
        LocalDate lunedi = LocalDate.of(2026, 1, 5);
        LocalDate giorno = lunedi.plusDays(giornoSettimana.getValue() - 1);
        LocalDateTime inizio = giorno.atTime(oraInizio);
        LocalDateTime fine = calcolaFine(giorno);
        for (int settimana = -1; settimana <= 1; settimana++) {
            LocalDate altroGiorno = lunedi.plusDays(altro.giornoSettimana.getValue() - 1)
                    .plusWeeks(settimana);
            LocalDateTime altroInizio = altroGiorno.atTime(altro.oraInizio);
            LocalDateTime altraFine = altro.calcolaFine(altroGiorno);
            if (inizio.isBefore(altraFine) && altroInizio.isBefore(fine)) {
                return true;
            }
        }
        return false;
    }

    private LocalDateTime calcolaFine(LocalDate giorno) {
        if (oraFine.isBefore(oraInizio)) {
            giorno = giorno.plusDays(1);
        }
        return giorno.atTime(oraFine);
    }
}
