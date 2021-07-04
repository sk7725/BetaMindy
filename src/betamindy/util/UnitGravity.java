package betamindy.util;

import arc.*;
import arc.input.*;
import arc.math.geom.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

//this is cursed yes
public class UnitGravity {
    public final Vec2 g = new Vec2(0, 0);
    public final float baseStrength = 0.98f; // strength of gravity
    public float groundedPad = 6f; // units need to be less than this amount from the ground to qualify as grounded

    public float jumpHeight = 25f;
    public Effect jumpEffect = Fx.mine; //maybe temp

    public boolean enabled = false;

    public Vec2 vec(){
        return g;
    }

    public int rotation(){
        return rotation(g);
    }
    public int rotation(Vec2 g){
        return ((int)(g.angle() + 45f) / 90) % 4;
    }

    public void setV(float x, float y){
        enabled = true;
        g.set(x, y);
    }
    public void trns(float angle, float l){
        enabled = true;
        g.trns(angle, l);
    }

    public void set(int i){
        set(i, baseStrength);
    }
    public void set(int i, float str){
        if(i < 0) enabled = false;
        else{
            trns(i * 90f, str);
            enabled = true;
        }
    }

    public void reset(){
        g.set(0, 0);
        enabled = false;
    }

    public void update(){
        /*
        if(Core.input.keyTap(KeyCode.down)) set(3);
        if(Core.input.keyTap(KeyCode.up)) set(1);
        if(Core.input.keyTap(KeyCode.right)) set(-1);*/

        if(!enabled || !state.isPlaying()) return;

        Groups.unit.each(u -> !u.isFlying(), u -> {
            Tile groundTile = world.tileWorld(u.x + Geometry.d4x[rotation()] * groundedPad, u.y + Geometry.d4y[rotation()] * groundedPad);
            boolean grounded = groundTile == null || groundTile.solid() && groundTile != u.tileOn();
            if(grounded){
                //if a positive vector is detected, assume that the unit tried to jump
                float a = Math.abs((u.vel.angle() - g.angle()) % 360);
                if(100f < a && a < 260f){
                    u.vel.add(g.x * -jumpHeight * u.type.speed, g.y * -jumpHeight * u.type.speed);
                    jumpEffect.at(u);
                }
            }
            else{
                u.apply(StatusEffects.unmoving, 5f);
                u.vel.add(g);
            }
        });
    }
}
