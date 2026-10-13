package com.jvitorsantana.common.mensagem;

public enum TipoMensagem {
  AVALIACAO("review.product"),
  SUPORTE("support.request");

  private final String routingKey;

  TipoMensagem(String routingKey) {
    this.routingKey = routingKey;
  }

  public String getRoutingKey() {
    return routingKey;
  }
}
