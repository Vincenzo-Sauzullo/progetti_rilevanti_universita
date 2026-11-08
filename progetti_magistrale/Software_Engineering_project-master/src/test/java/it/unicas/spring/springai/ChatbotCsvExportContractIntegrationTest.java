package it.unicas.spring.springai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
/**
 * Test di contratto per il flusso export CSV e modifica messaggi del chatbot.
 * Ruolo architetturale: garantisce che UI e script espongano i punti di integrazione
 * necessari al comportamento di restart/export conversazione.
 * Interagisce con risorse statiche (`index.html`, `app.js`, `style.css`).
 */
class ChatbotCsvExportContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    /**
     * Cosa fa: verifica presenza componenti HTML del modal CSV e aree feedback editing.
     * Perché esiste: protegge il contratto DOM usato dal JavaScript di export.
     * Chi lo chiama: JUnit Jupiter (`ChatbotCsvExportContractIntegrationTest.pagina_index_espone_controlli_csv_e_feedback_per_modifica_messaggi`).
     * Input/Output: nessun input applicativo; output `void` con assert su markup `index.html`.
     * Eccezioni/edge case: elementi mancanti o rinominati fanno fallire il test.
     * Side effects: nessuno.
     *
     * @throws Exception se la richiesta mock fallisce.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void pagina_index_espone_controlli_csv_e_feedback_per_modifica_messaggi() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"chatbotCsvModal\"")))
                .andExpect(content().string(containsString("id=\"chatbotCsvYesBtn\"")))
                .andExpect(content().string(containsString("id=\"chatbotCsvNoBtn\"")))
                .andExpect(content().string(containsString("id=\"chatbotCsvCancelBtn\"")))
                .andExpect(content().string(containsString("id=\"chatbotExportStatus\"")))
                .andExpect(content().string(containsString("id=\"chatbotEditStatus\"")))
                .andExpect(content().string(containsString("Salvare la chat in CSV prima del riavvio?")));
    }

    /**
     * Cosa fa: controlla che il JavaScript includa funzioni e header CSV previsti dal contratto.
     * Perché esiste: evita regressioni nel formato export consumato dall'utente finale.
     * Chi lo chiama: JUnit Jupiter (`ChatbotCsvExportContractIntegrationTest.javascript_export_csv_prevede_snapshot_header_temporali_e_download_file`).
     * Input/Output: input implicito GET `/js/app.js`; output `void` con assert su frammenti di script.
     * Eccezioni/edge case: funzioni rinominate o campi CSV rimossi rompono il test.
     * Side effects: nessuno.
     *
     * @throws Exception se la risorsa non è raggiungibile nel test context.
     */
    @Test
    void javascript_export_csv_prevede_snapshot_header_temporali_e_download_file() throws Exception {
        mockMvc.perform(get("/js/app.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("function buildChatbotConversationSnapshot()")))
                .andExpect(content().string(containsString("function downloadChatbotConversationCsv(snapshot)")))
                .andExpect(content().string(containsString("function buildChatbotConversationCsv(snapshot)")))
                .andExpect(content().string(containsString("['indice', 'ruolo', 'messaggio', 'data_ora_messaggio', 'data_ora_inizio_conversazione', 'data_ora_fine_conversazione', 'data_ora_export_csv']")))
                .andExpect(content().string(containsString("const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });")))
                .andExpect(content().string(containsString("window.URL.createObjectURL(blob);")))
                .andExpect(content().string(containsString("a.download = filename;")))
                .andExpect(content().string(containsString("replaceAll('\\n', '\\\\n');")))
                .andExpect(content().string(containsString("formatCsvDateTime(msg.ts)")));
    }

    /**
     * Cosa fa: verifica il percorso JS di restart conversazione con e senza download CSV.
     * Perché esiste: assicura che lo stato UI venga resettato e gli errori comunicati correttamente.
     * Chi lo chiama: JUnit Jupiter (`ChatbotCsvExportContractIntegrationTest.javascript_flusso_combinato_restart_csv_resetta_stato_e_gestisce_errori`).
     * Input/Output: input GET `/js/app.js`; output `void` con assert sulle istruzioni attese.
     * Eccezioni/edge case: messaggi/handler mancanti generano fallimento.
     * Side effects: nessuno.
     *
     * @throws Exception se il contenuto JS non può essere letto.
     */
    @Test
    void javascript_flusso_combinato_restart_csv_resetta_stato_e_gestisce_errori() throws Exception {
        mockMvc.perform(get("/js/app.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("function handleChatbotCsvRestartChoice(saveCsv)")))
                .andExpect(content().string(containsString("const snapshot = saveCsv ? buildChatbotConversationSnapshot() : null;")))
                .andExpect(content().string(containsString("restartChatbotConversationUi();")))
                .andExpect(content().string(containsString("const downloaded = downloadChatbotConversationCsv(snapshot);")))
                .andExpect(content().string(containsString("CSV scaricato correttamente (${count} messaggi esportati).")))
                .andExpect(content().string(containsString("Errore durante il download del CSV. Riprova.")))
                .andExpect(content().string(containsString("chatbotMessageEditingState = null;")))
                .andExpect(content().string(containsString("clearChatbotEditStatus();")));
    }

    /**
     * Cosa fa: controlla che il CSS includa regole responsive per modal CSV e editing messaggi.
     * Perché esiste: previene rotture visuali del nuovo flusso su dispositivi diversi.
     * Chi lo chiama: JUnit Jupiter (`ChatbotCsvExportContractIntegrationTest.css_prevede_layout_responsive_per_csv_modal_e_modifica_messaggi`).
     * Input/Output: input GET `/css/style.css`; output `void` con assert su selettori/regole.
     * Eccezioni/edge case: rimozione selettori CSS critici causa fail.
     * Side effects: nessuno.
     *
     * @throws Exception se risorsa CSS non servita.
     */
    @Test
    void css_prevede_layout_responsive_per_csv_modal_e_modifica_messaggi() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(".chatbot-csv-modal-content {")))
                .andExpect(content().string(containsString(".chatbot-csv-actions {")))
                .andExpect(content().string(containsString(".chat-message-edit-input {")))
                .andExpect(content().string(containsString("@media (max-width: 768px)")))
                .andExpect(content().string(containsString(".chatbot-csv-actions .btn {")))
                .andExpect(content().string(containsString(".chatbot-shell {")))
                .andExpect(content().string(containsString("min-height: calc(100dvh - 260px);")));
    }
}
