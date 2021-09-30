package betamindy.util;

import arc.*;
import betamindy.ui.*;
import mindustry.game.*;
import static mindustry.game.Objectives.*;

public class ObjectivesM {
    public static class PortalLevel implements Objective {
        public int exp, level;

        @Override
        public boolean complete(){
            return Core.settings.getInt("betamindy-campaign-exp", 0) >= exp;
        }

        @Override
        public String display(){
            return Core.bundle.format("requirement.portallevel", AnucoinTex.emojiPortal, level);
        }

        public PortalLevel(int level){
            this.level = level;
            this.exp = (int)HardMode.expCap(level - 1);
        }
    }
}
