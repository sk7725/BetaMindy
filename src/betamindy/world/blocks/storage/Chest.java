package betamindy.world.blocks.storage;

import arc.struct.*;
import arc.util.io.*;
import betamindy.util.*;
import betamindy.util.InventoryModule.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;

/** Stores inventory. */
public class Chest extends Block {
    /** Max amount of different stacks this chest can hold. */
    public int slots = 10;
    /** Max amount of blocks per stack. Must be under 100. */
    public int capacity = 25;
    private final IntSeq tmpi = new IntSeq(3);
    public Chest(String name){
        super(name);
        solid = true;
        update = true;
        configurable = true;
        //while sync = true would make sense, the packet of this bad boy is Very Big so...
        flags = EnumSet.of(BlockFlag.storage);

        config(IntSeq.class, (ChestBuild build, IntSeq i) -> {
            if(i.size == 3 && i.get(0) >= 0 && i.get(0) < Team.all.length){
                //team id, block id, amount: + if storing, - if taking
                Team team = Team.get(i.get(0));
                int id = i.get(1);
                int amount = i.get(2);
                if(amount > 0 && InventoryModule.teams[team.id] != null){
                    //store
                    amount = Math.min(amount, InventoryModule.teams[team.id].amount(id));
                    Block block = Vars.content.getByID(ContentType.block, id);
                    if(InventoryModule.add(block, -amount, team)){
                        build.inventory.add(block, amount);
                    }
                }
                else if(amount < 0){
                    //take
                    amount = Math.min(build.inventory.amount(id), -amount);
                    Block block = Vars.content.getByID(ContentType.block, id);
                    if(build.inventory.add(block, -amount)){
                        InventoryModule.add(block, amount, team);
                    }
                }
            }
        });
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.itemCapacity, slots * capacity, StatUnit.blocks);
    }

    public class ChestBuild extends Building {
        public Inventory inventory = new Inventory(null);

        public void storeChest(Block block, int amount){
            tmpi.clear();
            tmpi.add(team.id, block.id, amount);
            configure(tmpi);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.str(inventory.compressed());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            Inventory.uncompress(inventory, read.str());
        }
    }
}
