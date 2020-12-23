package betamindy.world.blocks.power;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Geometry;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.Category;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;

public class AccelBlock extends PowerBlock {
    public TextureRegion[] baseRegion = new TextureRegion[2];
    public TextureRegion ballRegion;
    public final float ballTicks = 8f;
    public float powerProduction = 8f;

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
        public Interval ballTimer = new Interval(2);
        public final int ballid = 0, powercheckid = 1;
        public int prev = 0;
        private boolean lastback, lastpower;

        @Override
        public void draw(){
            Draw.rect(baseRegion[rotation % 2], x, y);
            int back = lastback ? 2 : 0;
            float off = Mathf.clamp((ballTicks - ballTimer.getTime(ballid)) / ballTicks);
            Draw.rect(ballRegion, x + Geometry.d4x[rotation % 2 + back] * 3f * off, y + Geometry.d4y[rotation % 2 + back] * 3f * off);
        }

        public boolean wasMoved(int n){
            int current = (n == 0) ? tile.x : tile.y;
            if(prev == current) return false;
            else{
                lastback = prev < current;
                prev = current;

                ballTimer.reset(ballid, 0);
                return true;
            }
        }

        @Override
        public float getPowerProduction(){
            if(ballTimer.getTime(powercheckid) <= 0f) return lastpower ? powerProduction : 0f;
            else{
                lastpower = wasMoved(rotation % 2);
                ballTimer.reset(powercheckid, 0);
                return lastpower ? powerProduction : 0f;
            }
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
            ballTimer.reset(ballid, ballTicks + 1f);
            ballTimer.reset(powercheckid, 1f);
        }
    }
}