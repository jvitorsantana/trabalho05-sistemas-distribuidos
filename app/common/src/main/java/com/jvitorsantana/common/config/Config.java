package com.jvitorsantana.common.config;

public final class Config {

  private Config() {}

  public static String texto(String nome, String padrao) {
    String valor = System.getenv(nome);
    return (valor == null || valor.isBlank()) ? padrao : valor.trim();
  }

  public static int inteiro(String nome, int padrao) {
    return Integer.parseInt(texto(nome, String.valueOf(padrao)));
  }
}
