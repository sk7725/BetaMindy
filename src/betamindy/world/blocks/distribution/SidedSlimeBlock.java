package betamindy.world.blocks.distribution;

import mindustry.world.*;
import mindustry.gen.*;

public class SidedSlimeBlock extends SlimeBlock {
    public int slimeType;
    public SidedSlimeBlock(String name, int stype){
        super(name, stype);
        slimeType = stype;
    }

    public class SidedSlimeBuild extends SlimeBuild{

    }
}