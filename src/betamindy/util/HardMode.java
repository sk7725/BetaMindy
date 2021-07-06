package betamindy.util;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import betamindy.graphics.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.*;

public class HardMode {
    public final static float portalSize = 60f;
    public final static float maxCoreDamage = 4200f;
    public @Nullable Portal portal = null;
    public @Nullable CoreBlock.CoreBuild core = null;
    public float coreDamage = 0f;
    public int experience = 0;
    public Color[] lc1 = {Pal.sapBullet, Pal.lancerLaser, Color.coral, Pal2.exp, Color.cyan};
    public Color[] lc2 = {Color.pink, Pal2.placeLight, Pal.accent, Pal2.zeta, Color.pink};

    private final static Color tmpc = new Color();

    public void init(){
        Events.on(EventType.GameOverEvent.class, e -> {
            stop(false);
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            //TODO reset portal unless saved (use rules tag?)
        });
    }

    public void update(){
        if(portal != null){
            if(core == null || !core.isValid() || core.dead()){
                //something went wrong, or one-shot the core
                if(portal.state != 2) stop(false);
            }
            else{
                if(core.health < core.maxHealth - 1f){
                    coreDamage += core.maxHealth - core.health;
                    core.heal();
                }
                if(coreDamage > maxCoreDamage && portal.state != 2){
                    //TODO fx
                    if(!headless){
                        Sounds.corexplode.at(core);
                    }
                    stop(false);
                }
            }
            portal.update();
        }
        //TODO remove test
        if(Core.input.keyTap(KeyCode.down)) stop(true);
        if(Core.input.keyTap(KeyCode.up)) start();
    }

    public void draw(){
        if(portal != null){
            portal.draw();
            if(core != null && core.isValid()){
                Draw.z(Layer.effect);
                float rad = core.block.size * tilesize / 2f;
                int i = Math.min(portal.level / 5, lc1.length - 1);
                Draw.color(lc1[i], lc2[i], Mathf.absin(Time.globalTime, 20f, 1f));

                Lines.stroke(0.8f);
                for(int j = 0; j < 4; j++){
                    Tmp.v1.trns(j * 90f + Time.time / 2f, rad + 3f);
                    //Drawf.tri(core.x + Tmp.v1.x, core.y + Tmp.v1.y, 7f, 8f, j * 90f + Time.time / 2f);
                    Lines.poly(Tmp.v1.x + core.x, Tmp.v1.y + core.y, 3, 4f, j * 90f + Time.time / 2f);
                }

                Lines.circle(core.x, core.y, rad - 1f);
                Lines.circle(core.x, core.y, rad - 5f);
                Lines.stroke(1.2f);
                Lines.polySeg(30, 0, (int)(30f * Mathf.clamp(1f - coreDamage / maxCoreDamage)), core.x, core.y, rad - 3f, 0f);

                float f = (Time.globalTime % 90f) / 90f;
                Lines.stroke(2f * (1f - f));
                Lines.square(core.x, core.y, rad * f);
                Draw.reset();
            }
        }
    }

    public int level(){
        //TODO
        return (int)Mathf.sqrt(experience * 2);
    }

    public void start(){
        start(level());
    }

    public void start(int level){
        if(portal != null) return;
        Tile spawn = spawner.getFirstSpawn();
        if(spawn == null || state.rules.defaultTeam.cores().size < 1){
            ui.showOkText("$ui.hardmode.title", "$ui.hardmode.error", () -> {});
            return;
        }
        //TODO play cool music
        core = state.rules.defaultTeam.core();
        coreDamage = 0f;
        openPortal(spawn.worldx(), spawn.worldy(), level);
        if(!headless) MindySounds.portalOpen.play();
    }

    public void stop(boolean win){
        if(portal == null) return;
        if(win) experience += 50;//TODO
        closePortal();
        if(!headless) MindySounds.portalClose.play();
    }

    public void openPortal(float x, float y, int level){
        portal = new Portal(level, x, y, portalSize);
    }

    public void closePortal(){
        if(portal != null){
            portal.state = 2;
        }
    }

    //TODO: should be called from the initiator
    public void write(Writes write){

    }

    public void read(Reads read){

    }

    public class Portal implements Position {
        public float x, y, radius;
        public float r = 0f;
        public int state = 0; //0: idle 1: opening 2: closing
        public int level = 0;
        private float heat = 0f;

        public Seq<SpawnGroup> spawns = new Seq<>();
        public int wave = 0, maxWave = 0;

        public Portal(int l, float x, float y, float r){
            level = l;

            this.radius= r;
            this.r = 0;
            state = 1;
            this.x = x;
            this.y = y;

            spawns.clear();
            maxWave = 10 + l / 2;
        }

        public void runWave(){
            if(net.client() || wave >= maxWave) return;
            for(SpawnGroup group : spawns){
                if(group.type == null) continue;

                int spawned = group.getSpawned(wave);
                for(int i = 0; i < spawned; i++){
                    Unit unit = group.createUnit(Vars.state.rules.waveTeam, wave);
                    Tmp.v1.trns(Mathf.random(360f), radius + 20f);
                    unit.set(x + Tmp.v1.x, y + Tmp.v1.y);
                    //todo use call & createBullet to play the unit spawn animation
                    //spawnEffect(unit);
                }
            }
        }

        public void update(){
            if(state == 1 || state == 2){
                r = Mathf.lerpDelta(r, state == 1 ? radius : 0f, 0.02f);
                if(state == 1 && Mathf.equal(r, radius, 0.1f)){
                    state = 0;
                    Useful.cutsceneEnd();
                    return;
                }
                else if(state == 2 && Mathf.zero(r, 0.1f)){
                    BetaMindy.hardmode.portal = null;
                    Useful.cutsceneEnd();
                    return;
                }

                if(!headless) Useful.cutscene(Tmp.v6.set(x, y));
            }
            else{
                if(wave >= maxWave){
                    stop(true);
                    state = 2;
                }
            }

            if(!headless){
                control.sound.loop(MindySounds.portalLoop, this, 0.6f);
                if(heat > 0f) heat -= Time.delta;
                if(Mathf.chanceDelta(0.01f)){
                    Useful.lightningCircle(x, y, r, 6, lc1[Math.min(level / 5, lc1.length - 1)]);
                    if(renderer.bloom == null) heat = 11f;
                }
            }
        }

        public void draw(){
            int i = Math.min(level / 5, lc1.length - 1);
            if(renderer.bloom == null) Drawm.portal(x, y, r, tmpc.set(lc1[i]).lerp(Color.white, Mathf.clamp(heat / 11f)), lc2[i]);
            else Drawm.portal(x, y, r, lc1[i], lc2[i]);
            if(renderer.lights.enabled()) Drawf.light(x, y, r * 2.5f, lc1[i], 0.5f);
        }

        @Override
        public float getX(){
            return x;
        }

        @Override
        public float getY(){
            return y;
        }

        public void set(float x, float y){
            this.x = x;
            this.y = y;
        }
    }
}
