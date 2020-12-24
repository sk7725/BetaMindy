package betamindy.entities.bullet;

import arc.graphics.g2d.*;
import arc.util.Tmp;
import betamindy.world.blocks.defense.turrets.PayloadTurret;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.Damage;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

public class PayloadBullet extends ArtilleryBulletType {

    public PayloadBullet(){
        super();
        collidesAir = false;
        collidesGround = false;
        collides = false;
        scaleVelocity = true;
        hittable = false;
        absorbable = false;
        reflectable = false;
        drawSize = 240f;
        lifetime = 200f;
    }

    @Override
    public void draw(Bullet b){
        Draw.z(Layer.darkness);

        TextureRegion icon;
        if(b.data instanceof BuildPayload bp){
            icon = bp.build.block.icon(Cicon.full);
        }
        else if(b.data instanceof UnitPayload up){
            icon = up.unit.type().icon(Cicon.full);
        }
        else return;

        float rotation = b.fin() * 360f + b.rotation();
        Draw.color(Pal.shadow);
        Draw.rect(icon, b.x, b.y, rotation);

        float offset = b.fin() * (1 - b.fin()) * 8f;
        Draw.color();
        Draw.z(Layer.flyingUnit);
        Draw.rect(icon, b.x + offset, b.y + offset, rotation);

        Draw.reset();
    }

    @Override
    public void hit(Bullet b, float x, float y){
        if(b.data instanceof BuildPayload bp){
            PayloadTurret owner = (PayloadTurret) b.owner();
            bp.set(x, y, b.rotation());
            Building tile = bp.build;
            int tx = World.toTile(x - tile.block.offset), ty = World.toTile(y - tile.block.offset);
            Tile on = Vars.world.tile(tx, ty);
            if(on != null && Build.validPlace(tile.block, tile.team, tx, ty, tile.rotation, false)){
                float damageP = owner.maxDamagePercent * Tmp.v1.set(x, y).dst(tile) / (owner.range * owner.blockRangeMultiplier);
                int rot = (int)((b.rotation() + 45f) / 90f) % 4;
                bp.place(on, rot);

                Fx.unitDrop.at(tile);
                Fx.placeBlock.at(on.drawx(), on.drawy(), on.block().size);
                healthDamage(bp.block().health * owner.damage * damageP, b);
                tile.damage(damageP);
            }
            else {
                Fx.dynamicExplosion.at(tile, 3f);
                healthDamage(bp.block().health * owner.damage, b);
            }
        }
        super.hit(b, x, y);
    }

    public void healthDamage(float health, Bullet b){
        Damage.damage(b.team, b.x, b.y, health / 30f, health);
    }
}
