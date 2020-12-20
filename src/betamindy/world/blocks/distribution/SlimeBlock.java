package betamindy.world.blocks.distribution;

import mindustry.world.*;
import mindustry.gen.*;

public class SlimeBlock extends Block {
    public int slimeType;
    public SlimeBlock(String name, int stype){
        super(name);
        slimeType = stype;
    }

    public class SlimeBuild extends Building{

    }
}