package it.unicas.spring.springai.service;

import it.unicas.spring.springai.model.DocumentEntity;
import it.unicas.spring.springai.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Service responsabile dell'ingestion dei PDF e della loro indicizzazione vettoriale.
 * Ruolo architetturale: collega upload documento, persistenza metadati e popolamento
 * del `VectorStore` usato dalla ricerca semantica RAG.
 * Interagisce con `DocumentController`, `DocumentRepository` e `VectorStore`.
 */
public class PdfIngestionService {

    /*
     * - Carica PDF, lo salva nel DB e poi lo rende "ricercabile" nel vector store.
     * - Flusso: upload -> parse pagine -> split in chunk -> metadati -> embeddings nel vector DB.
     * - Da qui nasce la base dati usata da RAG per chatbot e assessment.
     */
    /*
     * Cheat sheet super rapido (da raccontare all'orale):
     * - `vectorStore`: posto dove salvo i chunk vettoriali.
     * - `documentRepository`: posto dove salvo il file originale e i metadati.
     *
     * Metodi:
     * - `uploadAndProcessPdf`: salva file in tabella e lancia indicizzazione.
     * - `processAndStoreEmbeddings`: parse PDF, split chunk, arricchisce metadata, add su vector store.
     * - `extractPageNumber`: normalizza numero pagina da metadata eterogenei.
     * - `getAllDocuments`: elenco file caricati.
     * - `getDocument`: dettaglio file per ID.
     * - `deleteDocument`: cancella file (nota: TODO rimozione embeddings).
     *
     * Frase da interrogazione:
     * "Questo service costruisce la base conoscenza del sistema RAG."
     */
    /*
     * Pipeline ingestion completa (sequenza runtime):
     *
     * Step 1 - upload file:
     * - arriva `MultipartFile` dal controller.
     * - si creano metadati applicativi (`DocumentEntity`) e si salva nel DB.
     * - `processed=false` finche' embedding non e' completato.
     *
     * Step 2 - lettura PDF:
     * - dal contenuto binario si crea `ByteArrayResource`.
     * - `PagePdfDocumentReader` produce documenti/pagine testuali.
     *
     * Step 3 - chunking:
     * - `TokenTextSplitter` divide il testo in blocchi piu' piccoli.
     * - ogni chunk viene arricchito con metadata (`documentId`, `fileName`, `chunkIndex`, pagina).
     *
     * Step 4 - indicizzazione vettoriale:
     * - `vectorStore.add(enrichedChunks)` salva gli embeddings.
     * - da qui in poi il contenuto diventa ricercabile semanticamente.
     *
     * Step 5 - finalizzazione:
     * - update entity: `processed=true`, `chunkCount=<numero chunk>`.
     * - persist aggiornamento su `documentRepository`.
     *
     * Step 6 - failure handling:
     * - qualunque errore parsing/embedding viene loggato.
     * - il service lancia `RuntimeException` per segnalare fallimento al layer superiore.
     *
     * Nota importante:
     * - cancellazione documento oggi rimuove solo DB applicativo (TODO embeddings).
     */

    // Dove salvo chunk indicizzati (parte "intelligente" per retrieval semantico).
    private final VectorStore vectorStore;
    // Dove salvo il file originale e i metadati lato applicazione.
    private final DocumentRepository documentRepository;

    /**
     * Cosa fa: salva il file PDF come `DocumentEntity` e avvia la pipeline di embedding.
     * Perché esiste: garantisce che ogni documento caricato sia subito disponibile per retrieval AI.
     * Chi lo chiama: `DocumentController.uploadPdf -> PdfIngestionService.uploadAndProcessPdf`.
     * Input/Output: input `MultipartFile`; output entità documento con metadata aggiornati.
     * Eccezioni/edge case: `IOException` se non è possibile leggere i byte del file.
     * Side effects: inserimento DB + chiamata a `processAndStoreEmbeddings` (scrittura vector store).
     *
     * @param file file PDF caricato dall'utente.
     * @return documento persistito.
     * @throws IOException se la lettura del contenuto file fallisce.
     */
    //salvo il file e lo indicizzo
    @Transactional
    public DocumentEntity uploadAndProcessPdf(MultipartFile file) throws IOException {
        // In pratica: salvo il file e subito dopo lo indicizzo per la ricerca semantica.
        log.info("Processing PDF file: {}", file.getOriginalFilename());

        // Salva il documento nel database
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileName(UUID.randomUUID().toString() + ".pdf");
        documentEntity.setOriginalFileName(file.getOriginalFilename());
        documentEntity.setFileSize(file.getSize());
        documentEntity.setContentType(file.getContentType());
        documentEntity.setFileContent(file.getBytes());
        documentEntity.setProcessed(false);

        documentEntity = documentRepository.save(documentEntity);

        // Processa il PDF per il vector store
        processAndStoreEmbeddings(documentEntity);

        return documentEntity;
    }

    /**
     * Cosa fa: legge il PDF, lo spezza in chunk, arricchisce metadati e salva nel vector store.
     * Perché esiste: rende interrogabile semanticamente il contenuto documento tramite RAG.
     * Chi lo chiama: `PdfIngestionService.uploadAndProcessPdf -> PdfIngestionService.processAndStoreEmbeddings`.
     * Input/Output: input `DocumentEntity`; output `void` (stato documento aggiornato nel DB).
     * Eccezioni/edge case: errori parser/vector store sono convertiti in `RuntimeException`.
     * Side effects: lettura bytes PDF, scrittura embeddings su vector store, update record DB, log.
     *
     * @param documentEntity documento già salvato da processare.
     */
    @Transactional
    public void processAndStoreEmbeddings(DocumentEntity documentEntity) {
        // In pratica: da PDF grezzo a chunk "ricercabili" nel vector store.
        log.info("Generating embeddings for document: {}", documentEntity.getOriginalFileName());

        try {
            // 1) Trasformiamo i byte salvati nel DB in una "risorsa file" leggibile dal parser PDF.
            // Crea una risorsa dal contenuto del file
            ByteArrayResource resource = new ByteArrayResource(documentEntity.getFileContent()) {
                /**
                 * Cosa fa: espone un nome file leggibile al reader PDF.
                 * Perché esiste: alcuni reader usano il filename per tracciamento/sorgente.
                 * Chi lo chiama: `PagePdfDocumentReader` durante parsing del `ByteArrayResource`.
                 * Input/Output: nessun input; ritorna filename originale.
                 * Eccezioni/edge case: nessuna.
                 * Side effects: nessuno.
                 *
                 * @return nome file originale associato alla risorsa in memoria.
                 */
                @Override
                public String getFilename() {
                    return documentEntity.getOriginalFileName();
                }
            };

            // 2) Leggiamo il PDF pagina per pagina.
            // Leggi il PDF
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource);
            List<Document> documents = pdfReader.get();

            // 3) Spezziamo il testo in chunk più piccoli.
            // E' come tagliare un capitolo lungo in paragrafi più facili da ritrovare.
            // Splitter per dividere in chunks
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.apply(documents);

            // 4) Arricchiamo ogni chunk con metadati utili per trace e debug.
            // Aggiungi metadata a ogni chunk (Document è immutabile: crea una copia arricchita)
            List<Document> enrichedChunks = new ArrayList<>(chunks.size());
            for (int i = 0; i < chunks.size(); i++) {
                Document chunk = chunks.get(i);
                Map<String, Object> metadata = new HashMap<>(chunk.getMetadata());
                metadata.put("documentId", documentEntity.getId());
                metadata.put("fileName", documentEntity.getOriginalFileName());
                metadata.put("source", "uploaded_pdf");
                metadata.put("chunkIndex", i + 1);

                // Se il parser non ha una pagina pulita (null o formato strano),
                // non blocchiamo il flusso: il chunk resta valido anche senza pagina.
                Integer page = extractPageNumber(metadata);
                if (page != null) {
                    metadata.put("page", page);
                }

                enrichedChunks.add(chunk.mutate().metadata(metadata).build());
            }

            // Salva nel vector store
            log.info("Adding {} chunks to vector store for document: {}", enrichedChunks.size(), documentEntity.getOriginalFileName());
            vectorStore.add(enrichedChunks);

            // 5) Solo dopo il salvataggio nel vector store marchiamo il documento come "processed".
            log.info("Update document status to processed for: {}", documentEntity.getOriginalFileName());
            // Aggiorna lo stato del documento
            documentEntity.setProcessed(true);
            documentEntity.setChunkCount(enrichedChunks.size());
            documentRepository.save(documentEntity);

            log.info("Successfully processed {} chunks for document: {}",
                    enrichedChunks.size(), documentEntity.getOriginalFileName());

        } catch (Exception e) {
            log.error("Error processing PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Cosa fa: normalizza/estrae il numero pagina da metadati eterogenei del chunk.
     * Perché esiste: diversi reader usano chiavi o formati differenti per la pagina.
     * Chi lo chiama: `PdfIngestionService.processAndStoreEmbeddings -> PdfIngestionService.extractPageNumber`.
     * Input/Output: input mappa metadata; output numero pagina o `null`.
     * Eccezioni/edge case: valori non numerici o non parsabili ritornano `null` senza interrompere il flusso.
     * Side effects: nessuno.
     *
     * @param metadata metadati chunk da interpretare.
     * @return pagina estratta o `null`.
     */
    private Integer extractPageNumber(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) return null;

        // Alcuni reader usano nomi diversi per la stessa informazione.
        // Cerchiamo la pagina in più chiavi, in ordine.
        Object page = metadata.get("page");
        if (page == null) page = metadata.get("page_number");
        if (page == null) page = metadata.get("pageNumber");
        if (page == null) page = metadata.get("page-number");

        if (page instanceof Number number) {
            return number.intValue();
        }

        if (page instanceof String str) {
            // Se arriva una stringa tipo "pag. 12", estraiamo solo le cifre.
            String digits = str.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) return null;
            try {
                return Integer.parseInt(digits);
            } catch (NumberFormatException e) {
                // JSON/metadato malformato: ritorniamo null e continuiamo senza interrompere l'upload.
                return null;
            }
        }

        return null;
    }

    /**
     * Cosa fa: recupera tutti i documenti caricati ordinati per data upload.
     * Perché esiste: supporta endpoint elenco documenti.
     * Chi lo chiama: `DocumentController.getAllDocuments -> PdfIngestionService.getAllDocuments`.
     * Input/Output: nessun input; output lista `DocumentEntity`.
     * Eccezioni/edge case: lista vuota se non ci sono documenti.
     * Side effects: nessuno.
     *
     * @return elenco documenti ordinato dal più recente.
     */
    public List<DocumentEntity> getAllDocuments() {
        // Lista di tutti i documenti caricati.
        return documentRepository.findAllByOrderByUploadDateDesc();
    }

    /**
     * Cosa fa: recupera un singolo documento per ID.
     * Perché esiste: base comune per dettaglio/download/cancellazione.
     * Chi lo chiama: `DocumentController.getDocument`, `DocumentController.downloadDocument`,
     * `PdfIngestionService.deleteDocument`.
     * Input/Output: input ID documento; output entità trovata.
     * Eccezioni/edge case: ID assente => `RuntimeException`.
     * Side effects: nessuno, sola lettura DB.
     *
     * @param id identificativo documento.
     * @return documento trovato.
     */
    public DocumentEntity getDocument(Long id) {
        // Recupera un documento per ID.
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    /**
     * Cosa fa: elimina il documento dal database.
     * Perché esiste: consente rimozione amministrativa dei file caricati.
     * Chi lo chiama: `DocumentController.deleteDocument -> PdfIngestionService.deleteDocument`.
     * Input/Output: input ID; output `void`.
     * Eccezioni/edge case: se documento non esiste, eccezione propagata da `getDocument`.
     * Side effects: delete DB e log; rimozione embeddings non ancora implementata (TODO esplicito).
     *
     * @param id identificativo documento da rimuovere.
     */
    @Transactional
    public void deleteDocument(Long id) {
        // Elimina un documento dal DB.
        DocumentEntity document = getDocument(id);
        // TODO: Rimuovere anche gli embeddings dal vector store
        documentRepository.delete(document);
        log.info("Deleted document: {}", document.getOriginalFileName());
    }
}
