package betamindy.world.blocks.distribution;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.BetaMindy;
import betamindy.world.blocks.payloads.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static mindustry.Vars.mobile;
import static mindustry.Vars.tilesize;

public class Spinner extends Block {
    public float spinTime = 16f;
    //public float holdTime = 60f;

    public TextureRegion baseRegion, topRegion, laser, laserEnd, altIcon;
    public TextureRegion[] sideRegions = new TextureRegion[2];
    public TextureRegion[][] sideRegions2 = new TextureRegion[2][2];

    public Color drawColor = Pal.heal;
    public float laserWidth = 0.4f;

    public int maxBlocks = 12;
    public final Boolf<Building> stickBool = b -> b.canPickup() && !(b.block == Blocks.phaseWall || b.block == Blocks.phaseWallLarge || b.block == Blocks.thoriumWall || b.block == Blocks.thoriumWallLarge);

    public final int[][] evenOffsets = {{-1, -1}, {0, -1}, {0, 0}, {-1, 0}};

    public Spinner(String name){
        super(name);

        update = true;
        rotate = true;
        solid = true;
        hasPower = true;
        configurable = true;
        saveConfig = true;
        expanded = true;

        group = BlockGroup.transportation;//TODO: Make own category for place-overs

        config(Boolean.class, (SpinnerBuild entity, Boolean b) -> entity.ccw = b);
    }

    @Override
    public void load() {
        super.load();
        baseRegion = atlas.find(name + "-base");
        topRegion = atlas.find(name + "-top");
        laser = atlas.find(name + "-laser", "betamindy-spinner-laser");
        laserEnd = atlas.find(name + "-laser-end", "betamindy-spinner-laser-end");
        altIcon = atlas.find(name + "-alt");
        for(int i = 0; i < 2; i++){
            sideRegions[i] = atlas.find(name + "-" + i);
            for(int j = 0; j < 2; j++){
                sideRegions2[i][j] = atlas.find(name + "-side-" + (j * 2 + i));
            }
        }
    }

    @Override
    public void drawRequestConfig(BuildPlan req, Eachable<BuildPlan> list){
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

    public class SpinnerBuild extends Building implements SpinDraw{
        public boolean ccw = false; //counter clockwise

        /** Below are only valid when spinning is true */
        public boolean spinning = false, looped = false;
        public float spin = 0f; //0 ~ 4 * spinTime
        public byte offset = 0; //-7 ~ 8 hopefully
        public @Nullable BuildPayload payload; //TODO refactor this to Building too
        //public float lifetime = 0f;

        /** Below are only valid if multiBuild is true */
        protected boolean multiBuild = false; //has more blocks stuck to this things
        protected Seq<RBuild> mbuilds = new Seq<RBuild>();

        /*@Override
        public int weight(){
            return payload == null ? 1 : multiBuild ? mbuilds.size + 2 : 2;//TODO make RBuild & XeloUtil consider weight (make this an interface?)
        }*/

        @Override
        public void updateTile() {
            if(spinning){
                spin += delta();
                if(spin >= spinTime) looped = true;
                if(!consValid()){
                    if(checkDrop()){
                        //it is almost 90 degrees, in its dropping window
                        if(payload == null) spinning = false; //nothing to drop; drop nothing
                        else{
                            int dir = Mathf.round(spin / spinTime) * Mathf.sign(ccw) + rotation;
                            Tile t = destTile(tile, dir, offset, payload.block().size);
                            //TODO: offload to payload blocks if possible
                            if(t != null && RBuild.validPlace(payload.block(), t.x, t.y)){ //if it can be dropped
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
            else{
                //idle
                if(consValid()){
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
                    multiBuild = RBuild.pickup(mbuilds, b, rotation, maxBlocks, other -> (other != this && stickBool.get(other)));
                    if(!multiBuild) return;
                }

                if(rotation % 2 == 0){
                    //y is the offset
                    offset = (byte)(b.tileY() - tileY());
                    if(b.block.size % 2 == 0) offset -= evenOffsets[rotation][1];
                }
                else{
                    //x is the offset
                    offset = (byte)(b.tileX() - tileX());
                    if(b.block.size % 2 == 0) offset -= evenOffsets[rotation][0];
                }
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
            drawSpinning(x, y, 0f);
            /*Draw.rect(baseRegion, x, y);
            Draw.rect(sideRegions[Mathf.num(ccw)], x, y);
            Draw.rect(sideRegions2[rotation >> 1][Mathf.num(ccw)], x, y, rotation * 90f);

            if(!spinning || spin % spinTime < 0.0001f){
                Draw.rect(topRegion, x, y);
            }
            else{
                float r = (spin % spinTime) / spinTime * 90f;
                Draw.rect(topRegion, x, y, r * Mathf.sign(ccw));
                Draw.alpha(r / 90f);
                Draw.rect(topRegion, x, y, (r - 90f) * Mathf.sign(ccw));
                Draw.alpha(1f);
            }

            if(spinning && payload != null){
                drawLaser();
                drawPay();
                if(multiBuild) RBuild.drawAll(mbuilds, x, y, angle(), rawAngle() * Mathf.sign(ccw));
            }*/
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            Draw.rect(baseRegion, x, y, dr);
            Draw.rect(sideRegions[Mathf.num(ccw)], x, y, dr);
            Draw.rect(sideRegions2[rotation >> 1][Mathf.num(ccw)], x, y, rotation * 90f + dr);

            if(!spinning || spin % spinTime < 0.0001f){
                Draw.rect(topRegion, x, y, dr);
            }
            else{
                float r = (spin % spinTime) / spinTime * 90f;
                Draw.rect(topRegion, x, y, r * Mathf.sign(ccw) + dr);
                Draw.alpha(r / 90f);
                Draw.rect(topRegion, x, y, (r - 90f) * Mathf.sign(ccw) + dr);
                Draw.alpha(1f);
            }

            if(spinning && payload != null){
                drawLaser(x, y, dr);
                drawPay(x, y, dr);//TODO
                if(multiBuild) RBuild.drawAll(mbuilds, x, y, angle() + dr, rawAngle() * Mathf.sign(ccw) + dr);
            }
        }

        public void drawLaser(){
            drawLaser(x, y, 0f);
        }

        public void drawLaser(float x, float y, float dr){
            //TODO: payload block io graphical extension
            Draw.z(Layer.blockOver + 0.15f);
            Tmp.v2.trns(angle() + dr, tilesize);
            Drawf.laser(team, laser, laserEnd, x, y, x + Tmp.v2.x, y + Tmp.v2.y, laserWidth);
        }

        public void drawPay(){
            drawPay(x, y, 0f);
        }

        public void drawPay(float x, float y, float dr){
            Draw.z(Layer.blockOver + 0.09f);
            Tmp.v1.set(tilesize * (payload.block().size / 2f + 0.5f), offset * tilesize - payload.block().offset).rotate(angle() + dr);
            Drawf.shadow(x + Tmp.v1.x, y + Tmp.v1.y, tilesize * payload.block().size * 2f);
            Draw.z(Layer.blockOver + 0.1f);
            if(payload.build instanceof SpinDraw) ((SpinDraw) payload.build).drawSpinning(x + Tmp.v1.x, y + Tmp.v1.y, (payload.block().size > 1 || multiBuild || payload.block().rotate ? (payload.block().rotate ? payload.build.rotation : 0) * 90f + rawAngle() * Mathf.sign(ccw) : 0f) + dr);
            else Draw.rect(payload.icon(Cicon.full), x + Tmp.v1.x, y + Tmp.v1.y, (payload.block().size > 1 || multiBuild || payload.block().rotate ? (payload.block().rotate ? payload.build.rotation : 0) * 90f + rawAngle() * Mathf.sign(ccw) : 0f) + dr);
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
        public double sense(LAccess sensor){
            switch(sensor){
                case rotation: return angle();
                default: return super.sense(sensor);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            ccw = read.bool();
            spinning = read.bool();
            if(spinning){
                spin = (float)read.b();
                if(spin >= spinTime) looped = true;
                offset = read.b();
                if(mobile) payload = BetaMindy.mobileUtil.readPayload(read);
                else payload = Payload.read(read);

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
