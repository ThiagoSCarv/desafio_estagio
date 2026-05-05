package com.thiago.desafio_estagio.exceptions;

import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {

  private final MessageSource messageSource;

  public ExceptionHandlerController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public List<ErrorMessageDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new ErrorMessageDTO(
            error.getField(),
            messageSource.getMessage(error, LocaleContextHolder.getLocale())))
        .toList();
  }

  @ExceptionHandler(EmailJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessageDTO handleEmailJaCadastrado(EmailJaCadastradoException ex) {
    return new ErrorMessageDTO("email", ex.getMessage());
  }

  @ExceptionHandler(CpfJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessageDTO handleCpfJaCadastrado(CpfJaCadastradoException ex) {
    return new ErrorMessageDTO("cpf", ex.getMessage());
  }

  @ExceptionHandler(RgJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessageDTO handleRgJaCadastrado(RgJaCadastradoException ex) {
    return new ErrorMessageDTO("rg", ex.getMessage());
  }

  @ExceptionHandler(CnpjJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessageDTO handleCnpjJaCadastrado(CnpjJaCadastradoException ex) {
    return new ErrorMessageDTO("cnpj", ex.getMessage());
  }

  @ExceptionHandler(RazaoSocialJaCadastradaException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessageDTO handleRazaoSocialJaCadastrada(RazaoSocialJaCadastradaException ex) {
    return new ErrorMessageDTO("razaoSocial", ex.getMessage());
  }

  @ExceptionHandler(CepJaCadastradoException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorMessageDTO handleCepJaCadastrado(CepJaCadastradoException ex) {
    return new ErrorMessageDTO("cep", ex.getMessage());
  }

  @ExceptionHandler(ClienteNaoEncontradoException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessageDTO handleClienteNaoEncontrado(ClienteNaoEncontradoException ex) {
    return new ErrorMessageDTO("id", ex.getMessage());
  }
}
