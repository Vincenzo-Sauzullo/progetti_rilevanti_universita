package it.unicas.spring.springai.controller;

import it.unicas.spring.springai.model.DocumentEntity;
import it.unicas.spring.springai.service.PdfIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
/**
 * Controller REST dedicato al ciclo di vita dei documenti PDF caricati in piattaforma.
 * Ruolo architetturale: espone endpoint HTTP per upload, consultazione, download e cancellazione,
 * delegando la logica applicativa a {@link PdfIngestionService}.
 * Interazioni principali: layer web (Spring MVC), layer service (indicizzazione/vector store)
 * e persistenza dei metadati documento.
 */
public class DocumentController {

    private final PdfIngestionService pdfIngestionService;

    /**
     * Cosa fa: valida il file ricevuto e avvia caricamento + indicizzazione del PDF.
     * Perché esiste: è il punto di ingresso per popolare il knowledge base usato dal RAG.
     * Chi lo chiama: endpoint `POST /api/documents/upload`; test principale:
     * `FileUploadUnsupportedFormatsIntegrationTest.documents_upload_rejects_non_pdf_files -> DocumentController.uploadPdf`.
     * Input/Output: riceve `MultipartFile`; restituisce metadati documento o payload errore JSON.
     * Eccezioni/edge case: file vuoto/non PDF => 400; `IOException` durante lettura => 500.
     * Side effects: scrittura DB + generazione embeddings/vector store tramite `PdfIngestionService.uploadAndProcessPdf`.
     *
     * @param file file inviato dal client nel campo multipart `file`.
     * @return risposta HTTP con dettagli del documento indicizzato oppure errore.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Il file è vuoto"));
        }

        if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Solo file PDF sono accettati"));
        }

        try {
            DocumentEntity document = pdfIngestionService.uploadAndProcessPdf(file);
            //log.info("File uploaded and processed: {}", document.getOriginalFileName());
            Map<String, Object> response = new HashMap<>();
            response.put("id", document.getId());
            response.put("fileName", document.getOriginalFileName());
            response.put("fileSize", document.getFileSize());
            response.put("uploadDate", document.getUploadDate());
            response.put("processed", document.isProcessed());
            response.put("chunkCount", document.getChunkCount());
            response.put("message", "File caricato e processato con successo");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante il caricamento del file: " + e.getMessage()));
        }
    }

    /**
     * Cosa fa: recupera l'elenco dei documenti caricati e lo trasforma in payload JSON semplificato.
     * Perché esiste: consente al frontend di mostrare archivio documentale e stato di processamento.
     * Chi lo chiama: endpoint `GET /api/documents`; non è stato trovato un chiamante diretto Java
     * oltre al routing HTTP di Spring (`DispatcherServlet -> DocumentController.getAllDocuments`).
     * Input/Output: nessun parametro; ritorna lista di mappe con metadati utili alla UI.
     * Eccezioni/edge case: lista vuota gestita naturalmente con array JSON vuoto.
     * Side effects: sola lettura DB attraverso `PdfIngestionService.getAllDocuments`.
     *
     * @return lista documenti ordinata per data upload.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDocuments() {
        List<DocumentEntity> documents = pdfIngestionService.getAllDocuments();

        List<Map<String, Object>> response = documents.stream().map(doc -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", doc.getId());
            map.put("fileName", doc.getOriginalFileName());
            map.put("fileSize", doc.getFileSize());
            map.put("uploadDate", doc.getUploadDate());
            map.put("processed", doc.isProcessed());
            map.put("chunkCount", doc.getChunkCount());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Cosa fa: restituisce il dettaglio di un documento identificato da ID.
     * Perché esiste: permette alla UI di aprire vista dettaglio con informazioni complete del file.
     * Chi lo chiama: endpoint `GET /api/documents/{id}`; chiamante Java diretto non trovato.
     * Input/Output: riceve ID documento; restituisce mappa dettaglio oppure 404 se assente.
     * Eccezioni/edge case: `RuntimeException` dal service (ID inesistente) mappata in Not Found.
     * Side effects: nessun effetto collaterale, sola lettura su repository.
     *
     * @param id identificativo del documento da recuperare.
     * @return dettaglio documento o risposta 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocument(@PathVariable Long id) {
        try {
            DocumentEntity document = pdfIngestionService.getDocument(id);

            Map<String, Object> response = new HashMap<>();
            response.put("id", document.getId());
            response.put("fileName", document.getOriginalFileName());
            response.put("fileSize", document.getFileSize());
            response.put("uploadDate", document.getUploadDate());
            response.put("processed", document.isProcessed());
            response.put("chunkCount", document.getChunkCount());
            response.put("description", document.getDescription());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cosa fa: invia al client il contenuto binario PDF del documento richiesto.
     * Perché esiste: abilita download locale del file originariamente caricato.
     * Chi lo chiama: endpoint `GET /api/documents/{id}/download`; chiamante Java diretto non trovato.
     * Input/Output: riceve ID; restituisce byte[] con header `Content-Disposition` per allegato.
     * Eccezioni/edge case: documento mancante => 404.
     * Side effects: nessuna modifica stato, solo lettura DB e trasferimento rete.
     *
     * @param id identificativo del documento da scaricare.
     * @return payload PDF con header HTTP di download o 404.
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        try {
            DocumentEntity document = pdfIngestionService.getDocument(id);

            HttpHeaders headers = new HttpHeaders(); //stai creando un nuovo oggetto che funge da contenitore per i metadati di una richiesta o di una risposta HTTP
            headers.setContentType(MediaType.APPLICATION_PDF); //controlla se siano in formato pdf i metadati
            headers.setContentDispositionFormData("attachment", document.getOriginalFileName()); //mi dice di scaricarlo e non di aprirlo come pagina e mi setta il nome del file

            return new ResponseEntity<>(document.getFileContent(), headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cosa fa: elimina il documento richiesto dal sistema.
     * Perché esiste: consente di rimuovere materiale non valido/obsoleto dall'archivio.
     * Chi lo chiama: endpoint `DELETE /api/documents/{id}`; chiamante Java diretto non trovato.
     * Input/Output: riceve ID documento; ritorna messaggio di conferma o 404.
     * Eccezioni/edge case: ID inesistente produce `RuntimeException` e risposta Not Found.
     * Side effects: cancellazione record DB tramite `PdfIngestionService.deleteDocument`
     * (nota nel service: rimozione embeddings da vector store ancora TODO).
     *
     * @param id identificativo del documento da cancellare.
     * @return esito operazione in formato JSON.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            pdfIngestionService.deleteDocument(id);
            return ResponseEntity.ok(Map.of("message", "Documento eliminato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
