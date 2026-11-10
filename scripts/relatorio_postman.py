import json
import sys
from datetime import datetime
from pathlib import Path

if len(sys.argv) != 3:
    print("uso: python3 scripts/relatorio_postman.py <resultado-newman.json> <saida.md>")
    sys.exit(1)

dados = json.loads(Path(sys.argv[1]).read_text(encoding="utf-8"))
run = dados["run"]
stats = run["stats"]
tempos = run["timings"]

pasta_do_item = {}


def mapear(itens, pasta=None):
    for item in itens:
        if "item" in item:
            mapear(item["item"], item["name"])
        else:
            pasta_do_item[item["id"]] = pasta or "(raiz)"


mapear(dados["collection"]["item"])


def url(req):
    u = req["url"]
    caminho = "/" + "/".join(u.get("path", []))
    query = [q for q in u.get("query", []) if not q.get("disabled")]
    if query:
        caminho += "?" + "&".join(f"{q['key']}={q.get('value', '')}" for q in query)
    return caminho


def escapar(texto):
    return str(texto).replace("|", "\\|").replace("\n", " ")


inicio = datetime.fromtimestamp(tempos["started"] / 1000)
duracao = (tempos["completed"] - tempos["started"]) / 1000
falhas_req = stats["requests"]["failed"]
falhas_ass = stats["assertions"]["failed"]
aprovado = falhas_req == 0 and falhas_ass == 0

por_pasta = {}
falhas = []
for ex in run["executions"]:
    pasta = pasta_do_item.get(ex["item"]["id"], "(raiz)")
    asserts = ex.get("assertions", [])
    ok = sum(1 for a in asserts if not a.get("error") and not a.get("skipped"))
    erro = [a for a in asserts if a.get("error")]
    resp = ex.get("response", {})
    linha = {
        "nome": ex["item"]["name"],
        "metodo": ex["request"]["method"],
        "url": url(ex["request"]),
        "status": f"{resp.get('code', '—')} {resp.get('status', '')}".strip(),
        "tempo": resp.get("responseTime", 0),
        "ok": ok,
        "total": len(asserts),
        "asserts": asserts,
    }
    por_pasta.setdefault(pasta, []).append(linha)
    for a in erro:
        falhas.append((pasta, linha["nome"], a["assertion"], a["error"].get("message", "")))

mais_lentas = sorted((l for ls in por_pasta.values() for l in ls), key=lambda l: -l["tempo"])[:5]

md = []
md.append("# 9. Relatório de testes de API (Postman)")
md.append("")
md.append(f"> Gerado automaticamente por `scripts/relatorio_postman.py` a partir da execução da collection "
          f"`postman/alocacao-salas.postman_collection.json` com o ambiente `postman/local.postman_environment.json`.")
md.append("")
md.append("## 9.1 Resumo")
md.append("")
md.append(f"**Resultado: {'✅ APROVADO' if aprovado else '❌ REPROVADO'}**")
md.append("")
md.append("| Item | Valor |")
md.append("|---|---|")
md.append(f"| Data da execução | {inicio:%d/%m/%Y %H:%M:%S} |")
md.append(f"| Duração total | {duracao:.1f} s |")
md.append(f"| Requisições executadas | {stats['requests']['total']} ({falhas_req} com falha) |")
md.append(f"| Asserções verificadas | {stats['assertions']['total']} ({falhas_ass} com falha) |")
md.append(f"| Scripts de teste | {stats['testScripts']['total']} ({stats['testScripts']['failed']} com falha) |")
md.append(f"| Scripts de pré-requisição | {stats['prerequestScripts']['total']} "
          f"({stats['prerequestScripts']['failed']} com falha) |")
md.append(f"| Tempo de resposta médio | {tempos['responseAverage']:.0f} ms "
          f"(mín. {tempos['responseMin']} ms, máx. {tempos['responseMax']} ms) |")
md.append("")
md.append("## 9.2 Resultado por pasta")
md.append("")
md.append("| Pasta | Requisições | Asserções aprovadas | Situação |")
md.append("|---|---|---|---|")
for pasta, linhas in por_pasta.items():
    ok = sum(l["ok"] for l in linhas)
    total = sum(l["total"] for l in linhas)
    md.append(f"| {escapar(pasta)} | {len(linhas)} | {ok}/{total} | {'✅' if ok == total else '❌'} |")
md.append("")
md.append("## 9.3 Falhas")
md.append("")
if falhas:
    md.append("| Pasta | Requisição | Asserção | Mensagem |")
    md.append("|---|---|---|---|")
    for f in falhas:
        md.append("| " + " | ".join(escapar(x) for x in f) + " |")
else:
    md.append("Nenhuma falha. Todas as asserções passaram.")
md.append("")
md.append("## 9.4 Requisições mais lentas")
md.append("")
md.append("| Requisição | Rota | Tempo |")
md.append("|---|---|---|")
for l in mais_lentas:
    md.append(f"| {escapar(l['nome'])} | `{l['metodo']} {escapar(l['url'])}` | {l['tempo']} ms |")
md.append("")
md.append("## 9.5 Detalhamento de todas as requisições")
for pasta, linhas in por_pasta.items():
    md.append("")
    md.append(f"### {pasta}")
    md.append("")
    md.append("| # | Requisição | Rota | Status | Tempo | Asserções |")
    md.append("|---|---|---|---|---|---|")
    for i, l in enumerate(linhas, 1):
        nomes = "<br>".join(("✅ " if not a.get("error") else "❌ ") + escapar(a["assertion"]) for a in l["asserts"])
        md.append(f"| {i} | {escapar(l['nome'])} | `{l['metodo']} {escapar(l['url'])}` | {l['status']} | "
                  f"{l['tempo']} ms | {nomes or '—'} |")
md.append("")
md.append("## 9.6 Como reproduzir")
md.append("")
md.append("1. Suba o MySQL e o WireMock: `docker compose up -d mysql wiremock` (ou use um MySQL local, ver README).")
md.append("2. Inicie a aplicação no perfil `dev`: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`.")
md.append("3. Execute a collection, pelo Collection Runner do Postman ou pela linha de comando:")
md.append("")
md.append("```bash")
md.append("npx newman@6.2.1 run postman/alocacao-salas.postman_collection.json \\")
md.append("  -e postman/local.postman_environment.json \\")
md.append("  --reporters cli,json --reporter-json-export target/newman.json")
md.append("python3 scripts/relatorio_postman.py target/newman.json docs/09-relatorio-testes-api.md")
md.append("```")
md.append("")
md.append("A pasta 07 cria uma reserva que começa cerca de 10 minutos após a execução, então rode entre 07h e 21h30.")
md.append("")

Path(sys.argv[2]).write_text("\n".join(md), encoding="utf-8")
print(f"relatório gerado: {sys.argv[2]} ({'aprovado' if aprovado else 'reprovado'})")
