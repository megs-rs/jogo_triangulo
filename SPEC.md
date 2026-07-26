# Jogo dos Triângulos — Especificação

## Visão Geral

Jogo de tabuleiro digital para 2 jogadores. Pontos coloridos são distribuídos aleatoriamente na tela. Jogadores alternam turnos desenhando linhas entre pontos para formar triângulos. Quem fechar mais triângulos vence.

## Regras

### Jogadores
- **Jogador 1 (J1):** azul
- **Jogador 2 (J2):** vermelho

### Setup
- 10 pontos coloridos gerados aleatoriamente na tela (com margem de segurança das bordas)
- Distância mínima entre pontos: ~40px
- Altura mínima entre pontos e retas formadas por pares existentes: ~25px (evita triângulos achatados)
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

### High Scores
- Ao finalizar o jogo, as pontuações são comparadas com os recordes salvos
- Se uma pontuação for maior que o recorde existente, ela é atualizada
- Os recordes são exibidos na tela inicial

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
├── TODO.md
├── README.md
└── src/main/java/com/megs/triangulo/
    ├── TrianguloGame.java          Entry point (extends Game)
    ├── ScreenManager.java          Navegação entre telas
    ├── UiSkin.java                  Skin para botões/labels
    ├── HighScore.java               Persistência de recordes
    ├── MenuScreen.java              Tela inicial com recordes
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
  - Jogador 1: azul
  - Jogador 2: vermelho
- Dado: quadrado branco com número preto (canto superior esquerdo)
- Placar: "J1: X | J2: Y" com cores dos jogadores
- Indicador de turno: "JOGADOR X" na cor do jogador
- Tela inicial: exibe recordes "Recorde J1: X | J2: Y"

### Input
- `touchDown` via InputProcessor
- 1º clique em ponto → seleciona (destaque visual)
- 2º clique em outro ponto → cria aresta (se válida)
- Hover: mudar cor do ponto sob o cursor

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
Ponto[] pontos               // 10 pontos
List<Linha> linhas           // linhas desenhadas
List<Triangulo> triangulos   // triângulos formados
int turnoAtual               // 0 ou 1
int[] placar                 // [pontosJ1, pontosJ2]
Dado dado
int linhasRestantesTurno     // quantas linhas o jogador ainda deve desenhar
boolean esperandoDado        // true = pode rolar, false = desenhando
boolean gameOver
Ponto pontoSelecionado       // primeiro ponto clicado (null se nenhum)
int linhasValidasCache       // cache de linhas válidas restantes
boolean linhasValidasDirty   // flag para recalcular cache
```

### Detecção de Triângulos (Algoritmo)

```java
// Para nova linha entre pontos i e j:
for (int k = 0; k < pontos.length; k++) {
    if (k == i || k == j) continue;
    boolean tem_ik = existeLinha(i, k);
    boolean tem_bk = existeLinha(j, k);
    if (tem_ik && tem_bk) {
        if (!trianguloExiste(i, j, k) && !pontoDentroDoTriangulo(i, j, k)) {
            triangulos.add(new Triangulo(i, j, k, turnoAtual));
            placar[turnoAtual]++;
        }
    }
}
```

### Checagem de Fim de Jogo

```java
// Usa cache de linhas válidas (evita recálculo a cada frame)
if (linhasValidasRestantes() == 0) {
    gameOver = true;
}
```

### Regra Final (Dado > linhas restantes)

```java
int restantes = linhasValidasRestantes();
if (dado.getValor() > restantes) {
    mostrarMensagem("Vez pulada! (dado " + dado.getValor() + " > " + restantes + " linhas)");
    proximoTurno();
}
```

### Geração de Pontos

```java
int n = 10;
float margem = 80f;
float margemTopo = 130f;
float distanciaMinima = 40f;
float alturaMinima = 25f;
// Gerar pontos com:
// - x: margem .. (largura - margem)
// - y: margemTopo .. (altura - margemTopo)
// - Verificar distância mínima entre todos os pares
// - Verificar altura mínima vs retas formadas por pares existentes
```

Cores vibrantes: vermelho, azul, verde, amarelo, magenta, ciano, laranja, rosa.

### Dependências (build.gradle)

```groovy
def gdxVersion = '1.12.1'

dependencies {
    implementation "com.badlogicgames.gdx:gdx:$gdxVersion"
    implementation "com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion"
    runtimeOnly "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
}
```
