package betamindy.world.draw;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawBlockShadow extends DrawBlock {
    public TextureRegion region;
    public String suffix = "-shadow";

    public DrawBlockShadow(String suffix){
        this.suffix = suffix;
    }

    public DrawBlockShadow(){
    }

    @Override
    public void draw(Building build){
        float z = Draw.z();
        Draw.z(Layer.block - 0.99f);
        Draw.color(Pal.shadow);
        Draw.rect(region, build.x, build.y, build.block.rotate ? build.rotation * 90f : 0);
        Draw.z(z);
    }

    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name + suffix);
    }
}