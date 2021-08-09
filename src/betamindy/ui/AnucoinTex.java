package betamindy.ui;

import arc.graphics.g2d.*;

import static arc.Core.atlas;

public class AnucoinTex {
    public static TextureRegion uiCoin;
    public static TextureRegion coin;
    public static String emoji;

    public static void load(){
        uiCoin = atlas.find("betamindy-anucoin");
        coin = atlas.find("betamindy-coin");
    }
}
