package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class PayloadTurret extends Turret {
    /** Base damage multiplier */
    public float damage = 2f;
    public float blockRangeMultiplier = 0.8f;
    /** Percentage of health that gets converted to area damage at max range */
    public float maxDamagePercent = 0.5f;
    /** Maximum range the fired payload does not lose health, note that area damage will still scale inside this range */
    public float safeRange = range * 0.3f;
    public final BulletType shootType = MindyBullets.payBullet;

    public Effect acceptEffect = Fx.select;

    public PayloadTurret(String name){
        super(name);

        targetAir = false;
        outputsPayload = false;
        sync = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * blockRangeMultiplier, Pal.accentBack);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, safeRange, Pal.heal);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.shootRange, "@", Core.bundle.format("stat.blockrange", blockRangeMultiplier));
        stats.remove(Stat.damage);
        stats.add(Stat.damage, "@", Core.bundle.format("stat.dphealth", damage * maxDamagePercent * tilesize / range));//dmg/health*range
    }

    @SuppressWarnings("unchecked")
    public class PayloadTurretBuild<T extends Payload> extends TurretBuild{
        public @Nullable T payload;

        @Override
        public void updateTile(){
            unit.ammo(Mathf.num(payload != null) * unit.type().ammoCapacity);

            super.updateTile();
        }

        @Override
        protected void updateShooting(){
            if(payload != null) super.updateShooting();
        }

        @Override
        protected void bullet(BulletType type, float angle){
            float lifeScl = type.scaleVelocity ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / type.range(), minRange / type.range(), range / type.range()) : 1f;

            type.create(this, team, x + tr.x, y + tr.y, angle, -1f, 1f, lifeScl, payload);
            payload = null;
        }

        /*
        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case ammo -> Mathf.num(payload != null);
                case ammoCapacity -> 1;
                default -> super.sense(sensor);
            };
        }*/

        @Override
        public BulletType useAmmo(){
            //nothing used directly
            return shootType;
        }

        @Override
        public boolean hasAmmo(){
            //you can always rotate, but never shoot if there's no payload
            return true;
        }

        @Override
        public BulletType peekAmmo(){
            return shootType;
        }

        public float realRange(){
            return payload == null || (payload instanceof UnitPayload) ? range : blockRangeMultiplier * range;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, realRange(), team.color);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.payload == null;
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            this.payload = (T)payload;
            Tmp.v1.set(source).sub(this).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
            acceptEffect.at(Tmp.v1);

            updatePayload(source.angleTo(this));
        }

        @Override
        public Payload getPayload(){
            return payload;
        }

        @Override
        public Payload takePayload(){
            T t = payload;
            payload = null;
            return t;
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            if(payload != null) payload.dump();
        }

        public void updatePayload(float r){
            if(payload != null){
                payload.set(x, y, r);
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            Payload.write(payload, write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            payload = Payload.read(read);
        }

        public void drawPayload(){
            if(payload != null){
                //TODO
            }
        }

        @Override
        public boolean canPickup(){
            return false;
        }
    }
}
