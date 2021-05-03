package betamindy.world.blocks.distribution;

import arc.audio.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.util.xelo.*;
import betamindy.world.blocks.distribution.PistonArm.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class Piston extends Block{
    public static final float extendTicks = 8f;
    public final Boolf<Building> pushBool = b -> !(b.block == Blocks.thoriumWall || b.block == Blocks.thoriumWallLarge);
    public final Boolf<Building> stickBool = b -> !(b.block == Blocks.phaseWall || b.block == Blocks.phaseWallLarge);
    public boolean sticky;
    public int maxBlocks = 16;
    public Block armBlock;
    public TextureRegion armRegion, shaftRegion;
    public TextureRegion[] baseRegion = new TextureRegion[4];
    public Sound pushSound = MindySounds.pistonPush, pullSound = MindySounds.pistonPull;
    public String armSprite, baseSprite;
    public float pushVolume = 0.4f;

    public Piston(String name, String arm, String base){
        super(name);
        armBlock = new PistonArm(this);

        solid = true;
        update = true;
        hasPower = true;
        rotate = true;
        quickRotate = false;

        group = BlockGroup.transportation;//TODO: Make own category for place-overs

        armSprite = arm;
        baseSprite = base;
    }

    public Piston(String name){
        this(name, "", "");
        armSprite = this.name + "-arm";
        baseSprite = this.name;
    }

    @Override
    public void load(){
        super.load();
        armRegion = atlas.find(armSprite);
        shaftRegion = atlas.find(name + "-shaft", "betamindy-piston-shaft");
        for(int i = 0; i < 4; i++){
            baseRegion[i] = atlas.find(baseSprite + "-" + i, "betamindy-piston-" + i);
        }
    }

    public class PistonBuild extends Building{
        public boolean extended;
        //public float heat; //takes 8 ticks to extend
        protected Interval heatTimer = new Interval(1);

        public boolean isIdle(){
            return heatTimer.check(0, extendTicks);
        }

        public boolean canExtend(){
            return isIdle() && tile.nearby(rotation) != null;
        }

        public boolean canRetract(){
            return isIdle() && tile.nearby(rotation) != null;
        }

        /**
         * Tries to push blocks and returns its success
         */
        public boolean push(){
            if(tile.nearby(rotation) == null) return false;
            if(tile.nearbyBuild(rotation) == null) return tile.nearby(rotation).block() == Blocks.air;
            if(!pushBool.get(tile.nearbyBuild(rotation))) return false;

            return XeloUtil.pushBlock(tile.nearbyBuild(rotation), rotation, maxBlocks, b -> (b != this && pushBool.get(b)), stickBool);
        }

        /**
         * Tries to pull blocks and returns its success
         */
        public boolean pull(){
            if(tile.nearby(rotation) == null || tile.nearby(rotation).nearbyBuild(rotation) == null) return true;
            if(tile.nearby(rotation).block() == armBlock) tile.nearby(rotation).remove();
            Building pullb = tile.nearby(rotation).nearbyBuild(rotation);
            if(!stickBool.get(pullb) || !pushBool.get(pullb)) return true;
            //does not care if it actually succeeds to push
            XeloUtil.pushBlock(pullb, (rotation + 2) % 4, maxBlocks, b -> (b != this && pushBool.get(b)), stickBool);
            return true;
        }

        public void extendArm(){
            extended = push();
            if(extended){
                heatTimer.reset(0, 0);
                XeloUtil.pushUnits(this, rotation);
                tile.nearby(rotation).setBlock(armBlock, team, (rotation + 2) % 4);
                ((PistonArmBuild)tile.nearbyBuild(rotation)).piston = this;
                pushSound.at(x, y, 1f, pushVolume);
            }
        }

        public void retract(){
            if(sticky) extended = !pull();
            else extended = false;
            if(!extended){
                heatTimer.reset(0, 0);
                if(tile.nearby(rotation) != null && tile.nearby(rotation).block() == armBlock)
                    tile.nearby(rotation).remove();
                pullSound.at(x, y, 1f, pushVolume);
            }
        }

        @Override
        public void updateTile(){
            if(extended){
                if(!consValid() && canRetract()) retract();
            }else{
                if(consValid() && canExtend()) extendArm();
            }
        }

        @Override
        public void draw(){
            float heat = Math.min(extendTicks, heatTimer.getTime(0)) / extendTicks;
            if(!extended) heat = 1f - heat;
            if(heat > 0.5f)
                Draw.rect(shaftRegion, x + heat * Geometry.d4x[rotation] * tilesize, y + heat * Geometry.d4y[rotation] * tilesize, rotation * 90f);
            Draw.rect(armRegion, x + heat * Geometry.d4x[rotation] * tilesize, y + heat * Geometry.d4y[rotation] * tilesize, rotation * 90f);
            Draw.rect(baseRegion[rotation], x, y);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read);
            extended = read.bool();
            heatTimer.reset(0, read.b());
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(extended);
            write.b((byte)Mathf.clamp(heatTimer.getTime(0), 0, 8));
        }

        @Override
        public boolean conductsTo(Building other){
            return tile.nearbyBuild(rotation) != other && super.conductsTo(other);
        }

    }

}
