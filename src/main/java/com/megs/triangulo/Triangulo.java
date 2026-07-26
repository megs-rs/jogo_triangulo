package com.megs.triangulo;

import com.badlogic.gdx.graphics.Color;
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
}
