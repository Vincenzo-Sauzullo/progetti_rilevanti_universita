package it.unicas.spring.springai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AssessmentGenerationGuardIntegrationTest {

    private static final String EXPLAINABLE_ASSESSMENT_JSON = """
            {
              "riepilogoConformita": "Riepilogo conformità (test).",
              "nonConformitaRilevate": [
                {"text": "Mancanza marcatura CE visibile", "chunkIds": [1]}
              ],
              "raccomandazioni": [
                {"text": "Applicare marcatura CE e aggiornare la documentazione", "chunkIds": [1]}
              ],
              "livelloRischio": "MEDIO",
              "punteggioConformita": 72
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void assessment_generation_is_blocked_when_machine_has_not_changed() throws Exception {
        stubAssessmentDependencies();
        long machineId = createMachine("Pressa Guard", "Prima descrizione");

        mockMvc.perform(post("/api/macchinari/{id}/assessment", machineId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/macchinari/{id}/assessment", machineId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Modifica il macchinario")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void assessment_generation_is_allowed_again_after_machine_update() throws Exception {
        stubAssessmentDependencies();
        long machineId = createMachine("Pressa Rigenerabile", "Descrizione iniziale");

        MvcResult firstAssessmentResult = mockMvc.perform(post("/api/macchinari/{id}/assessment", machineId))
                .andExpect(status().isOk())
                .andReturn();

        long firstAssessmentId = readAssessmentId(firstAssessmentResult);

        Thread.sleep(1100);

        String updatePayload = """
                {
                  "nome": "Pressa Rigenerabile",
                  "produttore": "ACME",
                  "modello": "X-500",
                  "numeroSerie": "SN-RIG-001",
                  "annoProduzione": 2024,
                  "categoria": "Presse",
                  "descrizione": "Descrizione aggiornata dopo la prima valutazione",
                  "specificheTecniche": "Potenza 5kW"
                }
                """;

        mockMvc.perform(put("/api/macchinari/{id}", machineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk());

        MvcResult secondAssessmentResult = mockMvc.perform(post("/api/macchinari/{id}/assessment", machineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macchinarioId").value(machineId))
                .andReturn();

        long secondAssessmentId = readAssessmentId(secondAssessmentResult);
        assertTrue(secondAssessmentId > firstAssessmentId, "Il secondo assessment dovrebbe essere persistito come nuovo record.");

        mockMvc.perform(get("/api/macchinari/{id}/assessments", machineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(secondAssessmentId))
                .andExpect(jsonPath("$[0].macchinarioId").value(machineId));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleting_assessment_from_assessment_section_allows_new_generation() throws Exception {
        stubAssessmentDependencies();
        long machineId = createMachine("Pressa Eliminabile", "Descrizione iniziale");

        MvcResult firstAssessmentResult = mockMvc.perform(post("/api/macchinari/{id}/assessment", machineId))
                .andExpect(status().isOk())
                .andReturn();

        long firstAssessmentId = readAssessmentId(firstAssessmentResult);

        mockMvc.perform(delete("/api/assessments/{id}", firstAssessmentId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/macchinari/{id}/assessments", machineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(post("/api/macchinari/{id}/assessment", machineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macchinarioId").value(machineId));
    }

    private void stubAssessmentDependencies() {
        Document ragChunk = new Document(
                "Articolo 10 comma 2: requisiti essenziali di sicurezza e marcatura CE.",
                Map.of("fileName", "Regolamento UE 2023-1230.pdf", "page", 10)
        );

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(ragChunk));

        ChatClient chatClient = mock(ChatClient.class, Answers.RETURNS_DEEP_STUBS);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()
                .system(anyString())
                .user(anyString())
                .call()
                .content()).thenReturn(EXPLAINABLE_ASSESSMENT_JSON);
    }

    private long createMachine(String name, String description) throws Exception {
        String payload = """
                {
                  "nome": "%s",
                  "produttore": "ACME",
                  "modello": "X-500",
                  "numeroSerie": "SN-RIG-001",
                  "annoProduzione": 2024,
                  "categoria": "Presse",
                  "descrizione": "%s",
                  "specificheTecniche": "Potenza 5kW"
                }
                """.formatted(name, description);

        MvcResult result = mockMvc.perform(post("/api/macchinari")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private long readAssessmentId(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
