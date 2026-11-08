package it.unicas.spring.springai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller MVC per il routing delle pagine web (non API JSON).
 * Ruolo architetturale: gestisce entrypoint UI e login, lasciando alle view
 * (Thymeleaf/static) la resa del contenuto.
 * Interagisce con il view resolver di Spring e con la configurazione di sicurezza
 * che protegge le pagine applicative.
 */
@Controller
public class WebController {

    /**
     * Cosa fa: intercetta la root `/` e reindirizza alla pagina principale dell'app.
     * Perché esiste: centralizza l'entrypoint UI evitando URL vuoto senza contenuto.
     * Chi lo chiama: endpoint `GET /`; flusso verificato in
     * `LoginFlowIntegrationTest.user_can_login_and_reach_final_results_flow -> WebController.index`.
     * Input/Output: nessun input; output stringa di redirect (`redirect:/index.html`).
     * Eccezioni/edge case: nessuna gestione specifica, comportamento deterministico.
     * Side effects: redirezione HTTP lato framework.
     *
     * @return istruzione di redirect verso pagina iniziale.
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }

    /**
     * Cosa fa: restituisce la vista logica di login (`login`).
     * Perché esiste: separa il percorso di autenticazione dall'entrypoint principale.
     * Chi lo chiama: endpoint `GET /login`; nel codice test:
     * `LoginFlowIntegrationTest.user_can_login_and_reach_final_results_flow -> WebController.login`.
     * Input/Output: nessun parametro; output nome vista risolta da Thymeleaf.
     * Eccezioni/edge case: nessuna eccezione applicativa gestita localmente.
     * Side effects: nessuno stato applicativo; solo risoluzione template.
     *
     * @return nome logico della pagina login.
     */
    @GetMapping("/login")
    public String login() {
        return "login"; // è scelto login.html in automatico grazie al View Resolver
    }
}
