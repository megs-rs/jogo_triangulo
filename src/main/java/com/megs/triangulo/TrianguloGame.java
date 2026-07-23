package com.megs.triangulo;

import com.badlogic.gdx.Game;

public class TrianguloGame extends Game {
    private ScreenManager screenManager;

    @Override
    public void create() {
        screenManager = new ScreenManager(this);
        screenManager.showMenu();
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }
}
