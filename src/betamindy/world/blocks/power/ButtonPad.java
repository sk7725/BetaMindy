package betamindy.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;

public class ButtonPad extends PowerBlock {
    public float pushTime = 16f;
    public float basicPowerProduction = 2f;
    public boolean detectAir = false;

    public ButtonPad(String name){
        super(name);
        quickRotate = false;
        consumesPower = false;
        outputsPower = true;
        targetable = false;
        solid = false;
        sync = true;
        expanded = true;
        group = BlockGroup.transportation;

        flags = EnumSet.of(BlockFlag.generator);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.basePowerGeneration, basicPowerProduction * 60.0f, StatUnit.powerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        if(hasPower && outputsPower && !consumes.hasPower()){
            bars.add("power", (ButtonPadBuild entity) -> new Bar(
                () -> Core.bundle.format("bar.poweroutput", entity.getPowerProduction() * 60 * entity.timeScale()),
                () -> Pal.powerBar,
                () -> entity.heat/pushTime)
            );
        }
    }

    public class ButtonPadBuild extends Building {
        public float heat = 0f;
        public float powerProduction;

        @Override
        public void draw() {
            super.draw();
            Draw.rect(atlas.find(name + (heat > 0.001f ? "-trig":"")), x, y);
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(heat >= 0.001f) heat -= edelta();

            if(detectAir) Units.nearby(x, y, size, size, this::unitOn);
        }

        @Override
        public void unitOn(Unit unit){
            powerProduction = basicPowerProduction * (unit.hitSize * 1.4f - 8);

            if(heat <= 0.001) Sounds.place.at(x, y, 1.2f);
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
            if(sensor == LAccess.heat) return heat;
            return super.sense(sensor);
        }
    }
}
