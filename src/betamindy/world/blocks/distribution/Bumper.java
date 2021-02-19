package betamindy.world.blocks.distribution;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class Bumper extends SlimeBlock {
    public float slimeSize = tilesize * 1.9f;
    public Rect rect, rect2;
    public float bumpTime = 16;
    public float bumpScl = 1.5f;
    public float bumpSpeedLimit = 15;
    public Sound bumpSound = Sounds.artillery;

    /** Bullet deflection chance. -1 to disable */
    public float chanceDeflect = -1f;
    public Sound deflectSound = Sounds.artillery;

    public Bumper(String name, int stype){
        super(name, stype);
    }

    @Override
    public void load(){
        super.load();
        rect = new Rect();
        rect2 = new Rect();
    }

    @Override
    public void setStats() {
        super.setStats();

        if(chanceDeflect > 0f) stats.add(Stat.baseDeflectChance, chanceDeflect, StatUnit.none);
    }

    public class BumperBuild extends SlimeBuild {
        public float heat;
        public float hit;

        @Override
        public void placed() {
            super.placed();
            heat = bumpTime;
        }

        public void unitOn(Unit unit){
            Log.info(1);
            if(heat < 0.001f) bumpSound.at(x, y,2.5f);

            heat = bumpTime;

            float penX = Math.abs(x - unit.x), penY = Math.abs(y - unit.y);

            Vec2 position = Geometry.raycastRect(
                unit.x - unit.vel.x * Time.delta,
                unit.y - unit.vel.y * Time.delta,
                unit.x + unit.vel.x * Time.delta,
                unit.y + unit.vel.y * Time.delta,
                rect.setSize(size * slimeSize + rect2.width * 2 + rect2.height * 2).setCenter(x, y)
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
            Units.nearby(x - size * (slimeSize - tilesize) / 2, y - size * (slimeSize - tilesize) / 2, slimeSize, slimeSize, this::unitOn);

            heat -= edelta();
        }

        @Override
        public boolean collision(Bullet bullet){
            super.collision(bullet);

            hit = 1f;

            if(chanceDeflect > 0f){
                if(bullet.vel().len() <= 0.1f || !bullet.type.reflectable) return true;
                if(!Mathf.chance(chanceDeflect / bullet.damage())) return true;

                deflectSound.at(x, y,2.5f);

                bullet.trns(-bullet.vel.x, -bullet.vel.y);

                float penX = Math.abs(x - bullet.x), penY = Math.abs(y - bullet.y);

                if(penX > penY) bullet.vel.x *= -1;
                else bullet.vel.y *= -1;

                bullet.owner(this);
                bullet.team(team);
                bullet.time(bullet.time() + 1f);

                return false;
            }

            return true;
        }
    }
}
