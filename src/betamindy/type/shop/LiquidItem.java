package betamindy.type.shop;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import betamindy.*;
import betamindy.world.blocks.storage.Shop.*;
import mindustry.type.*;
import mindustry.ui.*;

import static mindustry.Vars.state;

public class LiquidItem extends ShopItem{
    public Liquid liquid;
    public float amount;

    public LiquidItem(Liquid liquid, int score, float amount){
        super(liquid.name, score);
        this.liquid = liquid;
        this.amount = amount;

        localizedName = "[#" + liquid.color.toString() + "]" + liquid.localizedName + "[]";
        unlocked = e -> !((ShopBuild)e).disabledLiquid(liquid) && (liquid.unlocked() || state.rules.infiniteResources);
    }

    /*
    public LiquidLiquid(Liquid liquid){
        this(liquid, Math.max(Math.round(BetaMindy.liquidScores.get(liquid)), 15), 50f);
    }
     */
    //todo python dewit

    @Override
    public boolean shop(ShopBuild source){
        return source.addLiquidPayload(liquid, amount);
    }

    @Override
    public void buildButton(Button t){
        t.left();
        t.image(liquid.icon(Cicon.medium)).size(40).padRight(10f);

        t.table(tt -> {
            tt.left();
            tt.add(localizedName + " [accent]"+amount+"L[]").growX().left();
            tt.row();
            tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
        }).growX();
    }
}
