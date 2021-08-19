package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.io.*;
import betamindy.world.blocks.distribution.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static mindustry.Vars.world;

public class Pen extends Block {
    public int drawLength = 40;
    public TextureRegion topRegion;

    public Pen(String name){
        super(name);
        update = solid = true;
        expanded = true;
        noUpdateDisabled = false;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    public class PenBuild extends Building implements SpinDraw, SpinUpdate{
        public Trail trail = new Trail(drawLength);
        public int color = Color.white.rgba();
        public float stroke = 1f;
        public boolean glow = true;

        public int prev = -1;

        @Override
        public void updateTile(){
            if(prev != tile.pos()){
                if(enabled) trail.update(x, y);
                prev = tile.pos();
            }

            if(enabled){
                proximity.each(b -> {
                    if(b instanceof PenModifier pmod) pmod.handlePen(this);
                });
            }
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            Building n = world.buildWorld(x, y);
            boolean dis = false;
            if(n != null){
                if(n.block instanceof Disabler) dis = true;
                else if(n instanceof PenModifier pmod) pmod.handlePen(this);
            }
            if(!dis) trail.update(sx, sy);
            prev = -1;
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
            write.bool(glow);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            color = read.i();
            stroke = read.f();
            glow = read.bool();
        }
    }
}
