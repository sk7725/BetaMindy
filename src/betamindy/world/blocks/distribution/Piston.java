package betamindy.world.blocks.distribution;

import arc.audio.Sound;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Geometry;
import arc.util.*;
import betamindy.contents.MindySounds;
import mindustry.gen.Building;
import mindustry.world.*;
import betamindy.world.blocks.distribution.PistonArm.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class Piston extends Block {
    public PistonArm armBlock;
    public TextureRegion armRegion, shaftRegion;
    public TextureRegion[] baseRegion = new TextureRegion[4];
    public boolean sticky;
    public Sound pushSound = MindySounds.pistonPush, pullSound = MindySounds.pistonPull;

    public Piston(String name){
        super(name);
        armBlock = new PistonArm(this);

        solid = true;
        update = true;
        hasPower = true;
        rotate = true;
        quickRotate = false;
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
        public @Nullable PistonArmBuild arm;
        public float heat; //takes 8 ticks to extend

        public boolean canExtend(){
            return true;
        }
        public boolean canRetract(){
            return true;
        }

        /** Tries to push blocks and returns its success*/
        public boolean push(){
            return true;
        }
        /** Tries to pull blocks and returns its success*/
        public boolean pull(){
            return true;
        }

        public void extendArm(){
            extended = push();
            if(extended) pushSound.at(this);
        }
        public void retract(){
            if(sticky) extended = !pull();
            else extended = false;
            if(!extended) pullSound.at(this);
        }

        @Override
        public void updateTile(){
            heat = Mathf.lerpDelta(heat, extended ? 1f : 0f, 0.25f);

            if(extended){
                if(!consValid() && canRetract()) retract();
            }
            else{
                if(consValid() && canExtend()) extendArm();
            }
        }

        @Override
        public void draw(){
            if(heat > 0.5f) Draw.rect(shaftRegion, x + heat * Geometry.d4x[rotation] * tilesize, y + heat * Geometry.d4y[rotation] * tilesize, rotation * 90f);
            Draw.rect(armRegion, x + heat * Geometry.d4x[rotation] * tilesize, y + heat * Geometry.d4y[rotation] * tilesize, rotation * 90f);
            Draw.rect(baseRegion[rotation], x, y);
        }

        //TODO: r/w heat & extended
    }
}