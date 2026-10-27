package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ospedale {
    private String nome;
    private List<Reparto> reparti;
    private List<Utente> utenti;
    private List<Paziente> pazienti;
    private List<Ricovero> ricoveri;

    public Ospedale(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire il nome dell'ospedale.");
        }
        this.nome = nome.trim();
        this.reparti = new ArrayList<>();
        this.utenti = new ArrayList<>();
        this.pazienti = new ArrayList<>();
        this.ricoveri = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public List<Reparto> getReparti() {
        return new ArrayList<>(reparti);
    }

    public List<Utente> getUtenti() {
        return new ArrayList<>(utenti);
    }

    public List<Paziente> getPazienti() {
        return new ArrayList<>(pazienti);
    }

    public List<Ricovero> getRicoveri() {
        return new ArrayList<>(ricoveri);
    }

    public void aggiungiReparto(Reparto reparto) {
        if (reparto == null || reparto.getOspedale() != null) {
            throw new IllegalArgumentException("Il reparto e' nullo o gia' assegnato.");
        }
        for (Reparto altro : reparti) {
            if (altro.getNome().equalsIgnoreCase(reparto.getNome())) {
                throw new IllegalArgumentException("Esiste gia' un reparto con questo nome.");
            }
        }
        reparti.add(reparto);
        reparto.assegnaOspedale(this);
    }

    public void aggiungiStanza(Reparto reparto, Stanza stanza) {
        controllaReparto(reparto);
        if (stanza == null || stanza.getReparto() != null) {
            throw new IllegalArgumentException("La stanza e' nulla o gia' assegnata.");
        }
        for (Stanza altra : reparto.getStanze()) {
            if (altra.getNumeroStanza() == stanza.getNumeroStanza()) {
                throw new IllegalArgumentException("Numero di stanza gia' usato nel reparto.");
            }
        }
        reparto.aggiungiStanza(stanza);
        stanza.assegnaReparto(reparto);
    }

    public void aggiungiLetto(Letto letto) {
        if (letto == null) {
            throw new IllegalArgumentException("Specificare il letto.");
        }
        Stanza stanza = letto.getStanza();
        controllaReparto(stanza.getReparto());
        if (!stanza.getReparto().getStanze().contains(stanza)) {
            throw new IllegalArgumentException("Stanza non registrata.");
        }
        for (Reparto reparto : reparti) {
            for (Stanza altraStanza : reparto.getStanze()) {
                for (Letto altro : altraStanza.getLetti()) {
                    if (altro.getCodiceLetto().equalsIgnoreCase(letto.getCodiceLetto())) {
                        throw new IllegalArgumentException("Codice letto gia' usato nell'ospedale.");
                    }
                }
            }
        }
        stanza.aggiungiLetto(letto);
    }

    public void aggiungiAmministratore(Amministratore amministratore) {
        controllaNuovoUtente(amministratore);
        utenti.add(amministratore);
    }

    public void aggiungiMedico(Reparto reparto, Medico medico) {
        controllaReparto(reparto);
        controllaNuovoUtente(medico);
        if (medico.getReparto() != null) {
            throw new IllegalArgumentException("Il medico appartiene gia' a un reparto.");
        }
        for (Utente utente : utenti) {
            if (utente instanceof Medico) {
                Medico altro = (Medico) utente;
                if (altro.getMatricola().equalsIgnoreCase(medico.getMatricola())) {
                    throw new IllegalArgumentException("Matricola medico gia' presente.");
                }
            }
        }
        utenti.add(medico);
        reparto.aggiungiMedico(medico);
        medico.assegnaReparto(reparto);
    }

    public void aggiungiPaziente(Paziente paziente) {
        if (paziente == null) {
            throw new IllegalArgumentException("Specificare il paziente.");
        }
        for (Paziente altro : pazienti) {
            if (altro.getCodiceFiscale().equalsIgnoreCase(paziente.getCodiceFiscale())) {
                throw new IllegalArgumentException("Codice fiscale gia' presente.");
            }
        }
        pazienti.add(paziente);
    }

    public Utente autentica(String login, String password) {
        for (Utente utente : utenti) {
            if (utente.login(login, password)) {
                return utente;
            }
        }
        return null;
    }

    public void aggiungiTurno(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException("Specificare il turno.");
        }
        Medico medico = turno.getMedico();
        controllaMedico(medico);
        for (Turno altro : medico.getTurni()) {
            if (turno.siSovrappone(altro)) {
                throw new IllegalArgumentException("Il medico ha gia' un turno nella fascia indicata.");
            }
        }
        medico.aggiungiTurno(turno);
    }

    public void registraRicovero(Ricovero ricovero) {
        if (ricovero == null || ricoveri.contains(ricovero)) {
            throw new IllegalArgumentException("Ricovero nullo o gia' registrato.");
        }
        if (!pazienti.contains(ricovero.getPaziente())) {
            throw new IllegalArgumentException("Registrare prima il paziente nell'anagrafica.");
        }
        controllaDisponibilita(ricovero, ricovero.getLetto(), ricovero.getDataOraInizio(),
                ricovero.getDataOraFine());
        ricoveri.add(ricovero);
    }

    public void modificaRicovero(Ricovero ricovero, Letto letto, LocalDateTime inizio,
                                LocalDateTime prevista, LocalDateTime effettiva) {
        controllaRicovero(ricovero);
        if (inizio == null || prevista == null || !prevista.isAfter(inizio)) {
            throw new IllegalArgumentException("Intervallo previsto del ricovero non valido.");
        }
        LocalDateTime fine = prevista;
        if (effettiva != null) {
            if (!effettiva.isAfter(inizio)) {
                throw new IllegalArgumentException("Dimissione effettiva non valida.");
            }
            fine = effettiva;
        }
        controllaDisponibilita(ricovero, letto, inizio, fine);
        for (Prestazione prestazione : ricovero.getPrestazioni()) {
            if (prestazione.getDataOraInizio().isBefore(inizio)
                    || prestazione.getDataOraFine().isAfter(fine)) {
                throw new IllegalArgumentException("Una prestazione rimarrebbe fuori dal ricovero.");
            }
        }
        // I dati vengono modificati solo dopo aver superato tutti i controlli.
        ricovero.aggiorna(letto, inizio, prevista, effettiva);
    }

    public void dimettiPaziente(Ricovero ricovero, LocalDateTime effettiva) {
        controllaRicovero(ricovero);
        if (effettiva == null) {
            throw new IllegalArgumentException("Inserire la data di dimissione effettiva.");
        }
        modificaRicovero(ricovero, ricovero.getLetto(), ricovero.getDataOraInizio(),
                ricovero.getDataOraDimissionePrevista(), effettiva);
    }

    public void registraPrestazione(Prestazione prestazione) {
        if (prestazione == null) {
            throw new IllegalArgumentException("Specificare la prestazione.");
        }
        controllaMedico(prestazione.getMedico());
        controllaRicovero(prestazione.getRicovero());
        if (prestazione.getMedico().getPrestazioni().contains(prestazione)) {
            throw new IllegalArgumentException("Prestazione gia' registrata.");
        }
        controllaOrarioPrestazione(prestazione, prestazione.getDataOraInizio(),
                prestazione.getDataOraFine());
        prestazione.getMedico().aggiungiPrestazione(prestazione);
        prestazione.getRicovero().aggiungiPrestazione(prestazione);
    }

    public void modificaOrarioPrestazione(Prestazione prestazione, LocalDateTime inizio,
                                         LocalDateTime fine) {
        controllaPrestazione(prestazione);
        controllaOrarioPrestazione(prestazione, inizio, fine);
        prestazione.aggiornaOrario(inizio, fine);
    }

    public void modificaEsito(Medico medico, Prestazione prestazione, String esito) {
        controllaMedico(medico);
        controllaPrestazione(prestazione);
        if (prestazione.getMedico() != medico) {
            throw new IllegalArgumentException("La prestazione appartiene a un altro medico.");
        }
        if (prestazione.getDataOraFine().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La prestazione non e' ancora conclusa.");
        }
        if (esito == null || esito.trim().isEmpty()) {
            throw new IllegalArgumentException("Inserire un esito.");
        }
        prestazione.aggiornaEsito(esito.trim());
    }

    public List<Letto> getLettiDisponibili(Reparto reparto, LocalDateTime istante) {
        controllaReparto(reparto);
        if (istante == null) {
            throw new IllegalArgumentException("Specificare l'istante di ricerca.");
        }
        List<Letto> risultato = new ArrayList<>();
        for (Stanza stanza : reparto.getStanze()) {
            for (Letto letto : stanza.getLetti()) {
                if (!lettoOccupato(letto, istante)) {
                    risultato.add(letto);
                }
            }
        }
        return risultato;
    }

    public boolean lettoOccupato(Letto letto, LocalDateTime istante) {
        controllaLetto(letto);
        if (istante == null) {
            throw new IllegalArgumentException("Specificare l'istante di ricerca.");
        }
        for (Ricovero ricovero : ricoveri) {
            if (ricovero.getLetto() == letto && ricovero.eInCorso(istante)) {
                return true;
            }
        }
        return false;
    }

    public List<Ricovero> getDimissioniPreviste(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("Specificare la data di ricerca.");
        }
        List<Ricovero> risultato = new ArrayList<>();
        for (Ricovero ricovero : ricoveri) {
            if (ricovero.getDataOraDimissioneEffettiva() == null
                    && ricovero.getDataOraDimissionePrevista().toLocalDate().equals(data)) {
                risultato.add(ricovero);
            }
        }
        return risultato;
    }

    public List<Paziente> getPazientiInDimissione(LocalDate data) {
        List<Paziente> risultato = new ArrayList<>();
        for (Ricovero ricovero : getDimissioniPreviste(data)) {
            if (!risultato.contains(ricovero.getPaziente())) {
                risultato.add(ricovero.getPaziente());
            }
        }
        return risultato;
    }

    public List<Paziente> getPazientiInDimissioneOggi() {
        return getPazientiInDimissione(LocalDate.now());
    }

    public List<Prestazione> getAgendaGiornaliera(Medico medico, LocalDate giorno) {
        if (giorno == null) {
            throw new IllegalArgumentException("Specificare il giorno.");
        }
        return getAgenda(medico, giorno.atStartOfDay(), giorno.plusDays(1).atStartOfDay());
    }

    public List<Prestazione> getAgendaSettimanale(Medico medico, LocalDate giorno) {
        if (giorno == null) {
            throw new IllegalArgumentException("Specificare un giorno della settimana.");
        }
        LocalDate lunedi = giorno.minusDays(giorno.getDayOfWeek().getValue() - 1);
        return getAgenda(medico, lunedi.atStartOfDay(), lunedi.plusDays(7).atStartOfDay());
    }

    private List<Prestazione> getAgenda(Medico medico, LocalDateTime inizio, LocalDateTime fine) {
        controllaMedico(medico);
        List<Prestazione> risultato = new ArrayList<>();
        for (Prestazione prestazione : medico.getPrestazioni()) {
            if (prestazione.getDataOraInizio().isBefore(fine)
                    && inizio.isBefore(prestazione.getDataOraFine())) {
                risultato.add(prestazione);
            }
        }

        for (int i = 1; i < risultato.size(); i++) {
            Prestazione corrente = risultato.get(i);
            int j = i - 1;
            while (j >= 0 && risultato.get(j).getDataOraInizio().isAfter(corrente.getDataOraInizio())) {
                risultato.set(j + 1, risultato.get(j));
                j--;
            }
            risultato.set(j + 1, corrente);
        }
        return risultato;
    }

    private void controllaNuovoUtente(Utente nuovo) {
        if (nuovo == null) {
            throw new IllegalArgumentException("Specificare l'utente.");
        }
        for (Utente utente : utenti) {
            if (utente.getLogin().equals(nuovo.getLogin())) {
                throw new IllegalArgumentException("Login gia' presente.");
            }
            if (utente.haStessaPassword(nuovo)) {
                throw new IllegalArgumentException("Password gia' usata da un altro utente.");
            }
        }
    }

    private void controllaReparto(Reparto reparto) {
        if (reparto == null || !reparti.contains(reparto)) {
            throw new IllegalArgumentException("Reparto non registrato nell'ospedale.");
        }
    }

    private void controllaLetto(Letto letto) {
        if (letto == null) {
            throw new IllegalArgumentException("Specificare il letto.");
        }
        Stanza stanza = letto.getStanza();
        controllaReparto(stanza.getReparto());
        if (!stanza.getLetti().contains(letto)) {
            throw new IllegalArgumentException("Letto non registrato nell'ospedale.");
        }
    }

    private void controllaMedico(Medico medico) {
        if (medico == null || !utenti.contains(medico)) {
            throw new IllegalArgumentException("Medico non registrato nell'ospedale.");
        }
    }

    private void controllaRicovero(Ricovero ricovero) {
        if (ricovero == null || !ricoveri.contains(ricovero)) {
            throw new IllegalArgumentException("Ricovero non registrato nell'ospedale.");
        }
    }

    private void controllaPrestazione(Prestazione prestazione) {
        if (prestazione == null) {
            throw new IllegalArgumentException("Specificare la prestazione.");
        }
        controllaMedico(prestazione.getMedico());
        if (!prestazione.getMedico().getPrestazioni().contains(prestazione)) {
            throw new IllegalArgumentException("Prestazione non registrata nell'ospedale.");
        }
    }

    private void controllaDisponibilita(Ricovero daControllare, Letto letto,
                                       LocalDateTime inizio, LocalDateTime fine) {
        controllaLetto(letto);
        for (Ricovero altro : ricoveri) {
            if (altro != daControllare && inizio.isBefore(altro.getDataOraFine())
                    && altro.getDataOraInizio().isBefore(fine)) {
                if (altro.getLetto() == letto) {
                    throw new IllegalArgumentException("Il letto e' gia' occupato nel periodo scelto.");
                }
                if (altro.getPaziente() == daControllare.getPaziente()) {
                    throw new IllegalArgumentException("Il paziente ha gia' un ricovero nel periodo scelto.");
                }
            }
        }
    }

    private void controllaOrarioPrestazione(Prestazione prestazione, LocalDateTime inizio,
                                          LocalDateTime fine) {
        if (inizio == null || fine == null || !fine.isAfter(inizio)) {
            throw new IllegalArgumentException("Intervallo della prestazione non valido.");
        }
        Ricovero ricovero = prestazione.getRicovero();
        if (inizio.isBefore(ricovero.getDataOraInizio()) || fine.isAfter(ricovero.getDataOraFine())) {
            throw new IllegalArgumentException("La prestazione deve essere interna al ricovero.");
        }
        boolean nelTurno = false;
        for (Turno turno : prestazione.getMedico().getTurni()) {
            if (turno.contiene(inizio, fine)) {
                nelTurno = true;
                break;
            }
        }
        if (!nelTurno) {
            throw new IllegalArgumentException("La prestazione non e' compresa in un turno del medico.");
        }
        for (Prestazione altra : prestazione.getMedico().getPrestazioni()) {
            if (altra != prestazione && inizio.isBefore(altra.getDataOraFine())
                    && altra.getDataOraInizio().isBefore(fine)) {
                throw new IllegalArgumentException("Il medico ha gia' una prestazione nella fascia scelta.");
            }
        }
    }
}
