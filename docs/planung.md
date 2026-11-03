# Planung

Kurze Planung ohne Gantt. Das Projekt ist bewusst klein gehalten, damit jedes Feature sauber nach
TDD mit Unit-, Integrations- und gemockten Tests entsteht (siehe Bewertungsrubrik in [CLAUDE.md](../CLAUDE.md)).

## Team und Rollen

| Wer            | Rolle                                                                 |
|----------------|-----------------------------------------------------------------------|
| Team (2 Personen) | Architekturentscheidungen, Code Reviews auf GitHub, Merge, Reflexion, Präsentation |
| Claude Code    | Schreibt Tests und Implementierung auf Feature-Branches, eröffnet Pull Requests |

## Scope

**Im Scope**

- Dashboard (News, Termine, Prävention, Meine Links, Quick Links)
- Stundenplan und Ferien, echt von `stundenplan.geekz.ch`
- Meine Klassen, Meine Kurse, Absenzen (nur Anzeige), Noten mit Durchschnitten
- Modulare Architektur mit abschaltbaren Modulen
- Zweites Backend, das die Schulsysteme mockt
- CI/CD mit Testreports und Coverage auf GitHub Pages

**Nicht im Scope**

- Login (SSO der Schule), Benutzerverwaltung
- Erfassen von Absenzen oder Noten
- Persistenz in einer Datenbank
- Mail, Favoriten, Newsticker-Pflege

## Meilensteine

| Nr. | Meilenstein                                   | Inhalt                                                                              |
|-----|-----------------------------------------------|-------------------------------------------------------------------------------------|
| M1  | Grundgerüst und Pipeline                      | Monorepo, drei Projekte, Coverage-Gates, GitHub Actions, Pages-Deploy               |
| M2  | Backend-Kern                                  | Command/Query Gateway, Modul-Schalter, Fehler-Mapping, ArchUnit-Regeln              |
| M3  | Mock-Backend                                  | Klassen, Kurse, Absenzen, Noten als Fixtures mit eigenem Testset                    |
| M4  | Backend-Module                                | Stundenplan, Noten, Absenzen, Klassen/Kurse, Benutzer, Dashboard (parallel)         |
| M5  | Frontend-Shell                                | Layout, Navigation, Modul-Registry, API-Client                                       |
| M6  | Frontend-Module                               | Dashboard, Stundenplan, Klassen/Kurse, Absenzen, Noten                               |
| M7  | Dokumentation und Reflexion                   | Architektur, Testkonzept, Reflexion über TDD und Code Reviews, Präsentation         |

## Arbeitsweise

- Jedes Feature auf einem eigenen Branch, TDD mit sichtbarem Red/Green/Refactor in der Commit-Historie.
- Pull Request pro Feature. Das Team reviewt, kommentiert, challenged und merged.
- Stacked PRs, wo ein Feature auf einem anderen aufbaut (Reihenfolge in der PR-Beschreibung).
- Unabhängige Module werden parallel von mehreren Agenten in eigenen Git-Worktrees gebaut.

## Risiken

| Risiko                                               | Massnahme                                                                 |
|------------------------------------------------------|---------------------------------------------------------------------------|
| `stundenplan.geekz.ch` nicht erreichbar oder ändert Format | Backend cached 5 Minuten, liefert 502 mit klarer Meldung, Tests laufen gegen WireMock mit echten Fixtures |
| Zu viele Features, zu wenig Testtiefe                | Scope bewusst klein, Coverage-Gate 80 % in der Pipeline                   |
| Merge-Konflikte bei parallelen Branches              | Module in getrennten Paketen, Verträge (`api`-Pakete) vorab definiert     |
| Verständnis des KI-generierten Codes                 | Reviews durch das Team, kleine Commits, Javadoc und diese Doku            |
