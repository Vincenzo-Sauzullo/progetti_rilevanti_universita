package it.unicas.spring.springai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
/**
 * Smoke test del contesto Spring Boot.
 * Ruolo architetturale: verifica che il wiring base dell'applicazione si avvii nel profilo `test`
 * prima di eseguire test funzionali più specifici.
 * Interagisce con il container Spring Test e con l'intera configurazione applicativa.
 */
class SpringaiApplicationTests {

    /**
     * Cosa fa: verifica che il contesto Spring venga caricato senza errori.
     * Perché esiste: intercetta regressioni di configurazione globali (bean mancanti, proprietà errate).
     * Chi lo chiama: JUnit Jupiter (`SpringaiApplicationTests.contextLoads`).
     * Input/Output: nessun input, nessun output (`void`); il test passa se non vengono lanciate eccezioni.
     * Eccezioni/edge case: qualsiasi errore di bootstrap fallisce il test.
     * Side effects: avvio del contesto Spring nel runtime di test.
     */
    @Test
    void contextLoads() {
        // Test will pass if context loads successfully
    }

}
