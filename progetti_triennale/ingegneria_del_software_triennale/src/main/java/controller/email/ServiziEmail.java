package controller.email;

public final class ServiziEmail {
    private static ServizioEmail servizioConvocazioni = null;
    private static ServizioEmail servizioReport = null;

    private ServiziEmail() {}

    public static void inizializza(String smtpHost, int smtpPort, String username, String password, boolean useSSL) {
        ServizioEmail servizioEmail = new ServizioEmail(smtpHost, smtpPort, username, password, useSSL);
        if (username.startsWith("convocazioni") && servizioConvocazioni == null) {
            servizioConvocazioni = servizioEmail;
        } else if (username.startsWith("report") && servizioReport == null) {
            servizioReport = servizioEmail;
        } else {
            throw new IllegalArgumentException("Non hai impostato un servizio email con questo username: " + username);
        }
    }

    public static ServizioEmail getServizioConvocazioni() {
        return servizioConvocazioni;
    }

    public static ServizioEmail getServizioReport() {
        return servizioReport;
    }
}