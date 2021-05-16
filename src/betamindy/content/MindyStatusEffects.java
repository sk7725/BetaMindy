package betamindy.content;

import arc.graphics.*;
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
    public static StatusEffect radiation, controlSwap, booster, creativeShock, amnesia, ouch;

    public void load(){
        radiation = new StatusEffect("radiated"){
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
                color = Pal.heal; //very ironic, isn't it?
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
            }

            {
                color = Pal2.boostColor;
                effect = MindyFx.boostFire;
                speedMultiplier = 2.75f;
                dragMultiplier = 0.001f;
            }
        };

        amnesia = new StatusEffect("amnesia"){
            @Override
            public void update(Unit unit, float time){
                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(unit.type.hitSize /2f);
                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0f, unit.team.color);
                }
                if(unit.abilities.size > 0) unit.abilities.clear();
            }

            {
                color = Color.valueOf("fff9d8");
                effect = MindyFx.question;
                buildSpeedMultiplier = 0.2f;
                permanent = true;
            }
        };

        creativeShock = new StatusEffect("creative-shock"){
            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(unit.isBuilding()){
                    unit.clearBuilding();
                    MindyFx.forbidden.at(unit.x, unit.y, 0f, color);
                }
            }

            {
                color = Color.valueOf("ec83af");
                effect = Fx.none;
                buildSpeedMultiplier = 0f;
            }
        };

        //harmful blocks will ignore units with this on, as it has been damaged recently.
        ouch = new StatusEffect("ouch"){{
            color = Color.clear;
            effect = Fx.none;
        }};
    }
}
