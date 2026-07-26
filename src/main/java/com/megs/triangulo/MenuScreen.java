package com.megs.triangulo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
    private final ScreenManager screenManager;
    private Stage stage;
    private Skin skin;

    public MenuScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = UiSkin.create();

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        skin.add("title-font", titleFont);

        BitmapFont scoreFont = new BitmapFont();
        scoreFont.getData().setScale(1.5f);
        skin.add("score-font", scoreFont);

        Label title = new Label("JOGO DOS\nTRIANGULOS", new Label.LabelStyle(titleFont, Color.WHITE));
        title.setAlignment(com.badlogic.gdx.utils.Align.center);

        int hs1 = HighScore.get(0);
        int hs2 = HighScore.get(1);
        String hsTexto = "Recorde  J1: " + hs1 + "  |  J2: " + hs2;
        Label hsLabel = new Label(hsTexto, new Label.LabelStyle(scoreFont, Color.LIGHT_GRAY));
        hsLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        TextButton startButton = new TextButton("Iniciar", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenManager.showGame();
            }
        });

        TextButton exitButton = new TextButton("Sair", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.add(title).padBottom(60).row();
        table.add(startButton).width(200).height(50).padBottom(15).row();
        table.add(exitButton).width(200).height(50).padBottom(40).row();
        table.add(hsLabel);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            screenManager.showGame();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            Gdx.app.exit();
        }

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

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
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
    }
}
