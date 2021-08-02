package betamindy.entities.bullet;

import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;

public class IdeologyBulletType extends SoundwaveBulletType{
    public float successChance = 0.1f;

    public IdeologyBulletType(float speed, float damage){
        super(speed, damage, MindyStatusEffects.ideology);
        statusDuration = 0.01f; //effect is dummy
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
        if(other instanceof Unit){
            Unit unit = (Unit)other;
            Tmp.v3.set(unit).sub(b.x, b.y).nor().scl(knockback * 80f);
            unit.impulse(Tmp.v3);
            if(unit.hasEffect(StatusEffects.boss) || unit.hasEffect(MindyStatusEffects.portal)) return;
            unit.apply(MindyStatusEffects.glitched, 160f);
            if(!Vars.net.client() && Mathf.chance(successChance * (1f - unit.healthf()))) hypnotise(b, unit);
        }
    }

    public void hypnotise(Bullet b, Unit u){
        if(u.isPlayer()) return;
        u.team(b.team);
        u.apply(MindyStatusEffects.ideology, 360f);
    }
}
