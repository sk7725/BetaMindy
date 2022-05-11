package betamindy.world.blocks.production;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.graphics.Drawm;
import betamindy.world.blocks.payloads.*;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

public class BlockCloner extends Block {
    public Color color = Pal.lancerLaser;
    /** Build speed multiplier */
    public float buildSpeed = 0.8f;
    public int maxSize = 1; //add support for bigger sizes?
    public TextureRegion baseRegion;
    public TextureRegion[] topRegion = new TextureRegion[4];

    public BlockCloner(String name){
        super(name);
        update = true;
        solid = true;
        rotate = true;
        quickRotate = false;
        hasItems = true;
        sync = true;

        consume(new ConsumeItemDynamic((ClonerBuild e) -> e.recipe != null ? e.recipe.requirements : ItemStack.empty));
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

        addBar("progress", (ClonerBuild entity) -> new Bar("bar.progress", Pal.ammo, () -> entity.recipe == null ? 0f : (entity.progress / entity.constructTime())));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        int br = (rotation + 2) % 4;
        Lines.stroke(1f);

        Draw.color(Pal.accent, Mathf.absin(Time.globalTime, 2f, 1f));
        Tile t = world.tile(x, y);
        if(t != null) t = t.nearby(rotation);
        if(t != null && t.build != null && t.block().size > maxSize && t.block().outputsPayload){
            Lines.square(t.build.x, t.build.y, t.block().size * tilesize / 2f);
        }
        else Lines.square((x + Geometry.d4x(rotation)) * tilesize + offset, (y + Geometry.d4y(rotation)) * tilesize + offset, tilesize / 2f);

        Draw.color(color, Mathf.absin(Time.globalTime, 2f, 1f));
        Lines.square((x + Geometry.d4x(br)) * tilesize + offset, (y + Geometry.d4y(br)) * tilesize + offset, tilesize / 2f);
        Draw.color();
    }

    public class ClonerBuild extends Building {
        public float progress, heat, time;
        public @Nullable Block recipe, prev;
        public int recipeRot = 0;
        public Object recipeCon = null;
        public boolean parseFront = false;

        public Tile destTile(){
            return destTile(recipe.size, (rotation + 2) % 4);
        }

        public Tile destTile(int size){
            return destTile(size, (rotation + 2) % 4);
        }

        public Tile destTile(int size, int dir){
            Tile tile = isPayload() ? Vars.world.tileWorld(x, y) : this.tile;
            if(tile == null) return null;
            if(size <= 1) return tile.nearby(dir);
            else if(size % 2 == 1){
                int o = size / 2 + 1;
                return tile.nearby(o * Geometry.d4x(dir), o * Geometry.d4y(dir));
            }
            else{
                int o = size / 2 + 1;
                return tile.nearby(o * Geometry.d4x(dir) + RBuild.evenOffsets[dir][0], o * Geometry.d4y(dir) + RBuild.evenOffsets[dir][1]);
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            parseFront = false;

            Tile t = tile.nearby(rotation);
            if(t == null) recipe = null;
            else{
                if(t.block() == null) recipe = null;
                else if(t.block().size > maxSize && t.block().outputsPayload && t.build != null){
                    parseFront = true;
                }
                else if(!obstructed(t.block()) && t.block().size <= maxSize){
                    recipe = t.block();
                    if(t.build != null){
                        recipeRot = (recipe.rotate) ? t.build.rotation : 0;
                    }
                }
                else recipe = null;
            }
            if(recipe == null){
                recipeRot = 0;
                recipeCon = null;
            }
            //Log.info(recipe == null ? "null" : recipe.name);
        }

        public void peekPayload(){
            Tile t = tile.nearby(rotation);
            if(t == null || t.block() == null) recipe = null;
            else{
                if(t.build != null && t.build.getPayload() != null && (t.build.getPayload() instanceof BuildPayload p)){
                    if(!obstructed(p.block())){
                        recipe = p.block();
                        recipeRot = (recipe.rotate) ? t.build.rotation : 0; //default to the carrier's rotation
                        recipeCon = p.build.config();
                    }
                    else{
                        recipe = null;
                    }
                }
                else{
                    recipe = null;
                }
            }
            if(recipe == null){
                recipeRot = 0;
                recipeCon = null;
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(parseFront) peekPayload();
            boolean produce = recipe != null && canConsume();
            if(produce){
                progress += edelta();
                if(progress >= constructTime()){
                    placeBlock(destTile());
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
            Tile t = destTile(b.size);
            if(t == null) return true;
            return !Build.validPlace(b, team, t.x, t.y, recipeRot, true);
        }

        public boolean placeBlock(@Nullable Tile t){
            if(obstructed(recipe) || t == null) return false;
            consume();
            t.setBlock(recipe, team, recipeRot);
            if(t.build != null && !Vars.net.client()){
                if(parseFront){
                    if(recipeCon != null) t.build.configureAny(recipeCon);
                }
                else{
                    recipeCon = tile.nearbyBuild(rotation).config();
                    if(recipeCon != null) t.build.configureAny(recipeCon);
                    recipeCon = null;
                }
            }
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

                int dir = (rotation + 2) % 4;
                float dx = x + (Geometry.d4x(dir) * (1 + (recipe.size >> 1))) * tilesize;
                float dy = y + (Geometry.d4y(dir) * (1 + (recipe.size >> 1))) * tilesize;
                if(recipe.size % 2 == 0){
                    dx += RBuild.evenOffsets[dir][0] * tilesize + 4f;
                    dy += RBuild.evenOffsets[dir][1] * tilesize + 4f;
                }
                Draw.rect(recipe.fullIcon, dx, dy, recipeRot * 90f);
                Draw.blend();
                Draw.reset();

                if(heat > 0.001f){
                    float finalDx = dx;
                    float finalDy = dy;
                    Draw.draw(Layer.blockOver, () -> {
                        Drawm.constructLineless(finalDx, finalDy, recipe.fullIcon, recipeRot * 90f, progress / constructTime(), heat, time, color);
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
            if(parseFront){
                Tile t = tile.nearby(rotation);
                if(t != null && t.build != null){
                    Lines.square(t.build.x, t.build.y, t.block().size * tilesize / 2f);
                }
            }
            else Lines.square(x + Geometry.d4x(rotation) * tilesize, y + Geometry.d4y(rotation) * tilesize, tilesize / 2f);

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
