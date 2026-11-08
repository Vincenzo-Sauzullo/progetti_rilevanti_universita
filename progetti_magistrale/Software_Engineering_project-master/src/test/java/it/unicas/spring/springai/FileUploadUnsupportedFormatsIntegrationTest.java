package it.unicas.spring.springai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
/**
 * Test integrazione per validazione formati upload non supportati.
 * Ruolo architetturale: protegge i controlli input dei controller documenti/manuali,
 * verificando che vengano accettati solo PDF.
 * Interagisce con endpoint multipart via `MockMvc`.
 */
class FileUploadUnsupportedFormatsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    /**
     * Cosa fa: invia un file `.txt` all'endpoint upload documenti e verifica rifiuto 400.
     * Perché esiste: impedisce ingestione di formati non gestiti dalla pipeline PDF.
     * Chi lo chiama: JUnit Jupiter (`FileUploadUnsupportedFormatsIntegrationTest.documents_upload_rejects_non_pdf_files`).
     * Input/Output: input multipart testuale; output `void` con assert su status e messaggio errore.
     * Eccezioni/edge case: content-type diverso da PDF deve essere sempre respinto.
     * Side effects: nessuna persistenza attesa in caso di errore.
     *
     * @throws Exception se richiesta mock fallisce.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void documents_upload_rejects_non_pdf_files() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "note.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "ciao".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Solo file PDF sono accettati"));
    }

    /**
     * Cosa fa: verifica che l'endpoint estrazione manuale rifiuti file non PDF anche senza content-type.
     * Perché esiste: copre edge case reale di upload client che non valorizza MIME type.
     * Chi lo chiama: JUnit Jupiter (`FileUploadUnsupportedFormatsIntegrationTest.manual_extract_rejects_non_pdf_files_even_without_content_type`).
     * Input/Output: input multipart `.txt` con content-type nullo; output `void` con assert 400.
     * Eccezioni/edge case: fallback sulla estensione file deve comunque bloccare l'upload.
     * Side effects: nessuno (nessuna chiamata LLM o salvataggio).
     *
     * @throws Exception se la richiesta di test fallisce.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void manual_extract_rejects_non_pdf_files_even_without_content_type() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "note.txt",
                null,
                "ciao".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/macchinari/manual/extract")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Solo file PDF sono accettati"));
    }
}
