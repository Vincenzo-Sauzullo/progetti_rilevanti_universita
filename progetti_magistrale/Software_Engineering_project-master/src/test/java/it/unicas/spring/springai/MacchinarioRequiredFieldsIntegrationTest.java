package it.unicas.spring.springai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
/**
 * Test integrazione per validazione campi obbligatori del create macchinario.
 * Ruolo architetturale: assicura che il controller respinga payload privi del nome,
 * preservando coerenza minima del dominio.
 * Interagisce con endpoint `POST /api/macchinari`.
 */
class MacchinarioRequiredFieldsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    /**
     * Cosa fa: invia payload senza `nome` e verifica risposta `400 Bad Request`.
     * Perché esiste: impedisce creazione di record macchina senza identificativo umano.
     * Chi lo chiama: JUnit Jupiter (`MacchinarioRequiredFieldsIntegrationTest.create_macchinario_requires_nome`).
     * Input/Output: input JSON incompleto; output `void` con assert su status e messaggio errore.
     * Eccezioni/edge case: mancanza chiave `nome` deve essere trattata come invalidazione.
     * Side effects: nessuna persistenza DB attesa.
     *
     * @throws Exception se richiesta mock fallisce.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_macchinario_requires_nome() throws Exception {
        String payload = """
                {
                  "produttore": "ACME"
                }
                """;

        mockMvc.perform(post("/api/macchinari")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(containsString("nome")));
    }

    /**
     * Cosa fa: invia payload con `nome` composto solo da spazi e verifica rifiuto.
     * Perché esiste: copre edge case di validazione spesso sfuggente (`blank` invece di `null`).
     * Chi lo chiama: JUnit Jupiter (`MacchinarioRequiredFieldsIntegrationTest.create_macchinario_rejects_blank_nome`).
     * Input/Output: input JSON con nome blank; output `void` con assert 400.
     * Eccezioni/edge case: trimming lato controller non deve trasformare blank in valore valido.
     * Side effects: nessuna scrittura DB.
     *
     * @throws Exception se la chiamata di test fallisce.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_macchinario_rejects_blank_nome() throws Exception {
        String payload = """
                {
                  "nome": "   ",
                  "produttore": "ACME"
                }
                """;

        mockMvc.perform(post("/api/macchinari")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(containsString("nome")));
    }
}
