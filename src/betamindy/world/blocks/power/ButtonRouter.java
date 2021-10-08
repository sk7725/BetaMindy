package betamindy.world.blocks.power;

import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;

public class ButtonRouter extends Router {
    public float duration = 10f;
    public float powerProduction = 0.5f;
    public TextureRegion trigRegion;

    public ButtonRouter(String name){
        super(name);
        hasPower = true;
        consumesPower = false;
        outputsPower = true;

        group = BlockGroup.transportation;
        flags = EnumSet.of(BlockFlag.generator);
    }

    @Override
    public void load(){
        super.load();
        trigRegion = atlas.find(name + "-trig");
    }

    public class ButtonRouterBuild extends RouterBuild {
        public float heat;

        @Override
        public void updateTile(){
            super.updateTile();
            if(heat >= 0f) heat -= delta();
        }

        @Override
        public void handleItem(Building source, Item item){
            super.handleItem(source, item);
            heat = duration;
        }

        @Override
        public void draw(){
            Draw.rect(pressed() ? trigRegion : region, x, y);
        }

        public boolean pressed(){
            return heat > 0.001f;
        }

        @Override
        public float getPowerProduction(){
            return heat > 0.001f ? powerProduction : 0f;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(read.bool()) heat = duration;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(pressed());
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case heat -> heat / duration;
                case enabled -> heat > 0.001f ? 1 : 0;
                default -> super.sense(sensor);
            };
        }
    }
}
