package controller;

import controller.exceptions.EventoConcomitanteException;
import controller.exceptions.ParameterException;
import controller.parameters.*;
import controller.email.ServiziEmail;
import database.DatabaseManager;
import database.EventoDAO;
import entity.*;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class GestioneEventiTest {
    static DatabaseManager manager;
    static Connection connection;
    static GestioneEventi gestioneEventi;

    static final Set<Musicista> musicisti = new HashSet<>();

    @BeforeAll
    static void initAll() throws SQLException, ParameterException {
        GestioneEventiTest.manager = DatabaseManager.getInstance("jdbc:sqlite::memory:");
        DatabaseManager.setDefaultInstance(manager);
        GestioneEventiTest.connection = manager.getConnection();

        gestioneEventi = GestioneEventi.getInstance();

        List<MusicistaParameter> parameters = new ArrayList<>();

        List<LocalDate> disponibilitaEstive = List.of(
                new LocalDate[]{
                        LocalDate.of(2025, 7, 1),
                        LocalDate.of(2025, 7, 2),
                        LocalDate.of(2025, 7, 3),
                        LocalDate.of(2025, 7, 4),
                        LocalDate.of(2025, 7, 5),
                        LocalDate.of(2025, 7, 6),
                        LocalDate.of(2025, 8, 1),
                        LocalDate.of(2025, 8, 15),
                        LocalDate.of(2025, 8, 31),
                }
        );

        List<LocalDate> disponibilitaAutunnali = List.of(
                new LocalDate[]{
                        LocalDate.of(2025, 9, 4),
                        LocalDate.of(2025, 9, 5),
                        LocalDate.of(2025, 9, 6),
                        LocalDate.of(2025, 9, 7),
                        LocalDate.of(2025, 9, 8),
                        LocalDate.of(2025, 9, 9),
                        LocalDate.of(2025, 10, 3),
                        LocalDate.of(2025, 10, 4),
                        LocalDate.of(2025, 11, 5),
                }
        );

        parameters.add(new CantanteParameter(
                "Nome1", "Cognome1", "email1@email.com", "psw1", "+393931", 42, disponibilitaEstive, GenereMusicale.CLASSICA, RegistroVocale.SOPRANO
        ));

        parameters.add(new CantanteParameter(
                "Nome2", "Cognome2", "email2@email.com", "psw2", "+393932", 15, disponibilitaAutunnali, GenereMusicale.POP, null
        ));

        parameters.add(new CantanteParameter(
                "Nome3", "Cognome3", "email3@email.com", "psw3", "+393933", 8, disponibilitaEstive, GenereMusicale.JAZZ, null
        ));

        parameters.add(new CantanteParameter(
                "Nome4", "Cognome4", "email4@eventi-musicali.com", "psw4", "+393934", 13, disponibilitaAutunnali, GenereMusicale.LEGGERA, null
        ));

        parameters.add(new CantanteParameter(
                "Nome5", "Cognome5", "email5@eventi-musicali.com", "psw5", "+393935", 9, disponibilitaEstive, GenereMusicale.CLASSICA, RegistroVocale.BARITONO
        ));

        parameters.add(new StrumentistaParameter(
                "Nome6", "Cognome6", "email6@eventi-musicali.com", "psw6", "+393936", 11, disponibilitaEstive,
                List.of(Strumento.VIOLONCELLO, Strumento.PIANOFORTE, Strumento.OBOE, Strumento.TASTIERA)
        ));

        parameters.add(new StrumentistaParameter(
                "Nome7", "Cognome7", "email7@email.com", "psw7", "+393937", 77, disponibilitaAutunnali,
                List.of(Strumento.OBOE, Strumento.BASSO, Strumento.BATTERIA)
        ));

        parameters.add(new StrumentistaParameter(
                "Nome8", "Cognome8", "email8@eventi-musicali.com", "psw8", "+393938", 88, disponibilitaEstive,
                List.of(Strumento.CHITARRA, Strumento.PIANOFORTE, Strumento.BATTERIA)
        ));

        parameters.add(new StrumentistaParameter(
                "Nome9", "Cognome9", "email9@email.com", "psw9", "+393939", 99, disponibilitaAutunnali,
                List.of(Strumento.VIOLA, Strumento.FLAUTO, Strumento.OBOE, Strumento.VIOLINO)
        ));

        parameters.add(new StrumentistaParameter(
                "Nome10", "Cognome10", "email10@email.com", "psw10", "+3939310", 101, disponibilitaEstive,
                List.of(Strumento.TASTIERA, Strumento.BATTERIA, Strumento.FLAUTO)
        ));

        for (MusicistaParameter parameter : parameters)
            musicisti.add(gestioneEventi.InserisciMusicista(parameter));

        boolean email = System.getProperty("no-email") != null;

        if(email)
            inizializzaServiziEmail();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static void inizializzaServiziEmail() {
        ServiziEmail.inizializza(
                "smtps.aruba.it",
                465,
                "convocazioni@eventi-musicali.com",
                "vonfah-Demcu1-nodfow",
                true
        );
        ServiziEmail.inizializza(
                "smtps.aruba.it",
                465,
                "report@eventi-musicali.com",
                "vYdqux-rumzif-5qoxgo",
                true
        );
    }

    /**
     * Si richiede di non mostrare né cantanti né strumentisti.
     */
    @Test
    void testCercaMusicisti1() {
        QueryParameters query = new QueryParameters();
        query.setCantanti(false);
        query.setStrumentisti(false);

        try {
            gestioneEventi.CercaMusicisti(query);

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        }
    }

    /**
     * Si specifica un intervallo di disponibilità non valido, in cui la data di inizio non precede e non equivale a quella di fine
     */
    @Test
    void testCercaMusicisti2() {
        QueryParameters query = new QueryParameters();
        query.setDisponibileDa(LocalDate.of(2030, 12, 31));
        query.setDisponibileA(LocalDate.of(2030, 1, 1));

        try {
            gestioneEventi.CercaMusicisti(query);

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        }
    }

    /**
     * Si specifica un intervallo di disponibilità non valido, in cui la data di inizio non precede e non equivale a quella di fine
     */
    @Test
    void testCercaMusicisti3() {
        QueryParameters query = new QueryParameters();
        query.setGeneriMusicali(List.of(GenereMusicale.CLASSICA));

        try {
            Set<Musicista> trovati = gestioneEventi.CercaMusicisti(query);

            assertTrue(trovati.containsAll(
                    musicisti.stream().filter(m ->
                            m instanceof Cantante &&
                                    ((Cantante) m).getGenereMusicale() == GenereMusicale.CLASSICA
                    ).collect(Collectors.toSet())
            ));
        } catch (ParameterException e) {
            fail();
        }
    }

    /**
     * Si cercano solo musicisti il cui genere musicale è musica “Pop” con registro vocale “Soprano”
     */
    @Test
    void testCercaMusicisti4() {
        QueryParameters query = new QueryParameters();
        query.setGeneriMusicali(List.of(GenereMusicale.POP));
        query.setRegistriVocali(List.of(RegistroVocale.SOPRANO));

        try {
            assertTrue(gestioneEventi.CercaMusicisti(query).isEmpty());
        } catch (ParameterException e) {
            fail();
        }
    }

    /**
     * Si cercano solo strumentisti con strumenti violoncello, piano
     */
    @Test
    void testCercaMusicisti5() {
        List<Strumento> strumenti = List.of(Strumento.VIOLONCELLO, Strumento.PIANOFORTE);
        QueryParameters query = new QueryParameters();
        query.setStrumenti(strumenti);

        try {
            Set<Musicista> trovati = gestioneEventi.CercaMusicisti(query);

            assertTrue(trovati.containsAll(
                    musicisti.stream().filter(m ->
                            m instanceof Strumentista &&
                                    ((Strumentista) m).getStrumenti().containsAll(strumenti)
                    ).collect(Collectors.toSet())
            ));
        } catch (ParameterException e) {
            fail();
        }
    }

    /**
     * Si richiede di visualizzare tutti gli strumentisti.
     */
    @Test
    void testCercaMusicisti6() {
        QueryParameters query = new QueryParameters();
        query.setCantanti(false);

        try {
            Set<Musicista> trovati = gestioneEventi.CercaMusicisti(query);

            assertTrue(trovati.containsAll(
                    musicisti.stream().filter(m -> m instanceof Strumentista).collect(Collectors.toSet())
            ));
        } catch (ParameterException e) {
            fail();
        }
    }

    /**
     * Si richiede di visualizzare tutti i musicisti disponibili nel periodo estivo che corrispondono alla query di
     * ricerca “@eventi-musicali.com”
     */
    @Test
    void testCercaMusicisti7() {
        QueryParameters query = new QueryParameters("@eventi-musicali.com");
        query.setDisponibileDa(LocalDate.of(2025, 6, 1));
        query.setDisponibileA(LocalDate.of(2025, 8, 31));

        try {
            Set<Musicista> trovati = gestioneEventi.CercaMusicisti(query);

            assertTrue(trovati.containsAll(
                    musicisti.stream().filter(m ->
                            m.getEmail().toLowerCase().contains("@eventi-musicali.com") &&
                                    m.getDisponibilita().stream().anyMatch(p ->
                                            (p.isEqual(query.getDisponibileDa()) || p.isAfter(query.getDisponibileDa())) &&
                                                    (p.isEqual(query.getDisponibileA()) || p.isBefore(query.getDisponibileA()))
                                    )
                    ).collect(Collectors.toSet())
            ));
        } catch (ParameterException e) {
            fail();
        }
    }

    /**
     * Tutti input validi
     */
    @Test
    void testPianificaEvento1() {
        List<Strumentista> strumentisti5 = musicisti
                .stream()
                .filter(m -> m instanceof Strumentista)
                .map(m -> (Strumentista) m).limit(5).toList();

        Cantante cantante = musicisti
                .stream()
                .filter(m -> m instanceof Cantante)
                .map(m -> (Cantante) m)
                .findAny().orElse(null);

        if (cantante == null || strumentisti5.size() != 5)
            fail();

        Set<Musicista> partecipanti = new HashSet<>(strumentisti5);
        partecipanti.add(cantante);

        EventoParameters parameters = new EventoParameters(
                LocalDate.of(2025, 9, 25),
                "Formia",
                TipologiaEvento.CONCERTO,
                partecipanti
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            assertTrue(true);
        } catch (EventoConcomitanteException | ParameterException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    /**
     * Data passata
     */
    @Test
    void testPianificaEvento2() {
        List<Strumentista> strumentisti5 = musicisti
                .stream()
                .filter(m -> m instanceof Strumentista)
                .map(m -> (Strumentista) m).limit(5).toList();

        Cantante cantante = musicisti
                .stream()
                .filter(m -> m instanceof Cantante)
                .map(m -> (Cantante) m)
                .findAny().orElse(null);

        if (cantante == null || strumentisti5.size() != 5)
            fail();

        Set<Musicista> partecipanti = new HashSet<>(strumentisti5);
        partecipanti.add(cantante);

        EventoParameters parameters = new EventoParameters(
                LocalDate.of(1989, 9, 25),
                "Formia",
                TipologiaEvento.CONCERTO,
                partecipanti
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (EventoConcomitanteException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    /**
     * Data corrente
     */
    @Test
    void testPianificaEvento3() {
        List<Strumentista> strumentisti5 = musicisti
                .stream()
                .filter(m -> m instanceof Strumentista)
                .map(m -> (Strumentista) m).limit(5).toList();

        Cantante cantante = musicisti
                .stream()
                .filter(m -> m instanceof Cantante)
                .map(m -> (Cantante) m)
                .findAny().orElse(null);

        if (cantante == null || strumentisti5.size() != 5)
            fail();

        Set<Musicista> partecipanti = new HashSet<>(strumentisti5);
        partecipanti.add(cantante);

        EventoParameters parameters = new EventoParameters(
                LocalDate.now(),
                "Napoli",
                TipologiaEvento.CONCERTO,
                partecipanti
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            assertTrue(true);
        } catch (ParameterException | EventoConcomitanteException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    /**
     * Stringa nulla per il luogo
     */
    @Test
    void testPianificaEvento4() {
        List<Strumentista> strumentisti5 = musicisti
                .stream()
                .filter(m -> m instanceof Strumentista)
                .map(m -> (Strumentista) m).limit(5).toList();

        Cantante cantante = musicisti
                .stream()
                .filter(m -> m instanceof Cantante)
                .map(m -> (Cantante) m)
                .findAny().orElse(null);

        if (cantante == null || strumentisti5.size() != 5)
            fail();

        Set<Musicista> partecipanti = new HashSet<>(strumentisti5);
        partecipanti.add(cantante);

        EventoParameters parameters = new EventoParameters(
                LocalDate.of(2025, 8, 3),
                "",
                TipologiaEvento.CONCERTO,
                partecipanti
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (EventoConcomitanteException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    /**
     * Tipologia evento: Strumentale
     */
    @Test
    void testPianificaEvento5() {
        List<Strumentista> strumentisti5 = musicisti
                .stream()
                .filter(m -> m instanceof Strumentista)
                .map(m -> (Strumentista) m).limit(5).toList();

        Cantante cantante = musicisti
                .stream()
                .filter(m -> m instanceof Cantante)
                .map(m -> (Cantante) m)
                .findAny().orElse(null);

        if (cantante == null || strumentisti5.size() != 5)
            fail();

        Set<Musicista> partecipanti = new HashSet<>(strumentisti5);
        partecipanti.add(cantante);

        EventoParameters parameters = new EventoParameters(
                LocalDate.of(2025, 8, 7),
                "Scauri",
                TipologiaEvento.STRUMENTALE,
                partecipanti
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (EventoConcomitanteException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    /**
     * Tipologia evento: Esibizione
     */
    @Test
    void testPianificaEvento6() {
        List<Strumentista> strumentisti5 = musicisti
                .stream()
                .filter(m -> m instanceof Strumentista)
                .map(m -> (Strumentista) m).limit(5).toList();

        Cantante cantante = musicisti
                .stream()
                .filter(m -> m instanceof Cantante)
                .map(m -> (Cantante) m)
                .findAny().orElse(null);

        if (cantante == null || strumentisti5.size() != 5)
            fail();

        Set<Musicista> partecipanti = new HashSet<>(strumentisti5);
        partecipanti.add(cantante);

        EventoParameters parameters = new EventoParameters(
                LocalDate.of(2025, 9, 10),
                "Mondragone",
                TipologiaEvento.STRUMENTALE,
                partecipanti
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (EventoConcomitanteException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    /**
     * Tipologia evento: Concerto
     */
    @Test
    void testPianificaEvento7() {
        List<Strumentista> strumentisti3 = musicisti
                .stream()
                .filter(m -> m instanceof Strumentista)
                .map(m -> (Strumentista) m).limit(3).toList();

        List<Cantante> cantanti2 = musicisti
                .stream()
                .filter(m -> m instanceof Cantante)
                .map(m -> (Cantante) m).limit(2).toList();

        if (cantanti2.size() != 2 || strumentisti3.size() != 3)
            fail();

        Set<Musicista> partecipanti = new HashSet<>(strumentisti3);
        partecipanti.addAll(cantanti2);

        EventoParameters parameters = new EventoParameters(
                LocalDate.of(2025, 8, 7),
                "Formia",
                TipologiaEvento.CONCERTO,
                partecipanti
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            assertTrue(true);
        } catch (ParameterException | EventoConcomitanteException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    /**
     * Lista partecipanti con solo cantanti
     */
    @Test
    void testPianificaEvento8() {
        List<Cantante> cantanti2 = musicisti
                .stream()
                .filter(m -> m instanceof Cantante)
                .map(m -> (Cantante) m).limit(2).toList();

        if (cantanti2.size() != 2)
            fail();

        Set<Musicista> partecipanti = new HashSet<>(cantanti2);

        EventoParameters parameters = new EventoParameters(
                LocalDate.of(2025, 8, 14),
                "Gaeta",
                TipologiaEvento.ESIBIZIONE,
                partecipanti
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (EventoConcomitanteException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    /**
     * Lista partecipanti vuota
     */
    @Test
    void testPianificaEvento9() {
        EventoParameters parameters = new EventoParameters(
                LocalDate.of(2025, 8, 12),
                "Sessa Aurunca",
                TipologiaEvento.STRUMENTALE,
                Set.of()
        );

        Evento evento = null;
        try {
            evento = gestioneEventi.PianificaEvento(parameters);

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (EventoConcomitanteException e) {
            fail();
        } finally {
            assertTrue(this._deleteEvento(evento));
        }
    }

    private boolean _deleteEvento(final Evento evento) {
        if(evento == null)
            return true;

        try {
            EventoDAO.deleteEvento(evento);

            /* Java Reflection per rimuovere un evento al set privato eventi;
               serve per bypassare le verifiche del Controller */
            Field eventiField = gestioneEventi.getClass().getDeclaredField("eventi");
            eventiField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Set<Evento> eventi = (Set<Evento>) eventiField.get(gestioneEventi);
            eventi.remove(evento);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
            return false;
        }

        return true;
    }

    @Test
    void testInviaReport1() {
        // Non è stato pianificato alcun evento.
        try {
            gestioneEventi.InviaReport();
            // assertTrue(true);
        } catch(MessagingException ignored) {
            fail();
        }
    }

    @Test
    void testInviaReport2() {
        Set<Musicista> musicistiTutti = Collections.unmodifiableSet(musicisti);
        LocalDate meseScorso = YearMonth.now().minusMonths(1).atDay(1);

        // Viene pianificato un evento.
        Evento evento = new Evento(
            new EventoParameters(
                meseScorso,
                "Luogo di prova",
                TipologiaEvento.CONCERTO,
                musicistiTutti
            )
        );

        try {
            EventoDAO.createEvento(evento);

            /* Java Reflection per aggiungere evento alla lista privata eventi;
               serve per bypassare le verifiche del Controller */
            Field eventiField = gestioneEventi.getClass().getDeclaredField("eventi");
            eventiField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Set<Evento> eventi = (Set<Evento>) eventiField.get(gestioneEventi);
            eventi.add(evento);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
            fail();
        }

        try {
            gestioneEventi.InviaReport();
            // assertTrue(true);
        } catch(MessagingException ignored) {
            fail();
        }
    }

    /**
     * Tutti gli input validi
     */
    @Test
    void testInserisciMusicista1() {
        try {
            Musicista m = gestioneEventi.InserisciMusicista(new CantanteParameter(
                    "Vincenzo",
                    "Sauzullo",
                    "vincenzo@mail.com",
                    "345df5",
                    "3516789012",
                    5000,
                    List.of(LocalDate.of(2025, 7, 17)),
                    GenereMusicale.POP,
                    null
            ));

            assertNotNull(m);
        } catch (ParameterException | SQLException e) {
            fail();
        }
    }

    /**
     * Stringa Nome di caratteri di lunghezza > 255
     */
    @Test
    void testInserisciMusicista2() {
        try {
            gestioneEventi.InserisciMusicista(new CantanteParameter(
                    "Vincenzo" + "o".repeat(255),
                    "Sauzullo",
                    "vincenzo@mail.com",
                    "345df5",
                    "3516789012",
                    5000,
                    List.of(LocalDate.of(2025, 7, 17)),
                    GenereMusicale.POP,
                    null
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Stringa nulla
     */
    @Test
    void testInserisciMusicista3() {
        try {
            gestioneEventi.InserisciMusicista(new CantanteParameter(
                    null,
                    "Aquino",
                    "carlo@mail.com",
                    "345df5",
                    "+393516789012",
                    500,
                    List.of(LocalDate.of(2025, 7, 12)),
                    GenereMusicale.POP,
                    null
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Stringa in cui non sono presenti i simboli '.' e/o '@'
     */
    @Test
    void testInserisciMusicista4() {
        try {
            gestioneEventi.InserisciMusicista(new CantanteParameter(
                    "Marco",
                    "Sauzullo",
                    "vincenzomailcom",
                    "345df5",
                    "+393516789012",
                    5000,
                    List.of(LocalDate.of(2025, 7, 12)),
                    GenereMusicale.POP,
                    null
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Stringa Password di lunghezza > 255
     */
    @Test
    void testInserisciMusicista5() {
        try {
            gestioneEventi.InserisciMusicista(new StrumentistaParameter(
                    "Giuseppe",
                    "Sauzullo",
                    "giuseppe@mail.com",
                    "345df5432432432" + "1".repeat(255),
                    "3516789012",
                    5000,
                    List.of(LocalDate.of(2025, 7, 8)),
                    List.of(Strumento.BASSO)
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Stringa Telefono di lunghezza > 255
     */
    @Test
    void testInserisciMusicista6() {
        try {
            gestioneEventi.InserisciMusicista(new StrumentistaParameter(
                    "Gennaro",
                    "Ammendola",
                    "gennulux@mail.com",
                    "345df5432432432",
                    "3516789012" + "3".repeat(255),
                    156,
                    List.of(LocalDate.of(2025, 12, 6)),
                    List.of(Strumento.OBOE)
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Numero intero < 0 per il cachet
     */
    @Test
    void testInserisciMusicista7() {
        try {
            gestioneEventi.InserisciMusicista(new StrumentistaParameter(
                    "Gennaro",
                    "Ammendola",
                    "gennulux@mail.com",
                    "345df5432432432",
                    "3516789012",
                    -1,
                    List.of(LocalDate.of(2025, 12, 6)),
                    List.of(Strumento.OBOE)
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Valore null per GenereMusicale
     */
    @Test
    void testInserisciMusicista8() {
        try {
            gestioneEventi.InserisciMusicista(new CantanteParameter(
                    "Riccardo",
                    "Orsolini",
                    "rick@mail.com",
                    "3padow13",
                    "3516789012",
                    240,
                    List.of(),
                    null,
                    RegistroVocale.BARITONO
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    void testInserisciMusicista9() {
        try {
            gestioneEventi.InserisciMusicista(new CantanteParameter(
                    "Riccardo",
                    "Orsolini",
                    "rick@mail.com",
                    "3padow13",
                    "3516789012",
                    240,
                    List.of(LocalDate.of(2025, 12, 6)),
                    GenereMusicale.CLASSICA,
                    null
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Valore null per Strumento
     */
    @Test
    void testInserisciMusicista10() {
        try {
            gestioneEventi.InserisciMusicista(new StrumentistaParameter(
                    "Riccardo",
                    "Orsolini",
                    "rick@mail.com",
                    "3padow13",
                    "3516789012",
                    240,
                    List.of(LocalDate.of(2025, 12, 6)),
                    null
            ));

            fail();
        } catch (ParameterException e) {
            assertTrue(true);
        } catch (SQLException e) {
            fail();
        }
    }

    private Partecipazione getPartecipazione(Evento evento, Musicista musicista) {
        return evento.getPartecipanti().stream()
                .filter(p -> p.getMusicista().equals(musicista))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void testAccettaConvocazione1() throws Exception {
        Musicista cantante = musicisti.stream().filter(m -> m instanceof Cantante).findFirst().orElse(null);
        Musicista strumentista = musicisti.stream().filter(m -> m instanceof Strumentista).findFirst().orElse(null);
        if(cantante == null || strumentista == null) fail();
        Set<Musicista> partecipanti = Set.of(cantante, strumentista);
        partecipanti.forEach(m -> m.setDisponibilita(new ArrayList<>(m.getDisponibilita())));

        Evento evento = gestioneEventi.PianificaEvento(new EventoParameters(
                LocalDate.of(2025, 7, 20),
                "Roma",
                TipologiaEvento.ESIBIZIONE,
                partecipanti
        ));

        Partecipazione p = getPartecipazione(evento, cantante);
        gestioneEventi.AccettaConvocazione(p);

        assertTrue(p.isConfermato());
        assertNotEquals(StatoEvento.CONFERMATO, evento.getStato());

        assertTrue(this._deleteEvento(evento));
    }

    @Test
    void testAccettaConvocazione2() throws Exception {
        Musicista cantante = musicisti.stream().filter(m -> m instanceof Cantante).findFirst().orElse(null);
        Musicista strumentista = musicisti.stream().filter(m -> m instanceof Strumentista).findFirst().orElse(null);
        if(cantante == null || strumentista == null) fail();
        Set<Musicista> partecipanti = Set.of(cantante, strumentista);
        partecipanti.forEach(m -> m.setDisponibilita(new ArrayList<>(m.getDisponibilita())));

        Evento evento = gestioneEventi.PianificaEvento(new EventoParameters(
                LocalDate.of(2025, 7, 21),
                "Milano",
                TipologiaEvento.ESIBIZIONE,
                partecipanti
        ));

        Partecipazione p1 = getPartecipazione(evento, strumentista);
        Partecipazione p2 = getPartecipazione(evento, cantante);

        gestioneEventi.AccettaConvocazione(p1);
        gestioneEventi.AccettaConvocazione(p2);

        assertTrue(p2.isConfermato());
        assertEquals(StatoEvento.CONFERMATO, evento.getStato());

        assertTrue(this._deleteEvento(evento));
    }

    @Test
    void testAccettaConvocazione3() throws Exception {
        Musicista cantante = musicisti.stream().filter(m -> m instanceof Cantante).findFirst().orElse(null);
        Musicista strumentista = musicisti.stream().filter(m -> m instanceof Strumentista).findFirst().orElse(null);
        if(cantante == null || strumentista == null) fail();
        Set<Musicista> partecipanti = Set.of(cantante, strumentista);
        partecipanti.forEach(m -> m.setDisponibilita(new ArrayList<>(m.getDisponibilita())));

        Evento evento = gestioneEventi.PianificaEvento(new EventoParameters(
                LocalDate.of(2025, 7, 22),
                "Torino",
                TipologiaEvento.ESIBIZIONE,
                partecipanti
        ));

        Partecipazione p = getPartecipazione(evento, cantante);
        gestioneEventi.AccettaConvocazione(p);
        gestioneEventi.AccettaConvocazione(p);

        assertTrue(p.isConfermato());

        assertTrue(this._deleteEvento(evento));
    }

    @Test
    void testAccettaConvocazione4() throws Exception {
        Musicista cantante = musicisti.stream().filter(m -> m instanceof Cantante).findFirst().orElse(null);
        Musicista strumentista = musicisti.stream().filter(m -> m instanceof Strumentista).findFirst().orElse(null);
        if(cantante == null || strumentista == null) fail();
        Set<Musicista> partecipanti = Set.of(cantante, strumentista);

        LocalDate data = LocalDate.of(2026, 12, 25);
        cantante.setDisponibilita(new ArrayList<>()); // disponibilità vuota

        Evento evento = gestioneEventi.PianificaEvento(new EventoParameters(
                data,
                "Napoli",
                TipologiaEvento.ESIBIZIONE,
                partecipanti
        ));

        Partecipazione p = getPartecipazione(evento, cantante);
        gestioneEventi.AccettaConvocazione(p);

        assertTrue(p.isConfermato());

        assertTrue(this._deleteEvento(evento));
    }

    @Test
    void testAccettaConvocazione5() {
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> gestioneEventi.AccettaConvocazione(null));
    }

    @Test
    void testAccettaConvocazione6() {
        Musicista m = musicisti.iterator().next();
        Partecipazione p = new Partecipazione(m, null, 0, false);
        assertThrows(NullPointerException.class, () -> gestioneEventi.AccettaConvocazione(p));
    }

    @Test
    void testAccettaConvocazione7() throws Exception {
        Musicista cantante = musicisti.stream().filter(m -> m instanceof Cantante).findFirst().orElse(null);
        Musicista strumentista = musicisti.stream().filter(m -> m instanceof Strumentista).findFirst().orElse(null);
        if(cantante == null || strumentista == null) fail();
        Set<Musicista> partecipanti = Set.of(cantante, strumentista);

        Evento evento = gestioneEventi.PianificaEvento(new EventoParameters(
                LocalDate.of(2025, 8, 1),
                "Genova",
                TipologiaEvento.ESIBIZIONE,
                partecipanti
        ));

        Partecipazione p = new Partecipazione(null, evento, 0, false);
        assertThrows(NullPointerException.class, () -> gestioneEventi.AccettaConvocazione(p));

        assertTrue(this._deleteEvento(evento));
    }

    @Test
    void testAccettaConvocazione8() throws Exception {
        Musicista strumentista = musicisti.stream().filter(m -> m instanceof Strumentista).findFirst().orElse(null);
        if(strumentista == null) fail();
        Set<Musicista> partecipanti = Set.of(strumentista);
        partecipanti.forEach(m -> m.setDisponibilita(new ArrayList<>(m.getDisponibilita())));

        Evento evento = gestioneEventi.PianificaEvento(new EventoParameters(
                LocalDate.of(2025, 8, 2),
                "Bologna",
                TipologiaEvento.STRUMENTALE,
                partecipanti
        ));

        Partecipazione p = getPartecipazione(evento, strumentista);
        gestioneEventi.AccettaConvocazione(p);

        assertTrue(p.isConfermato());
        assertEquals(StatoEvento.CONFERMATO, evento.getStato());

        assertTrue(this._deleteEvento(evento));
    }
}