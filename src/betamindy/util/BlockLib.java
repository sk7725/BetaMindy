package betamindy.util;

import arc.struct.*;
import betamindy.content.*;
import betamindy.type.*;
import betamindy.type.shop.*;
import mindustry.world.*;

public class BlockLib {
    public static ObjectMap<Block, Seq<Block> > crafts = new ObjectMap<>();

    public static void addCraft(Block before, Block after){
        Seq<Block> blocks = crafts.get(before, Seq::new);
        if(!blocks.contains(after)) blocks.add(after);
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
}
