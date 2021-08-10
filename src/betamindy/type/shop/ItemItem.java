package betamindy.type.shop;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import betamindy.*;
import betamindy.world.blocks.storage.Shop.*;
import mindustry.type.*;
import mindustry.ui.*;

/** Used for non-item-shop shops that sell items. */
public class ItemItem extends ShopItem{
    public Item item;
    public int amount;

    public ItemItem(Item item, int score, int amount){
        super(item.name, score);
        this.item = item;
        this.amount = amount;

        localizedName = "[#" + item.color.toString() + "]" + item.localizedName + "[]";
        unlocked = e -> !((ShopBuild)e).disabledBox();
    }

    public ItemItem(Item item){
        this(item, Math.max(Math.round(BetaMindy.itemScores.get(item)), 15), 15);
    }

    @Override
    public boolean shop(ShopBuild source){
        return source.addItemPayload(item, amount);
    }

    @Override
    public void buildButton(Button t){
        t.left();
        t.image(item.icon(Cicon.medium)).size(40).padRight(10f);

        t.table(tt -> {
            tt.left();
            tt.add(localizedName + " [accent]x"+amount+"[]").growX().left();
            tt.row();
            tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();;
        }).growX();
    }
}
