package controller.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class ServizioEmail {
    private final String username;
    private final Session session;

    ServizioEmail(String smtpHost, int smtpPort, String username, String password, boolean useSSL) {
        this.username = username;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.smtp.ssl.trust", smtpHost);

        if (useSSL) {
            props.put("mail.smtp.ssl.enable", "true");   // SSL diretto (porta 465)
        } else {
            props.put("mail.smtp.starttls.enable", "true"); // STARTTLS (porta 587)
        }

        this.session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

    }

    public void inviaEmail(String to, String subject, String text) throws MessagingException {
        Message msg = inizializzaMessaggio(to, subject);
        msg.setText(text);
        Transport.send(msg);
    }

    public void inviaEmailHtml(String to, String subject, String html) throws MessagingException {
        Message msg = inizializzaMessaggio(to, subject);
        msg.setContent(html, "text/html; charset=utf-8");
        Transport.send(msg);
    }

    private Message inizializzaMessaggio(String to, String subject) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        return message;
    }
}