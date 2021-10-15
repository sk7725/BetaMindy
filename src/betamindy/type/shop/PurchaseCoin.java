package betamindy.type.shop;

import arc.*;
import arc.scene.ui.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.ui.*;
import betamindy.util.*;
import betamindy.world.blocks.storage.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;

public class PurchaseCoin extends PurchaseItem {
    public int coins, itemPrice;
    public Item currencyItem;

    public PurchaseCoin(int coins, int bittrium){
        this(coins, bittrium, MindyItems.bittrium);
    }

    public PurchaseCoin(int coins, int bittrium, Item currencyItem){
        super(coins + "coins", 0);
        this.currencyItem = currencyItem;
        this.coins = coins;
        itemPrice = bittrium;

        localizedName = "[#" + Pal2.coin.toString() + "]" + coins + " " + Core.bundle.get("ui.anucoin.multiple") + "[]";
        unlocked = e -> Vars.state.rules.infiniteResources || (e.team.core() != null && e.team.core().items().get(currencyItem) >= itemPrice);
    }

    @Override
    public void buildButton(Button t){
        t.left();
        t.image(AnucoinTex.uiCoin).size(40).padRight(10f);

        t.table(tt -> {
            tt.left();
            tt.add(localizedName).growX().left();
            tt.row();
            tt.add(Core.bundle.get("ui.price") + ": " + currencyItem.emoji() + itemPrice).left();
        }).growX();
    }

    @Override
    public boolean purchase(Building source, Unit player){
        if(source.team.core() == null) return false;
        if(!Vars.state.rules.infiniteResources && source.team.core().items().get(currencyItem) < itemPrice) return false;

        if(source instanceof CoinBuild cb){
            cb.handleCoin(source, coins);
            source.team.core().items().remove(currencyItem, itemPrice);
            return true;
        }
        return false;
    }
}
