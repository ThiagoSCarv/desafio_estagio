package com.thiago.desafio_estagio.shared.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageDTO {

  private String field;
  private String message;
}
