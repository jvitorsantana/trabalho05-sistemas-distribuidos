package com.jvitorsantana.sentimento;

import com.jvitorsantana.common.config.Config;
import com.jvitorsantana.common.mensagem.Mensagem;
import com.jvitorsantana.common.mensagem.MensagemJson;
import com.jvitorsantana.common.mensagem.TipoMensagem;
import com.jvitorsantana.common.rabbit.ConexaoRabbit;
import com.jvitorsantana.common.rabbit.Topologia;
import com.jvitorsantana.sentimento.ia.Classificacao;
import com.jvitorsantana.sentimento.ia.ClassificadorSentimento;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

public class ConsumidorSentimentoApp {

  private static final String NOME = "sentimento";
  private static final int ATRASO_MS = Config.inteiro("ATRASO_MS", 1000);

  public static void main(String[] args) throws Exception {
    ClassificadorSentimento classificador = ClassificadorSentimento.treinar();
    System.out.printf("[%s] IA treinada: %s%n", NOME, classificador.resumo());

    Connection con = ConexaoRabbit.abrir(NOME);
    Channel canal = con.createChannel();
    Topologia.declarar(canal);
    canal.basicQos(1);

    DeliverCallback aoReceber = (consumerTag, entrega) -> {
      long tag = entrega.getEnvelope().getDeliveryTag();
      try {
        Mensagem mensagem = MensagemJson.ler(entrega.getBody());
        if (mensagem.tipo() != TipoMensagem.AVALIACAO) {
          throw new IllegalArgumentException("tipo inesperado nesta fila: " + mensagem.tipo());
        }

        Classificacao resultado = classificador.classificar(mensagem.conteudo());
        pausar();

        System.out.printf("[%s] %-8s (%3.0f%%) | %s | %s | %s%n",
            NOME, resultado.sentimento(), resultado.confianca() * 100,
            mensagem.id(), mensagem.dataHora(), mensagem.conteudo());
        canal.basicAck(tag, false);
      } catch (Exception e) {
        System.out.printf("[%s] ERRO, enviando para a DLQ: %s%n", NOME, e.getMessage());
        canal.basicNack(tag, false, false);
      }
    };

    canal.basicConsume(Topologia.FILA_AVALIACOES, false, aoReceber, consumerTag -> {});
    System.out.printf("[%s] aguardando mensagens em %s (1 a cada %d ms)%n",
        NOME, Topologia.FILA_AVALIACOES, ATRASO_MS);
  }

  private static void pausar() {
    try {
      Thread.sleep(ATRASO_MS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}