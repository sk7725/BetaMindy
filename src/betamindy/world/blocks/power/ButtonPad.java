package betamindy.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class ButtonPad extends PowerBlock {
    public float pushTime = 16f;
    public float basicPowerProduction = 2f;
    public boolean detectAir = false;
    public TextureRegion trigRegion;

    public ButtonPad(String name){
        super(name);
        rotate = false;
        consumesPower = false;
        outputsPower = true;
        targetable = false;
        solid = false;
        sync = true;
        group = BlockGroup.power;
        noUpdateDisabled = false;

        flags = EnumSet.of(BlockFlag.generator);
    }

    @Override
    public void load() {
        super.load();
        trigRegion = atlas.find(name + "-trig");
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.basePowerGeneration, basicPowerProduction * 60.0f, StatUnit.powerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        if(hasPower && outputsPower && consPower == null){
            addBar("power", (ButtonPadBuild entity) -> new Bar(
                () -> Core.bundle.format("bar.poweroutput", entity.getPowerProduction() * 60 * entity.timeScale()),
                () -> Pal.powerBar,
                () -> entity.heat / pushTime
            ));
        }
    }

    public class ButtonPadBuild extends Building {
        public float heat = 0f;
        public float powerProduction;

        @Override
        public void draw() {
            super.draw();
            Draw.rect(heat > 0.001f ? trigRegion : region, x, y);
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(heat >= 0f) heat -= delta();

            if(detectAir && enabled) Units.nearby(x - size * tilesize / 2f, y - size * tilesize / 2f, size * tilesize, size * tilesize, this::unitOn);
        }

        @Override
        public void unitOn(Unit unit){
            if(!enabled) return;
            powerProduction = Mathf.clamp(basicPowerProduction * (unit.hitSize * 1.2f - 8) * 0.3f, 0.01f, 1f); //TODO: figure out how to deal with multiple units on one pad

            if(heat < 0.001f) Sounds.place.at(x, y, 1.2f / size);
            heat = pushTime;
        }

        @Override
        public float getPowerProduction(){
            return heat > 0.001f ? powerProduction : 0f;
        }

        @Override
        public void placed() {
            super.placed();
            heat = 0;
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case heat -> heat / pushTime;
                case enabled -> heat > 0.001f ? 1 : 0;
                default -> super.sense(sensor);
            };
        }
    }
}
