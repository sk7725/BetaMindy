package betamindy.world.blocks.units;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.distribution.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.Core.atlas;

//TODO grab all units on ground, grab air/mechs on spinning (use the TP method, not UnitPayload)
//TODO can output to payload convs
public class Claw extends Block {
    public float range = 36f;
    public float grabRange = 24f;
    public float grabOffset = grabRange / 2f + 4f;
    public float pullStrength = 8f;
    public float maxTension = 180f;

    public TextureRegion topRegion;
    public TextureRegion handRegion, handRegion2,  handOverlay, handOutline;
    public TextureRegion armRegion;

    public float clawOffset = 9f, clawGrab = 30f, clawOpen = 0f;

    private final Vec2 toV = new Vec2();
    //after being logic-controlled and this amount of time passes, the claw will resume normal AI
    public final static float logicControlCooldown = 60 * 3;

    public Claw(String name){
        super(name);
        update = true;
        rotate = true;
        expanded = true;
        solid = true;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        handRegion = atlas.find(name + "-hand");
        armRegion = atlas.find(name + "-arm", "betamindy-claw-arm");
        handRegion2 = atlas.find(name + "-hand2");
        handOverlay = atlas.find(name + "-hand-top");
        handOutline = atlas.find(name + "-hand-outline");
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);
        Drawm.outlineRegion(packer, topRegion, outlineColor, name + "-top");
        Drawm.outlineRegion(packer, handRegion, outlineColor, name + "-hand");
        Drawm.outlineRegion(packer, handRegion2, outlineColor, name + "-hand2");
    }

    public class ClawBuild extends Building implements SpinDraw, SpinUpdate {
        public @Nullable Unit unit;
        protected final Vec2 lastV = new Vec2(), targetV = new Vec2();
        public float tension = 0f;
        public float logicControlTime = -1f;

        public void checkUnit(){
            if(unit != null && (unit.dead() || !unit.isValid())) unit = null;
        }

        public void grab(float x, float y, float r){
            boolean empty = unit == null;
            Tmp.v2.trns(r, grabOffset).add(x, y);
            unit = Units.closestEnemy(null, Tmp.v2.x, Tmp.v2.y, grabRange / 2f, u -> u.type != UnitTypes.block && !u.hasEffect(MindyStatusEffects.ouch));
            if(empty && unit != null){
                //TODO effect
            }
        }

        public void detach(float x, float y){
            //TODO effect
            unit.apply(MindyStatusEffects.ouch, 45f);
            unit = null;
        }

        public void updateUnit(float x, float y, float r){
            if(unit == null){
                grab(x, y, r);
            }
            else checkUnit();

            if(unit == null){
                lastV.lerpDelta(Tmp.v1.trns(r, 8f), 0.05f);
                tension = 0f;
                return;
            }

            toV.set(unit).sub(x, y);
            lastV.set(toV);
            float dst = toV.len();
            if(tension >= 0f) tension -= Math.min(edelta() * 0.4f, 0.8f);
            if(dst > range - 8f) tension += delta();

            if(dst > range){
                Tmp.v1.set(toV).setLength(range).add(x, y).sub(unit);
                unit.move(Tmp.v1.x, Tmp.v1.y);
                unit.vel.setZero();
                if(unit.dst(x, y) > range + 4f || tension > maxTension){
                    detach(x, y);
                    return;
                }
            }
            Tmp.v1.trns(r, 8f).add(x, y).sub(unit).scl(pullStrength / unit.mass());
            unit.move(Tmp.v1.x, Tmp.v1.y);
        }

        public void drawClaw(float x, float y, float r){
            float angle = lastV.angle();
            float lz = Draw.z();
            Draw.rect(region, x, y, r);
            Draw.z(Layer.flyingUnitLow - 0.1f);
            Draw.rect(topRegion, x, y);

            Tmp.v1.set(lastV).sub(Tmp.v3.trns(angle, clawOffset)).add(x, y);
            Draw.z(Layer.flyingUnitLow - 0.11f);
            Lines.stroke(6f);
            Lines.line(armRegion, x, y, Tmp.v1.x, Tmp.v1.y, false);
            Draw.z(Layer.flyingUnit + 0.1f);

            Draw.rect(handOutline, Tmp.v1.x, Tmp.v1.y, angle - 90f);
            Draw.rect(handRegion2, Tmp.v1.x, Tmp.v1.y, angle + ( unit == null ? clawOpen : clawGrab) - 90f);
            Draw.rect(handRegion, Tmp.v1.x, Tmp.v1.y, angle - ( unit == null ? clawOpen : clawGrab) - 90f);
            Draw.rect(handOverlay, Tmp.v1.x, Tmp.v1.y, angle - 90f);
            Draw.z(lz);
        }

        @Override
        public void draw(){
            drawClaw(x, y, rotation * 90f);
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            drawClaw(x, y, dr + rotation * 90f);
        }

        @Override
        public void update(){
            super.update();
            updateUnit(x, y, rotation * 90f);
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            updateUnit(sx, sy, absRot);
        }
    }
}
