# Jogo dos Triângulos

Jogo de tabuleiro digital para 2 jogadores. Pontos coloridos são distribuídos aleatoriamente na tela. Jogadores alternam turnos desenhando linhas entre pontos para formar triângulos. Quem fechar mais triângulos vence.

## Como Jogar

1. **Dado rola automaticamente** — O valor (1–6) aparece no canto superior
2. **Desenhar Linhas** — Clique-clique em dois pontos diferentes para conectar com uma linha
3. **Fechar Triângulos** — Quando uma linha fecha um triângulo, você marca 1 ponto
4. **Repetir** — Desenhe exatamente a quantidade de linhas indicada pelo dado
5. **Próximo Turno** — Após usar todas as linhas, o dado rola automaticamente para o outro jogador

## Regras

- **2 jogadores** alternam turnos
- **5 a 8 pontos** coloridos gerados aleatoriamente
- **Dado (1–6)** rola automaticamente no início de cada turno, determina quantas linhas o jogador deve desenhar
- **Linha** une dois pontos ainda não conectados, desde que não cruze outra linha
- **Triângulo** = 3 pontos mutuamente conectados, sem pontos dentro → +1 ponto para quem fechou
- **Regra final:** Se o dado for maior que as linhas restantes no jogo → vez pulada
- **Fim do jogo:** Quando todos os pares de pontos estão conectados
- **Vitória:** Jogador com mais triângulos

## Como Rodar

Requisitos: Java 21

```bash
cd jogo_triangulo
./gradlew run
```

## Tecnologias

- Java 21
- libGDX 1.12.1
- Gradle 9.0
- LWJGL3 (Desktop)

## Estrutura do Projeto

```
src/main/java/com/megs/triangulo/
├── TrianguloGame.java      Entry point
├── ScreenManager.java      Navegação entre telas
├── GameScreen.java          Lógica principal do jogo
├── MenuScreen.java          Tela inicial
├── Ponto.java               Ponto colorido
├── Linha.java               Aresta entre pontos
├── Triangulo.java           Triângulo formado
├── Dado.java                Dado visual (1-6)
├── HighScore.java           Persistência de recordes
└── UiSkin.java               Skin para botões
```
