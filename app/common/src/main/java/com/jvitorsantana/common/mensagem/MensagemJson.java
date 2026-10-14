package com.jvitorsantana.common.mensagem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class MensagemJson {

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  private MensagemJson() {}

  public static byte[] escrever(Mensagem mensagem) {
    try {
      return MAPPER.writeValueAsBytes(mensagem);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Mensagem ler(byte[] corpo) {
    try {
      return MAPPER.readValue(corpo, Mensagem.class);
    } catch (JsonProcessingException e) {
            String motivo = e.getCause() != null ? e.getCause().getMessage() : e.getOriginalMessage();
      throw new MensagemInvalidaException(motivo, e);
    } catch (IOException e) {
      throw new MensagemInvalidaException(e.getMessage(), e);
    }
  }
}
