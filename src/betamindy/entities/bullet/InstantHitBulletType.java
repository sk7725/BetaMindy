package betamindy.entities.bullet;

import arc.math.geom.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class InstantHitBulletType extends BulletType {
    private static float cdist = 0f;
    private static Unit result;

    public float pierceDamage = 0f;
    public float percentDamage = 0f;
    public float hitRange = 1f;
    public Effect lineEffect = MindyFx.lineShot;
    public Effect cHitEffect = Fx.despawn;
    public Effect cFailEffect = Fx.none;

    public InstantHitBulletType(float damage){
        scaleLife = true;
        lifetime = 100f;
        collides = false;
        keepVelocity = false;
        backMove = false;
        this.damage = damage;
        pierceDamage = damage;
        hitEffect = despawnEffect = Fx.none;
        speed = 0.01f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        float px = b.x + b.lifetime * b.vel.x,
                py = b.y + b.lifetime * b.vel.y,
                rot = b.rotation();
        float bx = b.x, by = b.y;

        b.time = b.lifetime;
        b.set(px, py);

        //calculate hit entity

        cdist = 0f;
        result = null;
        float range = hitRange;

        Units.nearbyEnemies(b.team, px - range, py - range, range*2f, range*2f, e -> {
            if(e.dead()) return;

            e.hitbox(Tmp.r1);
            if(!Tmp.r1.contains(px, py)) return;

            float dst = e.dst(px, py) - e.hitSize;
            if((result == null || dst < cdist)){
                result = e;
                cdist = dst;
            }
        });

        Vec2 end = new Vec2(px, py); //this is inevitable shut up
        boolean chit = false;
        if(result != null){
            b.collision(result, px, py);
            result.damagePierce(pierceDamage);
            if(percentDamage > 0.0001f) result.damage(result.maxHealth() * percentDamage);
            end.trns(rot, cdist * 0.4f).add(px, py);
            chit = true;
        }else{
            Building build = Vars.world.buildWorld(px, py);
            if(build != null && build.team != b.team){
                build.collision(b);
                end.set(b).add(Tmp.v2.trns(rot + 180f, build.block.size * Vars.tilesize / 2f));
                chit = true;
            }
        }

        lineEffect.at(bx, by, rot, trailColor, end);
        if(chit){
            cHitEffect.at(end.x, end.y, rot + 180f, hitColor);
        }
        else{
            cFailEffect.at(end.x, end.y, rot, hitColor);
        }

        b.remove();

        b.vel.setZero();
    }
}