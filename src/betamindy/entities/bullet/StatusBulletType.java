package betamindy.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class StatusBulletType extends BulletType {
    public StatusBulletType(StatusEffect s, float r){
        status = s;
        hitSize = drawSize = r * 2f;
        lightRadius = r;
        hitColor = lightColor = s.color;
        lightOpacity = 0.2f;
        maxRange = r;

        speed = 0f;
        lifetime = 500f;
        collides = false;
        collidesTiles = false;
        collidesAir = false;
        collidesGround = false;
        keepVelocity = false;
        hittable = false;
        absorbable = false;

        collidesTeam = false; //if collidesTeam is true, it will only affect allies. Otherwise, it affects all but allies.
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        MindyFx.zoneStart.at(b, maxRange);
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(collidesTeam) Units.nearby(b.team, b.x, b.y, maxRange, u -> u.apply(status, 8f));
        else{
            Units.nearbyEnemies(b.team, b.x, b.y, hitSize, hitSize, u -> {
                if(u.dst2(b) <= maxRange * maxRange) u.apply(status, 8f);
            });
        }
        if(Mathf.chanceDelta(0.3f)){
            Tmp.v1.trns(Mathf.random(360f), maxRange).add(b);
            status.effect.at(Tmp.v1);
        }
    }

    @Override
    public void draw(Bullet b){
        Draw.color(status.color);
        Lines.stroke(1f);
        Lines.circle(b.x, b.y, Mathf.clamp((1f-b.fin())*20f)*maxRange);
        Draw.color();

        Draw.z(Layer.groundUnit - 0.01f);
        Fill.light(b.x, b.y, Lines.circleVertices(maxRange), Mathf.clamp((1-b.fin())*20)*maxRange, Tmp.c4.set(status.color).a(0f), Tmp.c3.set(status.color).a(0.2f+0.15f*Mathf.sin(b.time()*0.04f)));
        Draw.z(Layer.bullet);
    }

    @Override
    public void despawned(Bullet b){}

    @Override
    public void hit(Bullet b, float x, float y){}

    @Override
    public float calculateRange(){
        return maxRange;
    }
}
