package boundary;

import controller.GestioneEventi;
import jakarta.mail.MessagingException;

import java.util.Scanner;


public class BTempo {
    private static final GestioneEventi gestioneEventi = GestioneEventi.getInstance();
    private final static Scanner scanner = new Scanner(System.in);

    public static void InviaReport() {
        try {
            gestioneEventi.InviaReport();

            System.out.println("Report inviato correttamente.");
        } catch(MessagingException e) {
            System.out.println("Errore nell'invio del report: "+e.getMessage());
        }
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static boolean main() {
        boolean exit, exit_code = false;

        do {
            System.out.println("Attore Tempo - Scegli che azione compiere:");
            System.out.println("1 - Invia Report");
            System.out.println("2 - Esci dalla visualizzazione dell'attore");
            System.out.println("3 - Esci dall'applicativo");

            String input = scanner.nextLine();

            exit = switch (input) {
                case "1" -> {
                    BTempo.InviaReport();
                    yield false;
                }
                case "2" -> true;
                case "3" -> {
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