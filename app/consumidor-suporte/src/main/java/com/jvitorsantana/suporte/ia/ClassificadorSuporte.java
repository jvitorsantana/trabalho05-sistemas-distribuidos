package com.jvitorsantana.suporte.ia;

import smile.classification.DiscreteNaiveBayes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassificadorSuporte {

  private final Map<String, Integer> vocabulario = new HashMap<>();
  private final DiscreteNaiveBayes modelo;

  public ClassificadorSuporte() throws IOException {
    List<String> textos = new ArrayList<>();
    List<Categoria> categorias = new ArrayList<>();

    try (BufferedReader leitor = new BufferedReader(new InputStreamReader(
        getClass().getResourceAsStream("/treino-suporte.csv"), StandardCharsets.UTF_8))) {
      String linha;
      while ((linha = leitor.readLine()) != null) {
        String[] partes = linha.split(";", 2);
        categorias.add(Categoria.valueOf(partes[0]));
        textos.add(partes[1]);
      }
    }

    for (String texto : textos) {
      for (String palavra : palavras(texto)) {
        if (!vocabulario.containsKey(palavra)) {
          vocabulario.put(palavra, vocabulario.size());
        }
      }
    }

    int[][] x = new int[textos.size()][];
    int[] y = new int[textos.size()];
    for (int i = 0; i < textos.size(); i++) {
      x[i] = vetorizar(textos.get(i));
      y[i] = categorias.get(i).ordinal();
    }

    modelo = new DiscreteNaiveBayes(
        DiscreteNaiveBayes.Model.MULTINOMIAL, Categoria.values().length, vocabulario.size());
    modelo.update(x, y);
  }

  public Classificacao classificar(String texto) {
    double[] probabilidades = new double[Categoria.values().length];
    int classe = modelo.predict(vetorizar(texto), probabilidades);
    if (classe < 0) {
      throw new IllegalArgumentException("nenhuma palavra do texto é conhecida pelo modelo");
    }
    return new Classificacao(Categoria.values()[classe], probabilidades[classe]);
  }

  private int[] vetorizar(String texto) {
    int[] contagens = new int[vocabulario.size()];
    for (String palavra : palavras(texto)) {
      if (vocabulario.containsKey(palavra)) {
        contagens[vocabulario.get(palavra)]++;
      }
    }
    return contagens;
  }

  private static String[] palavras(String texto) {
    return texto.toLowerCase().split("[^\\p{L}]+");
  }
}