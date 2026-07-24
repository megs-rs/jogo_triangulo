# Jogo dos Triângulos — Especificação

## Visão Geral

Jogo de tabuleiro digital para 2 jogadores. Pontos coloridos são distribuídos aleatoriamente na tela. Jogadores alternam turnos desenhando linhas entre pontos para formar triângulos. Quem fechar mais triângulos vence.

## Regras

### Setup
- 10 pontos coloridos gerados aleatoriamente na tela (com margem de segurança das bordas)
- Distância mínima entre pontos: ~40px
- Cada ponto tem uma cor vibrante

### Turno
1. O dado rola automaticamente (1–6)
2. **Regra final:** Se o valor do dado for maior que o número de linhas ainda possíveis no jogo → pula a vez e dado rola para o próximo jogador
3. Jogador desenha **exatamente** essa quantidade de linhas
4. Cada linha une 2 pontos que ainda não estão ligados por uma linha
5. Para desenhar: **clique-clique** — clica no 1º ponto, depois no 2º ponto
6. Ao fechar triângulo(s): marca e ganha 1 ponto por triângulo fechado
7. Quando todas as linhas do turno foram usadas → dado rola automaticamente para o próximo jogador

### Detecção de Triângulos
- Quando uma nova linha é desenhada entre pontos A e B, o jogo verifica se existe um ponto C tal que as linhas A–C e B–C já existem
- Se sim, o triângulo (A, B, C) é formado, **desde que nenhum outro ponto esteja dentro dele**
- Uma linha pode fechar mais de um triângulo simultaneamente
- Linhas não podem se cruzar

### Fim de Jogo
- O jogo termina quando não há mais linhas possíveis (todos os pares de pontos estão conectados)
- Total máximo de linhas = C(n,2) = n × (n − 1) / 2

### Vitória
- Jogador com mais triângulos marcados vence
- Em caso de empate: ambos ganham

## Especificação Técnica

### Stack
- **Framework:** libGDX 1.12.1
- **Backend:** LWJGL3 (Desktop)
- **Java:** 21
- **Build:** Gradle 9.0

### Estrutura do Projeto

```
jogo_triangulo/
├── build.gradle
├── settings.gradle
├── .gitignore
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── SPEC.md
└── src/main/java/com/megs/triangulo/
    ├── TrianguloGame.java          Entry point (extends Game)
    ├── ScreenManager.java          Navegação entre telas
    ├── UiSkin.java                  Skin para botões/labels
    ├── HighScore.java               Persistência de recordes
    ├── MenuScreen.java              Tela inicial
    ├── GameScreen.java              Tela principal do jogo
    ├── Ponto.java                   Ponto colorido na tela
    ├── Linha.java                   Linha entre 2 pontos
    ├── Triangulo.java               Triângulo formado por 3 linhas
    ├── Dado.java                    Dado visual (1-6)
    └── desktop/
        └── DesktopLauncher.java     Launcher LWJGL3
```

### Window
- 900 × 700 pixels
- Título: "Jogo dos Triângulos"

### Renderização
- Tudo via **ShapeRenderer** (sem texturas externas)
- Fundo: preto
- Pontos: círculos preenchidos (raio ~10px)
- Linhas: brancas, espessura 2px
- Triângulos: preenchimento semi-transparente
  - Jogador 1: azul + símbolo ❤️ (coração)
  - Jogador 2: vermelho + símbolo ⭐ (estrela)
- Dado: quadrado branco com número preto (canto superior esquerdo)
- Placar: texto superior "Jogador 1: X | Jogador 2: Y"
- Indicador de turno: "Vez do Jogador X"
- Botão "Rolar Dado": retângulo clicável na parte inferior

### Input
- `touchDown` / `clicked` via InputProcessor
- 1º clique em ponto → seleciona (destaque visual)
- 2º clique em outro ponto → cria aresta (se válida)
- Hover: mudar cor do ponto sob o cursor
- Botão "Rolar Dado": cliquável

### Modelo de Dados

#### Ponto
- `float x, y` — posição
- `Color cor` — cor vibrante
- `float raio` — raio de renderização e colisão (~10px)

#### Linha
- `int idxA, idxB` — índices dos 2 pontos conectados

#### Triângulo
- `int idxA, idxB, idxC` — índices dos 3 vértices
- `int dono` — 0 ou 1 (jogador que fechou)

#### Dado
- `int valor` — 1–6
- `void rolar()` — gera valor aleatório
- Animação visual de rolagem

### Estado do GameScreen

```
List<Ponto> pontos          // 12-18 pontos
List<Linha> linhas          // linhas desenhadas
List<Triangulo> triangulos  // triângulos formados
int turnoAtual              // 0 ou 1
int[] placar                // [pontosJ1, pontosJ2]
Dado dado
int linhasRestantesTurno    // quantas linhas o jogador ainda deve desenhar
boolean esperandoDado       // true = pode rolar, false = desenhando
boolean turnoPulado         // flag para mensagem de vez pulada
boolean gameOver
Ponto pontoSelecionado      // primeiro ponto clicado (null se nenhum)
```

### Detecção de Triângulos (Algoritmo)

```java
// Para nova linha entre pontos i e j:
for (int k = 0; k < pontos.size(); k++) {
    if (k == i || k == j) continue;
    boolean tem_ik = existeLinha(i, k);
    boolean tem_jk = existeLinha(j, k);
    if (tem_ik && tem_jk) {
        // Triângulo (i, j, k) formado!
        adicionarTriangulo(i, j, k, turnoAtual);
        placar[turnoAtual]++;
    }
}
```

### Checagem de Fim de Jogo

```java
int totalPossivel = n * (n - 1) / 2;
if (linhas.size() == totalPossivel) {
    gameOver = true;
}
```

### Regra Final (Die > linhas restantes)

```java
int totalPossivel = n * (n - 1) / 2;
int linhasRestantesNoJogo = totalPossivel - linhas.size();
if (dado.getValor() > linhasRestantesNoJogo) {
    mostrarMensagem("Vez pulada! (dado > linhas restantes)");
    proximoTurno();
}
```

### Geração de Pontos

```java
int n = MathUtils.random(12, 18);
float margem = 80f;
float distanciaMinima = 40f;
// Gerar pontos com:
// - x: margem .. (largura - margem)
// - y: margem .. (altura - margem)
// - Verificar distância mínima entre todos os pares
```

Cores vibrantes: vermelho, azul, verde, amarelo, magenta, ciano, laranja, rosa.

### Dependências (build.gradle)

```groovy
def gdxVersion = '1.12.1'

dependencies {
    implementation "com.badlogicgames.gdx:gdx:$gdxVersion"
    implementation "com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion"
    implementation "com.badlogicgames.gdx:gdx-freetype:$gdxVersion"
    runtimeOnly "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
    runtimeOnly "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop"
}
```
