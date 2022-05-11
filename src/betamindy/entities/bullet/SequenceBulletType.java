package betamindy.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import betamindy.content.*;
import betamindy.graphics.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class SequenceBulletType extends BulletType {
    public float radius = 9f;
    public Effect realHitEffect = MindyFx.sequenceStarHit;

    public SequenceBulletType(float speed, float damage){
        this.speed = speed;
        this.damage = damage;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        pierce = true;
        pierceCap = 3;
        drag = -0.008f;
        collidesAir = true;
        lightRadius = 25f;
        lightColor = hitColor;
        trailColor = Color.white;
        hitSize = radius * 2f;
        trailWidth = radius * 0.9f;
        trailLength = 10;
        trailInterp = Interp.linear;
        despawnHit = true;
    }

    public Color realColor(Bullet b){
        return Drawm.starColor(b.fin());
    }

    public float realRadius(float f){
        return radius * (0.5f + f * f * 0.5f);
    }

    public void setDamage(Bullet b){
        b.damage = damage * b.damageMultiplier() * (1f + 6.5f * b.fin());
    }

    @Override
    public void update(Bullet b){
        setDamage(b);
        super.update(b);
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health){
        setDamage(b);
        super.hitEntity(b, entity, health);
    }

    @Override
    public void hit(Bullet b, float x, float y){
        realHitEffect.at(x, y, b.fin() * 100f, this);
        setDamage(b);
        super.hit(b, x, y);
    }

    @Override
    public void despawned(Bullet b){
        if(despawnHit){
            hit(b);
        }
        despawnSound.at(b);

        Effect.shake(despawnShake, despawnShake, b);
    }

    @Override
    public void draw(Bullet b){
        Draw.color(realColor(b));
        Fill.circle(b.x, b.y, realRadius(b.fin()));
        Draw.color(Drawm.starColor(b.fin() * 0.6f + 0.2f));
        Draw.rect("circle-shadow", b.x, b.y, radius * 1.8f, radius * 1.8f, 0);
        float z = Draw.z();
        Draw.z(Layer.bullet - 1);
        Draw.color(realColor(b), b.fin());
        Draw.blend(Blending.additive);
        Draw.rect("circle-shadow", b.x, b.y, radius * 4f, radius * 4f, 0);
        Draw.blend();
        Draw.z(z);
        drawTrail(b);
    }

    @Override
    public void drawTrail(Bullet b){
        if(trailLength > 0 && b.trail != null){
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            b.trail.draw(realColor(b), trailWidth);
            Draw.z(z);
        }
    }

    @Override
    public void drawLight(Bullet b){
        if(lightOpacity <= 0f || lightRadius <= 0f) return;
        Drawf.light(b, lightRadius * (0.5f + 0.5f * b.fin()), realColor(b), lightOpacity);
    }
}
