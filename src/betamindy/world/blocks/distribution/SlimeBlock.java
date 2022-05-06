package betamindy.world.blocks.distribution;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import betamindy.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.gen.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class SlimeBlock extends Block {
    public int slimeType;
    public Color color;
    public TextureRegion coreRegion, topRegion;
    public boolean useTopRegion = false;
    //public Effect destroyEffect = MindyFx.slimeBreak;

    public SlimeBlock(String name, int stype){
        super(name);
        slimeType = stype;
        update = true;
        solid = true;
        rotate = false;
        hasColor = true;
        destroyEffect = breakEffect = MindyFx.slimeBreak;
    }

    @Override
    public void load(){
        super.load();
        coreRegion = atlas.find(name + "-core");
        if(useTopRegion) topRegion = atlas.find(name + "-top");
        mapColor = color;
    }

    @Override
    public int minimapColor(Tile tile){
        return color.rgba();
    }

    @Override
    public void setBars() {
        super.setBars();
        if(hasPower){
            addBar("power", (SlimeBuild entity) -> new Bar(() ->
                Core.bundle.get("bar.power"),
                () -> Pal.powerBar,
                () -> entity.power.graph.getSatisfaction()
            ));
        }
    }

    public class SlimeBuild extends Building implements SpinDraw {
        @Override
        public void draw(){
            if(Core.settings.getBool("animatedshields") && Core.settings.getBool("slimeeffect")){
                Draw.rect(coreRegion, x, y);
                Draw.z(Layer.shields + 0.0001f);
                Draw.color(color);
                Fill.square(x, y, size * tilesize / 2f);
                if(useTopRegion){
                    Draw.color();
                    Draw.rect(topRegion, x, y);
                }
                Draw.reset();
            }
            else super.draw();
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            if(Core.settings.getBool("animatedshields") && Core.settings.getBool("slimeeffect")){
                Draw.rect(coreRegion, x, y, dr);
                Draw.z(Layer.shields + 0.0001f);
                Draw.color(color);
                Fill.rect(x, y, size * tilesize, size * tilesize, dr);
                if(useTopRegion){
                    Draw.color();
                    Draw.rect(topRegion, x, y, dr);
                }
                Draw.reset();
            }
            else Draw.rect(region, x, y, dr);
        }

        @Override
        public void onDestroyed(){
            destroyEffect.at(x, y, color);
        }
    }
}