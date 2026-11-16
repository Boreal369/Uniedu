package br.ucsal.uniedu.dto;

import java.math.BigDecimal;

public record NotaRequest(Integer codAluno, Integer codDisciplina, Integer unidade, BigDecimal valor) { }
