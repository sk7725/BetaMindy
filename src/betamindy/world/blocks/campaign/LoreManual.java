package betamindy.world.blocks.campaign;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.ui.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;

import java.util.*;

import static arc.Core.*;
import static betamindy.BetaMindy.uwu;
import static mindustry.Vars.*;

//This esoterum manual is the unique manual found in the first shar sector. Not to be confused with BallisticManual (portal attack remainders) or LorePage (found on other sectors)
public class LoreManual extends Block {
    public static final String loreCutsceneTag = "bm-lore-c", loreQueueTag = "bm-lore-q", pageTag = "bm-lore-id";
    private static final String[] dott = {".", "..", "..."}; //why the heck is repeat() unsupported
    private static final Vec2[] vecs = new Vec2[]{new Vec2(), new Vec2(), new Vec2(), new Vec2()};

    public IntMap<Cutscene> cutscenes = IntMap.of(
            //first landing, notices the manual
            0, new MultiCutscene(new FocusCutscene(100f, 6f), new Wait(30f), new FocusCutscene(60f){{ offsetY = 20f; }}, new EnemyScanFail()),
            1, new PieceCutscene()
    );
    public int lorePages = 1; //this is the least number of lore-related pages needed to restore, not including things like post-game or easter eggs. Set automatically.
    public LorePages.Chapter defaultChapter = LorePages.esot0;
    public final Seq<ManualPiece> pageBlocks = new Seq<>(); //added automatically
    public float scanRange = 80f;

    public Color flameColor = Pal2.esoterum;
    public Color effectColor = Pal2.esoterum;
    public Effect smokeEffect = MindyFx.smokeRise;
    public Effect flameEffect = MindyFx.manualFire;
    public Vec2 effectOffset = new Vec2(3f, 3f);
    public float effectChance = 0.18f;
    public float smokeChance = 0.06f;
    public float drawRotation = 18f; //there will only be one, and it just looks the best at this angle
    private static final Rect rect = new Rect();
    protected boolean isPage = false;
    BaseDialog baseDialog = null;

    public LoreManual(String name){
        super(name);
        update = configurable = true;
        clipSize = 8000 * 4f;
        lightRadius = 80f;

        envEnabled = Env.any;
        drawDisabled = false;
        replaceable = false;
        rebuildable = false;
        drawDisabled = false;
        canOverdrive = false;
        targetable = false;
        config(Integer.class, (LoreManualBuild entity, Integer value) -> {
            if(!state.isEditor()) return;
            Building other = world.build(value);
            boolean contains = entity.links.contains(value);

            if(contains){
                //unlink
                entity.links.removeValue(value);
            }else if(linkValid(entity, other)){
                if(!entity.links.contains(other.pos())){
                    entity.links.add(other.pos());
                }
            }
        });
    }

    public static boolean loreAdded(int id){
        if(settings.getBool(loreCutsceneTag, false) && settings.getInt(loreQueueTag, 0) != 0) return false;
        settings.put(loreCutsceneTag, true);
        settings.put(loreQueueTag, id);
        return true;
    }

    public static boolean loreEmpty(){
        return !settings.getBool(loreCutsceneTag, false) || settings.getInt(loreQueueTag, 0) == 0;
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("health");
    }

    public boolean linkValid(Building tile, Building link){
        return tile != link && link != null;
    }

    public class LoreManualBuild extends Building{
        public @Nullable Cutscene playing = null;
        public @Nullable Unit scanning = null;
        public boolean cutsceneInit = false, invInit = false;
        public float scanTime = 0f, heat;
        public IntSeq links = new IntSeq();

        public boolean scannedOnce = uwu;
        private @Nullable ManualPiece lastScanned = null;
        protected float currentPages = -1f;

        @Override
        public void updateTile(){
            if(!invInit){
                for(ManualPiece p : pageBlocks){
                    if(InventoryModule.add(p, -1, team)){
                        if(p.chapter != null) p.chapter.unlock();
                        lastScanned = p;
                        break;
                    }
                }
                invInit = true;
            }

            if((uwu || state.isCampaign()) && (!net.active() && !renderer.isCutscene()) && !state.isEditor()){ //cutscene disabled for multiplayer
                if(playing == null){
                    //if(uwu && !cutsceneInit) loreAdded(0);
                    if(!cutsceneInit && settings.getBool(loreCutsceneTag, true)){
                        if(cutscenes.containsKey(settings.getInt(loreQueueTag, 0))){
                            Useful.cutscene(Tmp.v5.set(camera.position), true); //initialize cutscene
                            playing = cutscenes.get(settings.getInt(loreQueueTag, 0));
                            playing.init();
                        }
                        cutsceneInit = true;
                    }
                }
                else{
                    if(playing.update(this)){
                        playing = null;
                        settings.put(loreCutsceneTag, false);
                        settings.put(loreQueueTag, 0);
                        ui.hudGroup.actions(Actions.delay(0.2f), Actions.alpha(1f, 0.17f));
                        Useful.cutsceneEnd();
                        return;
                    }
                }
            }

            if(scanning != null){
                if(scanning.dead() || !scanning.isValid() || !scanning.within(this, scanRange)){
                    scanning = null;
                    return;
                }
                else if(scanTime > 400f || (scannedOnce && scanning.team == team)){
                    if(scanning.team == team){
                        buildDialog();
                        scannedOnce = true;
                    }
                    scanning = null;
                    return;
                }
                else scanTime += delta();

                for(int i = 0; i < links.size; i++){
                    Building b = world.build(links.get(i));
                    if(b != null && b.isValid()){
                        b.control(LAccess.enabled, scanTime > 200f ? 1 : 0, 0, 0, 0);
                        if(scanTime > 200f && b instanceof Turret.TurretBuild turret){
                            turret.control(LAccess.shootp, scanning, (scanTime > 300f && scanning.team != team) ? 1 : 0, 0, 0);
                        }
                    }
                }
            }
            else{
                for(int i = 0; i < links.size; i++){
                    Building b = world.build(links.get(i));
                    if(b != null && b.isValid()) b.control(LAccess.enabled, 0, 0, 0, 0);
                }
            }

            heat = Mathf.lerpDelta(heat, scanning == null ? 0f : 1f, 0.05f);

            if(!headless){
                if(currentPages < 0f){
                    currentPages = 0;
                    for(ManualPiece p : pageBlocks){
                        if(p.chapter != null && p.chapter.unlocked()) currentPages++;
                    }
                }
                if(Mathf.chance(effectChance * (currentPages / (1f + lorePages)))){
                    Tmp.v1.rnd(1f).add(this).add(effectOffset);
                    flameEffect.at(Tmp.v1.x, Tmp.v1.y, effectColor);
                }
                if(Mathf.chance(smokeChance * (currentPages / ((float)lorePages)))){
                    Tmp.v1.rnd(3.5f).add(this).add(effectOffset, 0.5f);
                    smokeEffect.at(Tmp.v1.x, Tmp.v1.y, Pal.gray);
                }
            }
        }

        @Override
        public void draw(){
            Drawf.shadow(region, x, y - 1f, drawRotation);
            Draw.rect(region, x, y, drawRotation);
            Draw.color(Pal.rubble);
            Draw.rect("scorch-2-0", x, y);
            Draw.alpha(0.5f);
            Draw.rect("scorch-4-1", x, y);
            Draw.z(Layer.blockOver);
            Draw.alpha(0.25f);
            Draw.rect("scorch-0-1", x + 3f, y + 2f);

            if(scanning != null){
                Draw.z(Layer.overlayUI + 0.01f);
                if(scanTime > 300f){
                    if(scanning.team == team) drawPlaceText(bundle.get("scan.granted"), tile.x, tile.y, true);
                    else drawPlaceText(bundle.get("scan.deny"), tile.x, tile.y, false);
                }
                else if(scanTime > 200f){
                    drawPlaceText("ID: [#" + scanning.team.color.toString() + "]" + teamName(scanning.team) + "[]", tile.x, tile.y, true);
                }
                else drawPlaceText(bundle.get("scan.scanning") + dott[(int)(Time.time / 30f) % 3], tile.x, tile.y, true);
                Draw.z(Layer.buildBeam);
                if(!renderer.animateShields) Draw.blend(Blending.additive);
                Draw.color();
                Draw.mixcol(flameColor, 1f);
                Draw.alpha(heat * Mathf.clamp((400f - scanTime) / 100f));
                Draw.rect(scanning.type.shadowRegion, scanning, scanning.rotation - 90);
                Draw.mixcol();
                Draw.color(flameColor, Draw.getColor().a);
                drawScanner(scanning.x, scanning.y, scanning.hitSize / 3.3f);
                Draw.blend();
            }
            drawIndicator();
            Draw.reset();
            if(playing != null){
                playing.draw(this);
                Draw.reset();
            }
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius, flameColor, 0.65f + Mathf.absin(20f, 0.1f));
        }

        public String teamName(Team team){
            return switch(team.id){
                case 0 -> "SER-02";
                case 1 -> "SER-06";
                case 2 -> "SER-04";
                default -> "???";
            };
        }

        public void drawIndicator(){
            Draw.z(Layer.overlayUI - 0.01f);
            if(team == player.team()){
                if(!rect.setSize(Core.camera.width * 0.9f, Core.camera.height * 0.9f)
                        .setCenter(Core.camera.position.x, Core.camera.position.y).contains(x, y)){

                    Tmp.v1.set(this).sub(player).setLength(14f);

                    Lines.stroke(2f);
                    Draw.color(Color.white, flameColor, Mathf.clamp((Time.globalTime % 120f) / 60f));
                    Lines.lineAngle(player.x + Tmp.v1.x, player.y + Tmp.v1.y, Tmp.v1.angle(), 4f);
                }
            }
        }

        public void drawScanner(float tx, float ty, float sz){
            Lines.stroke(1f);
            float focusLen = 3f + Mathf.absin(Time.time, 3f, 0.6f);
            float ang = angleTo(tx, ty);
            float px = x + Angles.trnsx(ang, focusLen);
            float py = y + Angles.trnsy(ang, focusLen);

            vecs[0].set(tx - sz, ty - sz);
            vecs[1].set(tx + sz, ty - sz);
            vecs[2].set(tx - sz, ty + sz);
            vecs[3].set(tx + sz, ty + sz);

            Arrays.sort(vecs, Structs.comparingFloat(vec -> -Angles.angleDist(angleTo(vec), ang)));

            Vec2 close = Geometry.findClosest(x, y, vecs);

            float x1 = vecs[0].x, y1 = vecs[0].y,
                    x2 = close.x, y2 = close.y,
                    x3 = vecs[1].x, y3 = vecs[1].y;

            if(renderer.animateShields){
                Fill.square(tx, ty, sz);
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

            float f0 = Draw.getColor().a;
            Draw.reset();
            Draw.z(Layer.effect + 0.005f);
            Lines.stroke(f0, scanning.team.color);
            float f1 = Interp.exp10Out.apply(Mathf.clamp(scanTime / 50f));
            float f2 = Interp.exp10Out.apply(Mathf.clamp(scanTime / 50f - 1f));
            float s = scanning.hitSize + 12f;
            Lines.rect(scanning.x - s * f1 * f0 / 2f, scanning.y - s * f2 * f0 / 2f, s * f1 * f0, s * f2 * f0);
            Draw.z(Layer.buildBeam);
            Lines.stroke(f2 * Mathf.clamp((250f - scanTime) / 50f) * 4f);
            Tmp.v3.set(scanning.x, scanning.y + Mathf.sin(18f, f1 * s / 2f - 4f));
            Lines.lineAngleCenter(Tmp.v3.x, Tmp.v3.y, 0f, s - 4f);
            Draw.z(Layer.effect + 0.005f);

            float ox = scanning.x - (s / 2f + 1f) * f0;
            float isize = 7f * f0;
            if(scanTime > 80f){
                Draw.color(flameColor);
                float t = (scanTime - 80f) % 40f / 40f;
                Lines.stroke(1f - t);
                Lines.circle(ox - isize / 2f, scanning.y + (s / 2f - 0.5f) - isize / 2f, t * (isize / 2f + 0.5f));
            }

            Draw.color();
            Draw.mixcol(scanning.team.color, 1f);
            Draw.rect(atlas.find("team-" + scanning.team.name, scanning.icon()), ox - isize / 2f, scanning.y + (s / 2f - 0.5f) - isize / 2f,
                    isize * frac(80f, 20f), isize * (0.2f + 0.8f * frac(100f, 20f)), 0f);
            Draw.mixcol();
            Lines.stroke(1f);
            drawBar(ox, scanning.y - (s / 2f) + 1f, f0 * frac(100f, 40f) * (Mathf.absin(Time.time - 1f, 8f, 10f) + 3f));
            drawBar(ox, scanning.y - (s / 2f) + 3f, f0 * frac(120f, 30f) * (Mathf.absin(-Time.time, 13f, 8f) + 8f));
            drawBar(ox, scanning.y - (s / 2f) + 5f, f0 * frac(140f, 20f) * (Mathf.absin(Time.time - 2f, 11f, 2f) + 12f));
            Draw.color();
        }

        public float frac(float start, float duration){
            return Interp.exp10Out.apply(Mathf.clamp((scanTime - start) / duration));
        }

        public void drawBar(float x, float y, float len){
            Draw.color(flameColor);
            Lines.lineAngle(x, y, 180f, len);
            Draw.color(scanning.team.color);
            Lines.lineAngle(x, y, 180f, Mathf.absin(Mathf.cos(Time.time, 9f, 6.28f), 1f, len * 0.8f));
        }

        public void buildDialog(){
            if(baseDialog == null) baseDialog = new ManualDialog((LoreManual) block);
            baseDialog.show();
        }

        @Override
        public boolean shouldShowConfigure(Player player){
            if(!super.shouldShowConfigure(player) || playing != null) return false;
            if(state.isEditor()) return true;
            if(scanning == null && player.dst2(this) <= scanRange * scanRange){
                configure(true);
                return false;
            }
            if(!headless){
                if(scanning != null) ui.showInfoToast("@ui.scan.wait", 3f);
                else ui.showInfoToast("@ui.scan.failed", 3f);
            }
            return false;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(!net.active() && state.isEditor() && linkValid(this, other)){
                configure(other.pos());
                return false;
            }
            return true;
        }

        @Override
        public void drawSelect(){
            if(playing != null) return;
            Drawf.dashCircle(x, y, scanRange, Tmp.c3.set(Pal2.esoterum).a(Mathf.absin(Time.globalTime, 8f, 1f)));
        }

        void squares(Building b, Color color){
            float radius = b.block.size * tilesize / 2f;
            Lines.stroke(3f, Pal.gray);
            Lines.square(b.x, b.y, radius + 1f);
            Lines.stroke(1f, color);
            Lines.square(b.x, b.y, radius);
        }

        @Override
        public void drawConfigure(){
            if(!state.isEditor()) return;
            squares(this, Pal2.coin);

            for(int i = 0; i < links.size; i++){
                Building link = world.build(links.get(i));

                if(link != this && linkValid(this, link)){
                    boolean linked = links.indexOf(link.pos()) >= 0;
                    if(linked){
                        squares(link, flameColor);
                    }
                }
            }

            Draw.reset();
        }

        public void onScan(Unit builder){
            scanning = builder;
            scanTime = 0f;
        }

        @Override
        public void configured(Unit builder, Object value){
            if(value instanceof Boolean goobie){
                if(goobie && scanning == null){
                    onScan(builder);
                }
            }
            else super.configured(builder, value);
        }

        //if this somehow gets destroyed you have no way of game progression, you know
        @Override
        public float handleDamage(float amount){
            return 0f;
        }

        @Override
        public void damage(float damage){
        }

        @Override
        public boolean canPickup(){
            return false;
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(links.size);
            for(int i = 0; i < links.size; i++){
                write.i(links.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision < 2) return;
            links.clear();
            short amount = read.s();
            for(int i = 0; i < amount; i++){
                links.add(read.i());
            }
        }

        public ManualPiece getLastPiece(){
            return lastScanned == null ? (pageBlocks.size == 0 ? (ManualPiece) MindyBlocks.esotPage1 : pageBlocks.get(0)) : lastScanned;
        }
    }

    public static class Cutscene {
        public void init(){

        }

        public void draw(LoreManualBuild build){

        }

        /**
         * Updates every frame.
         * @return true if cutscene ends at this frame
         */
        public boolean update(LoreManualBuild build){
            return true;
        }
    }

    public static class MultiCutscene extends Cutscene {
        public Cutscene[] multi;
        protected int now = 0;

        public MultiCutscene(Cutscene... cutscenes){
            multi = cutscenes;
        }

        @Override
        public void init(){
            now = 0;
            multi[0].init();
        }

        @Override
        public void draw(LoreManualBuild build){
            if(now < multi.length) multi[now].draw(build);
        }

        @Override
        public boolean update(LoreManualBuild build){
            if(multi[now].update(build)){
                //next
                now++;
                if(now >= multi.length) return true;
                multi[now].init();
            }
            return false;
        }
    }

    public static class FocusCutscene extends Cutscene {
        public float duration;
        public float zoom; //if negative, doesnt change camera zoom
        public float moveTime;
        public float offsetX = 0f, offsetY = 0f;

        protected float playtime = 0f;
        protected final Vec2 campos = new Vec2();

        public FocusCutscene(float duration, float zoom, float moveTime){
            this.duration = duration;
            this.zoom = zoom;
            this.moveTime = moveTime;
        }

        public FocusCutscene(float duration, float zoom){
            this(duration, zoom, 50f);
        }

        public FocusCutscene(float duration){
            this(duration, -1);
        }

        @Override
        public void init(){
            playtime = 0f;
            campos.set(camera.position);
        }

        @Override
        public boolean update(LoreManualBuild build){
            if(playtime >= duration){
                return true;
            }

            Useful.cutscene(playtime >= moveTime ? Tmp.v6.set(build).add(offsetX, offsetY) : Tmp.v6.set(campos).lerp(build.x + offsetX, build.y + offsetY, Interp.smoother.apply(playtime / moveTime)), false); //hidden by the loreBlock
            if(zoom > -1){
                if(playtime >= moveTime) renderer.setScale(Scl.scl(zoom));
                else renderer.setScale(Mathf.lerp(renderer.getScale(), Scl.scl(zoom), playtime / moveTime));
            }
            playtime += Time.delta;
            return false;
        }
    }

    public static class Wait extends Cutscene {
        public float duration;
        protected float playtime = 0f;

        public Wait(float duration){
            this.duration = duration;
        }

        @Override
        public void init(){
            playtime = 0f;
        }

        @Override
        public boolean update(LoreManualBuild build){
            if(playtime >= duration){
                return true;
            }
            playtime += Time.delta;
            return false;
        }
    }

    public static class PieceCutscene extends Cutscene {
        public final float startDelay = 45f, passDelay = 90f;
        public Effect addEffect;
        public Sound addSound = MindySounds.synthSample;
        public float baseDuration = startDelay + 60f + passDelay;
        public float duration;

        protected float playtime = 0f;
        protected boolean started, added;
        protected ManualPiece piece;

        public PieceCutscene(Effect addEffect){
            this.addEffect = addEffect;
            duration = baseDuration + addEffect.lifetime + 10f;
        }
        public PieceCutscene(){
            this(MindyFx.ionBurstSmall);
        }

        @Override
        public void init(){
            piece = null;
            playtime = 0f;
            started = added = false;
            Image white = new Image();
            white.touchable = Touchable.disabled;
            white.setColor(0f, 0f, 0f, 0f);
            white.setFillParent(true);
            white.actions(Actions.fadeIn(startDelay / 60f), Actions.delay(0.25f), Actions.fadeOut(0.5f), Actions.remove());
            white.update(() -> {
                if(!Vars.state.isGame()){
                    white.remove();
                }
            });
            Core.scene.add(white);
        }

        @Override
        public boolean update(LoreManualBuild build){
            if(piece == null){
                piece = build.getLastPiece();
            }
            if(playtime >= startDelay + 2f && !started){
                started = true;

                Useful.snapCam(Tmp.v6.set(build.x + 20f, build.y - 40f));
                player.unit().set(Tmp.v6);
                player.unit().rotation = player.unit().angleTo(build);
            }

            if(started && playtime >= startDelay + 60f && !added){
                if((playtime - startDelay - 60f) > passDelay){
                    added = true;
                    addEffect.at(build);
                    addSound.at(build, (float)Math.pow(2, piece.chapter == null ? 0 : piece.chapter.id / 12.0));
                    build.currentPages = -1;
                }
            }

            if(playtime >= duration){
                return true;
            }
            playtime += Time.delta;
            return false;
        }

        @Override
        public void draw(LoreManualBuild build){
            if(started && playtime >= startDelay + 60f && !added){
                float f = Mathf.clamp((playtime - startDelay - 60f) / passDelay);
                Tmp.v1.set(player.unit()).lerp(build, f);
                float h = 40f * f * (1 - f);
                float z = Draw.z();
                Draw.z(Layer.overlayUI - 0.01f);
                Draw.rect(piece.region, Tmp.v1.x, Tmp.v1.y + h, Interp.fastSlow.apply(f) * 100f + 15f);
                Draw.z(z);
            }
        }
    }

    public static class EnemyScanFail extends Cutscene {
        public final float[][] path = { //(moveto) x, y, duration || (lookat) x, y, -duration
                {50f, 200f, 30f}, //off-screen
                {45f, -25f, 150f},
                {0f, 45f},
                {0f, 0f, -50f},
                {25f, -25f, 50f},
                {0f},
                {0f, 50f},
                {0f, 0f, -300f},
                {250f, -10f, 120f, 1f}, //at frame 300, the sentry goes brr
                {0f, 30f}, //killed by cutscene gun
        };

        public float duration = 600;
        public UnitType unitType = UnitTypes.flare;

        protected float playtime = 0f, pathtime = 0f, px, py;
        protected Unit u = null;
        protected int pathi = 0;
        protected @Nullable Cutscene panner = null;

        public EnemyScanFail(){
        }

        @Override
        public void init(){
            playtime = pathtime = 0f;
            pathi = 0;
            u = unitType.create(Team.crux);
            u.set(world.width() * tilesize - 30f, world.height() * tilesize - 30f);
            px = u.x; py = u.y;
            u.rotation = 0f;

            Events.fire(new EventType.UnitCreateEvent(u, null, null));
            if(!Vars.net.client()){
                u.add();
                u.apply(StatusEffects.unmoving, 999999f);
                u.apply(StatusEffects.disarmed, 999999f);
                u.apply(MindyStatusEffects.cutsceneDrag, 999999f);
            }
        }

        @Override
        public boolean update(LoreManualBuild build){
            if(playtime >= duration && pathi >= path.length){
                if(!u.dead) u.remove();
                return true;
            }
            if(u == null) return false; //should not happen

            if(pathi < path.length){
                if(path[pathi].length == 1){//scan
                    build.scanning = u;
                    build.scanTime = 0f;
                    pathi++;
                    pathtime = 0;
                    panner = new FocusCutscene(300f, -1f, 290f){{
                        offsetY = 15f;
                        offsetX = 12f;
                    }};
                    panner.init();
                    return false;
                }
                else if(path[pathi].length == 2){//wait
                    if(pathtime >= path[pathi][1]){
                        pathi++;
                        pathtime = 0;
                        return false;
                    }
                    pathtime += Time.delta;
                }
                else{//move
                    float d = path[pathi][2];
                    boolean rot = false;
                    if(d < 0){
                        rot = true;
                        d *= -1;
                    }
                    pathtime += Time.delta;
                    if(u.dead){
                        if(pathtime >= d){
                            pathi++;
                            pathtime = 0;
                        }
                        return false;
                    }
                    if(pathtime >= d){
                        if(!rot && pathi != 0 && path[pathi].length != 4){
                            u.set(Tmp.v2.set(build.x + path[pathi][0], build.y + path[pathi][1]));
                            Tmp.v3.set(Tmp.v2).sub(px, py);
                            u.vel.trns(Tmp.v3.angle(), Tmp.v3.len() / d);
                        }
                        pathi++;
                        pathtime = 0;
                        px = u.x;
                        py = u.y;
                    }
                    else{
                        if(rot) u.rotation = Angles.moveToward(u.rotation, u.angleTo(build.x + path[pathi][0], build.y + path[pathi][1]), (180f / d) * Time.delta);
                        else if(path[pathi].length == 4){
                            float r = Angles.moveToward(u.rotation, u.angleTo(build.x + path[pathi][0], build.y + path[pathi][1]), 10f * Time.delta);
                            u.rotation = r;
                            u.vel.trns(r, path[pathi][3]); //run for it
                            Useful.cutscene(Tmp.v6.set(camera.position).lerp(Tmp.v2.set(build).lerp(u, 0.5f), Mathf.clamp(playtime / (d / 2f))));
                        }
                        else{
                            u.vel.setZero();
                            u.rotation = Angles.moveToward(u.rotation, u.angleTo(build.x + path[pathi][0], build.y + path[pathi][1]), 10f * Time.delta);
                            u.set(Tmp.v1.set(px, py).lerp(Tmp.v2.set(build.x + path[pathi][0], build.y + path[pathi][1]), pathtime / d));
                        }
                    }
                }
            }
            if(panner != null && panner.update(build)) panner = null;

            playtime += Time.delta;
            return false;
        }
    }
}
