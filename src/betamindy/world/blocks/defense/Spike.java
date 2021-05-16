package betamindy.world.blocks.defense;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.world.blocks.logic.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.meta.*;

import static arc.math.geom.Geometry.d4;
import static mindustry.Vars.*;

public class Spike extends Wall {
    public float damage = 25f;
    public float damageSelf = 5f;
    public float invincibleFrames = 32f;
    public float pushMultiplier = 2.5f;

    /** Whether to use Celeste mechanics; to be able to float & only damage if a unit has the opposing vector */
    public boolean celeste = false;

    public @Nullable StatusEffect status;
    public float statusDuration = 300f;
    public final StatusEffect invincibleMarker = MindyStatusEffects.ouch;
    public TextureRegion shadowRegion;

    public Spike(String name){
        super(name);
        rotate = true;
        solid = false;
        solidifes = false;
        hasShadow = false;
    }

    public void drawSpike(float x, float y, int r){
        Draw.rect(region, x, y, size * Draw.scl * Draw.xscl * 32f, size * Draw.scl * Draw.yscl * Mathf.sign(r == 0 || r == 3) * 32f,r * 90f);
    }

    @Override
    public void load(){
        super.load();
        shadowRegion = Core.atlas.find(name + "-shadow", "betamindy-spike-shadow");
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        drawSpike(req.drawx(), req.drawy(), req.rotation);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        if(!valid || celeste) return;

        Tile t = world.tile(x, y);
        if(t != null) t = t.nearby((rotation + 2) % 4);
        valid = t == null || t.solid(); //if it is attached to a border, it is alright
        if(valid) return;

        Draw.mixcol(Pal.remove, 1f);
        Draw.alpha(Mathf.absin(Time.globalTime, 2f, 1f));
        Draw.rect(region, x * tilesize, y * tilesize, rotation * 90f);
        Draw.color();
        Draw.rect(Icon.warning.getRegion(), t.drawx(), t.drawy(), 32f * Draw.scl * Draw.xscl, 32f * Draw.scl * Draw.yscl);
        Draw.mixcol();
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.damage, (int)damage + " ~ [coral]" + (int)(damage * pushMultiplier) + "[]");
    }

    public class SpikeBuild extends WallBuild implements PushReact {
        public boolean active(){
            if(celeste) return true;
            Tile t = tile.nearby((rotation + 2) % 4);
            return t == null || t.solid();
        }

        public void damageUnit(Unit unit){
            if(celeste && Angles.near(unit.vel.angle(), rotation * 90f, 80f)) return;
            if(unit.hasEffect(invincibleMarker)) return;
            unit.damagePierce(damage);
            unit.apply(invincibleMarker, invincibleFrames);
            damage(damageSelf);
            if(status != null) unit.apply(status, statusDuration);
        }

        public void damagePush(Unit unit){
            unit.damagePierce(damage * pushMultiplier);
            unit.apply(invincibleMarker, invincibleFrames);
            damage(damageSelf);
            if(status != null) unit.apply(status, statusDuration);
        }

        //TODO surge spike zap idle effect

        @Override
        public void pushed(int dir){
            if(dir != rotation) return;
            float ox = d4(dir).x * (size * 4f + 4f);
            float oy = d4(dir).y * (size * 4f + 4f);

            if(dir % 2 == 0){
                //tall rectangle
                float dr = d4(dir).x;
                Units.nearby(x + ox - 4f, y + oy - size * 4f, 8f, size * 8f, u -> {
                    if(!u.isFlying() && u.x >= x + ox - 4f && u.x <= x + ox + 4f){
                        damagePush(u);
                    }
                });
            }
            else{
                //wide rectangle
                float dr = d4(dir).y;
                Units.nearby(x + ox - size * 4f, y + oy - 4f, size * 8f, 8f, u -> {
                    if(!u.isFlying() && u.y >= y + oy - 4f && u.y <= y + oy + 4f){
                        damagePush(u);
                    }
                });
            }
        }

        @Override
        public void unitOn(Unit unit){
            if(active()) damageUnit(unit);
        }

        @Override
        public void draw(){
            boolean a = active();
            if(a){
                Draw.z(Layer.block - 0.99f);
                Draw.color(Pal.shadow);
                Draw.rect(shadowRegion, x, y, rotation * 90f);
            }

            Draw.z(Layer.blockOver);
            Draw.color(Color.white, a ? 1f : 0.5f);
            drawSpike(x, y, rotation);
            Draw.color();
        }

        @Override
        public void drawSelect(){
            if(active()) return;
            Tile t = tile.nearby((rotation + 2) % 4);
            Draw.mixcol(Pal.remove, 1f);
            Draw.alpha(Mathf.absin(Time.globalTime, 2f, 1f));
            Draw.rect(region, x, y, rotation * 90f);
            Draw.color();
            Draw.rect(Icon.warning.getRegion(), t.drawx(), t.drawy(), 32f * Draw.scl * Draw.xscl, 32f * Draw.scl * Draw.yscl);
            Draw.mixcol();
        }

        @Override
        public BlockStatus status(){
            return active() ? BlockStatus.active : BlockStatus.noInput;
        }
    }
}
