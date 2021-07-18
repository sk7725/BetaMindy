package betamindy.world.blocks.campaign;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.util.*;
import betamindy.world.blocks.defense.*;
import betamindy.world.blocks.defense.Campfire.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static arc.graphics.g2d.Lines.*;
import static betamindy.BetaMindy.hardmode;
import static mindustry.Vars.spawner;
import static mindustry.Vars.tilesize;

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

    public Altar(String name){
        super(name);

        update = true;
        solid = false;
        rotate = false;
        size = 3;
        expanded = true;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 3; i++){
            heatRegions[i] = atlas.find(name + "-heat" + i);
        }
    }

    @Override
    public boolean canBreak(Tile tile){
        return super.canBreak(tile) && ((AltarBuild)tile.build).connections <= 0;
    }

    public class AltarBuild extends Building{
        public int phase = 0;
        public int amount;

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
            switch(phase){
                default: phase0();
                break;
                case 1: phase1();
            }
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
                initTorch();
            }
        }

        public void initTorch(){
            int rank = hardmode.level() / HardMode.rankLevel;
            amount = rank >= 7 ? 12800 : 100 << rank;
            amount /= 4;
            heat = 0f;
            for(int i = 0; i < torches; i++){
                sacrifice[i] = FireColor.items.get(Mathf.random(0, Math.min(FireColor.items.size - 1, 6 + rank * 2)));
                made[i] = 0;
                Tile torch = torch(i);
                if(torch != null){
                    Fx.healWaveMend.at(torch.drawx(), torch.drawy(), 15f, FireColor.from(sacrifice[i]));
                }
            }
            MindySounds.easterEgg2.at(this, 0.85f);
        }

        public void consumeTorch(Tile torch, int i){
            if(made[i] >= amount || torch.build == null || torch.build.items.empty()) return;
            if(torch.build.items.first() != sacrifice[i]) return;
            int a = Math.min(torch.build.items.get(sacrifice[i]), amount - made[i] + 3);
            if(a > 3){ //leave some for vanity
                torch.build.items.remove(sacrifice[i], a - 3);
                made[i] += a-3;
            }
        }

        public void phase1(){
            heatTorchSum = 1f;
            connections = 0;
            heat = 0f;
            for(int i = 0; i < torches; i++){
                Tile t = torch(i);
                if(t != null){
                    connections++;
                    consumeTorch(t, i);
                    heat += made[i] / (float)amount;
                }
                else{
                    phase = 0;
                    heat = 1f;
                    return;
                }
            }
            heat /= (float) torches;

            if(lastSpawnDir < 0f){
                Tile t = spawner.getFirstSpawn();
                if(t == null) lastSpawnDir = Mathf.random(360f);
                else lastSpawnDir = angleTo(t.worldx(), t.worldy());
            }
            if(Mathf.chance(0.3f)){
                Tmp.v1.trns(Mathf.random(360f), size * tilesize / 1.414f).clamp(-size * tilesize /2f, -size * tilesize / 2f, size * tilesize /2f, size * tilesize / 2f);
                updateEffect.at(Tmp.v1.x + x, Tmp.v1.y + y, lastSpawnDir);
            }
            if(Mathf.chance(0.2f)){
                Tmp.v1.trns(Mathf.random(360f), 36f + Mathf.random() * 6f);
                updateEffect2.at(Tmp.v1.x + x, Tmp.v1.y + y, lastSpawnDir);
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
                        if(phase >= 1){
                            Draw.z(Layer.effect - 0.001f);
                            Draw.color(c1, c2, Mathf.absin(Time.time + i * 20f, 11f, 1f));
                            Draw.alpha(made[i] / (float)amount);
                            Draw.rect(camp.torchHeatRegion, torch.drawx(), torch.drawy());
                            FireColor.fset(sacrifice[i], Mathf.absin(15f, 1f));
                            float r = Mathf.sin(17f, 2f);
                            Drawm.spark(torch.drawx(), torch.drawy(), 6f - Math.abs(r), 2f, r * 15f);
                        }
                    }
                }
            }

            Draw.z(Layer.blockUnder);
            Draw.blend(Blending.additive);
            float f0 = phase == 0 ? heat : 1f;

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
                square(Tmp.v1.x, Tmp.v1.y, 12, Mathf.sin(Time.time / 10 - i)*69 + Mathf.cos(Time.time / 20 + i)*42);
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

                if(fin > 0.1f){
                    int n = 30;
                    int m = lore[rank].length();
                    int off = (int)(Time.time / 3f) % (n * m);
                    for(int i = off; i < n + off; i++){
                        final Color c = i - off < n / 2 ? c1 : c2;
                        if((phase == 1 && Mathf.randomSeed(i + id + m) < heat) || phase == 2){
                            Draw.z(Layer.effect);
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
                Draw.z(Layer.effect);
                for(int j = 0; j < 3; j++){
                    float f1 = phase == 1 ? Mathf.clamp(heat * 3f - j) : 1f;
                    Draw.color(c1, c2, Mathf.absin(Time.time + j * 15f, 11f, 1f));
                    Draw.alpha(f1);
                    Draw.rect(heatRegions[j], x, y);
                }
            }

            Draw.reset();
            Draw.blend();
        }

        public void drawingCircle(float x, float y, float r, float f, float stroke){
            stroke(stroke * f);
            if(f > 0.99f) circle(x, y, r);
            else polySeg(circleVertices(r) * 2, 0, (int)(circleVertices(r) * 2 * f), x, y, r, 0f);
        }

        @Override
        public boolean canPickup(){
            return false;
        }

        @Override
        public float handleDamage(float amount){
            return connections <= 0 ? super.handleDamage(amount) : 0f;
        }
    }
}
