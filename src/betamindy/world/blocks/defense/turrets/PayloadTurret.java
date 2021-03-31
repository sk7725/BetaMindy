package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.ObjectSet;
import arc.util.*;
import arc.util.io.*;
import betamindy.BetaMindy;
import betamindy.content.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.Cicon;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class PayloadTurret extends Turret {
    /** Base damage multiplier */
    public float damage = 1.3f;
    /** Range multiplier for block payloads. Unused. */
    public float blockRangeMultiplier = 1f;
    /** Percentage of health that gets converted to area damage at max range */
    public float maxDamagePercent = 0.5f;
    /** Maximum range the fired payload does not lose health, note that area damage will still scale inside this range */
    public float safeRange = range * 0.3f;
    public BulletType shootType = MindyBullets.payBullet;
    public BulletType homingShootType = MindyBullets.homingPay;
    /** Payload draw offset, draw scale */
    public float payloadOffset = 15f,  payloadScale = 0.8f;
    /** Payload fire offset*/
    public float payloadShootOffset = 15f;
    /** Maximum accepted payload size */
    public float maxPaySize = 4.5f;
    
    public boolean drawTop = false;

    public Effect acceptEffect = MindyFx.cannonAccept;
    public TextureRegion topRegion;

    protected ObjectSet<Block> homingBlocks = new ObjectSet<Block>(2);

    public PayloadTurret(String name){
        super(name);

        targetAir = false;
        outputsPayload = true;//needs to be true to accept payloads, is this intended?
        outputFacing = false;
        sync = true;
    }

    @Override
    public void init(){
        super.init();
        homingBlocks.addAll(MindyBlocks.siliconWall, MindyBlocks.siliconWallLarge);
    }
    
    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
        //Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * blockRangeMultiplier, Pal.accentBack);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, safeRange, Pal.heal);
    }

    @Override
    public void setStats(){
        super.setStats();

        //stats.add(Stat.shootRange, "@", Core.bundle.format("stat.blockrange", blockRangeMultiplier));
        stats.add(Stat.damage, "@", Core.bundle.format("stat.dphealth", damage * maxDamagePercent * tilesize / range));//dmg/health*range
    }

    public class PayloadTurretBuild<T extends Payload> extends TurretBuild{
        public @Nullable T payload;
        protected float payheat;

        @Override
        public void updateTile(){
            unit.ammo(Mathf.num(payload != null) * unit.type().ammoCapacity);
            payheat = Mathf.lerpDelta(payheat, 1f, 0.1f);

            super.updateTile();
        }

        public TextureRegion payloadIcon(){
            if(payload instanceof BuildPayload){
                return ((BuildPayload)payload).build.block.icon(Cicon.full);
            }
            else if(payload instanceof UnitPayload){
                return ((UnitPayload)payload).unit.type().icon(Cicon.full);
            }
            return Core.atlas.find("error");
        }

        public float rotationOffset(){
            return (payload instanceof BuildPayload) && ((BuildPayload)payload).block().rotate ? 0f : -90f;
        }

        @Override
        public void draw(){
            super.draw();
            if(payload != null) {
                TextureRegion payIcon = payloadIcon();
                tr2.trns(rotation, -recoil + payloadOffset);
                Draw.mixcol(team.color, 1f - payheat);
                Draw.rect(payIcon, x + tr2.x, y + tr2.y, payIcon.width * Draw.scl * Draw.xscl * payloadScale * payheat, payIcon.height * Draw.scl * Draw.yscl * payloadScale * payheat, rotation + rotationOffset());
                Draw.reset();
            }
            if(drawTop) Draw.rect(topRegion, x + tr2.x, y + tr2.y, rotation - 90);
        }

        @Override
        protected void updateShooting(){
            if(payload != null) super.updateShooting();
        }

        @Override
        protected void bullet(BulletType type, float angle){
            if((payload instanceof BuildPayload) && homingBlocks.contains(((BuildPayload)payload).block())) type = homingShootType;
            tr.trns(rotation, payloadShootOffset, Mathf.range(xRand));
            float lifeScl = type.scaleVelocity ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / type.range(), minRange / type.range(), range / type.range()) : 1f;

            type.create(this, team, x + tr.x, y + tr.y, angle, -1f, 1f, lifeScl, payload);
            payload = null;
        }


        @Override
        public double sense(LAccess sensor){
            switch(sensor){
                case ammo: return Mathf.num(payload != null);
                case ammoCapacity: return 1;
                default: return super.sense(sensor);
            }
        }

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
            return range;
            //return payload == null || (payload instanceof UnitPayload) ? range : blockRangeMultiplier * range;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, realRange(), team.color);
            if(team == Vars.player.team()) Drawf.dashCircle(x, y, safeRange, Pal.heal);
        }

        @Override
        public boolean acceptPayload(Building source, Payload pay){
            return payload == null && pay.size() / tilesize <= maxPaySize;
        }

        @Override
        public void handlePayload(Building source, Payload pay){
            payload = (T)pay;
            Tmp.v1.set(source).sub(this).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
            acceptEffect.at(Tmp.v1.x + x, Tmp.v1.y + y);
            payheat = 0f;

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

            if(mobile) BetaMindy.mobileUtil.writePayload(payload, write);
            else Payload.write(payload, write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(mobile) payload = BetaMindy.mobileUtil.readPayload(read);
            else payload = Payload.read(read);
        }

        public void drawPayload(){
        }

        @Override
        public boolean canPickup(){
            return false;
        }
    }
}
