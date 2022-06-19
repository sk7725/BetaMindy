package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class PayloadTurret extends Turret{
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
    /** Maximum accepted payload size */
    public float maxPaySize = 4.5f;
    public float payloadSpeed = 0.7f, payloadRotateSpeed = 5f;
    public float loadTime = 20f;

    public TextureRegion inRegion, topRegion;

    protected ObjectSet<Block> homingBlocks = new ObjectSet<Block>(2);

    public PayloadTurret(String name){
        super(name);

        targetAir = false;
        outputsPayload = true;//needs to be true to accept payloads, is this intended?
        outputFacing = false;
        sync = true;
        //use last icon for outline
        outlinedIcon = -1;
    }

    @Override
    public void init(){
        super.init();
        homingBlocks.addAll(MindyBlocks.siliconWall, MindyBlocks.siliconWallLarge);
    }

    @Override
    public void load(){
        super.load();
        inRegion = atlas.find(name + "-in", "factory-in-" + size);
        topRegion = atlas.find(name + "-top", "factory-top-" + size);
    }

    @Override
    public TextureRegion[] icons(){
        TextureRegion base = ((DrawTurret)drawer).base;
        if(atlas.isFound(topRegion)) return new TextureRegion[]{base, inRegion, topRegion, region};
        return new TextureRegion[]{base, inRegion, region};
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

    //stolen from PayloadBlock
    public static boolean blends(Building build, int direction){
        int size = build.block.size;
        int trns = build.block.size/2 + 1;
        Building accept = build.nearby(Geometry.d4(direction).x * trns, Geometry.d4(direction).y * trns);
        return accept != null &&
            accept.block.outputsPayload &&

            //if size is the same, block must either be facing this one, or not be rotating
            ((accept.block.size == size
                && Math.abs(accept.tileX() - build.tileX()) % size == 0 //check alignment
                && Math.abs(accept.tileY() - build.tileY()) % size == 0
                && ((accept.block.rotate && accept.tileX() + Geometry.d4(accept.rotation).x * size == build.tileX() && accept.tileY() + Geometry.d4(accept.rotation).y * size == build.tileY())
                || !accept.block.rotate
                || !accept.block.outputFacing)) ||

                //if the other block is smaller, check alignment
                (accept.block.size != size &&
                    (accept.rotation % 2 == 0 ? //check orientation; make sure it's aligned properly with this block.
                        Math.abs(accept.y - build.y) <= Math.abs(size * tilesize - accept.block.size * tilesize)/2f : //check Y alignment
                        Math.abs(accept.x - build.x) <= Math.abs(size * tilesize - accept.block.size * tilesize)/2f   //check X alignment
                    )) && (!accept.block.rotate || accept.front() == build || !accept.block.outputFacing) //make sure it's facing this block
            );
    }

    public class PayloadTurretBuild<T extends Payload> extends TurretBuild{
        public @Nullable T payload;
        public Vec2 payVector = new Vec2();
        public float payRotation, loadProgress;
        public boolean rotating, loading, loaded, shooting;
        protected float payheat = 0f;

        @Override
        public void updateTile(){
            unit.ammo(Mathf.num(payload != null) * unit.type().ammoCapacity);
            if(!validateTarget()) target = null;

            wasShooting = false;

            curRecoil = Mathf.approachDelta(curRecoil, 0, 1 / recoilTime);
            heat = Math.max(heat - Time.delta / cooldownTime, 0);
            if(payheat > 0f) payheat = Mathf.lerpDelta(payheat, 0f, 0.09f);
            recoilOffset.trns(rotation, -Mathf.pow(curRecoil, recoilPow) * recoil);

            unit.health(health);
            unit.rotation(rotation);
            unit.team(team);
            unit.set(x, y);

            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }

            if(hasAmmo()){
                updateLoading();

                if(timer(timerTarget, targetInterval)){
                    findTarget();
                }

                if(validateTarget()){
                    boolean canShoot = true;

                    if(isControlled()){ //player behavior
                        targetPos.set(unit.aimX(), unit.aimY());
                        canShoot = unit.isShooting();
                    }else if(logicControlled()){ //logic behavior
                        canShoot = logicShooting;
                    }else{ //default AI behavior
                        targetPosition(target);

                        if(Float.isNaN(rotation)){
                            rotation = 0;
                        }
                    }

                    float targetRot = angleTo(targetPos);

                    if(shouldTurn()){
                        turnToTarget(targetRot);
                    }

                    if(Angles.angleDist(rotation, targetRot) < shootCone && canShoot){
                        wasShooting = true;
                        updateShooting();
                    }
                }

                if(shooting){
                    updateLaunching();
                }
            }else{
                moveInPayload(false); //Rotating is done elsewhere
            }

            if(coolant != null){
                updateCooling();
            }
        }

        public void updateLoading(){
            if(!loaded){
                loadPayload();
                rotatePayload();

                if(!loading && !rotating){
                    loaded = true;
                    payheat = 1f;
                }
            }
        }

        public void loadPayload(){
            loadProgress = Mathf.approach(loadProgress, shootY - recoil, payloadSpeed * delta());
            loading = !Mathf.equal(loadProgress, shootY - recoil, 0.01f);
        }

        public void rotatePayload(){
            if(!Angles.within(payRotation, rotation + rotationOffset(), 0.01f)){
                payRotation = Angles.moveToward(payRotation, rotation + rotationOffset(), payloadRotateSpeed * edelta());
                rotating = true;
            }else{
                rotating = false;
            }
        }

        public float rotationOffset(){
            return (payload instanceof BuildPayload) && ((BuildPayload)payload).block().rotate ? 0f : -90f;
        }

        @Override
        public void draw(){
            DrawTurret draw = (DrawTurret)drawer;
            Draw.rect(draw.base, x, y);

            //draw input
            for(int i = 0; i < 4; i++){
                if(blends(i)){
                    Draw.rect(inRegion, x, y, (i * 90f) - 180f);
                }
            }

            drawPayload();
            if(atlas.isFound(topRegion)){
                Draw.z(Layer.blockOver + 0.1f);
                Draw.rect(topRegion, x, y);
            }

            Draw.z(Layer.turret);

            Drawf.shadow(region, x + recoilOffset.x - elevation, y + recoilOffset.y - elevation, rotation - 90);
            //TODO this may be wrong, and you may want to have different drawing for the turret itself -Anuke
            draw.drawTurret((Turret)block, this);
        }

        public void drawPayload(){
            if(payload != null){
                updatePayload();

                if(loaded){
                    loadProgress = shootY - recoil;
                    payRotation = rotation + rotationOffset();
                }

                Draw.z(loaded ? Layer.turret + 0.01f : Layer.blockOver);
                //payload.draw() but with rotation
                Drawf.shadow(payload.x(), payload.y(), payload.size() * 2f);
                if(loaded && payheat > 0.001f) Draw.mixcol(team.color, payheat);
                Draw.rect(payload.icon(), payload.x(), payload.y(), payRotation);
                Draw.mixcol();
            }
        }

        public boolean blends(int direction){
            return PayloadBlock.blends(this, direction);
        }

        @Override
        public boolean shouldTurn(){
            return super.shouldTurn() && loaded;
        }

        @Override
        protected void updateShooting(){
            reloadCounter += delta() * peekAmmo().reloadMultiplier * baseReloadSpeed();

            if(reloadCounter > reload && loaded && !shooting){
                shooting = true;
            }
        }

        protected void updateLaunching(){
            loadProgress = 0f;
            loaded = false;
            shoot(peekAmmo());
            shooting = false;
            reloadCounter %= reload;
        }

        protected void shoot(BulletType type){
            super.shoot(type);
            curRecoil = 1f;
            loadProgress = 0f;
        }

        @Override
        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover){
            if((payload instanceof BuildPayload) && homingBlocks.contains(((BuildPayload)payload).block())) type = homingShootType;

            //TODO this logic (and this class) really needs testing -Anuke
            float
            bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset, shootY + yOffset),
            bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset, shootY + yOffset),
            shootAngle = rotation + angleOffset + Mathf.range(inaccuracy);

            float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range() / type.range) : 1f;

            type.create(this, team, bulletX, bulletY, shootAngle, -1f, 1f, lifeScl, payload);
            payload = null;

            (shootEffect == null ? type.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
            if(shake > 0){
                Effect.shake(shake, shake, this);
            }

            curRecoil = 1f;
            heat = 1f;
        }


        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case ammo -> Mathf.num(payload != null);
                case ammoCapacity -> 1;
                default -> super.sense(sensor);
            };
        }

        @Override
        public BulletType useAmmo(){
            //nothing used directly
            return shootType;
        }

        @Override
        public boolean hasAmmo(){
            return payload != null && hasArrived();
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
            return payload == null && pay.fits(maxPaySize);
        }

        @Override
        public void handlePayload(Building source, Payload pay){
            payload = (T)pay;
            this.payVector.set(source).sub(this).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
            this.payRotation = pay.rotation() + rotationOffset();
            if(payload instanceof BuildPayload bp && !bp.block().rotate) this.payRotation = 0f;

            updatePayload();
        }

        public void moveInPayload(boolean rotate){
            if(payload == null) return;

            updatePayload();

            if(rotate){
                payRotation = Angles.moveToward(payRotation, block.rotate ? rotdeg() : 90f, payloadRotateSpeed * edelta());
            }
            payVector.approach(Vec2.ZERO, payloadSpeed * delta());
        }

        public boolean hasArrived(){
            return payVector.isZero(0.01f);
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

        public void updatePayload(){
            if(payload != null){
                if(hasArrived()){
                    Tmp.v1.trns(rotation, payloadOffset);
                    Tmp.v1.add(recoilOffset);
                    payload.set(x + Tmp.v1.x, y + Tmp.v1.y, payRotation);
                }else{
                    payload.set(x + payVector.x, y + payVector.y, payRotation);
                }
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(payVector.x);
            write.f(payVector.y);
            write.f(payRotation);
            Payload.write(payload, write);

            write.f(loadProgress);
            write.bool(loaded);
            write.bool(shooting);
            write.bool(loading);
            write.bool(rotating);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 2){
                payVector.set(read.f(), read.f());
                payRotation = read.f();
                payload = Payload.read(read);

                loadProgress = read.f();
                loaded = read.bool();
                shooting = read.bool();
                loading = read.bool();
                rotating = read.bool();
            }else{
                if(mobile) payload = BetaMindy.mobileUtil.readPayload(read);
                else payload = Payload.read(read);
            }
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public boolean canPickup(){
            return false;
        }
    }
}