# Testkonzept

Nach der TBZ-Vorlage für Testkonzepte, auf die Grösse dieses Projekts angepasst.

## 1. Ziel und Testobjekt

Getestet wird BetterIntranet: Frontend (React), Backend (Spring Boot, modularer Monolith) und das
Mock-Backend `school-mock`. Ziel ist, dass jedes Feature durch automatisierte Tests abgesichert ist,
dass externe Schnittstellen in Tests nie echt aufgerufen werden und dass die Pipeline bei jedem
Pull Request und bei jedem Merge auf `main` Resultate und Coverage sichtbar macht.

## 2. Teststrategie

Wir arbeiten nach **Test Driven Development** (Red, Green, Refactor). Für jedes Feature entstehen
zuerst alle Tests (Red-Commit), dann die Implementierung (Green-Commit), dann das Aufräumen.

### Testpyramide

| Ebene         | Backend / school-mock                                                   | Frontend                                              | Anteil |
|---------------|-------------------------------------------------------------------------|-------------------------------------------------------|--------|
| Unit          | JUnit 5, Mockito, AssertJ. Handler, Domain-Logik (z.B. Notenrundung), Mapper. Alle Abhängigkeiten gemockt. | Vitest. Hooks, Utilities, Registry.                     | gross  |
| Component     | –                                                                       | Vitest + Testing Library. Komponenten mit MSW-gemockter API. | mittel |
| Integration   | `@SpringBootTest` + MockMvc: Controller → Gateway → Handler. Upstreams via WireMock oder `@MockitoBean`. Suffix `*IT`, Failsafe. | Seiten mit Router und MSW.                              | mittel |
| Architektur   | ArchUnit: Modulgrenzen, Schichten, keine Zyklen.                        | Lint-Regeln.                                           | klein  |
| Manuell       | Smoke-Test über `docker compose up`, Klick durch alle Module.           |                                                       | klein  |

### Was wird gemockt und womit

| Schnittstelle                  | Unit-Test                         | Integrationstest                              |
|--------------------------------|-----------------------------------|-----------------------------------------------|
| `stundenplan.geekz.ch`         | Mockito-Mock des `TimetableClient` | WireMock mit echten Antworten aus `docs/fixtures` |
| `school-mock` (Klassen, Kurse, Absenzen, Noten) | Mockito-Mock des jeweiligen Clients | WireMock oder `@MockitoBean`                  |
| Gateway / Handler              | Mockito                           | echter Spring-Kontext mit Test-Modul `testmodule` |
| Backend-API im Frontend        | MSW-Handler                       | MSW-Handler (unbehandelte Requests schlagen fehl) |

Regel: Tests machen keine Netzwerkaufrufe. Das MSW-Setup im Frontend und die WireMock-Server im
Backend sorgen dafür, dass jeder vergessene Mock sofort als Fehler sichtbar wird.

## 3. Testumgebung

| Umgebung | Zweck                                         | Wie                                                  |
|----------|-----------------------------------------------|------------------------------------------------------|
| Lokal    | TDD-Zyklus, schnelle Rückmeldung               | `./mvnw verify`, `npm run test:coverage`             |
| CI       | Jeder Push auf einen PR und jeder Merge auf `main` | GitHub Actions, drei parallele Jobs, Coverage-Gates |
| Docker   | Manueller Smoke-Test des Gesamtsystems         | `docker compose up --build`, Frontend auf Port 3000  |

Versionen: Java 21 (Build), Spring Boot 4.1, Node 22, Vitest 5, MSW 3, WireMock 3, ArchUnit 1.4, JaCoCo 0.8.

## 4. Testfälle (Auszug)

| ID   | Modul      | Testfall                                                            | Ebene       |
|------|------------|---------------------------------------------------------------------|-------------|
| C-01 | core       | Handler wird über generischen Nachrichtentyp gefunden               | Unit        |
| C-02 | core       | Deaktiviertes Modul → `ModuleDisabledException` → HTTP 503 mit `module` | Unit + IT |
| C-03 | core       | `X-User` fehlt → Default-Benutzer                                   | Unit + IT   |
| C-04 | core       | Modulgrenzen eingehalten                                            | Architektur |
| T-01 | timetable  | Upstream-JSON wird auf Lektionen gemappt (Lehrperson, Fach, Raum, Status) | Unit  |
| T-02 | timetable  | Upstream 500 / nicht erreichbar → HTTP 502 mit `upstream`           | IT (WireMock) |
| T-03 | timetable  | Zweiter Aufruf innerhalb 5 Minuten trifft den Upstream nicht (Cache) | IT         |
| G-01 | grades     | Gewichteter Durchschnitt und Zeugnisrundung auf 0.5                 | Unit        |
| G-02 | grades     | Unbekannter Benutzer → 404                                          | IT          |
| A-01 | absences   | Zusammenfassung zählt offen/entschuldigt/unentschuldigt und Lektionen | Unit      |
| D-01 | dashboard  | Ferien aus `timetable` plus eigene Termine, Vergangenes gefiltert   | Unit        |
| D-02 | dashboard  | `timetable` deaktiviert → Dashboard liefert trotzdem 200            | IT          |
| M-01 | school-mock | Fixtures werden korrekt geliefert, unbekannter Benutzer → 404       | Unit + IT   |
| F-01 | frontend   | Navigation zeigt nur aktive Module                                  | Component   |
| F-02 | frontend   | Route eines deaktivierten Moduls zeigt Hinweis-Karte                | Component   |
| F-03 | frontend   | API-Fehler (ProblemDetail) wird als `ApiError` mit Status und Detail geworfen | Unit |
| F-04 | frontend   | Notenseite zeigt Durchschnitte und markiert ungenügende Noten       | Component   |

Die vollständige Liste ergibt sich aus den Testklassen; jede Testmethode trägt einen sprechenden
Namen oder `@DisplayName`.

## 5. Abnahmekriterien

- Alle Tests grün in der Pipeline.
- Line Coverage mindestens 80 % in Backend, school-mock und Frontend (Gate schlägt sonst fehl).
- ArchUnit-Regeln verletzungsfrei.
- Jeder Pull Request hat eine sichtbare Red-Commit vor der Green-Commit.
- Mindestens ein Review-Approve pro Pull Request.

## 6. Reporting

| Report                   | Wo                                                        |
|--------------------------|-----------------------------------------------------------|
| JUnit-Resultate          | Als Check-Annotation am PR (dorny/test-reporter), als Artefakt |
| JaCoCo Backend           | `backend/target/site/jacoco`, auf Pages unter `/backend/jacoco/` |
| JaCoCo school-mock       | `school-mock/target/site/jacoco`, auf Pages unter `/school-mock/jacoco/` |
| Vitest Coverage          | `frontend/coverage`, auf Pages unter `/frontend/coverage/` |
| Übersicht                | Pages-Startseite mit Tests, Fehlern, Dauer und Links pro Projekt |

GitHub Pages wird bei jedem Merge auf `main` vom Job `deploy-reports` aktualisiert.

## 7. Fehlerbehandlung und Priorisierung

Gefundene Fehler werden als GitHub Issue erfasst und nach Kritikalität priorisiert: zuerst Fehler,
die eine Seite unbenutzbar machen (z.B. 500 statt 503 bei deaktiviertem Modul), danach fachliche
Fehler (falsche Rundung), zuletzt Darstellung. Bei gleicher Kritikalität zuerst die schnell
behebbaren. Fehler werden mit einem fehlschlagenden Test reproduziert, bevor sie behoben werden.
