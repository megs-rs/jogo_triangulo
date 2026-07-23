package com.megs.triangulo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Triangulo {
    public int idxA, idxB, idxC;
    public int dono;

    private static final Color COR_P1 = new Color(0.2f, 0.4f, 1f, 0.25f);
    private static final Color COR_P2 = new Color(1f, 0.2f, 0.2f, 0.25f);

    public Triangulo(int idxA, int idxB, int idxC, int dono) {
        this.idxA = idxA;
        this.idxB = idxB;
        this.idxC = idxC;
        this.dono = dono;
    }

    public boolean contem(int a, int b, int c) {
        int[] verts = {idxA, idxB, idxC};
        boolean temA = false, temB = false, temC = false;
        for (int v : verts) {
            if (v == a) temA = true;
            if (v == b) temB = true;
            if (v == c) temC = true;
        }
        return temA && temB && temC;
    }

    public void render(ShapeRenderer sr, Ponto[] pontos) {
        sr.setColor(dono == 0 ? COR_P1 : COR_P2);
        sr.triangle(
            pontos[idxA].x, pontos[idxA].y,
            pontos[idxB].x, pontos[idxB].y,
            pontos[idxC].x, pontos[idxC].y
        );
    }

    public void renderSimbolo(SpriteBatch batch, Ponto[] pontos, BitmapFont font) {
        float cx = (pontos[idxA].x + pontos[idxB].x + pontos[idxC].x) / 3f;
        float cy = (pontos[idxA].y + pontos[idxB].y + pontos[idxC].y) / 3f;

        String simbolo = dono == 0 ? "\u2764" : "\u2605";
        font.setColor(dono == 0 ? Color.WHITE : Color.YELLOW);
        GlyphLayout layout = new GlyphLayout(font, simbolo);
        font.draw(batch, simbolo, cx - layout.width / 2f, cy + layout.height / 2f);
    }
}
