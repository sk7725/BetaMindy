package betamindy.world.blocks.distribution;

import arc.audio.Sound;
import arc.graphics.g2d.*;
import arc.math.geom.Geometry;
import arc.util.*;
import betamindy.content.MindySounds;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.world.*;
import betamindy.world.blocks.distribution.PistonArm.*;
import mindustry.world.blocks.payloads.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class Piston extends Block {
    public Block armBlock;
    public TextureRegion armRegion, shaftRegion;
    public TextureRegion[] baseRegion = new TextureRegion[4];
    public boolean sticky;
    public Sound pushSound = MindySounds.pistonPush, pullSound = MindySounds.pistonPull;
    public static final float extendTicks = 8f;

    public Piston(String name){
        super(name);
        armBlock = new PistonArm(this);

        solid = true;
        update = true;
        hasPower = true;
        rotate = true;
        quickRotate = false;

        category = Category.distribution;//TODO: Make own category for place-overs
    }

    @Override
    public void load(){
        super.load();
        armRegion = atlas.find(name + "-arm");
        shaftRegion = atlas.find(name + "-shaft", "betamindy-piston-shaft");
        for(int i = 0; i<4; i++){
            baseRegion[i] = atlas.find(name + "-" + i, "betamindy-piston-" + i);
        }
    }

    public class PistonBuild extends Building {
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

        public boolean canPush(Block block){
            if(!block.update || !block.synthetic()) return false;
            if(block instanceof PistonArm) return false;
            return true;//TODO: unmovable blocks
        }
        public boolean canPull(Block block){
            return canPush(block) && true;//TODO: slippery blocks
        }

        /** Tries to push a single given building*/
        public void pushBuild(Building tile, int r){
            Tile e = tile.tile.nearby(r);
            //if(e.block() != Blocks.air) return;


            //tile.pickedUp();
            int rot = tile.rotation;
            tile.tile.remove();
            BuildPayload pay = new BuildPayload(tile);
            pay.place(e, rot);
        }
        public void pullBuild(Building tile, int r){
            pushBuild(tile, (r + 2) % 4);
        }

        /** Tries to push blocks and returns its success*/
        public boolean push(){
            //TODO: temp
            if(tile.nearbyBuild(rotation) == null) return true;
            pushBuild(tile.nearbyBuild(rotation), rotation);
            return true;
        }
        /** Tries to pull blocks and returns its success*/
        public boolean pull(){
            //TODO: temp
            if(tile.nearby(rotation) == null || tile.nearby(rotation).nearbyBuild(rotation) == null) return true;
            if(tile.nearby(rotation).block() == armBlock) tile.nearby(rotation).remove();
            pullBuild(tile.nearby(rotation).nearbyBuild(rotation), rotation);
            return true;
        }

        public void extendArm(){
            extended = push();
            if(extended){
                heatTimer.reset(0, 0);
                tile.nearby(rotation).setBlock(armBlock, team, (rotation + 2) % 4);
                ((PistonArmBuild)tile.nearbyBuild(rotation)).piston = this;
                pushSound.at(this);
            }
        }
        public void retract(){
            if(sticky) extended = !pull();
            else extended = false;
            if(!extended){
                heatTimer.reset(0, 0);
                if(tile.nearby(rotation) != null && tile.nearby(rotation).block() == armBlock) tile.nearby(rotation).remove();
                pullSound.at(this);
            }
        }

        @Override
        public void updateTile(){
            if(extended){
                if(!consValid() && canRetract()) retract();
            }
            else{
                if(consValid() && canExtend()) extendArm();
            }
        }

        @Override
        public void draw(){
            float heat = Math.min(extendTicks, heatTimer.getTime(0)) / extendTicks;
            if(!extended) heat = 1f - heat;
            if(heat > 0.5f) Draw.rect(shaftRegion, x + heat * Geometry.d4x[rotation] * tilesize, y + heat * Geometry.d4y[rotation] * tilesize, rotation * 90f);
            Draw.rect(armRegion, x + heat * Geometry.d4x[rotation] * tilesize, y + heat * Geometry.d4y[rotation] * tilesize, rotation * 90f);
            Draw.rect(baseRegion[rotation], x, y);
        }

        //TODO: r/w heatTimer & extended
    }
}