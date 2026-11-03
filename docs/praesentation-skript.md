# Präsentationsskript – BetterIntranet (10 Minuten)

Drehbuch für die Aufnahme der Präsentation. Vorgaben aus dem Auftrag (`grundaufbau.md`):
10 Minuten, Endprodukt kurz zeigen, auf Testing eingehen, Reports vorzeigen, Reflexion über
Code Reviews und TDD, kurzes Fazit.

**Rollen:** **A** und **B** sind die beiden Teammitglieder. Die Aufteilung ist ein Vorschlag und
kann getauscht werden. Texte in *kursiv* sind Regieanweisungen, normale Texte werden gesprochen.
Stellen mit `[eigene Worte]` sind bewusst offen: Die Reflexion ist eure eigene Meinung und wird
nicht von Claude vorformuliert.

| Zeit        | Teil                            | Wer   | Bildschirm                                  |
|-------------|---------------------------------|-------|---------------------------------------------|
| 0:00 – 0:45 | Einstieg                        | A     | Altes Intranet (Screenshot), dann neues     |
| 0:45 – 3:00 | Live-Demo                       | A     | `http://localhost:3000`                     |
| 3:00 – 4:15 | Architektur                     | B     | `docs/architektur.md` (Mermaid-Diagramm)    |
| 4:15 – 6:15 | Testing und TDD                 | B, A  | IDE: Tests, Git-Historie                    |
| 6:15 – 7:30 | Pipeline und Reports            | A     | GitHub Actions, JaCoCo, Vitest-Coverage     |
| 7:30 – 9:15 | Reflexion TDD und Code Reviews  | A, B  | PR #9 und #11 auf GitHub                    |
| 9:15 – 10:00| Fazit                           | B     | Startseite BetterIntranet                   |

---

## Vorbereitung (vor der Aufnahme)

- [ ] `docker compose up --build` laufen lassen, dann `http://localhost:3000` öffnen (Light Mode)
- [ ] Reports lokal erzeugen, falls GitHub Pages noch nicht aktiv ist:
  - `cd backend && ./mvnw verify` → `backend/target/site/jacoco/index.html`
  - `cd frontend && npm test -- --coverage` → `frontend/coverage/index.html`
  - alternativ: Artefakte des letzten CI-Runs auf `main` herunterladen
- [ ] Browser-Tabs in dieser Reihenfolge vorbereiten:
  1. Screenshot altes Intranet
  2. BetterIntranet `localhost:3000`
  3. `docs/architektur.md` auf GitHub (Diagramm wird gerendert)
  4. Git-Historie mit Red- und Green-Commit (z.B. `git log --oneline` im Terminal)
  5. GitHub Actions: letzter Run auf `main`
  6. JaCoCo-Report Backend
  7. Vitest-Coverage Frontend
  8. PR #9 und PR #11 (Tab «Files changed» bzw. Review-Kommentare)
- [ ] IDE mit `GradeAverageCalculatorTest` und `GeekzTimetableClientIT` offen
- [ ] Benachrichtigungen aus, Schrift im Terminal und in der IDE vergrössern
- [ ] Einmal mit Stoppuhr durchsprechen: Die Demo ist der Teil, der am ehesten zu lang wird

---

## 1. Einstieg (0:00 – 0:45) · A

*Bildschirm: Screenshot des alten TBZ-Intranets.*

> «Das ist das Intranet, das wir an der TBZ jeden Tag benutzen: Stundenplan, Absenzen, Noten,
> Klassen und Kurse. Es funktioniert, ist aber in die Jahre gekommen. Unser Projekt im Modul 450
> heisst **BetterIntranet**: Wir haben es neu gebaut, und zwar so, dass es von Anfang an
> testbar ist.»

*Wechsel auf `localhost:3000`.*

> «Der Stundenplan ist echt, er kommt live von stundenplan.geekz.ch. Klassen, Kurse, Absenzen
> und Noten haben wir gemockt, weil wir keinen Zugang zu den Schulsystemen haben. Wichtig zur
> Transparenz: Den Code hat Claude Code als KI-Agent geschrieben. Architektur, Entscheidungen
> und die Code Reviews kamen von uns. Das steht auch im README.»

---

## 2. Live-Demo (0:45 – 3:00) · A

*Ruhig klicken, nicht hetzen. Jede Station etwa 20 Sekunden.*

1. **Startseite / Dashboard**
   > «Auf der Startseite sind die gleichen Bereiche wie im alten Intranet: Newsticker, Termine,
   > Prävention, Meine Links und Quick Links. Die Termine sind die echten Ferien von
   > stundenplan.geekz.ch plus eigene Termine wie die Prüfungswoche.»

2. **Stundenplan**
   > «Das ist unsere Klasse AP 24b. Am Dienstag Englisch, ABU Gesellschaft und Modul 450 – genau
   > wie im echten Stundenplan. Ich kann die Klasse wechseln …»
   *Klasse wechseln, z.B. ICT 26a, Woche mit Teamteaching zeigen.*
   > «… und hier sieht man zwei Lehrpersonen in einer Lektion. Das kam aus einem Review, dazu
   > später mehr.» *Woche vor- und zurückschalten, Ferienwoche zeigen.*
   > «Ferien werden als ganze Spalte markiert.»

3. **Klassen & Kurse**
   > «Meine Klassen und Meine Kurse, filterbar nach Semester. Es gibt nur Berufsschul-Inhalte,
   > also Module und ABU – BMS und überbetriebliche Kurse gehören nicht ins TBZ-Intranet.»

4. **Absenzen**
   > «Die Absenzen werden nur angezeigt, oben mit einer Zusammenfassung, darunter nach Status
   > filterbar: offen, entschuldigt, unentschuldigt.»

5. **Noten**
   > «Bei den Noten sieht man oben das Diagramm mit den Durchschnitten, darunter pro Semester
   > die Fächer mit allen Prüfungen. Der gewichtete Durchschnitt und die Rundung auf halbe Noten
   > werden im Backend berechnet – das war unser bestes TDD-Beispiel.»

6. **Dark Mode** *(Toggle oben rechts, 5 Sekunden)*
   > «Und weil es heute dazugehört: ein Dark Mode.»

---

## 3. Architektur (3:00 – 4:15) · B

*Bildschirm: Diagramm in `docs/architektur.md`.*

> «Technisch haben wir drei Teile: ein React-Frontend mit TypeScript, ein Spring-Boot-Backend
> und ein zweites Spring-Boot-Backend, das `school-mock`, das die Schulsysteme simuliert.»

> «Das Hauptbackend ist ein **modularer Monolith**. Jedes Modul – Stundenplan, Noten,
> Absenzen und so weiter – kennt die anderen nicht direkt. Sie kommunizieren nur über
> **Command- und Query-Gateways**. Das Dashboard zum Beispiel fragt die Ferien beim Gateway
> an, nicht direkt beim Stundenplan-Modul.»

> «Das hat zwei Vorteile. Erstens lässt sich jedes Modul per Konfiguration abschalten: Dann
> antwortet das Backend mit 503, und das Frontend blendet den Menüpunkt aus. Zweitens kann
> man ein Modul austauschen, zum Beispiel die gemockten Noten später durch eine echte
> Anbindung ersetzen, ohne den Rest anzufassen.»

> «Damit diese Grenzen nicht nur auf dem Papier stehen, prüft ein **ArchUnit-Test** bei jedem
> Build, dass kein Modul in ein anderes hineingreift.»

---

## 4. Testing und TDD (4:15 – 6:15) · B, dann A

### 4.1 Testpyramide und Mocking · B

*Bildschirm: IDE, Ordner `src/test`.*

> «Wir haben insgesamt **725 Tests**: im Backend 228 Unit- und 95 Integrationstests, im
> School-Mock 43 Unit- und 8 Integrationstests und im Frontend 351 Tests.»

> «Unit-Tests laufen mit JUnit 5, AssertJ und **Mockito** als Mocking-Framework: Alle
> Abhängigkeiten werden gemockt. Integrationstests starten Spring Boot mit MockMvc. Die
> externe Webseite stundenplan.geekz.ch simulieren wir mit **WireMock**.»

*`GeekzTimetableClientIT` zeigen.*

> «Hier zum Beispiel: WireMock liefert echte, aufgezeichnete Antworten der Webseite zurück. Kein
> Test ruft das Internet auf. Das haben wir geprüft, indem wir die Adresse auf einen toten Port
> gesetzt haben – alle Tests waren trotzdem grün.»

> «Im Frontend benutzen wir Vitest und React Testing Library, und alle API-Aufrufe werden mit
> **MSW** beantwortet. Die Uhr wird in Tests eingefroren, damit kein Test vom heutigen Datum
> abhängt.»

### 4.2 TDD am Beispiel Noten · A

*Bildschirm: Terminal mit `git log --oneline` auf die Commits `c25cc16` (Red), `87eb065` (Green) und `4374a4b` (Refactor).*

> «TDD sieht man bei uns in der Git-Historie. Jedes Feature hat zuerst einen **Red-Commit**,
> der nur Tests enthält, die fehlschlagen, und danach einen **Green-Commit** mit der
> Implementierung.»

*`GradeAverageCalculatorTest` zeigen.*

> «Beispiel Notendurchschnitt: Zuerst haben wir die Tests geschrieben – gewichteter
> Durchschnitt, Rundung auf halbe Noten, und was passiert bei ungültigen Werten wie einer 7
> oder ‹keine Zahl›. Erst dann kam der Code. Die Tests haben die Regeln festgelegt, bevor es
> eine Zeile Implementierung gab.»

---

## 5. Pipeline und Reports (6:15 – 7:30) · A

*Bildschirm: GitHub Actions, letzter Run auf `main`.*

> «Bei jedem Push und jedem Pull Request läuft unsere Pipeline in GitHub Actions. Backend,
> School-Mock und Frontend werden parallel gebaut und getestet, inklusive Lint und
> Typecheck. Die Testresultate erscheinen direkt als Check im Pull Request. Ein roter Test
> blockiert den Merge.»

*JaCoCo-Report öffnen.*

> «Die Coverage ist als Report einsehbar. Im Backend haben wir **99,3 % Line Coverage**, im
> School-Mock **100 %**. Die Pipeline schlägt fehl, wenn wir unter 80 % fallen.»

*Vitest-Coverage öffnen.*

> «Im Frontend sind es **99 % der Zeilen** und **93 % der Branches**.»

> «Auf `main` werden die Reports zusätzlich automatisch auf GitHub Pages veröffentlicht.»
*(Nur sagen, wenn Pages zum Zeitpunkt der Aufnahme aktiviert ist und funktioniert.)*

---

## 6. Reflexion: TDD und Code Reviews (7:30 – 9:15) · A und B

*Dieser Teil ist in euren eigenen Worten. Die Stichpunkte sind Fakten aus dem Projekt, an
denen ihr euch festhalten könnt. Siehe auch die Leitfragen in `docs/reflexion.md`.*

### 6.1 TDD · A (ca. 45 Sekunden)

Fakten:
- Jeder Feature-PR (#2 und #4 bis #15) hat einen Red-Commit vor dem Green-Commit.
- Rundung der Zeugnisnote: Die Grenzfälle wurden durch die Tests zuerst durchdacht.
- Der Stundenplan-Test der aktuellen Woche war nur zufällig grün – er hätte nächste Woche
  versagt. Gelöst mit eingefrorener Uhr.

`[eigene Worte]`: Wie hat sich «zuerst Tests» angefühlt? Wo hat es geholfen, wo war es mühsam
(z.B. Layout im Frontend)? Würdet ihr es im Betrieb wieder so machen?

### 6.2 Code Reviews · B (ca. 60 Sekunden)

*Bildschirm: PR #9 und PR #11 mit Kommentaren.*

Fakten (gute Beispiele zum Zeigen):
- **PR #9, `errorCode` nie geprüft:** Jeder Fehler der Webseite wurde still zu «keine
  Lektionen». Nach dem Review: Nur `NOT_FOUND` heisst «keine Daten», alles andere ist ein Fehler.
- **PR #9, Teamteaching:** Die zweite Lehrperson ging verloren. Wir haben es an der echten
  Webseite nachgeprüft – es kommt wirklich vor – und Listen eingeführt.
- **PR #11, «tickende Zeitbombe»:** Ein Test hing vom echten Datum ab und wäre im nächsten
  Semester rot geworden.
- **PR #11, axios:** Wunsch nach axios und Konstanten für die API-Pfade, umgesetzt in allen
  Frontend-PRs.
- **PR #5, Benutzernamen mit Punkt:** Ein Vorschlag wurde begründet *nicht* umgesetzt, weil
  Benutzernamen wie `nicola.wehrli` einen Punkt brauchen. Auch das gehört zu einem Review.

`[eigene Worte]`: Wie war es, KI-generierten Code zu reviewen? Habt ihr alles verstanden, wo
musstet ihr nachfragen? Was hat das Review gebracht, das die Tests nicht gefunden haben?

---

## 7. Fazit (9:15 – 10:00) · B

*Bildschirm: Startseite BetterIntranet.*

> «Zum Schluss: Wir haben ein funktionierendes Intranet mit echtem Stundenplan, modularer
> Architektur und 725 automatisierten Tests, die bei jedem Pull Request laufen.»

`[eigene Worte]`, ein bis zwei Sätze: Was nehmt ihr mit? Was würdet ihr nächstes Mal anders
machen?

> «Danke fürs Zuschauen.»

---

## Notfallplan während der Aufnahme

| Problem                               | Lösung                                                              |
|---------------------------------------|---------------------------------------------------------------------|
| stundenplan.geekz.ch nicht erreichbar | Das ist sogar ein gutes Beispiel: Die Fehlermeldung im Frontend zeigen («Der Stundenplan ist zurzeit nicht erreichbar.», HTTP 502) |
| Docker startet nicht                  | Backend, School-Mock und Frontend einzeln starten (README, «Lokales Starten») |
| Zeit läuft davon                      | Dark Mode und Absenzen in der Demo weglassen                        |
