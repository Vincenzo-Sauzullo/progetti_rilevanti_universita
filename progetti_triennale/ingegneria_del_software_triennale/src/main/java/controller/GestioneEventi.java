package controller;

import controller.email.ServiziEmail;
import controller.email.ServizioEmail;
import controller.exceptions.EventoConcomitanteException;
import controller.exceptions.ParameterException;
import controller.parameters.*;
import database.*;
import entity.*;
import jakarta.mail.MessagingException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class GestioneEventi {
    private GestioneEventi() {}
    private static final GestioneEventi instance = new GestioneEventi();

    private static final Set<Musicista> musicisti = new HashSet<>();
    private static final Set<Evento> eventi = EventoDAO.readAllEventi();

    private static final ServizioEmail servizioConvocazioni = ServiziEmail.getServizioConvocazioni();
    private static final ServizioEmail servizioReport = ServiziEmail.getServizioReport();

    static {
        List<Strumentista> strumentisti = StrumentistaDAO.getAllStrumentisti();
        List<Cantante> cantanti = CantanteDAO.getAllCantanti();

        musicisti.addAll(strumentisti);
        musicisti.addAll(cantanti);
    }

    public static GestioneEventi getInstance() {
        return GestioneEventi.instance;
    }

    public Musicista InserisciMusicista(MusicistaParameter musicistaParameter) throws ParameterException, SQLException {
        musicistaParameter.validate();

        Musicista musicista = switch(musicistaParameter) {
            case CantanteParameter cantanteParameter -> {
                Cantante cantante = new Cantante(cantanteParameter);
                CantanteDAO.createCantante(cantante);
                yield cantante;
            }
            case StrumentistaParameter strumentistaParameter -> {
                Strumentista strumentista = new Strumentista(strumentistaParameter);
                StrumentistaDAO.createStrumentista(strumentista);
                yield strumentista;
            }
            default -> throw new ParameterException("Tipo di musicista non supportato");
        };

        musicisti.add(musicista);

        return musicista;
    }

    public boolean ModificaMusicista(Musicista musicista) {
        if(musicista instanceof Cantante cantante) {
            return CantanteDAO.updateCantante(cantante);
        } else if(musicista instanceof Strumentista strumentista) {
            return StrumentistaDAO.updateStrumentista(strumentista);
        }

        return false;
    }

    public boolean RimuoviMusicista(Musicista musicista) {
        boolean ok = false;

        if(musicista instanceof Cantante cantante) {
            ok = CantanteDAO.deleteCantante(cantante);
        } else if(musicista instanceof Strumentista strumentista) {
            ok = StrumentistaDAO.deleteStrumentista(strumentista);
        }

        if(ok)
            return musicisti.remove(musicista);

        return false;
    }

    public Set<Musicista> CercaMusicisti(QueryParameters queryParameters) throws ParameterException {
        queryParameters.validate();

        Set<Musicista> results = new HashSet<>();

        String query = queryParameters.getQuery();
        List<GenereMusicale> generiMusicali = queryParameters.getGeneriMusicali();
        List<RegistroVocale> registriVocali = queryParameters.getRegistriVocali() == null ? null :
                new ArrayList<>(queryParameters.getRegistriVocali()); // Necessario.
        List<Strumento> strumenti = queryParameters.getStrumenti();
        boolean cantanti = queryParameters.cantanti() && (strumenti == null || strumenti.isEmpty());
        boolean strumentisti = queryParameters.strumentisti() &&
                (generiMusicali == null || generiMusicali.isEmpty()) &&
                (registriVocali == null || registriVocali.isEmpty());
        LocalDate disponibileDa = queryParameters.getDisponibileDa();
        LocalDate disponibileA = queryParameters.getDisponibileA();

        String[] tokens = query.toLowerCase().split("\\s+|,");

        for(Musicista musicista : musicisti) {
            // Fast path - si ignorano subito i musicisti che non corrispondono ai requisiti.

            switch(musicista) {
                case Cantante cantante -> {
                    // Se filtro fuori i cantanti, li escludo dalla ricerca.
                    if(!cantanti) continue;
                    // Verifico che il genere del cantante corrisponda a uno dei generi musicali specificati nella query.
                    if(generiMusicali != null && !generiMusicali.isEmpty() && !generiMusicali.contains(cantante.getGenereMusicale())) continue;
                    // Verifico che il registro del cantante corrisponda a uno dei registri vocali specificati nella query.
                    if(registriVocali != null && !registriVocali.isEmpty() && !registriVocali.contains(cantante.getRegistroVocale())) continue;
                }
                case Strumentista strumentista -> {
                    // Se filtro fuori gli strumentisti, li escludo dalla ricerca.
                    if(!strumentisti) continue;
                    // Verifico che lo strumentista sappia suonare *tutti* gli strumenti specificati nella query.
                    //noinspection SlowListContainsAll
                    if(strumenti != null && !strumentista.getStrumenti().containsAll(strumenti)) continue;
                }
                case null, default -> {
                    continue;
                }
            }

            // Si escludono i musicisti i cui giorni di disponibilità non corrispondono ai criteri di ricerca.
            if((disponibileDa != null && disponibileA != null) && musicista.getDisponibilita().stream().noneMatch(e ->
                (e.isEqual(disponibileDa) || e.isAfter(disponibileDa)) && (e.isEqual(disponibileA) || e.isBefore(disponibileA)))
            ) continue;

            for(String token : tokens) {
                // Si inseriscono le voci corrispondenti.
                if(musicista.getNome().toLowerCase().contains(token) ||
                        musicista.getCognome().toLowerCase().contains(token) ||
                        musicista.getEmail().toLowerCase().contains(token) ||
                        musicista.getTelefono().toLowerCase().contains(token) ||
                        String.valueOf(musicista.getTelefono()).contains(token) ||
                        String.valueOf(musicista.getCachetPerSerata()).contains(token))
                    results.add(musicista);
            }
        }

        return results;
    }

    public Musicista Accedi(String email, String password) {
        Musicista musicista = musicisti.stream().filter(m -> m.getEmail().equalsIgnoreCase(email))
                                       .findFirst().orElse(null);

        if(musicista == null) return null;

        return musicista.getPassword().equals(password) ? musicista : null;
    }

    public List<Partecipazione> VisualizzaConvocazioni(Musicista musicista) {
        return PartecipazioneDAO.getPartecipazioni(musicista);
    }

    public boolean ModificaDisponibilita(Musicista musicista) {
        return DisponibilitaDAO.updateDisponibilita(musicista);
    }

    public List<Evento> cercaEventiPassati() {
        return eventi.stream().toList();
    }

    public List<Evento> cercaEventiPassati(LocalDate data) {
        List<Evento> eventiPassati = new ArrayList<>();
        for(Evento evento : eventi) {
            if(evento.getData().equals(data)) {
                eventiPassati.add(evento);
            }
        }

        return eventiPassati;
    }

    public List<Evento> cercaEventiPassati(String luogo) {
        List<Evento> eventiPassati = new ArrayList<>();
        for(Evento evento : eventi) {
            if(evento.getLuogo().equalsIgnoreCase(luogo)) {
                eventiPassati.add(evento);
            }
        }

        return eventiPassati;
    }

    public List<Evento> cercaEventiPassati(TipologiaEvento tipologia) {
        List<Evento> eventiPassati = new ArrayList<>();
        for(Evento evento : eventi) {
            if(evento.getTipologia().equals(tipologia)) {
                eventiPassati.add(evento);
            }
        }

        return eventiPassati;
    }

    public List<Evento> cercaEventiPassati(int costoTotale) {
        List<Evento> eventiPassati = new ArrayList<>();
        for(Evento evento : eventi) {
            if(evento.getCostoTotale() == costoTotale) {
                eventiPassati.add(evento);
            }
        }

        return eventiPassati;
    }

    public List<Evento> cercaEventiPassati(List<Musicista> musicisti) {
        Set<Evento> eventiPassati = new HashSet<>();
        for(Evento evento : eventi) {
            if(evento.getPartecipanti().stream().map(Partecipazione::getMusicista).collect(Collectors.toUnmodifiableSet()).containsAll(musicisti)) {
                eventiPassati.add(evento);
            }
        }

        return eventiPassati.stream().toList();
    }

    private static String caricaRisorsa(String resourceName) {
        InputStream input = GestioneEventi.class.getClassLoader().getResourceAsStream(resourceName);
        if (input == null) {
            throw new RuntimeException("Risorsa non trovata: " + resourceName);
        }

        try (Scanner scanner = new Scanner(input, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
    }

    private void inviaEmailEvento(Evento evento, boolean confermaEvento) {
        if(servizioReport == null || servizioConvocazioni == null) return;

        String oggetto, template;
        if (confermaEvento) {
            template = caricaRisorsa("templates/conferma_evento.html");
            oggetto = "Evento confermato";
        } else {
            template = caricaRisorsa("templates/convocazione.html");
            oggetto = "Nuova convocazione";
        }

        for(Partecipazione partecipazione : evento.getPartecipanti()) {
            Musicista musicista = partecipazione.getMusicista();
            template = template.replace("{{ nome }}", musicista.getNome());
            template = template.replace("{{ cognome }}", musicista.getCognome());
            template = template.replace("{{ tipologia }}", evento.getTipologia().toString());
            template = template.replace("{{ data }}", evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            String finalHtml = template.replace("{{ luogo }}", evento.getLuogo());

            try {
                servizioConvocazioni.inviaEmailHtml(musicista.getEmail(), oggetto, finalHtml);
            } catch (MessagingException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }

    public Evento PianificaEvento(EventoParameters eventoParameters) throws EventoConcomitanteException, ParameterException {
        eventoParameters.validate();

        Evento evento = new Evento(eventoParameters);

        if(eventi.stream().anyMatch(e -> e.getLuogo().equals(evento.getLuogo()) && e.getData().equals(evento.getData()))) {
            throw new EventoConcomitanteException(evento.getData(), evento.getLuogo());
        }

        EventoDAO.createEvento(evento);

        eventi.add(evento);
        inviaEmailEvento(evento, false);
        return evento;
    }

    public void ModificaEvento(Evento evento, EventoParameters eventoParameters) throws EventoConcomitanteException, ParameterException {
        if(eventi.stream().anyMatch(e -> e.getData().equals(eventoParameters.getData()) && e.getLuogo().equals(eventoParameters.getLuogo())))
            throw new EventoConcomitanteException(evento.getData(), evento.getLuogo());

        eventoParameters.validate();

        Evento aggiornato = new Evento(eventoParameters);
        aggiornato.setId(evento.getId());

        if(!EventoDAO.updateEvento(aggiornato))
            return;

        evento.setData(aggiornato.getData());
        evento.setLuogo(aggiornato.getLuogo());
        evento.setCostoTotale(aggiornato.getCostoTotale());
        evento.setTipologia(aggiornato.getTipologia());
        evento.setStato(aggiornato.getStato());
        evento.setPartecipanti(aggiornato.getPartecipanti());
    }

    public Evento CaricaEvento(long id) {
        return EventoDAO.readEvento(id);
    }

    public Musicista CaricaMusicista(long id) {
        return MusicistaDAO.readMusicista(id);
    }

    public void AccettaConvocazione(Partecipazione partecipazione) {
        partecipazione.setConfermato(true);

        Evento evento = partecipazione.getEvento();

        // Rimuove la disponibilità di partecipare all'evento da parte del musicista
        partecipazione.getMusicista().getDisponibilita().remove(evento.getData());

        DisponibilitaDAO.updateDisponibilita(partecipazione.getMusicista());
        PartecipazioneDAO.updatePartecipazione(partecipazione);

        // Controllo effettuato per verificare se, tra gli invitati all'evento, tutti hanno dato la propria disponibilità
        boolean tuttiHannoAccettato = evento.getPartecipanti().stream().allMatch(Partecipazione::isConfermato);

        if(tuttiHannoAccettato)
            ConfermaEvento(evento);
    }

    public void ConfermaEvento(Evento evento) {
        evento.setStato(StatoEvento.CONFERMATO);

        EventoDAO.updateEvento(evento);

        inviaEmailEvento(evento, true);
    }

    private Report GeneraReport() {
        YearMonth meseReport = YearMonth.now().minusMonths(1);

        List<Evento> eventiMesePrecedente = eventi.stream().filter(
            evento -> meseReport.equals(YearMonth.from(evento.getData()))
        ).toList();

        Map<Musicista, Integer> costoMusicisti = eventi.stream()
            .map(Evento::getPartecipanti)
            .flatMap(Collection::stream)
            .collect(
                Collectors.groupingBy(
                    Partecipazione::getMusicista,
                    Collectors.mapping(
                        Function.identity(),
                        Collectors.summingInt(Partecipazione::getCosto)
                    )
                )
            );

        return new Report(
            eventiMesePrecedente,
            costoMusicisti
        );
    }

    public void InviaReport() throws MessagingException {
        Report report = GeneraReport();

        if(report.getEventi().isEmpty())
            return;

        String template = caricaRisorsa("templates/report.html");

        StringBuilder sb = new StringBuilder();
        for (Evento evento : report.getEventi()) {
            sb.append("<tr>")
            .append("<td>").append(evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td>")
            .append("<td>").append(evento.getLuogo()).append("</td>")
            .append("<td>").append(evento.getTipologia()).append("</td>")
            .append("</tr>\n");
        }

        template = template.replace("{{ eventiRows }}", sb.toString());

        for (Map.Entry<Musicista, Integer> entry : report.getCompensiMusicisti().entrySet()) {
            Musicista m = entry.getKey();
            int compenso = entry.getValue();
            sb.append("<tr>")
                    .append("<td>").append(m.getNome()).append(" ").append(m.getCognome()).append("</td>")
                    .append("<td>").append(compenso).append("</td>")
                    .append("</tr>\n");
        }

        String finalHtml = template.replace("{{ compensiRows }}", sb.toString());

        if(servizioReport == null || servizioConvocazioni == null)
            return;

        servizioReport.inviaEmailHtml("agente@eventi-musicali.com", "Report mensile", finalHtml);
    }
}
