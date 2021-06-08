package betamindy.world.blocks.units;

import arc.math.geom.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.world.blocks.distribution.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;

//TODO grab all units on ground, grab air/mechs on spinning (use the TP method, not UnitPayload)
//TODO can output to payload convs
public class Claw extends Block {
    public float range = 36f;
    public float grabRange = 24f;
    public float grabOffset = grabRange / 2f + 4f;
    public float pullStrength = 0.5f;
    public float maxTension = 180f;

    private final Vec2 toV = new Vec2();

    public Claw(String name){
        super(name);
        update = true;
        rotate = true;
        expanded = true;
        solid = true;
    }

    public class ClawBuild extends Building implements SpinDraw, SpinUpdate {
        public @Nullable Unit unit;
        protected final Vec2 lastV = new Vec2();
        public float tension = 0f;

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

            if(dst > range){
                Tmp.v1.set(toV).setLength(range).add(x, y).sub(unit);
                unit.move(Tmp.v1.x, Tmp.v1.y);
                tension += delta();
                if(unit.dst(x, y) > range + 4f || tension > maxTension) detach(x, y);
            }
            else{
                Tmp.v1.trns(r, 8f).add(x, y).sub(unit);
                unit.impulseNet(Tmp.v1.scl(pullStrength));
            }
        }

        public void drawClaw(float x, float y, float r){
            //TODO
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            //TODO
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
