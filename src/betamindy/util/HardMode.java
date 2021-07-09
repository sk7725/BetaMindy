package betamindy.util;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import betamindy.entities.bullet.*;
import betamindy.graphics.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.*;

public class HardMode {
    public final static float portalSize = 60f;
    public final static float maxCoreDamage = 4200f;
    public static final int rankLevel = 10;
    public static final int maxLevel = 200; //integer overflow

    public @Nullable Portal portal = null;
    public @Nullable CoreBlock.CoreBuild core = null;
    public float coreDamage = 0f;
    public int experience = 0;
    public Color[] lc1 = {Pal.sapBullet, Pal.lancerLaser, Color.coral, Pal2.exp, Color.cyan};
    public Color[] lc2 = {Color.pink, Pal2.placeLight, Pal.accent, Pal2.zeta, Color.pink};

    private final static Color tmpc = new Color();
    private boolean loaded = false;

    public BulletType[] lightning;

    public void init(){
        //lightning for each rank
        lightning = new BulletType[lc1.length];
        for(int i = 0; i < lc1.length; i++){
            lightning[i] = new PortalLightningBulletType(750f + 150f * i, lc1[i], lc2[i]);
        }

        Events.on(EventType.GameOverEvent.class, e -> {
            stop(false);
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            if(!loaded) reset();
            else loaded = false;
        });

        Events.on(EventType.UnitDestroyEvent.class, e -> {
            if(e.unit.team == state.rules.waveTeam && portal != null){
                portal.exp += Math.max(1f, (e.unit.hitSize - 8f) * 2f) * (portal.level < 10 ? 4f : (portal.level < 20 ? 2f : 1f));
                portal.kills++;
            }
        });

        /*Events.on(EventType.BlockDestroyEvent.class, e -> {
            if(portal != null && core != null && e.tile == core.tile){
                //but it refused.
                boolean cango = state.rules.canGameOver;
                state.rules.canGameOver = false;

            }
        });*/
    }

    public void update(){
        if(portal != null){
            if(core == null || !core.isValid() || core.dead()){
                //something went wrong, or one-shot the core
                core = null;
                if(portal.state != 2) stop(false);
            }
            else{
                if(core.health < core.maxHealth - 1f){
                    coreDamage += core.maxHealth - core.health;
                    core.heal();
                }
                if(coreDamage > maxCoreDamage && portal.state != 2){
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
        if(Core.input.keyTap(KeyCode.right) && portal != null){
            portal.wave++;
            portal.next();
        }
        if(Core.input.keyTap(KeyCode.left) && portal != null){
            portal.shootLightning();
            //ThickLightning.create(state.rules.waveTeam, portal.color(), 1000f, portal.x, portal.y, 200f, 50);
        }
    }

    public void draw(){
        if(portal != null){
            portal.draw();
            if(core != null && core.isValid()){
                Draw.z(Layer.effect);
                float rad = core.block.size * tilesize / 2f;
                int i = Math.min(portal.level / rankLevel, lc1.length - 1);
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

                core.block.drawPlaceText(portal.waveString(), core.tile.x, core.tile.y, true);
            }
        }
    }

    public int level(){
        return Math.min(maxLevel, (int)(Math.cbrt(experience / 25f)));
    }

    public float expCap(int l){
        if(l > maxLevel) l = maxLevel;
        return (l + 1) * (l + 1) * (l + 1) * 25f;
    }

    public void reset(){
        portal = null;
        core = null;
        experience = 0;
        if(!headless) ui.hudfrag.toggleHudText(false);
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
        if(!headless){
            MindySounds.portalOpen.play();
            ui.hudfrag.toggleHudText(true);
            ui.hudfrag.setHudText("\n" + Core.bundle.get("ui.hardmode.hudIntro"));
            ui.hudfrag.showToast(new TextureRegionDrawable(Core.atlas.find("betamindy-hardmode-portal-icon")), Core.bundle.get("ui.hardmode.intro"));
        }
    }

    public void stop(boolean win){
        if(portal == null) return;
        int current = level();
        float cap = expCap(current);
        if(win){
            experience += Math.min(portal.exp, cap + 1f);
        }
        if(!headless){
            MindySounds.portalClose.play();
            if(core != null && core.isValid()) MindyFx.portalCoreKill.at(core.x, core.y, core.block.size * tilesize, portal.color());
            //TODO custom dialogue
            final Portal lp = portal;
            Time.run(300f, () -> {
                ui.showOkText(Core.bundle.get("ui.hardmode.title") + " " + Core.bundle.format("ui.hardmode.level", lp.level),
                        Core.bundle.get(win ? "ui.hardmode.win" : "ui.hardmode.lose") + "\n" +
                        Core.bundle.format("stat.hardmode.waves", win ? lp.maxWave : lp.wave, lp.maxWave) + "\n" +
                        Core.bundle.format("stat.hardmode.kills", lp.kills) + "\n" +
                        Core.bundle.format("stat.hardmode.hardmodeKills", 0) + "\n" +
                        Core.bundle.format("stat.hardmode.exp", experience, cap, win ? Math.min(lp.exp, cap + 1f) : 0) + "\n" +
                        (experience >= cap ? Core.bundle.get("ui.hardmode.levelup") : ""),
                        () -> {}
                );
            });
            ui.hudfrag.toggleHudText(false);
            //ui.showOkText("$ui.hardmode.title", Core.bundle.format(win ? "ui.hardmode.clear" : "ui.hardmode.gameover", win ? portal.maxWave : portal.wave, portal.maxWave), () -> {});
        }
        Groups.bullet.each(b -> {
            if((b.owner instanceof Unit) && ((Unit) b.owner).team == state.rules.waveTeam) b.remove();
        });
        Groups.unit.each(u -> {
            if(u.team == state.rules.waveTeam){
                MindyFx.portalUnitDespawn.at(u.x, u.y, u.rotation, portal.color(), u.icon());
                u.remove();
            }
        });
        closePortal();
    }

    public void openPortal(float x, float y, int level){
        portal = new Portal(level, x, y, portalSize);
        //show the player that from now on, the portal is more aggressive than before
        if(level == 5) portal.shootLightning();
    }

    public void closePortal(){
        if(portal != null){
            portal.state = 2;
        }
    }

    //TODO: add Delete Hardmode Progress setting
    public void deleteCampaign(){
        Core.settings.remove("betamindy-campaign-exp");
    }

    //TODO: should be called from the initiator
    public void write(Writes write){
        //experience is special; it is global in campaign and should be saved to settings
        //everything else is saved to the initiator
        //portal pos is saved (sectors can have more than 1 spawn), core pos is not (in campaign, you should have one core anyways).
        if(state.isCampaign()){
            Core.settings.put("betamindy-campaign-exp", experience);
        }
        else{
            write.i(experience);
        }
        write.bool(portal != null);
        if(portal != null){
            write.f(coreDamage);
            write.b(portal.state);
            write.s(portal.level);
            write.i(portal.pos());
            write.f(portal.radius);
            write.f(portal.state == 1 || portal.state == 2 ? portal.r : portal.nextWave);
            write.s(portal.wave);
            write.i(portal.kills);
            write.i(portal.exp);
            write.bool(false);//todo for useCustomSpawn r/w later
        }
    }

    public void read(Reads read, byte revision){
        loaded = true;
        if(state.isCampaign()){
            experience = Core.settings.getInt("betamindy-campaign-exp", 0);
        }
        else{
            experience = read.i();
        }
        boolean hasPortal = read.bool();
        if(hasPortal){
            coreDamage = read.f();
            int ps = read.b();
            if(ps != 2) core = state.rules.defaultTeam.core();
            else core = null;

            int pl = read.s();
            Point2 pPos = Point2.unpack(read.i());
            float pRadius = read.f();
            portal = new Portal(pl, pPos.x * tilesize, pPos.y * tilesize, pRadius);
            portal.state = ps;
            if(ps == 1 || ps == 2) portal.r = read.f();
            else portal.nextWave = read.f();
            portal.wave = read.s();
            portal.kills = read.i();
            portal.exp = read.i();
            read.bool();
        }
        else{
            portal = null;
            core = null;
        }
    }

    public class Portal implements Position {
        public float x, y, radius;
        public float r = 0f;
        public int state = 0; //0: idle 1: opening 2: closing 3: wave
        public int level = 0;
        private float heat = 0f;

        public Seq<SpawnGroup> spawns = new Seq<>(); //todo used for special spawns
        public boolean useCustomSpawn = false;
        public int wave = 0, maxWave = 0;

        public int kills = 0, exp = 0;
        public float nextWave = 0f;

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

        public void runWave(Seq<SpawnGroup> spawns, int wave){
            if(net.client()) return;

            if(level >= 5){
                int n = (int)(Mathf.random() * Mathf.sqrt(level) * ((float)wave / maxWave) * 1.6f - 1f);
                if(n > 16) n = 16;
                for(int i = 0; i < n; i++){
                    Time.run(Mathf.random(Math.min(Mathf.sqrt(level) * 60f, 300f)), () -> {
                        if(state == 2) return;
                        shootLightning();
                    });
                }
            }
            //if(net.active()) Call.effectReliable(MindyFx.portalShockwave, x, y, 0f, color());
            //else MindyFx.portalShockwave.at(x, y, 0f, color());

            for(SpawnGroup group : spawns){
                if(group.type == null) continue;

                //todo add hardmode units to the batch
                int spawned = group.getSpawned(wave);
                for(int i = 0; i < spawned; i++){
                    Time.run(Mathf.range(120f), () -> {
                        if(BetaMindy.hardmode.portal == null || state == 2 || !Vars.state.isGame()) return;

                        Unit unit = group.createUnit(Vars.state.rules.waveTeam, wave);
                        unit.set(x + Mathf.range(20f), y + Mathf.range(20f));
                        unit.rotation = unit.angleTo(world.width()/2f * tilesize, world.height()/2f * tilesize);
                        unit.add();
                        unit.apply(MindyStatusEffects.portal, 99999f);
                        if(net.active()) Call.effect(MindyFx.portalSpawn, unit.x, unit.y, unit.hitSize, color());
                        else MindyFx.portalSpawn.at(unit.x, unit.y, unit.hitSize, color());
                    });
                }
            }
        }

        public void next(){
            int offset = useCustomSpawn ? 0 : level * 2;
            if(useCustomSpawn){
                runWave(spawns, wave);
            }
            else{
                runWave(Vars.state.rules.spawns, wave + offset);
            }
            MindyFx.portalShockwave.at(x, y, 0f, color());
            Sounds.wave.play();
            //much more benevolent version of shockwave; only damage whats inside the visual portal
            Damage.damage(Vars.state.rules.waveTeam, x, y, radius + 20f, 99999999f, true);
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
                if(state == 3){
                    if(Vars.state.enemies <= 0){
                        //you won lol
                        wave++;
                        if(!headless) ui.hudfrag.setHudText("\n" + waveString());
                        state = 0;
                        if(wave > maxWave){
                            stop(true);
                            state = 2;
                            return;
                        }
                        nextWave = wave == maxWave ? 100f : (maxWave == wave - 1 ? 1000f + level * 55f : 500f + level * 35f);
                        //TODO boss wave
                    }
                    /*
                    else{
                        //attack the player itself
                        //lightning
                        if(level >= 5 && Mathf.chanceDelta((float)(level % rankLevel) / rankLevel * 0.005f + (level < 10 ? 0.01f : 0.005f) + Math.max(0.006f, (float)(level / rankLevel) * 0.0005f))) shootLightning();
                    }*/
                }
                else{
                    if(nextWave > 0f) nextWave -= Time.delta;
                    else{
                        //next wave
                        if(wave <= maxWave){
                            next();
                            if(!headless) ui.hudfrag.setHudText("\n" + waveString());
                        }
                        state = 3;
                    }
                }
            }

            if(!headless){
                control.sound.loop(MindySounds.portalLoop, this, 0.7f);
                if(heat > 0f) heat -= Time.delta;
                if(Mathf.chanceDelta(0.01f)){
                    Useful.lightningCircle(x, y, r, 6, color());
                    if(renderer.bloom == null) heat = 11f;
                }
            }
        }

        public void shootLightning(){
            shoot(lightning[Math.min(level / rankLevel, lightning.length - 1)]);
        }

        public void shoot(BulletType b){
            if(net.client()) return;
            Posc target = Units.bestTarget(Vars.state.rules.waveTeam, x, y, Math.max(radius * 3f, b.range()), u -> !u.spawnedByCore, build -> !(build.block instanceof CoreBlock), Unit::dst2);
            float rot = target == null ? Angles.angle(x, y, world.width()/2f * tilesize, world.height()/2f * tilesize) : target.angleTo(this) + 180f;
            Tmp.v1.trns(Mathf.random(360f), radius * 2.3f).add(x, y);
            Call.createBullet(b, Vars.state.rules.waveTeam, Tmp.v1.x, Tmp.v1.y, rot, b.damage, 1f, 1f);
        }

        public void draw(){
            int i = Math.min(level / rankLevel, lc1.length - 1);
            if(renderer.bloom == null) Drawm.portal(x, y, r, tmpc.set(lc1[i]).lerp(Color.white, Mathf.clamp(heat / 11f)), lc2[i]);
            else Drawm.portal(x, y, r, lc1[i], lc2[i]);
            if(renderer.lights.enabled()) Drawf.light(x, y, r * 2.5f, lc1[i], 0.5f);
        }

        public Color color(){
            return lc1[Math.min(level / rankLevel, lc1.length - 1)];
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

        public int pos(){
            return Point2.pack((int)(x / tilesize), (int)(y / tilesize));
        }

        public String waveString(){
            return "< [#" + color().toString() + "]" + Core.bundle.format("save.wave", wave + " / " + maxWave) + "[] >";
        }
    }
}
