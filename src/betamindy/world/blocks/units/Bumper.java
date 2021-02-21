package betamindy.world.blocks.units;

import arc.audio.Sound;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class Bumper extends Wall {
    public float hitSize = tilesize * 1.1f;
    public final Rect rect = new Rect(), rect2 = new Rect();

    public float bumpTime = 16f;
    public float bumpScl = 0.8f;
    public float bumpSpeedLimit = 5f;

    public Sound bumpSound = Sounds.artillery;

    public TextureRegion topRegion;

    public Bumper(String name){
        super(name);

        update = true;
        solid = true;
        deflectSound = Sounds.artillery;
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
            //Log.info(1);
            if(heat < 0.001f) bumpSound.at(x, y);

            heat = bumpTime;

            float penX = Math.abs(x - unit.x), penY = Math.abs(y - unit.y);

            Vec2 position = Geometry.raycastRect(
                unit.x - unit.vel.x * Time.delta,
                unit.y - unit.vel.y * Time.delta,
                unit.x + unit.vel.x * Time.delta,
                unit.y + unit.vel.y * Time.delta,
                rect.setSize(size * hitSize + rect2.width * 2 + rect2.height * 2).setCenter(x, y)
            );

            if(position != null) unit.set(position.x, position.y);


            if(penX > penY) unit.vel.x *= -1;
            else unit.vel.y *= -1;

            if(unit.vel.len() < bumpSpeedLimit){
                Vec2 avec = new Vec2(unit.x - x,unit.y - y);
                avec.scl(bumpScl,bumpScl);
                unit.vel.add(avec.x * Time.delta,avec.y * Time.delta);
            }
        }

        public void updateTile(){
            super.updateTile();
            Units.nearby(x - size * hitSize / 2, y - size * hitSize / 2, hitSize * size, hitSize * size, this::unitPush);

            if(heat > 0f) heat -= delta();
        }

        @Override
        public void draw(){
            float scl = 1f + Mathf.clamp(heat / bumpTime) * 0.3f;
            Draw.rect(region, x, y, Draw.scl * Draw.xscl * size * 32f * scl, Draw.scl * Draw.yscl * size * 32f * scl);
            Draw.rect(topRegion, x, y);
        }
    }
}
