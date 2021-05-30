package betamindy;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.Log;
import betamindy.graphics.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import betamindy.content.*;
import mindustry.net.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import static mindustry.Vars.headless;

public class BetaMindy extends Mod{
    public static final String githubURL = "https://github.com/sk7725/BetaMindy";
    public static final String shortName = "[#b59e72]Demo of Chaos Itself[]"; //do not use bundles unless you want to region-lock the multiplayer experience
    public static final String omegaServer = "185.86.230.102:25603";
    public static SettingAdder settingAdder = new SettingAdder();
    public static XeloUtil pushUtil = new XeloUtil();
    public static MobileFunctions mobileUtil = new MobileFunctions();
    public static Seq<Block> visibleBlockList = new Seq<Block>();
    //public static UnitGravity gravity = new UnitGravity();

    private final ContentList[] mindyContent = {
        new OverWriter(),
        new MindyStatusEffects(),
        new MindyItems(),
        new MindyBullets(),
        new MindyUnitTypes(),
        new MindyBlocks(),
        new MindyTechTree(),
        new MindyWeathers(),
        new FireColor()
    };

    public BetaMindy() {
        super();
        MindySounds.load();
        pushUtil.init();

        Core.settings.defaults("slimeeffect", true, "correctview", false, "accelballs", true, "nonmoddedservers", false, "animlevel", 2);
        Events.on(ClientLoadEvent.class, e -> {
            settingAdder.init();
            Core.app.post(() -> Core.app.post(() -> {
                if(!Core.settings.getBool("nonmoddedservers")) Vars.defaultServers.clear();
                Vars.defaultServers.add(new ServerGroup("[white][accent]Modded BetaMindy Server[][]", new String[]{omegaServer}));
            }));
        });
    }

    @Override
    public void init(){
        Vars.enableConsole = true;

        LoadedMod mod = Vars.mods.locateMod("betamindy");
        if(!headless){
            //Partial credits to ProjectUnity

            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            mod.meta.displayName = stringf.get(mod.meta.name + ".name");
            mod.meta.description = stringf.get(mod.meta.name + ".description");

            mod.meta.author = "[royal]" + mod.meta.author + "[]";
        }
        mod.meta.version = mod.meta.version + "\n" + shortName;

        //used for block weather
        Events.run(ClientLoadEvent.class, () -> {
            Vars.content.blocks().each(temp -> {
                if((temp instanceof ConstructBlock || !temp.hasBuilding()) || temp.icon(Cicon.medium) == Core.atlas.find("error")) return;
                visibleBlockList.add(temp);
            });
        });

        Events.run(WorldLoadEvent.class, () -> {
            if(!headless){
                Useful.unlockCam();
                Useful.cutsceneEnd();
            }
        });

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

            //Log.info("@: Loaded content list: @", getClass().getSimpleName(), list.getClass().getSimpleName());
        }
    }
}
