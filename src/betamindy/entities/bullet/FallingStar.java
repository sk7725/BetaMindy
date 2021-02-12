package betamindy.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class FallingStar extends ArtilleryBulletType{
    public float size = 180f;
    public int fragShots = 1;
    public boolean shiny = false;
    public float fallTime = 180f;

    public FallingStar(float speed, float damage){
        super(speed, damage, "betamindy-star");
        frontColor = Color.valueOf("ffbbbb");
        hitEffect = MindyFx.starPoof;
        despawnEffect = Fx.none;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        Tmp.v2.trns(b.rotation(), b.lifetime() * speed);
        b.set(b.x + Tmp.v2.x, b.y + Tmp.v2.y);
        b.vel.setZero();
        b.lifetime(fallTime / 15f);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
    }

    @Override
    public void draw(Bullet b) {

        float scl = size * (1f + 1.6f * b.fout());
        float rot = Mathf.randomSeed(b.id) * 360f;
        float xoff = (Mathf.randomSeed(b.id + 1) - 0.5f) * 64f;
        float yoff = (Mathf.randomSeed(b.id + 2) + 0.5f) * 128f;
        float x = b.x + xoff * b.fout();
        float y = b.y + yoff * b.fout();

        Draw.z(Layer.space - 0.01f); //I hope this does not do the sk to the light shader

        if(shiny){
            if(Time.globalTime % 20f <= 10f){
                Draw.color(Tmp.c1.set(frontColor).shiftHue(Time.globalTime * 1.2f), Mathf.clamp(b.fin() * 2f) * 0.3f);
                Fill.circle(x, y, size * (0.25f + 0.2f * b.fout()));
                Fill.circle(x, y, size * (0.20f + 0.2f * b.fout()));
            }
            rot += b.fout() * 120f;
        }

        Draw.color(Tmp.c1.set(frontColor).shiftHue(Time.globalTime * 1.2f));
        for(int i = 0; i < 4; i++){
            x = b.x + xoff * b.fout() * (1f + i * 0.2f);
            y = b.y + yoff * b.fout() * (1f + i * 0.2f);
            Draw.alpha(Mathf.clamp(b.fin() * 2f) * (4f - i) / 4f);
            Draw.rect(frontRegion, x, y,
                    Draw.scl * Draw.xscl * scl, Draw.scl * Draw.yscl * scl, rot);
        }
    }

    @Override
    public void hit(Bullet b, float x, float y){
        b.hit = true;
        hitEffect.at(x, y, size, hitColor, Mathf.randomSeed(b.id) * 360f);
        hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

        Effect.shake(hitShake, hitShake, b);

        if(fragBullet != null){
            for(int i = 0; i < fragBullets; i++){
                for(int j = 0; j < fragShots; j++){
                    float len = 4f;
                    float a = b.rotation() + 360f * ((float)i / fragBullets) + 120f * ((float)j / fragBullets);
                    fragBullet.create(b, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, 1f + 0.3f * j, 1f);
                }
            }
        }

        if(splashDamageRadius > 0 && !b.absorbed){
            Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), true, true);
        }
    }
}
