#!/usr/bin/env python3
"""
Gera docs/04-matriz-rastreabilidade.md cruzando os requisitos de docs/01-requisitos.md
com os testes que os citam (IDs entre colchetes no @DisplayName, ex.: "[RF05][RN02] ...",
e tags @RFxx nos arquivos .feature).

Uso:  python3 scripts/rastreabilidade.py
Se existirem relatórios do Surefire/Failsafe em target/, a coluna "Resultado" é preenchida.
"""
import re
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
REQUISITOS = RAIZ / "docs" / "01-requisitos.md"
SAIDA = RAIZ / "docs" / "04-matriz-rastreabilidade.md"
TESTES = RAIZ / "src" / "test"
ID = re.compile(r"\b(RNF\d{2}|RF\d{2}|RN\d{2})\b")

NIVEIS = {
    "/regras/": "Unitário", "StatusReservaTest": "Unitário", "ProtecaoConcorrenciaTest": "Unitário",
    "LimitadorTentativasLoginTest": "Unitário", "UtilitariosTest": "Unitário",
    "/integracao/": "Integração", "/concorrencia/": "Concorrência", "/api/": "API caixa-preta",
    "/e2e/": "End-to-end", "/bdd/": "BDD", ".feature": "BDD",
}

POR_FERRAMENTA = {
    "RNF01": ("JMeter", "src/test/jmeter/carga-reservas.jmx",
              "20 usuários simultâneos; build falha com erro > 1%; Duration Assertion de 1000 ms"),
    "RNF05": ("JaCoCo", "pom.xml (jacoco:check)", "build falha com cobertura < 80% de linhas ou < 70% de ramos"),
    "RNF06": ("SonarCloud", ".github/workflows/ci.yml", "análise estática e Quality Gate em cada pull request"),
}

def nivel(caminho: str) -> str:
    for chave, valor in NIVEIS.items():
        if chave in caminho:
            return valor
    return "Outro"

def ler_requisitos():
    reqs = {}
    for linha in REQUISITOS.read_text(encoding="utf-8").splitlines():
        m = re.match(r"^\|\s*\*?\*?((?:RNF|RF|RN)\d{2})\*?\*?\s*\|\s*([^|]+)\|", linha)
        if m:
            reqs[m.group(1)] = m.group(2).strip()
    return reqs

def ler_testes():
    """Devolve {id_requisito: [(nivel, classe, descricao)]}."""
    mapa = defaultdict(list)
    for arq in sorted(TESTES.rglob("*.java")):
        texto = arq.read_text(encoding="utf-8")
        classe = arq.stem
        for m in re.finditer(r'@(?:DisplayName|ParameterizedTest|RepeatedTest)\((?:[^)]*?name\s*=\s*)?"([^"]+)"', texto):
            desc = m.group(1)
            for rid in set(ID.findall(desc)):
                mapa[rid].append((nivel(str(arq)), classe, re.sub(r"\[[A-Z]+\d+\]", "", desc).strip()))
    for arq in sorted(TESTES.rglob("*.feature")):
        texto = arq.read_text(encoding="utf-8")
        tags = set(ID.findall(" ".join(l for l in texto.splitlines() if l.strip().startswith("@"))))
        for cen in re.findall(r"^\s*(?:Cenário|Esquema do Cenário):\s*(.+)$", texto, re.M):
            for rid in tags:
                mapa[rid].append(("BDD", arq.name, cen.strip()))
    return {k: list(dict.fromkeys(v)) for k, v in mapa.items()}

def ler_resultados():
    total = falhas = 0
    for pasta in ("surefire-reports", "failsafe-reports"):
        for xml in (RAIZ / "target" / pasta).glob("TEST-*.xml"):
            raiz = ET.parse(xml).getroot()
            total += int(raiz.get("tests", 0))
            falhas += int(raiz.get("failures", 0)) + int(raiz.get("errors", 0))
    return total, falhas

def main():
    reqs = ler_requisitos()
    testes = ler_testes()
    total, falhas = ler_resultados()

    linhas = [
        "# Matriz de rastreabilidade",
        "",
        "> Arquivo **gerado automaticamente** por `scripts/rastreabilidade.py` — não edite à mão.",
        "> Cada teste cita os requisitos que verifica no próprio nome (ex.: `[RF05][RN02]`).",
        "",
    ]
    if total:
        linhas += [f"Última execução encontrada em `target/`: **{total} testes, {falhas} falha(s).**", ""]

    linhas += ["## Resumo", "", "| Requisito | Descrição | Qtde. de testes | Níveis |", "|---|---|---|---|"]
    sem_cobertura = []
    for rid, desc in reqs.items():
        ts = testes.get(rid, []) + [(f[0], f[1], f[2]) for k, f in POR_FERRAMENTA.items() if k == rid]
        if not ts:
            sem_cobertura.append(rid)
        niveis = ", ".join(sorted({t[0] for t in ts})) or "⚠️ sem teste automatizado"
        linhas.append(f"| {rid} | {desc} | {len(ts)} | {niveis} |")

    linhas += ["", "## Detalhamento", ""]
    for rid, desc in reqs.items():
        linhas += [f"### {rid} — {desc}", ""]
        ts = testes.get(rid, []) + [(f[0], f[1], f[2]) for k, f in POR_FERRAMENTA.items() if k == rid]
        if not ts:
            linhas += ["_Sem teste automatizado associado (ver justificativa em 01-requisitos.md)._", ""]
            continue
        linhas += ["| Nível | Classe / arquivo | Caso de teste |", "|---|---|---|"]
        linhas += [f"| {n} | `{c}` | {d} |" for n, c, d in ts]
        linhas.append("")

    SAIDA.write_text("\n".join(linhas) + "\n", encoding="utf-8")
    print(f"Matriz gerada em {SAIDA.relative_to(RAIZ)}: {len(reqs)} requisitos, "
          f"{len(reqs) - len(sem_cobertura)} com testes.")
    if sem_cobertura:
        print("Requisitos sem teste automatizado:", ", ".join(sem_cobertura))
    return 0

if __name__ == "__main__":
    sys.exit(main())
