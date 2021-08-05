package betamindy.world.blocks.production;

import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.util.*;
import betamindy.graphics.*;
import mindustry.world.blocks.production.*;

/** This thing uh... teleports around */
public class SpaceCrafter extends GenericCrafter {
    public @Nullable Shader shader = MindyShaders.space;
    public TextureRegion shaderRegion, defaultRegion, glowRegion;
    public int range = 14;
    public int searchDepth = 5;

    public SpaceCrafter(String name){
        super(name);
    }
}
