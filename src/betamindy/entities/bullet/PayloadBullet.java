package betamindy.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.world.blocks.defense.turrets.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import static mindustry.Vars.*;

public class PayloadBullet extends ArtilleryBulletType {
    public float altitude = 40f;
    protected boolean correctView = true; //due to payloads updating, the building should be where it is supposed to be

    public PayloadBullet(float speed){
        super(speed, 1f);
        collidesAir = false;
        collidesGround = false;
        collides = false;
        scaleLife = true;
        hittable = false;
        absorbable = false;
        reflectable = false;
        drawSize = 80f;
    }

    public PayloadBullet(){
        this(1f);
    }

    @Override
    public void load(){
        super.load();
        //Events.run(Trigger.update, () -> {
        //   correctView = Core.settings.getBool("correctview");
        //});
    }

    @Override
    public void draw(Bullet b){
        Draw.z(Layer.darkness);

        TextureRegion icon;
        float rotation = b.rotation();

        if(b.data instanceof BuildPayload bp){
            //if(bp.build.block.rotate) rotation = Mathf.lerp(rotation, (int)((rotation + 45f) / 90f) * 90f, b.fin());
            //else rotation = Mathf.lerp((rotation + 45f) % 90f - 45f, 0f, b.fin());
            drawBuild(b, bp);
        }
        else if(b.data instanceof UnitPayload up){
            icon = up.unit.type().fullIcon;
            rotation -= 90f;
            drawNew(b, icon, rotation); //stays the same
        }
    }

    public float payloadRotation(Bullet b){
        float rotation = b.rotation();
        if(b.data instanceof BuildPayload bp){
            if(bp.build.block.rotate) return Mathf.lerp(rotation, (int)((rotation + 45f) / 90f) * 90f, b.fin());
            return Mathf.lerp((rotation + 45f) % 90f - 45f, 0f, b.fin());
        }
        return rotation;
    }

    @Deprecated
    public void drawOriginal(Bullet b, TextureRegion icon, float rotation){
        Draw.color(Pal.shadow);
        Draw.rect(icon, b.x, b.y, rotation);

        float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
        Draw.color();
        Draw.z(Layer.flyingUnit);
        Draw.rect(icon, b.x + offset, b.y + offset, rotation);

        Draw.reset();
    }

    public void drawNew(Bullet b, TextureRegion icon, float rotation){
        Draw.color(Pal.shadow);
        float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
        Draw.rect(icon, b.x - offset, b.y - offset, rotation);

        float sizeScl = offset * 0.02f + 1f;
        Draw.color();
        Draw.z(Layer.flyingUnit);
        Draw.rect(icon, b.x, b.y, icon.width * Draw.scl * Draw.xscl * sizeScl, icon.height * Draw.scl * Draw.yscl * sizeScl,rotation);

        Draw.reset();
    }

    public void drawBuild(Bullet b, BuildPayload bp){
        Draw.color(Pal.shadow);
        float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
        Draw.rect(bp.block().fullIcon, b.x - offset, b.y - offset, bp.rotation());

        float sizeScl = offset * 0.02f + 1f;
        float pxScl = Draw.xscl, pyScl = Draw.yscl;
        Draw.color();
        Draw.scl(sizeScl * pxScl, sizeScl * pyScl);
        Draw.z(Layer.flyingUnit);
        Draw.zTransform(z -> z >= Layer.flyingUnit ? z : 0.0011f + Mathf.clamp(z, Layer.flyingUnit + 1f - 0.001f, Layer.flyingUnit + 1.9f));
        bp.build.tile = emptyTile;
        bp.build.payloadDraw();
        Draw.zTransform();
        Draw.scl(pxScl, pyScl);
        Draw.z(Layer.flyingUnit);
    }

    @Override
    public void update(Bullet b){
        if(b.timer(0, (3 + b.fslope() * 2f) * trailMult)){
            trailEffect.at(b.x, b.y, b.fslope() * trailSize, b.team.color);
        }
        if(b.data instanceof Payload pay){
            pay.set(b.x, b.y, payloadRotation(b));
            pay.update(null, null);
        }
    }

    @Override
    public void hit(Bullet b, float x, float y){
        Tile on = Vars.world.tileWorld(x, y);

        if(b.data instanceof Payload payload){
            if(on != null && on.build != null && on.build.acceptPayload(on.build, payload)){
                Fx.unitDrop.at(on.build);
                on.build.handlePayload(on.build, payload);
            }
            else{
                if(payload instanceof BuildPayload) dropBuild((BuildPayload)payload, b, x, y);
                else if(payload instanceof UnitPayload) dropUnit((UnitPayload)payload, b, x, y, on);
            }
        }

        super.hit(b, x, y);
    }

    public void dropBuild(BuildPayload bp, Bullet b, float x, float y){
        float damageP = 0.7f;
        float ownerDamage = b.damage, selfDamage = 0.3f;

        if((b.owner instanceof Building o) && (o.block instanceof PayloadTurret owner)){
            float dist = Tmp.v1.set(x, y).dst(o);
            damageP = owner.maxDamagePercent * dist / (owner.range * owner.blockRangeMultiplier);
            ownerDamage = owner.damage;
            selfDamage = dist > owner.safeRange ? damageP : 0f;
        }

        bp.set(x, y, b.rotation());
        Building tile = bp.build;
        int tx = World.toTile(x - tile.block.offset), ty = World.toTile(y - tile.block.offset);
        Tile tileon = Vars.world.tile(tx, ty);

        if(tileon != null && Build.validPlace(tile.block, tile.team, tx, ty, tile.rotation, false)){
            int rot = (int)((b.rotation() + 45f) / 90f) % 4;

            bp.place(tileon, rot);
            if(tileon.build != null && selfDamage * bp.build.health > 0.1f) tileon.build.damage(selfDamage * bp.build.health);

            Fx.unitDrop.at(tile);
            Fx.placeBlock.at(tileon.drawx(), tileon.drawy(), tileon.block().size);
            healthDamage(bp.build.block.health * ownerDamage * damageP, b);
        }
        else{
            //Fx.dynamicExplosion.at(tile, bp.block().size / 1.3f);
            healthDamage(bp.build.block.health * ownerDamage * 0.65f, b);
            if(tileon != null){
                try{
                    tile.tile = tileon;
                    tile.onDestroyed();
                }
                catch(Exception ignore){
                }
            }
        }
    }

    public void dropUnit(UnitPayload up, Bullet b, float x, float y, @Nullable Tile on){
        if(Vars.net.client()) Vars.netClient.clearRemovedEntity(up.unit.id);

        float damageP = 0.7f;
        float ownerDamage = b.damage, selfDamage = 0.3f;

        if((b.owner instanceof Building o) && (o.block instanceof PayloadTurret owner)){
            float dist = Tmp.v1.set(x, y).dst(o);
            damageP = owner.maxDamagePercent * dist / owner.range;
            ownerDamage = owner.damage;
            selfDamage = dist > owner.safeRange ? damageP : 0f;
        }

        Unit u = up.unit;

        //can't drop ground units
        if(on != null && !u.canPass(on.x, on.y)){
            healthDamage(up.unit.type.health * ownerDamage * 0.7f, b);
        }
        else{
            healthDamage(up.unit.type.health * ownerDamage * damageP, b);
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
        if(selfDamage * u.health > 0.1f) u.damage(u.health * selfDamage);
    }

    public void healthDamage(float health, Bullet b){
        //print("Health: " + health);
        Damage.damage(b.team, b.x, b.y, splashDamageRadius, health);
    }

    public void print(String pain){
        Log.info(pain);
    }
}
