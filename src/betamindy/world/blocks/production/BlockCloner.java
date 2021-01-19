package betamindy.world.blocks.production;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.graphics.Drawm;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.consumers.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class BlockCloner extends Block {
    public Color color = Pal.lancerLaser;
    /** Build speed multiplier */
    public float buildSpeed = 0.8f;
    public int maxSize = 1;//TODO: add support for bigger sizes?
    public TextureRegion baseRegion;
    public TextureRegion[] topRegion = new TextureRegion[4];

    public BlockCloner(String name){
        super(name);
        update = true;
        solid = true;
        rotate = true;
        quickRotate = false;
        hasItems = true;

        consumes.add(new ConsumeItemDynamic((ClonerBuild e) -> e.recipe != null ? e.recipe.requirements : ItemStack.empty));
    }

    @Override
    public void load() {
        super.load();
        baseRegion = atlas.find(name + "-base");
        for(int i = 0; i < 4; i++){
            topRegion[i] = atlas.find(name + "-" + i);
        }
    }

    @Override
    public void setBars() {
        super.setBars();

        bars.add("progress", (ClonerBuild entity) -> new Bar("bar.progress", Pal.ammo, () -> entity.recipe == null ? 0f : (entity.progress / entity.constructTime())));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        int br = (rotation + 2) % 4;
        Lines.stroke(1f);
        Draw.color(Pal.accent, Mathf.absin(Time.globalTime, 2f, 1f));
        Lines.square((x + Geometry.d4x(rotation)) * tilesize + offset, (y + Geometry.d4y(rotation)) * tilesize + offset, tilesize / 2f);
        Draw.color(color, Mathf.absin(Time.globalTime, 2f, 1f));
        Lines.square((x + Geometry.d4x(br)) * tilesize + offset, (y + Geometry.d4y(br)) * tilesize + offset, tilesize / 2f);
        Draw.color();
    }

    public class ClonerBuild extends Building {
        public float progress, heat, time;
        public @Nullable Block recipe, prev;
        public int recipeRot = 0;

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            Tile t = tile.nearby(rotation);
            if(t == null) recipe = null;
            else{
                if(t.block() == null) recipe = null;
                if(!obstructed(t.block()) && t.block().size <= maxSize){
                    recipe = t.block();
                    if(recipe.rotate && t.build != null) recipeRot = t.build.rotation;
                    else recipeRot = 0;
                }
                else recipe = null;
            }
            if(recipe == null){
                recipeRot = 0;
            }
            //Log.info(recipe == null ? "null" : recipe.name);
        }

        @Override
        public void update() {
            super.update();
            boolean produce = recipe != null && consValid();
            if(produce){
                progress += edelta();
                if(progress >= constructTime()){
                    if(placeBlock(tile.nearby((rotation + 2) % 4))) consume();
                    progress = 0f;
                }
            }

            if(recipe == null) progress = 0f;
            else if(recipe != prev){
                prev = recipe;
                progress = 0f;
            }

            heat = Mathf.lerpDelta(heat, Mathf.num(progress > 0.001f), 0.3f);
            time += edelta();
        }

        public float constructTime(){
            return (recipe == null) ? 8f * buildSpeed : block.buildCost * buildSpeed;
        }

        public boolean obstructed(Block b){
            Tile t = tile.nearby((rotation + 2) % 4);
            if(t == null) return true;
            return !Build.validPlace(b, team, t.x, t.y, recipeRot, true);
        }

        public boolean placeBlock(@Nullable Tile t){
            if(obstructed(recipe) || t == null) return false;
            Vars.world.tile(t.x, t.y).setBlock(recipe, team, recipeRot);
            return true;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public int getMaximumAccepted(Item item){
            if(recipe == null) return 0;
            for(ItemStack stack : recipe.requirements){
                if(stack.item == item) return stack.amount * 2;
            }
            return 0;
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);
            Draw.rect(topRegion[rotation], x, y);
            if(recipe != null){
                Draw.z(Layer.blockOver);
                Draw.blend(Blending.additive);
                Draw.color(color, Mathf.absin(2f, 1f));
                float dx = x + Geometry.d4x((rotation + 2) % 4) * tilesize;
                float dy = y + Geometry.d4y((rotation + 2) % 4) * tilesize;
                Draw.rect(recipe.icon(Cicon.full), dx, dy, recipeRot * 90f);
                Draw.blend();
                Draw.reset();

                if(heat > 0.001f){
                    Draw.draw(Layer.blockOver, () -> {
                        Drawm.constructLineless(dx, dy, recipe.icon(Cicon.full), recipeRot * 90f, progress / constructTime(), heat, time, color);
                    });
                }
            }
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            int br = (rotation + 2) % 4;
            Lines.stroke(1f);
            Draw.color(Pal.accent, Mathf.absin(Time.time, 2f, 1f));
            Lines.square(x + Geometry.d4x(rotation) * tilesize, y + Geometry.d4y(rotation) * tilesize, tilesize / 2f);
            Draw.color(color, Mathf.absin(Time.time, 2f, 1f));
            Lines.square(x + Geometry.d4x(br) * tilesize, y + Geometry.d4y(br) * tilesize, tilesize / 2f);
            Draw.color();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
        }

        @Override
        public Object senseObject(LAccess sensor) {
            switch(sensor){
                case config: return recipe;
                default: return super.senseObject(sensor);
            }
        }
    }
}
