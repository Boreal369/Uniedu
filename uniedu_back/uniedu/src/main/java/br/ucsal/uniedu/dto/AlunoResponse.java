package br.ucsal.uniedu.dto;

public record AlunoResponse(Integer codAluno, String nome, String email, String login,
                            Long matricula, Integer codTurma, String nomeTurma) { }
