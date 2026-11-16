package br.ucsal.uniedu.dto;

public record AlunoRequest(String nome, String email, String login, String senha,
                           Long matricula, Integer codTurma) { }
