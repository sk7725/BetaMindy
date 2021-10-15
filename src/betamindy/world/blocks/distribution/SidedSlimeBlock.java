package betamindy.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.geom.Geometry;
import mindustry.graphics.*;
import mindustry.type.Category;
import mindustry.world.meta.BlockGroup;

//import java.util.regex.*;

import static arc.Core.atlas;

public class SidedSlimeBlock extends SlimeBlock {
    public TextureRegion[] baseRegion = new TextureRegion[4], slabRegion = new TextureRegion[4];
    public TextureRegion coreRegion;
    //public static final Pattern pattern = Pattern.compile(".*(?=-[^-]+)");

    public SidedSlimeBlock(String name, int stype){
        super(name, stype);

        rotate = true;
        group = BlockGroup.transportation;
    }

    @Override
    public void load(){
        super.load();
        baseRegion[0] = region;
        for(int i = 1; i<4; i++){
            baseRegion[i] = atlas.find(name + "-" + i);
        }
        for(int i = 0; i<4; i++){
            slabRegion[i] = atlas.find(name + "-slab-" + i, "betamindy-slab-" + i);
        }

        /*Matcher matcher = pattern.matcher(name);
        if(matcher.find()){
            coreRegion = atlas.find(name + "-core", matcher.group(1) + "-core");
        }
        else coreRegion = atlas.find(name + "-core");
        */
        coreRegion = atlas.find(name + "-core");
    }

    public class SidedSlimeBuild extends SlimeBuild{
        @Override
        public void draw(){
            if(Core.settings.getBool("animatedshields") && Core.settings.getBool("slimeeffect")){
                Draw.rect(coreRegion, x, y);
                Draw.rect(slabRegion[rotation], x, y);
                Draw.z(Layer.shields + 0.0001f);
                Draw.color(color);
                Fill.rect(x + Geometry.d4x[rotation] * 2f, y + Geometry.d4y[rotation] * 2f, rotation % 2 == 0 ? 4f : 8f, rotation % 2 == 0 ? 8f : 4f);
                Draw.reset();
            }
            else Draw.rect(baseRegion[rotation], x, y);
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            if(Core.settings.getBool("animatedshields") && Core.settings.getBool("slimeeffect")){
                Draw.rect(coreRegion, x, y, dr);
                Draw.rect(slabRegion[rotation], x, y, dr);
                Draw.z(Layer.shields + 0.0001f);
                Draw.color(color);
                Fill.rect(x + Geometry.d4x[rotation] * 2f, y + Geometry.d4y[rotation] * 2f, rotation % 2 == 0 ? 4f : 8f, rotation % 2 == 0 ? 8f : 4f, dr);
                Draw.reset();
            }
            else Draw.rect(baseRegion[rotation], x, y, dr);
        }
    }
}