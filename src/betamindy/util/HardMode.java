package betamindy.util;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import betamindy.entities.bullet.*;
import betamindy.graphics.*;
import betamindy.world.blocks.environment.*;
import mindustry.*;
import mindustry.content.*;
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
    public Color[] lc1 = {Pal.sapBullet, Pal.lancerLaser, Color.coral, Pal.heal, Color.cyan};
    public Color[] lc2 = {Color.pink, Pal2.placeLight, Pal.accent, Pal2.zeta, Color.pink};
    private static final Rand rand = new Rand();

    private final static Color tmpc = new Color();
    private boolean loaded = false;

    public BulletType[] lightning;
    public BulletType[] crystals;

    /** Called when mod is initialized. */
    public void init(){
        Events.on(EventType.GameOverEvent.class, e -> {
            stop(false);
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            if(!loaded) reset();
            loaded = false;
        });

        Events.on(EventType.UnitDestroyEvent.class, e -> {
            if(e.unit.team == state.rules.waveTeam && portal != null){
                portal.exp += Math.max(1f, (e.unit.hitSize - 8f) * 2f) * (portal.level < 10 ? 4f : (portal.level < 20 ? 2f : 1f));
                portal.kills++;
            }
        });
    }

    /** Called after contents load. */
    public void load(){
        //lightning for each rank
        lightning = new BulletType[lc1.length];
        for(int i = 0; i < lc1.length; i++){
            lightning[i] = new PortalLightningBulletType(750f + 150f * i, lc1[i], lc2[i]);
        }

        //crystal rewards todo matter crystals
        crystals = new BulletType[]{
                new CrystalBulletType((Crystal)MindyBlocks.crystalPyra),
                new CrystalBulletType((Crystal)MindyBlocks.crystalCryo),
                new CrystalBulletType((Crystal)MindyBlocks.crystalScalar),
                new CrystalBulletType((Crystal)MindyBlocks.crystalVector),
                new CrystalBulletType((Crystal)MindyBlocks.crystalTensor),
                new CrystalBulletType((Crystal)MindyBlocks.crystalBittrium)
        };
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

        if(BetaMindy.uwu && Core.input.keyDown(KeyCode.shiftRight)){
            //I am running out of keys to bind to this thing help
            if(Core.input.keyTap(KeyCode.down)) stop(true);
            if(Core.input.keyTap(KeyCode.up)) start();
            if(Core.input.keyTap(KeyCode.right) && portal != null){
                experience += (int)(expCap(level()) - expCap(level() - 1));
            }
            if(Core.input.keyTap(KeyCode.left) && portal != null){
                portal.nextWave = 0f;
            }
            if(Core.input.keyTap(KeyCode.slash) && portal != null){
                portal.shootCrystal(2, 3);
            }
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
                Lines.arc(core.x, core.y, rad - 3f, Mathf.clamp(1f - coreDamage / maxCoreDamage), 0f, 30);

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

    public static float expCap(int l){
        if(l < 0) return 0f;
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
        core = state.rules.defaultTeam.core();
        coreDamage = 0f;
        openPortal(spawn.worldx(), spawn.worldy(), level);
        if(!headless){
            MindySounds.portalOpen.play();
            ui.hudfrag.showToast(new TextureRegionDrawable(Core.atlas.find("betamindy-hardmode-portal-icon")), Core.bundle.get("ui.hardmode.intro"));
            BetaMindy.musics.playUntil(0, () -> BetaMindy.hardmode.portal == null, 1, false, true);
        }
    }

    public void stop(boolean win){
        if(portal == null) return;
        int current = level();
        float cap = expCap(current);
        float bf = expCap(current - 1);
        if(win){
            experience += Math.min(portal.exp, cap + 1f);
        }
        if(!headless){
            MindySounds.portalClose.play();
            if(core != null && core.isValid()) MindyFx.portalCoreKill.at(core.x, core.y, core.block.size * tilesize, portal.color());
            final Portal lp = portal;
            Time.run(300f, () -> {
                ui.showOkText(Core.bundle.get("ui.hardmode.title") + " " + Core.bundle.format("ui.hardmode.level", lp.level),
                        Core.bundle.get(win ? "ui.hardmode.win" : "ui.hardmode.lose") + "\n" +
                        Core.bundle.format("stat.hardmode.waves", lp.wave, lp.maxWave) + "\n" +
                        Core.bundle.format("stat.hardmode.kills", lp.kills) + "\n" +
                        Core.bundle.format("stat.hardmode.hardmodeKills", 0) + "\n" +
                        (current >= maxLevel ? "" : Core.bundle.format("stat.hardmode.exp", experience - bf, cap - bf, win ? Math.min(lp.exp, cap + 1f) : 0) + "\n") +
                        (experience >= cap ? Core.bundle.get("ui.hardmode.levelup") : ""),
                        () -> {}
                );
            });
        }
        Groups.bullet.each(b -> {
            if((b.owner instanceof Unit) && ((Unit) b.owner).team == state.rules.waveTeam) b.remove();
        });
        Groups.unit.each(u -> {
            if(u.team == state.rules.waveTeam){
                MindyFx.portalUnitDespawn.at(u.x, u.y, u.rotation + 90f, portal.color(), u.icon());
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

    public Color color(){
        if(portal != null) return portal.color();
        return lc1[Math.min(level() / rankLevel, lc1.length - 1)];
    }

    public Color getRandomColor(Color tmp, long seed){
        int l = Math.min(lc1.length - 1, (portal == null ? level() : portal.level) / rankLevel);
        return tmp.set(lc1[l]).lerp(lc2[l], Mathf.randomSeed(seed));
    }

    public String barText(){
        if(portal == null || portal.state == 1) return Core.bundle.get("ui.hardmode.hudIntro");
        if(isBoss() && portal.wave == portal.maxWave + 1) return Core.bundle.get("bar.waveboss");
        return portal.waveStringDull();
    }

    public float barVal(){
        if(portal == null) return 0f;
        if(portal.state == 1) return portal.r / portal.radius;
        if(portal.state == 2 || portal.wave >= portal.maxWave) return 1f;
        return portal.nextWave / (portal.nextWaveCap - 120f); //for aesthetics
    }

    public float lvlf(){
        int lv = level();
        if(lv >= maxLevel) return 1f;
        float lb = expCap(lv - 1);
        float lc = expCap(lv);
        return ((float) experience - lb) / (lc - lb);
    }

    public boolean isBoss(){
        return level() % 10 == 9;
    }

    //TODO: add Delete Hardmode Progress setting
    public void deleteCampaign(){
        Core.settings.remove("betamindy-campaign-exp");
        Core.settings.remove("betamindy-endhs");
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
            write.f(portal.r);

            write.s(portal.wave);
            write.i(portal.kills);
            write.i(portal.exp);
            write.f(portal.nextWave);

            write.bool(false);//for useCustomSpawn r/w later
        }
    }

    public void read(Reads read, byte revision){
        reset();
        if(world.isGenerating()) loaded = true; //do not set loaded to true when receiving a block snapshot (i think)
        Log.info("Loaded hardmode data! isGenerating : " + world.isGenerating());
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
            portal.r = read.f();

            portal.wave = read.s();
            portal.maxWave = 10 + pl / 5;
            portal.kills = read.i();
            portal.exp = read.i();
            portal.nextWave = read.f();
            portal.nextWaveCap = (portal.maxWave == portal.wave - 1 ? 1000f + pl * 10f : 500f + pl * 3f);

            read.bool();
        }
        else{
            portal = null;
            core = null;
        }
    }

    public class Portal implements Position {
        public float x, y, radius;
        public float r;
        public int state; //0: idle 1: opening 2: closing 3: wave
        public int level;
        private float heat = 0f;
        public boolean endless = false;

        public Seq<SpawnGroup> spawns = new Seq<>(); //todo used for special spawns
        public boolean useCustomSpawn = false;
        public int wave = 0, maxWave = 0;

        public int kills = 0, exp = 0;
        public float nextWave = 0f, nextWaveCap = 1f;

        public Portal(int l, float x, float y, float r){
            level = l;

            this.radius= r;
            this.r = 0;
            state = 1;
            this.x = x;
            this.y = y;

            spawns.clear();
            maxWave = 10 + l / 5;
        }

        public void runWave(Seq<SpawnGroup> spawns, int wave){
            if(net.client()) return;

            //lightning
            if(lightningWave(wave)){
                int n = (int)(Mathf.random() * Mathf.sqrt(level) * ((float)wave / maxWave) * 1.6f - 1f);
                if(n > 16) n = 16;
                else if(n <= 0) n = 1;
                for(int i = 0; i < n; i++){
                    Time.run(Mathf.random(Math.min(Mathf.sqrt(level) * 60f, 300f)), () -> {
                        if(state == 2) return;
                        shootLightning();
                    });
                }
            }
            int disaster = disaster(wave);
            //if(net.active()) Call.effectReliable(MindyFx.portalShockwave, x, y, 0f, color());
            //else MindyFx.portalShockwave.at(x, y, 0f, color());

            for(SpawnGroup group : spawns){
                if(group.type == null) continue;

                //if(disaster == 6){
                    //todo add hardmode units to the batch
                //}
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
            int offset = useCustomSpawn ? 0 : level * 10;
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
                    r = radius;
                    Useful.cutsceneEnd();
                    BetaMindy.musics.go();
                    return;
                }
                else if(state == 2 && Mathf.zero(r, 0.1f)){
                    BetaMindy.hardmode.portal = null;
                    Useful.cutsceneEnd();
                    return;
                }

                if(!headless) Useful.cutscene(Tmp.v6.set(x, y));
            }
            else if(state == 4){
                //boss start cutscene
                r = Mathf.lerpDelta(r, 0f, 0.006f);
                if(Mathf.zero(r, 0.01f)){
                    //todo
                    state = 5;
                    if(!headless) ui.showOkText("@ui.hardmode.title", "@ui.hardmode.demoend", () -> {});
                    Time.run(120f, () -> {
                        stop(false);
                    });
                }
            }
            else if(state == 5){
                //todo boss
            }
            else{
                if(state == 3){
                    //during a wave
                    if(nextWaveCap - nextWave > 180f && Vars.state.enemies <= 0){
                        //wait for 3 seconds before checking if all units are dead
                        //you won lol
                        state = 0;

                        int reward = reward(wave);
                        if(reward == 1){
                            coreDamage = Math.max(0, coreDamage - maxCoreDamage / 2);
                            if(core != null){
                                MindyFx.coreHeal.at(core, core.block.size);
                                MindySounds.portalOpen.at(core, 1.7f);
                            }
                        }
                        else if(reward == 2){
                            rewardCrystal();
                        }

                        if(isBoss()){
                            if(wave == maxWave){
                                nextWave = nextWaveCap = 190f; //no real meaning
                                wave++;
                                if(!headless) BetaMindy.mui.hardfrag.nextWave(this);
                                state = 4; //boss cutscene
                                r = radius;
                            }
                            else if(wave >= maxWave + 1){
                                stop(true);
                                state = 2;
                                return;
                            }
                        }
                        else{
                            if(wave >= maxWave){
                                stop(true);
                                state = 2;
                                return;
                            }
                        }
                    }
                }

                //waiting a wave
                if(nextWave > 0f){
                    if(Vars.state.enemies <= 0) nextWave -= Time.delta;
                    else nextWave -= Time.delta * Mathf.clamp(level * 0.015f);
                }
                else{
                    //next wave
                    if(wave < maxWave){
                        wave++;
                        nextWave = (maxWave == wave - 1 ? 1000f + level * 10f : 500f + level * 3f);
                        nextWaveCap = nextWave;
                        next();
                        if(!headless) BetaMindy.mui.hardfrag.nextWave(this);
                    }
                    state = 3;
                }
            }

            if(!headless){
                control.sound.loop(MindySounds.portalLoop, this, 0.7f);
                if(heat > 0f) heat -= Time.delta;
                if(state != 4 && Mathf.chanceDelta(0.01f)){
                    Useful.lightningCircle(x, y, r, 6, color());
                    if(renderer.bloom == null) heat = 11f;
                }
            }
        }

        //lightning may overlap with other disasters
        public boolean lightningWave(int wave){
            if(level < 5 || wave <= 1 || wave >= maxWave - 1 || wave % 5 == 0) return false;
            switch(level / rankLevel){
                case 0:
                    return wave % 4 == 2;
                case 1:
                    return wave % 3 == 0;
                case 2:
                    return wave % 6 == 3;
                case 3:
                    return wave % 2 == 0;
                case 4:
                    return wave % 4 == 0;
                default:
                    return wave % 3 == 1;
            }
        }

        //0: none 2: asteroid 3: firestorm 4: laser 5: bhol 6: hardmode units
        public int disaster(int wave){
            if(wave <= 1 || wave >= maxWave - 1) return 0;
            if(level < 10) return 0;
            if(wave % 5 == 0) return 6;
            switch(level / rankLevel){
                case 1:
                    //blu
                    if(wave % 7 == 5) return 2;
                    break;
                case 2:
                    //orange
                    if(wave % 7 == 0) return 3;
                    if(wave % 5 == 3) return 2;
                    break;
                case 3:
                    //green
                    if(wave % 7 == 6 || (level >= 35 && wave > 5 && wave % 5 == 4)) return 4;
                    if(wave % 7 == 0) return 3;
                    break;
                case 4:
                    //pink
                    if(wave % 10 == 9) return 5;
                    if(wave > 5 && wave % 5 == 4) return 4;
                    if(wave % 7 == 5) return 2;
                    break;
                default:
                    //pink
                    if(wave % 11 == 9) return 5;
                    if(wave % 4 == 3) return 4;
                    if(wave % 7 == 3) return 2;
                    if(wave % 5 == 4) return 3;
                    break;
            }
            return 0;
        }

        //unlike disasters, this is given at the end of the wave, and may misleadingly look like it is given in the start of the next wave
        //1: heal 2: crystal
        public int reward(int wave){
            if(wave <= 1 || wave >= maxWave - 1) return 0;
            if(wave % 5 == 0) return 1;
            rand.setSeed(level + 69);
            if(rand.random(6) < 1) return 1;
            if(level > 10 && rand.random(20 - Mathf.clamp(level / 5, 0, 10)) < 1) return 2;
            return 0;
        }

        public void shootLightning(){
            shoot(lightning[Math.min(level / rankLevel, lightning.length - 1)]);
        }

        public void shoot(BulletType b){
            if(net.client()) return;
            Posc target = Units.bestTarget(Vars.state.rules.waveTeam, x, y, Math.max(radius * 3f, b.range), u -> !u.spawnedByCore, build -> !(build.block instanceof CoreBlock), Unit::dst2);
            float rot = target == null ? Angles.angle(x, y, world.width()/2f * tilesize, world.height()/2f * tilesize) : target.angleTo(this) + 180f;
            Tmp.v1.trns(Mathf.random(360f), radius * 2.3f).add(x, y);
            Call.createBullet(b, Vars.state.rules.waveTeam, Tmp.v1.x, Tmp.v1.y, rot, b.damage, 1f, 1f);
        }

        public void rewardCrystal(){
            if(net.client()) return;
            if(level / rankLevel < 2){
                if(Mathf.chance(0.25f)) shootCrystal(1, Mathf.random(1, (int)Math.min(3, 2 + 0.5f * level / rankLevel)));
                else shootCrystal(0, Mathf.random(1, Math.min(4, 2 + level / rankLevel)));
            }
            else{
                for(int i = 0; i < crystals.length; i++){
                    if(i == crystals.length - 1 || Mathf.randomBoolean() || (i == crystals.length - 2 && level < 45)){
                        shootCrystal(i, Mathf.random(1, Mathf.clamp(4 + level / rankLevel - i, 1, crystals.length - i + 1)));
                        break;
                    }
                }
            }
        }

        public void shootCrystal(int id, int amount){
            if(world.width() < 20 || world.height() < 20) return; //too small to spawn crystals; they would clog the map up
            for(int i = 0; i < amount; i++){
                Tile t = null;
                for(int j = 0; j < 5; j++){
                    //keep looking for a good tile randomly
                    t = world.tile(Mathf.random(5, world.width() - 5), Mathf.random(5, world.height() - 5));
                    if(t != null && t.block() == Blocks.air && dst2(t.worldx(), t.worldy()) > radius * radius) break;
                }
                if(t == null) return;
                Tmp.v1.set(t.worldx(), t.worldy());
                float lifeScl = Mathf.dst(x, y, Tmp.v1.x, Tmp.v1.y) / crystals[id].range;
                Call.createBullet(crystals[id], Vars.state.rules.defaultTeam, x, y, 180f + Tmp.v1.angleTo(this), -1, 1f, lifeScl);
            }
        }

        public void draw(){
            int i = Math.min(level / rankLevel, lc1.length - 1);
            if(state == 4){
                float f = Mathf.clamp(3f * (r / radius) - 2f);
                float f2 = Mathf.clamp(1.5f * r / radius);
                Drawm.portal(x, y, f2 * radius, tmpc.set(Pal.remove).lerp(Color.white, f), tmpc);
            }
            else{
                if(renderer.bloom == null){
                    Drawm.portal(x, y, r, tmpc.set(lc1[i]).lerp(Color.white, Mathf.clamp(heat / 11f)), lc2[i]);
                }
                else{
                    Drawm.portal(x, y, r, lc1[i], lc2[i]);
                }
            }

            if(renderer.lights.enabled()) Drawf.light(x, y, r * 2.5f, lc1[i], 1f);
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

        public String waveStringDull(){
            return "[accent]<[] " + Core.bundle.format("save.wave", wave + " / " + maxWave) + " [accent]>[]";
        }

        public String waveString(){
            return "< [#" + color().toString() + "]" + Core.bundle.format("save.wave", wave + " / " + maxWave) + "[] >";
        }

        public void buildNext(Table table){
            table.defaults().size(22f).padLeft(3f);
            if(wave == maxWave){
                if(isBoss()) table.image(Core.atlas.find("betamindy-hardmode-boss-icon"));
                else table.image(Core.atlas.find("betamindy-hardmode-portal-icon"));
            }

            if(lightningWave(wave + 1)) table.image(Core.atlas.find("betamindy-disaster1"));
            int dis = disaster(wave + 1);
            int rew = reward(wave + 1);
            if(dis != 0) table.image(Core.atlas.find("betamindy-disaster" + dis));
            if(rew != 0) table.image(Core.atlas.find("betamindy-reward" + rew));
            //todo more
        }
    }
    public class RuleCache {
        //todo
    }
}
