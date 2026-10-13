package com.jvitorsantana.gerador;

import com.jvitorsantana.common.config.Config;
import com.jvitorsantana.common.mensagem.Mensagem;
import com.jvitorsantana.common.mensagem.MensagemJson;
import com.jvitorsantana.common.mensagem.TipoMensagem;
import com.jvitorsantana.common.rabbit.ConexaoRabbit;
import com.jvitorsantana.common.rabbit.Topologia;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GeradorApp {

  private static final int MENSAGENS_POR_SEGUNDO = Config.inteiro("MENSAGENS_POR_SEGUNDO", 10);
  private static final int ERRO_A_CADA = Config.inteiro("ERRO_A_CADA", 20);

  private static long contador = 0;

  public static void main(String[] args) throws Exception {
    Connection con = ConexaoRabbit.abrir("gerador");
    Channel canal = con.createChannel();
    Topologia.declarar(canal);

    System.out.printf("[gerador] publicando %d msg/s (1 inválida a cada %d)%n",
        MENSAGENS_POR_SEGUNDO, ERRO_A_CADA);

    ScheduledExecutorService agendador = Executors.newSingleThreadScheduledExecutor();
    long intervalo = 1_000_000L / MENSAGENS_POR_SEGUNDO;
    agendador.scheduleAtFixedRate(() -> publicarProxima(canal), 0, intervalo, TimeUnit.MICROSECONDS);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      agendador.shutdown();
      try {
        con.close();
      } catch (Exception ignored) {}
    }));
  }

  private static void publicarProxima(Channel canal) {
    try {
      contador++;
      TipoMensagem tipo = ThreadLocalRandom.current().nextBoolean()
          ? TipoMensagem.AVALIACAO
          : TipoMensagem.SUPORTE;

      if (ERRO_A_CADA > 0 && contador % ERRO_A_CADA == 0) {
        publicarInvalida(canal, tipo);
        return;
      }

      String conteudo = tipo == TipoMensagem.AVALIACAO
          ? GeradorDeTextos.avaliacao()
          : GeradorDeTextos.suporte();
      Mensagem mensagem = Mensagem.nova(tipo, conteudo);

      publicar(canal, tipo, mensagem.id().toString(), MensagemJson.escrever(mensagem));
      System.out.printf("[gerador] #%d %-15s %s%n", contador, tipo.getRoutingKey(), conteudo);
    } catch (Exception e) {
      System.out.printf("[gerador] falha ao publicar: %s%n", e.getMessage());
    }
  }

  private static void publicarInvalida(Channel canal, TipoMensagem tipo) throws IOException {
    String id = UUID.randomUUID().toString();
    String json = "{\"id\":\"%s\",\"dataHora\":\"%s\",\"tipo\":\"%s\",\"conteudo\":\"\"}"
        .formatted(id, Instant.now(), tipo.name());

    publicar(canal, tipo, id, json.getBytes(StandardCharsets.UTF_8));
    System.out.printf("[gerador] #%d %-15s (INVÁLIDA de propósito: conteúdo vazio)%n",
        contador, tipo.getRoutingKey());
  }

  private static void publicar(Channel canal, TipoMensagem tipo, String id, byte[] corpo) throws IOException {
    AMQP.BasicProperties propriedades = new AMQP.BasicProperties.Builder()
        .contentType("application/json")
        .deliveryMode(2)
        .messageId(id)
        .type(tipo.name())
        .build();

    canal.basicPublish(Topologia.EXCHANGE, tipo.getRoutingKey(), propriedades, corpo);
  }
}
