package betamindy.world.blocks.distribution;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.type.Category;
import mindustry.world.*;
import mindustry.gen.*;

import static arc.Core.atlas;

public class SidedSlimeBlock extends SlimeBlock {
    public TextureRegion[] baseRegion = new TextureRegion[4];

    public SidedSlimeBlock(String name, int stype){
        super(name, stype);

        category = Category.distribution;
    }

    @Override
    public void load(){
        super.load();
        baseRegion[0] = region;
        for(int i = 1; i<4; i++){
            baseRegion[i] = atlas.find(name + "-" + i);
        }
    }

    public class SidedSlimeBuild extends SlimeBuild{
        @Override
        public void draw(){
            Draw.rect(baseRegion[rotation], x, y);
        }
    }
}