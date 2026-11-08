package boundary;

import controller.GestioneEventi;
import entity.Evento;
import entity.Musicista;
import entity.Partecipazione;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class BMusicista {
    private static final GestioneEventi gestioneEventi = GestioneEventi.getInstance();
    private final static Scanner scanner = new Scanner(System.in);

    public static Musicista Accedi(String email, String password) {
        return gestioneEventi.Accedi(email, password);
    }

    public static void ModificaDisponibilita(Musicista musicista) {
        String input;
        do {
            VisualizzaDisponibilita(musicista);

            System.out.println("Aggiungi o rimuovi una nuova disponibilità nel formato yyyy-MM-dd");
            System.out.println("Premi invio una volta terminato");

            input = scanner.nextLine();

            if(input.isEmpty()) break;

            LocalDate date;
            try {
                date = LocalDate.parse(input);
            } catch (DateTimeParseException ignored) {
                System.out.println("Data non nel formato yyyy-MM-dd");
                continue;
            }

            if(date.isBefore(LocalDate.now())) {
                System.out.println("Data inserita non valida in quanto è precedente ad oggi");
                continue;
            }

            if(musicista.getDisponibilita().contains(date)) {
                musicista.getDisponibilita().remove(date);
            } else {
                musicista.getDisponibilita().add(date);
            }

            if(gestioneEventi.ModificaDisponibilita(musicista)) {
                System.out.println("Disponibilità aggiornate");
            } else {
                System.out.println("Disponibilità non aggiornato - problemi al database");
            }
        } while(true);
    }

    public static void VisualizzaDisponibilita(Musicista musicista) {
        List<LocalDate> disponibilita = new ArrayList<>(musicista.getDisponibilita());

        disponibilita.sort(LocalDate::compareTo);

        if(disponibilita.isEmpty()) {
            System.out.println("Non ci sono ancora date in cui è stata fornita la propria disponibilita");
        } else {
            System.out.println("Le disponibilità date finora:");

            for(LocalDate date : disponibilita) {
                System.out.println(date.toString());
            }
        }
    }

    public static void AccettaConvocazioni(Musicista musicista) {
        String input;
        long id;
        Evento evento;
        List<Musicista> partecipanti;
        Optional<Partecipazione> optional;
        Partecipazione partecipazione;
        do {
            VisualizzaConvocazioni(musicista);

            System.out.println("Inserisci il numero dell'evento di cui accettare la convocazione");
            System.out.println("Premi invio per uscire");

            input = scanner.nextLine();

            if(input.isEmpty())
                break;

            try {
                id = Long.parseLong(input);
            } catch (NumberFormatException ignored) {
                System.out.println("Input non valido");
                continue;
            }

            evento = gestioneEventi.CaricaEvento(id);

            if(evento == null) {
                System.out.println("Non esiste alcun evento associato a questo identificativo");
                continue;
            }

            partecipanti = evento.getPartecipanti().stream().map(Partecipazione::getMusicista).toList();

            if(!partecipanti.contains(musicista)) {
                System.out.println("Non sei stato invitato a partecipare a questo evento");
                continue;
            }

            optional = evento.getPartecipanti().stream().filter(p -> p.getMusicista().equals(musicista)).findFirst();

            if(optional.isPresent()) {
                partecipazione = optional.get();
            } else {
                // Casistica impossibile, già controllata in precedenza
                continue;
            }

            if(partecipazione.isConfermato()) {
                System.out.println("Hai già dato conferma di partecipazione per questo evento");
                continue;
            }

            gestioneEventi.AccettaConvocazione(partecipazione);

            System.out.println("Hai dato conferma della tua partecipazione all'evento #" + evento.getId());
        } while(true);
    }

    public static void VisualizzaConvocazioni(Musicista musicista) {
        List<Partecipazione> partecipazioni = gestioneEventi.VisualizzaConvocazioni(musicista);

        System.out.printf("Elenco di convocazioni per [%d] %s %s:%n",
            musicista.getId(),
            musicista.getNome(),
            musicista.getCognome()
        );

        for(Partecipazione partecipazione : partecipazioni) {
            Evento evento = partecipazione.getEvento();

            System.out.printf("- Evento #%d presso \"%s\" in data %s (Confermato: %s)%n",
                evento.getId(),
                evento.getLuogo(),
                evento.getData().toString(),
                partecipazione.isConfermato() ? "sì" : "no"
            );
        }

        System.out.println();
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static boolean main() {
        System.out.println("Inserisci email:");
        String email = scanner.nextLine();
        System.out.println("Inserisci password:");
        String password = scanner.nextLine();

        Musicista musicista = BMusicista.Accedi(email, password);

        if(musicista == null) {
            System.out.println("Accesso negato: email o password non corretta.");
            return false;
        }

        System.out.println("Accesso riuscito per " + musicista.getNome() + " " + musicista.getCognome());

        boolean exit, exit_code = false;
        do {
            System.out.printf("Attore musicista %s %s - Scegli cosa vuoi fare:%n", musicista.getNome(), musicista.getCognome());
            System.out.println("1 - Modifica disponibilità");
            System.out.println("2 - Visualizza disponibilità");
            System.out.println("3 - Accetta convocazioni");
            System.out.println("4 - Visualizza convocazioni");
            System.out.println("5 - Esci dalla visualizzazione dell'attore");
            System.out.println("6 - Esci dall'applicativo");

            String input = scanner.nextLine();

            exit = switch(input) {
                case "1" -> {
                    ModificaDisponibilita(musicista);
                    yield false;
                }
                case "2" -> {
                    VisualizzaDisponibilita(musicista);
                    yield false;
                }
                case "3" -> {
                    AccettaConvocazioni(musicista);
                    yield false;
                }
                case "4" -> {
                    VisualizzaConvocazioni(musicista);
                    yield false;
                }
                case "5" -> true;
                case "6" -> {
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
