import boundary.BAgente;
import boundary.BMusicista;
import boundary.BTempo;

import controller.email.ServiziEmail;

import java.io.*;

import java.util.Scanner;


public class Main {
    private final static Scanner scanner = new Scanner(System.in);

    public static void main(final String[] args) {
        if(args.length == 0 || !args[0].equals("--no-email"))
            inizializzaServiziEmail();

        banner();
        run();
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

    private static void banner() {
        InputStream input = Main.class.getClassLoader().getResourceAsStream("banner.txt");

        System.out.println();

        try {
            assert input != null;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch(IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }

        System.out.println();
    }

    private static void run() {
        boolean exit;

        do {
            System.out.println("Scegli che attore sei:");
            System.out.println("1 - Agente");
            System.out.println("2 - Musicista");
            System.out.println("3 - Tempo");
            System.out.println("4 - Esci dall'applicativo");

            String input = scanner.nextLine();

            exit = switch(input) {
                case "1" -> BAgente.main();
                case "2" -> BMusicista.main();
                case "3" -> BTempo.main();
                case "4" -> true;
                default -> {
                    System.out.println("Input non valido.");
                    yield false;
                }
            };
        } while(!exit);

        System.out.println("Terminazione.");
    }
}
