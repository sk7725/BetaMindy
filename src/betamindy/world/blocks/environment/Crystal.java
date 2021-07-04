package betamindy.world.blocks.environment;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static mindustry.type.ItemStack.with;

//todo does not refund items normally, naturally occurring ones give large amounts of resources when destroyed by mynamites
public class Crystal extends Block {
    public Item item;
    public int sprites = 1;

    public Effect updateEffect = MindyFx.sparkle;
    public float effectChance = 0.001f;
    public TextureRegion[] regions;
    public TextureRegion[] shineRegion;
    public Crystal(String name, Item item){
        super(name);
        update = true;
        solid = true;
        rotate = false;
        //hasShadow = false;
        drawDisabled = false;
        enableDrawStatus = false;
        this.item = item;
        requirements(Category.effect, with(item, 30f));
    }

    @Override
    public void load(){
        super.load();
        regions = new TextureRegion[sprites];
        shineRegion = new TextureRegion[sprites];
        for(int i = 0; i < sprites; i++){
            regions[i] = atlas.find(name + i, name);
            shineRegion[i] = atlas.find(name + "-shine" + i, "betamindy-crystal-shine");
        }
    }

    public class CrystalBuild extends Building {
        @Override
        public void updateTile(){
            if(Mathf.chanceDelta(effectChance)) updateEffect.at(x, y, item.color);
        }

        public void beforeDraw(){
            Draw.z(Layer.blockOver);
            //Draw.alpha(0.9f);
        }

        public void afterDraw(){}

        @Override
        public void draw(){
            float ox = x + Mathf.randomSeedRange(id, 0.5f);
            float oy = y + Mathf.randomSeedRange(id + 628, 0.5f);
            float r = Mathf.randomSeedRange(id + 420, 15f);
            int sprite = Mathf.randomSeed(id, 0, sprites - 1);

            Draw.z(Layer.blockOver - 0.1f);
            Draw.blend(Blending.additive);
            Draw.color(item.color, 0.5f);
            Draw.rect("circle-shadow", ox, oy, 17f, 17f);
            Draw.blend();
            Draw.color();

            beforeDraw();

            Draw.rect(regions[sprite], ox, oy, 10f, 10f, r);
            Draw.color();
            if(Vars.renderer.drawStatus) afterDraw();

            if(Vars.renderer.bloom == null) return;
            Draw.z(Layer.bullet - 0.01f);
            Draw.color(item.color);
            Draw.rect(shineRegion[sprite], ox, oy, 10f, 10f, r);
            Draw.color();
        }

        @Override
        public void drawLight(){
            super.drawLight();
            Drawf.light(x, y, 25f, Tmp.c1.set(item.color).mul(0.7f), 0.25f);
        }
    }
}
