package com.megs.triangulo;

import com.badlogic.gdx.Game;

public class ScreenManager {
    private final Game game;

    public ScreenManager(Game game) {
        this.game = game;
    }

    public void showMenu() {
        game.setScreen(new MenuScreen(this));
    }

    public void showMenu(int score1, int score2) {
        HighScore.setIfHigher(score1, score2);
        game.setScreen(new MenuScreen(this));
    }

    public void showGame() {
        game.setScreen(new GameScreen(this));
    }
}
