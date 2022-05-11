package betamindy.world.blocks.storage;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

public class Box extends Block {
    public int sprites = 7;
    public TextureRegion[] topRegions;
    public TextureRegion boxRegion, baseRegion, boxTopRegion, bottle, bottleTop;

    //public Effect destroyEffect = MindyFx.openBox;
    public Effect despawnEffect = MindyFx.despawnBox;
    public Sound openSound = MindySounds.boxOpen;
    public Box(String name){
        super(name);
        solid = true;
        update = true;
        canOverdrive = false;
        drawDisabled = false;
        hasItems = true;
        hasLiquids = true;
        outputsLiquid = true;
        itemCapacity = 50; //this should not matter
        liquidCapacity = 200; //neither should this
        //alwaysUnlocked = true;
        unloadable = true;
        rebuildable = false;
        destroySound = breakSound = MindySounds.boxOpen;
        destroyEffect = breakEffect = MindyFx.openBox;
        placeablePlayer = false;
        instantDeconstruct = true;
    }

    @Override
    public void load(){
        super.load();
        baseRegion = atlas.find(name + "-base", "betamindy-box-base");
        boxRegion = atlas.find(name + "-box", "betamindy-box-box"); //shut
        boxTopRegion = atlas.find(name + "-boxtop", "betamindy-box-boxtop");
        topRegions = new TextureRegion[sprites];
        for(int i = 0; i < sprites; i++){
            topRegions[i] = atlas.find(name + i, name);
        }
        bottle = atlas.find("betamindy-bottle");
        bottleTop = atlas.find("betamindy-bottle-top");
    }

    public class BoxBuild extends Building{
        public boolean open = false;
        public boolean hadLiquid = false;

        public void openBox(){
            if(open) return;
            open = true;
            //openEffect.at(x, y, (sprite() % 2) * 80f + Mathf.random(10f));
            openSound.at(x, y, Mathf.random(0.8f, 1.2f));
        }

        public int sprite(){
            return Mathf.randomSeed(id, 0, sprites - 1);
        }

        @Override
        public void updateTile(){
            if(items.any() && timer(timerDump, dumpTime)){
                if(dump()) openBox();
            }
            if(liquids.currentAmount() > 0.01f){
                hadLiquid = true;
                dumpLiquid(liquids.current());
            }

            if((open || hadLiquid) && items.empty() && liquids.currentAmount() <= 0.1f){
                //despawn
                despawnEffect.at(x, y, 0f, topRegions[sprite()]);
                tile.remove();
                remove();
            }
        }

        public void drawOpen(){
            Draw.z(Layer.block + 0.01f);
            Draw.rect(baseRegion, x, y);
            if(items.any()){
                TextureRegion icon = items.first().fullIcon;
                for(int i = 0; i < items.total() * 5 / itemCapacity; i++){
                    Draw.rect(icon, x + Mathf.randomSeed(id + i) * 3f * size - 1.5f, y + Mathf.randomSeed(id + i + 10) * 3f * size - 1.5f, 4f, 4f);
                }
            }
            if(liquids.currentAmount() > 0.001f || hadLiquid){
                int p = Mathf.randomSeed(id, 0, 3);
                Tmp.v1.trns(45f + 90f * p, 2.121f * size).add(this);
                if(liquids.currentAmount() > 0.01f){
                    Draw.color(Color.white, liquids.current().color, Mathf.clamp(liquids.currentAmount() / liquidCapacity));
                }
                Draw.rect(bottle, Tmp.v1.x, Tmp.v1.y);
                Draw.color();
                Draw.rect(bottleTop, Tmp.v1.x, Tmp.v1.y);
            }
            Draw.rect(boxRegion, x, y, (sprite() % 2) * 90f);
            Draw.z(Layer.blockOver);
            Draw.rect(boxTopRegion, x, y);
            Draw.reset();
        }

        @Override
        public void draw(){
            if(!Core.scene.hasMouse()){
                Building b = world.buildWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);
                if(b != null && b.tile == tile){
                    drawOpen();
                    return;
                }
            }
            Draw.rect(topRegions[sprite()], x, y);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return false;//note to pythonguy: force items in this block at the shop
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return 0;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return false;
        }

        @Override
        public void itemTaken(Item item){
            super.itemTaken(item);
            openBox();
        }

        @Override
        public int removeStack(Item item, int amount){
            int a = super.removeStack(item, amount);
            if(a > 0) openBox();
            return a;
        }

        @Override
        public void transferLiquid(Building next, float amount, Liquid liquid){
            float flow = Math.min(next.block.liquidCapacity - next.liquids.get(liquid), amount);

            if(next.acceptLiquid(self(), liquid)){
                next.handleLiquid(self(), liquid, flow);
                liquids.remove(liquid, flow);
                openBox();
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            open = read.bool();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(open);
        }

        @Override
        public void onDestroyed(){
            destroyEffect.at(x, y);
        }
    }
}
