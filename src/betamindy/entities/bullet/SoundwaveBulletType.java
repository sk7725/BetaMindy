package betamindy.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static arc.Core.atlas;

public class SoundwaveBulletType extends BulletType {
    public Color fromColor = Pal.accent, toColor = Pal.remove;
    public String sprite = "betamindy-soundwave";
    public TextureRegion region;

    public SoundwaveBulletType(float speed, float damage, StatusEffect status){
        this.speed = speed;
        this.damage = damage;
        this.status = status;
        statusDuration = 60f * 20f;
        shootEffect = smokeEffect = despawnEffect = Fx.none;
        hitEffect = MindyFx.soundwaveHit;
        pierce = pierceBuilding = true;
        absorbable = false;
        hittable = false;
        collidesTiles = false;
        lifetime = 120f;
        hitSize = 15f;
        lightRadius = 0f;
        lightColor = hitColor;
        buildingDamageMultiplier = 0f; //buildings don't have ears
    }

    @Override
    public void load(){
        super.load();
        region = atlas.find(sprite);
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        float f = b.fout(0.9f);
        float s = Mathf.sin(Time.time / 9f);
        Draw.color(fromColor, toColor, Mathf.absin(7f, 1f));
        Draw.rect(region, b.x, b.y, (2f - f) * (1f + 0.2f * s) * region.width / 4f, f * (1f - 0.1f * s) * region.height / 4f, -90f + b.rotation());
        Draw.reset();
    }
}
