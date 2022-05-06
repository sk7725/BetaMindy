package betamindy.entities.bullet;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.util.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class PortalLightningBulletType extends BulletType {
    public Color color1, color2;
    public int lightningLength = 50;
    public int lightningLengthRand = 0;
    public float orbRadius = 12.5f;

    public PortalLightningBulletType(float damage, Color c1, Color c2){
        this.damage = damage;
        color1 = c1;
        color2 = c2;

        speed = 0.001f;
        lifetime = 180f;
        shootEffect = MindyFx.lightningOrbCharge;
        despawnEffect = MindyFx.lightningOrbDespawn;
        hitEffect = Fx.none;
        keepVelocity = false;
        hittable = false;
        absorbable = false;
        hitSize = orbRadius;
        lightOpacity = 0f;
        collides = false;
        collidesAir = false;
        collidesGround = false;
        collidesTiles = false;
        //for stats
        status = StatusEffects.shocked;
    }

    @Override
    public float calculateRange(){
        return (lightningLength + lightningLengthRand/2f) * 15f;
    }

    @Override
    public float estimateDPS(){
        return super.estimateDPS() * Math.max(lightningLength / 10f, 1);
    }

    @Override
    public void draw(Bullet b){
        Drawm.lightningOrb(b.x, b.y, Mathf.clamp(b.fin() * 2f) * orbRadius, color1, color2);
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        if(!headless){
            Time.run(Math.max(0f, lifetime - shootEffect.lifetime), () -> {
                if(b != null) shootEffect.at(b.x, b.y, orbRadius, color1);
            });
            MindySounds.portalOpen.at(b, 1.6f);
        }
    }

    @Override
    public void despawned(Bullet b){
        ThickLightning.create(b.team, color1, damage, b.x, b.y, b.rotation(), lightningLength + Mathf.random(lightningLengthRand));
        if(!headless){
            despawnEffect.at(b.x, b.y, orbRadius, color1, color2);
            Sounds.plasmadrop.at(b);
        }
    }
}
