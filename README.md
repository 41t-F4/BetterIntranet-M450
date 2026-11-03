# BetterIntranet (M450)

Neubau des TBZ-Schulintranets als modulare Web-Anwendung. Projektarbeit im Modul 450
«Applikationen testen».

| Teil           | Technologie                                   | Port |
|----------------|-----------------------------------------------|------|
| `frontend/`    | React 19, TypeScript, Vite, React Router      | 5173 (dev), 3000 (Docker) |
| `backend/`     | Spring Boot 4, Java 21, modularer Monolith    | 8080 |
| `school-mock/` | Spring Boot 4, gemockte Schul-Schnittstellen  | 8081 |

Der Stundenplan kommt echt von `stundenplan.geekz.ch` (nur über das Backend). Klassen, Kurse,
Absenzen und Noten liefert das Mock-Backend, weil wir keinen Zugang zu den echten Schulsystemen haben.

Die Architektur (modularer Monolith mit Command/Query Gateways, abschaltbare Module) ist in
[CLAUDE.md](CLAUDE.md) und [docs/architektur.md](docs/architektur.md) beschrieben.

## Starten

```bash
# alles zusammen (Frontend auf http://localhost:3000)
docker compose up --build

# einzeln
cd school-mock && ./mvnw spring-boot:run
cd backend     && ./mvnw spring-boot:run
cd frontend    && npm ci && npm run dev
```

## Tests ausführen

```bash
cd backend     && ./mvnw verify            # Unit + Integration + JaCoCo (Gate 80 % Lines)
cd school-mock && ./mvnw verify
cd frontend    && npm run test:coverage    # Vitest + Coverage (Gate 80 % Lines)
```

Reports liegen danach unter `backend/target/site/jacoco`, `school-mock/target/site/jacoco`,
`frontend/coverage` und `frontend/reports/junit.xml`. Die Pipeline publiziert sie bei jedem Merge
auf `main` nach GitHub Pages.

## Teststrategie (kurz)

Wir arbeiten nach **Test Driven Development**: Für jedes Feature werden zuerst alle Tests
geschrieben und als «Red»-Commit festgehalten, danach die Implementierung («Green») und das
Aufräumen («Refactor»). Die Commit-Historie zeigt diesen Zyklus.

| Ebene        | Backend                                                        | Frontend                                      |
|--------------|----------------------------------------------------------------|-----------------------------------------------|
| Unit         | JUnit 5, Mockito, AssertJ. Handler, Domain, Mapper isoliert.   | Vitest. Hooks, Utils.                          |
| Component    | –                                                              | Vitest + React Testing Library, API via MSW.   |
| Integration  | `@SpringBootTest` + MockMvc. Externe HTTP-Dienste via WireMock. | Seiten mit Router + MSW.                       |
| Architektur  | ArchUnit erzwingt Modulgrenzen und Schichten.                  | Lint-Regeln gegen Imports zwischen Modulen.    |

Externe Schnittstellen (`stundenplan.geekz.ch`, Mock-Backend) werden in Tests immer weggemockt,
Tests machen keine Netzwerkaufrufe. Das vollständige Testkonzept steht in
[docs/testkonzept.md](docs/testkonzept.md).

## Deklaration der KI-Nutzung

Dieses Projekt wird mit **Claude Code** (Anthropic) als Coding-Agent entwickelt. Claude schreibt
Tests und Implementierung auf Feature-Branches und eröffnet Pull Requests. Das Team trifft alle
Architekturentscheidungen, reviewt jeden Pull Request auf GitHub, gibt Feedback und merged. Die
Commits tragen Claude als Co-Author. Die Arbeitsregeln für den Agenten stehen in
[CLAUDE.md](CLAUDE.md).

## Dokumentation

- [Planung](docs/planung.md)
- [Architektur](docs/architektur.md)
- [Testkonzept](docs/testkonzept.md)
- [Reflexion](docs/reflexion.md)
