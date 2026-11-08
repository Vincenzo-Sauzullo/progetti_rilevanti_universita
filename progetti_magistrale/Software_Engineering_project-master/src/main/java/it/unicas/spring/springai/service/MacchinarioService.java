package it.unicas.spring.springai.service;

import it.unicas.spring.springai.model.Macchinario;
import it.unicas.spring.springai.repository.MacchinarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Service di dominio per la gestione dei macchinari.
 * Ruolo architetturale: contiene operazioni CRUD e query di ricerca,
 * isolando il controller dai dettagli di persistenza (`MacchinarioRepository`).
 * Interagisce principalmente con `MacchinarioController`.
 */
public class MacchinarioService {

    /*
     * - Questo e' il CRUD dei macchinari: crea, legge, aggiorna, cancella.
     * - Non contiene AI: parla solo con il repository (database).
     * - Vantaggio: il controller resta pulito e la logica DB sta tutta qui.
     */
    /*
     * Cheat sheet super rapido (metodo per metodo):
     * - `create`: inserisce una nuova macchina in DB.
     * - `update`: trova la macchina e aggiorna i campi principali.
     * - `getById`: prende una macchina per ID (errore se non esiste).
     * - `getAll`: lista completa ordinata per data.
     * - `searchByName`: filtro per nome (contains, case-insensitive).
     * - `getByCategoria`: filtro per categoria.
     * - `delete`: elimina una macchina dopo controllo esistenza.
     *
     * Frase da interrogazione:
     * "Questo service e' puro dominio/CRUD, quindi niente AI e niente regole complesse."
     */
    /*
     * Mappa CRUD dettagliata (come leggerlo in modo veloce):
     *
     * CREATE:
     * - il controller passa una nuova entity `Macchinario`.
     * - il service scrive log applicativo.
     * - il repository persiste e assegna ID.
     *
     * READ BY ID:
     * - lookup diretto su `findById`.
     * - se assente, errore runtime con messaggio esplicito.
     *
     * READ LIST:
     * - `getAll` usa ordinamento per data creazione decrescente.
     * - `searchByName` usa contains case-insensitive.
     * - `getByCategoria` filtra con confronto case-insensitive.
     *
     * UPDATE:
     * - carica record esistente con `getById`.
     * - copia campo per campo i nuovi valori.
     * - salva nuovamente con repository.
     *
     * DELETE:
     * - verifica prima esistenza con `getById`.
     * - esegue cancellazione.
     * - logga il nome macchina eliminata.
     *
     * Nota di design:
     * - tutte le scritture stanno nel service, cosi' il controller resta sottile.
     */

    // Repository JPA per operazioni DB sui macchinari.
    private final MacchinarioRepository macchinarioRepository;

    /**
     * Cosa fa: salva un nuovo macchinario nel database.
     * Perché esiste: centralizza la creazione in un punto transazionale del layer service.
     * Chi lo chiama: `MacchinarioController.createMacchinario -> MacchinarioService.create`.
     * Input/Output: input entità con dati macchina; output entità persistita con ID assegnato.
     * Eccezioni/edge case: eventuali vincoli DB emergono come eccezioni runtime.
     * Side effects: inserimento record su DB e log applicativo.
     *
     * @param macchinario entità da creare.
     * @return entità salvata.
     */
    @Transactional
    public Macchinario create(Macchinario macchinario) {
        // Crea e salva una nuova macchina.
        log.info("Creating new macchinario: {}", macchinario.getNome());
        return macchinarioRepository.save(macchinario);
    }

    /**
     * Cosa fa: aggiorna i campi modificabili di un macchinario esistente.
     * Perché esiste: evita aggiornamenti parziali sparsi nei controller e mantiene coerenza di scrittura.
     * Chi lo chiama: `MacchinarioController.updateMacchinario -> MacchinarioService.update`.
     * Input/Output: input ID target + nuovi valori; output entità aggiornata e persistita.
     * Eccezioni/edge case: ID non trovato propagato da `getById`.
     * Side effects: update DB + log applicativo.
     *
     * @param id identificativo del record da aggiornare.
     * @param macchinarioDetails dati aggiornati richiesti.
     * @return entità aggiornata.
     */
    @Transactional
    public Macchinario update(Long id, Macchinario macchinarioDetails) {
        // Aggiorna i campi della macchina esistente.
        // Recuperiamo prima il record esistente: se non c'è, getById lancia eccezione.
        Macchinario macchinario = getById(id);

        // Copiamo solo i campi aggiornabili dal payload in ingresso.
        // E' come aggiornare una scheda anagrafica senza creare un nuovo record.
        macchinario.setNome(macchinarioDetails.getNome());
        macchinario.setDescrizione(macchinarioDetails.getDescrizione());
        macchinario.setProduttore(macchinarioDetails.getProduttore());
        macchinario.setModello(macchinarioDetails.getModello());
        macchinario.setNumeroSerie(macchinarioDetails.getNumeroSerie());
        macchinario.setAnnoProduzione(macchinarioDetails.getAnnoProduzione());
        macchinario.setCategoria(macchinarioDetails.getCategoria());
        macchinario.setSpecificheTecniche(macchinarioDetails.getSpecificheTecniche());

        log.info("Updated macchinario: {}", macchinario.getNome());
        return macchinarioRepository.save(macchinario);
    }

    /**
     * Cosa fa: recupera un macchinario tramite identificativo.
     * Perché esiste: fornisce lookup centralizzato con errore coerente in caso di assenza.
     * Chi lo chiama: `MacchinarioController.getMacchinario`, `MacchinarioController.downloadLatestAssessmentPdf`,
     * `MacchinarioService.update`, `MacchinarioService.delete`.
     * Input/Output: input ID; output entità `Macchinario`.
     * Eccezioni/edge case: se non presente lancia `RuntimeException` con messaggio esplicito.
     * Side effects: nessuno, sola lettura DB.
     *
     * @param id ID macchinario.
     * @return macchinario trovato.
     */
    public Macchinario getById(Long id) {
        // Legge una macchina per ID (se non c'e', errore).
        return macchinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Macchinario non trovato con ID: " + id));
    }

    /**
     * Cosa fa: ritorna tutti i macchinari ordinati per data creazione decrescente.
     * Perché esiste: serve alle schermate elenco e selezione macchina.
     * Chi lo chiama: `MacchinarioController.getAllMacchinari -> MacchinarioService.getAll`.
     * Input/Output: nessun input; output lista entità.
     * Eccezioni/edge case: lista vuota se non esistono record.
     * Side effects: nessuno.
     *
     * @return lista macchinari.
     */
    public List<Macchinario> getAll() {
        // Elenco completo macchinari.
        return macchinarioRepository.findAllByOrderByDataCreazioneDesc();
    }

    /**
     * Cosa fa: cerca macchinari il cui nome contiene la stringa richiesta.
     * Perché esiste: abilita filtro rapido lato UI.
     * Chi lo chiama: `MacchinarioController.searchMacchinari -> MacchinarioService.searchByName`.
     * Input/Output: input stringa nome; output lista di match.
     * Eccezioni/edge case: input vuoto delegato al comportamento repository.
     * Side effects: nessuno.
     *
     * @param nome testo da cercare.
     * @return risultati filtrati.
     */
    public List<Macchinario> searchByName(String nome) {
        // Ricerca per nome (contiene testo, ignorando maiuscole/minuscole).
        return macchinarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Cosa fa: filtra i macchinari per categoria ignorando maiuscole/minuscole.
     * Perché esiste: supporta navigazione tematica e segmentazione inventario.
     * Chi lo chiama: `MacchinarioController.getByCategoria -> MacchinarioService.getByCategoria`.
     * Input/Output: input categoria; output lista risultati.
     * Eccezioni/edge case: nessun match => lista vuota.
     * Side effects: nessuno.
     *
     * @param categoria categoria richiesta.
     * @return macchinari della categoria.
     */
    public List<Macchinario> getByCategoria(String categoria) {
        // Filtra per categoria.
        return macchinarioRepository.findByCategoriaIgnoreCase(categoria);
    }

    /**
     * Cosa fa: elimina un macchinario esistente.
     * Perché esiste: centralizza la cancellazione con controllo preventivo esistenza.
     * Chi lo chiama: `MacchinarioController.deleteMacchinario -> MacchinarioService.delete`.
     * Input/Output: input ID; output `void` (esito dedotto da assenza eccezioni).
     * Eccezioni/edge case: ID assente => eccezione propagata da `getById`.
     * Side effects: delete su DB e scrittura log.
     *
     * @param id ID del macchinario da cancellare.
     */
    @Transactional
    public void delete(Long id) {
        // Cancella una macchina esistente.
        // Controllo esistenza prima della delete: evita "cancellazioni fantasma".
        Macchinario macchinario = getById(id);
        macchinarioRepository.delete(macchinario);
        log.info("Deleted macchinario: {}", macchinario.getNome());
    }
}
