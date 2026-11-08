package it.unicas.spring.springai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
/**
 * Test integrazione del comportamento generale del chatbot RAG.
 * Ruolo architetturale: valida casi base (risposta con fonti, fallback senza info,
 * conversazioni lunghe con history troncata) lato endpoint.
 * Interagisce con `ChatbotController`/`ChatbotService`, mockando `VectorStore` e `ChatClient`.
 */
class ChatbotGeneralIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    private ChatClient chatClient;

    /**
     * Cosa fa: prepara mock deep-stub del client chat prima di ogni scenario.
     * Perché esiste: disaccoppia i test da provider LLM esterno.
     * Chi lo chiama: JUnit Jupiter lifecycle (`@BeforeEach` -> `ChatbotGeneralIntegrationTest.setUp`).
     * Input/Output: nessun input/output applicativo.
     * Eccezioni/edge case: eventuali errori mocking causano fallimento setup.
     * Side effects: reset/configurazione mock condivisi nel test context.
     */
    @BeforeEach
    void setUp() {
        chatClient = mock(ChatClient.class, Answers.RETURNS_DEEP_STUBS);
        when(chatClientBuilder.build()).thenReturn(chatClient);
    }

    /**
     * Cosa fa: verifica che a una domanda generale il chatbot risponda citando fonti pertinenti.
     * Perché esiste: copre il caso d'uso primario "domanda + evidenza documentale".
     * Chi lo chiama: JUnit Jupiter (`ChatbotGeneralIntegrationTest.chatbot_risponde_alle_domande_generiche_con_fonti_pertinenti`).
     * Input/Output: input richiesta POST chat con domanda; output `void` con assert su answer/sources.
     * Eccezioni/edge case: fallisce se mancano fileName/page/reference nella risposta.
     * Side effects: invoca endpoint chat con retriever e LLM mock.
     *
     * @throws Exception se richiesta MockMvc o setup mock falliscono.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void chatbot_risponde_alle_domande_generiche_con_fonti_pertinenti() throws Exception {
        Document chunk = new Document(
                "La marcatura CE e la dichiarazione UE di conformita sono obbligatorie prima della messa in servizio.",
                Map.of("fileName", "regolamento-macchine.pdf", "page", 12)
        );
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(chunk));
        when(chatClient.prompt()
                .system(anyString())
                .user(anyString())
                .call()
                .content()).thenReturn("""
                {"answer":"La marcatura CE è obbligatoria prima della messa in servizio.","chunkIds":[1]}
                """);

        mockMvc.perform(post("/api/chatbot/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question": "La marcatura CE è obbligatoria?"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(containsString("marcatura CE")))
                .andExpect(jsonPath("$.sources[0].fileName").value("regolamento-macchine.pdf"))
                .andExpect(jsonPath("$.sources[0].page").value(12))
                .andExpect(jsonPath("$.sources[0].reference").value("regolamento-macchine.pdf, Pag. 12"))
                .andExpect(jsonPath("$.sources[0].chunk").value(containsString("marcatura CE")));
    }

    /**
     * Cosa fa: verifica il fallback testuale quando il retrieval non trova documenti utili.
     * Perché esiste: evita risposte inventate in assenza di evidenze.
     * Chi lo chiama: JUnit Jupiter (`ChatbotGeneralIntegrationTest.chatbot_gestisce_assenza_informazioni_con_messaggio_chiaro`).
     * Input/Output: input domanda su caso non coperto; output `void` con assert su messaggio e lista fonti vuota.
     * Eccezioni/edge case: vector store vuoto deve produrre risposta sicura, non errore.
     * Side effects: nessuna chiamata LLM attesa oltre il flusso gestito dal service.
     *
     * @throws Exception se invocazione endpoint fallisce.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void chatbot_gestisce_assenza_informazioni_con_messaggio_chiaro() throws Exception {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

        mockMvc.perform(post("/api/chatbot/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "question": "Qual è la coppia di serraggio del modello ZX-900?"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(startsWith("Non ho trovato questa informazione")))
                .andExpect(jsonPath("$.sources.length()").value(0));
    }

    /**
     * Cosa fa: simula 25 turni chat e verifica il trimming history negli ultimi 10 messaggi.
     * Perché esiste: controlla stabilità e limiti contesto su conversazioni lunghe.
     * Chi lo chiama: JUnit Jupiter (`ChatbotGeneralIntegrationTest.chatbot_regge_conversazioni_prolungate_senza_incoerenze`).
     * Input/Output: input serie di richieste iterative; output `void` con assert su prompt catturato e risposta.
     * Eccezioni/edge case: assicura che i turni iniziali escano dal prompt finale (niente crescita illimitata).
     * Side effects: più chiamate endpoint e aggiornamento history in memoria test.
     *
     * @throws Exception se una delle iterazioni MockMvc fallisce.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void chatbot_regge_conversazioni_prolungate_senza_incoerenze() throws Exception {
        Document chunk = new Document(
                "Il DVR e il manuale operativo devono essere disponibili e aggiornati.",
                Map.of("fileName", "manuale-sicurezza.pdf", "page", 4)
        );
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(chunk));

        ArgumentCaptor<String> userPromptCaptor = ArgumentCaptor.forClass(String.class);
        AtomicInteger counter = new AtomicInteger(0);
        when(chatClient.prompt()
                .system(anyString())
                .user(userPromptCaptor.capture())
                .call()
                .content()).thenAnswer(invocation -> {
            int turn = counter.incrementAndGet();
            return "{\"answer\":\"Risposta turno " + turn + "\",\"chunkIds\":[1]}";
        });

        List<Map<String, String>> history = new ArrayList<>();

        for (int turn = 1; turn <= 25; turn++) {
            String question = "Domanda turno " + turn + ": quali documenti servono?";
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("question", question);
            requestBody.put("history", history);

            mockMvc.perform(post("/api/chatbot/chat")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.answer").value(equalTo("Risposta turno " + turn)))
                    .andExpect(jsonPath("$.sources[0].fileName").value("manuale-sicurezza.pdf"));

            history.add(Map.of("role", "user", "content", question));
            history.add(Map.of("role", "assistant", "content", "Risposta turno " + turn));
        }

        List<String> prompts = userPromptCaptor.getAllValues();
        assertThat(prompts.size(), equalTo(25));

        String lastPrompt = prompts.get(prompts.size() - 1);
        long historyLines = lastPrompt.lines()
                .filter(line -> line.startsWith("Utente:") || line.startsWith("Assistente:"))
                .count();

        assertThat(historyLines, lessThanOrEqualTo(10L));
        assertThat(lastPrompt, not(containsString("Domanda turno 1")));
        assertThat(lastPrompt, containsString("Domanda turno 24"));
    }
}
