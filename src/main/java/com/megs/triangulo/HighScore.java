package com.megs.triangulo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class HighScore {
    private static final String PREFS_NAME = "triangulo";
    private static final String KEY_P1 = "highscore_p1";
    private static final String KEY_P2 = "highscore_p2";

    private HighScore() {}

    public static int get(int player) {
        String key = player == 0 ? KEY_P1 : KEY_P2;
        return getPreferences().getInteger(key, 0);
    }

    public static void setIfHigher(int score1, int score2) {
        Preferences prefs = getPreferences();
        if (score1 > prefs.getInteger(KEY_P1, 0)) {
            prefs.putInteger(KEY_P1, score1);
        }
        if (score2 > prefs.getInteger(KEY_P2, 0)) {
            prefs.putInteger(KEY_P2, score2);
        }
        prefs.flush();
    }

    private static Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }
}
