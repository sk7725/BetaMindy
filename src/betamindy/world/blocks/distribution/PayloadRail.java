package betamindy.world.blocks.distribution;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import betamindy.world.blocks.payloads.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.payloads.*;

import java.util.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

//todo little roombas, allow it to pick up & place the blocks at the end
//scalar ore tier
public class PayloadRail extends PayloadConveyor {
    protected static final int stateMove = 0, stateLoad = 1, stateUnload = 2;
    public float loadTime = 60f, unloadTime = 40f;

    public TextureRegion roombaRegion;
    public Effect loadEffect = Fx.plasticburn;
    public Effect unloadEffect = Fx.plasticburn;
    private final Rect tr1 = new Rect(), tr2 = new Rect();
    private static final Vec2[] vecs = new Vec2[]{new Vec2(), new Vec2(), new Vec2(), new Vec2()};

    public PayloadRail(String name){
        super(name);
        interp = Interp.linear;
        size = 1;
        payloadLimit = 2f;
        moveTime = 20f;
        moveForce = 70f;
    }

    @Override
    public void load(){
        super.load();
        roombaRegion = atlas.find(name + "-roomba", "plastanium-conveyor-stack");
    }

    public class PayloadRailBuild extends PayloadConveyorBuild {
        public int state;
        public boolean loading = false, unloading = false; //true if the roomba is moving payload blocks / units in and out
        public float loadProgress;

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            state = stateLoad;
            if(next == null) state = stateUnload;
            else{
                for(int i = 0; i < 4; i++){
                    if(i == rotation) continue;
                    if(blends(i)){
                        state = stateMove;
                        break;
                    }
                }
            }
        }

        @Override
        public void updateTile(){
            if(item == null && (loading || unloading)){
                loading = false;
                unloading = false;
            }

            if(loading){
                boolean nextTime = updateIdle();
                if(loadProgress >= loadTime && nextTime){
                    loading = false;
                    animation = 0.5f;
                    return;
                }
                float f = Mathf.clamp(loadProgress / loadTime);
                Tmp.v2.set(item).lerp(this, f);
                item.set(Tmp.v2.x, Tmp.v2.y, rotdeg());
                loadProgress += edelta();
            }
            else if(unloading && item instanceof BuildPayload bp){
                Tile other = destTile(bp.block().size, rotation);
                boolean nextTime = updateIdle();
                if(loadProgress >= unloadTime && nextTime){
                    loadProgress = unloadTime;
                    if(other != null && RBuild.validPlace(bp.block(), other.x, other.y, true)){
                        //place da blocc
                        bp.place(other, rotation);
                        item = null;
                        unloading = false;
                        unloadEffect.at(this);
                        return;
                    }
                }
                float f = Mathf.clamp(loadProgress / unloadTime);
                if(other != null){
                    float o = bp.block().offset;
                    Tmp.v1.set(other.worldx() + o, other.worldy() + o);
                    Tmp.v2.set(item).lerp(Tmp.v1, f);
                    item.set(Tmp.v2.x, Tmp.v2.y, rotdeg());
                }
                loadProgress += edelta();
            }
            else{
                if(state == stateLoad && item == null){
                    //look for new items
                    if(updateIdle() && nearby((rotation + 2) % 4) != null){
                        Building front = nearby((rotation + 2) % 4);
                        if(front.team == team && front.canPickup() && front.block.size <= payloadLimit && !(front.block instanceof PayloadConveyor)){
                            front.pickedUp();
                            front.tile.remove();
                            item = new BuildPayload(front);
                            stepAccepted = curStep();
                            itemRotation = rotdeg();
                            animation = 0f;
                            loading = true;
                            loadProgress = 0f;
                        }
                    }
                }
                else{
                    super.updateTile();
                }
            }
        }

        public boolean updateIdle(){
            if(!enabled) return false;

            if(item != null){
                item.update(false);
            }

            lastInterp = curInterp;
            curInterp = fract();
            //rollover skip
            if(lastInterp > curInterp) lastInterp = 0f;
            progress = time() % moveTime;
            int curStep = curStep();
            if(curStep > step){
                step = curStep;
                return true;
            }
            return false;
        }

        @Override
        public void moveFailed(){
            super.moveFailed();
            if(state == stateUnload && item instanceof BuildPayload){
                unloading = true;
                loadProgress = 0f;
            }
        }

        @Override
        public void moved(){
            super.moved();
            if(state == stateLoad) loadEffect.at(this);
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            super.handlePayload(source, payload);
            if(source != this && source.block != block) loadEffect.at(this);
        }

        public Tile destTile(){
            return (item instanceof BuildPayload bp) ? destTile(bp.block().size, rotation) : destTile(1, rotation);
        }

        public Tile destTile(int size, int dir){
            Tile tile = this.tile == emptyTile ? Vars.world.tileWorld(x, y) : this.tile;
            if(tile == null) return null;
            if(size <= 1) return tile.nearby(dir);
            else if(size % 2 == 1){
                int o = size / 2 + 1;
                return tile.nearby(o * Geometry.d4x(dir), o * Geometry.d4y(dir));
            }
            else{
                int o = size / 2 + 1;
                return tile.nearby(o * Geometry.d4x(dir) + RBuild.evenOffsets[dir][0], o * Geometry.d4y(dir) + RBuild.evenOffsets[dir][1]);
            }
        }

        @Override
        public void unitOn(Unit unit){
            if(item != null){
                unit.hitboxTile(tr1);
                tr2.setCentered(item.x(), item.y(), item.size());
                Tmp.v2.set(Geometry.overlap(tr1, tr2, rotation % 2 == 1));
                unit.move(Tmp.v2.x, Tmp.v2.y);
            }
        }

        @Override
        public void draw(){
            super.draw();
            if(item != null){
                float rot = Mathf.slerp(itemRotation, rotdeg(), animation);
                float len = Mathf.clamp(item.size() / 2f, 1f, 7f);
                if(loading){
                    float f = Mathf.clamp(loadProgress / loadTime);
                    Tmp.v1.trns(rotdeg(), len * f).add(this);
                    Draw.z(Layer.blockOver - 1f);
                    Draw.rect(roombaRegion, Tmp.v1.x, Tmp.v1.y, rotdeg() + 180f * (1f - f));
                    drawBlockBeam(Tmp.v1);
                }
                else if(unloading){
                    float f = Mathf.clamp(loadProgress / unloadTime);
                    Draw.z(Layer.blockOver - 1f);
                    Draw.rect(roombaRegion, x, y, rotdeg());
                    drawBlockBeam(Tmp.v1.set(this));
                }
                else{
                    Draw.z(Layer.blockOver - 1f);
                    if(state == stateUnload){
                        Draw.rect(roombaRegion, x, y, Mathf.approach(rot, 180f + rotdeg(), fract()));
                    }else{
                        Tmp.v1.trns(rot, len > 4.2f ? 4f + (len - 4f) * (Math.abs(45f - rot % 90f) / 45f) : len).add(item);
                        Draw.rect(roombaRegion, Tmp.v1.x, Tmp.v1.y, rot);
                    }
                }
            }
            else if(state == stateLoad){
                Draw.z(Layer.blockOver - 1f);
                Draw.rect(roombaRegion, x, y, rotdeg() + Mathf.sin(30f, 15f) + 180f);
            }
        }

        public void drawBlockBeam(Vec2 u){
            Draw.z(Layer.buildBeam);
            float tx = item.x();
            float ty = item.y();

            Lines.stroke(1f, team.color);
            float px = u.x;
            float py = u.y;

            float sz = item.size() / 2f;
            float ang = u.angleTo(tx, ty);

            vecs[0].set(tx - sz, ty - sz);
            vecs[1].set(tx + sz, ty - sz);
            vecs[2].set(tx - sz, ty + sz);
            vecs[3].set(tx + sz, ty + sz);

            Arrays.sort(vecs, Structs.comparingFloat(vec -> -Angles.angleDist(u.angleTo(vec), ang)));

            Vec2 close = Geometry.findClosest(u.x, u.y, vecs);

            float x1 = vecs[0].x, y1 = vecs[0].y,
                    x2 = close.x, y2 = close.y,
                    x3 = vecs[1].x, y3 = vecs[1].y;

            Draw.alpha(1f);

            //Fill.square(e.x, e.y, e.rotation * tilesize/2f);

            if(renderer.animateShields){
                if(close != vecs[0] && close != vecs[1]){
                    Fill.tri(px, py, x1, y1, x2, y2);
                    Fill.tri(px, py, x3, y3, x2, y2);
                }else{
                    Fill.tri(px, py, x1, y1, x3, y3);
                }
            }else{
                Lines.line(px, py, x1, y1);
                Lines.line(px, py, x3, y3);
            }

            Fill.square(px, py, 1.8f + Mathf.absin(Time.time, 2.2f, 1.1f), ang + 45);

            Draw.reset();
        }
    }
}
