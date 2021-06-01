package betamindy.world.blocks.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;

import static arc.Core.atlas;
import static arc.math.geom.Geometry.d4;
import static mindustry.Vars.*;

public class ClearPipe extends Block {
    public static final byte[] pipePriority = {2, 1, 3, 0};
    public static final byte[][] shadowTypes = {{0, 0}, {1, 2}, {1, 3}, {3, 0}, {1, 0}, {2, 0}, {3, 1}, {4, 1}, {1, 1}, {3, 3}, {2, 1}, {4, 0}, {3, 2}, {4, 3} ,{4, 2} ,{5, 0}};
    public static final byte[][][] pipeOpening = {{{2, 0}, {2, 1}}, {{0, 2}, {1, 2}}, {{-1, 0}, {-1, 1}}, {{0, -1}, {1, -1}}};
    public float speed = 1f / 6f;
    public float ejectStrength = 6f;

    public Effect suckEffect = Fx.mineHuge; //TODO effect
    public Effect spitEffect = Fx.mineHuge;
    public TextureRegion[] pipeRegions = new TextureRegion[16], shadowRegions = new TextureRegion[6];
    private int tiling;

    public ClearPipe(String name){
        super(name);

        size = 2;
        solid = true;
        update = true;
        noUpdateDisabled = false;
        drawDisabled = false;
        rotate = false;
    }

    @Override
    public void load(){
        super.load();
        pipeRegions[0] = region;
        for(int i = 1; i < 16; i++){
            pipeRegions[i] = atlas.find(name + "-" + i, name + "" + i);
        }

        if(hasShadow) return;
        for(int i = 0; i < 6; i++){
            shadowRegions[i] = atlas.find(name + "-shadow-" + i, "clear-pipe-shadow-" + i);
        }
    }

    @Override
    public boolean canBreak(Tile tile){
        return !(tile.build instanceof ClearPipeBuild) || !((ClearPipeBuild) tile.build).unit().isPlayer();
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        tiling = 0;
        list.each(other -> {
            if(other.breaking || other == req || !(other.block instanceof ClearPipe)) return;
            if(other.y == req.y){
                if(other.x - req.x == 2) tiling += 1;
                else if(other.x - req.x == -2) tiling += 4;
            }
            else if(other.x == req.x){
                if(other.y - req.y == 2) tiling += 2;
                else if(other.y - req.y == -2) tiling += 8;
            }
        });
        int i = 0;
        for(Point2 point : Geometry.d4){
            int x = req.x + point.x * 2, y = req.y + point.y * 2;
            Tile t = world.tile(x, y);
            if(t != null && ((1 << i) & tiling) == 0 && (t.build instanceof ClearPipeBuild) && t.build.tile == t) tiling += (1 << i);
            i++;
        }
        Draw.rect(pipeRegions[tiling], req.drawx(), req.drawy());
    }

    public class ClearPipeBuild extends Building implements ControlBlock {
        public @Nullable BlockUnitc blockUnit;
        public Seq<UnitinaBottle> units = new Seq<>();
        public ClearPipeBuild[] pipes = new ClearPipeBuild[4];
        public int connections = 0, contype = 0;

        @Override
        public void created(){
            super.created();
            blockUnit = (BlockUnitc)UnitTypes.block.create(team);
            blockUnit.tile(this);
        }

        public float speed(){
            return speed;
        }

        public boolean validPipe(int r){
            if(connections == 0) return r % 2 == 0 && isOpen(r);
            if(connections == 1 && r == rotation) return isOpen(r);
            return pipes[r] != null;
        }

        public boolean isOpen(int r){ return isOpen(r, false); }

        public boolean isOpen(int r, boolean strict){
            for(int i = 0; i < 2; i++){
                Tile t = tile.nearby(pipeOpening[r][i][0], pipeOpening[r][i][1]);
                if(t == null || (!(t.block().outputsPayload && !strict) && t.solid())) return false;
            }
            return true;
        }

        public void suck(int dir){
            float ox = d4(dir).x * (size * 4f + 4f);
            float oy = d4(dir).y * (size * 4f + 4f);

            if(dir % 2 == 0){
                //tall rectangle
                Units.nearby(x + ox - 4f, y + oy - size * 4f, 8f, size * 8f, u -> {
                    if(u.y >= y + oy - size * 4f && u.y <= y + oy + size * 4f && Angles.within(u.vel.angle(), dir * 90f + 180f, 60f)){
                        acceptUnit(u, dir);
                    }
                });
            }
            else{
                //wide rectangle
                Units.nearby(x + ox - size * 4f, y + oy - 4f, size * 8f, 8f, u -> {
                    if(u.x >= x + ox - size * 4f && u.x <= x + ox + size * 4f && Angles.within(u.vel.angle(), dir * 90f + 180f, 60f)){
                        acceptUnit(u, dir);
                    }
                });
            }
        }

        public void acceptUnit(Unit unit, int dir){
            if(!unit.isAdded()) return;

            if(unit.isPlayer()){
                if(unit() != null && unit().isPlayer() && unit().getPlayer() != unit.getPlayer()){
                    //display text
                    if(!headless && unit.getPlayer() == player) ui.showInfoToast("$clearpipe.no", 3f);
                    return;
                }
                Player p = unit.getPlayer();
                unit.remove();
                if(!net.client()) p.unit(unit());
                units.add(new UnitinaBottle(new UnitPayload(unit), dir, this));
            }
            else{
                unit.remove();
                units.add(new UnitinaBottle(new UnitPayload(unit), dir));
            }

            suckEffect.at(Tmp.v1.trns(dir * 90f, tilesize * size / 2f).add(this));
            if(Vars.net.client()){
                Vars.netClient.clearRemovedEntity(unit.id);
            }
        }

        @Override
        public Unit unit(){
            return (Unit)blockUnit;
        }

        @Override
        public boolean canControl(){
            return unit().isPlayer() || (control.input.controlledType == UnitTypes.block && world.buildWorld(player.x, player.y) == self());
        }

        @Override
        public void update(){
            if(connections == 0){
                suck(0);
                suck(2);
            }
            else if (connections == 1){
                suck(rotation);
            }

            for(int i = 0; i < units.size; i++){
                if(units.get(i).update(this)){
                    units.remove(i);
                    i--;
                }
            }
        }

        @Override
        public void draw(){
            if(!hasShadow){
                Draw.z(Layer.block - 0.99f);
                Draw.color(Pal.shadow, 0.15f);
                Draw.rect(shadowRegions[shadowTypes[contype][0]], x, y, shadowTypes[contype][1] * 90f);
                Draw.color();
            }

            Draw.z(Layer.block);
            units.each(u -> u.draw(this));
            Draw.rect(pipeRegions[contype], x, y);
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            connections = 0;
            contype = 0;
            for(int i = 0; i < 4; i++){
                pipes[i] = null;
                Tile near = tile.nearby(Geometry.d4x(i) * 2, Geometry.d4y(i) * 2);
                if(near != null && (near.build instanceof ClearPipeBuild) && near.build.tile.pos() == near.pos()){
                    ClearPipeBuild cb = (ClearPipeBuild) near.build;
                    pipes[i] = cb;
                    connections++;
                    contype += (1 << i);
                    rotation = (i + 2) % 4;
                }
            }
        }

        @Override
        public void onRemoved(){
            units.each(u -> u.dump(this));
            super.onRemoved();
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int size = read.s();
            for(int i = 0; i < size; i++){
                units.add(readUnit(read));
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(units.size);
            for(int i = 0; i < units.size; i++){
                writeUnit(units.get(i), write);
            }
        }

        public UnitinaBottle readUnit(Reads read){
            UnitinaBottle u = new UnitinaBottle(BetaMindy.mobileUtil.readPayload(read), read.b());
            u.to = read.b();
            u.f = read.f();
            if(u.f < 0f) u.initf = read.f();
            if(read.bool()){
                u.savedTile = world.tile(read.i());
                if(!read.bool()) return u;
                player.set(u.savedTile.worldx(), u.savedTile.worldy());
                Core.app.post(() -> {
                    Core.camera.position.set(this);
                });
            }
            return u;
        }

        public void writeUnit(UnitinaBottle u, Writes write){
            if(u.player() != null){
                //u.playerPipe.unit().set(u.playerPipe); //return
            }
            BetaMindy.mobileUtil.writePayload(u.unit, write);
            write.b(u.from);
            write.b(u.to);
            write.f(u.f);
            if(u.f < 0f) write.f(u.initf);

            write.bool(u.player() != null);
            if(u.player() != null){
                write.i(u.playerPipe.pos());
                write.bool(u.player() == player);
            }
        }
    }

    public class UnitinaBottle{
        public UnitPayload unit;
        public int from;
        public int to;
        public float f;
        private float initf;
        public static final float maxDrawSize = 52f;
        public static final float shadowOffset = 3f;
        private float lastTime = 0f;

        public @Nullable ClearPipeBuild playerPipe;
        private @Nullable Tile savedTile = null;

        public UnitinaBottle(UnitPayload unit, int from){
            this(unit, from, null);
        }

        public UnitinaBottle(UnitPayload unit, int from, ClearPipeBuild player){
            this.unit = unit;
            this.from = from;
            to = -1;
            if(unit.icon(Cicon.full).width > maxDrawSize + 16f){
                initf = Math.min(150f, (unit.icon(Cicon.full).width - maxDrawSize) / 3f) + 15f;
                f = -initf;
            }
            else{
                f = 0f;
                initf = 0f;
            }
            playerPipe = player;
        }

        public @Nullable Player player(){
            return playerPipe != null && playerPipe.isValid() && playerPipe.unit().isPlayer() ? playerPipe.unit().getPlayer() : null;
        }

        public void updateSavedTile(){
            if(savedTile == null) return;
            if(playerPipe == null && savedTile.build != null && (savedTile.build instanceof ClearPipeBuild)) playerPipe = (ClearPipeBuild) savedTile.build;
            savedTile = null;
        }

        public void reset(){
            f -= 1f;
            if(f < 0f) f = 0f;
            from = (to + 2) % 4;
            to = -1;
            lastTime = Time.time;
        }

        public void uturn(){
            f = 0f;
            from = to;
            to = -1;
        }

        public void draw(ClearPipeBuild build){
            if(unit == null) return;
            TextureRegion icon = unit.icon(Cicon.full);
            float w, h, r;
            boolean above = false;
            if(f < 0f){
                //special animation
                float p = (initf + f)/initf;
                above = p < 075f;
                w = p < 0.75f ? (1f - Interp.swingIn.apply(p / 0.75f)) : 0f;
                w = w * (icon.width - maxDrawSize) + maxDrawSize;
                h = p < 0.5f ? (1f - 0.7f * Interp.pow2Out.apply(p / 0.5f)) : (p < 0.75f ? 0.3f * Interp.pow2In.apply((p - 0.5f) / 0.25f) + 0.3f : Mathf.lerp(0.6f, 0f, (p - 0.75f) * 4f));
                h = h * (icon.height - maxDrawSize) + maxDrawSize;
                r = from * 90f + 180f;
                Tmp.v1.trns(from * 90f, tilesize * build.block.size * 0.5f + h / 8f * (p > 0.75f ? 1f - (p - 0.75f) * 4f : 1f)).add(build);
                w *= Draw.scl * Draw.xscl;
                h *= Draw.scl * Draw.yscl;
            }
            else{
                if(f <= 0.5f || to < 0) Tmp.v1.trns(from * 90f, tilesize * build.block.size * (0.5f - f)).add(build);
                else Tmp.v1.trns(to * 90f, tilesize * build.block.size * (f - 0.5f)).add(build);
                w = Math.min(icon.width, maxDrawSize) * Draw.scl * Draw.xscl;
                h = Math.min(icon.height, maxDrawSize) * Draw.scl * Draw.yscl;
                r = (f <= 0.5f || to < 0) ? from * 90f + 180f : to * 90f;
            }

            Draw.z(Layer.block - 0.98f);
            Draw.mixcol(Pal.shadow, 1f);
            Draw.alpha(0.21f);
            Draw.rect(icon, Tmp.v1.x - shadowOffset, Tmp.v1.y - shadowOffset, w, h, r - 90f);
            Draw.mixcol();
            Draw.color();
            Draw.z(above ? Layer.turret + 0.1f : Layer.block - 0.1f);
            Draw.rect(icon, Tmp.v1.x, Tmp.v1.y, w, h, r - 90f);
        }

        public void effects(ClearPipeBuild build){
            spitEffect.at(Tmp.v2.trns(to * 90f, tilesize * size / 2f).add(build), to * 90f);
        }

        public void dump(ClearPipeBuild build){
            //init
            Player p = player();
            if(f <= 0.5f || to < 0) Tmp.v1.trns(from * 90f, tilesize * build.block.size * (0.5f - f)).add(build);
            else Tmp.v1.trns(to * 90f, tilesize * build.block.size * (f - 0.5f)).add(build);
            float r = (f <= 0.5f || to < 0) ? from * 90f + 180f : to * 90f;
            unit.set(Tmp.v1.x, Tmp.v1.y,r);

            //clear removed state of unit so it can be synced
            if(net.client()){
                netClient.clearRemovedEntity(unit.unit.id);
            }

            unit.unit.vel.trns(r, ejectStrength / 2f);

            if(p == null){
                unit.dump();
            }
            else{
                //Useful.unlockCam();
                Useful.dumpPlayerUnit(unit, p);
            }
        }

        /** Returns true if the unit left this build. */
        public boolean update(ClearPipeBuild build){
            updateSavedTile();
            if(f < 0f){
                //special animation playing for fat units, do nothing
                f += Time.delta;
                if(player() == player) playerPipe.unit().set(Tmp.v1.trns(from * 90f + 180f, size * build.block.size / 2f).add(build));
                /*Useful.lockCam(Tmp.v1.trns(from * 90f + 180f, size * build.block.size / 2f).add(build));*/
                if(f > 0f) f = 0f;
            }
            else{
                if(Time.time - lastTime < 0.1f){
                    return false;
                }

                f += build.delta() * build.speed();
                Player p = player();
                if(p == null && playerPipe != null) playerPipe = null;

                if(f <= 0.5f){
                    Tmp.v1.trns(from * 90f, tilesize * (0.5f - f) * build.block.size).add(build);
                    unit.set(Tmp.v1.x, Tmp.v1.y,from * 90f + 180f);
                    //not halfway yet
                    if(!headless && player() == Vars.player){
                        int input = Useful.dwas();
                        if(input >= 0 && from != input && build.validPipe(input)) to = input;
                        playerPipe.unit().set(Tmp.v1);
                        //Useful.lockCam(Tmp.v1);
                    }
                }
                else{
                    if(to < 0){
                        //find a destination asap
                        for(int i = 0; i < 4; i++){
                            int dir = (from + pipePriority[i]) % 4;
                            if(build.validPipe(dir)){
                                to = dir;
                                break;
                            }
                        }

                        if(to < 0) to = from; //u-turn
                    }
                    Tmp.v1.trns(to * 90f, tilesize * (f - 0.5f) * build.block.size).add(build);
                    unit.set(Tmp.v1.x, Tmp.v1.y,to * 90f);
                    if(p == player) playerPipe.unit().set(Tmp.v1);/*Useful.lockCam(Tmp.v1);*/

                    if(f > 1f){
                        if(to < 0) to = from;
                        if(build.pipes[to] != null && build.pipes[to].isValid()){
                            build.pipes[to].units.add(this);
                            reset();
                            return true;
                        }

                        //dump unit

                        //clear removed state of unit so it can be synced
                        if(net.client()){
                            netClient.clearRemovedEntity(unit.unit.id);
                        }

                        unit.unit.vel.trns(to * 90f, ejectStrength);

                        if(p == null){
                            //standard dump...?
                            Building front = build.front();
                            if(front != null && front.block.outputsPayload){
                                if(build.movePayload(unit)){
                                    effects(build);
                                    return true;
                                }
                            }else if(build.isOpen(to, true) && unit.dump()){
                                effects(build);
                                return true;
                            }

                            //failed to dump, commit u-turn
                            uturn();
                            return false;
                        }
                        else{
                            //Useful.unlockCam();
                            if(unit.unit.type == null) return true;
                            if(build.isOpen(to, true) && Useful.dumpPlayerUnit(unit, p)){
                                effects(build);
                                return true;
                            }

                            uturn();
                            return false;
                        }
                    }
                }

                if(p == null && unit.unit.spawnedByCore){
                    Fx.unitDespawn.at(unit.unit.x, unit.unit.y, 0f, unit.unit);
                    //Useful.unlockCam();
                    return true;
                }
            }
            return false;
        }
    }
}
