package betamindy.world.blocks.distribution;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.graphics.*;
import mindustry.type.Category;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.gen.*;
import mindustry.world.meta.BlockGroup;

import static arc.Core.atlas;

public class SlimeBlock extends Block {
    public int slimeType;
    public Color color;
    public TextureRegion coreRegion, topRegion;
    public boolean useTopRegion = false;

    public SlimeBlock(String name, int stype) {
        super(name);
        slimeType = stype;
        update = true;
        solid = true;
        rotate = false;
    }

    @Override
    public void load() {
        super.load();
        coreRegion = atlas.find(name + "-core");
        if (useTopRegion) topRegion = atlas.find(name + "-top");
    }

    @Override
    public void setBars() {
        super.setBars();
        if (hasPower) {
            bars.add("power", (SlimeBuild entity) -> new Bar(() ->
                    Core.bundle.get("bar.power"),
                    () -> Pal.powerBar,
                    () -> entity.power.graph.getSatisfaction()));
        }
    }

    public class SlimeBuild extends Building {
        @Override
        public void draw() {
            if (Core.settings.getBool("animatedshields") && Core.settings.getBool("slimeeffect")) {
                Draw.rect(coreRegion, x, y);
                Draw.z(Layer.shields + 0.0001f);
                Draw.color(color);
                Fill.square(x, y, 4f);
                if (useTopRegion) {
                    Draw.color();
                    Draw.rect(topRegion, x, y);
                }
                Draw.reset();
            } else super.draw();
        }
    }
}