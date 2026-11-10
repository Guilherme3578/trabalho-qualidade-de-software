import csv
import sys
import xml.etree.ElementTree as ET
from datetime import datetime
from pathlib import Path

raiz = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(".")
saida = Path(sys.argv[2]) if len(sys.argv) > 2 else raiz / "docs" / "10-relatorio-testes-backend.md"

NIVEIS = [
    ("api", "API caixa-preta"),
    ("integracao", "Integração (MySQL real + WireMock)"),
    ("concorrencia", "Concorrência"),
    ("bdd", "BDD (Cucumber)"),
    ("e2e", "E2E (Selenium)"),
]
META_COBERTURA = 80.0


def nivel(nome_classe):
    partes = nome_classe.split(".")
    for chave, rotulo in NIVEIS:
        if chave in partes:
            return rotulo
    return "Unitário"


def ler_suites(pasta):
    suites = []
    for arq in sorted(pasta.glob("TEST-*.xml")):
        s = ET.parse(arq).getroot()
        casos = []
        for tc in s.findall("testcase"):
            problema = tc.find("failure")
            if problema is None:
                problema = tc.find("error")
            casos.append({
                "nome": tc.get("name"),
                "tempo": float(tc.get("time") or 0),
                "situacao": "pulado" if tc.find("skipped") is not None else ("falhou" if problema is not None else "ok"),
                "mensagem": (problema.get("message") or "").strip() if problema is not None else "",
            })
        exibicao = casos and s.findall("testcase")[0].get("classname") or s.get("name")
        suites.append({
            "classe": s.get("name"),
            "exibicao": exibicao,
            "nivel": nivel(s.get("name")),
            "testes": int(s.get("tests")),
            "falhas": int(s.get("failures")) + int(s.get("errors")),
            "pulados": int(s.get("skipped")),
            "tempo": float(s.get("time") or 0),
            "casos": casos,
        })
    return suites


def escapar(texto):
    return str(texto).replace("|", "\\|").replace("\n", " ")


suites = ler_suites(raiz / "target" / "surefire-reports") + ler_suites(raiz / "target" / "failsafe-reports")
if not suites:
    print("nenhum resultado encontrado: rode ./mvnw verify antes")
    sys.exit(1)

total = sum(s["testes"] for s in suites)
falhas = sum(s["falhas"] for s in suites)
pulados = sum(s["pulados"] for s in suites)

cobertura = {}
csv_jacoco = raiz / "target" / "site" / "jacoco" / "jacoco.csv"
pacotes = {}
if csv_jacoco.exists():
    linhas = list(csv.DictReader(csv_jacoco.open(encoding="utf-8")))
    for metrica in ("LINE", "BRANCH", "INSTRUCTION", "METHOD"):
        perdidas = sum(int(l[metrica + "_MISSED"]) for l in linhas)
        cobertas = sum(int(l[metrica + "_COVERED"]) for l in linhas)
        cobertura[metrica] = 100 * cobertas / (perdidas + cobertas) if perdidas + cobertas else 100.0
    for l in linhas:
        p = pacotes.setdefault(l["PACKAGE"].replace("br.senac.alocacao.", ""), [0, 0, 0, 0])
        p[0] += int(l["LINE_MISSED"]); p[1] += int(l["LINE_COVERED"])
        p[2] += int(l["BRANCH_MISSED"]); p[3] += int(l["BRANCH_COVERED"])


def pct(perdidas, cobertas):
    return f"{100 * cobertas / (perdidas + cobertas):.1f}%" if perdidas + cobertas else "—"


aprovado = falhas == 0 and (not cobertura or cobertura["LINE"] >= META_COBERTURA)

md = ["# 10. Relatório de testes de back-end", ""]
md.append("> Gerado automaticamente por `scripts/relatorio_backend.py` a partir dos relatórios do JUnit "
          "(`target/surefire-reports`, `target/failsafe-reports`) e do JaCoCo (`target/site/jacoco`), "
          "após `./mvnw verify`.")
md += ["", "## 10.1 Resumo", "", f"**Resultado: {'✅ APROVADO' if aprovado else '❌ REPROVADO'}**", ""]
md += ["| Item | Valor |", "|---|---|"]
md.append(f"| Data da geração | {datetime.now():%d/%m/%Y %H:%M} |")
md.append(f"| Testes executados | {total} |")
md.append(f"| Aprovados | {total - falhas - pulados} |")
md.append(f"| Falhas ou erros | {falhas} |")
md.append(f"| Pulados | {pulados} |")
md.append(f"| Tempo somado | {sum(s['tempo'] for s in suites):.1f} s |")
if cobertura:
    md.append(f"| Cobertura de linhas | {cobertura['LINE']:.1f}% (meta: {META_COBERTURA:.0f}%) |")
    md.append(f"| Cobertura de desvios (branches) | {cobertura['BRANCH']:.1f}% |")
    md.append(f"| Cobertura de métodos | {cobertura['METHOD']:.1f}% |")

md += ["", "## 10.2 Resultado por nível da pirâmide", "", "| Nível | Classes | Testes | Falhas | Tempo |",
       "|---|---|---|---|---|"]
for rotulo in ["Unitário"] + [r for _, r in NIVEIS]:
    grupo = [s for s in suites if s["nivel"] == rotulo]
    if grupo:
        md.append(f"| {rotulo} | {len(grupo)} | {sum(s['testes'] for s in grupo)} | "
                  f"{sum(s['falhas'] for s in grupo)} | {sum(s['tempo'] for s in grupo):.1f} s |")

md += ["", "## 10.3 Resultado por classe de teste", "", "| Nível | Classe | Descrição | Testes | Falhas | Tempo |",
       "|---|---|---|---|---|---|"]
for s in sorted(suites, key=lambda s: ([r for r in ["Unitário"] + [r for _, r in NIVEIS]].index(s["nivel"]), s["classe"])):
    md.append(f"| {s['nivel']} | `{s['classe'].split('.')[-1]}` | {escapar(s['exibicao'])} | {s['testes']} | "
              f"{'✅ 0' if s['falhas'] == 0 else '❌ ' + str(s['falhas'])} | {s['tempo']:.2f} s |")

md += ["", "## 10.4 Falhas", ""]
lista_falhas = [(s, c) for s in suites for c in s["casos"] if c["situacao"] == "falhou"]
if lista_falhas:
    md += ["| Classe | Teste | Mensagem |", "|---|---|---|"]
    for s, c in lista_falhas:
        md.append(f"| `{s['classe'].split('.')[-1]}` | `{escapar(c['nome'])}` | {escapar(c['mensagem'][:300])} |")
else:
    md.append("Nenhuma falha. Todos os testes passaram.")

if pacotes:
    md += ["", "## 10.5 Cobertura por pacote", "", "| Pacote | Linhas | Desvios |", "|---|---|---|"]
    for nome, (lm, lc, bm, bc) in sorted(pacotes.items()):
        md.append(f"| `{nome}` | {pct(lm, lc)} | {pct(bm, bc)} |")

lentos = sorted(((s, c) for s in suites for c in s["casos"]), key=lambda x: -x[1]["tempo"])[:10]
md += ["", "## 10.6 Testes mais lentos", "", "| Classe | Teste | Tempo |", "|---|---|---|"]
for s, c in lentos:
    md.append(f"| `{s['classe'].split('.')[-1]}` | `{escapar(c['nome'])}` | {c['tempo']:.2f} s |")

md += ["", "## 10.7 Lista completa de casos de teste", ""]
for s in suites:
    md.append(f"<details><summary><code>{s['classe'].split('.')[-1]}</code> — {s['testes']} testes</summary>")
    md.append("")
    md += ["| Teste | Situação | Tempo |", "|---|---|---|"]
    for c in s["casos"]:
        icone = {"ok": "✅", "falhou": "❌", "pulado": "⏭️"}[c["situacao"]]
        md.append(f"| `{escapar(c['nome'])}` | {icone} | {c['tempo']:.3f} s |")
    md += ["", "</details>", ""]

md += ["## 10.8 Como reproduzir", "", "```bash",
       "./mvnw verify                            # unitários + integração + cobertura (Testcontainers usa Docker)",
       "python3 scripts/relatorio_backend.py     # regenera este relatório",
       "```", "",
       "Sem Docker, os testes de integração podem usar um MySQL local (banco `alocacao_teste`):", "",
       "```bash",
       "MYSQL_URL_TESTE='jdbc:mysql://127.0.0.1:3306/alocacao_teste' ./mvnw verify",
       "```", ""]

saida.write_text("\n".join(md), encoding="utf-8")
print(f"relatório gerado: {saida} ({total} testes, {falhas} falhas)")
