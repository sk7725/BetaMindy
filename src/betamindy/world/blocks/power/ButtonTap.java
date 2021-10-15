package betamindy.world.blocks.power;

import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.meta.*;

import static arc.Core.atlas;

public class ButtonTap extends PowerBlock {
    public float tapTime = 16f;
    public float powerProduction = 2f;
    public TextureRegion trigRegion;

    public ButtonTap(String name){
        super(name);
        quickRotate = false;
        consumesPower = false;
        outputsPower = true;
        targetable = false;
        solid = true;
        sync = true;
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
        public void draw(){
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
            return switch(sensor){
                case heat -> heat / tapTime;
                case enabled -> heat > 0.001f ? 1 : 0;
                default -> super.sense(sensor);
            };
        }
    }
}
