package com.megs.triangulo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

final class UiSkin {
    private UiSkin() {}

    static Skin create() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.2f);
        skin.add("default-font", font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.up = skin.newDrawable("white", new Color(0.2f, 0.2f, 0.3f, 1f));
        style.down = skin.newDrawable("white", new Color(0.12f, 0.12f, 0.22f, 1f));
        style.over = skin.newDrawable("white", new Color(0.3f, 0.3f, 0.45f, 1f));
        skin.add("default", style);

        return skin;
    }
}
