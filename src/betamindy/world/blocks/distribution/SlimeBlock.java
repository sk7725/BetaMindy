package betamindy.world.blocks.distribution;

import mindustry.type.Category;
import mindustry.world.*;
import mindustry.gen.*;

public class SlimeBlock extends Block {
    public int slimeType;
    public SlimeBlock(String name, int stype){
        super(name);
        slimeType = stype;

        category = Category.distribution;
    }

    public class SlimeBuild extends Building{

    }
}