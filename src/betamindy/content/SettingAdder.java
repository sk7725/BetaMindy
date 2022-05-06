package betamindy.content;

import arc.Core;
import arc.util.*;
import mindustry.*;

import java.lang.reflect.*;

public class SettingAdder {

    public void addGraphicSetting(String key){
        Vars.ui.settings.graphics.checkPref(key, Core.settings.getBool(key));
    }

    public void addGameSetting(String key){
        Vars.ui.settings.game.checkPref(key, Core.settings.getBool(key));
    }

    public void init(){
        boolean tmp = Core.settings.getBool("uiscalechanged", false);
        Core.settings.put("uiscalechanged", false);

        addGameSetting("nonmoddedservers");
        addGameSetting("touchpadenable");
        addGameSetting("touchpadalways");
        addGraphicSetting("slimeeffect");
        addGraphicSetting("accelballs");
        addGraphicSetting("correctview");

        Vars.ui.settings.graphics.sliderPref("animlevel", 2, 0, 2, i -> Core.bundle.get("slider.level." + i, "" + i));
        //addGraphicSetting("ifritview"); //merged to status view

        Core.settings.put("uiscalechanged", tmp);
    }
}
