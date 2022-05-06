package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.graphics.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class RayTurret extends BaseTurret {
    public final int timerTarget = timers++;
    public float retargetTime = 5f;

    public TextureRegion baseRegion, heatRegion;
    public float shootCone = 15f;
    public float shootLength = 5f;
    public float laserWidth = 2.4f;

    public float damage = 0.5f;
    public float damageScale = 3f;
    public float levelReload = 100f;
    public int levels = 4;
    public float powerUse = 0.75f;

    public boolean targetAir = true, targetGround = true;
    public Color heatColor = Color.cyan;
    public Color laserColor = Color.white;
    public Color[] edgeColors = {Pal.lancerLaser, Pal2.deepBlue, Color.royal, Color.cyan};
    public StatusEffect status = StatusEffects.none;
    public float statusDuration = 300;

    public Effect levelUpEffect = MindyFx.ionBurst;
    public float effectRadius = 10f;
    public Sound shootSound = Sounds.tractorbeam;
    public Sound levelUpSound = MindySounds.easterEgg1;
    public float[] edgeSounds = {0, 4, 7, 12};
    public float shootSoundVolume = 0.9f;

    public RayTurret(String name){
        super(name);
        rotateSpeed = 10f;
    }

    public float levelScale(int level){
        if(level <= 0) return 1f;
        return Mathf.pow(damageScale, level);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.targetsAir, targetAir);
        stats.add(Stat.targetsGround, targetGround);
        stats.add(Stat.damage, (int)(damage * 60) + " ~ [coral]" + (int)(levelScale(levels - 1) * damage * 60f) + "[]");
        stats.remove(Stat.powerUse);
        stats.add(Stat.powerUse, (int)(powerUse * 60) + " ~ [coral]" + (int)(levelScale(levels - 1) * powerUse * 60f) + "[] " + Core.bundle.get("unit.powersecond"));
    }

    @Override
    public void init(){
        consumePowerDynamic((RayBuild b) -> b.target == null ? 0f : powerUse * levelScale(b.level));
        super.init();

        clipSize = Math.max(clipSize, (range + tilesize) * 2);
    }

    @Override
    public void load(){
        super.load();
        baseRegion = atlas.find(name + "-base", "block-" + size);
        heatRegion = atlas.find(name + "-heat");
    }

    public class RayBuild extends BaseTurretBuild{
        public @Nullable Unit target;
        public float lastX, lastY, strength;
        public boolean any, had;
        public float coolantLevel = 1f;
        public int level = 0, lastLevel = 0;
        public float levelCharge = 0f;

        @Override
        public void updateTile(){
            //retarget
            if(timer(timerTarget, retargetTime) && (target == null || Units.invalidateTarget(target, team, x, y, range))){
                level = 0;
                levelCharge = 0f;
                target = Units.bestEnemy(team, x, y, range, u -> u.checkTarget(targetAir, targetGround), (u, x, y) -> -u.maxHealth);
                if(target != null) lastLevel = 0;
            }

            //consume coolant
            if(target != null && coolant != null){
                float maxUsed = coolant.amount;

                Liquid liquid = liquids.current();

                float used = Math.min(Math.min(liquids.get(liquid), maxUsed * Time.delta), Math.max(0, (1f / coolantMultiplier) / liquid.heatCapacity));

                liquids.remove(liquid, used);

                if(Mathf.chance(0.06 * used)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }

                coolantLevel = 1f + (used * liquid.heatCapacity * coolantMultiplier);
            }

            any = false;

            //look at target
            if(target != null && target.within(this, range + target.hitSize/2f) && target.team() != team && target.checkTarget(targetAir, targetGround) && efficiency() > 0.02f){
                if(!headless){
                    Vars.control.sound.loop(shootSound, this, shootSoundVolume);
                }

                float dest = angleTo(target);
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
                lastX = target.x;
                lastY = target.y;
                strength = Mathf.lerpDelta(strength, 1f, 0.1f);

                //shoot when possible
                if(Angles.within(rotation, dest, shootCone)){
                    levelCharge += edelta();
                    if(level < levels - 1 && levelCharge > (1 << level) * levelReload){
                        levelCharge = 0f;
                        if(!headless) levelUpSound.at(x, y, Mathf.pow(2, edgeSounds[Mathf.mod(level, edgeSounds.length)]));
                        level++;
                        Tmp.v1.trns(rotation, shootLength).add(this);
                        levelUpEffect.at(Tmp.v1.x, Tmp.v1.y, effectRadius * (1f + ((float)level) / levels), edgeColors[Math.min(edgeColors.length - 1, level)]);
                    }
                    if(damage > 0){
                        target.damageContinuous(damage * levelScale(level) * efficiency());
                    }

                    if(status != StatusEffects.none){
                        target.apply(status, statusDuration);
                    }

                    any = true;
                    had = true;
                    lastLevel = level;
                }
            }else{
                strength = Mathf.lerpDelta(strength, 0, 0.1f);
                if(target != null && levelCharge > 0f){
                    levelCharge -= 4f * Time.delta;
                }
                else{
                    levelCharge = 0f;
                    level = 0;
                }
            }
        }

        @Override
        public float efficiency(){
            return super.efficiency() * coolantLevel;
        }

        public void drawLaser(float w, float ang){
            Lines.stroke(w);
            Lines.line(x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength), lastX, lastY);
            Drawf.tri(lastX, lastY, w, w * w, ang + 90f);
            Drawf.tri(lastX, lastY, w, w * w, ang - 90f);
            Drawf.tri(lastX, lastY, w, w * 5f, ang);
            Drawm.spark(x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength), w * 0.7f, w * 3.8f, Time.time / 2f);
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);
            //draw heat
            if(heatRegion.found() && strength > 0.0001f){

                Draw.color(heatColor, strength);
                Draw.blend(Blending.additive);
                Draw.rect(heatRegion, x, y, rotation - 90);
                Draw.blend();
                Draw.color();
            }

            //draw laser if applicable
            if(any || (had && strength > 0.01f)){
                Draw.z(Layer.bullet);
                int level = lastLevel; //override hack
                int i = Math.min(edgeColors.length - 1, level);
                float w = laserWidth * (1 + (float)level / levels + Mathf.sin(3f * levels / (level + 1), 0.25f)) * strength * efficiency();
                float ang = angleTo(lastX, lastY);
                Draw.color(edgeColors[i], 0.5f);
                drawLaser(w * 1.6f, ang);
                Draw.alpha(1f);
                drawLaser(w, ang);
                Draw.z(Layer.bullet + 0.01f);
                Draw.color(laserColor);
                drawLaser(w * 0.6f, ang);
            }
            Draw.reset();
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(rotation);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            rotation = read.f();
        }
    }
}
