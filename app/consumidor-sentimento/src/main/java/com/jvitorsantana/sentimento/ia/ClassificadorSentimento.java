package com.jvitorsantana.sentimento.ia;

import smile.classification.DiscreteNaiveBayes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClassificadorSentimento {

  private static final String ARQUIVO_TREINO = "/treino-sentimento.csv";

  private final Map<String, Integer> vocabulario = new HashMap<>();
  private final DiscreteNaiveBayes modelo;
  private final int exemplos;

  private ClassificadorSentimento(List<String> textos, List<Sentimento> rotulos) {
    for (String texto : textos) {
      for (String termo : palavras(texto)) {
        vocabulario.putIfAbsent(termo, vocabulario.size());
      }
    }

    int[][] x = new int[textos.size()][];
    int[] y = new int[textos.size()];
    for (int i = 0; i < textos.size(); i++) {
      x[i] = vetorizar(textos.get(i));
      y[i] = rotulos.get(i).ordinal();
    }

    modelo = new DiscreteNaiveBayes(
        DiscreteNaiveBayes.Model.MULTINOMIAL, Sentimento.values().length, vocabulario.size());
    modelo.update(x, y);
    exemplos = textos.size();
  }

  public static ClassificadorSentimento treinar() {
    List<String> textos = new ArrayList<>();
    List<Sentimento> rotulos = new ArrayList<>();

    InputStream arquivo = ClassificadorSentimento.class.getResourceAsStream(ARQUIVO_TREINO);
    if (arquivo == null) {
      throw new IllegalStateException("Arquivo de treino não encontrado: " + ARQUIVO_TREINO);
    }

    try (BufferedReader leitor = new BufferedReader(new InputStreamReader(arquivo, StandardCharsets.UTF_8))) {
      String linha;
      while ((linha = leitor.readLine()) != null) {
        if (linha.isBlank() || linha.startsWith("#")) {
          continue;
        }
        String[] partes = linha.split(";", 2);
        rotulos.add(Sentimento.valueOf(partes[0].trim()));
        textos.add(partes[1].trim());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return new ClassificadorSentimento(textos, rotulos);
  }

  public Classificacao classificar(String texto) {
    int[] x = vetorizar(texto);
    if (Arrays.stream(x).sum() == 0) {
      throw new IllegalArgumentException("nenhum termo do texto é conhecido pelo modelo");
    }

    double[] probabilidades = new double[Sentimento.values().length];
    int classe = modelo.predict(x, probabilidades);
    return new Classificacao(Sentimento.values()[classe], probabilidades[classe]);
  }

  public String resumo() {
    return "Naive Bayes multinomial (Smile), %d exemplos, %d termos".formatted(exemplos, vocabulario.size());
  }

  private int[] vetorizar(String texto) {
    int[] contagens = new int[vocabulario.size()];
    for (String termo : palavras(texto)) {
      Integer indice = vocabulario.get(termo);
      if (indice != null) {
        contagens[indice]++;
      }
    }
    return contagens;
  }

  private static String[] palavras(String texto) {
    return texto.toLowerCase().split("[^\\p{L}]+");
  }
}