package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.world.blocks.distribution.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Pen extends Block {
    public int drawLength = 40;
    public TextureRegion topRegion;
    public boolean glow = false;

    public static final float despawnStart = 120f;

    public Pen(String name){
        super(name);
        update = solid = true;
        alwaysUpdateInUnits = true;
        noUpdateDisabled = false;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top", "betamindy-pen-top");
    }

    @Override
    public void init(){
        super.init();

        updateClipRadius(drawLength * tilesize);
    }

    public class PenBuild extends Building implements SpinDraw, SpinUpdate {
        public Trail trail = new Trail(drawLength);
        public int color = Color.white.rgba();
        public float stroke = 1f;
        public boolean carried = false;

        public int prev = -1;
        public float px, py;

        private boolean spinning = false;
        private float lastUpdated;

        @Override
        public void updateTile(){
            if(isPayload()){

                //TODO how does this work what am I supposed to do here? why does the ghost get removed? -Anuke
                if(spinning){
                    if(Time.time - lastUpdated > Time.delta + 1f) spinning = false;//this most likely means that the pen was removed with the spinner
                    return;
                }
                float margin = Time.time - lastUpdated - Time.delta - 1f;
                if(!(Mathf.equal(x, px, 0.1f) && Mathf.equal(y, py, 0.1f)) || margin > 0f) trail.update(x, y);
                if(margin > despawnStart + 30f){
                    //TODO why is the ghost supposed to be removed here
                    return;
                }
                px = x;
                py = y;
                prev = -1;

            }else{
                if(prev != tile.pos()){
                    if(enabled) trail.update(x, y);
                    prev = tile.pos();
                }

                if(enabled){
                    proximity.each(b -> {
                        if(b instanceof PenModifier pmod) pmod.handlePen(this);
                    });
                }
                carried = false;
                spinning = false;
            }
        }

        @Override
        public void pickedUp(){
            super.pickedUp();
            carried = true;
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            lastUpdated = Time.time;
        }

        @Override
        public void payloadDraw(){
            super.draw();
            //TODO this should call super.draw() but I'm not sure -Anuke
            //TODO doesn't update unit clip size!

            if(spinning) return;
            float lastZ = Draw.z();
            Draw.z(glow ? Layer.effect : Layer.effect + 5f);
            Draw.color(color);
            float margin = Time.time - lastUpdated - Time.delta - 1f;
            if(margin > despawnStart){
                trail.draw(Draw.getColor(), stroke * Mathf.clamp(1f - (margin - despawnStart) / 30f));
            }
            else{
                trail.draw(Draw.getColor(), stroke);
            }
            Draw.reset();
            Draw.z(lastZ);
        }

        @Override
        public void set(Position pos){ //called each frame by most blocks when it is a payload, thankfully
            super.set(pos);
            lastUpdated = Time.time;
            spinning = false;
        }

        @Override
        public void set(float x, float y){
            super.set(x, y);
            lastUpdated = Time.time;
            spinning = false;
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            Building n = world.buildWorld(sx, sy);
            boolean dis = false;
            if(n != null){
                if(n.block instanceof Disabler) dis = true;
                else if(n instanceof PenModifier pmod) pmod.handlePen(this);
            }
            if(!dis) trail.update(sx, sy);
            prev = -1;
            spinning = true;
            lastUpdated = Time.time;
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            Draw.rect(region, x, y, dr);
            float lastZ = Draw.z();
            Draw.z(glow ? Layer.effect : Layer.effect + 5f);
            Draw.color(color);
            if(enabled) Draw.rect(topRegion, x, y, dr);
            trail.draw(Draw.getColor(), stroke);
            Draw.reset();
            Draw.z(lastZ);
        }

        @Override
        public void draw(){
            drawSpinning(x, y, 0);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(color);
            write.f(stroke);
            write.bool(carried); //use later
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            color = read.i();
            stroke = read.f();
            carried = read.bool();
        }
    }
}
