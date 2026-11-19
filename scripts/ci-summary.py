"""Generate a small CI artifact without request bodies, tokens or environment values."""
import json
import os
from pathlib import Path
import xml.etree.ElementTree as ET

summary = {"backend": {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}, "playwright": {}}
reports = list(Path("backend/target/surefire-reports").glob("TEST-*.xml"))
summary["backend"]["report_files"] = len(reports)
for report in reports:
    suite = ET.parse(report).getroot()
    for key in ("tests", "failures", "errors", "skipped"):
        summary["backend"][key] += int(suite.attrib.get(key, 0))
for name in ("normal", "restarts"):
    path = Path(f"e2e-{name}.json")
    if not path.exists():
        summary["playwright"][name] = {"executed": False}
        continue
    text = path.read_text(encoding="utf-8")
    # npm's command banner precedes the JSON reporter; never export that text.
    start = text.find("{\n")
    try:
        data = json.loads(text[start:])
        stats = data["stats"]
        summary["playwright"][name] = {key: stats.get(key, 0) for key in ("expected", "unexpected", "flaky", "skipped", "duration")}
    except (ValueError, KeyError):
        summary["playwright"][name] = {"executed": False, "report_unavailable": True}
Path("artifacts").mkdir(exist_ok=True)
Path("artifacts/ci-summary.json").write_text(json.dumps(summary, indent=2), encoding="utf-8")
print(json.dumps(summary))
if os.environ.get("GITHUB_STEP_SUMMARY"):
    with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as output:
        output.write("## Verificações Matriz\n\n```json\n" + json.dumps(summary, indent=2) + "\n```\n")
