package it.unicas.spring.springai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unicas.spring.springai.model.AssessmentResult;
import it.unicas.spring.springai.model.Macchinario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
/**
 * Service che genera il report PDF di un assessment.
 * Ruolo architetturale: converte dati di dominio (`Macchinario`, `AssessmentResult`)
 * in documento binario scaricabile, mantenendo layout e wrapping tipografico.
 * Interagisce con `MacchinarioController` (download endpoint) e PDFBox.
 */
public class AssessmentPdfService {

    /*
     * - Prende i dati dell'assessment e li trasforma in un PDF bello leggibile.
     * - Fa anche il nome file "pulito" per il download.
     * - `PdfFlow` qui sotto e' il mini-motore che scrive testo, va a capo e cambia pagina quando serve.
     * - Da raccontare cosi': input dati -> impaginazione -> PDF in byte[] -> download.
     */
    /*
     * Cheat sheet super rapido:
     *
     * Metodi esterni principali:
     * - `generateAssessmentPdf`: crea tutto il documento.
     * - `buildDownloadFilename`: genera nome file sicuro per browser/filesystem.
     *
     * Helper di formattazione:
     * - `formatFilenameDateTime`: data compatta per filename.
     * - `formatDateTime`: data leggibile per umani nel PDF.
     * - `safe`: fallback per valori null/blank.
     * - `formatExplainableList`: converte JSON list in testo con bullet.
     * - `sanitizeFilenamePart`: pulisce nome file (caratteri strani -> underscore).
     *
     * Classe interna `PdfFlow` (la piu' importante da capire):
     * - gestisce pagina corrente, posizione y, margini e larghezza utile;
     * - `newPage` apre pagina nuova;
     * - `ensureSpace` evita di scrivere fuori pagina;
     * - `writeCentered` / `writeSection` / `writeParagraph` scrivono testo;
     * - `wrap` e `breakLongToken` spezzano testo troppo lungo;
     * - `sanitizeForFont` evita crash su caratteri non supportati;
     * - `close` / `closeQuietly` chiudono stream.
     *
     * Frase pronta:
     * "Qui non c'e' AI: c'e' solo impaginazione robusta di dati gia' calcolati."
     */
    /*
     * Flusso di generazione PDF spiegato passo per passo:
     *
     * Fase 1 - bootstrap documento:
     * - apre `PDDocument`.
     * - inizializza `PdfFlow` (prima pagina + coordinate + margini).
     *
     * Fase 2 - scrittura header:
     * - titolo report.
     * - sottotitolo con nome macchinario.
     *
     * Fase 3 - scrittura metadati assessment:
     * - id, data, versione, rischio, punteggio.
     *
     * Fase 4 - scrittura dati macchina:
     * - anagrafica generale.
     * - descrizione e specifiche.
     *
     * Fase 5 - scrittura esito assessment:
     * - riepilogo conformita'.
     * - non conformita' e raccomandazioni (anche da JSON explainable).
     * - documenti usati.
     * - output completo originale.
     *
     * Fase 6 - paginazione automatica:
     * - ogni scrittura verifica spazio residuo.
     * - se manca spazio, `PdfFlow` apre una nuova pagina.
     * - wrapping automatico per righe e parole troppo lunghe.
     *
     * Fase 7 - robustezza tipografica:
     * - caratteri non supportati dal font vengono sanificati.
     * - evita crash in `showText`.
     *
     * Fase 8 - serializzazione finale:
     * - salva il documento in `ByteArrayOutputStream`.
     * - ritorna `byte[]` pronto per download HTTP.
     */

    // Dimensioni pagina e margini del report PDF.
    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final float MARGIN = 48f;

    // Font base usati in tutto il documento.
    private static final PDFont FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    // Dimensioni font per titolo/sezioni/testo.
    private static final float FONT_SIZE_TITLE = 18f;
    private static final float FONT_SIZE_SECTION = 13f;
    private static final float FONT_SIZE_BODY = 11f;

    // Interlinee (leading) per i vari livelli tipografici.
    private static final float LEADING_TITLE = 1.4f * FONT_SIZE_TITLE;
    private static final float LEADING_SECTION = 1.35f * FONT_SIZE_SECTION;
    private static final float LEADING_BODY = 1.35f * FONT_SIZE_BODY;

    // Formati data: uno per nome file, uno per visualizzazione nel report.
    private static final DateTimeFormatter DATE_TIME_FILENAME = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm", Locale.ROOT);
    private static final DateTimeFormatter DATE_TIME_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ITALY);

    private final ObjectMapper objectMapper;

    /**
     * Cosa fa: costruisce il PDF completo dell'assessment con sezioni descrittive e risultati.
     * Perché esiste: fornisce output formale condivisibile fuori dall'interfaccia web.
     * Chi lo chiama: `MacchinarioController.downloadLatestAssessmentPdf -> AssessmentPdfService.generateAssessmentPdf`.
     * Input/Output: input macchinario + assessment; output array byte PDF.
     * Eccezioni/edge case: errori PDFBox o IO => `RuntimeException` con messaggio contestuale.
     * Side effects: allocazione memoria documento/stream e log errori.
     *
     * @param macchinario macchina associata al report.
     * @param assessment risultato assessment da esportare.
     * @return contenuto PDF serializzato.
     */
    public byte[] generateAssessmentPdf(Macchinario macchinario, AssessmentResult assessment) {
        // In pratica: preparo tutte le sezioni del report e alla fine salvo tutto in byte[].
        try (PDDocument document = new PDDocument()) {
            PdfFlow flow = new PdfFlow(document);

            // 1) Header del report.
            String title = "Assessment di Conformità";
            String subtitle = safe(macchinario != null ? macchinario.getNome() : null, "Macchinario");

            flow.writeCentered(title, FONT_BOLD, FONT_SIZE_TITLE, LEADING_TITLE);
            flow.writeCentered(subtitle, FONT_BOLD, FONT_SIZE_SECTION, LEADING_SECTION);
            flow.blankLine(LEADING_BODY);

            // 2) Dati principali assessment.
            flow.writeSection("Dettagli assessment");
            flow.writeWrappedLine("ID assessment: " + safe(assessment != null ? assessment.getId() : null, "-"));
            flow.writeWrappedLine("Data: " + formatDateTime(assessment != null ? assessment.getDataAssessment() : null));
            flow.writeWrappedLine("Versione: " + safe(assessment != null ? assessment.getVersione() : null, "-"));
            flow.writeWrappedLine("Livello rischio: " + safe(assessment != null ? assessment.getLivelloRischio() : null, "-"));
            flow.writeWrappedLine("Punteggio conformità: " + safe(assessment != null ? assessment.getPunteggioConformita() : null, "-"));
            flow.blankLine(LEADING_BODY);

            // 3) Sezione macchina (solo se presente, per evitare NPE).
            if (macchinario != null) {
                flow.writeSection("Dati macchinario");
                flow.writeWrappedLine("Nome: " + safe(macchinario.getNome(), "-"));
                flow.writeWrappedLine("Produttore: " + safe(macchinario.getProduttore(), "Non specificato"));
                flow.writeWrappedLine("Modello: " + safe(macchinario.getModello(), "Non specificato"));
                flow.writeWrappedLine("Numero di serie: " + safe(macchinario.getNumeroSerie(), "Non specificato"));
                flow.writeWrappedLine("Anno produzione: " + safe(macchinario.getAnnoProduzione(), "Non specificato"));
                flow.writeWrappedLine("Categoria: " + safe(macchinario.getCategoria(), "Non specificata"));
                flow.blankLine(LEADING_BODY);

                flow.writeSection("Descrizione");
                flow.writeParagraph(safe(macchinario.getDescrizione(), "Non disponibile"));
                flow.blankLine(LEADING_BODY);

                flow.writeSection("Specifiche tecniche");
                flow.writeParagraph(safe(macchinario.getSpecificheTecniche(), "Non disponibili"));
                flow.blankLine(LEADING_BODY);
            }

            // 4) Risultati assessment.
            flow.writeSection("Riepilogo conformità");
            flow.writeParagraph(safe(assessment != null ? assessment.getRiepilogoConformita() : null, "Non disponibile"));
            flow.blankLine(LEADING_BODY);

            flow.writeSection("Non conformità rilevate");
            flow.writeParagraph(formatExplainableList(safe(assessment != null ? assessment.getNonConformitaRilevate() : null, ""), "Nessuna non conformità rilevata"));
            flow.blankLine(LEADING_BODY);

            flow.writeSection("Raccomandazioni");
            flow.writeParagraph(formatExplainableList(safe(assessment != null ? assessment.getRaccomandazioni() : null, ""), "Nessuna raccomandazione"));
            flow.blankLine(LEADING_BODY);

            flow.writeSection("Documenti utilizzati");
            flow.writeParagraph(safe(assessment != null ? assessment.getDocumentiUtilizzati() : null, "Nessun documento di riferimento"));
            flow.blankLine(LEADING_BODY);

            flow.writeSection("Testo completo (output)");
            flow.writeParagraph(safe(assessment != null ? assessment.getRisultato() : null, "Non disponibile"));

            flow.close();

            // 5) Serializzazione finale in byte[] per download HTTP.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error generating assessment PDF", e);
            throw new RuntimeException("Errore durante la generazione del PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Cosa fa: genera filename safe per il download PDF includendo ID macchina e timestamp.
     * Perché esiste: evita collisioni e garantisce nomi compatibili filesystem/browser.
     * Chi lo chiama: `MacchinarioController.downloadLatestAssessmentPdf -> AssessmentPdfService.buildDownloadFilename`.
     * Input/Output: input macchinario + assessment; output nome file `.pdf`.
     * Eccezioni/edge case: campi mancanti => segmenti fallback (`unknown`, `macchinario`).
     * Side effects: nessuno.
     *
     * @param macchinario macchina associata.
     * @param assessment assessment esportato.
     * @return filename download.
     */
    public String buildDownloadFilename(Macchinario macchinario, AssessmentResult assessment) {
        // In pratica: creo un nome file stabile e senza caratteri strani.
        String machinePart = sanitizeFilenamePart(macchinario != null ? macchinario.getNome() : null, "macchinario");
        String idPart = assessment != null && assessment.getId() != null ? assessment.getId().toString() : "unknown";
        String datePart = assessment != null ? formatFilenameDateTime(assessment.getDataAssessment()) : "unknown";
        return "assessment_" + idPart + "_" + machinePart + "_" + datePart + ".pdf";
    }

    /**
     * Cosa fa: formatta una data in formato compatto per filename.
     * Perché esiste: separa formato tecnico file dal formato umano del report.
     * Chi lo chiama: `AssessmentPdfService.buildDownloadFilename`.
     * Input/Output: input data/ora; output stringa `yyyyMMdd_HHmm` o `unknown`.
     * Eccezioni/edge case: input nullo => `unknown`.
     * Side effects: nessuno.
     *
     * @param value data da formattare.
     * @return data in formato filename.
     */
    private String formatFilenameDateTime(LocalDateTime value) {
        if (value == null) return "unknown";
        return DATE_TIME_FILENAME.format(value);
    }

    /**
     * Cosa fa: formatta la data assessment in stile leggibile per il report.
     * Perché esiste: migliora comprensibilità umana del PDF finale.
     * Chi lo chiama: `AssessmentPdfService.generateAssessmentPdf`.
     * Input/Output: input data/ora; output stringa italiana `dd/MM/yyyy HH:mm` o `-`.
     * Eccezioni/edge case: input nullo => `-`.
     * Side effects: nessuno.
     *
     * @param value data da mostrare nel report.
     * @return data formattata per visualizzazione.
     */
    private String formatDateTime(LocalDateTime value) {
        if (value == null) return "-";
        return DATE_TIME_DISPLAY.format(value);
    }

    /**
     * Cosa fa: converte un valore generico in stringa non vuota con fallback.
     * Perché esiste: evita campi blank nelle sezioni PDF.
     * Chi lo chiama: `generateAssessmentPdf` e `formatExplainableList`.
     * Input/Output: input valore + fallback; output stringa pronta.
     * Eccezioni/edge case: null/blank => fallback.
     * Side effects: nessuno.
     *
     * @param value valore da normalizzare.
     * @param fallback testo sostitutivo.
     * @return stringa normalizzata.
     */
    private String safe(Object value, String fallback) {
        if (value == null) return fallback;
        String text = value.toString().trim();
        return text.isEmpty() ? fallback : text;
    }

    /**
     * Cosa fa: converte lista explainable JSON in testo bullet leggibile nel PDF.
     * Perché esiste: il PDF richiede testo piano anche quando il dato origine è strutturato JSON.
     * Chi lo chiama: `AssessmentPdfService.generateAssessmentPdf`.
     * Input/Output: input raw JSON/txt + fallback; output testo formattato.
     * Eccezioni/edge case: JSON non parsabile o non array => ritorna valore originale.
     * Side effects: parsing JSON in memoria.
     *
     * @param raw contenuto raw da formattare.
     * @param fallback testo fallback se non ci sono elementi.
     * @return elenco testuale pronto per PDF.
     */
    private String formatExplainableList(String raw, String fallback) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) return fallback;

        JsonNode node;
        try {
            node = objectMapper.readTree(value);
        } catch (Exception e) {
            // Se non è JSON, lo trattiamo già come testo pronto.
            return value;
        }

        if (!node.isArray()) {
            return value;
        }

        StringBuilder sb = new StringBuilder();
        for (JsonNode item : node) {
            if (item == null || item.isNull()) continue;

            String text;
            if (item.isTextual()) {
                text = item.asText("");
            } else {
                // Supportiamo sia "text" che "testo" per compatibilità vecchi output.
                text = item.path("text").asText("");
                if (text.isBlank()) {
                    text = item.path("testo").asText("");
                }
            }

            text = text == null ? "" : text.trim();
            if (text.isEmpty()) continue;

            if (!sb.isEmpty()) sb.append('\n');
            sb.append("• ").append(text);
        }

        return sb.isEmpty() ? fallback : sb.toString();
    }

    /**
     * Cosa fa: ripulisce una stringa per renderla sicura come parte di filename.
     * Perché esiste: evita caratteri non ammessi o troppo lunghi nel nome file scaricato.
     * Chi lo chiama: `AssessmentPdfService.buildDownloadFilename`.
     * Input/Output: input testo + fallback; output token filename-safe.
     * Eccezioni/edge case: input vuoto/non valido => fallback; lunghezza oltre 80 truncata.
     * Side effects: nessuno.
     *
     * @param input testo grezzo da sanificare.
     * @param fallback valore sostitutivo.
     * @return segmento filename compatibile.
     */
    private String sanitizeFilenamePart(String input, String fallback) {
        String value = input == null ? "" : input.trim();
        if (value.isEmpty()) return fallback;

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        normalized = normalized.replaceAll("[^a-zA-Z0-9._-]+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");

        if (normalized.isBlank()) {
            normalized = fallback;
        }

        if (normalized.length() > 80) {
            normalized = normalized.substring(0, 80);
        }

        return normalized;
    }

    /**
     * Helper interno per gestire scrittura progressiva del PDF con paginazione automatica.
     * Ruolo architetturale: incapsula logica layout (margini, wrapping, linee) separandola
     * dalla logica di business del report assessment.
     */
    private static final class PdfFlow implements Closeable {
        /*
         * Guida terra terra su PdfFlow:
         * - Tiene traccia di dove stai scrivendo nella pagina (coordinata y).
         * - Se non c'e' spazio, apre una nuova pagina.
         * - Spezza le righe troppo lunghe e sostituisce caratteri "problematici" per evitare crash PDF.
         */
        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream contentStream;
        private float y;
        private final float width;

        /**
         * Cosa fa: inizializza il writer PDF e apre la prima pagina.
         * Perché esiste: prepara stato interno condiviso da tutte le operazioni di stampa.
         * Chi lo chiama: `AssessmentPdfService.generateAssessmentPdf -> new PdfFlow(document)`.
         * Input/Output: input documento PDFBox; output istanza pronta alla scrittura.
         * Eccezioni/edge case: eventuali errori IO nella creazione pagina sono propagati.
         * Side effects: aggiunge pagina al documento e apre `PDPageContentStream`.
         *
         * @param document documento PDF destinazione.
         * @throws IOException se l'inizializzazione stream fallisce.
         */
        private PdfFlow(PDDocument document) throws IOException {
            this.document = document;
            this.width = PAGE_SIZE.getWidth() - (2 * MARGIN);
            newPage();
        }

        /**
         * Cosa fa: chiude lo stream corrente (se presente) e apre una nuova pagina A4.
         * Perché esiste: supporta paginazione quando il contenuto supera lo spazio disponibile.
         * Chi lo chiama: `PdfFlow.PdfFlow`, `PdfFlow.ensureSpace`.
         * Input/Output: nessun input esterno; aggiorna stato pagina/cursore `y`.
         * Eccezioni/edge case: errori stream propagati.
         * Side effects: modifica documento PDF aggiungendo una pagina.
         *
         * @throws IOException se creazione nuovo content stream fallisce.
         */
        private void newPage() throws IOException {
            closeQuietly(contentStream);
            page = new PDPage(PAGE_SIZE);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
            y = PAGE_SIZE.getHeight() - MARGIN;
        }

        /**
         * Cosa fa: verifica spazio verticale residuo e cambia pagina se necessario.
         * Perché esiste: evita testo stampato oltre margine inferiore.
         * Chi lo chiama: `writeCentered`, `writeSection`, `writeParagraph`, `blankLine`.
         * Input/Output: input altezza richiesta; output `void`.
         * Eccezioni/edge case: se spazio insufficiente apre nuova pagina.
         * Side effects: possibile creazione nuova pagina.
         *
         * @param requiredHeight altezza minima richiesta in punti.
         * @throws IOException se apertura pagina fallisce.
         */
        private void ensureSpace(float requiredHeight) throws IOException {
            if (y - requiredHeight < MARGIN) {
                newPage();
            }
        }

        /**
         * Cosa fa: scrive una singola riga centrata orizzontalmente.
         * Perché esiste: usato per titolo/sottotitolo del report.
         * Chi lo chiama: `AssessmentPdfService.generateAssessmentPdf -> PdfFlow.writeCentered`.
         * Input/Output: input testo/font/dimensione/interlinea; output `void`.
         * Eccezioni/edge case: glyph non supportate sanificate da `sanitizeForFont`.
         * Side effects: aggiorna cursore verticale `y`.
         *
         * @param text testo da centrare.
         * @param font font PDF da usare.
         * @param fontSize dimensione font.
         * @param leading passo verticale dopo la riga.
         * @throws IOException se scrittura stream fallisce.
         */
        private void writeCentered(String text, PDFont font, float fontSize, float leading) throws IOException {
            String line = sanitizeForFont(text, font, fontSize);
            float textWidth = stringWidth(font, fontSize, line);
            float x = Math.max(MARGIN, (PAGE_SIZE.getWidth() - textWidth) / 2f);
            ensureSpace(leading);
            writeLineAt(x, line, font, fontSize);
            y -= leading;
        }

        /**
         * Cosa fa: stampa un titolo sezione in grassetto sul margine sinistro.
         * Perché esiste: struttura semanticamente il report in blocchi leggibili.
         * Chi lo chiama: `AssessmentPdfService.generateAssessmentPdf -> PdfFlow.writeSection`.
         * Input/Output: input titolo; output `void`.
         * Eccezioni/edge case: testo sanificato per font non compatibili.
         * Side effects: aggiornamento posizione `y`.
         *
         * @param title titolo sezione da stampare.
         * @throws IOException se scrittura fallisce.
         */
        private void writeSection(String title) throws IOException {
            ensureSpace(LEADING_SECTION);
            String sanitized = sanitizeForFont(title, FONT_BOLD, FONT_SIZE_SECTION);
            writeLineAt(MARGIN, sanitized, FONT_BOLD, FONT_SIZE_SECTION);
            y -= LEADING_SECTION;
        }

        /**
         * Cosa fa: alias semantico che inoltra alla scrittura paragrafo con wrapping.
         * Perché esiste: migliora leggibilità chiamanti quando il contenuto è una singola riga logica.
         * Chi lo chiama: `AssessmentPdfService.generateAssessmentPdf -> PdfFlow.writeWrappedLine`.
         * Input/Output: input testo; output `void`.
         * Eccezioni/edge case: delegate a `writeParagraph`.
         * Side effects: scrittura PDF.
         *
         * @param text linea da stampare.
         * @throws IOException se scrittura fallisce.
         */
        private void writeWrappedLine(String text) throws IOException {
            writeParagraph(text);
        }

        /**
         * Cosa fa: scrive testo multi-riga con wrapping automatico e gestione paragrafi vuoti.
         * Perché esiste: supporta blocchi lunghi (descrizioni, risultati, raccomandazioni).
         * Chi lo chiama: `writeWrappedLine` e `AssessmentPdfService.generateAssessmentPdf`.
         * Input/Output: input testo; output `void`.
         * Eccezioni/edge case: newline consecutivi producono righe bianche.
         * Side effects: possibile cambio pagina tramite `ensureSpace`.
         *
         * @param text testo da stampare.
         * @throws IOException se scrittura o wrapping falliscono.
         */
        private void writeParagraph(String text) throws IOException {
            String safeText = text == null ? "" : text;
            String normalized = safeText.replace("\r\n", "\n").replace('\r', '\n');

            String[] paragraphs = normalized.split("\n", -1);
            for (int i = 0; i < paragraphs.length; i++) {
                String paragraph = paragraphs[i];
                if (paragraph.isBlank()) {
                    blankLine(LEADING_BODY);
                    continue;
                }

                List<String> lines = wrap(paragraph, FONT_REGULAR, FONT_SIZE_BODY, width);
                for (String line : lines) {
                    ensureSpace(LEADING_BODY);
                    writeLineAt(MARGIN, line, FONT_REGULAR, FONT_SIZE_BODY);
                    y -= LEADING_BODY;
                }
            }
        }

        /**
         * Cosa fa: inserisce una riga vuota avanzando il cursore verticale.
         * Perché esiste: mantiene separazione visiva tra sezioni del report.
         * Chi lo chiama: `AssessmentPdfService.generateAssessmentPdf` e `writeParagraph`.
         * Input/Output: input leading; output `void`.
         * Eccezioni/edge case: se non c'è spazio apre nuova pagina.
         * Side effects: modifica posizione `y`.
         *
         * @param leading spazio verticale da applicare.
         * @throws IOException se gestione pagina fallisce.
         */
        private void blankLine(float leading) throws IOException {
            ensureSpace(leading);
            y -= leading;
        }

        /**
         * Cosa fa: scrive una riga in coordinate assolute con font e dimensione indicati.
         * Perché esiste: primitive unica di output testo su content stream.
         * Chi lo chiama: `writeCentered`, `writeSection`, `writeParagraph`.
         * Input/Output: input coordinata X + testo + font; output `void`.
         * Eccezioni/edge case: eventuali caratteri incompatibili vanno sanificati a monte.
         * Side effects: scrittura su stream PDF aperto.
         *
         * @param x coordinata orizzontale.
         * @param line testo da mostrare.
         * @param font font da usare.
         * @param fontSize dimensione font.
         * @throws IOException se stream non scrivibile.
         */
        private void writeLineAt(float x, String line, PDFont font, float fontSize) throws IOException {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(line);
            contentStream.endText();
        }

        /**
         * Cosa fa: spezza un paragrafo in righe che rispettano larghezza massima disponibile.
         * Perché esiste: evita overflow orizzontale del testo nel PDF.
         * Chi lo chiama: `PdfFlow.writeParagraph -> PdfFlow.wrap`.
         * Input/Output: input testo/font/dimensione/maxWidth; output lista linee wrappate.
         * Eccezioni/edge case: token più lunghi della larghezza vengono delegati a `breakLongToken`.
         * Side effects: nessuno.
         *
         * @param text testo da spezzare.
         * @param font font di misura.
         * @param fontSize dimensione font.
         * @param maxWidth larghezza massima riga.
         * @return linee wrappate.
         * @throws IOException se misura font fallisce.
         */
        private List<String> wrap(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
            String sanitized = sanitizeForFont(text, font, fontSize);
            String trimmed = sanitized.trim();
            if (trimmed.isEmpty()) {
                return List.of("");
            }

            String[] words = trimmed.split("\\s+");
            List<String> lines = new ArrayList<>();
            StringBuilder current = new StringBuilder();

            // Algoritmo "riempi la riga finché ci sta":
            // è come comporre una valigia senza superare il peso massimo.
            for (String word : words) {
                if (current.length() == 0) {
                    if (stringWidth(font, fontSize, word) <= maxWidth) {
                        current.append(word);
                        continue;
                    }

                    List<String> broken = breakLongToken(word, font, fontSize, maxWidth);
                    lines.addAll(broken);
                    continue;
                }

                String candidate = current + " " + word;
                if (stringWidth(font, fontSize, candidate) <= maxWidth) {
                    current.append(' ').append(word);
                    continue;
                }

                lines.add(current.toString());
                current.setLength(0);

                if (stringWidth(font, fontSize, word) <= maxWidth) {
                    current.append(word);
                } else {
                    // Parola troppo lunga senza spazi: la spezziamo a caratteri.
                    List<String> broken = breakLongToken(word, font, fontSize, maxWidth);
                    lines.addAll(broken);
                }
            }

            if (current.length() > 0) {
                lines.add(current.toString());
            }

            return lines;
        }

        /**
         * Cosa fa: spezza un singolo token molto lungo in sottoparti stampabili.
         * Perché esiste: previene overflow anche quando non ci sono spazi nel testo.
         * Chi lo chiama: `PdfFlow.wrap -> PdfFlow.breakLongToken`.
         * Input/Output: input token/font/size/maxWidth; output parti spezzate.
         * Eccezioni/edge case: carattere non misurabile => sostituito con `?`.
         * Side effects: nessuno.
         *
         * @param token parola troppo lunga.
         * @param font font usato per misura.
         * @param fontSize dimensione font.
         * @param maxWidth larghezza massima ammessa.
         * @return parti del token adatte alla riga.
         * @throws IOException se misura font fallisce.
         */
        private List<String> breakLongToken(String token, PDFont font, float fontSize, float maxWidth) throws IOException {
            List<String> parts = new ArrayList<>();
            StringBuilder current = new StringBuilder();

            int offset = 0;
            while (offset < token.length()) {
                int codePoint = token.codePointAt(offset);
                String ch = new String(Character.toChars(codePoint));
                offset += Character.charCount(codePoint);

                // Proviamo ad aggiungere il prossimo carattere.
                String candidate = current + ch;
                if (stringWidth(font, fontSize, candidate) <= maxWidth) {
                    current.append(ch);
                    continue;
                }

                // Se non ci sta, "chiudiamo" la parte corrente e ripartiamo.
                if (current.length() > 0) {
                    parts.add(current.toString());
                    current.setLength(0);
                }

                if (stringWidth(font, fontSize, ch) <= maxWidth) {
                    current.append(ch);
                } else {
                    // Caso limite estremo: un singolo glyph non entra -> placeholder sicuro.
                    parts.add("?");
                }
            }

            if (current.length() > 0) {
                parts.add(current.toString());
            }

            return parts;
        }

        /**
         * Cosa fa: calcola larghezza grafica di una stringa nel font/dimensione correnti.
         * Perché esiste: decisione wrapping basata su misura reale del font PDF.
         * Chi lo chiama: `writeCentered`, `wrap`, `breakLongToken`.
         * Input/Output: input font+size+text; output larghezza in punti.
         * Eccezioni/edge case: errori metriche font propagati.
         * Side effects: nessuno.
         *
         * @param font font di riferimento.
         * @param fontSize dimensione font.
         * @param text testo da misurare.
         * @return larghezza in punti.
         * @throws IOException se il font non fornisce metriche.
         */
        private float stringWidth(PDFont font, float fontSize, String text) throws IOException {
            return (font.getStringWidth(text) / 1000f) * fontSize;
        }

        /**
         * Cosa fa: sostituisce caratteri non supportati dal font con placeholder sicuro.
         * Perché esiste: impedisce errori runtime `showText` con glyph non codificabili.
         * Chi lo chiama: `writeCentered`, `writeSection`, `wrap`.
         * Input/Output: input testo/font; output testo sanificato.
         * Eccezioni/edge case: fallback finale sostituisce non ASCII con `?` se necessario.
         * Side effects: nessuno.
         *
         * @param text testo originale.
         * @param font font target.
         * @param fontSize dimensione font (uso non diretto, mantenuto per coerenza API).
         * @return testo compatibile con font.
         */
        private String sanitizeForFont(String text, PDFont font, float fontSize) {
            if (text == null) return "";

            StringBuilder sb = new StringBuilder();
            int offset = 0;
            while (offset < text.length()) {
                int codePoint = text.codePointAt(offset);
                String ch = new String(Character.toChars(codePoint));
                offset += Character.charCount(codePoint);

                try {
                    // will throw for unsupported glyphs in the given font/encoding
                    font.getStringWidth(ch);
                    sb.append(ch);
                } catch (Exception e) {
                    sb.append('?');
                }
            }

            String sanitized = sb.toString();
            try {
                font.getStringWidth(sanitized);
                return sanitized;
            } catch (Exception e) {
                return sanitized.replaceAll("[^\\x20-\\x7E]", "?");
            }
        }

        /**
         * Cosa fa: chiude il content stream corrente del `PdfFlow`.
         * Perché esiste: garantisce rilascio risorse I/O nella fase finale di generazione PDF.
         * Chi lo chiama: `AssessmentPdfService.generateAssessmentPdf -> PdfFlow.close` (try-with-resources/manual close).
         * Input/Output: nessun input; output `void`.
         * Eccezioni/edge case: eccezioni di chiusura propagate al chiamante.
         * Side effects: chiusura risorsa I/O.
         *
         * @throws IOException se chiusura stream fallisce.
         */
        @Override
        public void close() throws IOException {
            closeQuietly(contentStream);
        }

        /**
         * Cosa fa: chiude una risorsa `Closeable` se non nulla.
         * Perché esiste: utility centralizzata per evitare duplicazione check null in chiusura stream.
         * Chi lo chiama: `PdfFlow.newPage` e `PdfFlow.close`.
         * Input/Output: input risorsa; output `void`.
         * Eccezioni/edge case: risorsa nulla ignorata.
         * Side effects: chiusura risorsa passata.
         *
         * @param closeable risorsa da chiudere.
         * @throws IOException se la chiusura genera errore.
         */
        private static void closeQuietly(Closeable closeable) throws IOException {
            if (closeable != null) {
                closeable.close();
            }
        }
    }
}
