package betamindy;

import arc.*;
import arc.func.*;
import arc.util.Log;
import betamindy.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import betamindy.content.*;
import mindustry.net.*;

public class BetaMindy extends Mod{
    public static final String githubURL = "https://github.com/sk7725/BetaMindy";
    public static final String shortName = "[#b59e72]Demo of Chaos Itself[]"; //do not use bundles unless you want to region-lock the multiplayer experience
    public static SettingAdder settingAdder = new SettingAdder();
    public static XeloUtil pushUtil = new XeloUtil();
    public static MobileFunctions mobileUtil = new MobileFunctions();
    //public static UnitGravity gravity = new UnitGravity();

    private final ContentList[] mindyContent = {
        new OverWriter(),
        new MindyStatusEffects(),
        new MindyBullets(),
        new MindyBlocks(),
        new MindyTechTree(),
        new MindyWeathers()
    };

    public BetaMindy() {
        super();
        MindySounds.load();
        pushUtil.init();
        if(Vars.mobile) mobileUtil.init();

        Events.on(DisposeEvent.class, e -> {
            MindySounds.dispose();
        });

        Core.settings.defaults("slimeeffect", true, "correctview", false, "accelballs", true, "nonmoddedservers", false);
        Events.on(ClientLoadEvent.class, e -> {
            settingAdder.init();
            Core.app.post(() -> {
                if(!Core.settings.getBool("nonmoddedservers")) Vars.defaultServers.clear();
                Vars.defaultServers.add(new ServerGroup("[white][accent]Modded BetaMindy Server[][]", new String[]{"185.86.230.102:25603"}));
            });
        });
    }

    @Override
    public void init(){
        Vars.enableConsole = true;

        LoadedMod mod = Vars.mods.locateMod("betamindy");
        if(!Vars.headless){
            //Partial credits to ProjectUnity

            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            mod.meta.displayName = stringf.get(mod.meta.name + ".name");
            mod.meta.description = stringf.get(mod.meta.name + ".description");

            mod.meta.author = "[royal]" + mod.meta.author + "[]";
        }
        mod.meta.version = mod.meta.version + "\n" + shortName;

        //TODO later, stashed for now
        /*
        Events.run(Trigger.update, () -> {
            gravity.update();
        });*/
    }

    @Override
    public void loadContent(){
        for(ContentList list : mindyContent){
            list.load();

            Log.info("@: Loaded content list: @", getClass().getSimpleName(), list.getClass().getSimpleName());
        }
    }
}
