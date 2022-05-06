package betamindy.world.blocks.units;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.distribution.*;
import betamindy.world.blocks.logic.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class Claw extends Block {
    public float range = 36f;
    public float grabRange = 24f;
    public float grabOffset = grabRange / 2f + 4f;
    public float pullStrength = 2f;
    public float maxTension = 210f;

    public float maxSize = 24f;
    public int maxBlockSize = 1;

    public TextureRegion topRegion;
    public TextureRegion handRegion, handRegion2,  handOverlay, handOutline;
    public TextureRegion armRegion;

    public float clawOffset = 9f, clawGrab = 20f, clawOpen = 0f, clawFail = -7f, clawGrabBlock = 10f;

    private final Vec2 toV = new Vec2();
    private UnitPayload tempUnitPayload = null;
    //after being logic-controlled and this amount of time passes, the claw will resume normal AI
    public final static float logicControlCooldown = 60 * 3;

    public Effect grabEffect = Fx.pickup;
    public Sound grabSound = Sounds.door;
    public Sound detachSound = Sounds.place;

    public Claw(String name){
        super(name);
        update = true;
        rotate = true;
        solid = true;
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("tension", (ClawBuild entity) -> new Bar(() -> Core.bundle.get("bar.tension"), () -> Pal.ammo, () -> Mathf.clamp(entity.tension / maxTension)));
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, (range + maxSize + 16f) * 2f);
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        handRegion = atlas.find(name + "-hand");
        armRegion = atlas.find(name + "-arm", "betamindy-claw-arm");
        handRegion2 = atlas.find(name + "-hand2");
        handOverlay = atlas.find(name + "-hand-top");
        handOutline = atlas.find(name + "-hand-outline");
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);
        Drawm.outlineRegion(packer, topRegion, outlineColor, name + "-top");
        Drawm.outlineRegion(packer, handRegion, outlineColor, name + "-hand");
        Drawm.outlineRegion(packer, handRegion2, outlineColor, name + "-hand2");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.add(Stat.payloadCapacity, "[white]\uF800[] "+(int)(maxSize / tilesize)+" "+Core.bundle.get("unit.blockssquared") + " [white]\uF8AE[] "+maxBlockSize+"x"+maxBlockSize);
    }

    public class ClawBuild extends Building implements SpinDraw, SpinUpdate, ControlBlock {
        public @Nullable Unit unit;
        public @Nullable BuildPayload heldBuild;
        protected final Vec2 lastV = new Vec2(), targetV = new Vec2();
        public float tension = 0f;
        public float uptime = 1f;

        public float logicControlTime = -1f;
        public boolean logicGrab = false;

        public float grabFail = 0f;
        public boolean grabFailed = false;

        public @Nullable BlockUnitc blockUnit;

        public void checkUnit(){
            if(unit != null && (unit.dead() || !unit.isValid())) unit = null;
        }
        public boolean hostile() { return unit == null || unit.team != team || unit.isPlayer(); }

        public void grab(float x, float y, float r){
            if(unit != null) return;
            Tmp.v2.trns(r, grabOffset).add(x, y);
            unit = Units.closestEnemy(null, Tmp.v2.x, Tmp.v2.y, grabRange / 2f, u -> u.type != UnitTypes.block && u.hitSize() <= maxSize && !u.hasEffect(MindyStatusEffects.ouch));
            if(unit != null){
                grabSound.at(x, y);
                grabEffect.at(x, y, unit.hitSize());
            }
            else{
                //free a unit from its demise
                Tile tile = world.tileWorld(x, y);
                if(tile == null || tile.build == null) return;

                //prevent grabbing unit payloads from solid blocks, because it will fail if the unit is a ground one
                if(!tile.solid() && (tile.build.getPayload() instanceof UnitPayload up)){
                    if(up.unit.hitSize <= maxSize){
                        Payload tp = tile.build.takePayload();
                        if(tp == null) return;
                        if(!(tp instanceof UnitPayload) || !tp.dump()){
                            //put it back, put it back!
                            tile.build.handlePayload(tile.build, tp);
                            return;
                        }
                        unit = ((UnitPayload)tp).unit;
                        grabSound.at(x, y);
                        grabEffect.at(x, y, 8f);
                    }
                }
            }
        }

        public void grabBuild(float x, float y){
            if(heldBuild != null) return;
            Tile tile = world.tileWorld(x, y);
            if(tile == null || tile.build == null) return;

            if(tile.build.getPayload() instanceof BuildPayload bp){
                if(bp.block().size <= maxBlockSize && !(bp.block() instanceof Claw)){
                    heldBuild = (BuildPayload) tile.build.takePayload();
                    grabSound.at(x, y);
                    grabEffect.at(x, y, 8f);
                    return;
                }
            }

            if(tile.build.block.size <= maxBlockSize && tile.build.canPickup() && tile.team() == team && !(tile.block() instanceof Claw)){
                Building build = tile.build;
                build.pickedUp();
                build.tile.remove();
                heldBuild = new BuildPayload(build);
                grabSound.at(x, y);
                grabEffect.at(x, y, 8f);
            }
        }

        public void detach(float x, float y){
            if(!hostile()){
                //shove the unit into demise
                Building on = world.buildWorld(x, y);
                if(tempUnitPayload == null) tempUnitPayload = new UnitPayload(unit);
                else tempUnitPayload.unit = unit;
                if(on != null && on.acceptPayload(on, tempUnitPayload)){
                    unit.remove();
                    on.handlePayload(on, new UnitPayload(unit));
                    unit = null;
                }
            }
            if(unit != null) unit.apply(MindyStatusEffects.ouch, 120f);
            unit = null;
            detachSound.at(x, y);
            Fx.unitDrop.at(x, y);
        }

        public boolean detachBuild(float x, float y){
            boolean dropped = dropBlock(heldBuild, x, y);
            if(dropped){
                heldBuild = null;
                detachSound.at(x, y);
            }
            return dropped;
        }

        public boolean dropBlock(BuildPayload payload, float x, float y){
            Building tile = payload.build;
            int tx = World.toTile(x - tile.block.offset), ty = World.toTile(y - tile.block.offset);
            Tile on = world.tile(tx, ty);
            //drop off payload on an acceptor if possible
            if(on != null && on.build != null && on.build.acceptPayload(on.build, payload)){
                Fx.unitDrop.at(on.build);
                on.build.handlePayload(on.build, payload);
                return true;
            }
            //drop it on the floor le deja vu lu mu tu su ku
            if(on != null && Build.validPlace(tile.block, tile.team, tx, ty, tile.rotation, false)){
                payload.place(on, tile.rotation);

                if(blockUnit != null && blockUnit.isPlayer()){
                    payload.build.lastAccessed = blockUnit.getControllerName();
                }

                Fx.unitDrop.at(tile);
                Fx.placeBlock.at(on.drawx(), on.drawy(), on.block().size);
                return true;
            }

            return false;
        }

        public void updateUnit(float x, float y, float r, boolean spinning, float spinningRadius){
            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }
            boolean con = !isPayload() && !spinning && logicControlled();
            boolean on = (!spinning && canConsume() && efficiency() > 0.9f) || (spinning && notNullified(x, y));
            if(!spinning && isPayload()) on = notNearNull(x, y);

            if(!con) targetV.trns(r, spinning ? Math.min(spinningRadius, range) : 8f);
            if(on){
                uptime = Mathf.lerpDelta(uptime, 1f, 0.04f);
            }else{
                uptime = Mathf.lerpDelta(uptime, 0f, 0.02f);
            }

            if(unit == null && (!con || logicGrab) && on){
                Tmp.v1.set(lastV).clamp(4f, range  - grabRange / 2f + 4f).add(x, y);
                if(heldBuild == null) grab(Tmp.v1.x, Tmp.v1.y, lastV.angle());
                if(!spinning && unit == null && con && logicGrab) grabBuild(lastV.x + x, lastV.y + y); //does not normally grab blocks
            }
            if(unit != null) checkUnit();

            if(unit == null){
                if(on) lastV.lerp(targetV, 0.05f * (spinning ? Time.delta : edelta()));
                tension = 0f;
                if(!con || !logicGrab) grabFailed = false;
                if(heldBuild != null){
                    if(!on || !con || !logicGrab){
                        if(detachBuild(lastV.x + x, lastV.y + y)) return;
                    }
                    heldBuild.set(lastV.x + x, lastV.y + y, 0f);
                    return;
                }

                if(grabFail > 0f) grabFail -= delta() * 0.05f;
                if(con && logicGrab && !grabFailed && heldBuild == null){
                    grabFailed = true;
                    grabFail = 1f;
                    Sounds.click.at(x, y);
                }
                return;
            }

            toV.set(unit).sub(x, y);
            lastV.set(toV);
            float dst = toV.len();
            if(tension >= 0f) tension -= Math.min(edelta() * 0.3f, 0.8f);
            if(!unit.vel.isZero(0.01f)/* && ((targetV.x - toV.x) * unit.vel.x <= 0f && (targetV.y - toV.y) * unit.vel.y <= 0f)*/) tension += delta() * (hostile() ? 2.5f : 1f);

            if(dst > range){
                Tmp.v1.set(toV).setLength(range).add(x, y).sub(unit);
                if((!spinning && !isPayload() && dst < range + 24f) || dst < range + 16f) unit.move(Tmp.v1.x, Tmp.v1.y);
                //unit.vel.setZero();
                if(unit.dst(x, y) > range + 4f || tension > maxTension){
                    detach(unit.x, unit.y);
                    return;
                }
            }
            Tmp.v1.set(targetV).add(x, y).sub(unit).clamp(0f, pullStrength).scl(Time.delta * 0.8f);
            //if(Tmp.v1.len2() >= pullStrength * pullStrength * 0.85f)
            unit.move(Tmp.v1.x, Tmp.v1.y);
            if(!on || (con && !logicGrab)) detach(unit.x, unit.y);
        }

        public void updatePlayer(){
            blockUnit.health(health);
            blockUnit.ammo(blockUnit.type().ammoCapacity * (unit == null ? 0f : 1f));
            blockUnit.team(team);
            blockUnit.set(x, y);

            //float angle = angleTo(blockUnit.aimX(), blockUnit.aimY());
            targetV.set(blockUnit.aimX(), blockUnit.aimY()).sub(this).clamp(0f, range + 2f);
            logicGrab = blockUnit.isShooting();
        }

        public void drawClaw(float x, float y, float r){
            float angle = lastV.angle();
            float lz = Draw.z();
            Draw.rect(region, x, y);
            Draw.z(Layer.flyingUnitLow - 0.1f);
            Draw.rect(topRegion, x, y);

            float offsetLen = unit == null ? (heldBuild == null ? clawOffset : 8f + heldBuild.block().size) : 5f + unit.hitSize / 2f;
            Tmp.v1.set(lastV).sub(Tmp.v3.trns(angle, offsetLen)).add(x, y);
            Draw.z(Layer.flyingUnitLow - 0.11f);
            Lines.stroke(6f);
            Draw.alpha(Math.max(0.25f, uptime) * Renderer.bridgeOpacity);
            Draw.mixcol(Color.white, unit == null || (tension / maxTension) < 0.3f ? 0f : Mathf.absin(Time.globalTime, 3f + maxTension / tension, Mathf.clamp(tension / maxTension)));
            Lines.line(armRegion, x, y, Tmp.v1.x, Tmp.v1.y, false);
            Draw.color();
            Draw.mixcol();
            Draw.z(Layer.flyingUnit + 0.1f);

            float clawa = (unit == null ? (heldBuild != null ? clawGrabBlock + 30f * heldBuild.block().size - 30f : clawOpen + clawFail * grabFail) : clawGrab + unit.hitSize / 2f);
            Draw.rect(handOutline, Tmp.v1.x, Tmp.v1.y, angle - 90f);
            Draw.rect(handRegion2, Tmp.v1.x, Tmp.v1.y, angle + clawa - 90f);
            Draw.rect(handRegion, Tmp.v1.x, Tmp.v1.y, angle - clawa - 90f);
            Draw.rect(handOverlay, Tmp.v1.x, Tmp.v1.y, angle - 90f);

            if(heldBuild != null) Draw.rect(heldBuild.build.block.fullIcon, lastV.x + x, lastV.y + y, heldBuild.block().rotate ? heldBuild.build.rotation * 90f : 0f);
            Draw.z(lz);
        }

        public boolean notNullified(float x, float y){
            Building n = world.buildWorld(x, y);
            if(n == null || !(n.block instanceof Disabler)) return true;
            return !n.canConsume();
        }

        public boolean notNearNull(float x, float y){
            Tile t = world.tileWorld(x, y);
            if(t == null) return true;
            if(!notNullified(x, y)) return false;
            for(int i = 0; i < 4; i++){
                Building n = t.nearbyBuild(i);
                if(n == null || !(n.block instanceof Disabler) || !n.canConsume()) continue;
                return false;
            }
            return true;
        }

        @Override
        public void pickedUp(){
            super.pickedUp();
            detach(x, y);
        }

        @Override
        public boolean conductsTo(Building other){
            return false;
        }

        @Override
        public void draw(){
            drawClaw(x, y, rotation * 90f);
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            drawClaw(x, y, dr + rotation * 90f);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Lines.stroke(1f, Pal.place);
            Lines.poly(x + targetV.x, y + targetV.y, 4, tilesize / 2f * Mathf.sqrt2, Time.time);
            Draw.color();
            Drawf.dashCircle(x, y, range, team.color);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(blockUnit != null && isControlled()) updatePlayer();
            updateUnit(x, y, rotation * 90f, false, 0f);
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            updateUnit(sx, sy, rawRot + rotation * 90f, true, srad);
        }

        public boolean logicControlled(){
            return logicControlTime > 0f || (blockUnit != null && isControlled());
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.shoot && (blockUnit == null || !blockUnit.isPlayer())){
                targetV.set(World.unconv((float)p1), World.unconv((float)p2)).sub(this);
                targetV.clamp(0f, range);
                logicControlTime = logicControlCooldown;
                logicGrab = !Mathf.zero(p3);
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4){
            if(type == LAccess.shootp && (blockUnit == null || !blockUnit.isPlayer())){
                logicControlTime = logicControlCooldown;
                logicGrab = !Mathf.zero(p2);

                if(p1 instanceof Posc){
                    targetV.set((Posc)p1).sub(this);
                    targetV.clamp(0f, range);
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case shootX -> World.conv(targetV.x + x);
                case shootY -> World.conv(targetV.y + y);
                case shooting -> heldBuild != null ? 2 : (unit != null ? 1 : 0);
                default -> super.sense(sensor);
            };
        }

        @Override
        public Object senseObject(LAccess sensor){
            return switch(sensor){
                case payloadType -> heldBuild != null ? heldBuild.block() : unit;
                default -> super.senseObject(sensor);
            };
        }

        @Override
        public Unit unit(){
            if(blockUnit == null){
                blockUnit = (BlockUnitc)UnitTypes.block.create(team);
                blockUnit.tile(this);
            }
            return (Unit)blockUnit;
        }

        @Override
        public boolean canControl(){
            return true;
        }

        @Override
        public boolean shouldAutoTarget(){
            return false;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            int mode = (heldBuild != null) ? 2 : (unit == null ? 0 : 1);
            write.b(mode);
            write.f(lastV.x);
            write.f(lastV.y);
            if(mode == 1){
                TypeIO.writeUnit(write, unit);
            }
            else if(mode == 2){
                BetaMindy.mobileUtil.writePayload(heldBuild, write);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision == 1){
                int mode = read.b();
                lastV.set(read.f(), read.f());
                if(mode == 1){
                    unit = TypeIO.readUnit(read);
                }
                else if(mode == 2){
                    heldBuild = BetaMindy.mobileUtil.readPayload(read);
                }
            }
        }
    }
}
