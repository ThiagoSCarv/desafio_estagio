package com.thiago.desafio_estagio.shared.exceptions;

import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.domain.exceptions.CepNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoPrincipalException;
import com.thiago.desafio_estagio.relatorio.application.exceptions.FormatoRelatorioInvalidoException;
import com.thiago.desafio_estagio.relatorio.application.exceptions.RelatorioException;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Centraliza o tratamento de excecoes da API. Todas as respostas seguem o mesmo
// envelope ErrorResponse, contendo uma lista de { field, message }.
@RestControllerAdvice
public class ExceptionHandlerController {

  private final MessageSource messageSource;

  public ExceptionHandlerController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
    var locale = LocaleContextHolder.getLocale();
    // inclui field errors e global errors (constraints de classe como @AssertTrue)
    List<ErrorMessageDTO> errors = Stream.concat(
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new ErrorMessageDTO(e.getField(), messageSource.getMessage(e, locale))),
        ex.getBindingResult().getGlobalErrors().stream()
            .map(e -> new ErrorMessageDTO(e.getObjectName(), messageSource.getMessage(e, locale)))
    ).toList();
    return ErrorResponse.of(errors);
  }

  @ExceptionHandler(DuplicidadeException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleDuplicate(DuplicidadeException ex) {
    return ErrorResponse.of(ex.field(), ex.getMessage());
  }

  @ExceptionHandler(ClienteNaoEncontradoException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleClienteNotFound(ClienteNaoEncontradoException ex) {
    return ErrorResponse.of("id", ex.getMessage());
  }

  @ExceptionHandler(EnderecoNaoEncontradoException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleEnderecoNotFound(EnderecoNaoEncontradoException ex) {
    return ErrorResponse.of("id", ex.getMessage());
  }

  @ExceptionHandler(CepNaoEncontradoException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ErrorResponse handleCepNotFound(CepNaoEncontradoException ex) {
    return ErrorResponse.of("cep", ex.getMessage());
  }

  @ExceptionHandler(EnderecoPrincipalException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleEnderecoPrincipalConflict(EnderecoPrincipalException ex) {
    return ErrorResponse.of("enderecoPrincipal", ex.getMessage());
  }

  @ExceptionHandler(FormatoRelatorioInvalidoException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleInvalidReportFormat(FormatoRelatorioInvalidoException ex) {
    return ErrorResponse.of("formato", ex.getMessage());
  }

  @ExceptionHandler(RelatorioException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleReportError(RelatorioException ex) {
    return ErrorResponse.of("relatorio", ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleUnexpected(Exception ex) {
    return ErrorResponse.of("erro", "Erro interno inesperado");
  }
}
