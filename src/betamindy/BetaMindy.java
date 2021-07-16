package betamindy;

import arc.*;
import arc.func.*;
import arc.input.*;
import arc.struct.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.ui.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import betamindy.content.*;
import mindustry.net.*;
import mindustry.type.*;
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

    public static ScoreLib scoreLib = new ScoreLib();
    public static OrderedMap<Item, Float> itemScores;
    public static OrderedMap<Liquid, Float> liquidScores;
    public static OrderedMap<UnitType, Float> unitScores;

    public static MindyHints hints = new MindyHints();

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
        musics.init();

        Core.settings.defaults("slimeeffect", true, "correctview", true, "accelballs", true, "nonmoddedservers", false, "animlevel", 2, "ifritview", false);
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
                if((temp instanceof ConstructBlock || !temp.hasBuilding()) || temp.icon(Cicon.medium) == Core.atlas.find("error")) return;
                visibleBlockList.add(temp);
            });

            hints.load();
            if(!Core.settings.getBool("bloom") && !Core.settings.getBool("nobloomask", false)){
                Core.app.post(() -> {
                    BaseDialog dialog = new BaseDialog("@mod.betamindy.name");
                    dialog.cont.add(Core.bundle.format("ui.bloomplease", Core.bundle.get("setting.bloom.name"))).width(Vars.mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
                    dialog.buttons.defaults().size(200f, 54f).pad(2f);
                    dialog.setFillParent(false);
                    dialog.cont.row();
                    dialog.cont.check("@ui.notagain", false, b -> {
                        if(b) Core.settings.put("nobloomask", true);
                    }).left().padTop(8f);
                    dialog.buttons.button("@cancel", dialog::hide);
                    dialog.buttons.button("@ok", () -> {
                        dialog.hide();
                        Core.settings.put("bloom", true);
                        renderer.toggleBloom(true);
                    });
                    dialog.keyDown(KeyCode.escape, dialog::hide);
                    dialog.keyDown(KeyCode.back, dialog::hide);
                    dialog.show();
                });
            }
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
