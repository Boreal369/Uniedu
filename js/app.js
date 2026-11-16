const API=(window.UNIEDU_API||"http://localhost:8080")+"/api";
const MEDIA_APROVACAO=6;                     
const LIMITE_ABAIXO=3;                       
const ANO_MINIMO=2000;                       
const TIPOS_MATERIAL=["Videoaula","Exercícios","Revisão"];
const TIPOS_DOCUMENTO=["Histórico escolar","Boletim","Declaração","Certificado","Comprovante de matrícula"];
const DESTINOS=[["TODOS","Todos"],["ALUNOS","Alunos"],["PROFESSORES","Professores"]];

const $=s=>document.querySelector(s);
const $$=s=>[...document.querySelectorAll(s)];
const esc=s=>String(s??"").replace(/[&<>"]/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;"}[c]));
const num=v=>{const s=String(v??"").trim().replace(",",".");return s===""?NaN:Number(s)};
const nz=v=>{const n=num(v);return isNaN(n)?null:n};             
const tx=v=>{const s=String(v??"").trim();return s===""?null:s}; 
const r2=v=>Math.round(v*100)/100;                               
const f1=n=>(n==null||isNaN(n))?"–":Number(n).toFixed(1).replace(".",",");
const fd=i=>i?String(i).split("-").reverse().join("/"):"";
const fdt=s=>{const a=String(s??"").split("T");return a[0]?fd(a[0])+(a[1]?" "+a[1].slice(0,5):""):""};
const safeUrl=u=>/^https?:\/\//i.test(u||"")?u:"#";
const bd=(c,t)=>`<span class="badge b-${c}">${t}</span>`;
const sit=m=>m>=MEDIA_APROVACAO?bd("green","Acima da média"):bd("red","Abaixo da média");
const dv='<div class="dv"></div>';
const rotDestino=d=>(DESTINOS.find(x=>x[0]===d)||["","—"])[1];
const aces=(...b)=>`<span style="display:flex;gap:8px;align-items:center;justify-content:flex-end;flex-wrap:wrap">${b.filter(Boolean).join("")}</span>`;
const bt=(rot,atr,cls="btn sec")=>`<button type="button" class="${cls}" ${atr}>${rot}</button>`;
function toast(m){const t=$("#toast");t.textContent=m;t.classList.add("show");clearTimeout(toast.h);toast.h=setTimeout(()=>t.classList.remove("show"),3200)}

function confirmar(mensagem,titulo="Confirmar ação"){
 return new Promise(resolve=>{
  let modal=$("#confirm-modal");
  if(!modal){
   const style=document.createElement("style");style.id="confirm-modal-style";style.textContent=`
    #confirm-modal{position:fixed;inset:0;z-index:10000;display:flex;align-items:center;justify-content:center;padding:20px;background:rgba(8,12,20,.68);backdrop-filter:blur(3px)}
    #confirm-modal[hidden]{display:none}
    #confirm-modal .confirm-box{width:min(430px,100%);background:var(--card,#fff);color:var(--text,#172033);border:1px solid var(--line,#dfe5ee);border-radius:16px;box-shadow:0 24px 70px rgba(0,0,0,.28);padding:24px;animation:confirm-in .16s ease-out}
    #confirm-modal .confirm-title{display:flex;align-items:center;gap:10px;margin:0 0 10px;font-size:20px;line-height:1.2}
    #confirm-modal .confirm-title::before{content:"!";display:inline-grid;place-items:center;width:28px;height:28px;border-radius:50%;background:var(--amber,#f4b740);color:#fff;font-weight:800;font-size:16px}
    #confirm-modal .confirm-message{margin:0;color:var(--mute,#667085);font-size:14px;line-height:1.55;white-space:pre-line}
    #confirm-modal .confirm-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:22px}
    #confirm-modal .confirm-actions button{min-width:96px;border:1px solid var(--line,#dfe5ee);border-radius:9px;padding:10px 16px;font:inherit;font-weight:700;cursor:pointer;background:transparent;color:inherit}
    #confirm-modal .confirm-actions .confirm-ok{border-color:var(--red,#db4b5b);background:var(--red,#db4b5b);color:#fff}
    #confirm-modal .confirm-actions button:focus-visible{outline:3px solid rgba(64,130,255,.35);outline-offset:2px}
    @keyframes confirm-in{from{opacity:0;transform:translateY(6px) scale(.98)}to{opacity:1;transform:none}}
   `;document.head.appendChild(style);
   modal=document.createElement("div");modal.id="confirm-modal";modal.hidden=true;modal.innerHTML=`<div class="confirm-box" role="dialog" aria-modal="true" aria-labelledby="confirm-title"><h2 id="confirm-title" class="confirm-title"></h2><p class="confirm-message"></p><div class="confirm-actions"><button type="button" class="confirm-cancel">Cancelar</button><button type="button" class="confirm-ok">Confirmar</button></div></div>`;document.body.appendChild(modal);
  }
  const title=modal.querySelector(".confirm-title"),message=modal.querySelector(".confirm-message"),ok=modal.querySelector(".confirm-ok"),cancel=modal.querySelector(".confirm-cancel");
  title.textContent=titulo;message.textContent=mensagem;modal.hidden=false;document.body.style.overflow="hidden";ok.focus();
  let done=false;const finish=value=>{if(done)return;done=true;modal.hidden=true;document.body.style.overflow="";ok.removeEventListener("click",yes);cancel.removeEventListener("click",no);modal.removeEventListener("click",outside);document.removeEventListener("keydown",escape);resolve(value)};
  const yes=()=>finish(true),no=()=>finish(false),outside=e=>{if(e.target===modal)no()},escape=e=>{if(e.key==="Escape")no()};
  ok.addEventListener("click",yes);cancel.addEventListener("click",no);modal.addEventListener("click",outside);document.addEventListener("keydown",escape);
 });
}

const qs=o=>{const p=Object.entries(o||{}).filter(([,v])=>v!==null&&v!==undefined&&v!=="").map(([k,v])=>encodeURIComponent(k)+"="+encodeURIComponent(v));return p.length?"?"+p.join("&"):""};

async function api(path,opt={}){
 let res;
 try{res=await fetch(API+path,{headers:{"Content-Type":"application/json"},...opt,body:opt.body?JSON.stringify(opt.body):undefined})}
 catch(e){throw new Error("Não foi possível falar com o servidor. A aplicação Spring está rodando?")}
 let data=null;const txt=await res.text();
 if(txt){try{data=JSON.parse(txt)}catch(e){data=txt}}
 if(!res.ok){
  if(data&&data.erro)throw new Error(data.erro);                      
  if(data&&data.error)throw new Error(data.error+" ("+res.status+")");  
  throw new Error("Erro "+res.status);
 }
 return data;                                  
}

const read=(k,d)=>{try{return JSON.parse(sessionStorage.getItem(k))??d}catch(e){return d}};
const U=read("uniedu_user",null);
const PF=Object.assign({tab:"Aluno",unidade:1,turma:null,disc:null,data:new Date().toISOString().slice(0,10),destino:"TODOS",avTurma:"",docAluno:"",edit:null},read("uniedu_prefs",{}));
const savePF=()=>sessionStorage.setItem("uniedu_prefs",JSON.stringify(PF));
const ROLE_OF={ADMIN:"admin",PROFESSOR:"prof",ALUNO:"aluno"};
const NAV={
 admin:[["inicio","Início"],["cadastros","Cadastros"],["turmas","Turmas e disciplinas"],["vincular","Vincular professores"],["avisos","Avisos"],["desempenho","Desempenho"],["baixo","Baixo rendimento"],["relatorios","Relatórios"]],
 prof:[["inicio","Início"],["turmas","Minhas turmas"],["notas","Lançar notas"],["freq","Frequência"],["material","Materiais"],["avisos","Avisos"],["calendario","Calendário"],["desempenho","Desempenho"]],
 aluno:[["inicio","Início"],["notas","Notas"],["freq","Frequência"],["material","Materiais"],["avisos","Avisos"],["desempenho","Desempenho"],["docs","Documentos"],["calendario","Calendário"]]};
const ROLE={admin:"Administrador",prof:"Professor",aluno:"Aluno"};
const SLUG={baixo:"baixo-rendimento",freq:"frequencia",material:"materiais",docs:"documentos"},PFX={admin:"admin",prof:"professor",aluno:"aluno"};
const url=(r,v)=>PFX[r]+"-"+(SLUG[v]||v)+".html";
const PAGE=document.body.dataset,ROLEP=PAGE.role,VIEW=PAGE.view;
const TABS=["Aluno","Professor","Turma","Disciplina","Documento"];
const tabAtual=()=>TABS.includes(PF.tab)?PF.tab:"Aluno";

const head=(t,s,x="")=>`<div class="head"><div><h1>${t}</h1>${s?`<p class="sub">${s}</p>`:""}</div>${x}</div>`;
const stats=a=>`<div class="stats">${a.map(s=>`<div class="card stat"><small>${s[0]}</small><b${s[3]?' style="color:var(--red)"':""}>${s[1]}</b><span>${s[2]}</span></div>`).join("")}</div>`;
const card=(h,b,c="")=>`<div class="card ${c}">${h?`<h2>${h}</h2>`:""}${b}</div>`;
const vazio=t=>`<p class="empty">${t}</p>`;
const list=(rows,mode="")=>rows.map(r=>`${mode==="b"?dv:""}<div class="li${mode?"":" nd"}"><span><b>${esc(r[0])}</b>${r[1]?`<small>${esc(r[1])}</small>`:""}</span>${r[2]||""}</div>${mode==="a"?dv:""}`).join("");
const opts=(o,cur)=>o.map(([v,l])=>`<option value="${esc(v)}"${String(v)===String(cur)?" selected":""}>${esc(l)}</option>`).join("");
const sel=(pref,label,o,cur)=>`<label class="field">${label}<select data-pref="${pref}">${opts(o,cur)}</select></label>`;
const selF=(name,label,o,cur,opc=false)=>`<label class="field">${label}<select data-name="${name}" name="${name}"${opc?"":" required"}>${opts(o,cur)}</select></label>`;
const fld=(n,l,t="text",ph="",v="",req=true,max=0)=>t==="area"
 ?`<label class="field">${l}<textarea name="${n}" rows="3"${max?` maxlength="${max}"`:""}${req?" required":""}>${esc(v)}</textarea></label>`
 :`<label class="field">${l}<input name="${n}" data-name="${n}" type="${t}"${ph?` placeholder="${esc(ph)}"`:""}${v!==""&&v!=null?` value="${esc(v)}"`:""}${max?` maxlength="${max}"`:""}${req?" required":""}></label>`;
const form=(id,h,fs,b)=>`<form class="card" id="${id}"><h2>${h}</h2>${fs.join("")}<div><button class="btn">${b}</button></div></form>`;
const acoesForm=(edit,rot)=>`<div class="actions"><span></span>${edit?bt("Cancelar","data-cancel"):'<button class="btn sec" type="reset">Cancelar</button>'}<button class="btn">${rot}</button></div>`;
const unidSel=()=>sel("unidade","Unidade",[1,2,3,4].map(i=>[i,i+"ª unidade"]),PF.unidade);
const rr=v=>`<span class="rr"><b style="color:${v<MEDIA_APROVACAO?"var(--red)":"var(--text)"}">${f1(v)}</b>${sit(v)}</span>`;
const semVinculo=()=>card("",vazio("Você ainda não está vinculado a nenhuma turma. Peça ao administrador para vincular você em “Vincular professores”."));
const semAluno=()=>card("",vazio("Nenhum aluno está vinculado a este usuário. Procure a secretaria."));
const avBadges=a=>aces(a.destino&&bd("amber",rotDestino(a.destino)),a.nomeTurma&&bd("green",a.nomeTurma));
const avisosLista=av=>av.length?list(av.map(a=>[a.titulo,fd(a.dataPublicacao)+" · "+a.autor+" — "+a.mensagem,avBadges(a)]),"a"):vazio("Nenhum aviso publicado.");
const avisosCard=av=>card("Publicados",avisosLista(av),"fl");

const avisosQS=()=>ROLEP==="aluno"?qs({perfil:"ALUNO",codTurma:U&&U.codTurma})
 :ROLEP==="prof"?qs({perfil:"PROFESSOR",codUsuario:U&&U.codUsuario})
 :qs({perfil:"ADMIN"});
const porProf=p=>(U&&U.codProf)?api(p+qs({codProf:U.codProf})):Promise.resolve([]);
const porTurma=p=>(U&&U.codTurma)?api(p+qs({codTurma:U.codTurma})):Promise.resolve([]);

function lowTable(low){
 if(!low.length)return vazio(`Nenhum aluno com mais de ${LIMITE_ABAIXO} disciplinas abaixo da média nesta unidade.`);
 return `<div class="tbl"><div class="tr th"><span class="c1">Aluno</span><span class="c2">Turma</span><span class="cf">Disciplinas abaixo da média</span></div>${dv}${low.map(r=>`<div class="tr"><b class="c1" style="font-weight:500">${esc(r.nome)}</b><span class="c2">${esc(r.turma)}</span><span class="cf">${bd("red",r.disciplinasAbaixo+" disciplinas")}</span></div>${dv}`).join("")}</div>`;
}
function eventosList(ev,comTurma){
 if(!ev.length)return vazio("Nenhuma data marcada.");
 return ev.map(e=>`<div class="li nd"><b>${esc(e.descricao)}${comTurma&&e.nomeTurma?`<small>${esc(e.nomeTurma)}${e.nomeProfessor?" · "+esc(e.nomeProfessor):""}</small>`:""}</b>${bd("amber",fd(e.dataEvento))}</div>`).join("");
}
function materiaisList(m){
 if(!m.length)return vazio("Nenhum material publicado.");
 return list(m.map(x=>[x.titulo,[x.nomeDisciplina,x.tipo,x.nomeProfessor].filter(Boolean).join(" · "),
  aces(x.dataPostagem&&bd("amber",fd(x.dataPostagem)),`<a class="lk" href="${esc(safeUrl(x.urlConteudo))}" target="_blank" rel="noopener">Abrir</a>`)]),"");
}

async function seletor(comUnidade,comData){
 const v=(U&&U.codProf)?await api("/vinculos"+qs({codProf:U.codProf})):[];
 if(!v.length)return {vazio:true,v};
 const turmas=[...new Map(v.map(x=>[x.codTurma,x.nomeTurma])).entries()];
 const t=turmas.some(x=>String(x[0])===String(PF.turma))?PF.turma:turmas[0][0];
 const discs=v.filter(x=>x.codTurma==t).map(x=>[x.codDisciplina,x.nomeDisciplina]);
 const d=discs.some(x=>String(x[0])===String(PF.disc))?PF.disc:discs[0][0];
 PF.turma=t;PF.disc=d;savePF();
 let h=sel("turma","Turma",turmas,t)+sel("disc","Disciplina",discs,d);
 if(comUnidade)h+=unidSel();
 if(comData)h+=`<label class="field">Data da aula<input type="date" data-pref="data" value="${PF.data}"></label>`;
 return {t,d,u:PF.unidade,v,html:`<div class="grid3">${h}</div>`};
}

const V={admin:{
 inicio:async()=>{const u=PF.unidade;
  const [res,low,av]=await Promise.all([api("/desempenho/resumo"+qs({unidade:u})),api("/desempenho/baixo-rendimento"+qs({unidade:u})),api("/avisos"+avisosQS())]);
  return head("Painel da coordenação","Visão geral da rede de colégios",unidSel())+
   stats([["Alunos",res.alunos,"cadastrados"],["Professores",res.professores,"cadastrados"],["Turmas",res.turmas,"cadastradas"],["Baixo rendimento",res.baixoRendimento,"alunos na "+u+"ª unidade",1]])+
   `<div class="cols"><div class="card gap8"><h2>Alunos com baixo rendimento</h2>${lowTable(low)}</div>${card("Avisos da coordenação",(av.length?list(av.slice(0,3).map(a=>[a.titulo,fd(a.dataPublicacao)+" · "+a.autor,avBadges(a)]),"a"):vazio("Nenhum aviso."))+`<div class="sp"></div><button type="button" class="btn" data-go="avisos">Publicar aviso</button>`,"fl")}</div>`;
 },
 cadastros:async()=>{
  const [turmas,discs,ualunos,uprofs,alunos]=await Promise.all([api("/turmas"),api("/disciplinas"),api("/usuarios/alunos"),api("/usuarios/professores"),api("/alunos")]);
  const tab=tabAtual(),edit=PF.edit&&PF.edit.t===tab?PF.edit:null;
  let corpo,lista;
  if(tab==="Aluno"){
   const r=edit&&ualunos.find(x=>x.codAluno===edit.cod);
   corpo=!turmas.length?card("",vazio("Cadastre uma turma primeiro (aba “Turma”) para poder cadastrar alunos.")):
    `<form class="card" id="cf" style="gap:20px"><h2>${r?"Editar aluno":"Novo aluno"}</h2><div class="grid2" style="row-gap:20px">${[
     fld("nome","Nome completo","text","Nome do aluno",r?r.nome:""),
     fld("matricula","Matrícula","number","000000",r?String(r.matricula):""),
     fld("email","E-mail","email","aluno@escola.com.br",r?r.email:""),
     selF("codTurma","Turma",turmas.map(t=>[t.codTurma,t.nomeTurma+" ("+t.anoLetivo+")"]),r?r.codTurma:""),
     fld("login","Login","text","nome.sobrenome",r?r.login:""),
     fld("senha",r?"Nova senha (opcional)":"Senha provisória","password","••••••••","",!r)
    ].join("")}</div>${dv}${acoesForm(!!r,r?"Salvar alterações":"Salvar cadastro")}</form>`;
   lista=card("Alunos cadastrados",ualunos.length?list(ualunos.map(x=>[x.nome,"matr. "+x.matricula+" · "+x.email+" · "+x.login+" · "+x.nomeTurma,
     aces(x.mediaGeral!=null&&bd(x.mediaGeral>=MEDIA_APROVACAO?"green":"red","Média "+f1(x.mediaGeral)),
      x.frequencia!=null&&bd("amber","Freq "+f1(x.frequencia)+"%"),
      x.notasAbaixo>0&&bd("red",x.notasAbaixo+" abaixo"),
      x.ativo?bd("green","Ativo"):bd("red","Inativo"),
      bt("Editar","data-edit=\"Aluno:"+x.codAluno+"\""),
      bt(x.ativo?"Desativar":"Ativar","data-ativo=\""+x.codUsuario+":"+(x.ativo?0:1)+"\""))]),"a"):vazio("Nenhum aluno cadastrado."),"fl");
  }else if(tab==="Professor"){
   const r=edit&&uprofs.find(x=>x.codProf===edit.cod);
   corpo=`<form class="card" id="cf" style="gap:20px"><h2>${r?"Editar professor":"Novo professor"}</h2><div class="grid2" style="row-gap:20px">${[
     fld("nome","Nome completo","text","Nome do professor",r?r.nome:""),
     fld("email","E-mail","email","professor@escola.com.br",r?r.email:""),
     fld("login","Login","text","nome.sobrenome",r?r.login:""),
     fld("senha",r?"Nova senha (opcional)":"Senha provisória","password","••••••••","",!r)
    ].join("")}</div>${dv}${acoesForm(!!r,r?"Salvar alterações":"Salvar cadastro")}</form>`;
   lista=card("Professores cadastrados",uprofs.length?list(uprofs.map(x=>[x.nome,x.email+" · "+x.login+" · "+((x.vinculos||[]).length?x.vinculos.join(" | "):"sem vínculo"),
     aces(x.ativo?bd("green","Ativo"):bd("red","Inativo"),
      bt("Editar","data-edit=\"Professor:"+x.codProf+"\""),
      bt(x.ativo?"Desativar":"Ativar","data-ativo=\""+x.codUsuario+":"+(x.ativo?0:1)+"\""))]),"a"):vazio("Nenhum professor cadastrado."),"fl");
  }else if(tab==="Turma"){
   const r=edit&&turmas.find(x=>x.codTurma===edit.cod);
   corpo=`<form class="card" id="cf"><h2>${r?"Editar turma":"Nova turma"}</h2>${
     fld("nomeTurma","Nome da turma","text","8º A",r?r.nomeTurma:"",true,100)}${
     fld("anoLetivo","Ano letivo","number","2026",r?String(r.anoLetivo):"")}${
     acoesForm(!!r,r?"Salvar alterações":"Salvar cadastro")}</form>`;
   lista=card("Turmas cadastradas",turmas.length?list(turmas.map(x=>[x.nomeTurma,"Ano letivo "+x.anoLetivo,aces(bt("Editar","data-edit=\"Turma:"+x.codTurma+"\""),bt("Excluir","data-del=\"Turma:"+x.codTurma+"\""))]),"a"):vazio("Nenhuma turma."),"fl");
  }else if(tab==="Disciplina"){
   const r=edit&&discs.find(x=>x.codDisciplina===edit.cod);
   corpo=`<form class="card" id="cf"><h2>${r?"Editar disciplina":"Nova disciplina"}</h2>${
     fld("nome","Nome da disciplina","text","Matemática",r?r.nome:"",true,100)}${
     acoesForm(!!r,r?"Salvar alterações":"Salvar cadastro")}</form>`;
   lista=card("Disciplinas cadastradas",discs.length?list(discs.map(x=>[x.nome,"",aces(bt("Editar","data-edit=\"Disciplina:"+x.codDisciplina+"\""),bt("Excluir","data-del=\"Disciplina:"+x.codDisciplina+"\""))]),"a"):vazio("Nenhuma disciplina."),"fl");
  }else{
   const docs=/^\d+$/.test(String(PF.docAluno))?await api("/alunos/"+PF.docAluno+"/documentos"):[];
   corpo=alunos.length?form("df2","Enviar documento ao aluno",[
     selF("codAluno","Aluno",alunos.map(a=>[a.codAluno,a.nome+" · matr. "+a.matricula+" · "+a.nomeTurma])),
     fld("titulo","Título","text","Histórico escolar 2026","",true,100),
     selF("tipo","Tipo",TIPOS_DOCUMENTO.map(x=>[x,x])),
     fld("urlArquivo","Link do arquivo","url","https://...")
    ],"Enviar documento"):card("Enviar documento ao aluno",vazio("Cadastre um aluno primeiro (aba “Aluno”)."));
   lista=card("Documentos do aluno",
     `<label class="field">Consultar aluno<select data-pref="docAluno">${opts([["","— escolha um aluno —"]].concat(ualunos.map(x=>[x.codAluno,x.nome+" · "+x.nomeTurma])),PF.docAluno)}</select></label>`+
     (/^\d+$/.test(String(PF.docAluno))
      ?(docs.length?dv+list(docs.map(x=>[x.titulo,x.tipo+" · enviado em "+fd(x.dataEnvio),`<a class="lk" href="${esc(safeUrl(x.urlArquivo))}" target="_blank" rel="noopener">Baixar</a>`]),"a"):vazio("Nenhum documento para este aluno."))
      :vazio("Escolha um aluno para ver os documentos enviados.")),"fl");
  }
  return head("Cadastrar "+(tab==="Documento"?"documentos":tab.toLowerCase()),
   "Preencha os dados. Dados duplicados ou inválidos são recusados antes de salvar."+(edit?" Editando: deixe a senha em branco para manter a atual.":""))+
   `<div class="tabs">${TABS.map(t=>`<button class="tab ${t===tab?"on":""}" data-tab="${t}">${t}</button>`).join("")}</div>`+
   `<div class="cols">${corpo}${lista}</div>`;
 },
 turmas:async()=>{const [t,d]=await Promise.all([api("/turmas"),api("/disciplinas")]);
  return head("Turmas e disciplinas","Estrutura escolar cadastrada")+
   `<div class="grid2">${card("Turmas",t.length?list(t.map(x=>[x.nomeTurma,"Ano letivo "+x.anoLetivo,aces(bt("Editar","data-goedit=\"Turma:"+x.codTurma+"\""),bt("Excluir","data-del=\"Turma:"+x.codTurma+"\""))]),"a"):vazio("Nenhuma turma."),"fl")}${
    card("Disciplinas",d.length?list(d.map(x=>[x.nome,"",aces(bt("Editar","data-goedit=\"Disciplina:"+x.codDisciplina+"\""),bt("Excluir","data-del=\"Disciplina:"+x.codDisciplina+"\""))]),"a"):vazio("Nenhuma disciplina."),"fl")}</div>`+
   card("",vazio("Turma com alunos ou disciplinas vinculadas não pode ser excluída, nem disciplina em uso (notas, frequência ou materiais)."),"fl");
 },
 vincular:async()=>{const [p,t,d,v]=await Promise.all([api("/usuarios/professores"),api("/turmas"),api("/disciplinas"),api("/vinculos")]);
  const ok=p.length&&t.length&&d.length;
  return head("Vincular professores","Associe cada professor a uma turma e disciplina (um professor por disciplina em cada turma)")+
   `<div class="cols">${ok?form("vf","Novo vínculo",[
     selF("codProf","Professor",p.map(x=>[x.codProf,x.nome+(x.ativo?"":" (inativo)")+" · "+x.login])),
     selF("codTurma","Turma",t.map(x=>[x.codTurma,x.nomeTurma+" ("+x.anoLetivo+")"])),
     selF("codDisciplina","Disciplina",d.map(x=>[x.codDisciplina,x.nome]))],"Vincular")
    :card("Novo vínculo",vazio("Cadastre ao menos um professor, uma turma e uma disciplina antes."))}${
    card("Vínculos",v.length?list(v.map(x=>[x.nomeProfessor,x.nomeTurma+" · "+x.qtdAlunos+" aluno(s) em "+x.nomeDisciplina,aces(bd("amber",x.nomeTurma),bd("green",x.nomeDisciplina))]),"a"):vazio("Nenhum vínculo ainda."),"fl")}</div>`;
 },
 avisos:async()=>{const [av,t]=await Promise.all([api("/avisos"+avisosQS()),api("/turmas")]);
  const off=PF.destino==="PROFESSORES";
  return head("Avisos","Escolha quem recebe o comunicado: toda a rede, os alunos ou os professores")+
   `<div class="cols">${form("af","Publicar aviso",[
     fld("titulo","Título","text","Comunicado da coordenação","",true,100),
     fld("mensagem","Mensagem","area","",null,null,true),
     selF("destino","Destino",DESTINOS,PF.destino),
     `<label class="field"${off?' style="opacity:.45"':""}>Turma alcançada<select data-name="codTurma" name="codTurma"${off?" disabled":""}>${opts([["","Todas as turmas"]].concat(t.map(x=>[x.codTurma,x.nomeTurma])),PF.avTurma)}</select></label>`
    ],"Publicar aviso")}${avisosCard(av)}</div>`;
 },
 desempenho:async()=>{const t=await api("/desempenho/turmas");
  return head("Desempenho","Média das notas e frequência por turma")+card("",t.length?list(t.map(x=>[x.nomeTurma,"Média "+f1(x.media)+" · Frequência "+(x.frequencia==null?"–":f1(x.frequencia)+"%"),x.media==null?bd("amber","Sem notas"):x.media>=MEDIA_APROVACAO?bd("green","Regular"):bd("red","Atenção")]),"a"):vazio("Nenhuma turma cadastrada."),"fl");
 },
 baixo:async()=>{const low=await api("/desempenho/baixo-rendimento"+qs({unidade:PF.unidade}));
  return head("Baixo rendimento",`Alunos com notas abaixo de ${f1(MEDIA_APROVACAO)} em mais de ${LIMITE_ABAIXO} disciplinas`,unidSel())+`<div class="card gap8">${lowTable(low)}</div>`;
 },
 relatorios:async()=>{const bk=await api("/backup");
  return head("Relatórios","Gere relatórios em CSV (abrem no Excel) e controle o backup do banco")+
   `<div class="cols">${card("",[["Desempenho por turma","turmas"],["Baixo rendimento da unidade","baixo"],["Lista de alunos com média, frequência e situação","alunos"],["Lista de professores com vínculos","professores"]].map(r=>`<div class="li"><b>${r[0]}</b>${bt("Gerar","data-rel=\""+r[1]+"\"")}</div>`).join(dv),"gap8")}${
    card("Backups do banco",`<div style="display:flex;gap:10px;flex-wrap:wrap;align-items:center">${bt("Gerar backup agora","data-backup","btn")}<small class="sub">Rotina automática diária às 02:00. Aqui você gera um backup na hora.</small></div>`+
     (bk.length?dv+list(bk.slice(0,10).map(x=>[x.nome,x.tamanhoKb+" KB · "+fdt(x.data),""]),"a"):""),"fl")}</div>`;
 }
},prof:{
 inicio:async()=>{if(!(U&&U.codProf))return head("Painel do professor","Resumo das suas turmas")+semVinculo();
  const [v,ev,m]=await Promise.all([porProf("/vinculos"),porProf("/eventos"),porProf("/materiais")]);
  return head("Painel do professor","Resumo das suas turmas")+stats([["Minhas turmas",v.length,"vínculos turma/disciplina"],["Datas marcadas",ev.length,"no calendário"],["Materiais",m.length,"publicados"]])+card("Próximas datas",eventosList(ev.slice(0,5),true));
 },
 turmas:async()=>{const v=await porProf("/vinculos");
  return head("Minhas turmas","Turmas e disciplinas vinculadas a você")+(v.length?card("",list(v.map(x=>[x.nomeTurma,x.nomeDisciplina+" · "+x.qtdAlunos+" aluno(s)",bd("amber",x.nomeTurma)]),"a"),"fl"):semVinculo());
 },
 notas:async()=>{const s=await seletor(true,false);
  const h=head("Lançar notas","Nota 1 e nota 2 geram a média da unidade (0 a 10). Deixe as duas em branco para manter a nota já lançada.",s.vazio?"":bt("Salvar notas","data-save","btn"));
  if(s.vazio)return h+semVinculo();
  const [alunos,notas]=await Promise.all([api("/alunos"+qs({codTurma:s.t})),api("/notas"+qs({codTurma:s.t,codDisciplina:s.d,unidade:s.u}))]);
  const nm=new Map(notas.map(n=>[n.codAluno,n.valor]));
  const linhas=alunos.map(a=>{const m=nm.get(a.codAluno),tem=m!=null;
   return `<div class="tr g16" data-aluno="${a.codAluno}" data-disc="${s.d}" data-un="${s.u}" data-atual="${tem?m:""}" data-novo="${tem?0:1}"><b class="cf" style="font-weight:500">${esc(a.nome)}</b><input class="ni c90" placeholder="${tem?"repor":"0 a 10"}" inputmode="decimal" aria-label="Nota 1"><input class="ni c90" placeholder="${tem?"repor":"0 a 10"}" inputmode="decimal" aria-label="Nota 2"><b class="c90 mm" style="color:${tem?(m>=MEDIA_APROVACAO?"var(--green)":"var(--red)"):"var(--mute)"}">${tem?f1(m):"–"}</b><span class="c150 st">${tem?sit(m):""}</span></div>${dv}`}).join("");
  return h+s.html+`<div class="card gap8">${alunos.length?`<div class="tbl"><div class="tr g16 th"><span class="cf">Aluno</span><span class="c90">Nota 1</span><span class="c90">Nota 2</span><span class="c90">Média</span><span class="c150">Situação</span></div>${dv}${linhas}</div>`:vazio("Nenhum aluno nessa turma.")}</div>`;
 },
 freq:async()=>{const s=await seletor(false,true);
  const h=head("Frequência","Marque a presença dos alunos na aula. Reenviar a mesma data substitui o registro do dia.",s.vazio?"":bt("Registrar frequência","data-savef","btn"));
  if(s.vazio)return h+semVinculo();
  const alunos=await api("/alunos"+qs({codTurma:s.t}));
  return h+s.html+card("",alunos.length?alunos.map(a=>`<div class="li"><b>${esc(a.nome)}</b><label><input type="checkbox" data-f="${a.codAluno}" checked> Presente</label></div>`).join(dv):vazio("Nenhum aluno nessa turma."),"gap8");
 },
 material:async()=>{if(!(U&&U.codProf))return head("Materiais","Publique videoaulas, exercícios e revisões")+semVinculo();
  const [v,m]=await Promise.all([porProf("/vinculos"),porProf("/materiais")]);
  const discs=[...new Map(v.map(x=>[x.codDisciplina,x.nomeDisciplina])).entries()];
  return head("Materiais","Publique videoaulas, exercícios e revisões")+
   `<div class="cols">${discs.length?form("mf","Novo material",[
     fld("titulo","Título","text","Lista de exercícios — equações","",true,100),
     selF("tipo","Tipo",TIPOS_MATERIAL.map(x=>[x,x])),
     selF("codDisciplina","Disciplina",discs),
     fld("urlConteudo","Link do conteúdo","url","https://...")],"Publicar")
    :card("Novo material",vazio("Você precisa estar vinculado a uma disciplina."))}${card("Publicados",materiaisList(m))}</div>`;
 },
 avisos:async()=>{const [av,v]=await Promise.all([api("/avisos"+avisosQS()),porProf("/vinculos")]);
  const turmas=[...new Map(v.map(x=>[x.codTurma,x.nomeTurma])).entries()];
  return head("Avisos","Avisos publicados por professor vão para os alunos das suas turmas")+
   `<div class="cols">${form("af","Publicar aviso",[
     fld("titulo","Título","text","Prova de Matemática — 24/11","",true,100),
     fld("mensagem","Mensagem","area","",null,null,true),
     selF("codTurma","Turma alcançada",[["","Todas as minhas turmas"]].concat(turmas),PF.avTurma,true)
    ],"Publicar aviso")}${avisosCard(av)}</div>`;
 },
 calendario:async()=>{if(!(U&&U.codProf))return head("Calendário","Datas de provas e atividades")+semVinculo();
  const [v,ev]=await Promise.all([porProf("/vinculos"),porProf("/eventos")]);
  const turmas=[...new Map(v.map(x=>[x.codTurma,x.nomeTurma])).entries()];
  return head("Calendário","Datas de provas e atividades · até 50 datas por turma")+
   `<div class="cols">${turmas.length?form("df","Nova data",[
     fld("descricao","Descrição","text","Prova 1 — Equações","",true,100),
     fld("dataEvento","Data","date","",new Date().toISOString().slice(0,10)),
     selF("codTurma","Turma",turmas)],"Adicionar"):card("Nova data",vazio("Você precisa estar vinculado a uma turma."))}${card("Datas marcadas",eventosList(ev,true))}</div>`;
 },
 desempenho:async()=>{const s=await seletor(true,false);
  const h=head("Desempenho","Média por aluno na turma e disciplina escolhidas");
  if(s.vazio)return h+semVinculo();
  const notas=await api("/notas"+qs({codTurma:s.t,codDisciplina:s.d,unidade:s.u}));
  return h+s.html+card("",notas.length?list(notas.map(n=>[n.nomeAluno,"nota "+f1(n.valor)+(n.dataLancamento?" · lançada em "+fd(n.dataLancamento):""),rr(n.valor)]),"a"):vazio("Nenhuma nota lançada para essa seleção."),"fl");
 }
},aluno:{
 inicio:async()=>{if(!(U&&U.codAluno))return head("Olá!","Suas notas e materiais")+semAluno();
  const [notas0,fr,mat,ev]=await Promise.all([api(`/alunos/${U.codAluno}/notas`),api(`/alunos/${U.codAluno}/frequencia`),porTurma("/materiais"),porTurma("/eventos")]);
  const notas=notas0.filter(n=>n.unidade==PF.unidade),med=notas.length?notas.reduce((a,n)=>a+Number(n.valor),0)/notas.length:null,ab=notas.filter(n=>Number(n.valor)<MEDIA_APROVACAO).length;
  return head("Olá, "+esc(U.nome)+"! Veja seu desempenho","Notas da "+PF.unidade+"ª unidade",unidSel())+
   stats([["Média geral",f1(med),PF.unidade+"ª unidade"],["Frequência",fr.percentual==null?"–":f1(fr.percentual)+"%","aulas registradas"],["Abaixo da média",ab,ab===1?"disciplina":"disciplinas"]])+
   (ab>LIMITE_ABAIXO?`<div class="card" style="border-color:var(--amber);background:var(--amberbg)"><b>Você terá acompanhamento pedagógico.</b> A escola dará apoio para as disciplinas abaixo da média.</div>`:"")+
   `<div class="cols" style="--rc:360px">${card("Notas por disciplina",notas.length?list(notas.map(n=>[n.nomeDisciplina,"",rr(n.valor)]),"b"):vazio("Nenhuma nota lançada nesta unidade."),"gap8")}<div class="rcol">${card("Materiais de apoio",materiaisList(mat.slice(0,4)))}${card("Próximas datas",eventosList(ev.slice(0,4)))}</div></div>`;
 },
 notas:async()=>{if(!(U&&U.codAluno))return head("Notas","Suas notas por disciplina")+semAluno();
  const n=(await api(`/alunos/${U.codAluno}/notas`)).filter(x=>x.unidade==PF.unidade);
  return head("Notas","Consulte suas notas e situação por disciplina",unidSel())+card("Notas por disciplina",n.length?list(n.map(x=>[x.nomeDisciplina,"unidade "+x.unidade+(x.dataLancamento?" · lançada em "+fd(x.dataLancamento):""),rr(x.valor)]),"b"):vazio("Nenhuma nota lançada nesta unidade."),"gap8");
 },
 freq:async()=>{if(!(U&&U.codAluno))return head("Frequência","Sua presença por disciplina")+semAluno();
  const f=await api(`/alunos/${U.codAluno}/frequencia`);
  return head("Frequência","Presença por disciplina · "+(f.percentual==null?"sem registros":f1(f.percentual)+"% no geral"))+card("",f.porDisciplina.length?list(f.porDisciplina.map(x=>[x.nome,x.presentes+" de "+x.total+" aulas",`<b>${f1(x.percentual)}%</b>`]),"a"):vazio("Nenhuma aula registrada ainda."),"fl");
 },
 material:async()=>{const m=await porTurma("/materiais");return head("Materiais","Videoaulas, exercícios e revisões")+card("Materiais de apoio",materiaisList(m))},
 avisos:async()=>{const av=await api("/avisos"+avisosQS());return head("Avisos","Comunicados da coordenação e dos professores")+card("",avisosLista(av),"fl")},
 desempenho:async()=>{if(!(U&&U.codAluno))return head("Desempenho","Sua evolução na unidade")+semAluno();
  const n=(await api(`/alunos/${U.codAluno}/notas`)).filter(x=>x.unidade==PF.unidade),med=n.length?n.reduce((a,x)=>a+Number(x.valor),0)/n.length:null,ab=n.filter(x=>Number(x.valor)<MEDIA_APROVACAO).length;
  return head("Desempenho","Acompanhe sua evolução ao longo da unidade",unidSel())+stats([["Média geral",f1(med),PF.unidade+"ª unidade"],["Abaixo da média",ab,ab===1?"disciplina":"disciplinas"]])+card("Notas por disciplina",n.length?list(n.map(x=>[x.nomeDisciplina,"",rr(x.valor)]),"b"):vazio("Nenhuma nota lançada nesta unidade."),"gap8");
 },
 docs:async()=>{if(!(U&&U.codAluno))return head("Documentos","Documentos da secretaria")+semAluno();
  const d=await api(`/alunos/${U.codAluno}/documentos`);
  return head("Documentos","Documentos disponibilizados pela secretaria")+card("",d.length?d.map(x=>`<div class="li"><span><b>${esc(x.titulo)}</b><small>${esc(x.tipo)} · enviado em ${fd(x.dataEnvio)}</small></span><a class="lk" href="${esc(safeUrl(x.urlArquivo))}" target="_blank" rel="noopener">Baixar</a></div>`).join(dv):vazio("Nenhum documento disponível."),"gap8");
 },
 calendario:async()=>{const ev=await porTurma("/eventos");return head("Calendário","Calendário escolar")+card("Próximas datas",eventosList(ev))}
}};

function loginView(err=""){return `<div class="login"><div class="brand"><i></i><b>UniEdu</b><p>Gestão escolar unificada para toda a rede de colégios.</p><small>Cadastro, notas, frequência, videoaulas e acompanhamento pedagógico em um único lugar.</small></div>
<div class="lbox"><form class="card" id="lf"><h1 style="font-size:24px">Acessar o sistema</h1><p class="sub" style="margin:0">Entre com seu login e senha. Você será direcionado ao painel do seu perfil.</p>
<label class="field">Login<input id="lg" autocomplete="username" placeholder="seu.login"></label>
<label class="field">Senha<input id="pw" type="password" autocomplete="current-password" placeholder="••••••••"></label>
<p class="err" id="er">${esc(err)}</p><button class="btn">Entrar</button><button type="button" class="link" data-forgot>Esqueci minha senha</button></form></div></div>`}

async function render(){
 const app=$("#app");
 if(ROLEP==="login"){app.innerHTML=loginView();return}
 if(!U||ROLE_OF[U.perfil]!==ROLEP){location.replace("index.html");return}
 const r=ROLEP;
 app.innerHTML=`<div class="shell"><aside class="side"><div class="logo"><i></i>UniEdu</div><div class="role">${ROLE[r]}</div>${NAV[r].map(([id,l])=>`<a class="nav ${VIEW===id?"on":""}" href="${url(r,id)}">${l}</a>`).join("")}<div class="sp"></div><div class="me"><i></i>${esc(U.nome)}</div><button class="out" data-out>Sair</button></aside><header class="top"><i></i><b>UniEdu</b><button class="out" data-out>Sair</button></header><main id="main"><p class="sub">Carregando…</p></main></div>`;
 try{$("#main").innerHTML=await V[r][VIEW]()}
 catch(e){$("#main").innerHTML=card("Não foi possível carregar esta tela",`<p>${esc(e.message)}</p><div><button class="btn" data-retry>Tentar de novo</button></div>`)}
}

function baixar(nome,linhas){
 const csv="\ufeff"+linhas.map(l=>l.map(c=>`"${String(c??"").replace(/"/g,'""')}"`).join(";")).join("\r\n");
 const a=document.createElement("a");a.href=URL.createObjectURL(new Blob([csv],{type:"text/csv;charset=utf-8"}));a.download=nome;document.body.appendChild(a);a.click();a.remove();
}
async function gerarRelatorio(tipo){
 if(tipo==="turmas"){const t=await api("/desempenho/turmas");baixar("desempenho-por-turma.csv",[["Turma","Média","Frequência (%)"],...t.map(x=>[x.nomeTurma,f1(x.media),x.frequencia==null?"":f1(x.frequencia)])])}
 else if(tipo==="baixo"){const l=await api("/desempenho/baixo-rendimento"+qs({unidade:PF.unidade}));baixar("baixo-rendimento-unidade-"+PF.unidade+".csv",[["Aluno","Turma","Disciplinas abaixo da média"],...l.map(x=>[x.nome,x.turma,x.disciplinasAbaixo])])}
 else if(tipo==="professores"){const p=await api("/usuarios/professores");baixar("professores.csv",[["Nome","E-mail","Login","Vínculos","Situação"],...p.map(x=>[x.nome,x.email,x.login,(x.vinculos||[]).join(" | "),x.ativo?"Ativo":"Inativo"])])}
 else{const a=await api("/usuarios/alunos");baixar("alunos.csv",[["Matrícula","Nome","E-mail","Login","Turma","Média geral","Frequência (%)","Notas abaixo da média","Situação"],...a.map(x=>[x.matricula,x.nome,x.email,x.login,x.nomeTurma,f1(x.mediaGeral),x.frequencia==null?"":f1(x.frequencia),x.notasAbaixo,x.ativo?"Ativo":"Inativo"])])}
}

const run=async f=>{try{await f()}catch(e){toast(e.message)}};
const par=s=>String(s).split(":");
const ROTA={Aluno:"/alunos",Professor:"/professores",Turma:"/turmas",Disciplina:"/disciplinas"};
const ROTA_EXCLUIR={Turma:"/turmas/",Disciplina:"/disciplinas/"};

document.addEventListener("click",e=>{
 const t=e.target.closest("button");if(!t)return;const d=t.dataset;
 if("out" in d){sessionStorage.removeItem("uniedu_user");location.href="index.html"}
 else if(d.tab){PF.tab=d.tab;PF.edit=null;savePF();render()}
 else if(d.go){location.href=url(ROLEP,d.go)}
 else if("retry" in d)render();
 else if("forgot" in d)toast("Procure a secretaria da sua escola para redefinir a senha.");
 else if("cancel" in d){PF.edit=null;savePF();render()}
 else if(d.edit){PF.edit={t:tabAtual(),cod:+par(d.edit)[1]};savePF();window.scrollTo(0,0);render()}
 else if(d.goedit){const [ti,c]=par(d.goedit);PF.tab=ti;PF.edit={t:ti,cod:+c};savePF();location.href=url("admin","cadastros")}
 else if(d.ativo){const [cu,v]=par(d.ativo);
  confirmar(v?"Ativar este usuário? Ele voltará a poder entrar no sistema.":"Desativar este usuário? Ele perderá o acesso ao sistema.",v?"Ativar usuário":"Desativar usuário").then(ok=>{if(!ok)return;run(async()=>{await api("/usuarios/"+cu+"/ativo",{method:"PUT",body:{ativo:!!v}});toast(v?"Usuário ativado.":"Usuário desativado.");render()})});
 }
 else if(d.del){const [ti,c]=par(d.del),rota=ROTA_EXCLUIR[ti];if(!rota)return;
  confirmar("Excluir este(a) "+ti.toLowerCase()+"? Só é possível excluir se não houver dados ligados a ele(a).","Excluir "+ti.toLowerCase()).then(ok=>{if(!ok)return;run(async()=>{await api(rota+c,{method:"DELETE"});toast(ti+" excluída.");if(PF.edit&&PF.edit.t===ti)PF.edit=null;render()})});
 }
 else if(d.rel)run(async()=>{await gerarRelatorio(d.rel);toast("Relatório gerado.")});
 else if("backup" in d)run(async()=>{toast("Gerando backup do banco…");const b=await api("/backup",{method:"POST"});toast("Backup "+b.nome+" ("+b.tamanhoKb+" KB) criado.");render()});
 else if("save" in d)run(async()=>{
  let ok=0,erro=null;
  for(const r of document.querySelectorAll("[data-aluno]")){
   const [a,b]=[...r.querySelectorAll(".ni")].map(i=>num(i.value));
   if(isNaN(a)&&isNaN(b))continue;                              // nada digitado: mantém a nota já lançada
   if(isNaN(a)||isNaN(b)||a<0||a>10||b<0||b>10){erro="Preencha as duas notas (de 0 a 10) de cada aluno.";break}
   const body={codAluno:+r.dataset.aluno,codDisciplina:+r.dataset.disc,unidade:+r.dataset.un,valor:r2((a+b)/2)};
   await api("/notas",{method:r.dataset.novo==="1"?"POST":"PUT",body});ok++;   // cria no POST, altera no PUT
  }
  if(erro){toast(erro);return}
  toast(ok?ok+" nota(s) salva(s). Desempenho atualizado.":"Nenhuma nota preenchida.");
  if(ok)render();
 })
 else if("savef" in d)run(async()=>{
  if(!PF.disc){toast("Escolha a turma e a disciplina.");return}
  if(!PF.data){toast("Informe a data da aula.");return}
  const registros=$$("[data-f]").map(c=>({codAluno:+c.dataset.f,presenca:c.checked}));
  if(!registros.length){toast("Nenhum aluno nessa turma.");return}
  const r=await api("/frequencias/lote",{method:"POST",body:{codDisciplina:+PF.disc,data:PF.data,registros}});
  toast(r.registrados+" registro(s) de frequência salvos.");
 });
});

document.addEventListener("change",e=>{
 const s=e.target.closest("[data-name=destino]");            // destino PROFESSORES não usa turma
 if(s){const t=document.querySelector("[data-name=codTurma]");
  if(t){const off=s.value==="PROFESSORES";t.disabled=off;t.closest(".field").style.opacity=off?".45":""}
  PF.destino=s.value;savePF();return}
 const k=e.target.dataset&&e.target.dataset.pref;if(!k)return;
 PF[k]=k==="unidade"?Number(e.target.value):e.target.value;
 if(k==="turma")PF.disc=null;
 savePF();render();
});
document.addEventListener("input",e=>{
 if(!e.target.matches(".ni"))return;
 const r=e.target.closest(".tr"),[a,b]=[...r.querySelectorAll(".ni")].map(i=>num(i.value)),x=r.querySelector(".mm"),s=r.querySelector(".st");
 const atual=r.dataset.atual?num(r.dataset.atual):NaN;
 if(isNaN(a)||isNaN(b)){                                       // volta a mostrar a nota já lançada
  if(isNaN(atual)){x.textContent="–";x.style.color="var(--mute)";s.innerHTML=""}
  else{x.textContent=f1(atual);x.style.color=atual>=MEDIA_APROVACAO?"var(--green)":"var(--red)";s.innerHTML=sit(atual)}
  return}
 const m=r2((a+b)/2);x.textContent=f1(m);x.style.color=m>=MEDIA_APROVACAO?"var(--green)":"var(--red)";s.innerHTML=sit(m);
});
document.addEventListener("submit",e=>{
 e.preventDefault();const f=e.target,id=f.id,d=Object.fromEntries(new FormData(f));
 const fim=msg=>{PF.edit=null;savePF();toast(msg);render()};
 if(id==="lf")return run(async()=>{
  const l=$("#lg").value.trim(),p=$("#pw").value;
  if(!l||!p){$("#er").textContent="Informe login e senha.";return}
  try{const r=await api("/login",{method:"POST",body:{login:l,senha:p}});
   const role=ROLE_OF[r.perfil];if(!role)throw new Error("Perfil desconhecido.");
   sessionStorage.setItem("uniedu_user",JSON.stringify(r));PF.edit=null;savePF();location.href=url(role,NAV[role][0][0]);
  }catch(err){$("#er").textContent=err.message}
 });
 run(async()=>{
  if(id==="cf"){
   const tab=tabAtual(),cod=PF.edit&&PF.edit.t===tab?PF.edit.cod:null,b={};
   if(tab==="Aluno"||tab==="Professor"){
    b.nome=tx(d.nome);b.email=tx(d.email);b.login=tx(d.login);
    if(tx(d.senha))b.senha=tx(d.senha);                        // em branco mantém a senha atual
    if(tab==="Aluno"){b.matricula=nz(d.matricula);b.codTurma=nz(d.codTurma)}
    await api(ROTA[tab]+(cod?"/"+cod:""),{method:cod?"PUT":"POST",body:b});
   }else if(tab==="Turma"){
    b.nomeTurma=tx(d.nomeTurma);b.anoLetivo=nz(d.anoLetivo);
    if(b.anoLetivo!=null&&b.anoLetivo<ANO_MINIMO)throw new Error("Ano letivo inválido.");
    await api(cod?"/turmas/"+cod:"/turmas",{method:cod?"PUT":"POST",body:b});
   }else{
    b.nome=tx(d.nome);
    await api(cod?"/disciplinas/"+cod:"/disciplinas",{method:cod?"PUT":"POST",body:b});
   }
   fim(cod?"Alterações de "+tab.toLowerCase()+" salvas.":tab+" cadastrado(a) com sucesso.");
  }else if(id==="df2"){
   await api("/documentos",{method:"POST",body:{titulo:tx(d.titulo),tipo:tx(d.tipo),urlArquivo:tx(d.urlArquivo),codAluno:nz(d.codAluno)}});
   PF.docAluno=d.codAluno;savePF();fim("Documento enviado ao aluno.");
  }else if(id==="af"){
   const destino=ROLEP==="prof"?"ALUNOS":(tx(d.destino)||"TODOS"),body={titulo:tx(d.titulo),mensagem:tx(d.mensagem),codUsuario:U.codUsuario,destino};
   if(destino!=="PROFESSORES")body.codTurma=nz(d.codTurma);     // turma vale só para TODOS e ALUNOS
   await api("/avisos",{method:"POST",body});
   PF.destino=destino;PF.avTurma="";savePF();fim("Aviso publicado.");
  }
  else if(id==="mf"){await api("/materiais",{method:"POST",body:{titulo:tx(d.titulo),tipo:tx(d.tipo),urlConteudo:tx(d.urlConteudo),codDisciplina:nz(d.codDisciplina),codProf:U.codProf}});fim("Material publicado.")}
  else if(id==="df"){await api("/eventos",{method:"POST",body:{descricao:tx(d.descricao),dataEvento:tx(d.dataEvento),codProf:U.codProf,codTurma:nz(d.codTurma)}});fim("Data adicionada ao calendário.")}
  else if(id==="vf"){await api("/vinculos",{method:"POST",body:{codProf:nz(d.codProf),codTurma:nz(d.codTurma),codDisciplina:nz(d.codDisciplina)}});fim("Professor vinculado.")}
 });
});
render();