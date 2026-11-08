package it.unicas.spring.springai.service;

import it.unicas.spring.springai.model.User;
import it.unicas.spring.springai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/**
 * Adapter tra modello utente applicativo e contratto Spring Security `UserDetailsService`.
 * Ruolo architetturale: fornisce al framework di sicurezza i dati utente/ruoli necessari
 * durante autenticazione form login.
 * Interagisce con `UserRepository` (lettura credenziali) e con il filtro di autenticazione Spring.
 */
public class CustomUserDetailsService implements UserDetailsService {

    /*
     * - Spring Security chiede: "questo username esiste? quali ruoli ha?".
     * - Questo service prende l'utente dal DB e lo traduce nel formato capito da Spring.
     * - Se l'utente non esiste, blocca il login con un errore chiaro.
     */
    /*
     * Cheat sheet super rapido:
     * - Campo chiave: `userRepository` (legge utenti dal database).
     * - Metodo unico importante: `loadUserByUsername`.
     * - Dentro il metodo succedono solo 2 cose:
     *   1) lookup utente da username;
     *   2) mapping in `UserDetails` con ruoli `ROLE_...`.
     *
     * Frase da interrogazione:
     * "E' l'adattatore tra il mio modello User e Spring Security."
     */
    /*
     * Mappa di esecuzione dettagliata (in ordine reale):
     *
     * Fase 1 - ingresso richiesta autenticazione:
     * - Spring Security riceve username/password da form login.
     * - L'authentication provider chiama questo service con lo username.
     *
     * Fase 2 - ricerca utente:
     * - query su `userRepository.findByUsername(username)`.
     * - se non esiste record: `UsernameNotFoundException`.
     *
     * Fase 3 - adattamento in UserDetails:
     * - username applicativo -> username Spring.
     * - password hash applicativa -> password Spring (non in chiaro).
     * - flag `enabled` applicativo -> flag account abilitato.
     * - flag account non scaduto/non bloccato/credenziali non scadute -> `true`.
     *
     * Fase 4 - mapping ruoli:
     * - ogni ruolo applicativo viene convertito in `ROLE_<RUOLO>`.
     * - il prefisso `ROLE_` e' il formato atteso da Spring.
     *
     * Fase 5 - ritorno al framework:
     * - Spring continua il controllo password e autorizzazioni.
     * - in caso successo, popola il SecurityContext.
     */

    // Accesso al database utenti.
    private final UserRepository userRepository;

    /**
     * Cosa fa: carica l'utente dal database e lo converte in `UserDetails` compatibile Spring Security.
     * Perché esiste: Spring invoca questo metodo per verificare credenziali e autorizzazioni.
     * Chi lo chiama: `DaoAuthenticationProvider.retrieveUser -> CustomUserDetailsService.loadUserByUsername`
     * durante login (`/login`).
     * Input/Output: input username; output oggetto `UserDetails` con password hash, flag account e ruoli prefissati `ROLE_`.
     * Eccezioni/edge case: username assente su DB => `UsernameNotFoundException`.
     * Side effects: nessuna modifica stato, sola lettura repository.
     *
     * @param username username richiesto dal processo di autenticazione.
     * @return profilo sicurezza costruito dall'utente persistito.
     * @throws UsernameNotFoundException se l'utente non esiste.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In parole povere: cerco utente nel DB e lo converto nel formato richiesto da Spring Security.
        // Primo passo: cerchiamo l'utente nel DB come quando controlli se un nome
        // è presente nella lista invitati. Se non c'è, il login deve fermarsi subito.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));

        // Secondo passo: traduciamo l'utente nel formato capito da Spring Security.
        // Il prefisso ROLE_ serve a far riconoscere correttamente i permessi.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList())
        );
    }
}
