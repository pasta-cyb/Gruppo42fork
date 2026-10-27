package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TestModel {
    private static int controlli = 0;

    public static void main(String[] args) {
        Ospedale ospedale = new Ospedale("Ospedale universitario");
        Reparto cardiologia = new Reparto("Cardiologia");
        Reparto neurologia = new Reparto("Neurologia");
        ospedale.aggiungiReparto(cardiologia);
        ospedale.aggiungiReparto(neurologia);
        Stanza stanza1 = new Stanza(1);
        Stanza stanza2 = new Stanza(1);
        ospedale.aggiungiStanza(cardiologia, stanza1);
        ospedale.aggiungiStanza(neurologia, stanza2);
        Letto c1 = new Letto("C1", stanza1);
        Letto c2 = new Letto("C2", stanza1);
        Letto n1 = new Letto("N1", stanza2);
        ospedale.aggiungiLetto(c1);
        ospedale.aggiungiLetto(c2);
        ospedale.aggiungiLetto(n1);
        verifica(stanza1.getReparto() == cardiologia && cardiologia.getStanze().contains(stanza1),
                "Collegamento stanza-reparto coerente");

        Amministratore admin = new Amministratore("admin", "passwordAdmin");
        Medico medico = new Medico("rossi", "passwordRossi", "M01");
        Medico collega = new Medico("bianchi", "passwordBianchi", "M02");
        ospedale.aggiungiAmministratore(admin);
        ospedale.aggiungiMedico(cardiologia, medico);
        ospedale.aggiungiMedico(cardiologia, collega);
        verifica(medico.getReparto() == cardiologia && cardiologia.getMedici().size() == 2,
                "Piu' medici nello stesso reparto");
        verifica(ospedale.autentica("rossi", "passwordRossi") == medico, "Login corretto");
        verifica(ospedale.autentica("rossi", "errata") == null, "Password errata");
        verifica(ospedale.autentica(null, null) == null, "Credenziali nulle");
        provaUnicita(ospedale, neurologia, stanza2, medico);

        Paziente anna = new Paziente("ANN001", "Anna", "Verdi");
        Paziente luca = new Paziente("LUC002", "Luca", "Neri");
        Paziente sara = new Paziente("SAR003", "Sara", "Viola");
        ospedale.aggiungiPaziente(anna);
        ospedale.aggiungiPaziente(luca);
        ospedale.aggiungiPaziente(sara);
        Ricovero primo = new Ricovero(anna, c1, data(11, 8, 0), data(14, 12, 0));
        Ricovero secondo = new Ricovero(luca, c1, data(14, 12, 0), data(15, 12, 0));
        ospedale.registraRicovero(primo);
        ospedale.registraRicovero(secondo);
        verifica(ospedale.getRicoveri().size() == 2, "Ricoveri consecutivi sullo stesso letto");
        verifica(ospedale.lettoOccupato(c1, data(11, 8, 0)), "Ingresso incluso nell'occupazione");
        verifica(!ospedale.lettoOccupato(c1, data(11, 7, 59)), "Letto libero prima dell'ingresso");
        verifica(ospedale.lettoOccupato(c1, data(14, 12, 0)), "Il secondo ricovero inizia alla dimissione");
        verifica(!ospedale.lettoOccupato(c1, data(15, 12, 0)), "Dimissione esclusa dall'occupazione");
        List<Letto> disponibili = ospedale.getLettiDisponibili(cardiologia, data(12, 10, 0));
        verifica(disponibili.size() == 1 && disponibili.contains(c2), "Ricerca letti liberi per reparto");
        provaRicoveri(ospedale, primo, secondo, sara, anna, c1, c2, n1);

        Turno lunedi = new Turno(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), medico);
        Turno notturno = new Turno(DayOfWeek.SUNDAY, LocalTime.of(22, 0), LocalTime.of(6, 0), medico);
        ospedale.aggiungiTurno(lunedi);
        ospedale.aggiungiTurno(notturno);
        ospedale.aggiungiTurno(new Turno(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), collega));
        verifica(lunedi.contiene(data(12, 8, 0), data(12, 18, 0)), "Prestazione coincidente con il turno");
        verifica(notturno.contiene(data(12, 0, 30), data(12, 1, 0)), "Turno notturno della domenica");
        verifica(!notturno.contiene(data(12, 5, 30), data(12, 6, 1)), "Fine oltre il turno rifiutata");
        verifica(notturno.siSovrappone(new Turno(DayOfWeek.MONDAY,
                LocalTime.of(5, 0), LocalTime.of(7, 0), medico)), "Sovrapposizione al confine di settimana");
        verifica(!notturno.siSovrappone(new Turno(DayOfWeek.MONDAY,
                LocalTime.of(6, 0), LocalTime.of(8, 0), medico)), "Turni adiacenti ammessi");
        boolean rifiutato = false;
        try {
            ospedale.aggiungiTurno(new Turno(DayOfWeek.MONDAY,
                    LocalTime.of(5, 0), LocalTime.of(7, 0), medico));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && medico.getTurni().size() == 2, "Inserimento turno sovrapposto annullato");

        Prestazione visita = new Prestazione("visita", data(12, 9, 0), data(12, 10, 0), medico, primo);
        Prestazione intervento = new Prestazione("intervento", data(12, 10, 0), data(12, 11, 0), medico, primo);
        Prestazione notte = new Prestazione("visita", data(11, 23, 30), data(12, 0, 30), medico, primo);
        ospedale.registraPrestazione(intervento);
        ospedale.registraPrestazione(visita);
        ospedale.registraPrestazione(notte);
        verifica(primo.getPrestazioni().size() == 3 && medico.getPrestazioni().size() == 3,
                "Prestazioni collegate a medico e ricovero");
        List<Prestazione> agenda = ospedale.getAgendaGiornaliera(medico, LocalDate.of(2026, 1, 12));
        verifica(agenda.size() == 3 && agenda.get(0) == notte && agenda.get(1) == visita,
                "Agenda ordinata e prestazione a cavallo della mezzanotte");
        verifica(ospedale.getAgendaSettimanale(medico, LocalDate.of(2026, 1, 14)).size() == 3,
                "Agenda settimanale include la prestazione iniziata domenica notte");
        provaPrestazioni(ospedale, medico, collega, primo, visita, intervento);

        ospedale.modificaEsito(medico, visita, "Paziente stabile");
        ospedale.modificaEsito(medico, visita, "Paziente in miglioramento");
        verifica(visita.getEsito().equals("Paziente in miglioramento"), "Inserimento e modifica esito");
        verifica(ospedale.getPazientiInDimissione(LocalDate.of(2026, 1, 14)).contains(anna),
                "Elenco pazienti in dimissione in una data");
        ospedale.dimettiPaziente(primo, data(13, 12, 0));
        verifica(primo.getDataOraFine().equals(data(13, 12, 0)), "Precedenza della dimissione effettiva");
        verifica(!ospedale.lettoOccupato(c1, data(13, 12, 0)), "Letto libero alla dimissione anticipata");
        verifica(ospedale.getPazientiInDimissione(LocalDate.of(2026, 1, 14)).isEmpty(),
                "Paziente gia' dimesso escluso dalle dimissioni previste");

        ospedale.getRicoveri().clear();
        cardiologia.getStanze().clear();
        medico.getTurni().clear();
        primo.getPrestazioni().clear();
        verifica(ospedale.getRicoveri().size() == 2 && cardiologia.getStanze().size() == 1
                && medico.getTurni().size() == 2 && primo.getPrestazioni().size() == 4,
                "Le copie delle liste non alterano il modello");
        System.out.println("\nTutti i " + controlli + " controlli sono riusciti.");
    }

    private static void provaUnicita(Ospedale ospedale, Reparto reparto, Stanza stanza, Medico medico) {
        boolean rifiutato = false;
        try {
            ospedale.aggiungiLetto(new Letto("c1", stanza));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && stanza.getLetti().size() == 1, "Codice letto univoco fra reparti");
        rifiutato = false;
        try {
            ospedale.aggiungiAmministratore(new Amministratore("rossi", "altraPassword"));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato, "Login duplicata rifiutata");
        rifiutato = false;
        try {
            ospedale.aggiungiAmministratore(new Amministratore("nuovo", "passwordRossi"));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato, "Password duplicata rifiutata come richiesto dalla traccia");
        rifiutato = false;
        try {
            ospedale.aggiungiMedico(reparto, medico);
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && reparto.getMedici().isEmpty(), "Medico non assegnabile a due reparti");
        rifiutato = false;
        try {
            ospedale.aggiungiStanza(reparto, new Stanza(1));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && reparto.getStanze().size() == 1, "Numero stanza univoco nel reparto");
        rifiutato = false;
        try {
            new Ospedale("Secondo").aggiungiReparto(reparto);
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato, "Reparto non condivisibile fra due ospedali");
    }

    private static void provaRicoveri(Ospedale ospedale, Ricovero primo, Ricovero secondo,
                                      Paziente sara, Paziente anna, Letto c1, Letto c2, Letto n1) {
        boolean rifiutato = false;
        try {
            ospedale.registraRicovero(new Ricovero(sara, c1, data(12, 8, 0), data(13, 12, 0)));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && ospedale.getRicoveri().size() == 2, "Ricovero sovrapposto rifiutato");
        rifiutato = false;
        try {
            ospedale.registraRicovero(new Ricovero(anna, n1, data(12, 8, 0), data(13, 12, 0)));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato, "Stesso paziente non ricoverabile in due letti contemporaneamente");
        rifiutato = false;
        try {
            ospedale.modificaRicovero(secondo, c1, data(13, 8, 0), data(15, 12, 0), null);
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && secondo.getDataOraInizio().equals(data(14, 12, 0)),
                "Modifica in conflitto annullata senza alterare il ricovero");
        rifiutato = false;
        try {
            ospedale.dimettiPaziente(primo, data(14, 13, 0));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && primo.getDataOraDimissioneEffettiva() == null,
                "Dimissione tardiva in conflitto annullata");
        ospedale.modificaRicovero(secondo, c2, data(14, 12, 0), data(15, 12, 0), null);
        verifica(secondo.getLetto() == c2, "Cambio letto valido");
        ospedale.modificaRicovero(secondo, c1, data(14, 12, 0), data(15, 12, 0), null);
        rifiutato = false;
        try {
            new Ricovero(sara, c1, data(12, 8, 0), data(12, 8, 0));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato, "Ricovero con durata nulla rifiutato");
        rifiutato = false;
        try {
            ospedale.registraRicovero(new Ricovero(sara, new Letto("ESTERNO", c1.getStanza()),
                    data(16, 8, 0), data(17, 8, 0)));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato, "Letto non registrato rifiutato");
    }

    private static void provaPrestazioni(Ospedale ospedale, Medico medico, Medico collega,
                                         Ricovero ricovero, Prestazione visita, Prestazione intervento) {
        boolean rifiutato = false;
        try {
            ospedale.registraPrestazione(new Prestazione("visita", data(12, 9, 30), data(12, 10, 30),
                    medico, ricovero));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && medico.getPrestazioni().size() == 3, "Prestazioni sovrapposte rifiutate");
        rifiutato = false;
        try {
            ospedale.registraPrestazione(new Prestazione("visita", data(12, 17, 30), data(12, 18, 30),
                    medico, ricovero));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato, "Prestazione in parte fuori turno rifiutata");
        rifiutato = false;
        try {
            ospedale.registraPrestazione(new Prestazione("visita", data(19, 9, 0), data(19, 10, 0),
                    medico, ricovero));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato, "Prestazione fuori ricovero rifiutata");
        rifiutato = false;
        try {
            ospedale.modificaOrarioPrestazione(intervento, data(12, 9, 30), data(12, 10, 30));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && intervento.getDataOraInizio().equals(data(12, 10, 0)),
                "Modifica prestazione in conflitto annullata");
        ospedale.modificaOrarioPrestazione(intervento, data(12, 11, 0), data(12, 12, 0));
        verifica(intervento.getDataOraInizio().equals(data(12, 11, 0)), "Modifica orario valida");
        rifiutato = false;
        try {
            ospedale.modificaRicovero(ricovero, ricovero.getLetto(), data(12, 9, 0), data(14, 12, 0), null);
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && ricovero.getDataOraInizio().equals(data(11, 8, 0)),
                "Modifica ricovero che escluderebbe una prestazione rifiutata");
        rifiutato = false;
        try {
            ospedale.dimettiPaziente(ricovero, data(12, 11, 30));
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && ricovero.getDataOraDimissioneEffettiva() == null,
                "Dimissione durante una prestazione rifiutata");
        rifiutato = false;
        try {
            ospedale.modificaEsito(collega, visita, "Esito non autorizzato");
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && visita.getEsito().equals("In attesa di esito"), "Esito di altro medico protetto");
        Prestazione parallela = new Prestazione("visita", data(12, 9, 0), data(12, 10, 0), collega, ricovero);
        ospedale.registraPrestazione(parallela);
        verifica(collega.getPrestazioni().size() == 1, "Prestazioni contemporanee di medici diversi ammesse");
        rifiutato = false;
        try {
            ospedale.registraPrestazione(visita);
        } catch (IllegalArgumentException e) {
            rifiutato = true;
        }
        verifica(rifiutato && medico.getPrestazioni().size() == 3, "Prestazione duplicata rifiutata");
    }

    private static LocalDateTime data(int giorno, int ora, int minuto) {
        return LocalDateTime.of(2026, 1, giorno, ora, minuto);
    }

    private static void verifica(boolean condizione, String descrizione) {
        if (!condizione) {
            throw new AssertionError("Controllo fallito: " + descrizione);
        }
        controlli++;
        System.out.println("OK " + controlli + " - " + descrizione);
    }
}
