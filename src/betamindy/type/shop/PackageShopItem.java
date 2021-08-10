package betamindy.type.shop;

import arc.scene.ui.*;
import betamindy.type.*;
import betamindy.world.blocks.storage.Shop.*;
import mindustry.type.*;
import mindustry.ui.*;

public class PackageShopItem extends ShopItem {
    public ItemStack[] packageItems;
    public PackageShopItem(String name, int cost){
        super(name, cost);
    }

    @Override
    public void buildButton(Button t){
        super.buildButton(t);
        for(ItemStack stack : packageItems){
            t.row();

            t.table(tt -> {
                tt.left();
                tt.image(stack.item.icon(Cicon.small)).left();
                tt.add(String.valueOf(stack.amount)).left();
            }).left();
        }
    }

    @Override
    public boolean shop(ShopBuild source){
        boolean success = super.purchase(source);
        if(!success) return false;

        for(ItemStack stack : packageItems){
            if(!source.addItemPayload(stack.item, stack.amount)) success = false;
        }

        return success;
    }
}
