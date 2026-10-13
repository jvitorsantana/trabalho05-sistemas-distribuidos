package com.jvitorsantana.common.rabbit;

import com.jvitorsantana.common.config.Config;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class ConexaoRabbit {

  private static final int TENTATIVAS = 30;
  private static final long INTERVALO = 2000; // em ms

  public static Connection abrir(String nome) throws InterruptedException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(Config.texto("RABBITMQ_HOST", "localhost"));
    factory.setPort(Config.inteiro("RABBITMQ_PORT", 5672));
    factory.setUsername(Config.texto("RABBITMQ_USER", "admin"));
    factory.setPassword(Config.texto("RABBITMQ_PASS", "admin"));

    for (int tentativa = 1;;tentativa++) {
      try {
        Connection conexao = factory.newConnection(nome);
        System.out.printf("[%s] conectado ao RabbitMQ em %s:%d%n", nome, factory.getHost(), factory.getPort());
        return conexao;
      } catch (IOException | TimeoutException e) {
        if (tentativa == TENTATIVAS) {
          throw new IllegalStateException("RabbitMQ indisponível após " + TENTATIVAS + " tentativas", e);
        }
        System.out.printf("[%s] RabbitMQ indisponível (tentativa %d/%d): %s%n",
            nome, tentativa, TENTATIVAS, e.getMessage());
        Thread.sleep(INTERVALO);
      }
    }
  }
}
