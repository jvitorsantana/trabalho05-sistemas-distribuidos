package com.jvitorsantana.sentimento.ia;

import smile.nlp.tokenizer.BreakIteratorTokenizer;
import smile.nlp.tokenizer.Tokenizer;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Tokenizador {

  private static final Locale PORTUGUES = Locale.of("pt", "BR");

  private static final Set<String> STOPWORDS = Set.of(
      "a", "o", "as", "os", "um", "uma", "de", "do", "da", "dos", "das", "e", "que",
      "em", "no", "na", "nos", "nas", "com", "para", "por", "ao", "se", "me", "eu",
      "foi", "ser", "esta", "estou", "meu", "minha", "minhas", "isso", "disso", "essa", "esse"
  );

  private final Tokenizer tokenizer = new BreakIteratorTokenizer(PORTUGUES);
  public List<String> tokenizar(String texto) {
    String semAcento = Normalizer.normalize(texto.toLowerCase(PORTUGUES), Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "");

    List<String> palavras = new ArrayList<>();
    for (String token : tokenizer.split(semAcento)) {
      if (token.chars().allMatch(Character::isLetter) && !STOPWORDS.contains(token)) {
        palavras.add(token);
      }
    }

    List<String> termos = new ArrayList<>(palavras);
    for (int i = 0; i + 1 < palavras.size(); i++) {
      termos.add(palavras.get(i) + "_" + palavras.get(i + 1));
    }
    return termos;
  }
}
