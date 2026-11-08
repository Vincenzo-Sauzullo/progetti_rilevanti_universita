package boundary;

import controller.GestioneEventi;
import controller.exceptions.EventoConcomitanteException;
import controller.exceptions.ParameterException;
import controller.parameters.*;
import entity.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


public class BAgente {
    private static final GestioneEventi gestioneEventi = GestioneEventi.getInstance();
    private final static Scanner scanner = new Scanner(System.in);

    public static void InserisciMusicista() {
        System.out.println("Inserisci nome");
        String nome = scanner.nextLine();
        System.out.println("Inserisci cognome");
        String cognome = scanner.nextLine();
        String email;
        do {
            System.out.println("Inserisci email");
            email = scanner.nextLine();

            if(!email.matches("^.+?@.+?\\..+$")) {
                System.out.println("Email non valida");
                continue;
            }

            break;
        } while(true);
        System.out.println("Inserisci password");
        String password = scanner.nextLine();
        String telefono;
        do {
            System.out.println("Inserisci telefono");
            try {
                telefono = scanner.nextLine();
                break;
            } catch (InputMismatchException ignored) { }
        } while(true);
        int cachet;
        do {
            System.out.println("Inserisci cachet (per serata)");
            try {
                cachet = scanner.nextInt();
                break;
            } catch (InputMismatchException ignored) { }
        } while(true);

        scanner.nextLine(); // Ignora la riga a capo.

        String input;
        boolean valid = false, cantante = false;
        do {
            System.out.println("Di che musicista si tratta?");
            System.out.println("1 - Cantante");
            System.out.println("2 - Strumentista");
            input = scanner.nextLine();

            switch(input) {
                case "1" -> {
                    cantante = true;
                    valid = true;
                }
                case "2" -> valid = true; /* cantante = false, implicitamente */
                default -> System.out.println("Input non valido.");
            }
        } while(!valid);

        MusicistaParameter parameter;
        if(cantante) {
            GenereMusicale genereMusicale = null;
            RegistroVocale registroVocale = null;

            do {
                System.out.println("Seleziona il genere musicale:");
                for(GenereMusicale gm : GenereMusicale.values())
                    System.out.println(gm);
                input = scanner.nextLine();
                try {
                    genereMusicale = GenereMusicale.valueOf(input.toUpperCase());
                } catch(IllegalArgumentException ignored) {
                    System.out.println("Input non valido.");
                }
            } while(genereMusicale == null);

            if(genereMusicale == GenereMusicale.CLASSICA) {
                do {
                    System.out.println("Seleziona il registro vocale:");
                    for(RegistroVocale rv : RegistroVocale.values())
                        System.out.println(rv);
                    input = scanner.nextLine();
                    try {
                        registroVocale = RegistroVocale.valueOf(input.toUpperCase());
                    } catch(IllegalArgumentException ignored) {
                        System.out.println("Input non valido.");
                    }
                } while(registroVocale == null);
            }

            parameter = new CantanteParameter(nome, cognome, email, password, telefono, cachet, List.of(), genereMusicale, registroVocale);
        } else {
            List<Strumento> strumenti = new ArrayList<>();

            Strumento strumento;
            boolean exit = false;
            do {
                System.out.println("Seleziona gli strumenti da aggiungere, uno alla volta (max 4)");
                System.out.print("Selezionati: ");
                if(strumenti.isEmpty()) System.out.println("Nessuno.");
                else System.out.println(strumenti.stream().map(Enum::toString).collect(Collectors.joining(", ")));
                System.out.println("Premi invio per ricominciare o una volta terminato l'inserimento");

                for(Strumento s : Strumento.values())
                    System.out.println(s);

                input = scanner.nextLine();

                if(!input.isEmpty()) {
                    try {
                        strumento = Strumento.valueOf(input.toUpperCase());

                        if(strumenti.contains(strumento)) {
                            continue;
                        } else {
                            strumenti.add(strumento);
                        }
                    } catch(IllegalArgumentException ignored) {
                        System.out.println("Input non valido.");
                    }
                } else {
                    if(strumenti.isEmpty()) {
                        System.out.println("Non ci sono abbastanza strumenti (min 1).");
                        continue;
                    }

                    exit = true;
                }

                exit = exit || strumenti.size() == 4;

                if(exit) {
                    boolean inner_exit;
                    do {
                        System.out.print("Strumenti inseriti: ");
                        System.out.println(strumenti.stream().map(Enum::toString).collect(Collectors.joining(", ")));
                        System.out.println("Vuoi proseguire o ricominciare da capo con l'inserimento?");
                        System.out.println("1 - Prosegui");
                        System.out.println("2 - Ricomincia");

                        input = scanner.nextLine();
                        switch(input) {
                            case "1" -> inner_exit = true;
                            case "2" -> {
                                strumenti.clear();
                                exit = false;
                                inner_exit = true;
                            }
                            default -> inner_exit = false;
                        }
                    } while(!inner_exit);
                }
            } while(!exit);

            parameter = new StrumentistaParameter(nome, cognome, email, password, telefono, cachet, List.of(), strumenti);
        }

        try {
            Musicista aggiunto = gestioneEventi.InserisciMusicista(parameter);
            System.out.println("Inserito un nuovo musicista:");
            System.out.println(aggiunto.toString());
        } catch (ParameterException | SQLException e) {
            System.err.println("Non è stato possibile inserire un nuovo musicista");
            System.err.println(e.getMessage());
        }
    }

    public static void ModificaMusicista() {
        //noinspection DuplicatedCode
        Set<Musicista> musicistiTrovati = _CercaMusicisti();
        if(musicistiTrovati == null) return;

        // Oggetto mutabile
        List<Musicista> musicisti = new ArrayList<>(musicistiTrovati);

        Map<Long, Musicista> musicistiIndicizzato = new HashMap<>();
        musicisti.forEach(m -> musicistiIndicizzato.put(m.getId(), m));

        String input;
        long id;
        Musicista musicista;

        do {
            musicisti.stream().sorted((a, b) -> Math.toIntExact(a.getId() - b.getId())).forEachOrdered(System.out::println);
            System.out.println();

            System.out.println("Seleziona il musicista da modificare");
            System.out.println("Una volta terminato, premi invio");

            input = scanner.nextLine();

            if(input.isEmpty()) return;

            if(musicisti.isEmpty()) {
                System.out.println("Non ci sono musicisti da modificare");
                return;
            }

            try {
                id = Long.parseLong(input);

                musicista = musicistiIndicizzato.get(id);

                if(musicista == null) throw new NullPointerException();
            } catch(NumberFormatException | NullPointerException ignored) {
                System.out.println("Input non valido");
                continue;
            }

            do {
                System.out.println("Cosa vuoi modificare?");
                System.out.printf("1 - %-20s %s%n", "Nome", musicista.getNome());
                System.out.printf("2 - %-20s %s%n", "Cognome", musicista.getCognome());
                System.out.printf("3 - %-20s %s%n", "Email", musicista.getEmail());
                System.out.printf("4 - %-20s %s%n", "Password", musicista.getPassword());
                System.out.printf("5 - %-20s %s%n", "Telefono", musicista.getTelefono());
                System.out.printf("6 - %-20s %d%n", "Cachet", musicista.getCachetPerSerata());

                if(musicista instanceof Cantante cantante) {
                    System.out.printf("7 - %-20s %s%n", "Genere musicale", cantante.getGenereMusicale());

                    if(cantante.getGenereMusicale() == GenereMusicale.CLASSICA) {
                        System.out.printf("8 - %-20s %s%n", "Registro vocale", cantante.getRegistroVocale());
                    }
                } else if(musicista instanceof Strumentista strumentista) {
                    System.out.printf("7 - %-20s %s%n", "Strumenti", strumentista.getStrumenti().isEmpty() ? "Nessuno" :
                    strumentista.getStrumenti().stream().map(Enum::toString).collect(Collectors.joining(", ")));
                }

                input = scanner.nextLine();
                if(input.isEmpty()) break;

                switch(input) {
                    case "1":
                        System.out.println("Inserisci nome");
                        input = scanner.nextLine();
                        musicista.setNome(input);
                        break;
                    case "2":
                        System.out.println("Inserisci cognome");
                        input = scanner.nextLine();
                        musicista.setCognome(input);
                        break;
                    case "3":
                        System.out.println("Inserisci email");
                        input = scanner.nextLine();
                        musicista.setEmail(input);
                        break;
                    case "4":
                        System.out.println("Inserisci password");
                        input = scanner.nextLine();
                        musicista.setPassword(input);
                        break;
                    case "5":
                        System.out.println("Inserisci telefono");
                        input = scanner.nextLine();
                        try {
                            musicista.setTelefono(input);
                        } catch(NumberFormatException | NullPointerException ignored) {
                            System.out.println("Input non valido.");
                        }
                        break;
                    case "6":
                        System.out.println("Inserisci cachet");
                        input = scanner.nextLine();
                        try {
                            int cachet = Integer.parseInt(input);
                            musicista.setCachetPerSerata(cachet);
                        } catch(NumberFormatException | NullPointerException ignored) {
                            System.out.println("Input non valido.");
                        }
                        break;
                    default:
                        boolean obbligoModificaRegistroVocale = false;

                        if(musicista instanceof Cantante cantante) {
                            if(input.equals("7")) {
                                List<GenereMusicale> generiMusicali = new ArrayList<>(List.of(cantante.getGenereMusicale()));
                                _SelezionaGeneriMusicali(generiMusicali, () -> {
                                    System.out.println("Inserisci il genere musicale da applicare al musicista");
                                    System.out.println("Premi invio una volta terminato");
                                }, 1, 1);
                                GenereMusicale genereMusicale = generiMusicali.getFirst();
                                cantante.setGenereMusicale(genereMusicale);

                                if(genereMusicale.equals(GenereMusicale.CLASSICA)) {
                                    if(cantante.getRegistroVocale() == null)
                                        obbligoModificaRegistroVocale = true;
                                    else
                                        break;
                                } else {
                                    cantante.setRegistroVocale(null);
                                    break;
                                }
                            }

                            if(obbligoModificaRegistroVocale || (cantante.getGenereMusicale() == GenereMusicale.CLASSICA && input.equals("8"))) {
                                List<RegistroVocale> registriVocali = new ArrayList<>(List.of(cantante.getRegistroVocale()));
                                _SelezionaRegistriVocali(registriVocali, () -> {
                                    System.out.println("Inserisci il registro vocale da applicare al musicista");
                                    System.out.println("Premi invio una volta terminato");
                                }, 1, 1);
                                cantante.setRegistroVocale(registriVocali.getFirst());
                            }

                            break;
                        } else if(musicista instanceof Strumentista strumentista) {
                            if(input.equals("7")) {
                                _SelezionaStrumenti(strumentista.getStrumenti(), () -> {
                                    System.out.println("Inserisci lo strumento da inserire o rimuovere dal musicista");
                                    System.out.println("Premi invio una volta terminato");
                                }, 1, 4);
                                break;
                            }
                        }

                        System.out.println("Input non valido");
                        break;
                }
            } while(true);

            if (gestioneEventi.ModificaMusicista(musicista)) {
                System.out.println("Musicista modificato.");
            } else {
                System.out.println("Musicista non modificato - si è verificato un problema.");
            }
        } while(true);
    }

    public static void RimuoviMusicista() {
        //noinspection DuplicatedCode
        Set<Musicista> musicistiTrovati = _CercaMusicisti();
        if(musicistiTrovati == null) return;

        // Oggetto mutabile
        List<Musicista> musicisti = new ArrayList<>(musicistiTrovati);

        Map<Long, Musicista> musicistiIndicizzato = new HashMap<>();
        musicisti.forEach(m -> musicistiIndicizzato.put(m.getId(), m));

        String input;
        long id;
        Musicista musicista;

        do {
            musicisti.stream().sorted((a, b) -> Math.toIntExact(a.getId() - b.getId())).forEachOrdered(System.out::println);
            System.out.println();

            System.out.println("Seleziona il musicista da rimuovere");
            System.out.println("Una volta terminato, premi invio");

            input = scanner.nextLine();

            if(input.isEmpty()) return;

            if(musicisti.isEmpty()) {
                System.out.println("Non ci sono musicisti da rimuovere");
                return;
            }

            try {
                id = Long.parseLong(input);

                musicista = musicistiIndicizzato.get(id);

                if(musicista == null) throw new NullPointerException();
            } catch(NumberFormatException | NullPointerException ignored) {
                System.out.println("Input non valido");
                continue;
            }

            if (gestioneEventi.RimuoviMusicista(musicista)) {
                musicisti.remove(musicista);
                musicistiIndicizzato.remove(id);
                System.out.println("Musicista rimosso.");
            } else {
                System.out.println("Musicista non rimosso - si è verificato un problema.");
            }
        } while(true);
    }

    public static void CercaMusicisti() {
        Set<Musicista> musicisti = _CercaMusicisti();

        if(musicisti == null) {
            System.out.println("Ricerca annullata");
        } else if(musicisti.isEmpty()) {
            System.out.println("Non è stato trovato alcun musicista corrispondente ai criteri specificati.");
        } else {
            System.out.println("Musicisti trovati:");
            musicisti.stream().sorted((a, b) -> Math.toIntExact(a.getId() - b.getId())).forEachOrdered(System.out::println);
            System.out.println();
        }
    }

    private static Set<Musicista> _CercaMusicisti() {
        String query = "";
        boolean cantanti = true;
        boolean strumentisti = true;
        List<GenereMusicale> generiMusicali = new ArrayList<>();
        List<RegistroVocale> registriVocali = new ArrayList<>();
        List<Strumento> strumenti = new ArrayList<>();
        LocalDate disponibilitaDa = null, disponibilitaA = null;
        BiFunction<LocalDate, LocalDate, String> disponibilitaFormatter = (da, a) -> da.isEqual(a) ?
            da.toString() :
            "%s - %s".formatted(
                da, a
            );

        String input;
        boolean exit, search = false;

        do {
            System.out.println("Premi un numero per cambiare le opzioni di ricerca.");
            System.out.println("Premi invio per confermare.");
            System.out.printf("1 - %-30s %s%n", "Query di ricerca", query.isEmpty() ? "N/A" : query);
            System.out.printf("2 - %-30s %s%n", "Ricerca tra cantanti", cantanti ? "Sì" : "No");
            System.out.printf("3 - %-30s %s%n", "Ricerca tra strumentisti", strumentisti ? "Sì" : "No");
            System.out.printf("4 - %-30s %s%n", "Genere musicale", generiMusicali.isEmpty() ? "N/A" : "...");
            System.out.printf("5 - %-30s %s%n", "Registro vocale", registriVocali.isEmpty() ? "N/A" : "...");
            System.out.printf("6 - %-30s %s%n", "Strumenti necessari", strumenti.isEmpty() ? "N/A" : "...");
            System.out.printf("7 - %-30s %s%n", "Giorni disponibilità", (disponibilitaDa == null || disponibilitaA == null) ?
                              "N/A" : disponibilitaFormatter.apply(disponibilitaDa, disponibilitaA));
            System.out.println("8 - Esci dalla ricerca");

            input = scanner.nextLine();

            exit = switch(input) {
                case "" -> {
                    search = true;
                    yield true;
                }
                case "1" -> {
                    System.out.println("Inserisci query");
                    query = scanner.nextLine();
                    yield false;
                }
                case "2" -> {
                    cantanti = !cantanti;
                    yield false;
                }
                case "3" -> {
                    strumentisti = !strumentisti;
                    yield false;
                }
                case "4" -> {
                    _SelezionaGeneriMusicali(generiMusicali, () -> {
                        System.out.println("Inserisci il genere musicale da inserire o rimuovere dal filtraggio");
                        System.out.println("Premi invio una volta terminato");
                    }, 0, 0);

                    yield false;
                }
                case "5" -> {
                    _SelezionaRegistriVocali(registriVocali, () -> {
                        System.out.println("Inserisci il registro vocale da inserire o rimuovere dal filtraggio");
                        System.out.println("Premi invio una volta terminato");
                    }, 0, 0);

                    yield false;
                }
                case "6" -> {
                    _SelezionaStrumenti(strumenti, () -> {
                        System.out.println("Inserisci lo strumento da inserire o rimuovere dal filtraggio");
                        System.out.println("Premi invio una volta terminato");
                        System.out.println("Nota: per il filtraggio per strumenti, lo strumentista deve essere specializzato in tutti contemporaneamente");
                    }, 0, 0);

                    yield false;
                }
                case "7" -> {
                    System.out.println("Inserisci i giorni di disponibilità in formato YYYY-MM-DD, come data singola (data) o intervallo di date (data-data)");

                    input = scanner.nextLine();

                    try {
                        if(input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            disponibilitaDa = LocalDate.parse(input);
                            disponibilitaA = LocalDate.parse(input);
                        }
                        else if(input.matches("\\d{4}-\\d{2}-\\d{2}-\\d{4}-\\d{2}-\\d{2}")) {
                            disponibilitaDa = LocalDate.parse(input.substring(0, 10));
                            disponibilitaA  = LocalDate.parse(input.substring(11, 21));
                        } else {
                            System.out.println("Input non valido.");
                            yield false;
                        }

                        if(disponibilitaDa.isAfter(disponibilitaA)) {
                            System.out.println("La data massima è situata dopo la data minima.");
                        }
                    } catch(DateTimeParseException ignored) {
                        System.out.println("Input non valido.");
                    }

                    yield false;
                }
                case "8" -> true;
                default -> {
                    System.out.println("Input non valido.");
                    yield false;
                }
            };
        } while(!exit);

        QueryParameters queryParameters = new QueryParameters(query, cantanti, strumentisti, generiMusicali, registriVocali, strumenti, disponibilitaDa, disponibilitaA);
        if(search) {
            try {
                return gestioneEventi.CercaMusicisti(queryParameters);
            } catch (ParameterException e) {
                System.err.println(e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    private static <E extends Enum<E>> void _Seleziona(Class<E> enumType, List<E> valori, Runnable domanda, Runnable errore, int min, int max) {
        if(min > max || min < 0) return;

        String input;
        do {
            for(E t : enumType.getEnumConstants())
                System.out.printf("[%c] %s%n", valori.contains(t) ? 'X' : ' ', t.toString());

            domanda.run();
            input = scanner.nextLine();

            if(input.isEmpty()) {
                if(valori.size() < min) {
                    System.out.println("Devi selezionare almeno "+min+" valori");
                    continue;
                } else if(valori.size() > max && max != 0) {
                    System.out.println("Devi selezionare al massimo "+max+" valori");
                    continue;
                }
                break;
            }

            E object;
            try {
                object = Enum.valueOf(enumType, input.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                errore.run();
                continue;
            }

            if(min == max && min > 0) {
                /* Se la condizione viene rispettata:
                 * è consentito andare in uno stato invalido temporaneamente, altrimenti non è possibile muoversi;
                 * un ulteriore controllo viene comunque effettuato prima della condizione di uscita. */
                if(min == 1) { // Caso speciale: si rimuove l'unico valore esistente per aggiungerne un altro.
                    valori.removeFirst();
                    valori.add(object);
                    continue;
                }
            } else {
                if(min == valori.size() && valori.contains(object)) {
                    System.out.println("Limite minimo raggiunto, impossibile rimuovere");
                    continue;
                } else if(max == valori.size() && !valori.contains(object)) {
                    System.out.println("Limite massimo raggiunto, impossibile aggiungere");
                    continue;
                }
            }

            if(valori.contains(object))
                valori.remove(object);
            else
                valori.add(object);
        } while(true);
    }

    private static void _SelezionaGeneriMusicali(List<GenereMusicale> generiMusicali, Runnable domanda, int min, int max) {
        _Seleziona(GenereMusicale.class, generiMusicali, domanda, () -> System.out.println("Genere musicale non valido"), min, max);
    }

    private static void _SelezionaRegistriVocali(List<RegistroVocale> registriVocali, Runnable domanda, int min, int max) {
        _Seleziona(RegistroVocale.class, registriVocali, domanda, () -> System.out.println("Registro vocale non valido"), min, max);
    }

    private static void _SelezionaStrumenti(List<Strumento> strumenti, Runnable domanda, int min, int max) {
        _Seleziona(Strumento.class, strumenti, domanda, () -> System.out.println("Strumento non valido"), min, max);
    }

    public static void PianificaEvento() {
        LocalDate data;

        do {
            System.out.println("Inserisci data (formato yyyy-MM-dd)");
            try {
                data = LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException ignored) {
                System.out.println("Inserita data in un formato errato");
                continue;
            }

            if(data.isBefore(LocalDate.now())) {
                System.out.println("La data è precedente ad oggi");
                continue;
            }

            break;
        } while(true);

        System.out.println("Inserisci luogo");
        String luogo = scanner.nextLine();

        TipologiaEvento tipologia = _SelezionaTipologia();

        Set<Musicista> invitati = new HashSet<>();

        if(_SelezionaMusicisti(invitati, tipologia, data))
            return;

        EventoParameters eventoParameters = new EventoParameters(data, luogo, tipologia, invitati);

        Evento evento;
        try {
            evento = gestioneEventi.PianificaEvento(eventoParameters);
        } catch (EventoConcomitanteException | ParameterException e) {
            System.out.println(e.getMessage());
            return;
        }

        if(evento == null) {
            System.out.println("Non è stato possibile pianificare l'evento - si è verificato un errore.");
            return;
        }

        _VisualizzaResoconto(evento);
    }

    private static boolean _SelezionaMusicisti(Set<Musicista> invitati, TipologiaEvento tipologia, LocalDate data) {
        String input;
        long id;
        do {
            do {
                System.out.println("Inserisci l'identificativo dei musicisti da invitare o a cui revocare l'invito.");
                System.out.println("Per avviare il menu di ricerca, scrivi \"cerca\"");
                System.out.println("Per annullare l'inserimento di un nuovo evento, scrivi \"esci\"");
                System.out.print("Musicisti invitati finora: ");
                if(invitati.isEmpty()) {
                    System.out.println("Nessuno");
                } else {
                    System.out.println();
                    for(Musicista m : invitati) {
                        System.out.printf("- %s%n", m);
                    }
                }
                System.out.println("I musicisti selezionati finora permettono la creazione dell'evento? " + (tipologia.verifica(invitati) ? "SÌ" : "NO"));
                System.out.println("Una volta terminato l'inserimento, premi invio");

                input = scanner.nextLine();

                if(input.isEmpty())
                    break;

                if(input.equalsIgnoreCase("cerca")) {
                    Set<Musicista> musicisti = _CercaMusicisti();

                    if(musicisti == null) {
                        System.out.println("Non è stato trovato alcun musicista corrispondente ai criteri specificati.");
                    } else {
                        musicisti.stream().sorted((a, b) -> Math.toIntExact(a.getId() - b.getId())).forEachOrdered(System.out::println);
                        System.out.println();
                    }
                    continue;
                }

                if(input.equalsIgnoreCase("esci"))
                    return true;

                try {
                    id = Long.parseLong(input);

                    Musicista musicista = gestioneEventi.CaricaMusicista(id);

                    if(musicista == null) {
                        System.out.println("Non esiste alcun musicista con questo identificativo");
                        continue;
                    }

                    if(invitati.contains(musicista)) {
                        System.out.printf("Revocato l'invito per: %s %s%n", musicista.getNome(), musicista.getCognome());
                        invitati.remove(musicista);
                    } else {
                        if(!musicista.getDisponibilita().contains(data)) {
                            System.out.println("Il musicista non ha dato ancora disponibilità per la data dell'evento");
                            continue;
                        }

                        System.out.printf("Inviato l'invito per: %s %s%n", musicista.getNome(), musicista.getCognome());
                        invitati.add(musicista);
                    }
                } catch(NumberFormatException ignored) {
                    System.out.println("Input non valido.");
                }
            } while(true);

            if(!tipologia.verifica(invitati)) {
                System.out.println("La tipologia di evento selezionata non è compatibile con il numero di cantanti e strumentisti selezionati.");
                continue;
            }

            break;
        } while(true);

        return false;
    }

    private static TipologiaEvento _SelezionaTipologia() {
        TipologiaEvento tipologia;
        do {
            System.out.println("Inserisci tipologia (concerto, esibizione o strumentale)");
            System.out.println("Concerto:    1  cantante, 1+ strumentisti");
            System.out.println("Esibizione:  1+ cantanti, 1+ strumentisti");
            System.out.println("Strumentale: 0  cantanti, 1+ strumentisti");
            try {
                tipologia = TipologiaEvento.valueOf(scanner.nextLine().toUpperCase());
                break;
            } catch (IllegalArgumentException ignored) { }
        } while(true);
        return tipologia;
    }

    private static void _VisualizzaResoconto(Evento evento) {
        System.out.println("L'evento è stato pianificato. Riepilogo:");
        System.out.printf("%-20s %s%n", "Tipologia", evento.getTipologia());
        System.out.printf("%-20s %s%n", "Data", evento.getData());
        System.out.printf("%-20s %s%n", "Luogo", evento.getLuogo());
        System.out.printf("%-20s %d%n", "Costo Totale", evento.getCostoTotale());
        System.out.println("Invitati:");
        evento.getPartecipanti().stream().map(Partecipazione::getMusicista).forEach(i -> System.out.printf("- %s%n", i));
    }

    public static void ModificaEvento() {
        String input;
        long id;
        Evento evento;

        do {
            System.out.println("Seleziona l'evento da modificare inserendo il suo ID");
            System.out.println("Per avviare il menu di ricerca, scrivi \"cerca\"");
            System.out.println("Una volta terminato, premi invio");

            input = scanner.nextLine();

            if(input.isEmpty())
                return;

            if(input.equalsIgnoreCase("cerca")) {
                List<Evento> eventi = _CercaEventiPassati();

                if(eventi == null) {
                    System.out.println("Ricerca annullata.");
                } else if(eventi.isEmpty()) {
                    System.out.println("Non è stato trovato alcun evento corrispondente ai criteri specificati.");
                } else {
                    System.out.printf("%3s | %-12s | %-15s | %-12s | %-10s | %-10s | %s%n",
                            "ID", "Data", "Luogo", "Costo (€)", "Tipologia", "Stato", "Musicisti");

                    for(Evento e : eventi) {
                        System.out.println(e);
                    }
                    System.out.println();
                }
                continue;
            }

            try {
                id = Long.parseLong(input);

                evento = gestioneEventi.CaricaEvento(id);

                if(evento == null)
                    throw new NullPointerException();

            } catch(NumberFormatException | NullPointerException ignored) {
                System.out.println("Input non valido");
                continue;
            }

            LocalDate data = evento.getData();
            String luogo = evento.getLuogo();
            TipologiaEvento tipologia = evento.getTipologia();
            Set<Musicista> partecipanti = evento.getPartecipanti().stream().map(Partecipazione::getMusicista).collect(Collectors.toSet());

            EventoParameters parameters;

            outer:do {
                int size = partecipanti.size();
                System.out.println("Cosa vuoi modificare?");
                System.out.printf("1 - %-20s %s%n", "Data", data);
                System.out.printf("2 - %-20s %s%n", "Luogo", luogo);
                System.out.printf("3 - %-20s %d partecipazion%c%n", "Musicisti", size, size == 1 ? 'e' : 'i');

                input = scanner.nextLine();
                if(input.isEmpty()) break;

                //noinspection SwitchStatementWithTooFewBranches
                switch(input) {
                    case "2":
                        System.out.println("Inserisci luogo");
                        luogo = scanner.nextLine();
                        break outer;
                    default:
                        boolean obbligoConvocazioneMusicisti = false;

                        if(input.equals("1")) {
                            boolean almenoUnaConferma = evento.getPartecipanti().stream().anyMatch(Partecipazione::isConfermato);

                            if(almenoUnaConferma) {
                                System.out.println("Non è possibile modificare la data di un evento di cui un utente ha già dato conferma per quella data");
                                continue;
                            }

                            System.out.println("Inserisci data (formato yyyy-MM-dd)");
                            System.out.println("Modificare la data causerà la revoca di tutte le partecipazioni");
                            input = scanner.nextLine();
                            try {
                                LocalDate nuovaData = LocalDate.parse(input);

                                if(nuovaData.isBefore(LocalDate.now())) {
                                    System.out.println("Data non valida perché precedente ad oggi");
                                    continue;
                                }

                                if(!data.equals(nuovaData)) {
                                    partecipanti.clear();
                                    obbligoConvocazioneMusicisti = true;
                                    System.out.println("Revocate tutte le partecipazioni");
                                }

                                data = nuovaData;
                            } catch(DateTimeParseException ignored) {
                                System.out.println("Input non valido.");
                                break;
                            }
                        }

                        if(input.equals("3")) {
                            boolean nullaostaConvocazione = evento.getStato() == StatoEvento.NON_CONFERMATO && evento.getPartecipanti().stream().noneMatch(Partecipazione::isConfermato);

                            if(!nullaostaConvocazione) {
                                System.out.println("Non è possibile apportare modifiche ad eventi già parzialmente o completamente confermati");
                                continue;
                            }
                        }

                        if(input.equals("3") || obbligoConvocazioneMusicisti) {
                            TipologiaEvento nuovaTipologia = _SelezionaTipologia();

                            Set<Musicista> invitati = new HashSet<>();

                            if(_SelezionaMusicisti(invitati, nuovaTipologia, data))
                                return;

                            partecipanti = invitati;

                            break outer;
                        }

                        System.out.println("Input non valido");
                        break;
                }
            } while(true);

            try {
                parameters = new EventoParameters(
                    data,
                    luogo,
                    tipologia,
                    partecipanti
                );

                gestioneEventi.ModificaEvento(evento, parameters);

                System.out.println("Evento modificato.");
            } catch (EventoConcomitanteException | ParameterException e) {
                System.err.println("Evento non modificato - si è verificato un problema.");
                System.err.println(e.getMessage());
                continue;
            }

            _VisualizzaResoconto(evento);
        } while(true);
    }

    public static void CercaEventiPassati() {
        List<Evento> eventi = _CercaEventiPassati();

        if(eventi == null) {
            System.out.println("Ricerca annullata");
        } else {
            System.out.println("Eventi trovati:");
            System.out.printf("%3s | %-12s | %-15s | %-12s | %-10s | %-10s | %s%n",
                    "ID", "Data", "Luogo", "Costo (€)", "Tipologia", "Stato", "Musicisti");

            for(Evento evento : eventi) {
                System.out.println(evento);
            }
            System.out.println();
        }
    }

    private static List<Evento> _CercaEventiPassati() {
        do {
            System.out.println();
            System.out.println("Ricerca eventi passati\n");
            System.out.println("Scegli il parametro di ricerca:");

            System.out.println("0 - Tutti gli eventi");
            System.out.println("1 - Data");
            System.out.println("2 - Luogo");
            System.out.println("3 - Tipologia evento");
            System.out.println("4 - Costo totale");
            System.out.println("5 - Musicisti partecipanti");
            System.out.println("6 - Esci dalla ricerca");

            String input = scanner.nextLine();

            switch(input) {
                case "0":
                    return gestioneEventi.cercaEventiPassati();
                case "1":
                    return _SelezionaEventiPassati(
                        "Inserisci la data di ricerca (formato YYYY-MM-DD)",
                        gestioneEventi::cercaEventiPassati,
                        (String dateString) -> {
                            try {
                                return LocalDate.parse(dateString);
                            } catch (DateTimeParseException ignored) {
                                return null;
                            }
                        }
                    );
                case "2":
                    return _SelezionaEventiPassati(
                        "Inserisci il luogo di ricerca",
                        gestioneEventi::cercaEventiPassati,
                        (String luogo) -> luogo
                    );
                case "3":
                    return _SelezionaEventiPassati(
                        "Inserisci la tipologia di evento per la ricerca",
                        gestioneEventi::cercaEventiPassati,
                        (String tipologia) -> {
                            try {
                                return TipologiaEvento.valueOf(tipologia.toUpperCase());
                            } catch(IllegalArgumentException ignored) {
                                return null;
                            }
                        }
                    );
                case "4":
                    return _SelezionaEventiPassati(
                        "Inserisci il costo totale per la ricerca",
                        gestioneEventi::cercaEventiPassati,
                        (String costoString) -> {
                            try {
                                return Integer.parseInt(costoString);
                            } catch (IllegalArgumentException ignored) {
                                return null;
                            }
                        }
                    );
                case "5":
                    return _SelezionaEventiPassati(
                        "Inserisci i musicisti partecipanti separati da una virgola",
                        gestioneEventi::cercaEventiPassati,
                        (String musicistiString) -> {
                            String[] arrayMusicistiString = musicistiString.split(",");

                            Set<Musicista> musicisti = new HashSet<>();

                            for(String musicistaString : arrayMusicistiString) {
                                QueryParameters query = new QueryParameters(musicistaString.trim());

                                Set<Musicista> musicistiNuovi;
                                try {
                                    musicistiNuovi = gestioneEventi.CercaMusicisti(query);
                                } catch (ParameterException e) {
                                    System.err.println(e.getMessage());
                                    return null;
                                }

                                for(Musicista m : musicistiNuovi) {
                                    System.out.println("Aggiunto " + m);
                                }

                                musicisti.addAll(musicistiNuovi);
                            }

                            return musicisti.stream().toList();
                        }
                    );
                case "6":
                    return null;
                default:
                    System.out.println("Scelta non valida. Riprova!");
                    break;
            }
        } while(true);
    }

    private static <T> List<Evento> _SelezionaEventiPassati(String domanda, Function<T, List<Evento>> funzioneParametro, Function<String, T> funzioneConversione) {
        do {
            System.out.println(domanda);
            System.out.println("Clicca INVIO per uscire");
            String input = scanner.nextLine();

            if(input.isEmpty()) {
                System.out.println("Uscita.");
                return null;
            }

            T parametro = funzioneConversione.apply(input);
            List<Evento> eventiPassati = funzioneParametro.apply(parametro);

            if(eventiPassati == null) {
                System.out.println("Input non valido.");
                continue;
            }

            return eventiPassati;
        } while (true);
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static boolean main() {
        boolean exit, exit_code = false;

        do {
            System.out.println("Attore Agente - Scegli che azione compiere:");
            System.out.println("1 - Inserisci Musicista");
            System.out.println("2 - Modifica Musicista");
            System.out.println("3 - Rimuovi Musicista");
            System.out.println("4 - Cerca Musicisti");
            System.out.println("5 - Pianifica Evento");
            System.out.println("6 - Modifica Evento");
            System.out.println("7 - Cerca Eventi passati");
            System.out.println("8 - Esci dalla visualizzazione dell'attore");
            System.out.println("9 - Esci dall'applicativo");

            String input = scanner.nextLine();

            exit = switch(input) {
                case "1" -> {
                    InserisciMusicista();
                    yield false;
                }
                case "2" -> {
                    ModificaMusicista();
                    yield false;
                }
                case "3" -> {
                    RimuoviMusicista();
                    yield false;
                }
                case "4" -> {
                    CercaMusicisti();
                    yield false;
                }
                case "5" -> {
                    PianificaEvento();
                    yield false;
                }
                case "6" -> {
                    ModificaEvento();
                    yield false;
                }
                case "7" -> {
                    CercaEventiPassati();
                    yield false;
                }
                case "8" -> true;
                case "9" -> {
                    exit_code = true;
                    yield true;
                }
                default -> false;
            };
        } while(!exit);

        System.out.println("Logout.");

        return exit_code;
    }
}