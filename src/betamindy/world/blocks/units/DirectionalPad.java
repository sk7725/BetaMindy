package betamindy.world.blocks.units;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;

public class DirectionalPad extends BoostPad{
    public @Nullable BoostPad friend;

    public DirectionalPad(String name){
        super(name);
        rotate = true;
        rangePad = 0f;
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region, animRegion[0]};
    }

    public class DirectionalPadBuild extends BoostPadBuild {
        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.mixcol(lightColor, heat / cooldown);
            Draw.rect(animRegion[(int)(Time.time / animSpeed) % sprites], x, y, rotation * 90f);
            Draw.mixcol();
        }

        @Override
        public void boostUnit(Unit unit) {
            if(heat <= 0f){
                boostSound.at(this);
                boostEffect.at(x, y, rotation * 90f);
                heat = cooldown;
            }

            float va = unit.vel.isZero() ? unit.rotation : unit.vel.angle();
            float vd = Angles.angleDist(va, rotation * 90f);

            if(impulseUnit && unit.vel().len2() < impulseAmount * impulseAmount * 0.9f){
                unit.impulseNet(Tmp.v1.trns(va, unit.mass() * (impulseAmount - unit.vel.len())));
            }
            if(friend != null && unit.hasEffect(friend.status)){
                unit.apply(friend.status, friend.duration);
            }

            /*
            if(vd > 90f){
                va = (rotation * 180f + 180f - va); //reflection
                va %= 360f;
            }
            if(vd > 50f && vd < 130f){
                va = Angles.moveToward(va, rotation * 90f, 40f - Math.abs(vd - 90f)); //bend
            }*/
            if(vd > 50f) va = rotation * 90f; //simple is better.

            unit.rotation(va);
            unit.apply(status, vd <= 50f ? duration : Math.max(duration, vd / 5.5f + 2f));
        }
    }
}
