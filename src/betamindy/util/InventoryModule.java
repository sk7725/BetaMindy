package betamindy.util;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import betamindy.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.*;
import static mindustry.world.Build.validPlace;

public class InventoryModule {
    final static int maxAmount = 99;

    private static final IntSeq invList = new IntSeq(); //ids
    private static final IntSeq amountList = new IntSeq();//amounts

    public static void loadInventory(){
        //todo: read inventory from rules
        //todo: remove any 0-amount items from both the invlist & rules
        invList.clear();
        amountList.clear();
        invList.addAll(MindyBlocks.floorRemover.id, MindyBlocks.metalGoldfloor.id, Blocks.scatter.id);
        amountList.addAll( -1, 20, 5);
    }

    /** This method MUST be called when making changes to the inventory! */
    public static boolean add(Block block, int amount){
        return add(block, amount, true);
    }

    public static boolean add(Block block, int amount, boolean updateUI){
        if(amount == 0) return hasActual(block);
        int id = block.id;
        if(!invList.contains(id)){
            if(amount < 0) return false;
            invList.add(id);
            amountList.add(0);
        }
        int index = invList.indexOf(id);
        int current = amountList.get(index);
        boolean ret = true;
        if(current != -1){
            if(amount > 0){
                //add
                if(current >= maxAmount) ret = false;
                else current = Math.min(current + amount, maxAmount);
            }
            else{
                //remove
                if(current <= 0) ret = false;
                else current = Math.max(current + amount, 0);
            }
            if(ret) amountList.set(index, current);
        }

        //todo: write to rules too
        if(!headless && BetaMindy.mui.invfrag != null && updateUI) BetaMindy.mui.invfrag.refreshInventory();
        return ret;
    }

    public static boolean has(Block block){
        return invList.contains(block.id);
    }

    public static boolean hasActual(Block block){
        int index = invList.indexOf(block.id);
        if(index < 0) return false;
        return amountList.get(index) != 0;
    }

    public static Block block(int index){
        return content.getByID(ContentType.block, invList.get(index));
    }

    public static int amount(int index){
        return amountList.get(index);
    }

    public static int getSize(){
        return invList.size;
    }

    public static void updatePlans(Unit player){
        boolean changed = false;
        for(BuildPlan current : player.plans()){
            if(current.breaking || current.initialized || !validPlace(current.block, player.team, current.x, current.y, current.rotation)) continue;

            if(add(current.block, -1, false)){
                changed = true;
                current.initialized = true;
                inventoryPlace(player, current.block, player.team, current.x, current.y, current.rotation);//todo see below
            }
        }
        if(changed && !headless && BetaMindy.mui.invfrag != null) BetaMindy.mui.invfrag.refreshInventory();
    }

    //todo make this a Call.inventoryPlace
    public static void inventoryPlace(@Nullable Unit unit, Block result, Team team, int x, int y, int rotation){
        if(!validPlace(result, team, x, y, rotation)){
            return;
        }

        Tile tile = world.tile(x, y);

        //just in case
        if(tile == null) return;

        //auto-rotate the block to the correct orientation and bail out
        if(tile.team() == team && tile.block() == result && tile.build != null && tile.block().quickRotate){
            if(unit != null && unit.getControllerName() != null) tile.build.lastAccessed = unit.getControllerName();
            tile.build.rotation = Mathf.mod(rotation, 4);
            tile.build.updateProximity();
            tile.build.noSleep();
            Fx.rotateBlock.at(tile.build.x, tile.build.y, tile.build.block.size);
            return;
        }

        //break all props in the way
        tile.getLinkedTilesAs(result, out -> {
            if(out.block() != Blocks.air && out.block().alwaysReplace){
                //out.block().breakEffect.at(out.drawx(), out.drawy(), out.block.size, out.block.mapColor);//todo v7
                out.remove();
            }
        });

        Block previous = tile.block();

        result.beforePlaceBegan(tile, previous);

        tile.setBlock(result, team, rotation);

        if(unit != null && unit.getControllerName() != null) tile.build.lastAccessed = unit.getControllerName();
        result.placeBegan(tile, previous);

        if(!headless){
            //todo lancerlaser build effects & cool stuff
            if(unit != null) MindyFx.buildLaser.at(tile.drawx(), tile.drawy(), result.size, Pal.lancerLaser, unit);
            MindyFx.placeBlockBlue.at(tile.drawx(), tile.drawy(), result.size);
        }
    }
}
