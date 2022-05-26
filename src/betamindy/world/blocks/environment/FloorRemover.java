package betamindy.world.blocks.environment;

import betamindy.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

public class FloorRemover extends Block {
    public FloorRemover(String name){
        super(name);
        update = true;
        alwaysUnlocked = true;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return super.canPlaceOn(tile, team, rotation) && tile.overlay() != Blocks.air && tile.overlay() instanceof DecorativeFloor;
    }

    @Override
    public boolean isHidden(){
        return !Vars.headless && !(BetaMindy.inventoryUI && InventoryModule.hasActual(this, Vars.player.team()));
    }

    public class FloorRemoverBuild extends Building {
        @Override
        public void update(){ //not updateTile on purpose
            if(tile.overlay() instanceof DecorativeFloor df){
                tile.clearOverlay();
                if(df.refund) InventoryModule.add(df, 1, team);
            }
            tile.remove();
            remove();
        }
    }
}
