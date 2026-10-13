package com.jvitorsantana.suporte;

import com.jvitorsantana.common.config.Config;
import com.jvitorsantana.common.mensagem.Mensagem;
import com.jvitorsantana.common.mensagem.MensagemJson;
import com.jvitorsantana.common.rabbit.ConexaoRabbit;
import com.jvitorsantana.common.rabbit.Topologia;
import com.jvitorsantana.suporte.ia.Classificacao;
import com.jvitorsantana.suporte.ia.ClassificadorSuporte;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

public class ConsumidorSuporteApp {

  private static final int ATRASO_MS = Config.inteiro("ATRASO_MS", 1000);

  public static void main(String[] args) throws Exception {
    ClassificadorSuporte classificador = new ClassificadorSuporte();

    Connection con = ConexaoRabbit.abrir("suporte");
    Channel canal = con.createChannel();
    Topologia.declarar(canal);
    canal.basicQos(1);

    DeliverCallback aoReceber = (consumerTag, entrega) -> {
      long tag = entrega.getEnvelope().getDeliveryTag();
      try {
        Mensagem mensagem = MensagemJson.ler(entrega.getBody());
        Classificacao resultado = classificador.classificar(mensagem.conteudo());
        Thread.sleep(ATRASO_MS);

        System.out.printf("[suporte] %-10s (%3.0f%%) | %s | %s | %s%n",
            resultado.categoria(), resultado.confianca() * 100,
            mensagem.id(), mensagem.dataHora(), mensagem.conteudo());
        canal.basicAck(tag, false);
      } catch (Exception e) {
        System.out.printf("[suporte] ERRO, enviando para a DLQ: %s%n", e.getMessage());
        canal.basicNack(tag, false, false);
      }
    };

    canal.basicConsume(Topologia.FILA_SUPORTE, false, aoReceber, consumerTag -> {});
    System.out.printf("[suporte] aguardando mensagens em %s%n", Topologia.FILA_SUPORTE);
  }
}