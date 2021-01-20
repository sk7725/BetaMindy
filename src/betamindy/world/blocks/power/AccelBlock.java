package betamindy.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Geometry;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;

public class AccelBlock extends PowerBlock {
    public TextureRegion[] baseRegion = new TextureRegion[2];
    public TextureRegion ballRegion;
    public final float duration = 9f;
    public float powerProduction = 2f;

    public AccelBlock(String name){
        super(name);

        rotate = true;
        quickRotate = false;
        consumesPower = false;
        outputsPower = true;
        group = BlockGroup.transportation;

        flags = EnumSet.of(BlockFlag.generator);
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 2; i++){
            baseRegion[i] = atlas.find(name + "-" + i);
        }
        ballRegion = atlas.find(name + "-ball");
    }

    public class AccelBuild extends Building {
        public float heat = 0f;
        public int prev = 0;
        private boolean lastback;

        @Override
        public void draw(){
            Draw.rect(baseRegion[rotation % 2], x, y);
            if(Core.settings.getBool("accelballs")){
                int back = lastback ? 2 : 0;
                float off = Mathf.clamp(heat / duration);
                Draw.rect(ballRegion, x + Geometry.d4x[rotation % 2 + back] * 3f * off, y + Geometry.d4y[rotation % 2 + back] * 3f * off);
            }
            else Draw.rect(ballRegion, x, y);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(heat > 0f) heat -= delta();

            int current = (rotation % 2 == 0) ? tile.x : tile.y;
            if(prev != current){
                lastback = prev < current;
                prev = current;

                heat = duration;
            }
        }

        @Override
        public float getPowerProduction(){
            return heat > 0.001f ? powerProduction : 0f;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(prev);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            prev = read.i();
        }

        public void initPos(Tile tile, boolean hor){
            prev = hor ? tile.x : tile.y;
        }

        @Override
        public void placed(){
            super.placed();
            initPos(tile, rotation % 2 == 0);
            heat = 0f;
        }
    }
}