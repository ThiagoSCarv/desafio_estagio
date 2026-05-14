package com.thiago.desafio_estagio.cliente.application;

import java.util.List;

public record ImportacaoResultado(int criados, List<String> erros) {}
