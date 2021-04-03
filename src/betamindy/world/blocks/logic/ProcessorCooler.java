package betamindy.world.blocks.logic;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import betamindy.util.Useful;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.consumers.ConsumeType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.Core.atlas;

public class ProcessorCooler extends Block {

    public TextureRegion heatRegion, liquidRegion, topRegion;
    public boolean useTopRegion = false; //set automatically
    public Color heatColor = Pal.turretHeat;
    public boolean acceptCoolant = false;
    public int boost = 2;
    public int maxProcessors = 2;

    /**
     * The block will assume this to be the best coolant it will ever get. Used for the bar only and does not affect gameplay.
     */
    public Liquid maxCoolantConsidered = Useful.getBestCoolant();
    /**
     * Set by maxCoolantConsidered. Again, this is visual only.
     */
    public int maxBoostBar;

    public ProcessorCooler(String name) {
        super(name);

        update = true;
        solid = true;
        rotate = false;
    }

    @Override
    public void init() {
        if(acceptCoolant && !consumes.has(ConsumeType.liquid)) {
            hasLiquids = true;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 3.5f));
        }

        super.init();
        maxBoostBar = acceptCoolant ? Mathf.round((1 + maxCoolantConsidered.heatCapacity) * boost) : boost;
    }

    @Override
    public void load() {
        super.load();
        heatRegion = atlas.find(name + "-heat", region);
        if(consumes.has(ConsumeType.liquid)) {
            liquidRegion = atlas.find(name + "-liquid");
        }
        topRegion = atlas.find(name + "-top");
        useTopRegion = atlas.isFound(topRegion);
    }

    @Override
    public TextureRegion[] icons() {
        if(useTopRegion) return new TextureRegion[] { region, topRegion };
        return super.icons();
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.output, "[orange]@[] \uF7E4", maxProcessors);
        stats.add(Stat.speedIncrease, boost * 100, StatUnit.percent);
    }

    @Override
    public void setBars() {
        super.setBars();
        bars.add("boost", (ProcessorCoolerBuild entity) -> new Bar(() -> Core.bundle.format("bar.boost", entity.realBoost() * 100), () -> Pal.accent, () -> (float) entity.realBoost() / maxBoostBar));
        bars.add("links", (ProcessorCoolerBuild entity) -> new Bar(() -> Core.bundle.format("bar.coolprocs", entity.usedLinks, maxProcessors), () -> Pal.ammo, () -> entity.heat));
    }

    public class ProcessorCoolerBuild extends Building {
        public float heat = 0;
        public int usedLinks = 0;

        public int realBoost() {
            if(enabled && cons.valid() && efficiency() > 0.8f) {
                if(acceptCoolant) {
                    Liquid liquid = liquids.current();
                    return Math.max(1, Mathf.round((1f + liquid.heatCapacity) * boost));
                }
                return boost;
            }
            return 1;
        }

        @Override
        public void updateTile() {
            int count = 0;

            int boost = realBoost();

            if(boost >= 2) {
                for(Building building : proximity) {
                    if(count >= maxProcessors) {
                        break;
                    }
                    if(building instanceof LogicBlock.LogicBuild) {
                        for(int i = 0; i < boost - 1; i++) {
                            building.updateTile();
                        }
                        count++;
                    }
                }
            }

            usedLinks = count;
            heat = Mathf.lerpDelta(heat, Mathf.clamp((float) count / maxProcessors), 0.03f);
        }

        @Override
        public void draw() {
            super.draw();
            if(liquids != null) {
                if(liquids.total() > 0.01f) {
                    Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
                }
            }
            if(useTopRegion) Draw.rect(topRegion, x, y);
            if(heat > 0.01f) {
                Draw.blend(Blending.additive);
                Draw.color(heatColor, heat * Mathf.absin(9f, 1f));
                Draw.rect(heatRegion, x, y);
                Draw.blend();
            }
            Draw.color();
        }
    }
}
