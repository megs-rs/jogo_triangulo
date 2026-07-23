package com.megs.triangulo.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.megs.triangulo.TrianguloGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Jogo dos Triangulos");
        config.setWindowedMode(900, 700);
        new Lwjgl3Application(new TrianguloGame(), config);
    }
}
