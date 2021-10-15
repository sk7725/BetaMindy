package betamindy.world.blocks.units;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.io.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;

import static mindustry.Vars.*;

public class BumperBlue extends Bumper {
    public float reflateTime = 240f;
    public float animTime = 30f;
    public Effect deflateEffect = MindyFx.blueBumperBonk;

    public BumperBlue(String name){
        super(name);

        solid = false;
        solidifes = true;
        canOverdrive = true;
        ignoreHeat = true;
        sync = true;
    }

    public class BumperBlueBuild extends BumperBuild {
        public boolean open = false;
        public float progress = 0f;

        public void deflate(){
            deflate(0f);
        }

        public void deflate(float r){
            open = true;
            deflateEffect.at(this, r);
            progress = 0f;
            pathfinder.updateTile(tile());
        }

        @Override
        public void updateTile(){
            if(!open) super.updateTile();
            else{
                progress += edelta();
                if(progress >= reflateTime - animTime && Units.anyEntities(tile)) progress = reflateTime - animTime - 10f;
                if(progress >= reflateTime){
                    open = false;
                    pathfinder.updateTile(tile());
                    heat = bumpTime;
                }
            }
        }

        @Override
        public void unitPush(Unit unit){
            super.unitPush(unit);
            if(!open) deflate(Angles.angle(unit.x, unit.y, x, y));
        }

        @Override
        public void draw(){
            if(open){
                float scl = (progress - (reflateTime - animTime)) / animTime;
                if(scl > 0.01f) drawFloat(Mathf.clamp(scl));
                else{
                    Draw.z(Layer.block - 0.99f); //block shadow is block - 1
                    Drawf.shadow(x, y, (size * 16f + 1f) * 0.7f);
                    Draw.z(Layer.blockOver);
                    Draw.rect(topRegion, x, y);
                }
            }
            else super.draw();
        }

        @Override
        public boolean checkSolid(){
            return !open;
        }

        @Override
        public double sense(LAccess sensor){
            switch(sensor){
                case heat: return open ? (double) Mathf.clamp(progress / reflateTime) : 1;
                case enabled: return !open ? 1 : 0;
                default: return super.sense(sensor);
            }
        }

        @Override
        public void write(Writes w){
            super.write(w);
            w.bool(open);
            if(open) w.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            open = read.bool();
            if(open) progress = read.f();
        }
    }
}
