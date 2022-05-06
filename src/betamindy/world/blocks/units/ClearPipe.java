package betamindy.world.blocks.units;

import arc.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static arc.math.geom.Geometry.d4;
import static mindustry.Vars.*;

public class ClearPipe extends Block {
    public static final byte[] pipePriority = {2, 1, 3, 0};
    public static final byte[][] shadowTypes = {{0, 0}, {1, 2}, {1, 3}, {3, 0}, {1, 0}, {2, 0}, {3, 1}, {4, 1}, {1, 1}, {3, 3}, {2, 1}, {4, 0}, {3, 2}, {4, 3} ,{4, 2} ,{5, 0}};
    public static final byte[][][] pipeOpening = {{{2, 0}, {2, 1}}, {{0, 2}, {1, 2}}, {{-1, 0}, {-1, 1}}, {{0, -1}, {1, -1}}};
    public static final StatusEffect immuneStatus = MindyStatusEffects.ouch;

    public float speed = 1f / 6f;
    public float ejectStrength = 6f;
    /** Whether to only input units when canConsume() */
    public boolean conditional = false;
    public boolean hasBaseRegion = false;  //set automatically
    public boolean hasLightRegion = false; //set automatically
    public boolean canChangeTeam = false; //"its a feature, not a bug" - might be useful later

    public Effect suckEffect = Fx.mineHuge, bigSuckEffect = MindyFx.mineHugeButHuger;
    public Effect suckSmokeEffect = MindyFx.suckSmoke;
    public Effect spitEffect = MindyFx.pipePop, bigSpitEffect = MindyFx.bigBoiPipePop;
    public float suckSmokeChance = 0.3f;
    public TextureRegion[] pipeRegions = new TextureRegion[16], shadowRegions = new TextureRegion[6];
    public TextureRegion baseRegion, lightRegion;
    private int tiling;

    public Sound popSound = MindySounds.pipePop;
    public Sound suckSound = MindySounds.pipeIn;
    public Sound squeezeSound = MindySounds.pipeSqueeze;
    public final int timerConfigure = timers++;
    public final int timerControl = timers++;
    public final static float configInterval = 30f;
    //public float squeezeSoundLength = 120f;

    public ClearPipe(String name){
        super(name);

        size = 2;
        solid = true;
        update = true;
        noUpdateDisabled = false;
        drawDisabled = conditional;
        rotate = false;
        sync = true;
        updateInUnits = false;

        config(Integer.class, (ClearPipeBuild build, Integer i) -> {
            if(0 <= i && i < 4) build.lastPlayerKey = i;
        });
    }

    @Override
    public void load(){
        super.load();
        pipeRegions[0] = region;
        for(int i = 1; i < 16; i++){
            pipeRegions[i] = atlas.find(name + "-" + i, name + "" + i);
        }

        baseRegion = atlas.find(name + "-base");
        hasBaseRegion = atlas.has(name + "-base");

        lightRegion = atlas.find(name + "-light");
        hasLightRegion = atlas.has(name + "-light");

        if(hasShadow) return;
        for(int i = 0; i < 6; i++){
            shadowRegions[i] = atlas.find(name + "-shadow-" + i, "clear-pipe-shadow-" + i);
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.speed, (int)(size * speed * 60f) + " " + Core.bundle.get("unit.blocks") + Core.bundle.get("unit.persecond"));
    }

    @Override
    public void setBars(){
        super.setBars();
        if(conditional){
            removeBar("power");
            ConsumePower cons = consPower;
            boolean buffered = cons.buffered;
            float capacity = cons.capacity;

            addBar("power", entity -> new Bar(() -> buffered ? Core.bundle.format("bar.poweramount", Float.isNaN(entity.power.status * capacity) ? "<ERROR>" : (int)(entity.power.status * capacity)) :
                    Core.bundle.get("bar.power"), () -> Mathf.zero(cons.requestedPower(entity)) ? Pal.lightishGray : Pal.powerBar, () -> Mathf.zero(cons.requestedPower(entity)) ? 1f : entity.power.status));
        }
    }

    @Override
    public boolean canBreak(Tile tile){
        return !(tile.build instanceof ClearPipeBuild) || !((ClearPipeBuild) tile.build).unit().isPlayer();
    }

    @Override
    public void drawPlanRegion(BuildPlan req, Eachable<BuildPlan> list){
        tiling = 0;
        list.each(other -> {
            if(other.breaking || other == req || !(other.block instanceof ClearPipe)) return;
            if(other.y == req.y){
                if(other.x - req.x == 2) tiling |= 1;
                else if(other.x - req.x == -2) tiling |= 4;
            }
            else if(other.x == req.x){
                if(other.y - req.y == 2) tiling |= 2;
                else if(other.y - req.y == -2) tiling |= 8;
            }
        });
        int i = 0;
        for(Point2 point : Geometry.d4){
            int x = req.x + point.x * 2, y = req.y + point.y * 2;
            Tile t = world.tile(x, y);
            if(t != null && ((1 << i) & tiling) == 0 && (t.build instanceof ClearPipeBuild) && t.build.tile == t) tiling |= (1 << i);
            i++;
        }
        if(hasBaseRegion) Draw.rect(baseRegion, req.drawx(), req.drawy());
        Draw.rect(pipeRegions[tiling % 16], req.drawx(), req.drawy()); //if even all is lost stop the crash at all costs
    }

    @Override
    public TextureRegion[] icons(){
        if(hasBaseRegion) return new TextureRegion[]{baseRegion, region};
        return super.icons();
    }

    public class ClearPipeBuild extends Building implements ControlBlock {
        public @Nullable BlockUnitc blockUnit;
        public Seq<UnitinaBottle> units = new Seq<>();
        public ClearPipeBuild[] pipes = new ClearPipeBuild[4];
        public int connections = 0, contype = 0;
        public int lastPlayerKey = -1;
        public float heat = 0f;

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

        public boolean isGate(){
            return connections <= 1;
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
            if(conditional && (!enabled || (power != null && power.status < 0.9f) || heat > 0f) || isPayload()) return;
            float ox = d4(dir).x * (size * 4f + 4f);
            float oy = d4(dir).y * (size * 4f + 4f);

            if(dir % 2 == 0){
                //tall rectangle
                Units.nearby(x + ox - 4f, y + oy - size * 4f, 8f, size * 8f, u -> {
                    if(u.y >= y + oy - size * 4f && u.y <= y + oy + size * 4f && Angles.within(u.vel.angle(), dir * 90f + 180f, 60f)){
                        acceptAttempt(u, dir);
                    }
                });
            }
            else{
                //wide rectangle
                Units.nearby(x + ox - size * 4f, y + oy - 4f, size * 8f, 8f, u -> {
                    if(u.x >= x + ox - size * 4f && u.x <= x + ox + size * 4f && Angles.within(u.vel.angle(), dir * 90f + 180f, 60f)){
                        acceptAttempt(u, dir);
                    }
                });
            }
        }

        public void acceptAttempt(Unit u, int dir){
            if((!canChangeTeam && u.team != team) || u.hasEffect(immuneStatus)) return;
            if(net.active()){
                if(u.isLocal() && connections == 1 && timer(timerConfigure, configInterval)){
                    u.vel.setZero();
                    configure(true);
                }
                return;
            }
            acceptUnit(u, dir);
        }

        public void acceptUnit(Unit unit, int dir){
            if(!unit.isValid() || unit.dead() || !unit.isAdded() || unit.team != team || isPayload()) return;

            if(unit.isPlayer()){
                if(unit() != null && unit().isPlayer() && unit().getPlayer() != unit.getPlayer()){
                    //display text
                    if(!headless && unit.getPlayer() == player) ui.showInfoToast("$clearpipe.no", 3f);
                    return;
                }
                Player p = unit.getPlayer();
                if(p == null) return;
                unit.remove();
                p.unit(unit()); //wrap this with !net.client? no.
                units.add(new UnitinaBottle(new UnitPayload(unit), dir, this));
            }
            else{
                unit.remove();
                units.add(new UnitinaBottle(new UnitPayload(unit), dir));
            }

            unit.vel.setZero();

            if(!headless){
                if(!units.peek().datBigBoi()){
                    suckEffect.at(Tmp.v1.trns(dir * 90f, tilesize * size / 2f).add(this));
                    suckSound.at(this);
                }else{
                    float len = Math.min(150f, ((net.active() ? unit.hitSize * 1.3f : unit.icon().width) - UnitinaBottle.maxDrawSize) / 3f) + 15f;
                    final int sfxid = squeezeSound.at(x, y, 1f, 0.7f);
                    Time.run(len, () -> {
                        Core.audio.stop(sfxid);
                        suckSound.at(this);
                    });
                }
            }

            if(Vars.net.client()){
                Vars.netClient.clearRemovedEntity(unit.id);
            }

            heat = 1f;
        }

        @Override
        public void configured(Unit builder, Object value){
            if((value instanceof Boolean) && builder != null && builder.isPlayer()){
                if(connections != 1) return; //do not let players inside 1-block pipes as that's unneeded and just promotes lag
                if((Boolean)value){
                    acceptUnit(builder, rotation);
                }
            }
            super.configured(builder, value);
        }

        @Override
        public Unit unit(){
            return (Unit)blockUnit;
        }

        @Override
        public boolean canControl(){
            return headless || unit().isPlayer() || (control.input.controlledType == UnitTypes.block && world.buildWorld(player.x, player.y) == self());
        }

        @Override
        public void updateTile(){
            if(tile == emptyTile) return;
            if(connections == 0){
                suck(0);
                suck(2);
            }
            else if (connections == 1){
                suck(rotation);
            }
            if(heat >= 0f) heat -= 0.005f * delta();

            if(!headless && unit() != null && unit().isPlayer() && unit().getPlayer() == player){
                int input = Useful.dwas();
                if(input >= 0 && lastPlayerKey != input && timer(timerControl, 25f)){
                    configure(input);
                    lastPlayerKey = input;
                }
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

            if(hasBaseRegion){
                Draw.z(Layer.blockUnder);
                Draw.rect(baseRegion, x, y);
            }
            Draw.z(Layer.block);
            units.each(u -> u.draw(this));
            Draw.rect(pipeRegions[contype], x, y);

            if(hasLightRegion && connections <= 1){
                Draw.z(Layer.bullet - 0.01f);
                Draw.color(heat > 0f ? Pal.accent : (enabled && (power == null || power.status > 0.9f) ? Pal.heal : Pal.remove));
                if(connections == 0){
                    Draw.rect(lightRegion, x, y, 0f);
                    Draw.rect(lightRegion, x, y, 180f);
                }
                else{
                    Draw.rect(lightRegion, x, y, rotation * 90f);
                }
                Draw.reset();
            }
        }

        @Override
        public void drawLight(){
            super.drawLight();
            units.each(u -> u.drawLight(this));
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            connections = 0;
            contype = 0;
            for(int i = 0; i < 4; i++){
                pipes[i] = null;
                Tile near = tile.nearby(Geometry.d4x(i) * 2, Geometry.d4y(i) * 2);
                if(near != null && (near.build instanceof ClearPipeBuild) && near.build.tile.pos() == near.pos() && near.build.team == team){
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
            units.clear();
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
                if(!read.bool() || headless) return u;
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

        @Override
        public float handleDamage(float amount){
            return unit().isPlayer() ? 0f : super.handleDamage(amount);
        }

        @Override
        public void drawStatus(){
            if(!isGate()) return;
            super.drawStatus();
        }

        public void effects(Vec2 v, float r, boolean big){
            if(big){
                bigSpitEffect.at(v, r);
                popSound.at(v, 0.9f);
            }
            else{
                spitEffect.at(v, r);
                popSound.at(v);
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
            if(datBigBoi(unit)){
                initf = Math.min(150f, ((net.active() ? unit.unit.hitSize * 1.3f : unit.unit.icon().width) - maxDrawSize) / 3f) + 15f;
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

        public boolean datBigBoi(){
            return datBigBoi(unit);
        }

        public boolean datBigBoi(UnitPayload unit){
            return (net.active() ? unit.unit.hitSize * 1.3f : unit.unit.icon().width) > maxDrawSize + 16f;
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

        public void vector(Vec2 v, ClearPipeBuild build){
            if(f <= 0.5f || to < 0) v.trns(from * 90f, tilesize * build.block.size * (0.5f - Math.min(1f, f))).add(build);
            else v.trns(to * 90f, tilesize * build.block.size * (Math.min(1f, f) - 0.5f)).add(build);
        }

        public void draw(ClearPipeBuild build){
            if(unit == null) return;
            TextureRegion icon = unit.unit.icon();
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
                vector(Tmp.v1, build);
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

        public void drawLight(ClearPipeBuild build){
            if(unit == null) return;
            UnitType type = unit.unit.type;
            if(type.lightRadius <= 0) return;
            vector(Tmp.v1, build);
            Drawf.light(Tmp.v1.x, Tmp.v1.y, type.lightRadius, type.lightColor, type.lightOpacity);
        }

        public void effects(ClearPipeBuild build){
            if(headless) return;
            Tmp.v2.trns(to * 90f, tilesize * size / 2f).add(build);
            build.effects(Tmp.v2, to * 90f, datBigBoi());
        }

        /** This is only called when the building is removed! */
        public void dump(ClearPipeBuild build){
            //init
            Player p = player();
            vector(Tmp.v1, build);
            float r = (f <= 0.5f || to < 0) ? from * 90f + 180f : to * 90f;
            unit.set(Tmp.v1.x, Tmp.v1.y,r);

            //clear removed state of unit so it can be synced
            if(net.client()){
                netClient.clearRemovedEntity(unit.unit.id);
            }

            unit.unit.vel.trns(r, ejectStrength / 2f);
            final Unit u = unit.unit;

            if(p == null){
                if(unit.dump()){
                    Core.app.post(() -> u.vel.trns(r, ejectStrength / 2f));
                }
            }
            else{
                //Useful.unlockCam();
                if(Useful.dumpPlayerUnit(unit, p)){
                    Core.app.post(() -> {
                        u.rotation = r;
                        u.vel.trns(r, ejectStrength / 2f);
                        if(u.isRemote()) u.move(u.vel.x, u.vel.y);
                    });
                }
            }
        }

        /** Returns true if the unit left this build. */
        public boolean update(ClearPipeBuild build){
            updateSavedTile();
            if(f < 0f){
                //special animation playing for fat units, do nothing
                f += Time.delta;
                Tmp.v1.trns(from * 90f, tilesize * build.block.size / 2f).add(build);
                if(player() != null && !net.active()) playerPipe.unit().set(Tmp.v1); //this is 99% aesthetics, and may cause rubberbanding issues for servers
                if(Mathf.chance(suckSmokeChance)) suckSmokeEffect.at(Tmp.v2.trns(from * 90f, -3f).add(Tmp.v1), from * 90f);
                /*Useful.lockCam(Tmp.v1.trns(from * 90f + 180f, size * build.block.size / 2f).add(build));*/
                if(f >= 0f){
                    f = 0f;
                    bigSuckEffect.at(Tmp.v1, from * 90f + 180f);
                }
            }
            else{
                if(Time.time - lastTime < 0.01f){
                    return false;
                }

                f += build.delta() * build.speed();
                Player p = player();
                if(p == null && playerPipe != null && !net.active()) playerPipe = null;

                if(p == null && unit.unit.spawnedByCore){
                    Fx.unitDespawn.at(unit.unit.x, unit.unit.y, 0f, unit.unit);
                    //Useful.unlockCam();
                    return true;
                }

                if(f <= 0.5f){
                    Tmp.v1.trns(from * 90f, tilesize * (0.5f - f) * build.block.size).add(build);
                    unit.set(Tmp.v1.x, Tmp.v1.y,from * 90f + 180f);
                    //not halfway yet
                    if(p != null){
                        int input = playerPipe.lastPlayerKey;
                        if(input >= 0 && from != input && build.validPipe(input)){
                            to = input;
                            playerPipe.lastPlayerKey = -1;
                        }
                        playerPipe.unit().set(Tmp.v1);
                        if(!headless && mobile && p == player) Core.camera.position.set(Tmp.v1);
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
                    Tmp.v1.trns(to * 90f, tilesize * (Math.min(f, 1f) - 0.5f) * build.block.size).add(build);
                    unit.set(Tmp.v1.x, Tmp.v1.y,to * 90f);
                    if(p != null){
                        playerPipe.unit().set(Tmp.v1);
                        if(!headless && mobile && p == player) Core.camera.position.set(Tmp.v1);
                    }

                    if(f > 1f){
                        if(to < 0) to = from;
                        if(build.pipes[to] != null && build.pipes[to].isValid()){
                            build.pipes[to].units.add(this);
                            reset();
                            return true;
                        }

                        //dump unit
                        //offset unit so ground units don't commit die
                        Tmp.v1.trns(to * 90f, tilesize * (build.block.size / 2f + 0.5f)).add(build);
                        unit.set(Tmp.v1.x, Tmp.v1.y,to * 90f);

                        //clear removed state of unit so it can be synced
                        if(net.client()){
                            netClient.clearRemovedEntity(unit.unit.id);
                        }

                        final Unit dumped = unit.unit;

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
                                dumped.apply(immuneStatus, 10f);
                                dumped.vel.trns(to * 90f, ejectStrength);
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
                                dumped.vel.trns(to * 90f, ejectStrength);
                                dumped.apply(immuneStatus, 10f);
                                if(p.isRemote()){
                                    //impulseNet behavior
                                    if(p.unit() != null) p.unit().move(dumped.vel.x, dumped.vel.y);
                                }
                                effects(build);
                                return true;
                            }

                            uturn();
                            return false;
                        }
                    }
                }
            }
            return false;
        }
    }
}
