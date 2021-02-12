package betamindy.content;

import arc.math.*;
import arc.util.*;
import betamindy.graphics.Pal2;
import mindustry.content.Fx;
import mindustry.ctype.ContentList;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

public class MindyStatusEffects implements ContentList {
    public static StatusEffect radiation, controlSwap, booster;

    public void load(){
        radiation = new StatusEffect("radiation"){
            //credits to EyeofDarkness
            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(Mathf.chanceDelta(0.008f * Mathf.clamp(time / 120f))) unit.damage(unit.maxHealth * 0.125f);
                for(int i = 0; i < unit.mounts.length; i++){
                    float strength = Mathf.clamp(time / 120f);
                    WeaponMount temp = unit.mounts[i];
                    if(temp == null) continue;
                    if(Mathf.chanceDelta(0.12f)) temp.reload = Math.min(temp.reload + Time.delta * 1.5f * strength, temp.weapon.reload);
                    temp.rotation += Mathf.range(12f * strength);
                }
            }

            {
                damage = 1.2f;
                effect = Fx.burning;
            }
        };

        controlSwap = new StatusEffect("controlswap"){{
            color = Pal.sapBullet;
            effect = MindyFx.exoticDust;
            effectChance = 0.4f;
            speedMultiplier = -1f;
        }};

        booster = new StatusEffect("booster"){
            @Override
            public void update(Unit unit, float time){
                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(unit.type.hitSize /2f);
                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation);
                }

                //Tmp.v1.trns(unit.rotation(), Time.delta * unit.type.speed * 1.75f * unit.mass());
                //unit.impulseNet(Tmp.v1);
            }

            {
                color = Pal2.boostColor;
                effect = MindyFx.boostFire;
                speedMultiplier = 2.75f;
            }
        };
    }
}
