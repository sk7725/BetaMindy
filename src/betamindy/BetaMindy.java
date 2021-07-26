package betamindy;

import arc.*;
import arc.func.*;
import arc.input.*;
import arc.struct.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.ui.*;
import betamindy.util.*;
import betamindy.world.blocks.campaign.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import betamindy.content.*;
import mindustry.net.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import static mindustry.Vars.*;

public class BetaMindy extends Mod{
    public static final String githubURL = "https://github.com/sk7725/BetaMindy";
    public static final String shortName = "[#b59e72]Demo of Chaos Itself[]"; //do not use bundles unless you want to region-lock the multiplayer experience
    public static final String omegaServer = "185.86.230.61:25573";

    public static SettingAdder settingAdder = new SettingAdder();
    public static XeloUtil pushUtil = new XeloUtil();
    public static MobileFunctions mobileUtil = new MobileFunctions();
    public static HardMode hardmode = new HardMode();
    public static MusicControl musics = new MusicControl();
    public static MindyHints hints = new MindyHints();
    public static MindyUILoader mui = new MindyUILoader();

    public static Seq<Block> visibleBlockList = new Seq<Block>();
    public static boolean uwu = OS.username.equals("sunny");
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
        pushUtil.init();
        musics.init();

        Core.settings.defaults("slimeeffect", true, "correctview", true, "accelballs", true, "nonmoddedservers", false, "animlevel", 2, "ifritview", false);
        Events.on(ClientLoadEvent.class, e -> {
            settingAdder.init();
            Core.app.post(() -> Core.app.post(() -> {
                if(!Core.settings.getBool("nonmoddedservers")) Vars.defaultServers.clear();
                Vars.defaultServers.add(new ServerGroup("[white][accent]Modded BetaMindy Server[][]", new String[]{omegaServer}));
            }));
        });

        Events.on(FileTreeInitEvent.class, e -> Core.app.post(() -> {
            MindyShaders.load();
            MindySounds.load();
        }));

        Events.on(DisposeEvent.class, e -> {
            MindyShaders.dispose();
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
            mod.meta.description = Core.bundle.get("mod.betamindy.description") + "\n\n" + Core.bundle.format("mod.betamindy.credits", Core.bundle.get("mod.codev"), Core.bundle.get("mod.contributors"), Core.bundle.get("mod.musics"));

            mod.meta.author = "[royal]" + mod.meta.author + "[]";
        }
        mod.meta.version = mod.meta.version + "\n" + shortName;

        //used for block weather
        Events.run(ClientLoadEvent.class, () -> {
            Vars.content.blocks().each(temp -> {
                if(((temp instanceof ConstructBlock) || (temp instanceof Altar) || !temp.hasBuilding()) || !temp.icon(Cicon.full).found()) return;
                visibleBlockList.add(temp);
            });

            hints.load();
            mui.init();
        });

        Events.run(WorldLoadEvent.class, () -> {
            if(!headless){
                Useful.unlockCam();
                Useful.cutsceneEnd();
            }
        });

        hardmode.init();
        Events.run(Trigger.update, () -> {
            if(state.isPlaying()) hardmode.update();
            musics.update();
            //later, stashed for now
            //gravity.update();
        });
        if(!headless){
            Events.run(Trigger.draw, () -> {
                if(state.isGame()) hardmode.draw();
            });
        }
    }

    @Override
    public void loadContent(){
        for(ContentList list : mindyContent){
            list.load();

            //Log.info("@: Loaded content list: @", getClass().getSimpleName(), list.getClass().getSimpleName());
        }
        hardmode.load();
    }
}
