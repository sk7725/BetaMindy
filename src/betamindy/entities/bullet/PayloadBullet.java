package betamindy.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.world.blocks.defense.turrets.PayloadTurret;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.Damage;
import mindustry.entities.EntityGroup;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

public class PayloadBullet extends ArtilleryBulletType {
    public float altitude = 40f;

    public PayloadBullet(){
        super();
        collidesAir = false;
        collidesGround = false;
        collides = false;
        scaleVelocity = true;
        hittable = false;
        absorbable = false;
        reflectable = false;
        drawSize = 80f;
        lifetime = 90f;
        trailSize = 6f;
        splashDamageRadius = 30f;
        despawnEffect = Fx.none;
    }

    @Override
    public void draw(Bullet b){
        Draw.z(Layer.darkness);

        TextureRegion icon;
        float rotation = b.rotation();

        if(b.data instanceof BuildPayload){
            BuildPayload bp = (BuildPayload)b.data;
            icon = bp.build.block.icon(Cicon.full);
            rotation = Mathf.lerp(rotation, (int)((rotation + 45f) / 90f) * 90f, b.fin());
        }
        else if(b.data instanceof UnitPayload){
            UnitPayload up = (UnitPayload)b.data;
            icon = up.unit.type().icon(Cicon.full);
        }
        else return;


        Draw.color(Pal.shadow);
        Draw.rect(icon, b.x, b.y, rotation - 90f);

        float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
        Draw.color();
        Draw.z(Layer.flyingUnit);
        Draw.rect(icon, b.x + offset, b.y + offset, rotation - 90f);

        Draw.reset();
    }

    @Override
    public void update(Bullet b){
        if(b.timer(0, (3 + b.fslope() * 2f) * trailMult)){
            float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
            trailEffect.at(b.x + offset, b.y + offset, b.fslope() * trailSize, b.team.color);
        }
    }

    @Override
    public void hit(Bullet b, float x, float y){
        Tile on = Vars.world.tileWorld(x, y);

        if(b.data instanceof Payload){
            Payload payload = (Payload) b.data;
            if(on != null && on.build != null && on.build.acceptPayload(on.build, payload)){
                Fx.unitDrop.at(on.build);
                on.build.handlePayload(on.build, payload);
            }
            else{
                if(payload instanceof BuildPayload) dropBuild((BuildPayload)payload, b, x, y, (Building)b.owner);
                else if(payload instanceof UnitPayload) dropUnit((UnitPayload)payload, b, x, y, (Building)b.owner, on);
            }
        }

        super.hit(b, x, y);
    }

    public void dropBuild(BuildPayload bp, Bullet b, float x, float y, Building o){
        PayloadTurret owner = (PayloadTurret) o.block;
        bp.set(x, y, b.rotation());
        Building tile = bp.build;
        int tx = World.toTile(x - tile.block.offset), ty = World.toTile(y - tile.block.offset);
        Tile tileon = Vars.world.tile(tx, ty);
        if(tileon != null && Build.validPlace(tile.block, tile.team, tx, ty, tile.rotation, false)){
            //print("Dst: " + Tmp.v1.set(x, y).dst(o));
            float dist = Tmp.v1.set(x, y).dst(o);
            float damageP = owner.maxDamagePercent * dist / (owner.range * owner.blockRangeMultiplier);
            //print("DamageP: " + damageP);
            int rot = (int)((b.rotation() + 45f) / 90f) % 4;
            if(dist > owner.safeRange) tile.damage(damageP * bp.build.health);
            bp.place(tileon, rot);

            Fx.unitDrop.at(tile);
            Fx.placeBlock.at(tileon.drawx(), tileon.drawy(), tileon.block().size);
            healthDamage(bp.build.block.health * owner.damage * damageP, b);
        }
        else {
            Fx.dynamicExplosion.at(tile, bp.block().size / 1.3f);
            healthDamage(bp.build.block.health * owner.damage * 0.65f, b);
            //TODO: make blocks with power/flammability/explosives explode violently(use Damage.explosion?)
        }
    }

    public void dropUnit(UnitPayload up, Bullet b, float x, float y, Building o, Tile on){
        if(Vars.net.client()) Vars.netClient.clearRemovedEntity(((UnitPayload)up).unit.id);

        PayloadTurret owner = (PayloadTurret) o.block;
        Unit u = up.unit;

        float dist = Tmp.v1.set(x, y).dst(o);
        float damageP = owner.maxDamagePercent * dist / owner.range;

        //can't drop ground units
        if(!u.canPass(on.x, on.y)){
            healthDamage(up.unit.type.health * owner.damage * 0.65f, b);
        }
        else{

            healthDamage(up.unit.type.health * owner.damage * damageP, b);
        }

        Fx.unitDrop.at(b);

        //clients do not drop payloads
        if(Vars.net.client()) return;

        u.set(b.x, b.y);
        u.trns(Tmp.v1.rnd(Mathf.random(2f)));
        u.rotation(b.rotation());
        //reset the ID to a new value to make sure it's synced
        u.id = EntityGroup.nextId();
        //decrement count to prevent double increment
        if(!u.isAdded()) u.team.data().updateCount(u.type, -1);
        u.add();
        if(dist > owner.safeRange) u.damage(u.health * damageP);
    }

    public void healthDamage(float health, Bullet b){
        //print("Health: " + health);
        Damage.damage(b.team, b.x, b.y, splashDamageRadius, health);
    }

    public void print(String pain){
        Vars.mods.getScripts().log("BetaMindy", pain);
    }
}
