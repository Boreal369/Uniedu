/* utilitarios */
const $ = (selector) => document.querySelector(selector);

const esc = (str) =>
  String(str).replace(/[&<>"]/g, (char) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;"
  }[char]));

const pf = (val) => parseFloat(String(val).replace(",", ".")) || 0;

const f1 = (num) => num.toFixed(1).replace(".", ",");

const bd = (colorClass, text) => `<span class="badge b-${colorClass}">${text}</span>`;

const sit = (avg) => (avg >= 6 ? bd("green", "Acima da média") : bd("red", "Abaixo da média"));

const dv = '<div class="dv"></div>';

function toast(message) {
  const t = $("#toast");
  if (!t) return;
  t.textContent = message;
  t.classList.add("show");
  clearTimeout(toast.h);
  toast.h = setTimeout(() => t.classList.remove("show"), 2400);
}

/* dados*/
const db = {
  baixo: [
    ["Ana Beatriz Souza", "8º A", 4, 0],
    ["Carlos Eduardo Lima", "9º B", 5, 0],
    ["Marina Alves", "7º C", 4, 1],
    ["João Pedro Santos", "8º B", 6, 1]
  ],
  pn: [
    ["Ana Beatriz Souza", 5, 4.5],
    ["Carlos Eduardo Lima", 6.5, 7],
    ["Marina Alves", 5.5, 5],
    ["João Pedro Santos", 8, 7.5],
    ["Luiza Ferreira", 9, 8.5]
  ],
  avisos: [
    ["Reunião pedagógica da unidade", "Sexta · todos os professores"],
    ["Entrega de notas da unidade", "Até 20/11 · professores"],
    ["Calendário de provas publicado", "Hoje · todas as turmas"]
  ],
  materiais: [
    ["Videoaula: Frações", "Matemática"],
    ["Exercícios: Equações", "Matemática"],
    ["Revisão: Interpretação de texto", "Português"]
  ],
  datas: [
    ["Prova de Matemática", "12/11"],
    ["Entrega de trabalho de História", "18/11"]
  ],
  notas: [
    ["Matemática", 5.8],
    ["Português", 7.5],
    ["Ciências", 8.2],
    ["História", 7],
    ["Geografia", 6.8]
  ],
  turmas: ["7º C", "8º A", "8º B", "9º B"],
  disc: ["Matemática", "Português", "Ciências", "História", "Geografia", "Inglês"],
  profs: ["Helena Prado", "Ricardo Matos", "Sônia Barros"],
  nomes: {
    Aluno: ["Ana Beatriz Souza", "Carlos Eduardo Lima", "Marina Alves", "João Pedro Santos", "Luiza Ferreira"],
    Professor: ["Helena Prado", "Ricardo Matos", "Sônia Barros"],
    Turma: ["7º C", "8º A", "8º B", "9º B"],
    Disciplina: ["Matemática", "Português", "Ciências", "História", "Geografia", "Inglês"]
  },
  vinc: [
    ["Helena Prado", "8º A · Matemática"],
    ["Ricardo Matos", "9º B · Português"]
  ],
  freq: {}
};

const KEY = "uniedu";
let saved = null;
try {
  saved = JSON.parse(sessionStorage.getItem(KEY) || "null");
} catch (e) {}

if (saved && saved.db) {
  Object.assign(db, saved.db);
}

const S = Object.assign(
  { user: null, tab: "Aluno", err: "" },
  (saved && saved.S) || {}
);

const save = () => {
  try {
    sessionStorage.setItem(
      KEY,
      JSON.stringify({
        db,
        S: { user: S.user, tab: S.tab }
      })
    );
  } catch (e) {}
};

/* config*/
const SLUG = {
  baixo: "baixo-rendimento",
  freq: "frequencia",
  material: "materiais",
  docs: "documentos"
};

const PFX = {
  admin: "admin",
  prof: "professor",
  aluno: "aluno"
};

const url = (role, view) => PFX[role] + "-" + (SLUG[view] || view) + ".html";

const PAGE = document.body.dataset;
const ROLEP = PAGE.role;
const VIEW = PAGE.view;

const NAV = {
  admin: [
    ["inicio", "Início"],
    ["cadastros", "Cadastros"],
    ["turmas", "Turmas e disciplinas"],
    ["vincular", "Vincular professores"],
    ["avisos", "Avisos"],
    ["desempenho", "Desempenho"],
    ["baixo", "Baixo rendimento"],
    ["relatorios", "Relatórios"]
  ],
  prof: [
    ["inicio", "Início"],
    ["turmas", "Minhas turmas"],
    ["notas", "Lançar notas"],
    ["freq", "Frequência"],
    ["material", "Materiais"],
    ["avisos", "Avisos"],
    ["calendario", "Calendário"],
    ["desempenho", "Desempenho"]
  ],
  aluno: [
    ["inicio", "Início"],
    ["notas", "Notas"],
    ["freq", "Frequência"],
    ["material", "Materiais"],
    ["avisos", "Avisos"],
    ["desempenho", "Desempenho"],
    ["docs", "Documentos"],
    ["calendario", "Calendário"]
  ]
};

const ROLE = {
  admin: "Administrador",
  prof: "Professor",
  aluno: "Aluno"
};

const NAME = {
  admin: "Coordenação",
  prof: "Professor(a)",
  aluno: "Aluno(a)"
};

/*componentes UI*/
const head = (title, sub, extra = "") =>
  `<div class="head"><div><h1>${title}</h1><p class="sub">${sub}</p></div>${extra}</div>`;

const stats = (items) =>
  `<div class="stats">${items
    .map(
      (s) =>
        `<div class="card stat"><small>${s[0]}</small><b${
          s[3] ? ' style="color:var(--red)"' : ""
        }>${s[1]}</b><span>${s[2]}</span></div>`
    )
    .join("")}</div>`;

const card = (header, body, extraClass = "") =>
  `<div class="card ${extraClass}">${header ? `<h2>${header}</h2>` : ""}${body}</div>`;

const list = (rows, mode = "") =>
  rows
    .map(
      (r) =>
        `${mode === "b" ? dv : ""}<div class="li${
          mode ? "" : " nd"
        }"><span><b>${esc(r[0])}</b>${
          r[1] ? `<small>${esc(r[1])}</small>` : ""
        }</span>${r[2] || ""}</div>${mode === "a" ? dv : ""}`
    )
    .join("");

const fld = (name, label, type = "text", placeholder = "") => {
  if (Array.isArray(type)) {
    return `<label class="field">${label}<select name="${name}">${type
      .map((opt) => `<option>${esc(opt)}</option>`)
      .join("")}</select></label>`;
  }
  if (type === "area") {
    return `<label class="field">${label}<textarea name="${name}" rows="3" required></textarea></label>`;
  }
  return `<label class="field">${label}<input name="${name}" type="${type}" placeholder="${placeholder}" required></label>`;
};

const form = (id, header, fields, submitText) =>
  `<form class="card" id="${id}"><h2>${header}</h2>${fields.join("")}<div><button class="btn">${submitText}</button></div></form>`;

const low = () =>
  `<div class="tbl"><div class="tr th"><span class="c1">Aluno</span><span class="c2">Turma</span><span class="c3">Disciplinas</span><span class="cf">Situação</span></div>${dv}${db.baixo
    .map(
      (r, i) =>
        `<div class="tr"><b class="c1" style="font-weight:500">${r[0]}</b><span class="c2">${r[1]}</span><span class="c3">${r[2]}</span><span class="cf"><button class="badge bb b-${
          r[3] ? "amber" : "red"
        }" data-meet="${i}" title="Alternar reunião">${
          r[3] ? "Reunião marcada" : "Reunião pendente"
        }</button></span></div>${dv}`
    )
    .join("")}</div>`;

const notasCard = () =>
  card(
    "Notas por disciplina",
    list(
      db.notas.map((n) => [
        n[0],
        "",
        `<span class="rr"><b style="color:${
          n[1] < 6 ? "var(--red)" : "var(--text)"
        }">${f1(n[1])}</b>${sit(n[1])}</span>`
      ]),
      "b"
    ),
    "gap8"
  );

const matCard = () => card("Materiais de apoio", list(db.materiais, ""), "");

const datasCard = (title) =>
  card(
    title || "Próximas datas",
    db.datas
      .map(
        (d) =>
          `<div class="li nd"><b>${esc(d[0])}</b>${bd("amber", esc(d[1]))}</div>`
      )
      .join("")
  );

const avisosForm = () =>
  form(
    "af",
    "Publicar aviso",
    [fld("t", "Título"), fld("m", "Mensagem", "area")],
    "Publicar aviso"
  );

const avisosList = () => card("Publicados", list(db.avisos, "a"), "fl");

const FORMS = {
  Aluno: [
    ["Nome completo", "Nome do aluno"],
    ["Matrícula", "000000"],
    ["E-mail", "aluno@escola.com.br"],
    ["Turma", "Selecione a turma"],
    ["Login", "nome.sobrenome"],
    ["Senha provisória", "••••••••"]
  ],
  Professor: [
    ["Nome completo", "Nome do professor"],
    ["E-mail", "professor@escola.com.br"],
    ["Disciplina", "Selecione a disciplina"],
    ["Login", "nome.sobrenome"],
    ["Senha provisória", "••••••••"]
  ],
  Turma: [
    ["Nome da turma", "8º A"],
    ["Ano letivo", "2026"]
  ],
  Disciplina: [
    ["Nome da disciplina", "Matemática"],
    ["Carga horária", "80 h"]
  ]
};

/*Admin, Professor, Aluno*/
const V = {
  admin: {
    inicio: () =>
      head(
        "Painel da coordenação",
        "Visão geral da rede de colégios · dados de exemplo",
        `<button class="btn gold" data-rel="Relatório de desempenho">Gerar relatório</button>`
      ) +
      stats([
        ["Alunos ativos", "1.248", "em toda a rede"],
        ["Professores", "86", "vinculados a turmas"],
        ["Turmas", "42", "ano letivo 2026"],
        ["Baixo rendimento", "17", "alunos na unidade atual", 1]
      ]) +
      `<div class="cols"><div class="card gap8"><h2>Alunos com baixo rendimento</h2><p class="sub s13" style="margin:0">Notas abaixo da média em mais de 3 disciplinas ao final da unidade (RF011)</p>${low()}</div>${card(
        "Avisos da coordenação",
        list(db.avisos.slice(0, 3), "a") +
          `<div class="sp"></div><button class="btn" data-go="avisos">Publicar aviso</button>`,
        "fl"
      )}</div>`,

    cadastros: () =>
      head(
        "Cadastrar " + S.tab.toLowerCase(),
        "Preencha os dados do usuário. Dados duplicados ou inválidos são recusados antes de salvar."
      ) +
      `<div class="tabs">${Object.keys(FORMS)
        .map(
          (t) =>
            `<button class="tab ${S.tab === t ? "on" : ""}" data-tab="${t}">${t}</button>`
        )
        .join("")}</div>
      <form class="card" id="cf" style="gap:20px"><div class="grid2" style="row-gap:20px">${FORMS[
        S.tab
      ]
        .map((f, i) =>
          fld("f" + i, f[0], f[0].includes("Senha") ? "password" : "text", f[1])
        )
        .join(
          ""
        )}
        
    turmas: () =>
      head("Turmas e disciplinas", "Estrutura escolar cadastrada") +
      `<div class="grid2">${card(
        "Turmas",
        list(db.nomes.Turma.map((t) => [t]), "a"),
        "fl"
      )}${card(
        "Disciplinas",
        list(db.nomes.Disciplina.map((t) => [t]), "a"),
        "fl"
      )}</div>`,

    vincular: () =>
      head("Vincular professores", "Associe professores às turmas e disciplinas") +
      `<div class="cols">${form(
        "vf",
        "Novo vínculo",
        [
          fld("p", "Professor", db.nomes.Professor),
          fld("t", "Turma", db.nomes.Turma),
          fld("d", "Disciplina", db.nomes.Disciplina)
        ],
        "Vincular"
      )}${card("Vínculos", list(db.vinc, "a"), "fl")}</div>`,

    avisos: () =>
      head("Avisos", "Publique comunicados para a plataforma") +
      `<div class="cols">${avisosForm()}${avisosList()}</div>`,

    desempenho: () =>
      head("Desempenho", "Média e frequência por turma · dados de exemplo") +
      card(
        "",
        list(
          [
            ["8º A", "Média 7,4 · Frequência 95%", bd("green", "Regular")],
            ["8º B", "Média 6,9 · Frequência 92%", bd("green", "Regular")],
            ["9º B", "Média 5,8 · Frequência 88%", bd("red", "Atenção")],
            ["7º C", "Média 6,1 · Frequência 90%", bd("amber", "Acompanhar")]
          ],
          "a"
        ),
        "fl"
      ),

    baixo: () =>
      head("Baixo rendimento", "Alunos com notas abaixo da média em mais de 3 disciplinas") +
      `<div class="card gap8">${low()}</div>`,

    relatorios: () =>
      head("Relatórios", "Gere relatórios de desempenho acadêmico") +
      card(
        "",
        ["Desempenho por turma", "Baixo rendimento da unidade", "Frequência geral"]
          .map(
            (r) =>
              `<div class="li"><b>${r}</b><button class="btn sec" data-rel="${r}">Gerar</button></div>`
          )
          .join(dv),
        "gap8"
      )
  },

  prof: {
    inicio: () =>
      head("Painel do professor", "Resumo das suas turmas · dados de exemplo") +
      stats([
        ["Minhas turmas", "3", "ano letivo 2026"],
        ["Provas marcadas", "2", "neste mês"],
        ["Abaixo da média", "2", "alunos em Matemática", 1]
      ]) +
      datasCard(),

    turmas: () =>
      head("Minhas turmas", "Turmas e disciplinas vinculadas a você") +
      card(
        "",
        list(
          [
            ["8º A", "Matemática · 32 alunos"],
            ["8º B", "Matemática · 30 alunos"],
            ["9º B", "Matemática · 28 alunos"]
          ],
          "a"
        ),
        "fl"
      ),

    notas: () =>
      head(
        "Lançar notas",
        "Selecione turma, disciplina e unidade. Média de aprovação de exemplo: 6,0",
        `<button class="btn" data-save>Salvar notas</button>`
      ) +
      `<div class="grid3">${[
        ["Turma", "8º A"],
        ["Disciplina", "Matemática"],
        ["Unidade", "I Unidade"]
      ]
        .map(([l, v]) => `<label class="field">${l}<input class="ro" value="${v}" readonly></label>`)
        .join("")}</div>` +
      `<div class="card gap8"><div class="tbl"><div class="tr g16 th"><span class="cf">Aluno</span><span class="c90">Nota 1</span><span class="c90">Nota 2</span><span class="c90">Média</span><span class="c150">Situação</span></div>${dv}${db.pn
        .map((a, i) => {
          const m = (a[1] + a[2]) / 2;
          return `<div class="tr g16" data-i="${i}"><b class="cf" style="font-weight:500">${
            a[0]
          }</b><input class="ni c90" value="${f1(
            a[1]
          )}" inputmode="decimal" aria-label="Nota 1"><input class="ni c90" value="${f1(
            a[2]
          )}" inputmode="decimal" aria-label="Nota 2"><b class="c90 mm" style="color:${
            m >= 6 ? "var(--green)" : "var(--red)"
          }">${f1(m)}</b><span class="c150 st">${sit(m)}</span></div>${dv}`;
        })
        .join(
          ""
        )}</div></div><p class="sub s13" style="margin:0">Ao salvar, o sistema atualiza o desempenho dos alunos para a análise da coordenação e do professor.</p>`,

    freq: () =>
      head(
        "Frequência",
        "8º A · aula de hoje",
        `<button class="btn" data-savef>Registrar frequência</button>`
      ) +
      card(
        "",
        db.pn
          .map(
            (a) =>
              `<div class="li"><b>${a[0]}</b><label><input type="checkbox" data-f="${
                a[0]
              }" ${db.freq[a[0]] === false ? "" : "checked"}> Presente</label></div>`
          )
          .join(dv),
        "gap8"
      ),

    material: () =>
      head("Materiais", "Publique videoaulas, exercícios e revisões") +
      `<div class="cols">${form(
        "mf",
        "Novo material",
        [
          fld("t", "Título"),
          fld("k", "Tipo", ["Videoaula", "Exercícios", "Revisão"]),
          fld("d", "Disciplina", db.disc)
        ],
        "Publicar"
      )}${card("Publicados", list(db.materiais, ""))}</div>`,

    avisos: () =>
      head("Avisos", "Divulgue avisos relacionados às suas aulas") +
      `<div class="cols">${avisosForm()}${avisosList()}</div>`,

    calendario: () =>
      head("Calendário", "Datas de provas e atividades") +
      `<div class="cols">${form(
        "df",
        "Nova data",
        [fld("t", "Descrição"), fld("d", "Data", "text", "dd/mm")],
        "Adicionar"
      )}${datasCard("Datas marcadas")}</div>`,

    desempenho: () =>
      head("Desempenho", "Média por aluno · Matemática · 8º A") +
      card(
        "",
        list(
          db.pn.map((a) => {
            const m = (a[1] + a[2]) / 2;
            return [a[0], "", `<span class="rr"><b>${f1(m)}</b>${sit(m)}</span>`];
          }),
          "a"
        ),
        "fl"
      )
  },

  aluno: {
    inicio: () =>
      head("Olá! Veja seu desempenho", "8º A · I Unidade · dados de exemplo") +
      stats([
        ["Média geral", "7,2", "I Unidade"],
        ["Frequência", "94%", "aulas registradas"],
        ["Abaixo da média", "1", "disciplina"]
      ]) +
      `<div class="cols" style="--rc:360px">${notasCard()}<div class="rcol">${matCard()}${datasCard()}</div></div>`,

    notas: () =>
      head("Notas", "Consulte suas notas e situação por disciplina") + notasCard(),

    freq: () =>
      head("Frequência", "Presença por disciplina · 94% no geral") +
      card(
        "",
        list(
          [
            ["Matemática", "", "96%"],
            ["Português", "", "93%"],
            ["Ciências", "", "95%"],
            ["História", "", "92%"],
            ["Geografia", "", "94%"]
          ].map((r) => [r[0], "", `<b>${r[2]}</b>`]),
          "a"
        ),
        "fl"
      ),

    material: () =>
      head("Materiais", "Videoaulas, exercícios e revisões") + matCard(),

    avisos: () =>
      head("Avisos", "Comunicados da coordenação e dos professores") +
      card("", list(db.avisos, "a"), "fl"),

    desempenho: () =>
      head("Desempenho", "Acompanhe sua evolução ao longo da unidade") +
      stats([
        ["Média geral", "7,2", "I Unidade"],
        ["Abaixo da média", "1", "disciplina"]
      ]) +
      notasCard(),

    docs: () =>
      head("Documentos", "Documentos disponibilizados pela secretaria") +
      card(
        "",
        ["Boletim da I Unidade", "Declaração de matrícula", "Histórico escolar"]
          .map(
            (d) =>
              `<div class="li"><b>${d}</b><button class="btn sec" data-rel="${d}">Baixar</button></div>`
          )
          .join(dv),
        "gap8"
      ),

    calendario: () =>
      head("Calendário", "Calendário escolar") + datasCard("Próximas datas")
  }
};

/* controle*/
function loginView() {
  return `<div class="login"><div class="brand"><i></i><b>UniEdu</b><p>Gestão escolar unificada para toda a rede de colégios.</p><small>Cadastro, notas, frequência, videoaulas e acompanhamento pedagógico em um único lugar.</small></div>
<div class="lbox"><form class="card" id="lf"><h1 style="font-size:24px">Acessar o sistema</h1><p class="sub" style="margin:0">Entre com seu login e senha. Você será direcionado ao painel do seu perfil.</p>
<label class="field">Login<input id="lg" autocomplete="username" placeholder="seu.login"></label>
<label class="field">Senha<input id="pw" type="password" autocomplete="current-password" placeholder="••••••••"></label>
<p class="err" id="er">${esc(S.err)}</p><button class="btn">Entrar</button><button type="button" class="link" data-forgot>Esqueci minha senha</button>
<div class="demo"><small>Demonstração: entre como</small><button type="button" class="btn sec" data-demo="admin">Administrador</button><button type="button" class="btn sec" data-demo="prof">Professor</button><button type="button" class="btn sec" data-demo="aluno">Aluno</button></div></form></div></div>`;
}

function render() {
  const app = $("#app");
  if (!app) return;

  if (ROLEP === "login") {
    app.innerHTML = loginView();
    return;
  }

  if (S.user !== ROLEP) {
    location.replace("index.html");
    return;
  }

  const r = ROLEP;
  app.innerHTML = `<div class="shell"><aside class="side"><div class="logo"><i></i>UniEdu</div><div class="role">${
    ROLE[r]
  }</div>${NAV[r]
    .map(
      ([id, label]) =>
        `<a class="nav ${VIEW === id ? "on" : ""}" href="${url(r, id)}">${label}</a>`
    )
    .join("")}<div class="sp"></div><div class="me"><i></i>${
    NAME[r]
  }</div><button class="out" data-out>Sair</button></aside><header class="top"><i></i><b>UniEdu</b><button class="out" data-out>Sair</button></header><main>${V[r][VIEW]()}</main></div>`;
}

const enter = (role) => {
  S.user = role;
  S.err = "";
  save();
  location.href = url(role, NAV[role][0][0]);
};

/*eventos*/
document.addEventListener("click", (e) => {
  const t = e.target.closest("button");
  if (!t) return;
  const d = t.dataset;

  if (d.demo) {
    enter(d.demo);
  } else if ("out" in d) {
    S.user = null;
    save();
    location.href = "index.html";
  } else if (d.tab) {
    S.tab = d.tab;
    save();
    render();
  } else if (d.go) {
    location.href = url(S.user, d.go);
  } else if (d.meet) {
    const r = db.baixo[+d.meet];
    r[3] = r[3] ? 0 : 1;
    render();
    toast(r[3] ? "Reunião de acompanhamento marcada." : "Reunião desmarcada.");
  } else if ("forgot" in d) {
    toast("Procure a secretaria da sua escola para redefinir a senha.");
  } else if (d.rel) {
    toast(d.rel + ": gerado (demonstração).");
  } else if ("save" in d) {
    document.querySelectorAll("[data-i]").forEach((r) => {
      const v = [...r.querySelectorAll(".ni")].map((i) => pf(i.value));
      db.pn[+r.dataset.i][1] = v[0];
      db.pn[+r.dataset.i][2] = v[1];
    });
    render();
    toast("Notas salvas. Desempenho atualizado.");
  } else if ("savef" in d) {
    document.querySelectorAll("[data-f]").forEach(
      (c) => (db.freq[c.dataset.f] = c.checked)
    );
    toast("Frequência registrada.");
  }
});

document.addEventListener("input", (e) => {
  if (!e.target.matches(".ni")) return;
  const r = e.target.closest("tr,.tr");
  const v = [...r.querySelectorAll(".ni")].map((i) => pf(i.value));
  const m = (v[0] + v[1]) / 2;
  const x = r.querySelector(".mm");
  
  x.textContent = f1(m);
  x.style.color = m >= 6 ? "var(--green)" : "var(--red)";
  r.querySelector(".st").innerHTML = sit(m);
});

document.addEventListener("submit", (e) => {
  e.preventDefault();
  const f = e.target;
  const id = f.id;
  const d = new FormData(f);

  if (id === "lf") {
    const l = $("#lg").value.trim().toLowerCase();
    const r = l.startsWith("adm")
      ? "admin"
      : l.startsWith("prof")
      ? "prof"
      : l.startsWith("alun")
      ? "aluno"
      : null;

    if (!r) {
      S.err = "Login não reconhecido. Use admin, professor ou aluno.";
      $("#er").textContent = S.err;
      return;
    }
    enter(r);
  } else if (id === "cf") {
    const n = String(d.get("f0")).trim();
    const L = db.nomes[S.tab];

    if (L.some((x) => x.toLowerCase() === n.toLowerCase())) {
      toast("Cadastro recusado: já existe um registro com esse nome.");
      return;
    }
    L.push(n);
    f.reset();
    toast(S.tab + " cadastrado(a) com sucesso.");
  } else if (id === "af") {
    db.avisos.unshift([d.get("t"), "Hoje · " + d.get("m")]);
    render();
    toast("Aviso publicado.");
  } else if (id === "mf") {
    db.materiais.unshift([d.get("k") + ": " + d.get("t"), d.get("d")]);
    render();
    toast("Material publicado.");
  } else if (id === "df") {
    db.datas.push([d.get("t"), d.get("d")]);
    render();
    toast("Data adicionada ao calendário.");
  } else if (id === "vf") {
    db.vinc.unshift([d.get("p"), d.get("t") + " · " + d.get("d")]);
    render();
    toast("Professor vinculado.");
  }
});

["click", "submit"].forEach((ev) =>
  document.addEventListener(ev, () => setTimeout(save))
);
addEventListener("pagehide", save);

render();