# CLAUDE.md – BetterIntranet (Modul 450)

Diese Datei ist der Arbeitsvertrag für Claude Code in diesem Repository. Sie beschreibt den Auftrag,
die Architektur, die Arbeitsweise (TDD, Branches, Reviews, CI/CD) und die Regeln, die bei jeder
Aufgabe gelten. Lies sie vollständig, bevor du etwas änderst.

---

## 1. Auftrag

Das Schul-Intranet der **Technischen Berufsschule Zürich (TBZ)** ist in die Jahre gekommen. Wir bauen
es als **BetterIntranet** neu: moderner, modularer, testbar.

Das bestehende Intranet bietet (siehe Screenshot im Projekt-Kickoff):

| Bereich               | Inhalt                                                                 |
|-----------------------|------------------------------------------------------------------------|
| Header                | TBZ-Logo, angemeldeter Benutzer (z.B. `nicola.wehrli`), Info/Mail/Favoriten/Logout |
| Hauptnavigation       | Home, Klassen & Kurse, Stundenplan, Absenzen, Noten                     |
| Newsticker            | Schulnachrichten                                                        |
| Termine der Schule    | Kalendereinträge, Abonnieren, Export Excel/PDF, Alle anzeigen           |
| Prävention            | Hinweis-Box (147.ch)                                                    |
| Meine Links           | Meine Kurse, Absenzen: Einträge                                         |
| Quick Links           | Service Desk, Absenzen, Dateiablage, Hilfe, Mediothek, OrgWeb, Tagesagenda, M365 |

**Inhaltlicher Scope:** Es ist das Intranet der **Berufsschule TBZ**. Es enthält die ICT-Module
(z.B. «450 Applikationen testen», «306 IT-Kleinprojekt abwickeln») und Berufsschulfächer wie ABU
(«Allgemeinbildung Gesellschaft») und Englisch. **BMS** (Berufsmaturitätsschule) und **ÜK**
(überbetriebliche Kurse) gehören nicht dazu und erscheinen nirgends, weder in Noten, Kursen noch
Absenzen. Massgebend für die Fächer ist der echte Stundenplan der Klasse.

Das neue Intranet muss mindestens diese Funktionen abdecken:

1. **Dashboard / Home** mit Newsticker, Terminen, Prävention, Meine Links, Quick Links
2. **Meine Klassen** (gemockt)
3. **Meine Kurse** (gemockt)
4. **Stundenplan** (echt, über unser Backend von `stundenplan.geekz.ch` geholt) inkl. Ferien/Feiertage
5. **Absenzen** (gemockt)
6. **Noten** (gemockt)

Schulischer Rahmen (Modul 450 «Applikationen testen»): Der Auftrag liegt unter
`/Users/nwehrli/Documents/GitHub/m450/m450-main/Unterlagen/projekt`. Die Bewertung erfolgt nach
folgender Rubrik, die **jede Entscheidung in diesem Projekt leitet**:

- Kleine Teststrategie-Beschreibung im README.md
- Unit Tests vorhanden
- Integration Tests vorhanden
- Schnittstellen werden zum Testen weggemockt (Mocking-Framework Pflicht)
- Testresultate werden nach dem Ausführen reportet
- Tests laufen in einer Pipeline beim Deploy auf `main` automatisch und werden reportet
- Code Coverage ist als Report einsehbar
- TDD wird angewendet
- Pro Teammitglied 3 Pull Requests, die aktiv kommentiert und challenged werden
- Kleine Dokumentation: Planung (ohne Gantt), Architektur-Visualisierung, Testkonzept, Reflexion über TDD und Code Reviews
- KI-Nutzung wird im README.md deklariert (dieses Projekt wird mit Claude Code als Agent entwickelt; das muss transparent im README stehen)

**Wichtig:** Weil KI eingesetzt wird, wird das Projekt stärker nach **Aufwand und Nachvollziehbarkeit**
als nach reiner Feature-Menge bewertet. Lieber weniger Features mit sauberer Testabdeckung als viele
Features mit dünnen Tests.

---

## 2. Tech-Stack

| Schicht                 | Technologie                                                       |
|-------------------------|-------------------------------------------------------------------|
| Frontend                | React + TypeScript (Vite), React Router, Vitest + React Testing Library, MSW für API-Mocks |
| Backend (Hauptsystem)   | Spring Boot 3 + Java 21, Maven (über Maven Wrapper `./mvnw`, keine lokale Installation nötig), JUnit 5, Mockito, AssertJ, Spring Boot Test, WireMock für externe HTTP-Schnittstellen, JaCoCo für Coverage |
| Backend (Mock-Server)   | Zweiter, eigenständiger Spring-Boot-Service («school-mock»), der die Schnittstellen der Schulsysteme simuliert: Meine Klassen, Meine Kurse, Absenzen, Noten |
| Externe Schnittstelle   | `https://stundenplan.geekz.ch` (Stundenplan + Ferien), nur vom Backend aufgerufen, nie direkt vom Frontend |
| CI/CD                   | GitHub Actions (Repo liegt auf GitHub: `41t-F4/BetterIntranet-M450`) |
| Containerisierung       | Docker + docker-compose für lokales Hochfahren aller drei Services |

Hinweis: Die Unterlagen nennen GitLab; wir arbeiten auf GitHub, daher GitHub Actions statt
`.gitlab-ci.yml`. Die geforderten Stages (Build, Test, Deploy der Reports) werden 1:1 abgebildet.

---

## 3. Repository-Struktur (Monorepo)

```
BetterIntranet-M450/
├── CLAUDE.md                 # diese Datei
├── README.md                 # Projektbeschreibung, Teststrategie, KI-Deklaration, Setup
├── docs/                     # Planung, Architektur (Diagramme), Testkonzept, Reflexion
│   ├── architektur.md
│   ├── testkonzept.md
│   ├── planung.md
│   └── reflexion.md
├── frontend/                 # React + TypeScript
├── backend/                  # Spring Boot Hauptsystem (modularer Monolith)
├── school-mock/              # Zweites Backend: gemockte Schul-Schnittstellen
├── docker-compose.yml
└── .github/workflows/        # CI/CD
```

---

## 4. Architektur: Modularer Monolith mit Command Gateways

### 4.1 Grundidee

Das Backend ist ein **modularer Monolith**. Jedes fachliche Modul ist in sich geschlossen und kennt
die anderen Module **nicht direkt**. Module kommunizieren ausschliesslich über **Command Gateways**:
Ein Modul schickt ein Command (oder eine Query) an ein Gateway, das Gateway leitet es an den
registrierten Handler weiter. Kein Modul importiert Klassen eines anderen Moduls, ausser den
öffentlichen Command-/Query-/Result-Typen aus einem gemeinsamen `api`-Paket.

Damit erreichen wir:

- **Abschaltbarkeit:** Ein Modul kann per Konfiguration deaktiviert werden (`modules.noten.enabled=false`).
  Das Gateway liefert dann eine definierte Antwort (`ModuleDisabledException` -> HTTP 503 mit klarer
  Meldung), das Frontend blendet die Kachel/Navigation aus.
- **Austauschbarkeit:** Ein Modul (z.B. Noten gemockt) kann später durch eine echte Anbindung ersetzt
  werden, ohne dass andere Module angepasst werden müssen. Nur der Handler wird ausgetauscht.
- **Testbarkeit:** Jedes Modul lässt sich isoliert testen, indem das Gateway bzw. die Handler gemockt werden.

### 4.2 Module

| Modul          | Verantwortung                                                     | Datenquelle                              |
|----------------|-------------------------------------------------------------------|------------------------------------------|
| `core`         | Command/Query Gateway, Modul-Registry, Enable/Disable, Fehlerbehandlung, gemeinsame Typen | –                                        |
| `user`         | Angemeldeter Benutzer, Profil, Klassenzugehörigkeit               | Vorerst statisch/gemockt                 |
| `dashboard`    | Newsticker, Termine der Schule, Prävention, Meine Links, Quick Links | Termine = Ferien (`timetable`-Modul via Gateway) + eigene Mock-Termine; Rest aus JSON |
| `timetable`    | Stundenplan und Ferien                                            | `stundenplan.geekz.ch` (echt, via HTTP-Client) |
| `classes`      | Meine Klassen                                                     | `school-mock` Backend                    |
| `courses`      | Meine Kurse                                                       | `school-mock` Backend                    |
| `absences`     | Absenzen (Einträge, Status), **nur Anzeige, keine Erfassung**     | `school-mock` Backend                    |
| `grades`       | Noten (Fächer, Semester, Durchschnitt)                            | `school-mock` Backend                    |

### 4.3 Paketstruktur im Backend

```
backend/src/main/java/ch/tbz/betterintranet/
├── core/
│   ├── gateway/        CommandGateway, QueryGateway, Handler-Interfaces, Registry
│   ├── module/         ModuleDescriptor, ModuleRegistry, @ConditionalOnModule
│   └── error/          ModuleDisabledException, UpstreamUnavailableException, GlobalExceptionHandler
├── timetable/
│   ├── api/            Queries + Result-DTOs (öffentlich, darf von anderen Modulen importiert werden)
│   ├── application/    Query-Handler (Use Cases)
│   ├── domain/         Fachliche Typen (Lesson, Holiday, ...)
│   ├── infrastructure/ HTTP-Client zu stundenplan.geekz.ch, Mapper, Cache
│   └── web/            REST-Controller (nur dünne Schicht, ruft Gateway auf)
├── grades/             gleiche Struktur
├── absences/           gleiche Struktur
├── classes/            gleiche Struktur
├── courses/            gleiche Struktur
├── dashboard/          gleiche Struktur
└── user/               gleiche Struktur
```

Regeln:

- Controller sprechen **nur** mit dem Gateway, nie direkt mit Handlern oder Infrastruktur.
- Handler sprechen mit Domain + Infrastruktur des eigenen Moduls.
- Modul-übergreifender Zugriff läuft **nur** über `api`-Pakete und das Gateway.
- Ein ArchUnit-Test (`ModuleBoundariesTest`) erzwingt diese Regeln automatisch.

### 4.4 Gateway-Vertrag (Skizze)

```java
public interface Command<R> {}
public interface Query<R> {}

public interface CommandHandler<C extends Command<R>, R> { R handle(C command); }
public interface QueryHandler<Q extends Query<R>, R> { R handle(Q query); }

public interface CommandGateway { <R> R send(Command<R> command); }
public interface QueryGateway   { <R> R ask(Query<R> query); }
```

Die Registry findet Handler über Spring (alle Beans, die `QueryHandler`/`CommandHandler` implementieren),
prüft, ob das zugehörige Modul aktiviert ist, und wirft sonst `ModuleDisabledException`.

### 4.5 Frontend-Architektur

```
frontend/src/
├── app/            Router, Layout (Header, Navigation, Footer), Providers
├── modules/
│   ├── dashboard/  Newsticker, Termine, Prävention, Links
│   ├── timetable/
│   ├── classes/
│   ├── courses/
│   ├── absences/
│   └── grades/
├── shared/         UI-Komponenten, API-Client, Typen, Hooks
└── test/           Test-Setup, MSW-Handler, Fixtures
```

Jedes Frontend-Modul exportiert seine Routen und Navigationseinträge. Die Navigation fragt beim
Backend `GET /api/modules` ab, welche Module aktiv sind, und zeigt nur diese an.

### 4.6 Design (Vorlage: «Marks»-Figma-Board)

Das Frontend übernimmt den Stil des Projekts «Marks» (Figma-Board vom Team). Kennzeichen:

- **Top-Bar** dunkel (fast schwarz, ca. `#1f1f1f`), Logo/Projektname links bzw. zentriert,
  rechts der angemeldete Benutzer mit Dropdown. Darunter eine **Breadcrumb**-Zeile
  (`Home > Noten`).
- **Seitenhintergrund** hellgrau (ca. `#f2f2f2`), Inhalte in **weissen Karten** mit kleinem Radius
  (6 bis 8 px) und sehr dezentem Schatten. Viel Weissraum, keine Rahmen um Tabellen.
- **Akzentfarben** aus den Diagrammen: Türkis `#2bb3c0`, Blau `#2f6fb6`, Violett `#7b61c9`.
  Primäre Aktion (Buttons, FAB) in Blau. Status-Farben: Grün für entschuldigt/bestanden, Rot für
  unentschuldigt/ungenügend (< 4.0), Orange für offen.
- **Typografie**: eine neutrale Sans-Serif (Inter oder System-Stack), Kartentitel fett,
  Kennzahlen (Durchschnitte) gross neben dem Titel (`Englisch  5.25`).
- **Noten-Seite**: oben eine Karte «Notendurchschnitte» mit Gesamtdurchschnitt, Semesterwahl und
  Balkendiagramm pro Fach des Semesters (Serien: Durchschnitt, Zeugnis) mit Legende. Darunter
  Abschnitte pro Semester (neuestes zuerst) mit Semesterdurchschnitt, darin je Fach eine Karte mit
  Tabelle (Titel, Datum, Note, Gewichtung).
- **Stundenplan**: Wochenraster, Lektionen als farbige Blöcke mit Fach, Lehrperson, Raum;
  Ausfälle durchgestrichen/grau, Ferien als ganze Spalte markiert.
- **Dashboard**: Kartenraster wie im alten Intranet (Newsticker, Termine, Prävention, Meine Links,
  Quick Links), aber im Marks-Stil statt der farbigen Titelbalken.
- **Listen/Tabellen** (Klassen, Kurse, Absenzen): Karten mit schlichten Tabellen, Kopfzeile grau,
  rechtsbündige Zahlen.
- Responsive: ab 768 px zweispaltig, darunter einspaltig. Navigation bleibt oben.

Design-Tokens liegen als CSS-Variablen in `frontend/src/shared/styles/tokens.css` und werden
überall referenziert, nie hart kodiert. Diagramme werden mit Recharts gebaut.

---

## 5. Schnittstellen

### 5.1 Stundenplan (echt, `stundenplan.geekz.ch`)

Das Backend ruft diese Endpoints auf und gibt die Daten vereinfacht ans Frontend weiter.
Das Frontend ruft `stundenplan.geekz.ch` **niemals** direkt auf.

**Klassenliste / Filter-Metadaten**

```
GET https://stundenplan.geekz.ch/api/rest/view/v1/timetable/filter
    ?resourceType=CLASS&timetableType=STANDARD&start=YYYY-MM-DD&end=YYYY-MM-DD
```

Antwort (gekürzt): `{ "classes": [ { "class": { "id": 7372, "shortName": "PE 26a", "longName": "...", "displayName": "..." }, "classTeacher1": {...}, "department": {...} } ], "departments": [...] }`

Dieser Endpoint liefert **keine Lektionen**, sondern die Liste aller Klassen (ca. 250) mit IDs.
Er wird gebraucht, um aus einem Klassenkürzel (z.B. `PE 26a`) die Klassen-ID zu ermitteln.

**Lektionen einer Klasse**

```
GET https://stundenplan.geekz.ch/api/rest/view/v1/timetable/entries
    ?start=YYYY-MM-DD&end=YYYY-MM-DD&format=5&resourceType=CLASS
    &resources=<classId>&periodTypes=&timetableType=STANDARD&layout=START_TIME
```

Antwort (gekürzt): `{ "format": 5, "days": [ { "date": "2026-11-02", "resource": { "id": 7372, "shortName": "PE 26a" }, "status": "REGULAR", "gridEntries": [ { "duration": { "start": "2026-11-02T08:10", "end": "2026-11-02T09:45" }, "type": "NORMAL_TEACHING_PERIOD", "status": "REGULAR", "position1": [ { "current": { "type": "TEACHER", "shortName": "CAL", "longName": "Calisto" } } ], "position2": [ { "current": { "type": "SUBJECT", "shortName": "117", "longName": "117 Informatik- und Netzinfrastruktur" } } ], "position3": [ { "current": { "type": "ROOM", "shortName": "AU-U60" } } ], "lessonInfo": "..." } ] } ] }`

Mapping-Regel: `position1` = Lehrperson, `position2` = Fach, `position3` = Raum. Bei
`status != REGULAR` (z.B. `CANCELLED`, `SUBSTITUTION`) wird der Status ins eigene DTO übernommen.
Ein `errorCode: "NOT_FOUND"` im Body bedeutet: keine Daten für diesen Zeitraum, kein Fehler.

**Ferien / Feiertage**

```
GET https://stundenplan.geekz.ch/holidays
```

Antwort: `[ { "name": "Herbstferien", "longName": "Herbstferien", "startDate": "2026-10-05", "endDate": "2026-10-18", "bookingAllowed": false }, ... ]`

**Unsere eigene API dafür (Backend -> Frontend)**

```
GET /api/timetable/classes                         -> [{ id, shortName, longName, department }]
GET /api/timetable?classId=7372&from=...&to=...    -> [{ date, start, end, subject, teacher, room, status, info }]
GET /api/timetable/holidays                        -> [{ name, startDate, endDate }]
```

Im Backend: `TimetableClient` (Interface) mit `GeekzTimetableClient` (echt, `RestClient`) als
Implementierung. In Tests wird `TimetableClient` mit Mockito gemockt (Unit) oder `GeekzTimetableClient`
gegen WireMock getestet (Integration). Antworten werden kurz gecached (z.B. 5 Minuten), damit
`stundenplan.geekz.ch` nicht bei jedem Klick belastet wird.

### 5.2 Gemockte Schul-Schnittstellen (`school-mock`)

Zweites Spring-Boot-Backend auf Port `8081`. Es simuliert die Schulsysteme, zu denen wir keinen
Zugang haben. Daten liegen als JSON-Fixtures im Repo und werden pro Benutzer ausgeliefert.

```
GET /mock/users/{username}/classes     -> Meine Klassen
GET /mock/users/{username}/courses     -> Meine Kurse
GET /mock/users/{username}/absences    -> Absenzen (Datum, Lektion, Status: offen/entschuldigt/unentschuldigt)
GET /mock/users/{username}/grades      -> Noten (Fach, Semester, Note, Gewichtung)
```

Das Hauptbackend spricht diese Endpoints über je ein Client-Interface pro Modul an
(`ClassesClient`, `CoursesClient`, `AbsencesClient`, `GradesClient`). Auch `school-mock` hat eigene
Tests (Controller-Tests mit MockMvc), damit der Mock-Vertrag stabil bleibt.

Unsere API (Backend -> Frontend):

```
GET /api/me                 -> angemeldeter Benutzer
GET /api/classes            -> Meine Klassen
GET /api/courses            -> Meine Kurse
GET /api/absences           -> Absenzen des Benutzers
GET /api/grades             -> Noten des Benutzers, inkl. berechneter Durchschnitte
GET /api/dashboard          -> News, Termine, Prävention, Links
GET /api/modules            -> [{ name, enabled }]
```

Authentifizierung ist **kein** Scope dieses Projekts: In der Schule läuft das über SSO, das wir nicht
nachbauen. Der Benutzer wird über einen Header `X-User` (Default `nicola.wehrli`, Klasse `AP24B`)
bestimmt. Das ist bewusst einfach gehalten und im README dokumentiert.

---

## 6. Arbeitsweise: Test Driven Development

TDD ist **Pflicht** in diesem Projekt. Der Zyklus ist Red -> Green -> Refactor und wird **pro
Feature in dieser Reihenfolge** durchlaufen:

1. **Red:** Zuerst werden **alle Tests** für das Feature geschrieben (Unit, Integration, Frontend).
   Die Tests kompilieren, aber schlagen fehl. Dieser Zustand wird committed
   (`test(grades): add failing tests for grade average calculation`).
2. **Green:** Dann wird die minimale Implementierung geschrieben, bis alle Tests grün sind.
   Auch das wird committed (`feat(grades): implement grade average calculation`).
3. **Refactor:** Code und Tests werden aufgeräumt, ohne das Verhalten zu ändern. Separater Commit
   (`refactor(grades): extract GradeAverageCalculator`).

Regeln:

- Keine Produktionszeile ohne vorher fehlschlagenden Test.
- Tests beschreiben Verhalten, nicht Implementierung: `shouldReturn503WhenGradesModuleIsDisabled`,
  nicht `testHandle`.
- Commits sind klein und folgen Conventional Commits (`feat`, `fix`, `test`, `refactor`, `docs`, `ci`, `chore`)
  mit dem Modul als Scope.
- Die Commit-Historie muss den TDD-Prozess zeigen (Red-Commit vor Green-Commit). Das ist Teil der Bewertung.

### 6.1 Testpyramide

| Ebene                  | Backend                                                               | Frontend                                             |
|------------------------|-----------------------------------------------------------------------|------------------------------------------------------|
| Unit                   | JUnit 5 + Mockito + AssertJ: Handler, Domain-Logik, Mapper. Alle Abhängigkeiten gemockt. | Vitest: Hooks, Utils, Mapper                          |
| Component              | –                                                                     | Vitest + React Testing Library: Komponenten isoliert, API via MSW gemockt |
| Integration            | `@SpringBootTest` + MockMvc: Controller -> Gateway -> Handler. Externe HTTP-Clients via WireMock. | Seiten-Tests mit Router + MSW                         |
| Architektur            | ArchUnit: Modulgrenzen, Schichtenregeln                               | ESLint-Regeln (`no-restricted-imports` zwischen Modulen) |
| End-to-End (optional)  | Playwright gegen docker-compose, nur Smoke-Tests                      |                                                      |

Coverage-Ziel: **mindestens 80 % Line Coverage** im Backend (JaCoCo) und Frontend (Vitest/v8).
Die Pipeline schlägt unterhalb des Ziels fehl.

### 6.2 Was gemockt wird

- `stundenplan.geekz.ch`: in Unit-Tests via Mockito (`TimetableClient`), in Integrationstests via WireMock.
  **Echte Netzwerkaufrufe sind in Tests verboten.**
- `school-mock`: im Hauptbackend via Mockito/WireMock, genau wie oben.
- Gateway/Handler: in Controller-Tests wird das Gateway gemockt.
- Im Frontend werden alle `/api/*`-Aufrufe via MSW beantwortet.

### 6.3 Reports

- Backend: Surefire-XML + JaCoCo-HTML unter `backend/target/site/jacoco`.
- school-mock: dito.
- Frontend: Vitest JUnit-Reporter + Coverage-HTML unter `frontend/coverage`.
- Die Pipeline lädt alle Reports als Artefakte hoch und publiziert sie auf **GitHub Pages** (Deploy-Stage).

---

## 7. Branching, Pull Requests, Reviews

- `main` ist geschützt. Es wird **nie** direkt auf `main` committed.
- Jedes Feature bekommt einen eigenen Branch: `feature/<modul>-<kurzbeschreibung>`,
  z.B. `feature/timetable-fetch-lessons`, `feature/core-command-gateway`.
  Weitere Präfixe: `fix/`, `docs/`, `ci/`, `chore/`.
- Pro Branch ein Pull Request nach `main`. Der PR wird vom Team auf GitHub reviewt. Claude liefert
  den PR, **das Team entscheidet und gibt Feedback**. Feedback wird im selben Branch eingearbeitet.
- PR-Beschreibung enthält: Was wurde gemacht, welche Tests wurden zuerst geschrieben (Red),
  wie wurde es umgesetzt (Green), was wurde refactored, wie kann man es lokal prüfen.
- Ein PR ist klein genug, um in 15 Minuten reviewt zu werden. Lieber mehrere PRs als ein grosser.
- Merge erst, wenn CI grün ist und mindestens ein Review-Approve vorliegt.
- Claude merged **nicht selbst**. Das Team merged nach dem Review.
- Commit-Messages und PR-Beschreibungen tragen die Claude-Attribution (siehe System-Vorgabe).

### 7.1 Geplante Feature-Branches (Reihenfolge)

1. `chore/project-scaffold` – Monorepo-Grundgerüst, Build-Tools, docker-compose, leere Module
2. `ci/pipeline` – GitHub Actions: Build, Test, Coverage, Report-Deploy auf Pages
3. `feature/core-command-gateway` – Gateway, Registry, Modul-Enable/Disable, ArchUnit-Regeln
4. `feature/school-mock-backend` – zweites Backend mit allen vier Mock-Schnittstellen + Tests
5. `feature/timetable-backend` – Stundenplan- und Ferien-Client, Handler, Controller
6. `feature/user-and-dashboard-backend` – Benutzer, News, Termine, Links
7. `feature/classes-courses-backend` – Meine Klassen, Meine Kurse
8. `feature/absences-backend`
9. `feature/grades-backend` – inkl. Durchschnittsberechnung (gute TDD-Kandidatin)
10. `feature/frontend-shell` – Layout, Header, Navigation, Router, Modul-Sichtbarkeit
11. `feature/frontend-dashboard`
12. `feature/frontend-timetable`
13. `feature/frontend-classes-courses`
14. `feature/frontend-absences`
15. `feature/frontend-grades`
16. `docs/architecture-testconcept-reflection`

Reihenfolge kann angepasst werden; unabhängige Branches dürfen **parallel durch Subagents**
bearbeitet werden (siehe Abschnitt 9).

---

## 8. CI/CD Pipeline (GitHub Actions)

Datei: `.github/workflows/ci.yml`. Läuft bei jedem Push auf einen Branch und bei jedem PR. Der
Deploy-Job läuft nur auf `main`.

| Stage   | Inhalt                                                                                                     |
|---------|------------------------------------------------------------------------------------------------------------|
| Build   | `mvn -B -DskipTests package` für `backend` und `school-mock`; `npm ci && npm run build` für `frontend`      |
| Test    | `mvn -B verify` (Unit + Integration + JaCoCo + Coverage-Gate); `npm test -- --coverage`; ArchUnit; Lint     |
| Report  | Surefire/JUnit-Resultate als Check annotieren (z.B. `dorny/test-reporter`); Coverage-HTML als Artefakt hochladen |
| Deploy  | Nur auf `main`: Reports (JaCoCo + Vitest Coverage + Testreport-Übersicht) nach GitHub Pages publizieren     |

Jobs für Backend, school-mock und Frontend laufen parallel. Ein roter Test blockiert den PR.

---

## 9. Arbeiten mit Subagents

Claude darf und soll Subagents einsetzen, um unabhängige Arbeit parallel zu erledigen. Regeln:

- Ein Subagent bearbeitet **genau einen Feature-Branch** in einem **eigenen Git-Worktree**
  (`isolation: "worktree"`), damit Branches sich nicht in die Quere kommen.
- Jeder Subagent erhält diese CLAUDE.md als Kontext und hält sich an TDD (Red-Commit zuerst).
- Subagents committen auf ihrem Branch und pushen ihn, erstellen aber **keinen Merge**.
- Der orchestrierende Agent prüft das Ergebnis (Tests laufen lokal grün, Commit-Historie zeigt
  Red/Green/Refactor) und eröffnet dann den PR.
- Abhängige Branches (z.B. Frontend-Module brauchen den Shell-Branch) werden erst gestartet, wenn
  die Basis gemerged ist oder werden auf den Basis-Branch gesetzt.

---

## 10. Entscheidungen und offene Fragen

**Grundsatz:** Bei Fragen oder Architekturentscheidungen entscheidet **der Mensch** (Projektteam),
nicht Claude. Claude macht einen Vorschlag mit Begründung und wartet auf die Entscheidung, bevor
etwas Grundlegendes gebaut wird. Kleine, umkehrbare Entscheidungen (Namen, Dateistruktur innerhalb
eines Moduls, Testbenennung) trifft Claude selbst.

### 10.1 Bereits entschieden (durch den Auftrag)

- Frontend React + TypeScript, Backend Spring Boot + Java
- Modularer Aufbau mit Command Gateways, Module abschaltbar/ersetzbar
- Stundenplan echt über das Backend von `stundenplan.geekz.ch`
- Zweites Backend (`school-mock`) für Klassen, Kurse, Absenzen, Noten
- TDD: zuerst alle Tests, dann Implementierung
- Features auf Branches, Review per GitHub PR durch das Team
- CI/CD-Pipeline
- Subagents für die Umsetzung

### 10.2 Vorschläge von Claude, vom Team am 2026-11-01 bestätigt

| Thema                 | Vorschlag                                        | Alternative                        |
|-----------------------|--------------------------------------------------|------------------------------------|
| Build-Tool Backend    | Maven (verbreitet, einfache Pipeline)            | Gradle                             |
| Java-Version          | 21 (LTS, Records, Pattern Matching)              | 17                                 |
| Frontend-Tooling      | Vite + Vitest + RTL + MSW                        | Create React App + Jest            |
| Styling               | Plain CSS Modules, TBZ-Farben (Blau `#7fa9d2`, Rot, Orange, Violett wie im alten Intranet) | Tailwind |
| Persistenz            | Keine DB, alle Daten in-memory/JSON (reicht für Scope) | H2/Postgres                   |
| Auth                  | `X-User`-Header, kein Login (SSO in der Schule, nicht Scope) | Spring Security mit Fake-Login |
| Report-Hosting        | GitHub Pages aus dem `main`-Deploy               | Nur Artefakte                      |
| Coverage-Schwelle     | 80 % Lines                                       | 70 % / 90 %                        |
| Monorepo              | Ja, ein Repo mit drei Projekten                  | Drei Repos                         |

### 10.3 Entscheidungen des Teams (2026-11-01)

1. **Standardklasse im Stundenplan:** `AP24B` (Klasse des angemeldeten Benutzers).
2. **Team:** 2 Personen. Claude erstellt die PRs, das Team reviewt und merged sie auf GitHub.
3. **Termine der Schule:** Ferien/Feiertage von `stundenplan.geekz.ch/holidays` **plus** eigene
   Mock-Termine (z.B. Elternabend, Prüfungswoche) aus dem Dashboard-Modul.
4. **Absenzen:** nur Anzeige, keine Erfassung. Es gibt deshalb nur Queries, keine Commands.
5. **Login:** keins. SSO wäre der echte Mechanismus und ist nicht Scope.
6. **Design:** Stil des «Marks»-Figma-Boards (siehe Abschnitt 4.6).
7. **Git:** Claude committet selbst, regelmässig und klein, mit Co-Author-Zeile. Claude pusht
   Branches und eröffnet PRs, merged aber nicht.
8. **Qualität:** Production-ready nach Best Practices, keine Abkürzungen bei Tests, Fehlerbehandlung
   und Struktur.
9. **Nur Berufsschule TBZ:** Keine BMS- und keine ÜK-Inhalte, kein Konzept «Schulteil». Noten und
   Kurse werden nach Semester gruppiert. Mock-Daten von `nicola.wehrli` bilden den echten Stundenplan
   von AP 24b ab (Englisch, Allgemeinbildung Gesellschaft, Modul 450, Modul 306).

---

## 11. Konventionen

### Allgemein

- Sprache im Code: **Englisch** (Klassen, Methoden, Variablen, Commits, PRs).
  Sprache in Doku und UI: **Deutsch** (README, docs/, Oberflächentexte).
- Keine toten Features, keine `TODO`-Kommentare ohne Issue, keine auskommentierten Codeblöcke.
- Keine Secrets im Repo. Externe URLs kommen aus `application.yml` bzw. `.env`.

### Backend

- Java 21, Records für DTOs, `final` wo sinnvoll, keine Lombok-Abhängigkeit (Records reichen).
- Konstruktor-Injection, keine Field-Injection.
- Ein Controller pro Modul, dünn, delegiert ans Gateway.
- Fehler als eigene Exceptions, zentral im `GlobalExceptionHandler` auf HTTP-Status gemappt.
- Tests: `*Test` für Unit, `*IT` für Integration. Given/When/Then-Struktur, AssertJ.

### Frontend

- Funktionale Komponenten, Hooks, kein Class-Component.
- Strikte TypeScript-Konfiguration (`strict: true`), keine `any`.
- Daten-Fetching über einen zentralen API-Client in `shared/api`, pro Modul ein Hook (`useGrades`).
- Tests neben der Komponente: `GradesPage.test.tsx`. Queries über Rollen/Texte, nicht über CSS-Klassen.

### Dokumentation

- `README.md` enthält: Projektbeschreibung, Setup (docker-compose + lokal), Teststrategie (kurz),
  Link zu Reports, KI-Deklaration («Code wurde mit Claude Code als Agent erstellt; Architektur,
  Reviews und Entscheidungen durch das Team»).
- `docs/architektur.md` mit Mermaid-Diagrammen (Module, Gateway, Datenfluss Stundenplan).
- `docs/testkonzept.md` nach TBZ-Vorlage (Testarten, Testumgebung, Mocking, Reports, Abnahmekriterien).
- `docs/reflexion.md` wird vom Team geschrieben, nicht von Claude.

---

## 12. Lokales Starten

```bash
# alles zusammen
docker compose up --build

# einzeln
cd school-mock && ./mvnw spring-boot:run          # Port 8081
cd backend     && ./mvnw spring-boot:run          # Port 8080
cd frontend    && npm ci && npm run dev           # Port 5173

# Tests
cd backend     && ./mvnw verify                   # Unit + IT + JaCoCo
cd school-mock && ./mvnw verify
cd frontend    && npm test -- --coverage
```

---

## 13. Definition of Done (pro Feature)

- [ ] Tests wurden **vor** der Implementierung geschrieben und als Red-Commit festgehalten
- [ ] Alle Tests grün, Coverage über der Schwelle
- [ ] Externe Schnittstellen in Tests gemockt (kein Netzwerk)
- [ ] Modulgrenzen eingehalten (ArchUnit grün)
- [ ] Modul kann per Konfiguration deaktiviert werden und das Frontend reagiert korrekt
- [ ] Feature-Branch gepusht, PR eröffnet mit Beschreibung nach Abschnitt 7
- [ ] CI grün
- [ ] Doku (README/docs) aktualisiert, falls betroffen
