package com.jvitorsantana.gerador;

import java.util.concurrent.ThreadLocalRandom;

public final class GeradorDeTextos {

  private static final String[] PRODUTOS = {
      "celular", "notebook", "pendrive", "mouse", "teclado", "relógio",
      "liquidificador", "ventilador", "alicate", "fone de ouvido", "monitor", "tablet"
  };

  private static final String[][] AVALIACOES = {
      {
        "Muito massa o %s, superou minhas expectativas e chegou antes do prazo.",
          "O %s é bom demaise, ótima qualidade e funciona perfeitamente.",
          "Recomendo demaise o %s, essa foi a melhor compra que fiz",
          "Estou embasbacado com o %s, acabamento impecável e ótimo custo-benefício.",
          "Produto excelente. O %s chegou muito rápido e veloz e bem embalado.",
          "Gostei demais do %s, vale cada centavo gasto."
      },
      {
        "Péssimo, o %s chegou quebrado e o vendedor não respondeu.",
          "O %s parou de funcionar em uma semana, que decepção.",
          "Não recomendo o %s, qualidade dar pior que tem e muito caro.",
          "Me arrependi de comprar o %s, veio com defeito e quase não chega.",
          "O %s é muito ruim, material frágil e acabamento terrível.",
          "Era melhor ter queimado o dinheiro, o %s não presta e quero devolução."
      },
      {
        "Até que o %s é mais ou menos, cumpre o que promete e nada além disso.",
          "Produto mediano, o %s funciona, mas não tem nada de especial.",
          "O %s chegou no prazo, é um produto comum pelo preço.",
          "Achei o %s normal, nem bom nem ruim. Só o básico.",
          "O %s atende ao básico, esperava um pouco mais.",
          "É um %s ok, vai servir para o uso do dia a dia."
      }
  };

  private static final String[][] SOLICITACOES = {
      {
        "Não consigo fazer login, o sistema diz que minha senha está incorreta.",
          "Minha conta foi bloqueada após várias tentativas de acesso.",
          "Esqueci minha senha e o e-mail de recuperação não chega.",
          "O sistema informa usuário inválido quando tento entrar na minha conta.",
          "Não recebo o código de verificação para autenticar meu acesso.",
          "Meu usuário não tem permissão para acessar o painel administrativo."
      },
      {
        "O sistema está muito lento, cada página demora minutos para carregar.",
          "O aplicativo trava e congela toda vez que abro um relatório.",
          "O processamento está demorando demais e consumindo toda a memória.",
          "A tela fica travada e o sistema responde com muita lentidão.",
          "Os relatórios demoram uma eternidade para carregar desde a atualização.",
          "O aplicativo ficou pesado e lento, o uso de CPU está altíssimo."
      },
      {
        "A internet cai o tempo todo e perco a conexão com o servidor.",
          "Aparece erro de timeout ao tentar conectar ao servidor.",
          "O wifi desconecta sozinho e a rede fica instável.",
          "Não consigo conectar na VPN, a conexão é recusada.",
          "Estou sem sinal de rede e o aplicativo mostra que está offline.",
          "A conexão com o banco de dados cai a cada poucos minutos."
      }
  };

  private static final String[] SAUDACOES = {"", "Olá! ", "Bom dia. ", "Boa tarde. "};

  private static final String[] COMPLEMENTOS = {
      "", " Podem me ajudar?", " Preciso de ajuda urgente.", " Já tentei reiniciar e não resolveu."
  };

  public static String avaliacao() {
    return sortear(sortear(AVALIACOES)).formatted(sortear(PRODUTOS));
  }

  public static String suporte() {
    return sortear(SAUDACOES) + sortear(sortear(SOLICITACOES)) + sortear(COMPLEMENTOS);
  }

  private static <T> T sortear(T[] opcoes) {
    return opcoes[ThreadLocalRandom.current().nextInt(opcoes.length)];
  }
}
