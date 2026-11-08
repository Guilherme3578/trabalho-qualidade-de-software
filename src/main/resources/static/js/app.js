'use strict';

/* ======================================================================
 * Interface web da Alocação de Salas (JavaScript puro, sem frameworks).
 * Toda a regra de negócio fica no back-end; aqui apenas exibimos dados e
 * mostramos as mensagens de erro da API de forma compreensível.
 * ====================================================================== */

const $ = (seletor, raiz = document) => raiz.querySelector(seletor);

const estado = {
    token: sessionStorage.getItem('token'),
    usuario: JSON.parse(sessionStorage.getItem('usuario') || 'null'),
    edicao: null // reserva sendo alterada {id, versao}
};

// ---------------------------------------------------------------- utilitários

function esc(valor) {
    return String(valor ?? '').replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

function dataHora(iso) {
    if (!iso) return '';
    const [d, h] = iso.split('T');
    const [a, m, dia] = d.split('-');
    return `${dia}/${m}/${a} ${h.substring(0, 5)}`;
}

function hoje() {
    const d = new Date();
    d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
    return d.toISOString().substring(0, 10);
}

function temPerfil(...perfis) {
    return estado.usuario && perfis.includes(estado.usuario.perfil);
}

async function api(metodo, caminho, corpo) {
    const cabecalhos = { 'Content-Type': 'application/json' };
    if (estado.token) cabecalhos.Authorization = 'Bearer ' + estado.token;
    let resposta;
    try {
        resposta = await fetch(caminho, {
            method: metodo,
            headers: cabecalhos,
            body: corpo === undefined ? undefined : JSON.stringify(corpo)
        });
    } catch (e) {
        throw { detail: 'Não foi possível conectar ao servidor. Verifique sua conexão.' };
    }
    if (resposta.status === 204) return null;
    const dados = await resposta.json().catch(() => null);
    if (!resposta.ok) {
        if (resposta.status === 401 && estado.token && caminho !== '/api/auth/login') {
            sair();
        }
        throw dados || { detail: 'Erro inesperado (HTTP ' + resposta.status + ').' };
    }
    return dados;
}

function mostrarMensagem(tipo, html) {
    const caixa = $('#mensagem');
    caixa.className = 'mensagem ' + tipo;
    caixa.innerHTML = html;
    caixa.hidden = false;
    caixa.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function sucesso(texto) {
    mostrarMensagem('sucesso', esc(texto));
}

/** Converte a resposta Problem Details da API em mensagem legível. */
function erro(problema) {
    let html = esc(problema?.detail || 'Ocorreu um erro.');
    if (Array.isArray(problema?.campos) && problema.campos.length) {
        html += '<ul>' + problema.campos.map(c => `<li>${esc(c.mensagem)}</li>`).join('') + '</ul>';
    }
    if (problema?.correlationId) {
        html += `<small>Código para suporte: ${esc(problema.correlationId)}</small>`;
    }
    mostrarMensagem('erro', html);
}

function limparMensagem() {
    $('#mensagem').hidden = true;
}

async function executar(acao, textoSucesso, depois) {
    try {
        const r = await acao();
        if (textoSucesso) sucesso(typeof textoSucesso === 'function' ? textoSucesso(r) : textoSucesso);
        if (depois) await depois(r);
        return r;
    } catch (e) {
        erro(e);
        return undefined;
    }
}

// ---------------------------------------------------------------- sessão

async function entrar(evento) {
    evento.preventDefault();
    await executar(() => api('POST', '/api/auth/login', {
        email: $('#login-email').value,
        senha: $('#login-senha').value
    }), null, r => {
        estado.token = r.token;
        estado.usuario = r.usuario;
        sessionStorage.setItem('token', r.token);
        sessionStorage.setItem('usuario', JSON.stringify(r.usuario));
        limparMensagem();
        iniciarApp();
    });
}

function sair() {
    sessionStorage.clear();
    estado.token = null;
    estado.usuario = null;
    $('#tela-app').hidden = true;
    $('#usuario-logado').hidden = true;
    $('#tela-login').hidden = false;
}

// ---------------------------------------------------------------- navegação

const ABAS = [
    { id: 'disponibilidade', nome: 'Disponibilidade', render: telaDisponibilidade },
    { id: 'nova', nome: 'Nova reserva', render: telaNovaReserva, perfis: ['PROFESSOR', 'COORDENADOR'] },
    { id: 'reservas', nome: 'Reservas', render: telaReservas },
    { id: 'aprovacoes', nome: 'Aprovações', render: telaAprovacoes, perfis: ['RESPONSAVEL'] },
    { id: 'relatorios', nome: 'Relatórios', render: telaRelatorios, perfis: ['COORDENADOR', 'RESPONSAVEL', 'ADMIN'] },
    { id: 'cadastros', nome: 'Cadastros', render: telaCadastros, perfis: ['ADMIN'] },
    { id: 'notificacoes', nome: 'Notificações', render: telaNotificacoes }
];

function iniciarApp() {
    $('#tela-login').hidden = true;
    $('#tela-app').hidden = false;
    $('#usuario-logado').hidden = false;
    $('#usuario-nome').textContent = `${estado.usuario.nome} (${estado.usuario.perfil})`;
    const nav = $('#abas');
    nav.innerHTML = '';
    ABAS.filter(a => !a.perfis || temPerfil(...a.perfis)).forEach(a => {
        const b = document.createElement('button');
        b.type = 'button';
        b.textContent = a.nome;
        b.dataset.aba = a.id;
        b.addEventListener('click', () => abrir(a.id));
        nav.appendChild(b);
    });
    abrir('disponibilidade');
}

function abrir(id) {
    document.querySelectorAll('#abas button').forEach(b => b.setAttribute('aria-selected', b.dataset.aba === id));
    const aba = ABAS.find(a => a.id === id);
    $('#conteudo').innerHTML = '';
    aba.render($('#conteudo'));
}

// ---------------------------------------------------------------- telas

function telaDisponibilidade(raiz) {
    raiz.innerHTML = `
    <div class="cartao">
      <h2>Pesquisar salas disponíveis</h2>
      <form id="form-disp">
        <div class="grade">
          <label>Data <input id="d-data" type="date" value="${hoje()}" required></label>
          <label>Início <input id="d-inicio" type="time" value="19:00" required></label>
          <label>Término <input id="d-fim" type="time" value="21:00" required></label>
          <label>Tipo
            <select id="d-tipo"><option value="">Todos</option><option>SALA_AULA</option>
              <option>LABORATORIO</option><option>AUDITORIO</option></select></label>
          <label>Capacidade mínima <input id="d-cap" type="number" min="1"></label>
          <label>Localização <input id="d-local" placeholder="ex.: Bloco A"></label>
        </div>
        <button class="botao" type="submit">Pesquisar</button>
      </form>
    </div>
    <div id="disp-resultado"></div>`;
    $('#form-disp').addEventListener('submit', async e => {
        e.preventDefault();
        const data = $('#d-data').value;
        const p = new URLSearchParams({ inicio: `${data}T${$('#d-inicio').value}`, fim: `${data}T${$('#d-fim').value}` });
        if ($('#d-tipo').value) p.set('tipo', $('#d-tipo').value);
        if ($('#d-cap').value) p.set('capacidadeMinima', $('#d-cap').value);
        if ($('#d-local').value) p.set('localizacao', $('#d-local').value);
        const salas = await executar(() => api('GET', '/api/salas?' + p));
        if (!salas) return;
        limparMensagem();
        $('#disp-resultado').innerHTML = `<div class="cartao tabela-rolagem"><h3>${salas.length} sala(s) livre(s)</h3>
          <table><thead><tr><th>Código</th><th>Nome</th><th>Tipo</th><th>Capacidade</th><th>Local</th><th>Restrita</th></tr></thead>
          <tbody>${salas.map(s => `<tr><td>${esc(s.codigo)}</td><td>${esc(s.nome)}</td><td>${esc(s.tipo)}</td>
          <td>${s.capacidade}</td><td>${esc(s.localizacao)}</td><td>${s.restrita ? 'Sim (requer aprovação)' : 'Não'}</td></tr>`).join('')}
          </tbody></table></div>`;
    });
}

async function telaNovaReserva(raiz) {
    const [salas, professores, materiais] = await Promise.all([
        api('GET', '/api/salas'), api('GET', '/api/professores'), api('GET', '/api/materiais')
    ]).catch(e => { erro(e); return [[], [], []]; });
    const ed = estado.edicao;
    const profs = temPerfil('PROFESSOR')
        ? professores.filter(p => p.id === estado.usuario.professorId)
        : professores;

    raiz.innerHTML = `
    <div class="cartao">
      <h2>${ed ? 'Alterar reserva' : 'Nova reserva'}</h2>
      <form id="form-reserva">
        <div class="grade">
          <label>Sala <select id="r-sala" required>${salas.map(s =>
              `<option value="${s.id}">${esc(s.codigo)} — ${esc(s.nome)} (${s.capacidade})${s.restrita ? ' 🔒' : ''}</option>`).join('')}</select></label>
          <label>Professor <select id="r-professor" required>${profs.map(p =>
              `<option value="${p.id}">${esc(p.nome)}</option>`).join('')}</select></label>
          <label>Disciplina <input id="r-disciplina" maxlength="120" required></label>
          <label>Data <input id="r-data" type="date" value="${hoje()}" required></label>
          <label>Início <input id="r-inicio" type="time" value="19:00" required></label>
          <label>Término <input id="r-fim" type="time" value="21:00" required></label>
          <label>Alunos <input id="r-alunos" type="number" min="1" value="20" required></label>
        </div>
        <fieldset class="cartao"><legend>Materiais (opcional)</legend>
          <div class="materiais-lista">${materiais.map(m =>
              `<label>${esc(m.nome)}${m.restrito ? ' 🔒' : ''} <input type="number" min="0" value="0" data-material="${m.id}"></label>`).join('')}
          </div>
        </fieldset>
        <label>Observação <textarea id="r-obs" maxlength="500" rows="2"></textarea></label>
        <div class="acoes">
          <button id="r-salvar" class="botao" type="submit">${ed ? 'Salvar alteração' : 'Reservar'}</button>
          ${ed ? '<button id="r-cancelar-edicao" class="botao secundario" type="button">Descartar</button>' : ''}
        </div>
        <p class="dica">🔒 Recursos restritos exigem aprovação do responsável.</p>
      </form>
    </div>`;

    if (ed) {
        const r = ed.reserva;
        $('#r-sala').value = r.salaId;
        $('#r-professor').value = r.professorId;
        $('#r-disciplina').value = r.disciplina;
        $('#r-data').value = r.inicio.substring(0, 10);
        $('#r-inicio').value = r.inicio.substring(11, 16);
        $('#r-fim').value = r.fim.substring(11, 16);
        $('#r-alunos').value = r.quantidadeAlunos;
        $('#r-obs').value = r.observacao || '';
        r.materiais.forEach(i => {
            const campo = raiz.querySelector(`[data-material="${i.materialId}"]`);
            if (campo) campo.value = i.quantidade;
        });
        $('#r-cancelar-edicao').addEventListener('click', () => { estado.edicao = null; abrir('reservas'); });
    }

    $('#form-reserva').addEventListener('submit', async e => {
        e.preventDefault();
        const data = $('#r-data').value;
        const corpo = {
            salaId: $('#r-sala').value,
            professorId: $('#r-professor').value,
            disciplina: $('#r-disciplina').value,
            inicio: `${data}T${$('#r-inicio').value}`,
            fim: `${data}T${$('#r-fim').value}`,
            quantidadeAlunos: Number($('#r-alunos').value),
            observacao: $('#r-obs').value || null,
            materiais: [...raiz.querySelectorAll('[data-material]')]
                .filter(i => Number(i.value) > 0)
                .map(i => ({ materialId: i.dataset.material, quantidade: Number(i.value) })),
            versao: ed ? ed.reserva.versao : null
        };
        const chamada = ed
            ? () => api('PUT', '/api/reservas/' + ed.reserva.id, corpo)
            : () => api('POST', '/api/reservas', corpo);
        await executar(chamada, r => r.status === 'APROVADA'
            ? 'Reserva confirmada com sucesso!'
            : 'Reserva registrada e aguardando aprovação: ' + r.motivosAprovacao.join('; '), () => {
            estado.edicao = null;
        });
    });
}

function botoesReserva(r) {
    const eu = estado.usuario;
    const dono = r.solicitanteId === eu.id;
    const b = [];
    const add = (acao, texto, classe = 'secundario') =>
        b.push(`<button class="botao pequeno ${classe}" data-acao="${acao}" data-id="${r.id}" type="button">${texto}</button>`);

    if (dono && ['SOLICITADA', 'APROVADA'].includes(r.status)) {
        add('alterar', 'Alterar');
        add('cancelar', 'Cancelar', 'perigo');
    }
    if ((dono || eu.perfil === 'ADMIN') && !['EM_USO', 'CONCLUIDA', 'NAO_COMPARECEU'].includes(r.status)) {
        add('excluir', 'Apagar', 'perigo');
    }
    if ((dono || eu.perfil === 'RESPONSAVEL') && r.status === 'APROVADA') add('iniciar', 'Iniciar uso');
    if ((dono || eu.perfil === 'RESPONSAVEL') && r.status === 'EM_USO') add('concluir', 'Concluir');
    if (eu.perfil === 'RESPONSAVEL' && r.status === 'SOLICITADA') {
        add('aprovar', 'Aprovar', '');
        add('rejeitar', 'Rejeitar', 'perigo');
    }
    if (eu.perfil === 'RESPONSAVEL' && r.status === 'APROVADA') add('nao-compareceu', 'Não compareceu');
    if (eu.perfil === 'RESPONSAVEL' && r.status === 'EM_USO' && r.materiais.length) add('materiais', 'Retirada/Devolução');
    add('historico', 'Histórico');
    return `<div class="acoes">${b.join('')}</div>`;
}

function tabelaReservas(reservas) {
    if (!reservas.length) return '<p class="dica">Nenhuma reserva encontrada.</p>';
    return `<div class="tabela-rolagem"><table><thead><tr><th>Quando</th><th>Sala</th><th>Professor</th>
      <th>Disciplina</th><th>Materiais</th><th>Status</th><th>Ações</th></tr></thead><tbody>
      ${reservas.map(r => `<tr>
        <td>${dataHora(r.inicio)}–${r.fim.substring(11, 16)}</td>
        <td>${esc(r.salaCodigo)}</td><td>${esc(r.professorNome)}</td><td>${esc(r.disciplina)}</td>
        <td>${r.materiais.map(m => esc(m.materialNome) + ' x' + m.quantidade).join('<br>')}</td>
        <td><span class="status ${r.status}">${r.status}</span>
            ${r.motivosAprovacao.length && r.status === 'SOLICITADA' ? `<br><small>${esc(r.motivosAprovacao.join('; '))}</small>` : ''}</td>
        <td>${botoesReserva(r)}<div id="extra-${r.id}"></div></td></tr>`).join('')}
      </tbody></table></div>`;
}

async function acaoReserva(evento, recarregar) {
    const botao = evento.target.closest('[data-acao]');
    if (!botao) return;
    const { acao, id } = botao.dataset;
    const extra = $('#extra-' + id);
    const simples = { cancelar: 'cancelar', aprovar: 'aprovar', rejeitar: 'rejeitar', iniciar: 'iniciar',
        concluir: 'concluir', 'nao-compareceu': 'nao-compareceu' };

    if (acao === 'alterar') {
        const reserva = await executar(() => api('GET', '/api/reservas/' + id));
        if (reserva) { estado.edicao = { reserva }; abrir('nova'); }
    } else if (acao === 'excluir') {
        await executar(() => api('DELETE', '/api/reservas/' + id), 'Reserva apagada.', recarregar);
    } else if (simples[acao]) {
        await executar(() => api('POST', `/api/reservas/${id}/${simples[acao]}`, {}),
            r => `Reserva agora está ${r.status}.`, recarregar);
    } else if (acao === 'historico') {
        const eventos = await executar(() => api('GET', `/api/reservas/${id}/historico`));
        if (eventos) {
            extra.innerHTML = `<ul>${eventos.map(ev => `<li>${dataHora(ev.dataHora)} — ${esc(ev.usuarioNome)}:
                ${esc(ev.acao)} ${esc(ev.estadoAnterior || '')} → ${esc(ev.estadoNovo || '')} ${esc(ev.detalhes || '')}</li>`).join('')}</ul>`;
        }
    } else if (acao === 'materiais') {
        const r = await executar(() => api('GET', '/api/reservas/' + id));
        if (!r) return;
        extra.innerHTML = `<form class="cartao" id="mov-${id}">
            <label>Material <select name="material">${r.materiais.map(m =>
                `<option value="${m.materialId}">${esc(m.materialNome)} (reservado ${m.quantidade})</option>`).join('')}</select></label>
            <label>Quantidade <input name="qtd" type="number" min="1" value="1"></label>
            <div class="acoes"><button class="botao pequeno" data-tipo="retiradas" type="submit">Registrar retirada</button>
            <button class="botao pequeno secundario" data-tipo="devolucoes" type="submit">Registrar devolução</button></div></form>`;
        $('#mov-' + id).addEventListener('submit', async e => {
            e.preventDefault();
            const tipo = e.submitter.dataset.tipo;
            const f = e.target;
            await executar(() => api('POST', `/api/reservas/${id}/${tipo}`, {
                materialId: f.material.value, quantidade: Number(f.qtd.value)
            }), tipo === 'retiradas' ? 'Retirada registrada.' : 'Devolução registrada.');
        });
    }
}

function telaReservas(raiz) {
    const solicitante = temPerfil('PROFESSOR', 'COORDENADOR');
    raiz.innerHTML = `
    <div class="cartao">
      <h2>${solicitante ? 'Minhas reservas' : 'Agenda de reservas'}</h2>
      <form id="form-filtro"><div class="grade">
        <label>De <input id="f-de" type="date" value="${hoje()}"></label>
        <label>Até <input id="f-ate" type="date"></label>
        <label>Status <select id="f-status"><option value="">Todos</option>
          ${['SOLICITADA', 'APROVADA', 'EM_USO', 'CONCLUIDA', 'REJEITADA', 'CANCELADA', 'NAO_COMPARECEU']
              .map(s => `<option>${s}</option>`).join('')}</select></label>
        <button class="botao" type="submit">Filtrar</button></div></form>
    </div>
    <div id="lista-reservas" class="cartao"></div>`;

    const carregar = async () => {
        const p = new URLSearchParams();
        if ($('#f-de').value) p.set('inicio', $('#f-de').value + 'T00:00');
        if ($('#f-ate').value) p.set('fim', $('#f-ate').value + 'T23:59');
        if ($('#f-status').value) p.set('status', $('#f-status').value);
        if (solicitante) p.set('minhas', 'true');
        const lista = await executar(() => api('GET', '/api/reservas?' + p));
        if (lista) $('#lista-reservas').innerHTML = tabelaReservas(lista);
    };
    $('#form-filtro').addEventListener('submit', e => { e.preventDefault(); carregar(); });
    $('#lista-reservas').addEventListener('click', e => acaoReserva(e, carregar));
    carregar();
}

function telaAprovacoes(raiz) {
    raiz.innerHTML = '<div class="cartao"><h2>Reservas aguardando aprovação</h2><div id="lista-aprov"></div></div>';
    const carregar = async () => {
        const lista = await executar(() => api('GET', '/api/reservas?status=SOLICITADA'));
        if (lista) $('#lista-aprov').innerHTML = tabelaReservas(lista);
    };
    $('#lista-aprov').addEventListener('click', e => acaoReserva(e, carregar));
    carregar();
}

function telaRelatorios(raiz) {
    const inicioMes = hoje().substring(0, 8) + '01';
    raiz.innerHTML = `
    <div class="cartao"><h2>Relatórios</h2>
      <form id="form-rel"><div class="grade">
        <label>De <input id="rel-de" type="date" value="${inicioMes}" required></label>
        <label>Até <input id="rel-ate" type="date" value="${hoje()}" required></label>
        <button class="botao" type="submit">Gerar</button></div></form></div>
    <div id="rel-resultado"></div>`;
    $('#form-rel').addEventListener('submit', async e => {
        e.preventDefault();
        const p = `inicio=${$('#rel-de').value}&fim=${$('#rel-ate').value}`;
        const r = await executar(() => Promise.all([
            api('GET', '/api/relatorios/utilizacao?' + p),
            api('GET', '/api/relatorios/carga-horaria?' + p),
            api('GET', '/api/relatorios/conflitos-evitados?' + p)
        ]));
        if (!r) return;
        const [uso, carga, conf] = r;
        limparMensagem();
        const linhasUso = lista => lista.map(x => `<tr><td>${esc(x.descricao)}</td><td>${x.reservas}</td>
            <td>${x.horasReservadas}</td><td>${x.taxaOcupacaoPercentual}%</td></tr>`).join('');
        $('#rel-resultado').innerHTML = `
          <div class="cartao tabela-rolagem"><h3>Utilização de salas</h3><table><thead><tr><th>Sala</th><th>Reservas</th>
            <th>Horas</th><th>Ocupação</th></tr></thead><tbody>${linhasUso(uso.salas)}</tbody></table></div>
          <div class="cartao tabela-rolagem"><h3>Utilização de materiais</h3><table><thead><tr><th>Material</th><th>Reservas</th>
            <th>Unidades·hora</th><th>Ocupação</th></tr></thead><tbody>${linhasUso(uso.materiais)}</tbody></table></div>
          <div class="cartao tabela-rolagem"><h3>Carga horária por professor</h3><table><thead><tr><th>Professor</th>
            <th>Reservas</th><th>Horas</th></tr></thead><tbody>${carga.map(c => `<tr><td>${esc(c.professorNome)}</td>
            <td>${c.reservas}</td><td>${c.horasAlocadas}</td></tr>`).join('')}</tbody></table></div>
          <div class="cartao"><h3>Conflitos evitados: ${conf.total}</h3>
            <p>${Object.entries(conf.porTipo).map(([k, v]) => `${esc(k)}: ${v}`).join(' · ') || 'Nenhum'}</p></div>`;
    });
}

function telaCadastros(raiz) {
    raiz.innerHTML = `
    <div class="cartao"><h2>Nova sala</h2><form id="c-sala"><div class="grade">
      <label>Código <input name="codigo" required></label><label>Nome <input name="nome" required></label>
      <label>Tipo <select name="tipo"><option>SALA_AULA</option><option>LABORATORIO</option><option>AUDITORIO</option></select></label>
      <label>Capacidade <input name="capacidade" type="number" min="1" required></label>
      <label>Localização <input name="localizacao" required></label>
      <label>Restrita <select name="restrita"><option value="false">Não</option><option value="true">Sim</option></select></label>
      <button class="botao" type="submit">Cadastrar</button></div></form></div>

    <div class="cartao"><h2>Novo professor</h2><form id="c-prof"><div class="grade">
      <label>Nome <input name="nome" required></label><label>E-mail <input name="email" type="email" required></label>
      <label>Departamento <input name="departamento"></label>
      <label>Competências (separadas por vírgula) <input name="competencias"></label>
      <button class="botao" type="submit">Cadastrar</button></div></form></div>

    <div class="cartao"><h2>Novo material</h2><form id="c-mat"><div class="grade">
      <label>Nome <input name="nome" required></label><label>Categoria <input name="categoria" required></label>
      <label>Quantidade <input name="quantidadeTotal" type="number" min="0" required></label>
      <label>Localização <input name="localizacao"></label>
      <label>Restrito <select name="restrito"><option value="false">Não</option><option value="true">Sim</option></select></label>
      <button class="botao" type="submit">Cadastrar</button></div></form></div>

    <div class="cartao"><h2>Bloqueio / manutenção</h2><form id="c-bloq"><div class="grade">
      <label>Tipo <select name="tipoRecurso"><option>SALA</option><option>MATERIAL</option><option>PROFESSOR</option></select></label>
      <label>ID do recurso <input name="recursoId" required></label>
      <label>Início <input name="inicio" type="datetime-local" required></label>
      <label>Término <input name="fim" type="datetime-local" required></label>
      <label>Motivo <input name="motivo" required></label>
      <button class="botao" type="submit">Bloquear</button></div></form></div>

    <div class="cartao"><h2>Novo usuário</h2><form id="c-usu"><div class="grade">
      <label>Nome <input name="nome" required></label><label>E-mail <input name="email" type="email" required></label>
      <label>Senha <input name="senha" type="password" required></label>
      <label>Perfil <select name="perfil"><option>PROFESSOR</option><option>COORDENADOR</option>
        <option>RESPONSAVEL</option><option>ADMIN</option></select></label>
      <label>ID do professor (perfil PROFESSOR) <input name="professorId"></label>
      <button class="botao" type="submit">Cadastrar</button></div></form></div>
    <div class="cartao tabela-rolagem" id="c-listas"></div>`;

    const dados = form => Object.fromEntries(new FormData(form).entries());
    const enviar = (id, caminho, transformar, texto) => $(id).addEventListener('submit', async e => {
        e.preventDefault();
        await executar(() => api('POST', caminho, transformar(dados(e.target))), texto, () => {
            e.target.reset();
            listar();
        });
    });
    enviar('#c-sala', '/api/salas', d => ({ ...d, capacidade: Number(d.capacidade), restrita: d.restrita === 'true' }),
        'Sala cadastrada.');
    enviar('#c-prof', '/api/professores', d => ({
        ...d, competencias: d.competencias.split(',').map(s => s.trim()).filter(Boolean)
    }), 'Professor cadastrado.');
    enviar('#c-mat', '/api/materiais', d => ({ ...d, quantidadeTotal: Number(d.quantidadeTotal), restrito: d.restrito === 'true' }),
        'Material cadastrado.');
    enviar('#c-bloq', '/api/bloqueios', d => d, r => r.reservasAfetadas.length
        ? `Bloqueio criado. Atenção: ${r.reservasAfetadas.length} reserva(s) existente(s) no período.`
        : 'Bloqueio criado.');
    enviar('#c-usu', '/api/usuarios', d => ({ ...d, professorId: d.professorId || null }), 'Usuário cadastrado.');

    async function listar() {
        const r = await executar(() => Promise.all([
            api('GET', '/api/salas?incluirInativas=true'), api('GET', '/api/professores'), api('GET', '/api/materiais')]));
        if (!r) return;
        const [salas, profs, mats] = r;
        const linhas = (itens, f) => itens.map(i => `<tr><td><code>${esc(i.id)}</code></td><td>${esc(f(i))}</td></tr>`).join('');
        $('#c-listas').innerHTML = `<h3>IDs cadastrados</h3><table><thead><tr><th>ID</th><th>Recurso</th></tr></thead><tbody>
          ${linhas(salas, s => 'Sala ' + s.codigo + (s.ativa ? '' : ' (inativa)'))}
          ${linhas(profs, p => 'Professor ' + p.nome)}${linhas(mats, m => 'Material ' + m.nome)}</tbody></table>`;
    }
    listar();
}

async function telaNotificacoes(raiz) {
    const lista = await executar(() => api('GET', '/api/notificacoes'));
    if (!lista) return;
    raiz.innerHTML = `<div class="cartao"><h2>Notificações</h2>${lista.length ? `<ul>${lista.map(n =>
        `<li>${dataHora(n.criadoEm)} — ${esc(n.mensagem)} <small>(${esc(n.status)})</small></li>`).join('')}</ul>`
        : '<p class="dica">Nenhuma notificação.</p>'}</div>`;
}

// ---------------------------------------------------------------- início

$('#form-login').addEventListener('submit', entrar);
$('#sair').addEventListener('click', sair);
if (estado.token && estado.usuario) {
    iniciarApp();
}
