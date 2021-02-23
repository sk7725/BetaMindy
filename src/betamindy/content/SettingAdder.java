package betamindy.content;

import arc.*;
import mindustry.*;

public class SettingAdder {
    public void addGraphicSetting(String key){
        Vars.ui.settings.graphics.checkPref(key, Core.settings.getBool(key));
    }

    public void init(){
        boolean tmp = Core.settings.getBool("uiscalechanged", false);
        Core.settings.put("uiscalechanged", false);

        addGraphicSetting("slimeeffect");
        addGraphicSetting("accelballs");
        addGraphicSetting("correctview");

        Core.settings.put("uiscalechanged", tmp);
    }
}
