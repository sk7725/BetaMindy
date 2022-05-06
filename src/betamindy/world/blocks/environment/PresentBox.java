package betamindy.world.blocks.environment;

//import arc.Core;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;

public class PresentBox extends Block {
    //How many colors do you want?
    //yes.
    public static final Color[] colors = {Team.sharded.color, Team.crux.color, Team.blue.color, Team.green.color, Team.malis.color, thorium.color, titanium.color, plastanium.color, pyratite.color, cryofluid.color, slag.color, blastCompound.color, Pal.place, Pal.engine, surgeAlloy.color, Pal.lancerLaser, Pal.sapBullet, Pal.heal};
    public int itemCount = 15, naughtyItemCount = 3;
    public float naughtyChance = 0.05f;
    public Item naughtyItem = coal;
    public TextureRegion baseRegion, boxRegion, ribbonRegion, ribbonRegionBack, ribbonRegionBase, topRegion;
    public Effect openEffect = Fx.none, naughtyEffect = Fx.explosion;
    public Sound openSound = MindySounds.presentBells, naughtySound = Sounds.bang;

    //private final CustomBlockInventoryFragment invFrag = new CustomBlockInventoryFragment();

    public PresentBox(String name){
        super(name);

        solid = true;
        update = true;
        configurable = true;
        saveConfig = false;
        drawDisabled = false;
        hasItems = true;
        itemCapacity = itemCount;
        alwaysUnlocked = true;

        config(Item.class, (PresentBuild entity, Item item) -> {
            entity.open = true;
            boolean naughty = item == naughtyItem;
            entity.items.add(item, naughty ? naughtyItemCount : itemCount);
            if(!headless){
                if(naughty){
                    naughtySound.at(entity);
                    naughtyEffect.at(entity);
                    Effect.shake(size, 3f, entity);
                }
                else{
                    openSound.at(entity);
                    openEffect.at(entity);
                }
            }
        });
    }

    @Override
    public void load(){
        super.load();
        baseRegion = atlas.find(name + "-base", "betamindy-present-base");
        boxRegion = atlas.find(name + "-box", "betamindy-present-box");
        topRegion = atlas.find(name + "-top");
        ribbonRegion = atlas.find(name + "-ribbon", "betamindy-present-ribbon");
        ribbonRegionBack = atlas.find(name + "-ribbon-back", "betamindy-present-ribbon-back");
        ribbonRegionBase = atlas.find(name + "-ribbon-base");
    }

    @Override
    public boolean isHidden(){
        return super.isHidden() || !(Useful.jolly() || state.rules.infiniteResources);
    }

    public class PresentBuild extends Building {
        public Color color1, color2;
        public boolean open;

        private float heat = 0;
        //protected int itemHas = 0;

        @Override
        public Building create(Block block, Team team){
            Building result = super.create(block, team);
            ((PresentBuild)result).color1 = (team != null && team != Team.derelict && Mathf.chance(0.5f)) ? team.color : colors[Mathf.random(colors.length - 1)];
            ((PresentBuild)result).color2 = colors[Mathf.random(colors.length - 1)];
            return result;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(open) heat = Mathf.lerpDelta(heat, 1f, 0.05f);
            if(open && timer(timerDump, dumpTime)) dump();

            /*if(invFrag.visible && timer.get(1, 6)){
                itemHas = 0;
                items.each((item, amount) -> itemHas++);
            }*/
        }

        @Override
        public void deselect(){
            //if(!headless) invFrag.hide();
            super.deselect();
        }

        @Override
        public void draw(){
            if(open){
                Draw.rect(baseRegion, x, y);
                if(items.any()){
                    TextureRegion icon = items.first().fullIcon;
                    for(int i = 0; i < items.total() * 5 / itemCapacity; i++){
                        Draw.rect(icon, x + Mathf.randomSeed(id + i) * 3f - 1.5f, y + Mathf.randomSeed(id + i + 10) * 3f - 1.5f, Draw.scl * Draw.xscl * 16f, Draw.scl * Draw.yscl * 15f);
                    }
                }
                Draw.color(color1);
                Draw.rect(boxRegion, x, y);
                Draw.reset();

                if(heat < 0.99f){
                    drawLid(x + heat * 15f * Mathf.sign(id % 2 == 0), y + heat * ( 0.7f - heat) * 26f, heat * 360f * Mathf.randomSeed(id), 1 - heat, Layer.turret + 1f);
                }
            }
            else{
                drawLid(x, y, 0f, 1f, Layer.block);
            }
        }

        public void drawLid(float x, float y, float rotation, float alpha, float layer){
            Draw.z(layer);
            Draw.color(color1, alpha);
            Draw.rect(topRegion, x, y, rotation);
            Draw.color(color2, alpha);
            Draw.rect(ribbonRegionBase, x, y, rotation);
            float r = Mathf.randomSeed(id) * 360f;
            Draw.rect(ribbonRegionBack, x, y, r + 20f + rotation);
            Draw.mixcol(Pal.shadow, 1f);
            Draw.alpha(0.21f * alpha);
            Draw.rect(ribbonRegion, x, y - 0.5f, r + rotation);
            Draw.mixcol();
            Draw.alpha(alpha);
            Draw.rect(ribbonRegion, x, y + 0.3f, r + rotation);
            Draw.reset();
        }

        @Override
        public boolean configTapped(){
            if(open){
                //Vars.control.input.frag.inv.showFor(this);
                return false;
            }
            Item item = (player.name.toLowerCase().startsWith("glen") || Mathf.chance(naughtyChance)) ? naughtyItem : content.items().random(naughtyItem);
            configure(item);
            return false;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            open = read.bool();
            if(open) heat = 1f;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(open);
        }

        @Override
        public void buildConfiguration(Table table){
            //invFrag.build(table.parent);
            /*if(invFrag.isShown()){
                invFrag.hide();
                control.input.frag.config.hideConfig();
                return;
            }*/
            //invFrag.showFor(this);
        }

        @Override
        public void updateTableAlign(Table table){
            /*
            float pos = Core.input.mouseScreen(x, y - size * 4 - 1).y;
            Vec2 relative = Core.input.mouseScreen(x, y + size * 4);

            table.setPosition(relative.x, Math.min(pos, (float)(relative.y - Math.ceil((float)itemHas / 3f) * 48f - 4f)), Align.top);*/

            super.updateTableAlign(table);
            //if(!invFrag.isShown() && control.input.frag.config.getSelectedTile() == this && items.any()) invFrag.showFor(this);
        }

        @Override
        public void drawConfigure(){
        }
    }

    /*
    //credits to younggam
    class CustomBlockInventoryFragment extends BlockInventoryFragment {
        private boolean built = false;
        private boolean visible = false;

        public boolean isBuilt(){
            return built;
        }

        public boolean isShown(){
            return visible;
        }

        @Override
        public void showFor(Building t){
            visible = true;
            super.showFor(t);
        }

        @Override
        public void hide(){
            visible = false;
            super.hide();
        }

        @Override
        public void build(Group parent){
            built = true;
            super.build(parent);
        }
    }*/
}
