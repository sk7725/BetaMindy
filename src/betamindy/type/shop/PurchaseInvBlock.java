package betamindy.type.shop;

import arc.*;
import arc.scene.ui.*;
import betamindy.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.util.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;

public class PurchaseInvBlock extends PurchaseItem {
    public Block block;
    public int amount;
    public PurchaseInvBlock(Block block, int price, int amount){
        super(block.name + "-inv", price);
        this.block = block;
        this.amount = amount;

        localizedName = "[#" + Pal2.inventory.toString() + "]" + block.localizedName + "[]";
    }

    @Override
    public void buildButton(Button t){
        t.left();
        t.image(block.icon(Cicon.medium)).size(40).padRight(10f);

        t.table(tt -> {
            tt.left();
            tt.add("[accent]" + Iconc.box + "[] " + localizedName + (amount > 1 ? " [white]x"+amount+"[]" : "")).growX().left(); //todo icon emoji (how)?
            tt.row();
            tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
        }).growX();
    }

    @Override
    public boolean purchase(Building source, Unit player){
        if(player == null) return false;
        if(!block.unlockedNow()) block.unlock();
        return InventoryModule.add(block, amount, true);//todo add team
    }
}
