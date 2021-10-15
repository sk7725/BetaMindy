package betamindy.world.blocks.production.payduction.craft;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import betamindy.world.blocks.production.payduction.*;
import mindustry.graphics.*;

public interface Catalyst {
    float boostTime = 3600f;
    boolean updateB = false;

    TextureRegion[] boostIcons = null;
    Color boostColor = Pal.accent;

    default void initBoost(PayloadFactory.PayloadFactoryBuild factory){}
    default void updateBoost(PayloadFactory.PayloadFactoryBuild factory){}
}
