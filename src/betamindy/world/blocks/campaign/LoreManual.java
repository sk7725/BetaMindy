package betamindy.world.blocks.campaign;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.actions.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import java.util.*;

import static arc.Core.*;
import static betamindy.BetaMindy.uwu;
import static mindustry.Vars.*;

//todo lore manual, comes with camera panning to give it attention every time it updates, intangible and stuff
//This esoterum manual is the unique manual found in the first shar sector. Not to be confused with BallisticManual (portal attack remainders) or LorePage (found on other sectors)
public class LoreManual extends Block {
    public static final String loreCutsceneTag = "bm-lore-c", loreQueueTag = "bm-lore-q", pageTag = "bm-lore-id";
    private static final String[] dott = {".", "..", "..."}; //why the heck is repeat() unsupported
    private static final Vec2[] vecs = new Vec2[]{new Vec2(), new Vec2(), new Vec2(), new Vec2()};

    public IntMap<Cutscene> cutscenes = IntMap.of(
            //first landing, notices the manual
            0, new MultiCutscene(new FocusCutscene(100f, 6f), new Wait(30f), new FocusCutscene(60f){{ offsetY = 20f; }}, new EnemyScanFail())
    );
    public int lorePages = 5; //this is the least number of lore-related pages needed to restore, not including things like post-game or easter eggs.
    public float scanRange = 80f;

    public Color flameColor = Pal2.esoterum;
    public Effect smokeEffect = MindyFx.smokeRise;
    public Effect flameEffect = MindyFx.manualFire;
    public Vec2 effectOffset = new Vec2(3f, 3f);
    public float effectChance = 0.18f;
    public float smokeChance = 0.06f;
    public float drawRotation = 18f; //there will only be one, and it just looks the best at this angle
    private static final Rect rect = new Rect();

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
    }

    public static boolean loreAdded(int id){
        if(settings.getBool(loreCutsceneTag, false) && settings.getInt(loreQueueTag, 0) != 0) return false;
        settings.put(loreCutsceneTag, true);
        settings.put(loreQueueTag, id);
        return true;
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.remove("health");
    }

    public class LoreManualBuild extends Building{
        public @Nullable Cutscene playing = null;
        public boolean cutsceneInit = false;
        public @Nullable Unit scanning = null;
        public float scanTime = 0f, heat;

        @Override
        public void updateTile(){
            if((uwu || state.isCampaign()) && (headless || !renderer.isCutscene())){
                if(playing == null){
                    if(uwu && !cutsceneInit) loreAdded(0); //todo remove
                    if(!cutsceneInit && settings.getBool(loreCutsceneTag, true)){
                        if(cutscenes.containsKey(settings.getInt(loreQueueTag, 0))){
                            ui.hudGroup.actions(Actions.alpha(0f, 0.17f));
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
                }
                else if(scanTime > 400f){
                    if(scanning.team == team){
                        buildDialog();
                    }
                    scanning = null;
                }
                else scanTime += delta();
            }

            heat = Mathf.lerpDelta(heat, scanning == null ? 0f : 1f, 0.05f);

            if(!headless){
                //todo make chance get smaller with more restoration
                if(Mathf.chance(effectChance)){
                    Tmp.v1.rnd(1f).add(this).add(effectOffset);
                    flameEffect.at(Tmp.v1.x, Tmp.v1.y, flameColor);
                }
                if(Mathf.chance(smokeChance)){
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
                Draw.z(Layer.effect + 0.01f);
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
        }

        @Override
        public void drawLight(){
            Drawf.light(team, x, y, lightRadius, flameColor, 0.65f + Mathf.absin(20f, 0.1f));
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

            Draw.reset();
        }

        public void buildDialog(){
            //todo
        }

        @Override
        public boolean shouldShowConfigure(Player player){
            if(!super.shouldShowConfigure(player) || playing != null) return false;
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
        public void drawSelect(){
            Drawf.dashCircle(x, y, scanRange, Tmp.c3.set(Pal2.esoterum).a(Mathf.absin(Time.globalTime, 8f, 1f)));
        }

        @Override
        public void configured(Unit builder, Object value){
            if(value instanceof Boolean goobie){
                if(goobie && scanning == null){
                    scanning = builder;
                    scanTime = 0f;
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
    }

    public class Cutscene {
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

    public class MultiCutscene extends Cutscene {
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

    public class FocusCutscene extends Cutscene {
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

    public class Wait extends Cutscene {
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

    public class EnemyScanFail extends Cutscene {
        public final float[][] path = { //(moveto) x, y, duration || (lookat) x, y, -duration
                {50f, 200f, 30f}, //off-screen
                {65f, -30f, 150f},
                {65f, -30f, 45f},
                {0f, 0f, -50f},
                {19f, -19f, 50f},
                {0f},
                {16f, -16f, 50f},
                {0f, 0f, -350f},
        };

        public float duration = 600;
        public UnitType unitType = UnitTypes.flare;

        protected float playtime = 0f, pathtime = 0f, px, py;
        protected Unit u = null;
        protected int pathi = 0;

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
            }
        }

        @Override
        public boolean update(LoreManualBuild build){
            if(playtime >= duration && pathi >= path.length){
                u.remove();
                return true;
            }
            if(u == null || u.dead) return false; //should not happen

            if(pathi < path.length){
                if(path[pathi].length == 1){//scan
                    build.scanning = u;
                    build.scanTime = 0f;
                    pathi++;
                    pathtime = 0;
                    return false;
                }

                float d = path[pathi][2];
                boolean rot = false;
                if(d < 0){
                    rot = true;
                    d *= -1;
                }
                pathtime += Time.delta;
                if(pathtime >= d){
                    if(!rot){
                        u.set(Tmp.v2.set(build.x + path[pathi][0], build.y + path[pathi][1]));
                        u.impulse(Tmp.v1.trns(u.rotation, 4f)); //make it seem natural
                    }
                    pathi++;
                    pathtime = 0;
                    px = u.x;
                    py = u.y;
                }
                else{
                    if(rot) u.rotation = Angles.moveToward(u.rotation, u.angleTo(build.x + path[pathi][0], build.y + path[pathi][1]), (180f / d) * Time.delta);
                    else{
                        u.lookAt(build.x + path[pathi][0], build.y + path[pathi][1]);
                        u.set(Tmp.v1.set(px, py).lerp(Tmp.v2.set(build.x + path[pathi][0], build.y + path[pathi][1]), pathtime / d));
                    }
                }
            }

            playtime += Time.delta;
            return false;
        }
    }
}
