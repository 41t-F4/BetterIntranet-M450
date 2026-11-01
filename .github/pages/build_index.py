#!/usr/bin/env python3
"""Builds the GitHub Pages index for the test and coverage reports.

Reads JUnit XML files from the downloaded CI artifacts, summarises them per
project and writes site/index.html linking to the JaCoCo and Vitest coverage
reports. Only the Python standard library is used.
"""
import glob
import html
import sys
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from pathlib import Path

PROJECTS = [
    ("Backend", "backend-reports", ["surefire-reports", "failsafe-reports"], "backend/jacoco/index.html"),
    ("School mock", "school-mock-reports", ["surefire-reports", "failsafe-reports"], "school-mock/jacoco/index.html"),
    ("Frontend", "frontend-reports", ["reports"], "frontend/coverage/index.html"),
]


def summarise(artifact_dir: Path, subdirs: list[str]) -> dict:
    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0, "time": 0.0}
    for sub in subdirs:
        for xml_file in glob.glob(str(artifact_dir / sub / "**" / "*.xml"), recursive=True):
            try:
                root = ET.parse(xml_file).getroot()
            except ET.ParseError:
                continue
            suites = [root] if root.tag == "testsuite" else root.iter("testsuite")
            for suite in suites:
                totals["tests"] += int(suite.get("tests", 0))
                totals["failures"] += int(suite.get("failures", 0))
                totals["errors"] += int(suite.get("errors", 0))
                totals["skipped"] += int(suite.get("skipped", 0))
                totals["time"] += float(suite.get("time", 0) or 0)
    return totals


def main() -> None:
    artifacts, site, sha, run_id = Path(sys.argv[1]), Path(sys.argv[2]), sys.argv[3], sys.argv[4]
    rows = []
    for name, artifact, subdirs, coverage_link in PROJECTS:
        t = summarise(artifacts / artifact, subdirs)
        ok = t["failures"] == 0 and t["errors"] == 0
        status = "✅ grün" if ok else "❌ rot"
        has_cov = (site / coverage_link).exists()
        cov = f'<a href="{coverage_link}">Coverage-Report</a>' if has_cov else "–"
        rows.append(
            f"<tr><td>{html.escape(name)}</td><td>{status}</td><td>{t['tests']}</td>"
            f"<td>{t['failures'] + t['errors']}</td><td>{t['skipped']}</td><td>{t['time']:.1f}s</td><td>{cov}</td></tr>"
        )
    generated = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
    page = f"""<!doctype html>
<html lang="de">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>BetterIntranet Testreports</title>
<style>
  body {{ font-family: Inter, system-ui, sans-serif; background: #f2f2f2; color: #1f1f1f; margin: 0; }}
  header {{ background: #1f1f1f; color: #fff; padding: 16px 24px; }}
  main {{ max-width: 960px; margin: 24px auto; padding: 0 16px; }}
  .card {{ background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,.08); padding: 20px; }}
  table {{ width: 100%; border-collapse: collapse; }}
  th, td {{ text-align: left; padding: 10px 8px; border-bottom: 1px solid #e3e3e3; }}
  th {{ background: #f7f7f7; font-weight: 600; }}
  td:nth-child(n+3):nth-child(-n+6) {{ text-align: right; }}
  .meta {{ color: #6b6b6b; font-size: .875rem; margin-top: 16px; }}
  a {{ color: #2f6fb6; text-decoration: none; }}
</style>
</head>
<body>
<header><strong>BetterIntranet</strong> · Testreports (main)</header>
<main>
  <div class="card">
    <h2>Testresultate</h2>
    <table>
      <thead><tr><th>Projekt</th><th>Status</th><th>Tests</th><th>Fehler</th><th>Übersprungen</th><th>Dauer</th><th>Coverage</th></tr></thead>
      <tbody>{''.join(rows)}</tbody>
    </table>
    <p class="meta">Commit <code>{html.escape(sha[:10])}</code> · Run {html.escape(run_id)} · erzeugt {generated}</p>
  </div>
</main>
</body>
</html>
"""
    (site / "index.html").write_text(page, encoding="utf-8")


if __name__ == "__main__":
    main()
