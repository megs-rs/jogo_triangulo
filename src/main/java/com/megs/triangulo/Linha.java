package com.megs.triangulo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Linha {
    public int idxA;
    public int idxB;

    public Linha(int idxA, int idxB) {
        this.idxA = Math.min(idxA, idxB);
        this.idxB = Math.max(idxA, idxB);
    }

    public boolean conecta(int a, int b) {
        return (idxA == a && idxB == b) || (idxA == b && idxB == a);
    }

    public void render(ShapeRenderer sr, Ponto[] pontos) {
        sr.setColor(Color.WHITE);
        sr.line(pontos[idxA].x, pontos[idxA].y, pontos[idxB].x, pontos[idxB].y);
    }
}
