package betamindy.content;

import arc.Core;
import mindustry.*;

public class SettingAdder {
    public void addGraphicSetting(String key){
        Vars.ui.settings.graphics.checkPref(key, Core.settings.getBool(key));
    }

    public void init(){
        addGraphicSetting("slimeeffect");
        addGraphicSetting("correctview");
    }
}
