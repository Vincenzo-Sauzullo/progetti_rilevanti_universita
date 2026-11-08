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
 * Test di contratto dell'interfaccia chatbot (HTML/CSS/JS).
 * Ruolo architetturale: protegge i selettori e le funzioni front-end attese dal flusso chat,
 * senza entrare nella logica interna JavaScript.
 * Interagisce con risorse statiche servite da Spring MVC tramite `MockMvc`.
 */
class ChatbotInterfaceContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    /**
     * Cosa fa: verifica che la pagina index contenga i componenti DOM minimi del chatbot.
     * Perché esiste: evita regressioni UI che romperebbero invio/ricezione messaggi lato browser.
     * Chi lo chiama: JUnit Jupiter (`ChatbotInterfaceContractIntegrationTest.pagina_index_contiene_tutti_i_componenti_chat_essenziali`).
     * Input/Output: nessun input esplicito; output `void` con assert su risposta HTTP/HTML.
     * Eccezioni/edge case: se un ID/attributo manca, il test fallisce.
     * Side effects: nessun side effect persistente; sola lettura della risorsa `/index.html`.
     *
     * @throws Exception se la richiesta MockMvc non può essere eseguita.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void pagina_index_contiene_tutti_i_componenti_chat_essenziali() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"chatbot\"")))
                .andExpect(content().string(containsString("id=\"chatbotMessages\"")))
                .andExpect(content().string(containsString("id=\"chatbotComposer\"")))
                .andExpect(content().string(containsString("id=\"chatbotInput\"")))
                .andExpect(content().string(containsString("id=\"chatbotSendBtn\"")))
                .andExpect(content().string(containsString("id=\"chatbotNewChatBtn\"")))
                .andExpect(content().string(containsString("aria-live=\"polite\"")));
    }

    /**
     * Cosa fa: controlla che `app.js` includa funzioni e istruzioni critiche del flusso chatbot.
     * Perché esiste: garantisce il contratto funzionale del client JavaScript (submit/render/stato input).
     * Chi lo chiama: JUnit Jupiter (`ChatbotInterfaceContractIntegrationTest.javascript_chatbot_gestisce_render_e_flusso_invio_in_modo_affidabile`).
     * Input/Output: input implicito GET `/js/app.js`; output `void` con assert su contenuto script.
     * Eccezioni/edge case: assenza delle stringhe attese comporta fallimento test.
     * Side effects: nessuno, sola lettura file statico.
     *
     * @throws Exception se la chiamata HTTP di test fallisce.
     */
    @Test
    void javascript_chatbot_gestisce_render_e_flusso_invio_in_modo_affidabile() throws Exception {
        mockMvc.perform(get("/js/app.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("function submitChatbotChat()")))
                .andExpect(content().string(containsString("chatbotInput.disabled = true;")))
                .andExpect(content().string(containsString("chatbotInput.disabled = false;")))
                .andExpect(content().string(containsString("if (chatbotSendBtn) chatbotSendBtn.disabled = true;")))
                .andExpect(content().string(containsString("if (chatbotSendBtn) chatbotSendBtn.disabled = false;")))
                .andExpect(content().string(containsString("chatbotMessages.scrollTop = chatbotMessages.scrollHeight;")))
                .andExpect(content().string(containsString("escapeHtml(text).replaceAll('\\n', '<br>')")))
                .andExpect(content().string(containsString("renderChatbotMessageSourcesHtml")));
    }

    /**
     * Cosa fa: verifica la presenza delle regole CSS base e responsive della shell chatbot.
     * Perché esiste: protegge layout e usabilità della chat su desktop/mobile.
     * Chi lo chiama: JUnit Jupiter (`ChatbotInterfaceContractIntegrationTest.css_chatbot_ha_regole_layout_chiare_e_responsive`).
     * Input/Output: input implicito GET `/css/style.css`; output `void` con assert stringhe CSS.
     * Eccezioni/edge case: rimozione o rinomina classi CSS attese => test rosso.
     * Side effects: nessuno.
     *
     * @throws Exception se richiesta mock non eseguibile.
     */
    @Test
    void css_chatbot_ha_regole_layout_chiare_e_responsive() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(".chatbot-shell {")))
                .andExpect(content().string(containsString(".chatbot-messages {")))
                .andExpect(content().string(containsString(".chatbot-composer {")))
                .andExpect(content().string(containsString(".chatbot-shell .chat-message--assistant .chat-bubble {")))
                .andExpect(content().string(containsString("@media (max-width: 768px)")))
                .andExpect(content().string(containsString("min-height: calc(100dvh - 260px);")));
    }
}
