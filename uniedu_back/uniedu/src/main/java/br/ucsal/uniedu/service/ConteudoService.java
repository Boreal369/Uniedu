package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.exception.ApiException;
import br.ucsal.uniedu.model.*;
import br.ucsal.uniedu.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConteudoService {

    private static final long LIMITE_EVENTOS_POR_TURMA = 50;

    private final AvisoRepository avisoRepository;
    private final MaterialRepository materialRepository;
    private final EventoRepository eventoRepository;
    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfessorRepository professorRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;

    public ConteudoService(AvisoRepository avisoRepository, MaterialRepository materialRepository,
                           EventoRepository eventoRepository, DocumentoRepository documentoRepository,
                           UsuarioRepository usuarioRepository, ProfessorRepository professorRepository,
                           DisciplinaRepository disciplinaRepository, TurmaRepository turmaRepository,
                           AlunoRepository alunoRepository) {
        this.avisoRepository = avisoRepository;
        this.materialRepository = materialRepository;
        this.eventoRepository = eventoRepository;
        this.documentoRepository = documentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.professorRepository = professorRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
    }

    // RF008
    @Transactional
    public Dtos.AvisoResponse criarAviso(Dtos.AvisoRequest r) {
        if (r == null || Util.vazio(r.titulo()) || Util.vazio(r.mensagem()) || r.codUsuario() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe título e mensagem.");
        }
        Usuario u = usuarioRepository.findById(r.codUsuario())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        String destino = "PROFESSOR".equals(u.getPerfil()) ? "ALUNOS"
                : (Util.vazio(r.destino()) ? "TODOS" : r.destino().trim().toUpperCase());
        if (!List.of("TODOS", "ALUNOS", "PROFESSORES").contains(destino)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Destino inválido.");
        }
        Turma turma = null;
        if (r.codTurma() != null && !"PROFESSORES".equals(destino)) {
            turma = turmaRepository.findById(r.codTurma())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Turma não encontrada."));
        }
        Aviso a = new Aviso();
        a.setTitulo(r.titulo().trim());
        a.setMensagem(r.mensagem().trim());
        a.setUsuario(u);
        a.setDestino(destino);
        a.setTurma(turma);
        avisoRepository.save(a);
        return paraResposta(a);
    }

    // RF008
    @Transactional(readOnly = true)
    public List<Dtos.AvisoResponse> listarAvisos(String perfil, Integer codTurma, Integer codUsuario) {
        return avisoRepository.findAllByOrderByCodAvisoDesc().stream()
                .filter(a -> visivel(a, perfil, codTurma, codUsuario))
                .map(this::paraResposta).toList();
    }

    private boolean visivel(Aviso a, String perfil, Integer codTurma, Integer codUsuario) {
        if (perfil == null || "ADMIN".equals(perfil)) {
            return true;
        }
        if ("ALUNO".equals(perfil)) {
            return !"PROFESSORES".equals(a.getDestino())
                    && (a.getTurma() == null || (codTurma != null && a.getTurma().getCodTurma().equals(codTurma)));
        }
        if ("PROFESSOR".equals(perfil)) {
            return !"ALUNOS".equals(a.getDestino())
                    || (codUsuario != null && a.getUsuario().getCodUsuario().equals(codUsuario));
        }
        return false;
    }

    // RF014
    @Transactional
    public Dtos.MaterialResponse criarMaterial(Dtos.MaterialRequest r) {
        if (r == null || Util.vazio(r.titulo()) || Util.vazio(r.tipo()) || Util.vazio(r.urlConteudo())
                || r.codDisciplina() == null || r.codProf() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preencha todos os campos.");
        }
        Disciplina d = disciplinaRepository.findById(r.codDisciplina())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Disciplina não encontrada."));
        Professor p = professorRepository.findById(r.codProf())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Professor não encontrado."));
        Material m = new Material();
        m.setTitulo(r.titulo().trim());
        m.setTipo(r.tipo().trim());
        m.setUrlConteudo(r.urlConteudo().trim());
        m.setDisciplina(d);
        m.setProfessor(p);
        materialRepository.save(m);
        return paraResposta(m);
    }

    // RF014
    @Transactional(readOnly = true)
    public List<Dtos.MaterialResponse> listarMateriais(Integer codProf, Integer codTurma) {
        List<Material> lista;
        if (codTurma != null) lista = materialRepository.findDaTurma(codTurma);
        else if (codProf != null) lista = materialRepository.findByProfessorCodProfOrderByCodMaterialDesc(codProf);
        else lista = materialRepository.findAllByOrderByCodMaterialDesc();
        return lista.stream().map(this::paraResposta).toList();
    }

    // RF015
    @Transactional
    public Dtos.EventoResponse criarEvento(Dtos.EventoRequest r) {
        if (r == null || Util.vazio(r.descricao()) || r.dataEvento() == null
                || r.codProf() == null || r.codTurma() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preencha descrição, data e turma.");
        }
        Professor p = professorRepository.findById(r.codProf())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Professor não encontrado."));
        Turma t = turmaRepository.findById(r.codTurma())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Turma não encontrada."));
        if (eventoRepository.countByTurmaCodTurma(r.codTurma()) >= LIMITE_EVENTOS_POR_TURMA) {
            throw new ApiException(HttpStatus.CONFLICT, "Limite de " + LIMITE_EVENTOS_POR_TURMA + " datas por turma atingido.");
        }
        Evento e = new Evento();
        e.setDescricao(r.descricao().trim());
        e.setDataEvento(r.dataEvento());
        e.setProfessor(p);
        e.setTurma(t);
        eventoRepository.save(e);
        return paraResposta(e);
    }

    // RF015
    @Transactional(readOnly = true)
    public List<Dtos.EventoResponse> listarEventos(Integer codProf, Integer codTurma) {
        List<Evento> lista;
        if (codTurma != null) lista = eventoRepository.findByTurmaCodTurmaOrderByDataEvento(codTurma);
        else if (codProf != null) lista = eventoRepository.findByProfessorCodProfOrderByDataEvento(codProf);
        else lista = eventoRepository.findAllByOrderByDataEvento();
        return lista.stream().map(this::paraResposta).toList();
    }

    @Transactional
    public Dtos.DocumentoResponse criarDocumento(Dtos.DocumentoRequest r) {
        if (r == null || Util.vazio(r.titulo()) || Util.vazio(r.tipo()) || Util.vazio(r.urlArquivo()) || r.codAluno() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preencha todos os campos.");
        }
        Aluno a = alunoRepository.findById(r.codAluno())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        Documento d = new Documento();
        d.setTitulo(r.titulo().trim());
        d.setTipo(r.tipo().trim());
        d.setUrlArquivo(r.urlArquivo().trim());
        d.setAluno(a);
        documentoRepository.save(d);
        return paraResposta(d);
    }

    @Transactional(readOnly = true)
    public List<Dtos.DocumentoResponse> listarDocumentos(Integer codAluno) {
        return documentoRepository.findByAlunoCodAlunoOrderByCodDocumentoDesc(codAluno)
                .stream().map(this::paraResposta).toList();
    }

    private Dtos.AvisoResponse paraResposta(Aviso a) {
        return new Dtos.AvisoResponse(a.getCodAviso(), a.getTitulo(), a.getMensagem(), a.getDataPublico(),
                a.getUsuario().getNome(), a.getDestino(), a.getTurma() == null ? null : a.getTurma().getNomeTurma());
    }

    private Dtos.MaterialResponse paraResposta(Material m) {
        return new Dtos.MaterialResponse(m.getCodMaterial(), m.getTitulo(), m.getTipo(), m.getUrlConteudo(),
                m.getDataPostagem(), m.getDisciplina().getCodDisciplina(), m.getDisciplina().getNome(),
                m.getProfessor().getUsuario().getNome());
    }

    private Dtos.EventoResponse paraResposta(Evento e) {
        return new Dtos.EventoResponse(e.getCodEvento(), e.getDescricao(), e.getDataEvento(),
                e.getTurma().getCodTurma(), e.getTurma().getNomeTurma(), e.getProfessor().getUsuario().getNome());
    }

    private Dtos.DocumentoResponse paraResposta(Documento d) {
        return new Dtos.DocumentoResponse(d.getCodDocumento(), d.getTitulo(), d.getTipo(), d.getUrlArquivo(), d.getDataEnvio());
    }
}
