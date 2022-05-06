package betamindy.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.ui.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/**
 * Same as a LiquidConverter but also gives out "gas" to adjacent Condensers.
 */
public class LiquidRefiner extends GenericCrafter {
    public @Nullable Condenser condenser = null;
    public float gasProduce = 20f;
    public boolean drawJoint;

    public TextureRegion topRegion, jointRegion;
    public Effect gasEffect = MindyFx.releaseSteam, gasMoveEffect = MindyFx.releaseSteamSmall;
    public float effectOffset = 11.5f, effectRotation = 45f;

    private final Seq<Condenser.CondenserBuild> tmpa = new Seq<>();

    public LiquidRefiner(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        if(condenser != null) stats.add(Stat.output, table -> table.add(new GasDisplay(condenser, gasProduce, stats.timePeriod, true)));
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        jointRegion = atlas.find(name + "-joint");
        drawJoint = jointRegion.found();
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public class LiquidRefinerBuild extends GenericCrafterBuild {
        @Override
        public void consume(){
            super.consume();
            if(condenser != null){
                tmpa.clear();
                proximity.each(b -> {
                    if(b.block == condenser && !((Condenser.CondenserBuild)b).full()) tmpa.add((Condenser.CondenserBuild) b);
                });

                if(tmpa.size > 0){
                    float perGas = gasProduce / tmpa.size;
                    tmpa.each(c -> {
                        c.acceptGas(perGas);
                    });
                    if(!headless && gasMoveEffect != Fx.none){
                        for(int i = 0; i < 4; i++){
                            Tmp.v1.trns(i * 90f + effectRotation, effectOffset).add(this);
                            gasMoveEffect.at(Tmp.v1);
                        }
                    }
                }
                else if(!headless && gasEffect != Fx.none){
                    for(int i = 0; i < 4; i++){
                        Tmp.v1.trns(i * 90f + effectRotation, effectOffset).add(this);
                        gasEffect.at(Tmp.v1);
                    }
                }
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            if(condenser == null) return;
            Lines.stroke(1f, Pal.accent);
            for(Building p : proximity){
                if(p.isValid() && p.block == condenser){
                    Lines.square(p.x, p.y, p.block.size * tilesize / 2f);
                }
            }
            Draw.color();
        }

        @Override
        public void draw(){
            super.draw();
            if(drawJoint && proximity.size > 0){
                Draw.z(Layer.blockUnder - 0.01f);
                Point2[] nearby = Edges.getEdges(block.size);
                for(Point2 point : nearby){
                    Tile other = Vars.world.tile(tile.x + point.x, tile.y + point.y);

                    if(other == null || other.block() != condenser || !(other.interactable(team))) continue;

                    Draw.rect(jointRegion, other.worldx(), other.worldy());
                }
            }
        }
    }
}
