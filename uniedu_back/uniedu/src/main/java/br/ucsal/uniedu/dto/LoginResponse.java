package br.ucsal.uniedu.dto;

public record LoginResponse(Integer codUsuario, String nome, String perfil,
                            Integer codAluno, Integer codProf, Integer codTurma) { }
