package betamindy.ui;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;

import static arc.Core.atlas;

public class AnucoinTex {
    /** UI Atlas regions. */
    public static TextureRegion uiCoin, outlineCoin, uiBittrium, outlineBittrium, portalIcon;
    /** World Atlas regions. */
    public static TextureRegion coin, bittrium;
    public static String emoji = "", emojiBit = "", emojiPortal = "";
    public static final int unicode = 0xE005, unicodeBit = 0xE006, unicodePortal = 0xE00D;
    public static TextureRegion[] uiYut = new TextureRegion[6];
    public static String[] emojuYut = new String[6];

    public static void load(){
        uiCoin = atlas.find("betamindy-anucoin");
        outlineCoin = atlas.find("betamindy-anucoin-outline");
        coin = atlas.find("betamindy-coin");

        uiBittrium = atlas.find("betamindy-uibittrium");
        outlineBittrium = atlas.find("betamindy-uibittrium-outline");
        bittrium = atlas.find("betamindy-bittrium");

        for(int i = 0; i < 6; i++){
            uiYut[i] = atlas.find("betamindy-yut-ui-" + i);
        }
        portalIcon = atlas.find("betamindy-hardmode-portal-icon");

        if(!uiCoin.found()){
            Log.err("Cannot find the coin texture!");
            return;
        }

        Core.app.post(() -> {
            emoji = MindyUILoader.addOutlineEmoji(uiCoin, outlineCoin, unicode);
            if(uiBittrium.found()){
                emojiBit = MindyUILoader.addOutlineEmoji(uiBittrium, outlineBittrium, unicodeBit);
            }
            for(int i = 0; i < 6; i++){
                emojuYut[i] = MindyUILoader.addEmoji(uiYut[i], unicodeBit + 1 + i);
            }
            emojiPortal = MindyUILoader.addEmoji(portalIcon, unicodePortal);
        });
    }
}
