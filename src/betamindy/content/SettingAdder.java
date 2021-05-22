package betamindy.content;

import arc.Core;
import arc.scene.ui.SettingsDialog.*;
import arc.util.*;
import mindustry.*;

import java.lang.reflect.*;

public class SettingAdder {

    //reflection is used as a workaround for fields being merged into the subclass in 7.0
    public void addGraphicSetting(String key){
        set("graphics", "checkPref", new Class[]{String.class, boolean.class}, key, Core.settings.getBool(key));
    }

    public void addGameSetting(String key){
        set("game", "checkPref", new Class[]{String.class, boolean.class}, key, Core.settings.getBool(key));
    }

    public void init(){
        boolean tmp = Core.settings.getBool("uiscalechanged", false);
        Core.settings.put("uiscalechanged", false);

        addGameSetting("nonmoddedservers");
        addGraphicSetting("slimeeffect");
        addGraphicSetting("accelballs");
        addGraphicSetting("correctview");

        //Vars.ui.settings.graphics is technically located in a different class, so this throws an error in 7.0
        //simply recompiling with the 7.0 jar as a dependency will fix it
        try{
            Vars.ui.settings.graphics.sliderPref("animlevel", 2, 0, 3, i -> Core.bundle.get("slider.level." + i, "" + i));
        }catch(NoSuchFieldError nope){
            Log.warn("[BetaMindy] Ignoring animlevel setting for 7.0 compatibility");
        }

        Core.settings.put("uiscalechanged", tmp);
    }

    private static void set(String field, String method, Class[] types, Object... values){
        try{
            Object table = Reflect.get(Vars.ui.settings, field);
            Method m = table.getClass().getDeclaredMethod(method, types);
            m.invoke(table, values);
        }catch(Exception e){
            Log.err(e);
        }
    }
}
