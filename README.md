# Portfolio dei progetti universitari

**Vincenzo Sauzullo — Ingegneria Informatica**

[Profilo GitHub](https://github.com/Vincenzo-Sauzullo) · [LinkedIn](https://www.linkedin.com/in/vincenzo-sauzullo-9aaa09448/) · [Email](mailto:vincenzo.sauzullo@gmail.com)

Raccolta di progetti sviluppati durante la laurea triennale in Ingegneria Informatica presso l'Università degli Studi di Napoli Federico II e la laurea magistrale presso l'Università degli Studi di Cassino e del Lazio Meridionale.

Il portfolio comprende applicazioni Java con database, sistemi client-server in Python, una pipeline di Image Processing e Machine Learning e un'applicazione web basata su LLM e Retrieval-Augmented Generation. Ogni cartella costituisce un progetto autonomo, con requisiti e modalità di esecuzione specifici.

Diversi lavori sono stati realizzati in gruppo. I crediti riportati nelle relazioni, nei README e nei file di licenza dei singoli progetti restano parte integrante della documentazione.

## Indice

- [Progetti inclusi](#progetti-inclusi)
- [Download e organizzazione](#download-e-organizzazione)
- [Bone Fracture Detection](#bone-fracture-detection)
- [RAG Assessment Macchinari](#rag-assessment-macchinari)
- [Sistema di gestione degli eventi musicali](#sistema-di-gestione-degli-eventi-musicali)
- [PokerMind](#pokermind)
- [Test e valutazione](#test-e-valutazione)
- [Competenze applicate](#competenze-applicate)
- [Crediti e licenze](#crediti-e-licenze)
- [English overview](#english-overview)

## Progetti inclusi

| Progetto | Ambito | Tecnologie principali | Percorso |
|---|---|---|---|
| **Bone Fracture Detection** | Image Processing e Machine Learning | C++, OpenCV, Python, scikit-learn, Tkinter/CustomTkinter | [progetti_magistrale/Bone_Fracture_Detection-main](progetti_magistrale/Bone_Fracture_Detection-main/) |
| **RAG Assessment Macchinari** | Applicazioni web e integrazione LLM | Java 17, Spring Boot, Spring AI, PostgreSQL, pgvector, Docker, Ollama | [progetti_magistrale/Software_Engineering_project-master](progetti_magistrale/Software_Engineering_project-master/) |
| **Sistema di gestione degli eventi musicali** | Ingegneria del Software | Java, Gradle, SQLite, JDBC, Jakarta Mail, JUnit, Docker | [progetti_triennale/ingegneria_del_software_triennale](progetti_triennale/ingegneria_del_software_triennale/) |
| **PokerMind** | Sistemi distribuiti, concorrenza e analisi dati | Python, socket TCP, JSON, Proxy–Skeleton, KNIME | [progetti_triennale/PokerMind-main](progetti_triennale/PokerMind-main/) |

## Download e organizzazione

Per scaricare il repository:

```bash
git clone https://github.com/Vincenzo-Sauzullo/progetti_rilevanti_universita.git
cd progetti_rilevanti_universita
```

Le cartelle `progetti_magistrale` e `progetti_triennale` separano i lavori dei due percorsi di studio. Non esiste un unico comando di avvio per l'intera raccolta.

I comandi riportati di seguito sono per macOS/Linux. Per ciascun progetto, il primo comando `cd` parte dalla **radice del repository** appena scaricato. Per eseguire un altro progetto, aprire un nuovo terminale nella radice oppure tornarvi prima di usare il relativo blocco.

Su Windows sono disponibili i wrapper `gradlew.bat` e `mvnw.cmd` nei rispettivi progetti Java. I progetti Python possono essere eseguiti con l'interprete Python installato nel proprio ambiente.

## Bone Fracture Detection

**Progetto magistrale di Image Processing and Analysis e Machine Learning.**

Sistema per localizzare regioni sospette di frattura nelle radiografie pediatriche dell'avambraccio. La pipeline produce bounding box candidate, le descrive mediante feature numeriche e applica un classificatore supervisionato per ridurre i falsi positivi.

Il lavoro è un prototipo accademico per lo studio della detection su immagini radiografiche; non è uno strumento clinicamente validato.

**Documentazione:** [README del progetto](progetti_magistrale/Bone_Fracture_Detection-main/README.md) · [Relazione in PDF](progetti_magistrale/Bone_Fracture_Detection-main/Bone_Fracture_Detection.pdf)

### Pipeline e componenti

1. **Image Processing in C++:** normalizzazione, segmentazione ossea, enhancement locale e individuazione di candidati lungo il profilo corticale e nelle regioni interne.
2. **Estrazione delle feature:** descrittori GLCM, LBP e HOG, con informazioni aggiuntive sulle ROI e sul metodo di rilevamento.
3. **Machine Learning in Python:** preprocessing, eventuale PCA, addestramento e selezione dei modelli.
4. **Post-processing:** filtro in base allo score ML e Non-Maximum Suppression per limitare le detection ridondanti.
5. **Valutazione e visualizzazione:** metriche, curve Precision–Recall/FROC e interfaccia grafica per esaminare i risultati.

| Componente | Responsabilità |
|---|---|
| `IPA/main.cpp` | Menu di avvio: analisi, valutazione della sensibilità o estrazione delle ROI e delle feature |
| `IPA/functions.cpp` | Implementazione dell'Image Processing e produzione dei CSV |
| `ML/fracture_fp_filter.py` | Addestramento, confronto delle configurazioni e predizione sui CSV |
| `ML/evaluate_test_postprocess.py` | Valutazione detection-level e selezione dei parametri di post-processing |
| `ML/pipeline_gui.py` | Revisione delle ROI, applicazione ML/NMS e consultazione delle metriche |

Il codice permette di confrontare SVM, KNN, Random Forest ed ensemble. Il report di addestramento presente nel repository documenta una configurazione **SVM con kernel RBF**, normalizzazione standard e PCA, applicata al feature set `texture_score`.

### Dati inclusi e protocollo sperimentale

Il progetto utilizza il dataset **GRAZPEDWRI-DX**, con annotazioni YOLO. Viene considerata la classe `3`, corrispondente alle fratture; il criterio di corrispondenza con una ground truth è `IoU >= 0.50`.

Il repository include:

- un campione di **100 immagini** in `test_100img/`, con le relative annotazioni in `test_100img/labels/`;
- un CSV di feature in `IPA/risultati_rilevamento_fratture/CSV_feature/` riferito al campione incluso;
- un modello salvato in `ML/risultati_addestramento/modello/fracture_fp_model.pkl`;
- report, predizioni, metriche e grafici già prodotti durante gli esperimenti.

Il report di addestramento salvato descrive un insieme sperimentale di **1.203 immagini e 12.685 ROI**, distinto dal solo campione distribuito. Riporta 721 immagini di training, 241 di validation e 241 di test. Lo split è effettuato per immagine; l'undersampling è applicato al training set. Questi dati non costituiscono una suddivisione dichiarata per paziente.

Per riprodurre l'intero addestramento occorrono il corpus originario e le corrispondenti annotazioni. La cartella completa `img_fracture/` non è inclusa nella versione del repository qui documentata.

### Risultati salvati

Le metriche seguenti provengono dai file JSON/CSV versionati. I due livelli di valutazione misurano oggetti diversi: classificazione delle singole ROI e detection finali associate alle fratture annotate.

| Livello di valutazione | Precision | Recall | F1 | Average Precision |
|---|---:|---:|---:|---:|
| Classificatore sulle ROI del test set | 0.4846 | 0.8582 | 0.6194 | 0.6954 |
| Detection dopo ML e NMS | 0.5194 | 0.8040 | 0.6311 | 0.7045 |

La prima riga usa la soglia ML selezionata nel report, circa `0.43544`. La seconda riporta il punto operativo con soglia ML `0.50`, NMS IoU `0.50`, criterio TP IoU `0.50` e soglia IGNORE `0.30`.

Fonti dei risultati:

- [Report di addestramento](progetti_magistrale/Bone_Fracture_Detection-main/ML/risultati_addestramento/report/fracture_fp_report.json)
- [Metriche operative della detection](progetti_magistrale/Bone_Fracture_Detection-main/ML/risultati_valutazione_postprocessing/curve/operating_metrics_nms_only.csv)
- [Parametri e Average Precision della detection](progetti_magistrale/Bone_Fracture_Detection-main/ML/risultati_valutazione_postprocessing/curve/best_postprocess_params.json)

**Coerenza delle configurazioni:** nella GUI corrente la soglia NMS è fissata a `0.40` e la soglia IGNORE a `0.20`, mentre il report di valutazione sopra citato usa rispettivamente `0.50` e `0.30`. La GUI mantiene i propri valori anche se trova valori differenti nel JSON. Per confrontare gli esperimenti occorre quindi verificare parametri e split utilizzati.

### Requisiti e avvio

- Per il modulo C++: compilatore compatibile con C++14, CMake 3.16 o successivo e OpenCV con header e librerie di sviluppo.
- Per il modulo Python: Python 3.10 o successivo, NumPy, pandas, scikit-learn, Matplotlib, Pillow e CustomTkinter; per la GUI serve anche Tkinter.
- Per utilizzare il modello `.pkl`: un ambiente compatibile con le versioni delle librerie usate per salvarlo. Nel progetto non è presente un file che blocchi tutte le versioni delle dipendenze Python.

Preparazione dell'ambiente Python:

```bash
cd progetti_magistrale/Bone_Fracture_Detection-main
python3 -m venv .venv
source .venv/bin/activate
python -m pip install numpy pandas scikit-learn matplotlib pillow customtkinter
```

Avvio dell'interfaccia:

```bash
python ML/pipeline_gui.py
```

La GUI usa **CSV di feature già estratti**: non esegue automaticamente il modulo C++. Per analizzare un'immagine devono essere presenti le relative righe nel CSV e il modello salvato. I suoi output sono raccolti sotto `ML/risultati_pipeline/`.

Per consultare le opzioni di training e valutazione:

```bash
python ML/fracture_fp_filter.py train --help
python ML/evaluate_test_postprocess.py --help
```

Per compilare il modulo C++, dalla cartella del progetto:

```bash
cmake -S . -B build
cmake --build build --config Release
```

Il target CMake si chiama `ipaproject`; con un generatore a configurazione singola l'eseguibile è prodotto sotto `build/bin/`.

Prima di eseguirlo, adattare in `IPA/functions.cpp` i percorsi `DEFAULT_IMAGES_DIR`, `DEFAULT_LABELS_DIR` e `DEFAULT_RESULTS_DIR`, attualmente riferiti a una cartella locale. Il modulo C++ ricrea la propria directory dei risultati: usare un percorso dedicato se si vogliono conservare i CSV e gli esperimenti già presenti.

## RAG Assessment Macchinari

**Progetto magistrale di Software Engineering.**

Applicazione web per consultare documentazione tecnica e normativa caricata dall'utente, gestire schede di macchinari e generare assessment assistiti da un modello linguistico. Il contesto documentale viene recuperato tramite RAG e fornito al modello insieme ai dati del macchinario.

Gli assessment sono risultati generati da un prototipo software e richiedono la verifica di una persona competente; l'applicazione non costituisce una certificazione di conformità.

**Documentazione:** [README del progetto](progetti_magistrale/Software_Engineering_project-master/README.md)

### Funzionalità e architettura

- Caricamento e gestione dei PDF, con estrazione del testo, suddivisione in chunk e memorizzazione degli embedding.
- Ricerca semantica mediante pgvector, con riferimenti ai documenti e alle pagine quando disponibili.
- Creazione, modifica, ricerca ed eliminazione dei macchinari.
- Estrazione assistita dei dati di un macchinario da un manuale PDF.
- Generazione e storico degli assessment, con rilievi, raccomandazioni e fonti quando restituite dalla modalità strutturata.
- Chat generale sui documenti e chat contestualizzata a un assessment.
- Esportazione del report del macchinario in PDF e delle conversazioni in CSV.
- Accesso autenticato tramite Spring Security e interfaccia HTML/CSS/JavaScript.

| Livello | Implementazione |
|---|---|
| Applicazione web | Spring Boot 3.4.2, controller REST, Thymeleaf e risorse statiche |
| Logica applicativa | Service per documenti, retrieval, macchinari, assessment e chat |
| Persistenza | Spring Data JPA e PostgreSQL 16 |
| Ricerca vettoriale | Spring AI 1.0.0-M5 e pgvector, indice HNSW e distanza coseno |
| Embedding | Ollama locale con `nomic-embed-text`, dimensione configurata: 768 |
| Generazione testuale | API Regolo.ai compatibile OpenAI; modello configurato: `Llama-3.3-70B-Instruct` |

Per leggere il codice, partire da `SpringaiApplication.java`, poi consultare `config/`, `controller/`, `service/`, `model/` e `repository/` sotto `src/main/java/it/unicas/spring/springai/`.

### Requisiti e avvio

- JDK 17 o successivo compatibile con il progetto.
- Docker con Docker Compose per PostgreSQL e pgvector.
- Ollama installato e in esecuzione, con il modello per gli embedding disponibile.
- Una propria chiave Regolo.ai e accesso al modello chat configurato.
- Maven, fornito anche tramite il wrapper `mvnw` incluso.

Entrare nella cartella e avviare il database:

```bash
cd progetti_magistrale/Software_Engineering_project-master
docker compose up -d
```

Con il servizio Ollama già attivo, scaricare il modello degli embedding:

```bash
ollama pull nomic-embed-text
```

Il database è esposto su `localhost:5432`, Ollama è configurato su `localhost:11434` e l'applicazione web su `localhost:8080`.

Impostare una propria chiave, sostituendo il testo del segnaposto:

```bash
export SPRING_AI_OPENAI_APIKEY="LA_TUA_CHIAVE_REGOLO"
```

La variabile Spring sovrascrive `spring.ai.openai.api-key` senza richiedere l'inserimento della chiave nel repository. Il nome segue le regole di [configurazione esterna di Spring Boot](https://docs.spring.io/spring-boot/3.4/reference/features/external-config.html): punti sostituiti da underscore e trattini rimossi. Se si modifica la configurazione del database, allineare anche le proprietà `spring.datasource.*`, eventualmente tramite `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`.

Il file `.env.example` è un modello di configurazione: la sola creazione di un file `.env` non lo rende automaticamente disponibile al processo avviato con Maven. Nella configurazione corrente, le proprietà di connessione e API non fanno riferimento alle variabili `REGOLO_API_KEY` e `POSTGRES_*` dell'esempio.

Avviare il backend:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Aprire [http://localhost:8080](http://localhost:8080). Gli account dimostrativi sono descritti nel README del progetto e creati dal relativo inizializzatore.

Il file `init-db.sql` prepara lo schema applicativo nella prima inizializzazione del volume PostgreSQL. Il container Docker fornito avvia il database; backend Java e servizio Ollama devono essere avviati separatamente.

### Flusso di utilizzo e API

1. Accedere all'applicazione.
2. Caricare i PDF di riferimento e attendere l'elaborazione.
3. Inserire i dati del macchinario oppure estrarli da un manuale PDF e verificarli.
4. Generare un assessment e consultare i riferimenti documentali disponibili.
5. Approfondire gli esiti tramite la chat contestualizzata e, se necessario, esportare il report.

| Area | Endpoint rappresentativi |
|---|---|
| Documenti | `POST /api/documents/upload`, `GET /api/documents`, `GET /api/documents/{id}/download` |
| Macchinari | `GET/POST /api/macchinari`, `PUT/DELETE /api/macchinari/{id}` |
| Estrazione da manuale | `POST /api/macchinari/manual/extract` |
| Assessment | `POST /api/macchinari/{id}/assessment`, `GET /api/assessments/{id}` |
| Chat | `POST /api/chatbot/chat`, `POST /api/assessments/{id}/chat` |
| Report PDF | `GET /api/macchinari/{id}/assessments/latest/pdf` |

Le API richiedono autenticazione. I modelli e i servizi esterni devono essere disponibili per le funzioni che li utilizzano.

## Sistema di gestione degli eventi musicali

**Progetto triennale di Ingegneria del Software — gruppo MAGiC-V.**

Applicazione Java a interfaccia testuale per supportare un agente nell'organizzazione di eventi musicali e nella gestione delle convocazioni degli artisti.

**Documentazione:** [README del progetto](progetti_triennale/ingegneria_del_software_triennale/README.md) · [Relazione di progetto, 70 pagine](progetti_triennale/ingegneria_del_software_triennale/Sistema%20gestione%20eventi%20musicali.pdf)

### Funzionalità e progettazione

- Gestione di cantanti, strumentisti, strumenti, disponibilità e cachet.
- Pianificazione di esibizioni, concerti ed eventi strumentali.
- Verifica dei vincoli sulla composizione dell'evento e delle disponibilità.
- Ricerca degli artisti e degli eventi tramite diversi criteri.
- Consultazione e accettazione delle convocazioni da parte dei musicisti.
- Conferma degli eventi e calcolo dei costi.
- Notifiche email e produzione dei report sugli eventi e sui compensi.

L'architettura segue il modello **Boundary–Control–Entity**:

| Package | Responsabilità |
|---|---|
| `boundary` | Menu testuali per agente, musicista e attore Tempo |
| `controller` | Casi d'uso, controlli sui dati e coordinamento delle operazioni |
| `entity` | Modello del dominio: musicisti, eventi, partecipazioni e report |
| `database` | Accesso a SQLite tramite JDBC e classi DAO |
| `controller.email` | Servizi di invio delle notifiche |

Il database locale è `db.sqlite`; lo schema è disponibile in `src/main/resources/schema.sql`. La generazione dei report è esposta nel menu dell'attore **Tempo**: il codice incluso non configura un servizio autonomo di pianificazione mensile.

Per studiare il progetto, partire da `src/main/java/Main.java`, poi da `controller/GestioneEventi.java` e dalle classi DAO.

### Requisiti e avvio

Il progetto è stato sviluppato con **JDK 23** e include il wrapper Gradle. Non richiede l'installazione di un server database esterno.

Per compilare e avviare la dimostrazione senza utilizzare servizi SMTP:

```bash
cd progetti_triennale/ingegneria_del_software_triennale
chmod +x gradlew
./gradlew build
./gradlew run --args="--no-email"
```

Il menu iniziale permette di scegliere l'attore agente, musicista o Tempo. L'opzione `--no-email`, gestita in `Main.java`, evita l'inizializzazione dei servizi email.

Per abilitare le notifiche occorre configurare un servizio SMTP proprio. I parametri di inizializzazione sono nel codice di `Main.java`; eventuali credenziali versionate devono essere rimosse dal sorgente e sostituite con una configurazione esterna prima di utilizzare il servizio con account reali.

È disponibile anche un Dockerfile. Per costruire ed eseguire l'immagine locale in modalità dimostrativa:

```bash
docker build -t eventi-musicali .
docker run --rm -it eventi-musicali --no-email
```

Questi comandi vanno eseguiti dalla cartella del progetto. Il Dockerfile usa una build a più stadi per produrre ed eseguire il JAR dell'applicazione.

## PokerMind

**Progetto triennale in Python con architettura client-server e materiale per l'analisi dati in KNIME.**

Gioco nel quale si tenta di dedurre una carta nascosta a partire dalle mani di due giocatori e dall'esito di due round. Il server genera le partite e offre le operazioni di gioco; il client invia richieste attraverso socket TCP locali e riceve risposte JSON.

**Documentazione:** [README del progetto](progetti_triennale/PokerMind-main/README.md)

### Componenti e funzionalità

- Modellazione di carte, semi, valori, mani e graduatoria delle combinazioni.
- Generazione delle partite con soglia di difficoltà configurabile.
- Comandi per visualizzare la partita, formulare un'ipotesi, rivelare la carta e iniziare una nuova partita.
- Separazione tra interfaccia del servizio, Proxy del client e Skeleton del server.
- Worker concorrenti per la generazione delle partite e gestione delle connessioni.
- Modalità di generazione di dati CSV per esperimenti e analisi.

| Percorso | Contenuto |
|---|---|
| `src/server.py` | Avvio del server e parametri di configurazione |
| `src/client.py` | Interfaccia a riga di comando |
| `src/service/` | Proxy, Skeleton, interfaccia e gestione della concorrenza |
| `src/game/` | Regole, modello delle carte e confronto delle mani |
| `tests/hands.py` | Test delle combinazioni e dei confronti tra mani |
| `static/Project.knwf` | Workflow KNIME con preprocessing, classificatori e valutazione |
| `static/dataset_1000000.csv.7z` | Archivio del dataset per gli esperimenti |

Nel workflow KNIME sono presenti nodi per Decision Tree, Random Forest, Gradient Boosted Trees, regressione logistica e Naive Bayes. Il workflow e l'archivio dati sono risorse separate dal normale avvio del gioco.

### Requisiti e avvio

È richiesto **Python 3.11 o successivo**. L'esecuzione diretta del client e del server utilizza la libreria standard Python e non richiede dipendenze applicative esterne.

Nel primo terminale:

```bash
cd progetti_triennale/PokerMind-main
python3 src/server.py --threshold 12 --port 2231
```

Lasciare il server in esecuzione. In un secondo terminale, dalla stessa cartella del progetto:

```bash
python3 src/client.py --port 2231 show
python3 src/client.py --port 2231 guess 7 spades
python3 src/client.py --port 2231 reveal
python3 src/client.py --port 2231 new_game
```

Il collegamento è configurato su `127.0.0.1`; la porta predefinita è `2231`. I semi ammessi dal client sono `hearts`, `diamonds`, `clubs` e `spades`.

Per generare 100 esempi in formato numerico, con il server già attivo:

```bash
python3 src/client.py reveal --training-mode 100 --numerical > esempi.csv
```

Per gli esperimenti KNIME occorre importare `static/Project.knwf`, estrarre il dataset dall'archivio `.7z` e adattare i percorsi dei nodi CSV Reader al proprio ambiente.

## Test e valutazione

I comandi di questa sezione vanno eseguiti dalla cartella del rispettivo progetto.

| Progetto | Verifiche presenti | Comando o punto di ingresso |
|---|---|---|
| Eventi musicali | Test JUnit sui casi d'uso, sui vincoli di pianificazione, sui musicisti, sulle convocazioni e sui report | `./gradlew test` |
| PokerMind | Test `unittest` delle combinazioni e dei confronti tra mani | `PYTHONPATH=src python3 -m unittest discover -s tests -p hands.py` |
| RAG Assessment Macchinari | Test di integrazione su login, upload, validazioni, assessment e chat; controlli dei contratti dell'interfaccia | `./mvnw test` |
| Bone Fracture Detection | Valutazione sperimentale del modello e delle detection, con report e curve | `ML/fracture_fp_filter.py` e `ML/evaluate_test_postprocess.py` |

I test del progetto RAG usano il profilo `test`; il relativo file di configurazione prevede H2 in memoria e i test di integrazione sostituiscono i componenti AI con mock dove previsto.

Per il progetto di visione, una valutazione sui dati non equivale a una suite di test unitari. Metriche, split, soglie e configurazioni vanno letti insieme ai relativi report.

## Competenze applicate

| Area | Evidenza nei progetti |
|---|---|
| Programmazione orientata agli oggetti | Modelli di dominio Java e Python, gestione delle responsabilità e delle interfacce |
| Architetture e pattern | Boundary–Control–Entity e DAO negli eventi musicali; Proxy–Skeleton in PokerMind; controller, service e repository nell'applicazione Spring |
| Persistenza dei dati | SQLite/JDBC, PostgreSQL/JPA e gestione vettoriale con pgvector |
| Sistemi client-server e concorrenza | Comunicazione TCP/JSON e worker in PokerMind |
| Image Processing e Machine Learning | ROI, feature GLCM/LBP/HOG, preprocessing, classificazione supervisionata, NMS e valutazione |
| Integrazione di modelli linguistici | Ingestione PDF, embedding, retrieval semantico e generazione contestualizzata |
| Build e distribuzione | Gradle, Maven, CMake, ambienti Python, Docker e Docker Compose |
| Verifica del software | Test unitari, test di integrazione e analisi sperimentale delle prestazioni |

## Crediti e licenze

La raccolta è curata da **Vincenzo Sauzullo**. I lavori di gruppo mantengono i rispettivi autori e riferimenti:

- **Bone Fracture Detection:** Daniela Spitaleri, Vincenzo Sauzullo e Marco Vizzaccaro, come indicato nella relazione.
- **Eventi musicali — MAGiC-V:** Gennaro Ammendola, Mario Anaclerio, Carlo Aquino, Angelo Carrillo e Vincenzo Sauzullo, come indicato nell'elaborato di progetto.
- **PokerMind:** Midblyte, AngelogyPythITA e Sauz926, come indicato nel README originale e nei metadati del pacchetto.
- **RAG Assessment Macchinari:** i riferimenti del progetto sono conservati nella relativa cartella e nella documentazione originale.

PokerMind include un [file LICENSE con licenza MIT](progetti_triennale/PokerMind-main/LICENSE). Per gli altri componenti, le librerie terze e i dati, fare riferimento ai rispettivi file di licenza e alle condizioni delle fonti originali. Non è presente una licenza unica che uniformi automaticamente l'intero portfolio.

Per approfondimenti sui progetti e sulle attività svolte: [vincenzo.sauzullo@gmail.com](mailto:vincenzo.sauzullo@gmail.com) oppure [LinkedIn](https://www.linkedin.com/in/vincenzo-sauzullo-9aaa09448/).

## English overview

This repository collects four academic projects from my Computer Engineering studies at the University of Naples Federico II and the University of Cassino and Southern Lazio. Each folder is an independent project with its own requirements and entry points.

| Project | Focus | Main technologies |
|---|---|---|
| **Bone Fracture Detection** | Candidate region detection, feature extraction, supervised filtering and evaluation of pediatric forearm X-rays | C++, OpenCV, Python, scikit-learn |
| **RAG Assessment Macchinari** | Document-grounded assistance for machinery assessment, PDF ingestion and contextual chat | Java, Spring Boot, Spring AI, PostgreSQL, pgvector, Ollama |
| **Musical Event Management** | Artist availability, event planning, invitations, persistence and reporting | Java, SQLite, JDBC, Gradle, Jakarta Mail |
| **PokerMind** | Hidden-card deduction game, local TCP communication, concurrency and data generation | Python, JSON, Proxy–Skeleton, KNIME |

The detailed sections above provide architecture notes, setup commands, source-code entry points and testing instructions. The vision project includes a 100-image sample and saved experimental reports; the full training corpus is not included. The RAG application requires both a database and the configured AI services.

Several projects were developed collaboratively. Original authorship, documentation and component-specific licenses are retained in the corresponding folders.
