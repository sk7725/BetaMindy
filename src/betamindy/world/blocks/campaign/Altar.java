package betamindy.world.blocks.campaign;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.ui.*;
import betamindy.util.*;
import betamindy.world.blocks.defense.*;
import betamindy.world.blocks.defense.Campfire.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static arc.Core.settings;
import static arc.graphics.g2d.Lines.*;
import static betamindy.BetaMindy.hardmode;
import static betamindy.BetaMindy.uwu;
import static mindustry.Vars.*;

//initiator
public class Altar extends Block {
    //can be overriden for higher tier. Note that it does not need to be 4.
    public int[][] torchOffset = {{3, 0}, {0, 3}, {-3, 0}, {0, -3}};
    public int torches = 4;
    private final String[] lore = {"yqjsrzrlgqjjsyjrltgqlmqlztjpwzxnyqjsrmqltysygqjmuzzkq", "cqzryjsrzrlbulsljqyxnzqlztjpwzzswyrkqjpkrlnkvlaxqZyqjeyqjvxvknljqjnq", "cqwnyctjsrmn", "wtlxjsymsrgqjjslmojmqjajrccrgnzmqlxnqxpjnqgrlulsljqzuzzqxjrjawtkrkqzrgqyyqxrlslmujwnxjrjaxngqjtgjslcpymqlztjrzzxvmuctzvjecvjsyjryxsyalslkqzrgqyyqxvmuctzvje", "yqycplkubnxrmqcrjqymjslxpjoxalfnkqjzryjslzrlgqjzrfrcrjqymjslxpzjrwyrkqyqycplkukngqjircrjqymjslxpjoxslrxncplkuyplkkpyqcrjqymjslxpjocplkuypljtllrcrjqymjslxpjoyqycplkucqxwaljryzqmqcrjqymjslxpjoyqycplkuxpcrzgqljslmqcrjqymjslxxpxncplkujqbscrjqymjslxpjo"};

    public TextureRegion[] heatRegions = new TextureRegion[3];
    public float runeLerp = 0.01f;

    public Effect updateEffect = MindyFx.altarDust;
    public Effect updateEffect2 = MindyFx.altarDustSmall;
    public Seq<AltarGameMode> pages;
    public TextureRegion lockedIcon;

    protected @Nullable AltarBuild currentAltar;

    public Altar(String name){
        super(name);

        update = true;
        solid = false;
        rotate = false;
        size = 3;
        clipSize = 200f;
        configurable = true;
        saveConfig = false;
        noUpdateDisabled = false;
        sync = true;

        config(Integer.class, (AltarBuild build, Integer i) -> {
            if(i < 1 || i > pages.size) return;
            if(build.canStart(i - 1)) pages.get(i - 1).start.get(build, i - 1);
        });

        //TODO the current bosses are a joke
        pages = Seq.with(new AltarGameMode("invasion", () -> false, b -> b.charged && b.heat > 0.999f, (b, i) -> b.startInvasion()),
                new AltarGameMode(UnitTypes.dagger, 0),
                new AltarGameMode(UnitTypes.fortress, 1),
                new AltarGameMode(UnitTypes.quad, 2),
                new AltarGameMode(UnitTypes.corvus, 3),
                new AltarGameMode(UnitTypes.eclipse, 4),
                new AltarGameMode("endless", () -> (hardmode.level() < 69) && !uwu, b -> false, (b, i) -> {}).setLabel(() -> Core.bundle.format("endless.highscore", settings.getInt("betamindy-endhs", 0))),
                new AltarGameMode("ancient"));

        Events.on(WorldLoadEvent.class, e -> {
            currentAltar = null;
        });
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 3; i++){
            heatRegions[i] = atlas.find(name + "-heat" + i);
        }

        pages.each(AltarGameMode::load);
        lockedIcon = atlas.find("betamindy-mode-locked");
    }

    @Override
    public boolean canBreak(Tile tile){
        return super.canBreak(tile) && ((AltarBuild)tile.build).connections <= 0;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return super.canBeBuilt() && (team == Team.derelict || currentAltar == null || !currentAltar.isValid());
    }

    //todo: configure-sync the level of the portal spawned for multiplayer campaign
    public class AltarBuild extends Building{
        public int phase = 0;
        public int menuPage = 0;
        public int amount;
        public boolean charged = false;

        public float[] heatTorch = new float[torches];
        public Item[] sacrifice = new Item[torches];
        public short[] made = new short[torches];

        public float heatTorchSum, heat;
        public int connections = 0;
        private float lastSpawnDir = -1f;

        public @Nullable Tile torch(int i){
            if(i >= torches) return null;
            Tile t = tile.nearby(torchOffset[i][0], torchOffset[i][1]);
            if(t == null || !(t.block() instanceof Campfire) || !((Campfire)t.block()).isTorch) return null;
            return t;
        }

        @Override
        public void created(){
            super.created();
            lastSpawnDir = -1f;
        }

        @Override
        public void updateTile(){
            if(team != Team.derelict) currentAltar = this;
            if(hardmode.portal != null){
                phase = 3;
                if(heat > 0.01f) heat -= delta() / 60f;
                return; //do not do anything
            }
            //portal is over
            switch(phase){
                case 1 -> phase1();
                case 3 -> {
                    heat = 0f;
                    phase = 0;
                }
                default -> phase0();
            }
        }

        public boolean canStart(){
            return canStart(menuPage);
        }

        public boolean canStart(int page){
            if(team == Team.derelict || hardmode.portal != null || state.rules.defaultTeam.cores().size < 1 || !state.rules.waves || state.rules.attackMode || state.rules.pvp) return false;
            if(state.isCampaign() && (!state.hasSector() || !state.getSector().isCaptured())) return false;
            return phase == 1 && pages.get(page).canStart.get(this);
        }

        public boolean isLocked(int page){
            if(page < 0 || page >= pages.size) return true;
            return phase == 0 || pages.get(page).locked.get();
        }

        public void startInvasion(){
            charged = false;
            hardmode.start();
            heat = 1f;
        }

        public void startBoss(int page){
            //todo
        }

        public void phase0(){
            heatTorchSum = 0f;
            connections = 0;
            for(int i = 0; i < torches; i++){
                boolean b = torch(i) == null;
                heatTorch[i] = Mathf.lerpDelta(heatTorch[i], b ? 0f : 1f, 0.05f);
                heatTorchSum += heatTorch[i];
                if(!b) connections++;
            }
            heatTorchSum /= torches;
            if(heat < 1f) heat += runeLerp * delta();

            if(lastSpawnDir < 0f){
                Tile t = spawner.getFirstSpawn();
                if(t == null) lastSpawnDir = Mathf.random(360f);
                else lastSpawnDir = angleTo(t.worldx(), t.worldy());
            }
            if(heatTorchSum > 0.5f && Mathf.chance((heatTorchSum - 0.5f) *2f* 0.3f)){
                Tmp.v1.trns(Mathf.random(360f), size * tilesize / 1.414f).clamp(-size * tilesize /2f, -size * tilesize / 2f, size * tilesize /2f, size * tilesize / 2f);
                updateEffect.at(Tmp.v1.x + x, Tmp.v1.y + y, lastSpawnDir);
            }
            if(heatTorchSum > 0.75f && Mathf.chance(((heatTorchSum - 0.75f) * 0.2f))){
                Tmp.v1.trns(Mathf.random(360f), 36f + Mathf.random() * 6f);
                updateEffect2.at(Tmp.v1.x + x, Tmp.v1.y + y, lastSpawnDir);
            }

            if(heatTorchSum > 0.999f && heat > 0.999f){
                phase = 1;
                initTorch(hardmode.experience);
            }
        }

        public void initTorch(int seed){
            phase = 1;
            int rank = hardmode.level() / HardMode.rankLevel;
            amount = rank >= 7 ? 12800 : 100 << rank;
            amount /= 4;
            heat = 0f;
            for(int i = 0; i < torches; i++){
                sacrifice[i] = charged ? Items.coal : FireColor.items.get(Mathf.randomSeed(((long) seed) * torches + i, 0, Math.min(FireColor.items.size - 1, 6 + rank * 2)));
                made[i] = 0;
                Tile torch = torch(i);
                if(torch != null && !charged){
                    Fx.healWaveMend.at(torch.drawx(), torch.drawy(), 15f, FireColor.from(sacrifice[i]));
                }
            }
            MindySounds.easterEgg2.at(this, 0.75f);
        }

        /** Returns true if all sacrifice has been met. */
        public boolean consumeTorch(Tile torch, int i){
            if(made[i] >= amount) return true;
            if(torch.build.items.empty()) return false;
            if(torch.build.items.first() != sacrifice[i]) return false;
            int a = Math.min(torch.build.items.get(sacrifice[i]), amount - made[i] + 3);
            if(a > 3){ //leave some for vanity
                torch.build.items.remove(sacrifice[i], a - 3);
                made[i] += a-3;
            }
            return false;
        }

        public void phase1(){
            heatTorchSum = 0f;
            connections = 0;
            int dones = 0;
            for(int i = 0; i < torches; i++){
                Tile t = torch(i);
                if(t != null){
                    connections++;
                    boolean done = charged || consumeTorch(t, i);
                    t.build.control(LAccess.enabled, done ? 0 : 1, 0, 0, 0);
                    if(done){
                        ((CampfireBuild)t.build).torchEffects();
                        dones++;
                        heatTorchSum += 1f;
                    }
                    else heatTorchSum += made[i] / (float)amount;
                }
                else{
                    phase = 0;
                    MindyFx.altarOrbDespawn.at(x, y, heat);
                    heat = 1f;
                    return;
                }
            }
            heatTorchSum /= (float)torches;

            heat = Mathf.lerpDelta(heat, charged ? 1f : heatTorchSum, 0.03f);
            if(dones >= torches && heat > 0.999f){
                if(!charged) MindyFx.portalShockwave.at(x, y, hardmode.color());
                charged = true;
            }

            if(lastSpawnDir < 0f){
                Tile t = spawner.getFirstSpawn();
                if(t == null) lastSpawnDir = Mathf.random(360f);
                else lastSpawnDir = angleTo(t.worldx(), t.worldy());
            }
            if(Mathf.chance(0.4f)){
                Tmp.v1.trns(Mathf.random(360f), size * tilesize / 1.414f).clamp(-size * tilesize /2f, -size * tilesize / 2f, size * tilesize /2f, size * tilesize / 2f);
                updateEffect.at(Tmp.v1.x + x, Tmp.v1.y + y, lastSpawnDir);
            }
            if(Mathf.chance(0.15f)){
                Tmp.v1.trns(Mathf.random(360f), 36f + Mathf.random() * 6f);
                updateEffect2.at(Tmp.v1.x + x, Tmp.v1.y + y, lastSpawnDir);
            }
        }

        @Override
        public void buildConfiguration(Table table){
            table.clearChildren();
            menuPage = Mathf.clamp(menuPage, 0, pages.size - 1);
            table.table(t -> {
                if(HardmodeFragment.background != null) t.background(HardmodeFragment.background);
                t.add("[#ef8aff]Portal[][white].OS[]", Styles.techLabel, 1.2f).pad(4f).padBottom(4f);
                t.row();
                t.image().color(Pal2.portal).growX().height(3f).padLeft(6f).padRight(6f);
                t.row();
                t.image().color(Pal2.portalBack).growX().height(3f).padLeft(6f).padRight(6f).padTop(-0.1f);
                t.row();
                Label lab = new Label(this::phaseText);
                lab.setFontScale(0.8f);
                t.add(lab).color(Pal2.portalBack).pad(4f).padTop(0f);
                t.row();

                t.image().color(Pal.gray).fillX().growX().height(4f).padBottom(3f);
                t.row();
                t.table(s -> {
                    s.button(Icon.left, Styles.cleari, () -> {
                        if(menuPage > 0){
                            menuPage--;
                            buildConfiguration(table);
                        }
                    }).disabled(b -> menuPage <= 0).fillY().width(35f).left();

                    s.table(sm -> {
                        sm.image(() -> pages.get(Mathf.clamp(menuPage, 0, pages.size - 1)).icon()).size(130f);
                        sm.row();
                        sm.label(() -> pages.get(Mathf.clamp(menuPage, 0, pages.size - 1)).local()).get().setStyle(new Label.LabelStyle(Styles.outlineLabel));
                    }).grow();

                    s.button(Icon.right, Styles.cleari, () -> {
                        if(menuPage < pages.size - 1){
                            menuPage++;
                            buildConfiguration(table);
                        }
                    }).disabled(b -> menuPage >= pages.size - 1).fillY().width(35f).right();
                }).fillX().height(160f).visible(() -> phase > 0);
                t.row();

                if(pages.get(menuPage).name.equals("invasion")){
                    t.table(lv -> {
                        lv.label(() -> Core.bundle.format("ui.hardmode.lv", hardmode.level())).size(80f, 26f);
                        lv.add(new SBar(this::expText, () -> Pal2.exp, hardmode::lvlf, "betamindy-barM", 1, 2).outline(false)).pad(3f).growX().height(22f);
                    }).fillX().height(27f).pad(2f).visible(() -> phase > 0);
                }
                else{
                    t.label(pages.get(menuPage).label).fillX().height(27f).pad(2f).visible(() -> phase > 0);
                }

                t.row();
                t.image().color(Pal.gray).fillX().growX().height(4f).padTop(2f).padBottom(2f);
                t.row();

                t.table(but -> {
                    but.button("Start", new TextureRegionDrawable(Core.atlas.find("betamindy-hardmode-portal-icon")).tint(Pal2.portal), Styles.cleart, () -> {
                        configure(menuPage + 1);
                    }).height(33f).growX().disabled(b -> !canStart()).get().getLabel().setStyle(new Label.LabelStyle(Styles.techLabel));
                    but.button(Icon.info, Styles.cleari, 27f, () -> {
                        //todo
                    }).size(33f).disabled(b -> isLocked(menuPage));
                }).fillX().visible(() -> phase > 0);
            }).width(275f);
        }

        public String expText(){
            int l = hardmode.level();
            if(l >= HardMode.maxLevel) return Core.bundle.get("bar.altar.max");
            float lc = HardMode.expCap(l - 1);
            return Core.bundle.format("bar.altar.exp", hardmode.experience - lc, HardMode.expCap(l) - lc);
        }

        public String phaseText(){
            if(phase == 0){
                if(heat < 1f) return Core.bundle.format("altar.setup", (int)(heat * 100));
                return Core.bundle.format("altar.0", (int)(heatTorchSum * 100));
            }
            if(phase == 1){
                if(charged) return Core.bundle.get("altar.done");
                return Core.bundle.format("altar.1", (int)(heat * 100));
            }
            if(phase == 3) return Core.bundle.get("altar.3");
            return "...";
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            if(phase == 1){
                for(int i = 0; i < torches; i++){
                    Tile torch = torch(i);
                    if(torch != null){
                        torch.block().drawPlaceText(made[i]+"/"+amount, torch.x, torch.y, made[i] > 0);
                    }
                }
            }
        }

        @Override
        public void draw(){
            super.draw();
            int rank = Math.min(hardmode.level() / HardMode.rankLevel, hardmode.lc1.length - 1);
            Color c1 = hardmode.lc1[rank];
            Color c2 = hardmode.lc2[rank];

            if(connections > 0){
                for(int i = 0; i < torches; i++){
                    Tile torch = torch(i);
                    if(torch != null){
                        Campfire camp = (Campfire)torch.block();
                        Draw.z(Layer.block + 1f);
                        Draw.color();
                        Draw.rect(camp.torchRegion, torch.drawx(), torch.drawy());
                        if(phase >= 1 && phase != 3){
                            Draw.z(Layer.bullet);
                            Draw.color(c1, c2, Mathf.absin(Time.time + i * 20f, 11f, 1f));
                            Draw.alpha(made[i] / (float)amount);
                            Draw.rect(camp.torchHeatRegion, torch.drawx(), torch.drawy());
                            FireColor.fset(sacrifice[i], Mathf.absin(15f, 0.5f));
                            float r = Mathf.sin(17f, 2f);
                            Drawm.spark(torch.drawx(), torch.drawy(), 6f - Math.abs(r), 2f, r * 15f);
                        }
                    }
                }
            }

            if(phase != 3 && hardmode.portal != null) return; //minimize lag during invasions
            if(phase == 3){
                if(heat < 0.02f) return;
                c1 = c2 = Tmp.c4.set(Pal2.clearWhite).lerp(Color.white, heat);
                Draw.z(Layer.bullet);
            }
            else{
                Draw.z(Layer.blockUnder);
                Draw.blend(Blending.additive);
            }
            float f0 = phase == 0 || phase == 3 ? heat : 1f;

            Draw.color(c1, Mathf.absin(21f, 0.7f) + 0.3f);
            drawingCircle(x, y, 24, Math.min(1f, f0 * (torches + 1)), 1.5f);
            for(int i = 0; i < torches; i++){
                Tmp.v1.set(torchOffset[i][0], torchOffset[i][1]).scl(tilesize).add(this);
                float f = Mathf.clamp(f0 * (torches + 1) - i - 1f);
                stroke(f);
                square(Tmp.v1.x, Tmp.v1.y, Mathf.lerp(5 * 1.414f, 12f, heatTorch[i]), Mathf.lerp(0f, (Time.time / 4f) % 360f, heatTorch[i]));
                stroke(1);
                drawingCircle(Tmp.v1.x, Tmp.v1.y, 12, f, 1f);
                stroke(0.5f * heatTorch[i]);
                square(Tmp.v1.x, Tmp.v1.y, 12, Time.time * -1f);
            }
            if(heatTorchSum > 0.01f || phase > 0){
                float f1 = 1f;
                float f2 = 1f;
                float f3 = 1f;
                float fin= 1f;
                if(phase == 0){
                    f1 = Mathf.clamp(heatTorchSum * 3f);
                    f2 = Mathf.clamp(heatTorchSum * 3f - 1f);
                    f3 = Mathf.clamp(heatTorchSum * 3f - 2f);
                    fin = Mathf.clamp(heatTorchSum * torches - torches + 1f);
                }
                else if(phase == 3){
                    f1 = f2 = f3 = fin = heat;
                }
                stroke(0.5f);
                Draw.color(c1, Mathf.absin(13f, 0.6f) + fin * 0.4f);
                circle(x, y, 36 * f2);
                circle(x, y, 42 * f2);
                square(x, y, 36 * f2, 2f*Time.time + 45f);
                square(x, y, 36 * f2, -Time.time + 45f);

                Draw.color(c2, Mathf.absin(16f, 0.5f) + 0.5f);
                stroke(1f);
                circle(x, y, 12 * 1.414f * f1);
                stroke(1 * f3);
                for(int i = 0; i < 6; i++){
                    Tmp.v1.trns(i * 60 + Time.time * 1.4f, 20 * f3);
                    poly(Tmp.v1.x + x, Tmp.v1.y + y, 3, 3f, Tmp.v1.angle());
                }
                Draw.blend();

                if(fin > 0.1f){
                    int n = 30;
                    int m = lore[rank].length();
                    int off = (int)(Time.time / 3f) % (n * m);
                    for(int i = off; i < n + off; i++){
                        final Color c = i - off < n / 2 ? c1 : c2;
                        if(phase == 3){
                            Draw.color(Color.white);
                            Draw.alpha(heat);
                            Draw.z(Layer.bullet);
                        }
                        else if((phase == 1 && Mathf.randomSeed(i + id + m) < heat) || phase == 2){
                            Draw.z(Layer.bullet);
                            Draw.color(c, Color.white, (i - off) % ((float)n/2) / (float)(n/2));
                        }
                        else{
                            Draw.z(Layer.blockUnder);
                            Draw.color(c, (i - off) % ((float)n/2) / (float)(n/2) * fin);
                        }

                        Tmp.v1.trns(i * 360f / n, 39f).add(this);
                        Drawm.koruh(Tmp.v1.x, Tmp.v1.y, 3f, i * 360f / n - 90f, lore[rank].charAt(i % m));
                    }
                }
            }

            if(phase >= 1){
                Draw.z(Layer.bullet);
                Draw.blend();
                for(int j = 0; j < 3; j++){
                    float f1 = phase == 1 ? Mathf.clamp(heat * 3f - j) : 1f;
                    Draw.color(c1, c2, Mathf.absin(Time.time + j * 15f, 11f, 1f));
                    Draw.alpha(f1);
                    Draw.rect(heatRegions[j], x, y);
                }

                float f1 = phase == 1 ? heat : 1f;
                if(phase == 3){
                    Tmp.v1.trns(lastSpawnDir, (1f - heat) * 60f).add(this);
                    Drawm.altarOrb(Tmp.v1.x, Tmp.v1.y, 7.5f, heat);
                }
                else Drawm.altarOrb(x, y, 7.5f, f1);
            }

            Draw.reset();
            Draw.blend();
        }

        public void drawingCircle(float x, float y, float r, float f, float stroke){
            stroke(stroke * f);
            if(f > 0.99f) circle(x, y, r);
            else arc(x, y, r, f, 0f, circleVertices(r) * 2);
        }

        @Override
        public boolean canPickup(){
            return false;
        }

        @Override
        public float handleDamage(float amount){
            return connections <= 0 ? super.handleDamage(amount) : 0f;
        }

        //note: the version must ALSO be increased for r/w changes made on the HardMode class!!
        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
        }

        @Override
        public void write(Writes write){
            super.write(write);
        }
    }

    public class AltarGameMode {
        public TextureRegion icon;
        public String name;
        public Boolp locked;
        public Boolf<AltarBuild> canStart;
        public Cons2<AltarBuild, Integer> start;
        private boolean isBoss = false;
        public UnitType boss;
        protected Prov<CharSequence> label = () -> "[gray]...[]";

        public AltarGameMode(String name, Boolp locked, Boolf<AltarBuild> canStart, Cons2<AltarBuild, Integer> start){
            this.name = name;
            this.locked = locked;
            this.canStart = canStart;
            this.start = start;
        }

        public AltarGameMode(String name){
            this(name, () -> true, b -> false, (b, i) -> {});
        }

        public AltarGameMode(UnitType boss, int rank){
            this("boss" + rank, () -> hardmode.level() <= rank * 10 + 9, b -> false, AltarBuild::startBoss);//todo boolf
            isBoss = true;
            this.boss = boss;
            label = () -> "[gray]Lv" + (rank * 10 + 9) + "[]";
        }

        public AltarGameMode setLabel(Prov<CharSequence> l){
            label = l;
            return this;
        }

        public void load(){
            icon = atlas.find("betamindy-mode-" + name);
        }

        public String local(){
            if(locked.get()) return "[gray]???[]";
            if(isBoss) return Core.bundle.format("altar.mode.boss", boss.localizedName);
            return Core.bundle.get("altar.mode." + name);
        }

        public TextureRegion icon(){
            if(locked.get()) return lockedIcon;
            return icon;
        }
    }
}
