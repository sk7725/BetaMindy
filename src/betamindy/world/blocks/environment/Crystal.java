package betamindy.world.blocks.environment;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import betamindy.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static mindustry.type.ItemStack.with;

//looks pretty TODO
//does not refund items normally, naturally occurring ones give large amounts of resources when destroyed by mynamites
public class Crystal extends Block {
    public Item item;

    public Effect updateEffect = MindyFx.powerDust;//TODO port cb sparkle
    public float effectChance = 0.01f;
    public TextureRegion shineRegion; //TODO unbloomshineregion, random sprite variants etc.
    public Crystal(String name, Item item){
        super(name);
        update = true;
        solid = true;
        rotate = false;
        //hasShadow = false;
        this.item = item;
        requirements(Category.effect, with(item, 15f));
    }

    @Override
    public void load(){
        super.load();
        shineRegion = atlas.find(name + "-shine", "betamindy-crystal-shine");
    }

    public class CrystalBuild extends Building {
        @Override
        public void updateTile(){
            if(Mathf.chanceDelta(effectChance)) updateEffect.at(this);
        }

        public void beforeDraw(){
            Draw.z(Layer.blockOver);
            //Draw.alpha(0.9f);
        }

        @Override
        public void draw(){
            //TODO placeholder code
            float ox = x + Mathf.randomSeedRange(id, 0.5f);
            float oy = y + Mathf.randomSeedRange(id + 628, 0.5f);
            float r = Mathf.randomSeedRange(id + 420, 15f);
            beforeDraw();
            Draw.rect(region, ox, oy, 10f, 10f, r);
            Draw.color();
            Draw.z(Layer.bullet - 0.009f);
            Draw.rect(shineRegion, ox, oy, 10f, 10f, r);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            Drawf.light(x, y, 15f, item.color, 0.25f);
        }
    }
}
