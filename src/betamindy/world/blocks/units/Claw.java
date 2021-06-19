package betamindy.world.blocks.units;

import arc.audio.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.distribution.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
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
    public float pullStrength = 3f;
    public float maxTension = 180f;
    public float maxSize = 24f;

    public TextureRegion topRegion;
    public TextureRegion handRegion, handRegion2,  handOverlay, handOutline;
    public TextureRegion armRegion;

    public float clawOffset = 9f, clawGrab = 30f, clawOpen = 0f, clawFail = -7f, clawGrabBlock = 10f;

    private final Vec2 toV = new Vec2();
    //after being logic-controlled and this amount of time passes, the claw will resume normal AI
    public final static float logicControlCooldown = 60 * 3;

    public Effect grabEffect = Fx.none;//todo hmm
    public Sound grabSound = Sounds.door;
    public Sound detachSound = Sounds.click;

    public Claw(String name){
        super(name);
        update = true;
        rotate = true;
        expanded = true;
        solid = true;
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
        stats.add(Stat.payloadCapacity, maxSize / tilesize, StatUnit.blocksSquared);
    }

    public class ClawBuild extends Building implements SpinDraw, SpinUpdate, ControlBlock {
        public @Nullable Unit unit;
        public @Nullable BuildPayload heldBuild;
        protected final Vec2 lastV = new Vec2(), targetV = new Vec2();
        public float tension = 0f;//TODO better escaping
        public float uptime = 1f;

        public float logicControlTime = -1f;
        public boolean logicGrab = false;

        public float grabFail = 0f;
        public boolean grabFailed = false;

        public @Nullable BlockUnitc blockUnit;

        public void checkUnit(){
            if(unit != null && (unit.dead() || !unit.isValid())) unit = null;
        }

        public void grab(float x, float y, float r){
            if(unit != null) return;
            Tmp.v2.trns(r, grabOffset).add(x, y);
            unit = Units.closestEnemy(null, Tmp.v2.x, Tmp.v2.y, grabRange / 2f, u -> u.type != UnitTypes.block && u.hitSize() <= maxSize && !u.hasEffect(MindyStatusEffects.ouch));
            if(unit != null){
                grabSound.at(x, y);
                grabEffect.at(x, y, unit.hitSize());
            }
        }

        public void grabBuild(float x, float y){
            if(heldBuild != null) return;
            Tile tile = world.tileWorld(x, y);
            if(tile == null || tile.build == null) return;

            if(tile.build.getPayload() instanceof BuildPayload){
                BuildPayload bp = (BuildPayload) tile.build.getPayload();
                if(bp.block().size == 1 && !(bp.block() instanceof Claw)){
                    heldBuild = (BuildPayload) tile.build.takePayload();
                    grabSound.at(x, y);
                    grabEffect.at(x, y, 8f);
                    return;
                }
            }

            if(tile.build.block.size == 1 && tile.build.canPickup() && tile.team() == team && !(tile.block() instanceof Claw)){
                Building build = tile.build;
                build.pickedUp();
                tile.remove();
                heldBuild = new BuildPayload(build);
                grabSound.at(x, y);
                grabEffect.at(x, y, 8f);
            }
        }

        public void detach(float x, float y){
            unit.apply(MindyStatusEffects.ouch, 45f);
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
                int rot = (int)((rotation + 45f) / 90f) % 4;
                payload.place(on, rot);

                if(blockUnit != null && blockUnit.isPlayer()){
                    payload.build.lastAccessed = blockUnit.getControllerName();
                }

                Fx.unitDrop.at(tile);
                Fx.placeBlock.at(on.drawx(), on.drawy(), on.block().size);
                return true;
            }

            return false;
        }

        public void updateUnit(float x, float y, float r, boolean spinning){
            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }
            boolean con = !spinning && logicControlled();
            boolean on = (consValid() && efficiency() > 0.9f) || spinning;

            if(!con) targetV.trns(r, 8f);
            if(on){
                uptime = Mathf.lerpDelta(uptime, 1f, 0.04f);
            }else{
                uptime = Mathf.lerpDelta(uptime, 0f, 0.02f);
            }

            if(unit == null && (!con || logicGrab) && on){
                Tmp.v1.set(targetV).clamp(4f, range  - grabRange / 2f + 4f).add(x, y);
                if(heldBuild == null) grab(Tmp.v1.x, Tmp.v1.y, targetV.angle());
                if(!spinning && unit == null && con && logicGrab) grabBuild(Tmp.v1.x, Tmp.v1.y); //does not normally grab blocks
            }
            if(unit != null) checkUnit();

            if(unit == null){
                if(on) lastV.lerp(targetV, 0.05f * edelta());
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
            if(tension >= 0f) tension -= Math.min(edelta() * 0.4f, 0.8f);
            if(dst > range - 8f) tension += delta();

            if(dst > range){
                Tmp.v1.set(toV).setLength(range).add(x, y).sub(unit);
                unit.move(Tmp.v1.x, Tmp.v1.y);
                //unit.vel.setZero();
                if(unit.dst(x, y) > range + 4f || tension > maxTension){
                    detach(x, y);
                    return;
                }
            }
            Tmp.v1.set(targetV).add(x, y).sub(unit).clamp(0f, pullStrength).scl(Time.delta);
            unit.move(Tmp.v1.x, Tmp.v1.y);
            if(!on || (con && !logicGrab)) detach(x, y);
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

            Tmp.v1.set(lastV).sub(Tmp.v3.trns(angle, clawOffset)).add(x, y);
            Draw.z(Layer.flyingUnitLow - 0.11f);
            Lines.stroke(6f);
            Draw.alpha(Math.max(0.25f, uptime) * Renderer.bridgeOpacity);
            Lines.line(armRegion, x, y, Tmp.v1.x, Tmp.v1.y, false);
            Draw.color();
            Draw.z(Layer.flyingUnit + 0.1f);

            float clawa = (unit == null ? (heldBuild != null ? clawGrabBlock : clawOpen + clawFail * grabFail) : clawGrab);
            Draw.rect(handOutline, Tmp.v1.x, Tmp.v1.y, angle - 90f);
            Draw.rect(handRegion2, Tmp.v1.x, Tmp.v1.y, angle + clawa - 90f);
            Draw.rect(handRegion, Tmp.v1.x, Tmp.v1.y, angle - clawa - 90f);
            Draw.rect(handOverlay, Tmp.v1.x, Tmp.v1.y, angle - 90f);

            if(heldBuild != null) Draw.rect(heldBuild.icon(Cicon.full), lastV.x + x, lastV.y + y, heldBuild.block().rotate ? heldBuild.build.rotation * 90f : 0f);
            Draw.z(lz);
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
        public void update(){
            super.update();
            if(blockUnit != null && isControlled()) updatePlayer();
            updateUnit(x, y, rotation * 90f, false);
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            updateUnit(sx, sy, absRot, true);
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
    }
}
