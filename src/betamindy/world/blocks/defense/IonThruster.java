package betamindy.world.blocks.defense;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.entities.bullet.*;
import betamindy.graphics.*;
import betamindy.world.blocks.logic.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class IonThruster extends LogicSpinBlock {
    public float range = 80f;
    public float strength = 0.045f;
    public boolean affectPayloads = false;
    public boolean corrupted = false;
    public float damageReduction = 0.995f; //hits 40% damage in 3 seconds according to wolframalpha
    public float minDamage = 0.3f;

    public TextureRegion jetRegion;
    public Effect hitEffect = MindyFx.ionHit;
    public Effect payloadHitEffect = MindyFx.ionHitPayload;
    public Effect smokeEffect = MindyFx.ionJet;
    public float smokeChance = 0.2f, smokeX = 14f, smokeY = 6f, effectChance = 0.2f;
    private final Vec2 tmp = new Vec2();

    public IonThruster(String name){
        super(name);

        lightColor = Pal2.drift;
        priority = TargetPriority.turret;
        flags = EnumSet.of(BlockFlag.turret);
        ambientSound = MindySounds.coolingFan;
        ambientSoundVolume = 0.2f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal2.drift);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.add(Stat.speed, strength, StatUnit.none);
    }

    @Override
    public void init(){
        super.init();
        updateClipRadius(range);
    }

    @Override
    public void load(){
        super.load();
        jetRegion = atlas.find(name + "-jet", "betamindy-gradient");
    }

    public class IonThrusterBuild extends LogicSpinBuild {
        public float heat;

        public void pushBullets(float strength){
            float rot = realRotation();
            tmp.trns(rot, strength);
            Groups.bullet.intersect(x - range, y - range, range * 2f, range * 2f, b -> {
                if(b.type != null && b.within(this, range)){
                    if(affectPayloads && b.type instanceof PayloadBullet){
                        if(b.data instanceof Payload pay){
                            b.vel.add(tmp.x * 2.5f / pay.size(), tmp.y * 2.5f / pay.size());
                            b.rotation(b.vel.angle());
                            if(Mathf.chance(effectChance)) payloadHitEffect.at(b.x + Mathf.range(pay.size() / 2f), b.y + Mathf.range(pay.size() / 2f), rot, lightColor);
                        }
                    }
                    else if(corrupted || b.type.hittable){
                        b.vel.add(tmp);
                        if(b.type.speed < 1f || b.type.drag > 0.00001f) b.rotation(b.vel.angle()); //these look awful
                        if(Mathf.chance(effectChance)) hitEffect.at(b.x + Mathf.range(b.hitSize() / 2f), b.y + Mathf.range(b.hitSize() / 2f), rot, lightColor);
                        if(b.team == team){
                            //nerf allied bullets: speed exchanged with damage
                            float defdam = b.type.damage * b.damageMultiplier();
                            if(defdam > 1f && b.damage > defdam * minDamage) b.damage(b.damage * damageReduction);
                            //this doesnt account for deltatime, but the way to do it involves power to the Time.delta, and considering the float precision its just as bad.
                        }
                    }
                }
            });
        }

        @Override
        public void updateTile(){
            super.updateTile();

            heat = Mathf.lerpDelta(heat, efficiency(), 0.1f);

            if(efficiency() > 0.1f){
                pushBullets(strength * edelta());
                if(Mathf.chance(smokeChance * edelta())){
                    Tmp.v1.trns(realRotation(), smokeY, Mathf.range(0.5f) * smokeX);
                    smokeEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, realRotation(), lightColor);
                }
            }
        }

        public void drawJet(float x, float y, float r, float w, float h){
            Tmp.v1.trns(r, smokeY + h / 2f).add(x, y);
            Draw.rect(jetRegion, Tmp.v1.x, Tmp.v1.y, h, w, r);//height is the distance to the end of the jet, which is actually the sprite's width
        }

        @Override
        public void draw(){
            super.draw();

            if(heat > 0.001f){
                Draw.z(Layer.turret + 1f);
                Draw.blend(Blending.additive);
                Draw.color(lightColor, (Mathf.absin(7f, 0.6f) + 0.3f) * heat);
                drawJet(x, y, realRotation(), smokeX * (0.35f + Mathf.absin(11f, 0.15f)) * 2f, 18f * size * (1.5f - Mathf.absin(11f, 0.5f)));
                drawJet(x, y, realRotation(), smokeX * (0.25f + Mathf.absin(Time.time + 16.57f, 11f, 0.25f)) * 2f, (16f * size + 6f) * (1.5f - Mathf.absin(Time.time + 16.57f, 11f, 0.5f)));
                Draw.blend();
                Draw.color();
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, range, Pal2.drift);
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency() > 0.1f;
        }
    }
}
