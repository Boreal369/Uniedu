package br.ucsal.uniedu.dto;

import java.time.LocalDate;
import java.util.List;

public final class Dtos {

    private Dtos() { }

    public record ProfessorRequest(String nome, String email, String login, String senha) { }
    public record ProfessorResponse(Integer codProf, String nome, String email) { }
    public record ProfessorUpdateRequest(String nome, String email, String login, String senha) { }
    public record AlunoUpdateRequest(String nome, String email, String login, String senha, Long matricula, Integer codTurma) { }
    public record AtivoRequest(Boolean ativo) { }
    public record TurmaRequest(String nomeTurma, Integer anoLetivo) { }
    public record DisciplinaRequest(String nome) { }
    public record VinculoRequest(Integer codTurma, Integer codDisciplina, Integer codProf) { }
    public record VinculoResponse(Integer codTurma, String nomeTurma, Integer codDisciplina,
                                  String nomeDisciplina, Integer codProf, String nomeProfessor, long qtdAlunos) { }

    public record AlunoDetalhe(Integer codAluno, Integer codUsuario, String nome, String email, String login,
                               Long matricula, Integer codTurma, String nomeTurma, Boolean ativo,
                               Double mediaGeral, Double frequencia, long notasAbaixo) { }
    public record ProfessorDetalhe(Integer codProf, Integer codUsuario, String nome, String email, String login,
                                   Boolean ativo, List<String> vinculos) { }

    public record AvisoRequest(String titulo, String mensagem, Integer codUsuario, String destino, Integer codTurma) { }
    public record AvisoResponse(Integer codAviso, String titulo, String mensagem, LocalDate dataPublicacao,
                                String autor, String destino, String nomeTurma) { }
    public record MaterialRequest(String titulo, String tipo, String urlConteudo, Integer codDisciplina, Integer codProf) { }
    public record MaterialResponse(Integer codMaterial, String titulo, String tipo, String urlConteudo,
                                   LocalDate dataPostagem, Integer codDisciplina, String nomeDisciplina, String nomeProfessor) { }
    public record EventoRequest(String descricao, LocalDate dataEvento, Integer codProf, Integer codTurma) { }
    public record EventoResponse(Integer codEvento, String descricao, LocalDate dataEvento,
                                 Integer codTurma, String nomeTurma, String nomeProfessor) { }
    public record DocumentoRequest(String titulo, String tipo, String urlArquivo, Integer codAluno) { }
    public record DocumentoResponse(Integer codDocumento, String titulo, String tipo, String urlArquivo, LocalDate dataEnvio) { }

    public record RegistroFrequencia(Integer codAluno, Boolean presenca) { }
    public record FrequenciaLoteRequest(Integer codDisciplina, LocalDate data, List<RegistroFrequencia> registros) { }
    public record FrequenciaPorDisciplina(Integer codDisciplina, String nome, long total, long presentes, Double percentual) { }
    public record FrequenciaResumo(long total, long presentes, Double percentual, List<FrequenciaPorDisciplina> porDisciplina) { }

    public record TurmaDesempenho(String nomeTurma, Double media, Double frequencia) { }
    public record ResumoAdmin(long alunos, long professores, long turmas, long baixoRendimento) { }

    public record BackupInfo(String nome, long tamanhoKb, String data) { }
}
