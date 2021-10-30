package betamindy.util;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import betamindy.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import java.util.*;

import static mindustry.Vars.*;
import static mindustry.world.Build.validPlace;

public class InventoryModule {
    public final static int maxAmount = 99; //must be <= 126
    public static final String tag = "bm-inv-";

    public static final Inventory[] teams = new Inventory[Team.all.length];

    static boolean changed = false;

    public static class Inventory {
        protected final IntSeq invList = new IntSeq(); //ids, short range [0?, 65535]
        protected final IntSeq amountList = new IntSeq(); //amounts, byte range [-1, 127]
        public @Nullable Team team; //null unless this inventory is global

        public Inventory(@Nullable Team team){
            this.team = team;
        }

        /** This method MUST be called when making changes to the inventory! */
        public boolean add(Block block, int amount){
            return add(block, amount, true);
        }

        public boolean add(Block block, int amount, boolean updateUI){
            if(amount == 0) return hasActual(block);
            int id = block.id;
            //Log.info("id:" + id + " [accent]" + String.format("%16s", Integer.toBinaryString(id)).replace(" ", "0"));
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

            //todo: refactor or something, rn i cannot give a shit
            //save to the rules only if the inventory is a global team one
            if(ret && current != -1 && team != null){
                if(state.isCampaign()){
                    if(team == state.rules.defaultTeam){
                        //in campaign, only the player team is saved
                        Core.settings.put(tag + "0", compressed());
                    }
                }else{
                    state.rules.tags.put(tag + team.id, compressed());
                }
            }

            if(!headless && BetaMindy.mui.invfrag != null && updateUI) BetaMindy.mui.invfrag.refreshInventory();
            return ret;
        }

        public boolean has(Block block){
            return invList.contains(block.id);
        }

        public boolean hasActual(Block block){
            int index = invList.indexOf(block.id);
            if(index < 0) return false;
            return amountList.get(index) != 0;
        }

        public Block block(int index){
            return content.getByID(ContentType.block, invList.get(index));
        }

        /** INDEX IS NOT THE BLOCK'S ID */
        public int amountOf(int index){
            return amountList.get(index);
        }

        public int amount(Block block){
            int index = invList.indexOf(block.id);
            if(index < 0) return 0;
            return amountList.get(index);
        }

        public int getSize(){
            return invList.size;
        }

        public int getRealSize(){
            int n = invList.size;
            int sum = 0;
            for(int i = 0; i < n; i++){
                if(amountOf(i) > 0) sum++;
            }
            return sum;
        }

        public String compressed(){
            if(invList.size <= 0) return "";
            StringBuilder s = new StringBuilder(invList.size + "|");
            for(int i = 0; i < invList.size; i++){
                int item = invList.get(i);//short
                s.append((char) ((item >> 8) & 0xFF));
                s.append((char) (item & 0xFF));
                int a = amountList.get(i);//[-1, 127] byte
                if(a == -1) a = maxAmount + 1;
                s.append((char) ((byte) a));
            }
            //Log.info("c>" + invList.size);
            return s.toString();
        }

        public static void uncompress(Inventory inventory, String s){
            inventory.reset();
            int shaft = s.indexOf('|');
            if(shaft <= 0){
                Log.err("Invalid inventory data! Skipping...");
                return;
            }
            int n = Integer.parseInt(s.substring(0, shaft));
            //Log.info("u>" + n);
            String data = s.substring(shaft + 1);
            if(data.length() != n * 3){
                Log.err("Invalid inventory data! Skipping...");
                return;
            }
            for(int i = 0; i < n * 3; i+=3){
                int block = (((int)data.charAt(i)) << 8) | ((int)data.charAt(i + 1));
                int amount = data.charAt(i + 2);
                if(amount == 0) continue;
                inventory.invList.add(block);
                inventory.amountList.add(amount == maxAmount + 1 ? -1 : amount);
            }
        }

        public void reset(){
            invList.clear();
            amountList.clear();
        }

        @Override
        public String toString(){
            return "Inventory{" +
                    "invList=" + invList +
                    ", amountList=" + amountList +
                    '}';
        }
    }

    //methods below are for global team inventories; Do not use for inventory storages!
    public static void loadInventory(Team team){
        if(teams[team.id] == null){
            teams[team.id] = new Inventory(team);
            Log.info("Initialized Inventory for team [#" + team.color.toString() + "]" + team.name + "[]");
        }

        teams[team.id].reset();

        if(state.isGame()){
            if(state.isCampaign()){
                if(team == state.rules.defaultTeam && Core.settings.has(tag + "0")){
                    //in campaign, only the player team is saved
                    Inventory.uncompress(teams[team.id], Core.settings.getString(tag + "0"));
                }
            }
            else if(state.rules.tags.containsKey(tag + team.id)){
                Inventory.uncompress(teams[team.id], state.rules.tags.get(tag + team.id));
            }
        }

        /*if(uwu){
            teams[team.id].invList.addAll(MindyBlocks.floorRemover.id, MindyBlocks.metalGoldfloor.id, MindyBlocks.copperFloor.id, Blocks.scatter.id);
            teams[team.id].amountList.addAll( -1, 20, 40, 5);
        }
         */
    }

    public static void reset(){
        Arrays.fill(teams, null);
    }

    public static int getSize(){
        return getSize(false);
    }

    public static int getSize(boolean initialize){
        if(headless) return 0;
        int i = player.team().id;
        if(initialize){
            loadInventory(player.team());
            return teams[i].getSize();
        }
        else {
            return (teams[i] == null) ? 0 : teams[i].getSize();
        }
    }

    public static boolean add(Block block, int amount, Team team){
        if(teams[team.id] == null) loadInventory(team);
        return teams[team.id].add(block, amount);
    }

    public static boolean hasActual(Block block, Team team){
        return teams[team.id] != null && teams[team.id].hasActual(block);
    }

    public static void updatePlans(Unit player){
        if(teams[player.team.id] == null) loadInventory(player.team);
        for(BuildPlan current : player.plans()){
            if(current.breaking || current.initialized || !validPlace(current.block, player.team, current.x, current.y, current.rotation)) continue;

            if(teams[player.team.id].hasActual(current.block)){
                current.initialized = true;
                inventoryPlace(player, current.block, player.team, current.x, current.y, current.rotation);//todo see below
            }
        }
        if(changed && !headless && BetaMindy.mui.invfrag != null && player.team == Vars.player.team()){
            BetaMindy.mui.invfrag.refreshInventory();
            changed = false;
            if(control.input.block != null && !teams[player.team.id].hasActual(control.input.block)){
                control.input.block = null; //unfocus
            }
        }
    }

    //todo make this a Call.inventoryPlace
    public static void inventoryPlace(@Nullable Unit unit, Block result, Team team, int x, int y, int rotation){
        if(teams[team.id] == null || !validPlace(result, team, x, y, rotation) || !teams[team.id].add(result, -1, false)){
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
            if(unit != null) MindyFx.buildLaser.at(tile.drawx(), tile.drawy(), result.size, Pal.lancerLaser, unit);
            MindyFx.placeBlockBlue.at(tile.drawx(), tile.drawy(), result.size);
            if(team == player.team()) changed = true;
        }
    }
}
