package betamindy.world.blocks.campaign;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;

import static mindustry.Vars.*;
import static mindustry.Vars.ui;

public class ManualPiece extends LoreManual {
    public @Nullable LorePages.Chapter chapter;
    /** The type of cutscene to play. */
    public int cutsceneID = 1;
    public @Nullable LoreManual parent = null;
    public Effect despawnEffect = Fx.mineHuge;

    public ManualPiece(String name, LorePages.Chapter chapter){
        super(name);
        this.chapter = chapter;
        isPage = true;
        scanRange = 60f;

        effectColor = Color.white;
        flameEffect = MindyFx.sparkle;
        effectOffset.set(0f, 0f);
        smokeChance = 0f;
        smokeEffect = Fx.none;
        effectChance = 0.05f;
        drawRotation = Mathf.randomSeed(chapter.id, 20f, 170f);
        targetable = false;
        breakable = false;
        hasShadow = false;
        cutscenes = IntMap.of();
    }

    @Override
    public void init(){
        super.init();
        if(parent != null && chapter != null){
            parent.pageBlocks.add(this);
            parent.pageBlocks.sort(Structs.comparingInt(ManualPiece::getCID)); //comparingInt is not available in Android
            if(!chapter.optional) parent.lorePages++;
        }
    }

    public int getCID(){
        return chapter == null ? 0 : chapter.id;
    }

    public class LorePageBuild extends LoreManualBuild {
        @Override
        public void buildDialog(){
            if(loreEmpty() && InventoryModule.add(block, 1, team)){
                if(!loreAdded(cutsceneID)){
                    InventoryModule.add(block, -1, team);
                    if(!headless) ui.showInfoToast("@ui.scan.error", 3f);
                }
                else{
                    if(!headless){
                        ui.showInfoToast("@ui.scan.added", 3f);
                        despawnEffect.at(x, y, flameColor);
                    }
                    if(chapter != null) chapter.unlock();
                    tile.remove();
                    remove();
                }
            }
            else{
                if(!headless) ui.showInfoToast("@ui.scan.error", 3f);
            }
        }

        @Override
        public boolean shouldShowConfigure(Player player){
            if(!super.shouldShowConfigure(player) || playing != null) return false;
            if(state.isEditor()) return true;
            if(scanning == null && player.dst2(this) <= scanRange * scanRange && loreEmpty()){
                configure(true);
                return false;
            }
            if(!headless){
                if(scanning != null) ui.showInfoToast("@ui.scan.wait", 3f);
                else if(!loreEmpty()) ui.showInfoToast("@ui.scan.full", 3f);
                else ui.showInfoToast("@ui.scan.failed", 3f);
            }
            return false;
        }
    }
}
