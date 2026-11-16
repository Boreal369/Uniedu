package br.ucsal.uniedu.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NotaResponse(Integer codNota, Integer codAluno, String nomeAluno,
                           Integer codDisciplina, String nomeDisciplina,
                           Integer unidade, BigDecimal valor, LocalDate dataLancamento) { }
