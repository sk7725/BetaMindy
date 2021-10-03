package betamindy.type.shop;

import arc.func.*;
import arc.graphics.Color;
import arc.scene.ui.*;
import betamindy.BetaMindy;
import betamindy.graphics.*;
import betamindy.world.blocks.storage.Shop.*;
import mindustry.gen.Icon;
import mindustry.type.*;
import mindustry.ui.*;

import static mindustry.Vars.*;

public class PackageShopItem extends ShopItem {
    public ItemStack[] packageItems;

    public PackageShopItem(String name, ItemStack[] packageItems){
        super(name, 0);

        this.packageItems = packageItems;

        unlocked = e -> {
            boolean unlocked = true;

            if(!state.rules.infiniteResources) {
                for (ItemStack stack : packageItems) {
                    if(!stack.item.unlocked()){
                        unlocked = false;
                        break;
                    }
                }
            }

            return unlocked;
        };
    }

    public void definePrice(){
        for(ItemStack stack : packageItems){
            this.cost += (int)(BetaMindy.itemScores.get(stack.item) * stack.amount * 0.6f);
        }

        if(this.cost >= 500) {
            this.cost /= 100;
            this.cost = Math.round(this.cost) * 100 - 1;
        }
    }

    @Override
    public void buildButton(Button t){
        super.buildButton(t);
        for(ItemStack stack : packageItems){
            boolean unlocked = stack.item.unlocked() || state.rules.infiniteResources;

            t.row();

            t.table(tt -> {
                tt.left();
                tt.image(unlocked ? stack.item.uiIcon : Icon.lock.getRegion()).left().padRight(2f).color(unlocked ? Color.white : Pal2.locked);
                tt.add(String.valueOf(stack.amount)).left();
            }).left();
        }
    }

    @Override
    public boolean shop(ShopBuild source){
        boolean success = true;
        for(ItemStack stack : packageItems){
            if(!source.addItemPayload(stack.item, stack.amount)) success = false;
        }
        return success;
    }
}
