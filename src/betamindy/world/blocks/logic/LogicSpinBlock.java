package betamindy.world.blocks.logic;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.graphics.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

/** Can be placed in 4 directions via the player, and rotated a full 360 degrees via processors. A base block for other blocks, do not use directly!*/
public class LogicSpinBlock extends Block {
    //each lighting sprite
    public TextureRegion[] regions = new TextureRegion[4];
    public TextureRegion baseRegion;
    public boolean outlineIcons = true;
    public float lineLength = 5;

    public float rotateSpeed = 5f;

    public LogicSpinBlock(String name){
        super(name);

        update = true;
        solid = true;
        rotate = true;
        outlineIcon = true;
        noUpdateDisabled = false;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 4; i++){
            regions[i] = atlas.find(name + "-" + i);
        }
        baseRegion = atlas.find(name + "-base");
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);
        if(outlineIcons) Drawm.outlineRegion(packer, regions, outlineColor, name);
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Lines.stroke(2f, Pal.accent);
        Lines.dashLine(
            x * tilesize + offset + Geometry.d4[rotation].x * (tilesize / 2f + 2),
            y * tilesize + offset + Geometry.d4[rotation].y * (tilesize / 2f + 2),
            x * tilesize + offset + Geometry.d4[rotation].x * (lineLength + 0.5f) * tilesize,
            y * tilesize + offset + Geometry.d4[rotation].y * (lineLength + 0.5f) * tilesize,
            (int)lineLength);

        Draw.reset();
    }

    public class LogicSpinBuild extends Building {
        public float r = 0f;

        //only valid when targetSet = true
        public float targetRot = 0f;
        public boolean targetSet = false;

        public float realRotation(){
            return r + rotation * 90f;
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.z(Layer.turret);
            Drawm.spinSprite(regions, x, y, realRotation());
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Lines.stroke(2f, Pal.accent);
            Tmp.v1.trns(realRotation(), tilesize * (lineLength + 0.5f));
            Tmp.v2.trns(realRotation(), tilesize / 2f + 2);
            Lines.dashLine(x + Tmp.v2.x, y + Tmp.v2.y, x + Tmp.v1.x, y + Tmp.v1.y, (int)lineLength);
            Draw.reset();
        }

        @Override
        public void updateTile(){
            turnToTarget();
        }

        @Override
        public double sense(LAccess sensor){
            switch(sensor){
                case rotation: return realRotation();
                case controlled: return targetSet ? 1 : 0;
                default: return super.sense(sensor);
            }
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.shoot){
                targetRot = Angles.angle(World.unconv((float) p1) - x, World.unconv((float) p2) - y);
                targetSet = true;
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4){
            if(type == LAccess.shootp){
                if(p1 instanceof Posc){
                    Position t = (Posc)p1;
                    targetRot = Angles.angle(t.getX() - x, t.getY() - y);
                    targetSet = true;
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        public void turnToTarget(){
            if(!targetSet) return;
            float cur = realRotation();
            float tar;
            if(Angles.near(cur, targetRot, 0.01f)){
                tar = targetRot;
                targetSet = false;
            }
            else{
                tar = Angles.moveToward(cur, targetRot, rotateSpeed * edelta());
            }
            setRot(tar);
        }

        public void setRot(float target){
            r = target - rotation * 90f;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(r);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            r = read.f();
        }
    }
}
