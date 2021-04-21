package betamindy.content;

import arc.Core;
import mindustry.*;

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
        addGraphicSetting("slimeeffect");
        addGraphicSetting("accelballs");
        addGraphicSetting("correctview");

        Vars.ui.settings.graphics.sliderPref("animlevel", 2, 0, 3, i -> Core.bundle.get("slider.level." + i, "" + i));

        Core.settings.put("uiscalechanged", tmp);
    }
}
