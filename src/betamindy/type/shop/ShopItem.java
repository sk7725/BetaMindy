package betamindy.type.shop;

import betamindy.type.*;
import betamindy.world.blocks.storage.Shop.*;
import mindustry.gen.*;

public class ShopItem extends PurchaseItem {
    public ShopItem(String name, int cost){
        super(name, cost);
    }

    public boolean shop(ShopBuild source){
        return false;
    }
}
