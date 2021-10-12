package betamindy.world.blocks.campaign;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
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
import mindustry.world.*;
import mindustry.world.meta.*;

import java.util.*;

import static arc.Core.*;
import static betamindy.BetaMindy.uwu;
import static mindustry.Vars.*;

//todo lore manual, comes with camera panning to give it attention every time it updates, intangible and stuff
//This esoterum manual is the unique manual found in the first shar sector. Not to be confused with BallisticManual (portal attack remainders) or LorePage (found on other sectors)
public class LoreManual extends Block {
    public static final String loreCutsceneTag = "bm-lore-c", loreQueueTag = "bm-lore-q";
    private static final String[] dott = {".", "..", "..."}; //why the heck is repeat() unsupported
    private static final Vec2[] vecs = new Vec2[]{new Vec2(), new Vec2(), new Vec2(), new Vec2()};

    public IntMap<Cutscene> cutscenes = IntMap.of(0, new FocusCutscene(240f));
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
                    if(!cutsceneInit && settings.getBool(loreCutsceneTag, true)){
                        if(cutscenes.containsKey(settings.getInt(loreQueueTag, 0))){
                            playing = cutscenes.get(settings.getInt(loreQueueTag, 0));
                            playing.init();
                        }
                        cutsceneInit = true;
                    }
                }
                else{
                    if(playing.end()){
                        playing = null;
                        settings.put(loreCutsceneTag, false);
                        settings.put(loreQueueTag, 0);
                        return;
                    }
                    playing.update(this);
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
        public void draw(LoreManualBuild build){

        }

        public void update(LoreManualBuild build){

        }

        public boolean end(){
            return true;
        }

        public void init(){

        }
    }

    public class FocusCutscene extends Cutscene {
        public float duration;
        protected float playtime = 0f;

        public FocusCutscene(float duration){
            this.duration = duration;
        }

        @Override
        public void init(){
            playtime = 0f;
        }

        @Override
        public void update(LoreManualBuild build){
            Useful.cutscene(Tmp.v6.set(build));
            playtime += Time.delta;
        }

        @Override
        public boolean end(){
            if(playtime >= duration){
                Useful.cutsceneEnd();
                return true;
            }
            return false;
        }
    }
}
