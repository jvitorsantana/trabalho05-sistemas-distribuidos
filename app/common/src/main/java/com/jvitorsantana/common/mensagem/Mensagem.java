package com.jvitorsantana.common.mensagem;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Mensagem(UUID id, Instant dataHora, TipoMensagem tipo, String conteudo) {

  public Mensagem {
    Objects.requireNonNull(id, "O id é obrigatório");
    Objects.requireNonNull(dataHora, "A dataHora é obrigatória");
    Objects.requireNonNull(tipo, "O tipo é obrigatório");
    if (conteudo == null || conteudo.isBlank()) {
      throw new IllegalArgumentException("O conteúdo é obrigatório");
    }
  }

  public static Mensagem nova(TipoMensagem tipo, String conteudo) {
    return new Mensagem(UUID.randomUUID(), Instant.now(), tipo, conteudo);
  }
}
