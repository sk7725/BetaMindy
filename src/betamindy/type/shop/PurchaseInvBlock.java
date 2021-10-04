package betamindy.type.shop;

import arc.*;
import arc.scene.ui.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

public class PurchaseInvBlock extends PurchaseItem {
    public Block block;
    public int amount, itemPrice;
    public @Nullable Item currencyItem;

    public PurchaseInvBlock(Block block, int price, int amount){
        super(block.name + "-inv", price);
        this.block = block;
        this.amount = amount;

        localizedName = "[#" + Pal2.inventory.toString() + "]" + block.localizedName + "[]";
    }

    public PurchaseInvBlock(Block block, int price, int amount, Item currencyItem){
        super(block.name + "-inv", 0);
        this.block = block;
        this.amount = amount;
        this.currencyItem = currencyItem;
        itemPrice = price;

        localizedName = "[#" + Pal2.inventory.toString() + "]" + block.localizedName + "[]";
        unlocked = e -> currencyItem == null || Vars.state.rules.infiniteResources || (e.team.core() != null && e.team.core().items().get(currencyItem) >= itemPrice);
    }

    @Override
    public void buildButton(Button t){
        t.left();
        t.image(block.uiIcon).size(40).padRight(10f);

        t.table(tt -> {
            tt.left();
            tt.add("[accent]" + Iconc.box + "[] " + localizedName + (amount > 1 ? " [white]x"+amount+"[]" : "")).growX().left();
            tt.row();
            tt.add(Core.bundle.get("ui.price") + ": " + (currencyItem == null ? Core.bundle.format("ui.anucoin.emoji", cost) : currencyItem.emoji() + itemPrice)).left();
        }).growX();
    }

    @Override
    public boolean purchase(Building source, Unit player){
        if(player == null) return false;
        if(currencyItem != null){
            if(source.team.core() == null) return false;
            if(!Vars.state.rules.infiniteResources && source.team.core().items().get(currencyItem) < itemPrice) return false;

            if(!block.unlockedNow()) block.unlock();
            if(InventoryModule.add(block, amount, player.team)){
                source.team.core().items().remove(currencyItem, itemPrice);
                return true;
            }
            return false;
        }
        else{
            if(!block.unlockedNow()) block.unlock();
            return InventoryModule.add(block, amount, player.team);
        }
    }
}
