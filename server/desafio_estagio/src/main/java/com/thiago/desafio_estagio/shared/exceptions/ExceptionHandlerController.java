package com.thiago.desafio_estagio.shared.exceptions;

import com.thiago.desafio_estagio.cliente.domain.exceptions.ClienteNaoEncontradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CnpjJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.CpfJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.EmailJaCadastradoException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RazaoSocialJaCadastradaException;
import com.thiago.desafio_estagio.cliente.domain.exceptions.RgJaCadastradoException;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoNaoEncontradoException;
import com.thiago.desafio_estagio.endereco.domain.exceptions.EnderecoPrincipalException;
import com.thiago.desafio_estagio.relatorio.application.exceptions.FormatoRelatorioInvalidoException;
import java.util.List;
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
  public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
    List<ErrorMessageDTO> erros = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new ErrorMessageDTO(
            error.getField(),
            messageSource.getMessage(error, LocaleContextHolder.getLocale())))
        .toList();
    return ErrorResponse.of(erros);
  }

  @ExceptionHandler(EmailJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleEmailJaCadastrado(EmailJaCadastradoException ex) {
    return ErrorResponse.of("email", ex.getMessage());
  }

  @ExceptionHandler(CpfJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleCpfJaCadastrado(CpfJaCadastradoException ex) {
    return ErrorResponse.of("cpf", ex.getMessage());
  }

  @ExceptionHandler(RgJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleRgJaCadastrado(RgJaCadastradoException ex) {
    return ErrorResponse.of("rg", ex.getMessage());
  }

  @ExceptionHandler(CnpjJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleCnpjJaCadastrado(CnpjJaCadastradoException ex) {
    return ErrorResponse.of("cnpj", ex.getMessage());
  }

  @ExceptionHandler(RazaoSocialJaCadastradaException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleRazaoSocialJaCadastrada(RazaoSocialJaCadastradaException ex) {
    return ErrorResponse.of("razaoSocial", ex.getMessage());
  }

  @ExceptionHandler(ClienteNaoEncontradoException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleClienteNaoEncontrado(ClienteNaoEncontradoException ex) {
    return ErrorResponse.of("id", ex.getMessage());
  }

  @ExceptionHandler(EnderecoNaoEncontradoException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleEnderecoNaoEncontrado(EnderecoNaoEncontradoException ex) {
    return ErrorResponse.of("id", ex.getMessage());
  }

  @ExceptionHandler(EnderecoPrincipalException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleEnderecoPrincipal(EnderecoPrincipalException ex) {
    return ErrorResponse.of("enderecoPrincipal", ex.getMessage());
  }

  @ExceptionHandler(FormatoRelatorioInvalidoException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleFormatoRelatorioInvalido(FormatoRelatorioInvalidoException ex) {
    return ErrorResponse.of("formato", ex.getMessage());
  }
}
