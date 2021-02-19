package betamindy.world.blocks.power;

import arc.graphics.g2d.*;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import betamindy.world.blocks.production.*;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.logic.*;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;

import static arc.Core.atlas;

//TODO heal shar
public class ButtonTap extends PowerBlock {
    public float tapTime = 16f;
    public float powerProduction = 2 * 60f;
    public TextureRegion trigRegion;

    public ButtonTap(String name){
        super(name);
        quickRotate = false;
        consumesPower = false;
        outputsPower = true;
        targetable = false;
        solid = true;
        sync = true;
        expanded = true;
        configurable = true;
        saveConfig = false;
        noUpdateDisabled = false;
        group = BlockGroup.power;

        flags = EnumSet.of(BlockFlag.generator);

        config(Boolean.class, (ButtonTapBuild build, Boolean b) -> {
            if(b && build.enabled && !build.pressed()) build.heat = tapTime;
        });
    }

    @Override
    public void load() {
        super.load();
        trigRegion = atlas.find(name + "-trig");
    }

    public class ButtonTapBuild extends Building {
        public float heat = 0f;

        @Override
        public void draw() {
            super.draw();
            Draw.rect(pressed() ? trigRegion : region, x, y);
        }

        public boolean pressed(){
            return heat > 0.001f;
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(heat >= 0f) heat -= delta();
        }

        @Override
        public float getPowerProduction(){
            return heat > 0.001f ? powerProduction : 0f;
        }

        @Override
        public boolean configTapped(){
            if(!enabled || pressed()) return false;

            //어떤 약을 빠셨나요 그것이 궁금합니다 // 샤로인요
            configure(true);
            return false;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(read.bool()) heat = tapTime;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(pressed());
        }

        @Override
        public double sense(LAccess sensor){
            switch(sensor){
                case heat: return heat / tapTime;
                case enabled: return heat > 0.001f ? 1 : 0;
                default: return super.sense(sensor);
            }
        }
    }
}
