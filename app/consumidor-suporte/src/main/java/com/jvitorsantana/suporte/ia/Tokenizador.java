package com.jvitorsantana.suporte.ia;

import smile.nlp.tokenizer.BreakIteratorTokenizer;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tokenizador {

  private static final Set<String> STOPWORDS = Set.of(
      "a", "o", "as", "os", "um", "uma", "de", "do", "da", "e", "que", "em", "no", "na",
      "com", "para", "por", "ao", "me", "meu", "minha", "esta", "estou");

  private final BreakIteratorTokenizer tokenizer = new BreakIteratorTokenizer();

  public List<String> tokenizar(String texto) {
    String semAcento = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "");

    List<String> palavras = new ArrayList<>();
    for (String token : tokenizer.split(semAcento)) {
      if (token.chars().allMatch(Character::isLetter) && !STOPWORDS.contains(token)) {
        palavras.add(token);
      }
    }
    return palavras;
  }
}