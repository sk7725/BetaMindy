package betamindy.world.blocks.campaign;

import arc.*;
import arc.graphics.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;

import static arc.Core.graphics;
import static arc.Core.settings;
import static betamindy.BetaMindy.uwu;
import static mindustry.Vars.*;

//todo lore manual, comes with camera panning to give it attention every time it updates, intangible and stuff
//This esoterum manual is the unique manual found in the first shar sector. Not to be confused with BallisticManual (portal attack remainders) or LorePage (found on other sectors)
public class LoreManual extends Block {
    public static final String loreCutsceneTag = "bm-lore-c", loreQueueTag = "bm-lore-q";
    public IntMap<Cutscene> cutscenes = IntMap.of(0, new FocusCutscene(240f));
    public Color flameColor = Pal2.esoterum;
    public Effect smokeEffect = MindyFx.smokeRise;
    public Effect flameEffect = MindyFx.manualFire;
    public Vec2 effectOffset = new Vec2(3f, 3f);
    public int lorePages = 5; //this is the least number of lore-related pages needed to restore, not including things like post-game or easter eggs.

    public LoreManual(String name){
        super(name);
        update = configurable = true;
        clipSize = 8000 * 4f;
    }

    public static boolean loreAdded(int id){
        if(settings.getBool(loreCutsceneTag, false) && settings.getInt(loreQueueTag, 0) != 0) return false;
        settings.put(loreCutsceneTag, true);
        settings.put(loreQueueTag, id);
        return true;
    }

    public class LoreManualBuild extends Building{
        public @Nullable Cutscene playing = null;
        public boolean cutsceneInit = false;

        @Override
        public void updateTile(){
            if((uwu || state.isCampaign()) && (headless || !renderer.isCutscene())){
                if(playing == null){
                    if(!cutsceneInit && settings.getBool(loreCutsceneTag, true)){
                        if(cutscenes.containsKey(settings.getInt(loreQueueTag, 0))){
                            playing = cutscenes.get(settings.getInt(loreQueueTag, 0));
                            playing.init();
                        }
                        cutsceneInit = true;
                    }
                }
                else{
                    if(playing.end()){
                        playing = null;
                        settings.put(loreCutsceneTag, false);
                        settings.put(loreQueueTag, 0);
                        return;
                    }
                    playing.update(this);
                }
            }
        }
    }

    public class Cutscene {
        public void draw(LoreManualBuild build){

        }

        public void update(LoreManualBuild build){

        }

        public boolean end(){
            return true;
        }

        public void init(){

        }
    }

    public class FocusCutscene extends Cutscene {
        public float duration;
        protected float playtime = 0f;

        public FocusCutscene(float duration){
            this.duration = duration;
        }

        @Override
        public void init(){
            playtime = 0f;
        }

        @Override
        public void update(LoreManualBuild build){
            Useful.cutscene(Tmp.v6.set(build));
            playtime += Time.delta;
        }

        @Override
        public boolean end(){
            if(playtime >= duration){
                Useful.cutsceneEnd();
                return true;
            }
            return false;
        }
    }
}
