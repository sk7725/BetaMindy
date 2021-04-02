package betamindy.world.blocks.logic;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;

public class ProcessorCooler extends Block {
    public TextureRegion heatRegion, liquidRegion, topRegion;
    public boolean useTopRegion = false; //set automatically
    public Color heatColor = Pal.turretHeat;
    public boolean acceptCoolant = false;
    public int boost = 2;
    public int maxProcessors = 2;

    /** The block will assume this to be the best coolant it will ever get. Switch this with the highest equal-game-progression coolant of your mod. Used for the bar only and does not affect gameplay. */
    public Liquid maxCoolantConsidered = Liquids.cryofluid;
    /** Set by maxCoolantConsided; Again, this is visual only. */
    public int maxBoostBar;

    public ProcessorCooler(String name){
        super(name);

        update = true;
        solid = true;
        rotate = false;
    }

    @Override
    public void init(){
        if(acceptCoolant && !consumes.has(ConsumeType.liquid)){
            hasLiquids = true;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 3.5f));
        }

        super.init();
        if(acceptCoolant) maxBoostBar =  Mathf.round((1f + maxCoolantConsidered.heatCapacity) * boost);
        else maxBoostBar = boost;
    }

    @Override
    public void load(){
        super.load();
        heatRegion = atlas.find(name + "-heat", region);
        if(consumes.has(ConsumeType.liquid)){
            liquidRegion = atlas.find(name + "-liquid");
        }
        topRegion = atlas.find(name + "-top");
        useTopRegion = atlas.isFound(topRegion);
    }

    @Override
    public TextureRegion[] icons(){
        if(useTopRegion) return new TextureRegion[]{region, topRegion};
        return super.icons();
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, "[orange]@[] \uF7E4", maxProcessors);
        stats.add(Stat.speedIncrease, boost * 100, StatUnit.percent);
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("boost", (ProcessorCoolerBuild entity) -> new Bar(() -> Core.bundle.format("bar.boost", entity.realBoost() * 100), () -> Pal.accent, () -> (float)entity.realBoost() / maxBoostBar));
        bars.add("links", (ProcessorCoolerBuild entity) -> new Bar(() -> Core.bundle.format("bar.coolprocs", entity.usedLinks, maxProcessors), () -> Pal.ammo, () -> entity.heat));
    }

    public class ProcessorCoolerBuild extends Building {
        public float heat = 0;
        public int usedLinks = 0;
        private final int[] veryGoodLanguageDesign = new int[]{0};

        public int realBoost(){
            if(enabled && cons.valid() && efficiency() > 0.8f){
                if(acceptCoolant){
                    Liquid liquid = liquids.current();
                    return Math.max(1, Mathf.round((1f + liquid.heatCapacity) * boost));
                }
                return boost;
            }
            return 1;
        }

        @Override
        public void updateTile(){
            veryGoodLanguageDesign[0] = 0;

            int b = realBoost();
            if(b >= 2) proximity.each(p -> {
                if(veryGoodLanguageDesign[0] >= maxProcessors) return;
                if(p instanceof LogicBlock.LogicBuild){
                    for(int i = 0; i < b - 1; i++) p.updateTile();
                    veryGoodLanguageDesign[0]++; //suck
                }
            });
            usedLinks = veryGoodLanguageDesign[0];
            heat = Mathf.lerpDelta(heat, Mathf.clamp(((float)veryGoodLanguageDesign[0]) / maxProcessors), 0.03f);
        }

        @Override
        public void draw(){
            super.draw();
            if(liquids != null){
                if(liquids.total() > 0.01f) Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
            }
            if(useTopRegion) Draw.rect(topRegion, x, y);
            if(heat > 0.01f){
                Draw.blend(Blending.additive);
                Draw.color(heatColor, heat * Mathf.absin(9f, 1f));
                Draw.rect(heatRegion, x, y);
                Draw.blend();
            }
            Draw.color();
        }
    }
}
