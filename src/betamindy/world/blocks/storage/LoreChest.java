package betamindy.world.blocks.storage;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.campaign.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

public class LoreChest extends Chest{
    public Color chainColor = Pal2.esoterum;
    public TextureRegion chain, chainEnd;

    public Effect chainBreakEffect = MindyFx.chainShatter; //played at all chain segments
    public Effect captureEffect = MindyFx.tarnationShoot; //todo
    public Effect chainDestroyEffect = MindyFx.sniperShoot; //played at the connected block only
    public Sound captureSound = MindySounds.easterEgg2;
    public float chainScale = 1f;

    public LoreChest(String name){
        super(name);
        canStore = false;
        slots = 15;
        capacity = 99;
        targetable = false;

        config(Integer.class, (LoreChestBuild entity, Integer value) -> {
            if(!state.isEditor()) return;
            entity.captured = false;
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

    @Override
    public void load(){
        super.load();
        chain = atlas.find(name + "-laser", "betamindy-clear-chain");
        chainEnd = atlas.find(name + "-laser-end", "betamindy-clear-chain-end");
    }

    public boolean linkValid(Building tile, Building link){
        return tile != link && link != null;
    }

    public class LoreChestBuild extends ChestBuild {
        public IntSeq links = new IntSeq();
        public boolean captured = false;
        public float heat = 1f;

        public void chainBreak(float x, float y, float x2, float y2){
            if(headless) return;
            chainDestroyEffect.at(x, y, chainColor);
            float rot = Mathf.angle(x2 - x, y2 - y);
            float len = Mathf.len(x2 - x, y2 - y);
            float c = (chain.width - 0.01f) * chainScale * Draw.scl;
            int n = (int)(len / c);
            if(n > 1){
                for(int i = 0; i < n; i++){
                    Tmp.v1.trns(rot, c * (i + 0.5f)).add(x, y);
                    chainBreakEffect.at(Tmp.v1.x, Tmp.v1.y, rot, chainColor, chain);
                }
            }
        }

        public void capture(){
            captureEffect.at(x, y, chainColor);
            captureSound.at(this);
        }

        @Override
        public void updateTile(){
            heat = Mathf.lerpDelta(heat, captured ? 0f : 1f, 0.04f);
            if(captured) return;

            int broke = -1;
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(b == null || b.dead || b.team != team){
                    Tile t = world.tile(links.get(i));
                    if(t != null) chainBreak(t.worldx(), t.worldy(), x, y);
                    broke = i;
                    break;
                }
            }
            if(broke != -1) links.removeIndex(broke);
            if(links.size <= 0 && !state.isEditor()){
                captured = true;
                capture();
                changeTeam(state.rules.defaultTeam);
            }
        }

        @Override
        public void draw(){
            super.draw();

            Draw.z(Layer.power);
            Draw.blend(Blending.additive);
            Draw.color(chainColor);
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(b == null || !linkValid(this, b) || links.get(i) != b.pos()) continue;
                Drawm.chain(null, chain, chainEnd, b.x, b.y, x, y, chainScale, true);
            }
            Draw.blend();
            Draw.reset();
        }

        @Override
        public boolean shouldShowChest(){
            return super.shouldShowChest() || state.isEditor();
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
            super.drawConfigure();
            if(!state.isEditor()) return;
            squares(this, Pal2.coin);

            for(int i = 0; i < links.size; i++){
                Building link = world.build(links.get(i));

                if(link != this && linkValid(this, link)){
                    boolean linked = links.indexOf(link.pos()) >= 0;
                    if(linked){
                        squares(link, chainColor);
                    }
                }
            }

            Draw.reset();
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
        public void write(Writes write){
            super.write(write);
            write.bool(captured);
            write.s(links.size);
            for(int i = 0; i < links.size; i++){
                write.i(links.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            captured = read.bool();
            links.clear();
            short amount = read.s();
            for(int i = 0; i < amount; i++){
                links.add(read.i());
            }
        }

        @Override
        public float handleDamage(float amount){
            return captured ? super.handleDamage(amount) : 0f;
        }

        @Override
        public void damage(float damage){
            if(captured) super.damage(damage);
        }

        @Override
        public boolean canPickup(){
            return captured;
        }
    }
}
