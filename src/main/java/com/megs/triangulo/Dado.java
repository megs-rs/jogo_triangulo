package com.megs.triangulo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Dado {
    private int valor;
    private float animTimer;
    private float animDuracao;
    private boolean animando;

    public static final float TAMANHO = 50f;

    public Dado() {
        valor = 1;
        animando = false;
        animTimer = 0;
        animDuracao = 0;
    }

    public void rolar() {
        animando = true;
        animTimer = 0;
        animDuracao = 0.5f;
    }

    public void update(float delta) {
        if (animando) {
            animTimer += delta;
            valor = MathUtils.random(1, 6);
            if (animTimer >= animDuracao) {
                animando = false;
            }
        }
    }

    public boolean isAnimando() {
        return animando;
    }

    public int getValor() {
        return valor;
    }

    public void render(ShapeRenderer sr, float x, float y) {
        sr.setColor(Color.WHITE);
        sr.rect(x, y, TAMANHO, TAMANHO);
    }

    public void renderTexto(SpriteBatch batch, BitmapFont font, float x, float y) {
        String texto = String.valueOf(valor);
        font.setColor(Color.BLACK);
        GlyphLayout layout = new GlyphLayout(font, texto);
        font.draw(batch, texto,
            x + TAMANHO / 2f - layout.width / 2f,
            y + TAMANHO / 2f + layout.height / 2f);
    }
}
