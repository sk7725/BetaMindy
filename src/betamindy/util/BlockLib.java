package betamindy.util;

import arc.struct.*;
import betamindy.content.*;
import betamindy.type.*;
import betamindy.type.item.*;
import betamindy.type.shop.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.world.*;

public class BlockLib {
    public static ObjectMap<Block, Seq<Block> > crafts = new ObjectMap<>();
    private final static OrderedMap<WallType, String> loadCache = new OrderedMap<>();

    public static void load(){
        if(!loadCache.isEmpty()){
            loadCache.each((t, s) -> {
                t.block = Vars.content.getByName(ContentType.block, s);
            });
        }
    }

    public static void addCraft(Block before, Block after){
        Seq<Block> blocks = crafts.get(before, Seq::new);
        if(!blocks.contains(after)) blocks.add(after);
        //after.playerPlaceable = false;//todo v7
    }

    public static PurchaseItem[] bitl(Block... blocks){
        PurchaseItem[] p = new PurchaseItem[blocks.length];
        for(int i = 0; i < blocks.length; i++){
            p[i] = asBitPurchase(blocks[i]);
        }
        return p;
    }

    public static PurchaseItem asPurchase(Block block){
        return asPurchase(block, 1, 20);
    }

    public static PurchaseItem asPurchase(Block block, int price, int amount){
        return new PurchaseInvBlock(block, price, amount);
    }

    public static PurchaseItem asBitPurchase(Block block){
        return asBitPurchase(block, 5, 20);
    }

    public static PurchaseItem asBitPurchase(Block block, int price, int amount){
        return new PurchaseInvBlock(block, price, amount, MindyItems.bittrium);
    }

    public static boolean isForeign(Block block){
        if(block.requirements.length < 1 || block.minfo.mod == null) return false;
        for(int i = block.requirements.length - 1; i >= 0; i--){ //foreign items should be at the end of the items
            if(block.requirements[i].item instanceof ForeignItem){
                //block.playerPlaceable = false; //todo v7
                return true;
            }
        }
        return false;
    }

    private static void loadAfter(WallType t, String s){
        loadCache.put(t, s);
    }

    public enum WallType {
        stone(Blocks.stoneWall);

        public Block block;

        WallType(Block core){
            this.block = core;
        }

        WallType(String name){
            this.block = null;
            loadAfter(this, name);
        }
    }
}
