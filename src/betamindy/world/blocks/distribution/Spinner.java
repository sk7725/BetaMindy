package betamindy.world.blocks.distribution;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.world.blocks.logic.*;
import betamindy.world.blocks.payloads.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.payloads.PayloadBlock.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Spinner extends Block {
    public float spinTime = 16f;

    public TextureRegion baseRegion, topRegion, laser, laserEnd, altIcon, topRegion2;
    public TextureRegion[] sideRegions = new TextureRegion[2];
    public TextureRegion[][] sideRegions2 = new TextureRegion[2][2];

    public Color drawColor = Pal.heal;
    public float laserWidth = 0.4f;

    public int maxBlocks = 12;
    public final Boolf<Building> stickBool = b -> b.canPickup() && !(b.block == Blocks.phaseWall || b.block == Blocks.phaseWallLarge || b.block == Blocks.thoriumWall || b.block == Blocks.thoriumWallLarge);
    /** Whether to keep spinning when picked up. */
    public boolean inertia = false;
    public Color inertiaColor = Pal.accent;
    public boolean drawSpinSprite = true;
    public boolean drawTop = false;

    public final int[][] evenOffsets = {{-1, -1}, {0, -1}, {0, 0}, {-1, 0}};

    public Spinner(String name){
        super(name);

        update = true;
        rotate = true;
        solid = true;
        hasPower = true;
        configurable = true;
        saveConfig = true;

        group = BlockGroup.transportation;//TODO: Make own category for place-overs

        config(Boolean.class, (SpinnerBuild entity, Boolean b) -> entity.ccw = b);
    }

    @Override
    public void init(){
        super.init();
        clipSize = (maxBlocks + 16) * tilesize * 2f;
    }

    @Override
    public void load() {
        super.load();
        baseRegion = atlas.find(name + "-base", "betamindy-cog-base");
        topRegion = atlas.find(name + "-top", "betamindy-spinner-top");
        altIcon = atlas.find(name + "-alt");
        if(drawSpinSprite){
            laser = atlas.find(name + "-laser", "betamindy-spinner-laser");
            laserEnd = atlas.find(name + "-laser-end", "betamindy-spinner-laser-end");
            for(int i = 0; i < 2; i++){
                sideRegions[i] = atlas.find(name + "-" + i, "betamindy-spinner-" + i);
                for(int j = 0; j < 2; j++){
                    sideRegions2[i][j] = atlas.find(name + "-side-" + (j * 2 + i), "betamindy-spinner-side-" + (j * 2 + i));
                }
            }
        }
        else{
            laser = atlas.find(name + "-laser", "betamindy-cog-laser");
            laserEnd = atlas.find(name + "-laser-end", "betamindy-cog-laser-end");
            topRegion2 = atlas.find(name + "-top-2", "betamindy-cog-top-2");
            for(int i = 0; i < 2; i++){
                for(int j = 0; j < 2; j++){
                    sideRegions2[i][j] = atlas.find(name + "-side-" + (j * 2 + i), "betamindy-cog-side-" + (j * 2 + i));
                }
            }
        }
    }

    @Override
    public void drawPlanConfig(BuildPlan req, Eachable<BuildPlan> list){
        if(req.config != null && (boolean)req.config) Draw.rect(altIcon, req.drawx(), req.drawy(), req.rotation * 90f);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Lines.stroke(1f);
        Draw.color(Pal.accent, Mathf.absin(Time.globalTime, 2f, 1f));
        Lines.square((x + Geometry.d4x(rotation)) * tilesize + offset, (y + Geometry.d4y(rotation)) * tilesize + offset, tilesize / 2f);
        Draw.color();
    }

    public class SpinnerBuild extends Building implements SpinDraw, HeavyBuild, SpinUpdate{
        public boolean ccw = false; //counter clockwise

        /** Below are only valid when spinning is true */
        public boolean spinning = false, looped = false;
        public float spin = 0f; //0 ~ 4 * spinTime
        public byte offset = 0; //-7 ~ 8 hopefully
        public @Nullable BuildPayload payload;
        //public float lifetime = 0f;
        protected int weight = 0;

        /** Below are only valid if multiBuild is true */
        protected boolean multiBuild = false; //has more blocks stuck to this things
        protected Seq<RBuild> mbuilds = new Seq<RBuild>();

        @Override
        public int weight(){
            return payload == null ? 1 : weight + 1;
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            if(spinning){
                if(inertia && notNullified(sx, sy)){
                    spin += delta();
                    if(spin >= spinTime) looped = true;
                }
                updateSpinBlocks(sx, sy, rawRot, srad);
            }
        }

        public boolean notNullified(float x, float y){
            Building n = world.buildWorld(x, y);
            if(n == null || !(n.block instanceof Disabler)) return true;
            return !n.canConsume();
        }

        public void updateSpinBlocks(float x, float y, float dr, float addrad){
            if(payload != null){
                if(payload.build instanceof SpinUpdate){
                    Tmp.v2.set(tilesize * (payload.block().size / 2f + 0.5f), offset * tilesize - payload.block().offset).rotate(angle() + dr);
                    ((SpinUpdate) payload.build).spinUpdate(x + Tmp.v2.x, y + Tmp.v2.y, Tmp.v2.len() + addrad, angle() + dr, rawAngle() * Mathf.sign(ccw) + dr);
                }

                if(multiBuild){
                    float a = angle() + dr;
                    float ra = rawAngle() * Mathf.sign(ccw) + dr;

                    mbuilds.each(mb -> {
                        if(mb.build instanceof SpinUpdate){
                            Tmp.v2.set(mb.x * tilesize, mb.y * tilesize).rotate(ra).add(Tmp.v3.trns(a, tilesize));
                            ((SpinUpdate) mb.build).spinUpdate(x + Tmp.v2.x, y + Tmp.v2.y, Tmp.v2.len() + addrad, a, ra);
                        }
                    });
                }
            }
        }

        @Override
        public void updateTile(){
            if(spinning){
                spin += delta();
                if(spin >= spinTime) looped = true;
                updateSpinBlocks(x, y, 0f, 0f);
                if(!canConsume()){
                    if(checkDrop()){
                        //it is almost 90 degrees, in its dropping window
                        if(payload == null) spinning = false; //nothing to drop; drop nothing
                        else{
                            int dir = Mathf.round(spin / spinTime) * Mathf.sign(ccw) + rotation;
                            Tile t = destTile(tile, dir, offset, payload.block().size);

                            if(t != null){
                                if(!multiBuild && t.build != null && t.build.acceptPayload(t.build, payload)){
                                    //payload placing
                                    t.build.handlePayload(t.build, payload);
                                    if(t.build instanceof PayloadBlockBuild<?>){
                                        ((PayloadBlockBuild<?>) t.build).payVector.set(tilesize * (payload.block().size / 2f + 0.5f), offset * tilesize - payload.block().offset).rotate(angle()).add(this).sub(t.build).clamp(-t.build.block.size * tilesize / 2f, -t.build.block.size * tilesize / 2f, t.build.block.size * tilesize / 2f, t.build.block.size * tilesize / 2f);
                                    }

                                    payload = null;
                                    spinning = false;
                                }
                                else if(RBuild.validPlace(payload.block(), t.x, t.y)){ //if it can be dropped
                                    //try dropping what it has
                                    payload.place(t, dir - rotation + payload.build.rotation);

                                    payload = null;
                                    spinning = false;
                                    if(multiBuild){
                                        multiBuild = false;
                                        int newr = Mathf.mod(Mathf.round(spin / spinTime) * Mathf.sign(ccw), 4);
                                        RBuild.placeAll(mbuilds, tile.nearby((newr + rotation) % 4), newr, dir);
                                        mbuilds.clear();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else{
                //idle
                if(canConsume()){
                    //try grabbing the front block
                    grabPayload();
                    spinning = true;
                    spin = 0f;
                    looped = false;
                }
            }

            super.updateTile();
        }

        public Tile destTile(Tile origin, int dir, int off, int bsize){
            dir = dir % 4;
            if(dir < 0) dir += 4;
            if(bsize % 2 == 0){
                if(dir % 2 == 0){
                    //y is offset
                    return origin.nearby(Geometry.d4x(dir) * (1 + bsize / 2) + evenOffsets[dir][0], off * Geometry.d4x(dir) + evenOffsets[dir][1]);
                }
                else{
                    //x is offset
                    return origin.nearby(-off * Geometry.d4y(dir) + evenOffsets[dir][0], Geometry.d4y(dir) * (1 + bsize / 2) + evenOffsets[dir][1]);
                }
            }
            else{
                if(dir % 2 == 0){
                    //y is offset
                    return origin.nearby(Geometry.d4x(dir) * (1 + bsize / 2), off * Geometry.d4x(dir));
                }
                else{
                    //x is offset
                    return origin.nearby(-off * Geometry.d4y(dir), Geometry.d4y(dir) * (1 + bsize / 2));
                }
            }
        }

        public void grabPayload(){
            payload = null;
            if(tile.nearby(rotation) == null || tile.nearbyBuild(rotation) == null) return;
            Building b = tile.nearbyBuild(rotation);
            if(b.canPickup()){
                if(b.block instanceof SlimeBlock){
                    weight = RBuild.pickup(mbuilds, b, rotation, maxBlocks, other -> (other != this && stickBool.get(other)));
                    multiBuild = weight >= 0;
                    if(!multiBuild) return;
                    weight++; //root tile, is never a HeavyTile anyways
                }
                else weight = (b instanceof HeavyBuild) ? ((HeavyBuild) b).weight() : 1;
                if(weight > maxBlocks && !multiBuild) return; //even if it is overweight, refusing to pick up multiblocks here will lead to a critical bug. Normally this should not happen anyways

                switch(rotation){
                    case 0 -> offset = (byte) (b.tileY() - tileY());
                    case 1 -> offset = (byte) (tileX() - b.tileX());
                    case 2 -> offset = (byte) (tileY() - b.tileY());
                    default -> offset = (byte) (b.tileX() - tileX());
                }
                if(b.block.size % 2 == 0) offset -= evenOffsets[rotation][0];

                b.tile.remove();
                payload = new BuildPayload(b);
            }
        }

        public float rawAngle(){
            return (spinning) ? spin / spinTime * 90f : 0f;
        }

        public float angle(){
            return rawAngle() * Mathf.sign(ccw) + rotation * 90f;
        }

        public boolean checkDrop(){
            return spinning && looped && spin % spinTime <= 2f;
        }

        @Override
        public void draw(){
            drawSpinning(x, y, 0f, false);
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            drawSpinning(x, y, dr, inertia);
        }

        public void drawSpinning(float x, float y, float dr, boolean tint){
            Draw.rect(baseRegion, x, y, dr);

            if(drawSpinSprite){
                Draw.rect(sideRegions[Mathf.num(ccw)], x, y, dr);
                Draw.rect(sideRegions2[rotation >> 1][Mathf.num(ccw)], x, y, rotation * 90f + dr);

                if(!spinning || spin % spinTime < 0.0001f){
                    Draw.rect(topRegion, x, y, dr);
                }else{
                    float r = (spin % spinTime) / spinTime * 90f;
                    Draw.rect(topRegion, x, y, r * Mathf.sign(ccw) + dr);
                    Draw.alpha(r / 90f);
                    Draw.rect(topRegion, x, y, (r - 90f) * Mathf.sign(ccw) + dr);
                    Draw.alpha(1f);
                }
            }
            else{
                float r = (!spinning || spin % spinTime < 0.0001f) ? 0f : (spin % spinTime) / spinTime * 90f;
                if(drawTop) Draw.z(Layer.blockOver + 0.18f);
                Draw.rect(sideRegions2[rotation >> 1][Mathf.num(ccw)], x, y, rotation * 90f + dr);
                Draw.z(Layer.blockOver + 0.17f);
                Draw.rect(topRegion, x, y, r * Mathf.sign(ccw) + dr);
                Draw.rect(topRegion2, x, y, dr);
            }

            if(spinning && payload != null){
                drawLaser(x, y, dr, tint);
                drawPay(x, y, dr);
                if(multiBuild) RBuild.drawAll(mbuilds, x, y, angle() + dr, rawAngle() * Mathf.sign(ccw) + dr);
            }
        }

        public void drawLaser(float x, float y, float dr, boolean tint){
            Draw.z(Layer.blockOver + 0.15f);
            Tmp.v2.trns(angle() + dr, tilesize);
            if(tint){
                Draw.color(inertiaColor);
                Drawf.laser(laser, laserEnd, x, y, x + Tmp.v2.x, y + Tmp.v2.y, laserWidth);
                Draw.color();
            }
            else{
                Drawf.laser(laser, laserEnd, x, y, x + Tmp.v2.x, y + Tmp.v2.y, laserWidth);
            }
        }

        public void drawPay(float x, float y, float dr){
            Draw.z(Layer.blockOver + 0.09f);
            Tmp.v1.set(tilesize * (payload.block().size / 2f + 0.5f), offset * tilesize - payload.block().offset).rotate(angle() + dr);
            Drawf.shadow(x + Tmp.v1.x, y + Tmp.v1.y, tilesize * payload.block().size * 2f);
            Draw.z(Layer.blockOver + 0.1f);
            if(payload.build instanceof SpinDraw) ((SpinDraw) payload.build).drawSpinning(x + Tmp.v1.x, y + Tmp.v1.y, rawAngle() * Mathf.sign(ccw) + dr);
            else Draw.rect(payload.build.block.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, (payload.block().size > 1 || multiBuild || payload.block().rotate ? (payload.block().rotate ? payload.build.rotation : 0) * 90f + rawAngle() * Mathf.sign(ccw) : 0f) + dr);
        }

        @Override
        public boolean configTapped(){
            if(spinning) return false;
            configure(!ccw);
            Sounds.click.at(this);
            return false;
        }

        @Override
        public Boolean config(){
            return ccw;
        }

        @Override
        public boolean conductsTo(Building other){
            return tile.nearbyBuild(rotation) != other && super.conductsTo(other);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Lines.stroke(1f);
            Draw.color(Pal.accent, Mathf.absin(Time.time, 2f, 1f));
            Lines.square(x + Geometry.d4x(rotation) * tilesize, y + Geometry.d4y(rotation) * tilesize, tilesize / 2f);
            if(spinning){
                Draw.color(drawColor, Mathf.absin(Time.time, 2f, 0.3f) + 0.7f);
                Lines.lineAngle(x, y, angle(), tilesize);
            }
            Draw.color();
        }

        @Override
        public void onDestroyed(){
            if(payload != null){
                Tmp.v1.set(tilesize * (payload.block().size / 2f + 0.5f), offset * tilesize - payload.block().offset).rotate(angle());
                Fx.dynamicExplosion.at(x + Tmp.v1.x, y + Tmp.v1.y, payload.block().size / 1.3f);
            }
            if(multiBuild){
                for(RBuild rb : mbuilds){
                    Tmp.v1.set(rb.x * tilesize, rb.y * tilesize).rotate(rawAngle() + Mathf.sign(ccw));
                    if(rb.build.block.size % 2 == 0){
                        Tmp.v1.add(Tmp.v3.set(-4f, -4f).rotate(angle()));
                    }
                    Tmp.v1.add(Tmp.v3.trns(angle(), tilesize));
                    Fx.dynamicExplosion.at(x + Tmp.v1.x, y + Tmp.v1.y, rb.build.block.size / 1.3f);
                }
            }
            super.onDestroyed();
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case rotation -> angle();
                case enabled -> spinning ? 1 : 0;
                default -> super.sense(sensor);
            };
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            ccw = read.bool();
            spinning = read.bool();
            weight = 0;
            if(spinning){
                spin = (float)read.b();
                if(spin >= spinTime) looped = true;
                offset = read.b();
                if(mobile) payload = BetaMindy.mobileUtil.readPayload(read);
                else payload = Payload.read(read);

                if(payload != null) weight = read.b();

                if(read.bool()){
                    multiBuild = true;
                    mbuilds.clear();
                    int s = read.s();
                    for(int i = 0; i < s; i++){
                        mbuilds.add(RBuild.read(read, revision));
                    }
                }
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(ccw);
            write.bool(spinning);
            if(spinning){
                write.b((byte)(spin % (spinTime * 4f)));
                write.b(offset);
                if(mobile) BetaMindy.mobileUtil.writePayload(payload, write);
                else Payload.write(payload, write);

                if(payload != null) write.b(weight);

                write.bool(multiBuild);
                if(multiBuild){
                    write.s(mbuilds.size);
                    mbuilds.each(mb -> RBuild.write(mb, write));
                }
            }
        }
    }

        /*public static class BuildGroup {
        public Seq<BuildPayload> builds;
        public float lifetime;

        public void read(Reads read, byte revision){
            int size = read.i();
            builds = new Seq<BuildPayload>();
            for(int i = 0; i < size; i++){
                BuildPayload pay = mobile ? BetaMindy.mobileUtil.readPayload(read) : Payload.read(read);
                builds.add(pay);
            }
            if(size > 0) lifetime = read.f();
        }

        public void write(Writes write){
            write.i(builds.size);
            builds.forEach(b -> {
                if(mobile) BetaMindy.mobileUtil.writePayload(b, write);
                else Payload.write(b, write);
            });
            if(builds.size > 0) write.f(lifetime);
        }
    }*/
}
