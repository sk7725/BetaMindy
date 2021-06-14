package betamindy.world.blocks.units;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.distribution.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import static arc.Core.atlas;

//TODO grab all units on ground, grab air/mechs on spinning (use the TP method, not UnitPayload)
//TODO can output to payload convs
public class Claw extends Block {
    public float range = 36f;
    public float grabRange = 24f;
    public float grabOffset = grabRange / 2f + 4f;
    public float pullStrength = 3f;
    public float maxTension = 180f;

    public TextureRegion topRegion;
    public TextureRegion handRegion, handRegion2,  handOverlay, handOutline;
    public TextureRegion armRegion;

    public float clawOffset = 9f, clawGrab = 30f, clawOpen = 0f, clawFail = -7f;

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

    public class ClawBuild extends Building implements SpinDraw, SpinUpdate, ControlBlock {
        public @Nullable Unit unit; //TODO grab blocks when controlled
        protected final Vec2 lastV = new Vec2(), targetV = new Vec2();
        public float tension = 0f;//TODO better escaping

        public float logicControlTime = -1f;
        public boolean logicGrab = false;

        public float grabFail = 0f;
        public boolean grabFailed = false;

        public @Nullable BlockUnitc blockUnit;

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

        public void updateUnit(float x, float y, float r, boolean spinning){
            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }
            boolean con = !spinning && logicControlled();

            if(!con) targetV.trns(r, 8f);

            if(unit == null && (!con || logicGrab)){
                Tmp.v1.set(targetV).clamp(4f, range  - grabRange / 2f + 4f);
                grab(Tmp.v1.x, Tmp.v1.y, targetV.angle());
            }
            else checkUnit();

            if(unit == null){
                lastV.lerpDelta(targetV, 0.05f);
                tension = 0f;
                if(!con || !logicGrab) grabFailed = false;
                if(grabFail > 0f) grabFail -= delta() * 0.05f;
                if(con && logicGrab && !grabFailed){
                    grabFailed = true;
                    grabFail = 1f;
                    Sounds.click.at(x, y);
                }
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
                //unit.vel.setZero();
                if(unit.dst(x, y) > range + 4f || tension > maxTension){
                    detach(x, y);
                    return;
                }
            }
            Tmp.v1.set(targetV).add(x, y).sub(unit).clamp(0f, pullStrength);
            unit.move(Tmp.v1.x, Tmp.v1.y);
            if(con && !logicGrab) detach(x, y);
        }

        public void updatePlayer(){
            blockUnit.health(health);
            blockUnit.ammo(blockUnit.type().ammoCapacity * (unit == null ? 0f : 1f));
            blockUnit.team(team);
            blockUnit.set(x, y);

            //float angle = angleTo(blockUnit.aimX(), blockUnit.aimY());
            targetV.set(blockUnit.aimX(), blockUnit.aimY()).sub(this).clamp(0f, range);
            logicGrab = blockUnit.isShooting();
        }

        public void drawClaw(float x, float y, float r){
            float angle = lastV.angle();
            float lz = Draw.z();
            Draw.rect(region, x, y);
            Draw.z(Layer.flyingUnitLow - 0.1f);
            Draw.rect(topRegion, x, y);

            Tmp.v1.set(lastV).sub(Tmp.v3.trns(angle, clawOffset)).add(x, y);
            Draw.z(Layer.flyingUnitLow - 0.11f);
            Lines.stroke(6f);
            Lines.line(armRegion, x, y, Tmp.v1.x, Tmp.v1.y, false);
            Draw.z(Layer.flyingUnit + 0.1f);

            float clawa = (unit == null ? clawOpen + clawFail * grabFail : clawGrab);
            Draw.rect(handOutline, Tmp.v1.x, Tmp.v1.y, angle - 90f);
            Draw.rect(handRegion2, Tmp.v1.x, Tmp.v1.y, angle + clawa - 90f);
            Draw.rect(handRegion, Tmp.v1.x, Tmp.v1.y, angle - clawa - 90f);
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
            if(blockUnit != null && isControlled()) updatePlayer();
            updateUnit(x, y, rotation * 90f, false);
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            updateUnit(sx, sy, absRot, true);
        }

        public boolean logicControlled(){
            return logicControlTime > 0f || (blockUnit != null && isControlled());
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.shoot && (blockUnit == null || !blockUnit.isPlayer())){
                targetV.set(World.unconv((float)p1), World.unconv((float)p2)).sub(this);
                targetV.clamp(0f, range);
                logicControlTime = logicControlCooldown;
                logicGrab = !Mathf.zero(p3);
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4){
            if(type == LAccess.shootp && (blockUnit == null || !blockUnit.isPlayer())){
                logicControlTime = logicControlCooldown;
                logicGrab = !Mathf.zero(p2);

                if(p1 instanceof Posc){
                    targetV.set((Posc)p1).sub(this);
                    targetV.clamp(0f, range);
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public Unit unit(){
            if(blockUnit == null){
                blockUnit = (BlockUnitc)UnitTypes.block.create(team);
                blockUnit.tile(this);
            }
            return (Unit)blockUnit;
        }

        @Override
        public boolean canControl(){
            return true;
        }

        @Override
        public boolean shouldAutoTarget(){
            return false;
        }
    }
}
