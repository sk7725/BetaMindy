package betamindy.world.blocks.environment;

import betamindy.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class DecorativeFloor extends OverlayFloor {
    public boolean refund = true;
    public DecorativeFloor(String name){
        super(name);
        update = true;
        alwaysUnlocked = true;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team){
        return super.canPlaceOn(tile, team) && (tile.overlay() == Blocks.air || ((tile.overlay() instanceof DecorativeFloor) && tile.overlay().id != id));
    }

    @Override
    public boolean isHidden(){
        return !Vars.headless && !(BetaMindy.inventoryUI && InventoryModule.hasActual(this));
    }

    public class DecorativeFloorBuild extends Building {
        @Override
        public void update(){ //not updateTile on purpose
            if(tile.overlay() instanceof DecorativeFloor df){
                if(df.refund){
                    InventoryModule.add(df, 1);
                }
            }
            tile.setOverlay(block);
            tile.remove();
            remove();
        }
    }
}
