package betamindy.type.shop;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.*;
import betamindy.*;
import betamindy.graphics.*;
import betamindy.world.blocks.storage.Shop.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;

import static mindustry.Vars.*;

/** Used for non-item-shop shops that sell items. */
public class ItemItem extends ShopItem{
    public Item item;
    public int amount;

    public ItemItem(Item item, int score, int amount){
        super(item.name, score);
        this.item = item;
        this.amount = amount;

        localizedName = "[#" + item.color.toString() + "]" + item.localizedName + "[]";
        unlocked = e -> !((ShopBuild)e).disabledBox() && (item.unlocked() || state.rules.infiniteResources);
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
        boolean unlocked = item.unlocked() || state.rules.infiniteResources;

        t.left();
        t.image(unlocked ? item.uiIcon : Icon.lock.getRegion()).size(40).padRight(10f).color(unlocked ? Color.white : Pal2.locked);

        if(unlocked){
            t.table(tt -> {
                tt.left();

                Label text = new Label(localizedName + " [accent]x" + amount + "[]");
                text.setWrap(true);

                tt.add(text).growX().left();
                tt.row();
                tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
            }).growX();
        }
    }
}
