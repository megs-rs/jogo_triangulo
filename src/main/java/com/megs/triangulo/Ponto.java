package com.megs.triangulo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Ponto {
    public static final float RAIO = 10f;
    public static final float DISTANCIA_MINIMA = 40f;
    public static final float ALTURA_MINIMA_TRIANGULO = 25f;

    public float x, y;
    public Color cor;

    public Ponto(float x, float y, Color cor) {
        this.x = x;
        this.y = y;
        this.cor = cor;
    }

    public boolean contains(float px, float py) {
        float dx = px - x;
        float dy = py - y;
        return dx * dx + dy * dy <= RAIO * RAIO * 4;
    }

    public void render(ShapeRenderer sr, boolean selected, boolean hover) {
        if (selected) {
            sr.setColor(Color.WHITE);
            sr.circle(x, y, RAIO + 4);
        }
        if (hover) {
            sr.setColor(1f, 1f, 1f, 0.4f);
            sr.circle(x, y, RAIO + 8);
        }
        sr.setColor(cor);
        sr.circle(x, y, RAIO);
    }

    public static Ponto[] gerarPontos(int quantidade, float largura, float altura) {
        float margem = 80f;
        float margemTopo = 130f;
        Ponto[] pontos = new Ponto[quantidade];
        Color[] cores = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
        };

        for (int i = 0; i < quantidade; i++) {
            boolean valido = false;
            int tentativas = 0;
            while (!valido && tentativas < 1000) {
                float x = MathUtils.random(margem, largura - margem);
                float y = MathUtils.random(margemTopo, altura - margemTopo);
                Color cor = cores[MathUtils.random(0, cores.length - 1)];

                valido = true;
                for (int j = 0; j < i; j++) {
                    float dx = x - pontos[j].x;
                    float dy = y - pontos[j].y;
                    if (dx * dx + dy * dy < DISTANCIA_MINIMA * DISTANCIA_MINIMA) {
                        valido = false;
                        break;
                    }
                }
                if (valido) {
                    for (int j = 0; j < i; j++) {
                        for (int k = 0; k < j; k++) {
                            float lx = pontos[j].x - pontos[k].x;
                            float ly = pontos[j].y - pontos[k].y;
                            float len = (float) Math.sqrt(lx * lx + ly * ly);
                            if (len < 1f) continue;
                            float h = Math.abs(lx * (pontos[k].y - y) - ly * (pontos[k].x - x)) / len;
                            if (h < ALTURA_MINIMA_TRIANGULO) {
                                valido = false;
                                break;
                            }
                        }
                        if (!valido) break;
                    }
                }
                if (valido) {
                    pontos[i] = new Ponto(x, y, cor);
                }
                tentativas++;
            }
            if (pontos[i] == null) {
                float x = margem + (i * (largura - 2 * margem) / quantidade);
                float y = altura / 2f;
                pontos[i] = new Ponto(x, y, cores[i % cores.length]);
            }
        }
        return pontos;
    }
}
