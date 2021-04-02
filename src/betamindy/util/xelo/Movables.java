package betamindy.util.xelo;

import arc.util.pooling.Pools;
import betamindy.util.xelo.movables.BridgeMovable;
import betamindy.util.xelo.movables.PowerNodeMovable;
import mindustry.gen.Building;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.power.PowerNode;

public class Movables {

    public static Movable get(Building building) {
        if(building instanceof ItemBridge.ItemBridgeBuild) {
            // We don't need to check for LiquidBridgeBuild as it extend ItemBridgeBuild
            return Pools.obtain(BridgeMovable.class, BridgeMovable::new);
        }
        if(building instanceof PowerNode.PowerNodeBuild) {
            return Pools.obtain(PowerNodeMovable.class, PowerNodeMovable::new);
        }

        return null;
    }
}
