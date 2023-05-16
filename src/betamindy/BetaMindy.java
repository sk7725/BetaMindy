package betamindy;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.ui.*;
import betamindy.util.*;
import betamindy.world.blocks.campaign.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.net.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import static java.lang.Float.*;
import static mindustry.Vars.*;

public class BetaMindy extends Mod{
    public static final String githubURL = "https://github.com/sk7725/BetaMindy";
    public static final String shortName = "[#b59e72]Demo of Chaos Itself[]"; //do not use bundles unless you want to region-lock the multiplayer experience
    public static final String omegaServer = "n1.yeet.ml:6603";

    public static XeloUtil pushUtil = new XeloUtil();
    public static MobileFunctions mobileUtil = new MobileFunctions();
    public static HardMode hardmode = new HardMode();
    public static MusicControl musics = new MusicControl();

    public static ScoreLib scoreLib = new ScoreLib();
    public static OrderedMap<Item, Float> itemScores;
    public static OrderedMap<Liquid, Float> liquidScores;
    public static OrderedMap<UnitType, Float> unitScores;

    public static MindyHints hints = new MindyHints();
    public static MindyUILoader mui = new MindyUILoader();

    public static Seq<Block> visibleBlockList = new Seq<Block>();
    public static boolean uwu = OS.username.equals("sunny") || OS.username.equals("anuke") || OS.username.equals("feeli");
    public static boolean inventoryUI = false;
    //public static UnitGravity gravity = new UnitGravity();
    public static MFilters filters = new MFilters();

    public BetaMindy() {
        pushUtil.init();
        musics.init();
        MindySounds.load();
        Log.info(OS.username);

        Core.settings.defaults("slimeeffect", true, "correctview", true, "accelballs", true, "nonmoddedservers", false, "animlevel", 2, "ifritview", false, "touchpadenable", mobile, "touchpadalways", false);
        Events.on(ClientLoadEvent.class, e -> {
            SettingAdder.init();
            Core.app.post(() -> Core.app.post(() -> {
                if(!Core.settings.getBool("nonmoddedservers")) Vars.defaultServers.clear();
                Vars.defaultServers.add(new ServerGroup("[white][accent]Modded BetaMindy Server[][]", new String[]{omegaServer}));
            }));
        });

        Events.on(WorldLoadEvent.class, e -> Team.sharded.cores().each(c -> {
            if(isNaN(c.health)) c.health = c.maxHealth;
        }));

        Events.on(FileTreeInitEvent.class, e -> Core.app.post(MindyShaders::load));

        Events.on(DisposeEvent.class, e -> {
            MindyShaders.dispose();
        });

        //hm
        /*
        Events.on(EventType.pickupEvent, e -> {
            if(e.build instanceof GhostHolder ghost) ghost.pickedEvent(e.carrier);
        });
         */
    }

    @Override
    public void init(){
        scoreLib.loadItems();

        itemScores = scoreLib.scores();
        liquidScores = scoreLib.liquidScores();
        unitScores = scoreLib.unitScores();

        Log.info("Scores: ");

        Log.info("  - Items: ");
        for(Item item : Vars.content.items()){
            if(itemScores.containsKey(item)){
                Log.info("    - " + item.localizedName + ": " + itemScores.get(item).toString());
            }
        }

        Log.info("  - Liquids: ");
        for(Liquid liquid : Vars.content.liquids()){
            if(liquidScores.containsKey(liquid)){
                Log.info("    - " + liquid.localizedName + ": " + liquidScores.get(liquid).toString());
            }
        }

        Log.info("  - Units: ");
        for(UnitType unit : Vars.content.units()){
            if(unitScores.containsKey(unit)){
                Log.info("    - " + unit.localizedName + ": " + unitScores.get(unit).toString());
            }
        }

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
                if(((temp instanceof ConstructBlock) || (temp instanceof Altar) || !temp.hasBuilding()) || !temp.fullIcon.found()) return;
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
            if(!headless){
                musics.update();
                filters.update();
            }
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
        OverWriter.load();
        MindyStatusEffects.load();
        MindyLiquids.load();
        MindyItems.load();
        MindyBullets.load();
        ShopItems.load();
        FireColor.load();
        MindyBlocks.load();
        MindyWeathers.load();
        MindyPlanets.load();
        MindySectors.load();
        MindyTechTree.load();
        
        hardmode.load();
    }

    public static void clearCampaign(){
        hardmode.deleteCampaign();
        LorePages.clearCampaign();
    }
}
