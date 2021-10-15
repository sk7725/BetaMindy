package betamindy.world.blocks.units;

import arc.audio.Sound;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class Bumper extends Wall {
    public float hitSize = tilesize * 1.1f;
    public final Rect rect = new Rect();

    public float bumpTime = 16f;
    public float bumpScl = 0.5f;
    public float bumpSpeedLimit = 4f;

    public Sound bumpSound = MindySounds.boing;
    /** If heat should be disregarded when playing a sound. Used for blue bumper types. */
    public boolean ignoreHeat = false;

    public TextureRegion topRegion;

    public Bumper(String name){
        super(name);

        update = true;
        solid = true;
        deflectSound = MindySounds.boing;
        hasShadow = false;
        noUpdateDisabled = false;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    @Override
    public void setStats(){
        super.setStats();
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region, topRegion};
    }

    public class BumperBuild extends WallBuild {
        public float heat = 0f;

        public void unitPush(Unit unit){
            if(heat < 0.001f || ignoreHeat) bumpSound.at(x, y);

            heat = bumpTime;

            float penX = Math.abs(x - unit.x), penY = Math.abs(y - unit.y);

            /*
            Vec2 position = Geometry.raycastRect(
                unit.x - unit.vel.x * Time.delta,
                unit.y - unit.vel.y * Time.delta,
                unit.x + unit.vel.x * Time.delta,
                unit.y + unit.vel.y * Time.delta,
                rect.setSize(size * hitSize).setCenter(x, y)
            );

            if(position != null) unit.set(position.x, position.y);*/
            unit.move(-unit.vel.x, -unit.vel.y);


            if(penX > penY) unit.vel.x *= -1;
            else unit.vel.y *= -1;

            if(unit.vel.len() < bumpSpeedLimit){
                Tmp.v2.set(unit.x - x,unit.y - y);
                Tmp.v2.scl(bumpScl,bumpScl);
                unit.vel.add(Tmp.v2.x, Tmp.v2.y);
            }
        }

        public void updateTile(){
            Units.nearby(x - size * hitSize / 2, y - size * hitSize / 2, hitSize * size, hitSize * size, this::unitPush);

            if(heat > 0f) heat -= delta();
        }

        @Override
        public void draw(){
            float scl = 1f + Mathf.clamp(heat / bumpTime) * 0.2f;
            drawFloat(scl);
        }

        public void drawFloat(float scl){
            Draw.z(Layer.block - 0.99f); //block shadow is block - 1
            Drawf.shadow(x, y, (size * 16f + 1f) * Math.max(scl, 0.7f));
            Draw.z(Layer.blockOver + scl - 1f);
            Draw.rect(region, x, y, Draw.scl * Draw.xscl * size * 32f * scl, Draw.scl * Draw.yscl * size * 32f * scl);
            Draw.rect(topRegion, x, y);
        }
    }
}
