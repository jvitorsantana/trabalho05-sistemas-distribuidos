package com.jvitorsantana.common.rabbit;

import com.jvitorsantana.common.mensagem.TipoMensagem;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Map;

public final class Topologia {

  public static final String EXCHANGE = "mensagens.topic";
  public static final String DLX = "mensagens.dlx";
  public static final String FILA_AVALIACOES = "fila.avaliacoes";
  public static final String FILA_SUPORTE = "fila.suporte";
  public static final String DLQ_AVALIACOES = FILA_AVALIACOES + ".dlq";
  public static final String DLQ_SUPORTE = FILA_SUPORTE + ".dlq";

  public static void declarar(Channel canal) throws IOException {
    canal.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, true);
    canal.exchangeDeclare(DLX, BuiltinExchangeType.DIRECT, true);

    declararFila(canal, FILA_AVALIACOES, DLQ_AVALIACOES, TipoMensagem.AVALIACAO.getRoutingKey());
    declararFila(canal, FILA_SUPORTE, DLQ_SUPORTE, TipoMensagem.SUPORTE.getRoutingKey());
  }

  private static void declararFila(Channel canal, String fila, String dlq, String routingKey) throws IOException {
    canal.queueDeclare(dlq, true, false, false, null);
    canal.queueBind(dlq, DLX, dlq);

    Map<String, Object> args = Map.of(
        "x-dead-letter-exchange", DLX,
        "x-dead-letter-routing-key", dlq
    );

    canal.queueDeclare(fila, true, false, false, args);
    canal.queueBind(fila, EXCHANGE, routingKey);
  }
}
