package betamindy.world.blocks.environment;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static mindustry.type.ItemStack.with;

public class Crystal extends Block {
    public Item item;
    public int sprites = 1;

    public StatusEffect status = StatusEffects.none;

    public Effect updateEffect = MindyFx.sparkle;
    public float effectChance = 0.001f;
    public TextureRegion[] regions;
    public TextureRegion[] shineRegion;
    public float sizeScl = 10f;
    public float glowOpacity = 1f;

    public Crystal(String name, Item item, int amount){
        super(name);
        update = true;
        solid = true;
        rotate = false;
        drawDisabled = false;
        enableDrawStatus = false;
        this.item = item;
        requirements(Category.effect, with(item, amount));

        destroySound = breakSound = MindySounds.shatter;
        destroyEffect = breakEffect = MindyFx.crystalBreak;
        instantDeconstruct = true;
        deconstructThreshold = 1f; //deconstructing it is a crime
        rebuildable = false;
        hasColor = true;
        mapColor = item.color; //"do not set manually" h
    }

    public Crystal(String name, Item item){
        this(name, item, 20);
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

    @Override
    public int minimapColor(Tile tile){
        return item.color.rgba();
    }

    public class CrystalBuild extends Building {
        @Override
        public void updateTile(){
            if(Mathf.chanceDelta(effectChance)) updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f), item.color);
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
            Draw.color(item.color, 0.2f);
            Draw.rect("circle-shadow", ox, oy, sizeScl * 1.7f, sizeScl * 1.7f);
            Draw.blend();
            Draw.color();

            beforeDraw();

            Draw.rect(regions[sprite], ox, oy, sizeScl, sizeScl, r);
            Draw.color();
            if(Vars.renderer.drawStatus) afterDraw();

            if(Vars.renderer.bloom == null) return;
            Draw.z(Layer.bullet - 0.01f);
            Draw.color(item.color, glowOpacity);
            Draw.rect(shineRegion[sprite], ox, oy, sizeScl, sizeScl, r);
            Draw.color();
        }

        @Override
        public void drawLight(){
            super.drawLight();
            Drawf.light(x, y, 30f, Tmp.c1.set(item.color).mul(0.7f), 0.25f);
            Drawf.light(x, y, 10f, Tmp.c1.set(item.color).mul(0.7f), 0.95f);
        }

        @Override
        public void drawTeam(){
            if(team == Team.derelict) return;
            super.drawTeam();
        }

        @Override
        public void onDestroyed(){
            destroyEffect.at(x, y, item.color);
            Damage.status(null, x, y, 30f, status, 300f, true, true);
        }
    }
}
