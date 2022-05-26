package betamindy.ui;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import betamindy.*;
import betamindy.world.blocks.defense.*;
import betamindy.world.blocks.environment.*;
import betamindy.world.blocks.units.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.ui.fragments.HintsFragment.*;

import static mindustry.Vars.*;

public class MindyHints{
    ObjectSet<String> events = new ObjectSet<>();

    public void load(){
        Core.app.post(() -> {
            ui.hints.hints.add(MindyHint.values());
        });

        Events.on(BlockBuildBeginEvent.class, event -> {
            if(state != null && !state.rules.infiniteResources && event.breaking && event.unit == player.unit() && event.tile != null && (event.tile.block() instanceof Crystal)){
                events.add("cryst");
            }
        });
        /*Events.on(BlockBuildEndEvent.class, event -> {
            if(!event.breaking && event.unit == player.unit() && event.tile != null && (event.tile.block() instanceof Mynamite)){
                events.add("myna");
            }
        });*/
        Events.on(ResetEvent.class, e -> {
            events.clear();
        });
    }

    public enum MindyHint implements Hint {
        breakCrystal(() -> BetaMindy.hints.events.contains("cryst"), () -> false),
        clearPipe(visibleDesktop, () -> (player.unit() instanceof BlockUnitc) && (((BlockUnitc)player.unit()).tile().block instanceof ClearPipe), () -> false),
        spike(() -> control.input.block instanceof Spike, () -> false);
        //mynamite(() -> BetaMindy.hints.events.contains("myna"), () -> false);
        //note: the bundles keays for these are...camelcased?!

        @Nullable
        String text;
        int visibility = visibleAll;
        Hint[] dependencies = {};
        boolean finished, cached;
        Boolp complete, shown = () -> true;

        MindyHint(Boolp complete){
            this.complete = complete;
        }

        MindyHint(int visiblity, Boolp complete){
            this(complete);
            this.visibility = visiblity;
        }

        MindyHint(Boolp shown, Boolp complete){
            this(complete);
            this.shown = shown;
        }

        MindyHint(int visiblity, Boolp shown, Boolp complete){
            this(complete);
            this.shown = shown;
            this.visibility = visiblity;
        }

        @Override
        public boolean finished(){
            if(!cached){
                cached = true;
                finished = Core.settings.getBool(name() + "-hint-done", false);
            }
            return finished;
        }

        @Override
        public void finish(){
            Core.settings.put(name() + "-hint-done", finished = true);
        }

        @Override
        public String text(){
            if(text == null){
                text = Vars.mobile && Core.bundle.has("hint." + name() + ".mobile") ? Core.bundle.get("hint." + name() + ".mobile") : Core.bundle.get("hint." + name());
                if(!Vars.mobile) text = text.replace("tap", "click").replace("Tap", "Click");
            }
            return text;
        }

        @Override
        public boolean complete(){
            return complete.get();
        }

        @Override
        public boolean show(){
            return shown.get() && (dependencies.length == 0 || !Structs.contains(dependencies, d -> !d.finished()));
        }

        @Override
        public int order(){
            return ordinal();
        }

        @Override
        public boolean valid(){
            return (Vars.mobile && (visibility & visibleMobile) != 0) || (!Vars.mobile && (visibility & visibleDesktop) != 0);
        }
    }
}