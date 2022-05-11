package betamindy.content;

import arc.*;
import mindustry.*;

public class SettingAdder {

    public static void addGraphicSetting(String key){
        Vars.ui.settings.graphics.checkPref(key, Core.settings.getBool(key));
    }

    public static void addGameSetting(String key){
        Vars.ui.settings.game.checkPref(key, Core.settings.getBool(key));
    }

    public static void init(){
        addGameSetting("nonmoddedservers");
        addGameSetting("touchpadenable");
        addGameSetting("touchpadalways");
        addGraphicSetting("slimeeffect");
        addGraphicSetting("accelballs");
        addGraphicSetting("correctview");
        
        //Vars.ui.settings.addCategory("BetaMindy", "betamindy-icon", table -> {

        //});

        Vars.ui.settings.graphics.sliderPref("animlevel", 2, 0, 2, i -> Core.bundle.get("slider.level." + i, "" + i));
        //addGraphicSetting("ifritview"); //merged to status view
    }
}
