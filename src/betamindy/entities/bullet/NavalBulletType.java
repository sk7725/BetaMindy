package betamindy.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.environment.*;

public class NavalBulletType extends BulletType {
    public float length = 40f, width = 6f;
    public float finAngle = 60f;
    public Color fromColor = Color.white, toColor = Pal.lancerLaser;
    public float groundDrag = 0.08f, rippleTime = 20f;
    public Effect rippleEffect = Fx.ripple;

    public NavalBulletType(float speed, float damage){
        this.speed = speed;
        this.damage = damage;
        hitEffect = Fx.hitLancer;
        shootEffect = smokeEffect = Fx.lightningShoot;
        despawnEffect = MindyFx.energyDespawn;
        pierce = true;
        absorbable = false;
        drag = 0.008f;
        collidesAir = false;
        despawnShake = 1f;
        lightRadius = 16f;
        lightColor = hitColor;
    }

    @Override
    public void update(Bullet b){
        Floor floor = Vars.world.floorWorld(b.x, b.y);
        if(!floor.isLiquid) b.vel.scl(Math.max(1f - groundDrag * Time.delta, 0.01f));
        else{
            if(Time.time - b.fdata > rippleTime){
                b.fdata = Time.time;
                rippleEffect.at(b.x, b.y, hitSize / 3f, floor.mapColor.equals(Color.black) ? Blocks.water.mapColor : floor.mapColor);
            }
        }
        super.update(b);
    }

    @Override
    public void draw(Bullet b){
        float f = Mathf.clamp(b.finpow() * 2f);
        Draw.color(fromColor, toColor, f);
        Drawf.tri(b.x, b.y, width, length, b.rotation() + 180f);
        Drawf.tri(b.x, b.y, width, width, b.rotation());
        Drawf.tri(b.x, b.y, width * 0.8f, length * 0.6f, b.rotation() + f * finAngle + 180f);
        Drawf.tri(b.x, b.y, width * 0.8f, length * 0.6f, b.rotation() - f * finAngle + 180f);
    }
}
