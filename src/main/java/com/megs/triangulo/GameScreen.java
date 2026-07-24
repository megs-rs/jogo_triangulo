package com.megs.triangulo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends InputAdapter implements Screen {
    private static final float HUD_ALTURA = 100f;

    private final ScreenManager screenManager;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont fontTitulo;
    private BitmapFont fontInfo;
    private BitmapFont fontMsg;
    private BitmapFont fontGrande;
    private BitmapFont fontDado;

    private Ponto[] pontos;
    private List<Linha> linhas;
    private List<Triangulo> triangulos;
    private int turnoAtual;
    private int[] placar;
    private Dado dado;
    private int linhasRestantesTurno;
    private boolean esperandoDado;
    private boolean gameOver;
    private Ponto pontoSelecionado;
    private int pontoHover;
    private String mensagem;
    private float mensagemTimer;
    private int linhasValidasCache;
    private boolean linhasValidasDirty;

    public GameScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        fontTitulo = new BitmapFont();
        fontTitulo.getData().setScale(2.0f);

        fontInfo = new BitmapFont();
        fontInfo.getData().setScale(1.4f);

        fontMsg = new BitmapFont();
        fontMsg.getData().setScale(1.6f);

        fontGrande = new BitmapFont();
        fontGrande.getData().setScale(3.0f);

        fontDado = new BitmapFont();
        fontDado.getData().setScale(2.0f);

        int n = com.badlogic.gdx.math.MathUtils.random(5, 8);
        pontos = Ponto.gerarPontos(n, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        linhas = new ArrayList<>();
        triangulos = new ArrayList<>();
        turnoAtual = 0;
        placar = new int[]{0, 0};
        dado = new Dado();
        linhasRestantesTurno = 0;
        esperandoDado = true;
        gameOver = false;
        pontoSelecionado = null;
        pontoHover = -1;
        mensagem = "";
        mensagemTimer = 0;
        linhasValidasCache = 0;
        linhasValidasDirty = true;

        dado.rolar();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameOver) {
            screenManager.showMenu(placar[0], placar[1]);
            return true;
        }

        if (dado.isAnimando()) return true;
        if (linhasRestantesTurno <= 0) return true;

        float gameY = Gdx.graphics.getHeight() - screenY;
        if (gameY < HUD_ALTURA) return true;

        for (int i = 0; i < pontos.length; i++) {
            if (pontos[i].contains(screenX, gameY)) {
                if (pontoSelecionado == null) {
                    pontoSelecionado = pontos[i];
                } else {
                    if (pontos[i] != pontoSelecionado) {
                        int idxA = -1, idxB = -1;
                        for (int j = 0; j < pontos.length; j++) {
                            if (pontos[j] == pontoSelecionado) idxA = j;
                            if (pontos[j] == pontos[i]) idxB = j;
                        }
                        if (idxA >= 0 && idxB >= 0 && !existeLinha(idxA, idxB)
                                && !cruzaAlgumaLinha(idxA, idxB)) {
                            linhas.add(new Linha(idxA, idxB));
                            checarTriangulos(idxA, idxB);
                            linhasRestantesTurno--;
                            linhasValidasDirty = true;
                            checarFimDeJogo();
                            if (linhasRestantesTurno <= 0 && !gameOver) {
                                proximoTurno();
                            }
                        } else if (idxA >= 0 && idxB >= 0 && existeLinha(idxA, idxB)) {
                            mensagem = "Linha ja existe!";
                            mensagemTimer = 1.5f;
                        } else if (idxA >= 0 && idxB >= 0) {
                            mensagem = "Linha cruzaria outra!";
                            mensagemTimer = 1.5f;
                        }
                    }
                    pontoSelecionado = null;
                }
                return true;
            }
        }

        pontoSelecionado = null;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        int yInvertido = Gdx.graphics.getHeight() - screenY;
        pontoHover = -1;
        if (yInvertido < HUD_ALTURA) return false;
        for (int i = 0; i < pontos.length; i++) {
            if (pontos[i].contains(screenX, yInvertido)) {
                pontoHover = i;
                break;
            }
        }
        return false;
    }

    private boolean existeLinha(int a, int b) {
        for (Linha l : linhas) {
            if (l.conecta(a, b)) return true;
        }
        return false;
    }

    private boolean cruzaAlgumaLinha(int a, int b) {
        float ax = pontos[a].x, ay = pontos[a].y;
        float bx = pontos[b].x, by = pontos[b].y;
        for (Linha l : linhas) {
            if (l.idxA == a || l.idxA == b || l.idxB == a || l.idxB == b) continue;
            float cx = pontos[l.idxA].x, cy = pontos[l.idxA].y;
            float dx = pontos[l.idxB].x, dy = pontos[l.idxB].y;
            if (segmentosCruzam(ax, ay, bx, by, cx, cy, dx, dy)) return true;
        }
        return false;
    }

    private boolean segmentosCruzam(float ax, float ay, float bx, float by,
                                    float cx, float cy, float dx, float dy) {
        int o1 = orientacao(ax, ay, bx, by, cx, cy);
        int o2 = orientacao(ax, ay, bx, by, dx, dy);
        int o3 = orientacao(cx, cy, dx, dy, ax, ay);
        int o4 = orientacao(cx, cy, dx, dy, bx, by);

        if (o1 != o2 && o3 != o4) return true;

        if (o1 == 0 && pontoNoSegmento(cx, cy, ax, ay, bx, by)) return true;
        if (o2 == 0 && pontoNoSegmento(dx, dy, ax, ay, bx, by)) return true;
        if (o3 == 0 && pontoNoSegmento(ax, ay, cx, cy, dx, dy)) return true;
        if (o4 == 0 && pontoNoSegmento(bx, by, cx, cy, dx, dy)) return true;

        return false;
    }

    private int orientacao(float ax, float ay, float bx, float by, float cx, float cy) {
        float val = (by - ay) * (cx - bx) - (bx - ax) * (cy - by);
        if (Math.abs(val) < 1e-9) return 0;
        return val > 0 ? 1 : 2;
    }

    private boolean pontoNoSegmento(float px, float py, float ax, float ay, float bx, float by) {
        return px >= Math.min(ax, bx) && px <= Math.max(ax, bx)
            && py >= Math.min(ay, by) && py <= Math.max(ay, by);
    }

    private void checarTriangulos(int a, int b) {
        for (int k = 0; k < pontos.length; k++) {
            if (k == a || k == b) continue;
            boolean tem_ak = existeLinha(a, k);
            boolean tem_bk = existeLinha(b, k);
            if (tem_ak && tem_bk) {
                if (!trianguloExiste(a, b, k) && !pontoDentroDoTriangulo(a, b, k)) {
                    triangulos.add(new Triangulo(a, b, k, turnoAtual));
                    placar[turnoAtual]++;
                }
            }
        }
    }

    private boolean pontoDentroDoTriangulo(int a, int b, int c) {
        float ax = pontos[a].x, ay = pontos[a].y;
        float bx = pontos[b].x, by = pontos[b].y;
        float cx = pontos[c].x, cy = pontos[c].y;
        for (int i = 0; i < pontos.length; i++) {
            if (i == a || i == b || i == c) continue;
            if (pontoEmTriangulo(pontos[i].x, pontos[i].y, ax, ay, bx, by, cx, cy)) {
                return true;
            }
        }
        return false;
    }

    private boolean pontoEmTriangulo(float px, float py,
                                     float ax, float ay, float bx, float by, float cx, float cy) {
        float d1 = sinal(px, py, ax, ay, bx, by);
        float d2 = sinal(px, py, bx, by, cx, cy);
        float d3 = sinal(px, py, cx, cy, ax, ay);
        boolean neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        boolean pos = (d1 > 0) || (d2 > 0) || (d3 > 0);
        return !(neg && pos);
    }

    private float sinal(float px, float py, float ax, float ay, float bx, float by) {
        return (px - bx) * (ay - by) - (ax - bx) * (py - by);
    }

    private boolean trianguloExiste(int a, int b, int c) {
        for (Triangulo t : triangulos) {
            if (t.contem(a, b, c)) return true;
        }
        return false;
    }

    private void proximoTurno() {
        turnoAtual = 1 - turnoAtual;
        linhasRestantesTurno = 0;
        esperandoDado = true;
        pontoSelecionado = null;
        dado.rolar();
    }

    private void checarFimDeJogo() {
        if (linhasValidasRestantes() == 0) {
            gameOver = true;
        }
    }

    private int linhasValidasRestantes() {
        if (linhasValidasDirty) {
            int count = 0;
            for (int i = 0; i < pontos.length; i++) {
                for (int j = i + 1; j < pontos.length; j++) {
                    if (!existeLinha(i, j) && !cruzaAlgumaLinha(i, j)) {
                        count++;
                    }
                }
            }
            linhasValidasCache = count;
            linhasValidasDirty = false;
        }
        return linhasValidasCache;
    }

    private void desenharHUD() {
        shapeRenderer.setColor(0.08f, 0.08f, 0.15f, 1f);
        shapeRenderer.rect(0, Gdx.graphics.getHeight() - HUD_ALTURA,
            Gdx.graphics.getWidth(), HUD_ALTURA);

        shapeRenderer.setColor(0.2f, 0.6f, 1f, 1f);
        shapeRenderer.rect(0, Gdx.graphics.getHeight() - 3, Gdx.graphics.getWidth(), 3);

        dado.render(shapeRenderer, 20, Gdx.graphics.getHeight() - 70);
    }

    private void desenharTextosHUD() {
        float hudTop = Gdx.graphics.getHeight() - 5;

        dado.renderTexto(batch, fontDado, 20, Gdx.graphics.getHeight() - 70);

        String turnoTexto = "JOGADOR " + (turnoAtual + 1);
        fontTitulo.setColor(turnoAtual == 0 ? new Color(0.3f, 0.9f, 1f, 1f) : new Color(1f, 0.3f, 0.3f, 1f));
        GlyphLayout turnoLayout = new GlyphLayout(fontTitulo, turnoTexto);
        fontTitulo.draw(batch, turnoTexto,
            Gdx.graphics.getWidth() / 2f - turnoLayout.width / 2f,
            hudTop - 15);

        String placarTexto = "J1: " + placar[0] + "  |  J2: " + placar[1];
        fontInfo.setColor(Color.WHITE);
        GlyphLayout placarLayout = new GlyphLayout(fontInfo, placarTexto);
        fontInfo.draw(batch, placarTexto,
            Gdx.graphics.getWidth() - placarLayout.width - 30,
            hudTop - 18);

        String restantesTexto = "Linhas validas: " + linhasValidasRestantes();
        fontInfo.setColor(new Color(0.5f, 0.8f, 0.5f, 1f));
        GlyphLayout restLayout = new GlyphLayout(fontInfo, restantesTexto);
        fontInfo.draw(batch, restantesTexto,
            Gdx.graphics.getWidth() / 2f - restLayout.width / 2f,
            hudTop - 55);

        String statusTexto;
        if (esperandoDado && dado.isAnimando()) {
            statusTexto = "Rolando dado...";
            fontInfo.setColor(Color.YELLOW);
        } else if (esperandoDado) {
            statusTexto = "";
        } else {
            statusTexto = "Faltam " + linhasRestantesTurno + " linha(s)  |  Clique 2 pontos";
            fontInfo.setColor(Color.YELLOW);
        }
        if (!statusTexto.isEmpty()) {
            GlyphLayout statusLayout = new GlyphLayout(fontInfo, statusTexto);
            fontInfo.draw(batch, statusTexto,
                Gdx.graphics.getWidth() / 2f - statusLayout.width / 2f,
                hudTop - 80);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        dado.update(delta);

        if (esperandoDado && !dado.isAnimando() && linhasRestantesTurno == 0 && !gameOver) {
            int restantes = linhasValidasRestantes();
            if (restantes == 0) {
                gameOver = true;
            } else if (dado.getValor() > restantes) {
                mensagem = "Vez pulada! (dado " + dado.getValor() + " > " + restantes + " linhas)";
                mensagemTimer = 2f;
                proximoTurno();
            } else {
                linhasRestantesTurno = dado.getValor();
                esperandoDado = false;
            }
        }

        if (mensagemTimer > 0) {
            mensagemTimer -= delta;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Triangulo t : triangulos) {
            t.render(shapeRenderer, pontos);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Linha l : linhas) {
            l.render(shapeRenderer, pontos);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < pontos.length; i++) {
            boolean selected = pontoSelecionado != null && pontos[i] == pontoSelecionado;
            boolean hover = i == pontoHover;
            pontos[i].render(shapeRenderer, selected, hover);
        }

        desenharHUD();

        shapeRenderer.end();

        batch.begin();

        desenharTextosHUD();

        batch.end();

        if (mensagemTimer > 0 && !mensagem.isEmpty()) {
            fontMsg.setColor(Color.WHITE);
            GlyphLayout msgLayout = new GlyphLayout(fontMsg, mensagem);
            float msgY = Gdx.graphics.getHeight() / 2f + 80;
            float msgX = Gdx.graphics.getWidth() / 2f - msgLayout.width / 2f - 15;
            float msgBgY = msgY - msgLayout.height - 5;

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(msgX, msgBgY, msgLayout.width + 30, msgLayout.height + 15);
            shapeRenderer.end();

            batch.begin();
            fontMsg.draw(batch, mensagem,
                Gdx.graphics.getWidth() / 2f - msgLayout.width / 2f, msgY);
            batch.end();
        }

        if (gameOver) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.75f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            String vencedor;
            if (placar[0] > placar[1]) {
                vencedor = "JOGADOR 1 VENCEU!";
            } else if (placar[1] > placar[0]) {
                vencedor = "JOGADOR 2 VENCEU!";
            } else {
                vencedor = "EMPATE!";
            }

            batch.begin();

            GlyphLayout goLayout = new GlyphLayout(fontGrande, vencedor);
            fontGrande.setColor(Color.YELLOW);
            fontGrande.draw(batch, vencedor,
                Gdx.graphics.getWidth() / 2f - goLayout.width / 2f,
                Gdx.graphics.getHeight() / 2f + goLayout.height / 2f + 50);

            String placarFinal = placar[0] + " x " + placar[1];
            fontInfo.setColor(Color.WHITE);
            GlyphLayout pfLayout = new GlyphLayout(fontInfo, placarFinal);
            fontInfo.draw(batch, placarFinal,
                Gdx.graphics.getWidth() / 2f - pfLayout.width / 2f,
                Gdx.graphics.getHeight() / 2f - 20);

            String cliqueMsg = "Clique para jogar novamente";
            fontInfo.setColor(Color.LIGHT_GRAY);
            GlyphLayout clLayout = new GlyphLayout(fontInfo, cliqueMsg);
            fontInfo.draw(batch, cliqueMsg,
                Gdx.graphics.getWidth() / 2f - clLayout.width / 2f,
                Gdx.graphics.getHeight() / 2f - 70);

            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (fontTitulo != null) fontTitulo.dispose();
        if (fontInfo != null) fontInfo.dispose();
        if (fontMsg != null) fontMsg.dispose();
        if (fontGrande != null) fontGrande.dispose();
        if (fontDado != null) fontDado.dispose();
    }
}
