package com.jvitorsantana.common.mensagem;

public class MensagemInvalidaException extends RuntimeException {
  public MensagemInvalidaException(String motivo, Throwable causa) {
    super(motivo, causa);
  }
}
