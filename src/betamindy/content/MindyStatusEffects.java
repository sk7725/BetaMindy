package betamindy.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.util.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static betamindy.BetaMindy.*;
import static mindustry.Vars.*;

public class MindyStatusEffects{
    public static StatusEffect radiation, controlSwap, booster, creativeShock, amnesia, ouch, icy, pause, dissonance, ideology, glitched, cozy, portal, bittriumBane, drift, debugger, cutsceneDrag,
    //drinks
    caffeinated, herbed, blossoming, flowered, glowing, absorbing, sporeSlimed, starDrunk,
    //inflicts
    reverseBiased, forwardBiased, selfishRepair, decay;

    public static void load(){
        //marker for portal-spawned enemies
        portal = new StatusEffect("warped"){
            {
                healthMultiplier = 1.5f;
                damageMultiplier = 5f;
                color = Color.pink;
                effect = Fx.none;
                permanent = true;
            }

            @Override
            public void draw(Unit unit){
                if(hardmode.portal == null) return;
                float r = unit.dst(hardmode.portal) / (hardmode.portal.r + 20f);
                if(unit.type().flying && !unit.type.lowAltitude) Draw.z(Layer.flyingUnit + 1f);
                else Draw.z(Layer.effect + 0.0001f);
                Draw.color();
                Draw.mixcol(Tmp.c1.set(Color.white).lerp(hardmode.portal.color(), Mathf.clamp(r)), 1f);
                Draw.alpha(1f - Mathf.clamp(r - 1.5f));
                Draw.rect(unit.icon(), unit.x, unit.y, unit.rotation - 90f);
                Draw.reset();
            }
        };

        //harmful blocks will ignore units with this on, as it has been damaged recently.
        ouch = new StatusEffect("ouch"){{
            color = Color.clear;
        }};

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

        drift = new StatusEffect("drift"){
            @Override
            public void update(Unit unit, float time){
                MindyFx.unitShinyTrail.at(unit.x, unit.y, unit.rotation, color, unit.type);
                unit.vel.setAngle(Angles.moveToward(unit.vel.angle(), unit.rotation, 5.5f * Time.delta));
                if(unit.type.canBoost) unit.elevation = 1f;
            }

            {
                color = Color.valueOf("2acdff");
                effect = Fx.none;
                speedMultiplier = 0f;
                dragMultiplier = 0.00001f;
            }
        };

        booster = new StatusEffect("booster"){
            @Override
            public void update(Unit unit, float time){
                boolean drifting = unit.hasEffect(drift);

                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(unit.type.hitSize /2f);
                    if(drifting){
                        MindyFx.driftFire.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation, drift.color);
                    }
                    else{
                        effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation);
                    }
                }
                if(!drifting) MindyFx.unitShinyTrail.at(unit.x, unit.y, unit.rotation, color, unit.type);
            }

            {
                color = Pal2.boostColor;
                effect = MindyFx.boostFire;
                speedMultiplier = 2.75f;
                dragMultiplier = 0.001f;
            }
        };

        cozy = new StatusEffect("cozy"){{
            color = Color.coral;
            effect = Fx.none;
            damage = -0.05f; //slow af healing
            healthMultiplier = 1.1f;
        }};

        icy = new StatusEffect("icy"){{
            color = Color.valueOf("b1f6fa");
            effect = MindyFx.snowflake;
            speedMultiplier = 0.3f;
            dragMultiplier = 0.5f;
            reloadMultiplier = 0.5f;
            healthMultiplier = 0.75f;
            transitionDamage = 36f;
            damage = 0.16f;

            init(() -> {
                opposite(StatusEffects.melting, StatusEffects.burning);

                affinity(StatusEffects.blasted, ((unit, result, time) -> {
                    unit.damagePierce(transitionDamage);
                    result.set(icy, time);
                }));

                affinity(StatusEffects.wet, ((unit, result, time) -> {
                    unit.damagePierce(transitionDamage);
                    result.set(icy, time);
                }));

                affinity(StatusEffects.freezing, ((unit, result, time) -> {
                    result.set(icy, time + result.time);
                }));
            });
        }};

        amnesia = new StatusEffect("amnesia"){
            @Override
            public void update(Unit unit, float time){
                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(unit.type.hitSize /2f);
                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0f, unit.team.color);
                }
                if(unit.abilities.length > 0) unit.abilities = new Ability[0];
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

        pause = new StatusEffect("paused"){
            @Override
            public void draw(Unit unit){
                Draw.z(Layer.flyingUnit + 0.05f);
                Draw.blend(Blending.additive);
                Draw.color(Pal2.vector, Mathf.absin(Time.globalTime, 8f, 0.5f) + 0.4f);
                Draw.rect(unit.icon(), unit.x, unit.y, unit.rotation - 90f);
                Draw.blend();
                Draw.color();
            }

            {
                color = Pal2.vector;
                speedMultiplier = 0.001f;
                buildSpeedMultiplier = 0f;
                reloadMultiplier = 0f;
            }
        };

        dissonance = new StatusEffect("dissonance"){{
            damage = 4f;
            color = Color.white;
        }};

        ideology = new StatusEffect("ideology"){{
            color = Color.coral;
            effect = MindyFx.ideologied;
        }};

        //graphic status effect
        glitched = new StatusEffect("glitched"){
            @Override
            public void draw(Unit unit){
                Draw.z(Layer.flyingUnit + 0.05f);
                Draw.blend(Blending.additive);
                float f = Mathf.sin(Time.time / 5f) * 3f;
                float a = Mathf.random();
                int c = (int)(Mathf.randomSeed(unit.id) * 3f);
                Draw.color(c == 0 ? Color.magenta : c == 1 ? Color.yellow : Color.cyan, a);
                Draw.rect(unit.icon(), unit.x + f, unit.y, unit.rotation - 90f);
                Draw.color(c == 0 ? Color.cyan : c == 1 ? Color.magenta : Color.yellow, a);
                Draw.rect(unit.icon(), unit.x - f, unit.y, unit.rotation - 90f);
                Draw.blend();
                Draw.color();
            }

            {
                color = Pal2.source;
            }
        };

        bittriumBane = new StatusEffect("bittbane"){
            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(Useful.interval(3f, 0f)) MindyFx.unitBittTrail.at(unit.x, unit.y, unit.rotation, unit.type);
            }

            {
                color = Color.cyan;
                effect = MindyFx.sparkleBittrium;
                damageMultiplier = Float.POSITIVE_INFINITY;
                healthMultiplier = 0.000001f;
                reloadMultiplier = 0.25f;
                speedMultiplier = 1.5f;
            }

            @Override
            public void setStats(){
                super.setStats();
                stats.remove(Stat.damageMultiplier);
                stats.add(Stat.damageMultiplier, "[cyan]NULL[]");
                stats.remove(Stat.healthMultiplier);
                stats.add(Stat.healthMultiplier, "[pink]NULL[]");
            }
        };

        caffeinated = new StatusEffect("caffeinated"){{
            //in v5, I needed to use hacky cheats for something like these.
            buildSpeedMultiplier = 2.5f;
            reloadMultiplier = 2f;
            effect = Fx.steam;
            color = Pal2.coffee;
        }};

        herbed = new StatusEffect("herbed"){{
            reloadMultiplier = 0.8f;
            healthMultiplier = 1.2f;
            damage = -0.15f;
            effect = MindyFx.herbSteam;
            color = Color.valueOf("abd857");
        }};

        flowered = new StatusEffect("flowered"){{
            healthMultiplier = 1.1f;
            damage = -0.06f;
            reloadMultiplier = 1.1f;
            speedMultiplier = 1.1f;
            color = Pal2.cherry;
            effect = MindyFx.petals;
        }};

        blossoming = new InflictStatusEffect("blossoming", flowered){
            {
                speedMultiplier = 1.1f;
                reloadMultiplier = 1.1f;
                damageMultiplier = 0.5f;
                healthMultiplier = 0.75f;
                color = Pal2.cherry;
                effect = MindyFx.petals;

                effect2 = MindyFx.cherrySteam;
                onInterval = MindyFx.perfume;
                effectInterval = 140f;
                inflictDuration = 480f;
            }
        };

        glowing = new StatusEffect("glowing"){
            @Override
            public void draw(Unit unit){
                Draw.z(unit.type.flying && !unit.type.lowAltitude ? Layer.flyingUnit + 1f : Layer.effect);
                if(unit.type.drawCell){
                    Draw.mixcol(color, 1f);
                    Draw.rect(unit.type.cellRegion, unit.x, unit.y, unit.rotation - 90);
                    Draw.mixcol();
                }
                Draw.color(color);
                float r = Mathf.sin(27f, 3f);
                Drawm.spark(unit.x, unit.y, (6f - Math.abs(r)) * unit.hitSize / 8f, 0.25f * unit.hitSize, r * 15f);

                /*
                Draw.z(Layer.groundUnit - 1f);
                Draw.alpha(0.7f);
                Draw.rect("circle-shadow", unit.x, unit.y, unit.hitSize * 0.7f - r, unit.hitSize * 0.7f - r);
                */

                Drawf.light(unit.x, unit.y, 80f + unit.type.lightRadius, color, 1f);
                Draw.reset();
            }
            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(unit.type.flying && !unit.type.lowAltitude){
                    MindyFx.sparkTrailHigh.at(unit.x, unit.y, unit.hitSize, color);
                }
                else{
                    MindyFx.sparkTrail.at(unit.x, unit.y, unit.hitSize, color);
                }

                if(time <= 3f * Time.delta){
                    //get ready for it to fade
                    MindyFx.lightFade.at(unit.x, unit.y, 80f + unit.type.lightRadius, color, unit);
                }
            }

            @Override
            public void setStats(){
                super.setStats();
                stats.add(Stat.range, "[#00ff00]+[]" + (int)(80f / tilesize) + " " + Core.bundle.get("unit.blocks"));
            }

            {
                color = Color.white;
                effect = MindyFx.sparkle;
                effectChance = 0.035f; //for the record, default is 0.15f
            }
        };

        reverseBiased = new StatusEffect("reverse-biased"){
            @Override
            public void update(Unit unit, float time){
                if(Mathf.chanceDelta(effectChance)){
                    Useful.lightningCircle(unit.x, unit.y, Math.max(unit.hitSize / 2f + 4f, 8f), Math.max(4, (int) unit.hitSize / 9 + 2), color);
                }
                if(unit.isShooting()) unit.damagePierce(damage);
                if(unit.type.canBoost && !unit.type.flying) unit.elevation = Math.max(unit.elevation - 0.1f * Time.delta, 0f);
            }

            {
                dragMultiplier = 3f;
                buildSpeedMultiplier = 0.5f;
                reloadMultiplier = 0.8f;
                color = Pal.remove;
                effect = Fx.none;
                damage = 5f;
            }
        };

        forwardBiased = new InflictStatusEffect("forward-biased", reverseBiased){
            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(Mathf.chanceDelta(effectChance)){
                    Useful.lightningCircle(unit.x, unit.y, Math.max(unit.hitSize / 2f + 4f, 8f), Math.max(4, (int) unit.hitSize / 9 + 2), color);
                }
            }

            {
                ally = false;
                inflictDuration = 60f;
                range = 60f;
                effectInterval = 60f;
                onInterval = MindyFx.empBlast;
                dragMultiplier = 0.5f;
                buildSpeedMultiplier = 1.5f;
                reloadMultiplier = 1.1f;
                color = Pal.accent;
                effect = Fx.none;
            }
        };

        selfishRepair = new StatusEffect("selfish-repair"){{
            color = Pal.heal;
            damage = -2f;
            effectChance = 0.9f;
            effect = MindyFx.undecay;
        }};

        decay = new InflictStatusEffect("selfless-decay", selfishRepair){{
            damage = 2.5f;
            range = 50f;
            ally = false;
            color = Pal.accentBack;
            inflictDuration = 120f;
            effectChance = 0.9f;
            effect = MindyFx.decay;
            effect2 = Fx.blastExplosion;
            effect2Chance = 0.05f;
        }};

        absorbing = new LayerStatusEffect("absorbing"){{
            color = Pal.engine;
            useTeamColor = true;
            healthMultiplier = 5f;
        }};

        sporeSlimed = new LayerStatusEffect("spore-slimed"){{
            color = Pal2.sporeSlime;
        }};

        cutsceneDrag = new StatusEffect("cutscene-drag"){{
            dragMultiplier = 2.5f;
            healthMultiplier = 2f;
            color = Color.clear;
        }};

        starDrunk = new StatusEffect("star-drunk"){
            {
                color = Color.yellow;
                effect = MindyFx.starSparkle;
                buildSpeedMultiplier = 0.8f;
            }

            @Override
            public void setStats(){
                super.setStats();
                stats.add(Stat.mineSpeed, "200%");
            }

            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);

                if(unit.mining()){
                    unit.mineTimer(unit.mineTimer() + Time.delta * unit.type.mineSpeed);
                }
            }
        };
    }
}
