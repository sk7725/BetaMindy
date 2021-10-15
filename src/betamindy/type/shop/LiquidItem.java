package betamindy.type.shop;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.*;
import betamindy.graphics.*;
import betamindy.world.blocks.storage.Shop.*;
import mindustry.gen.*;
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
        boolean unlocked = liquid.unlocked() || state.rules.infiniteResources;

        t.left();
        t.image(unlocked ? liquid.uiIcon : Icon.lock.getRegion()).size(40).padRight(10f).color(unlocked ? Color.white : Pal2.locked);

        if(unlocked){
            t.table(tt -> {
                tt.left();

                Label text = new Label(localizedName + " [accent]" + amount + "L[]");
                text.setWrap(true);

                tt.add(text).growX().left();
                tt.row();
                tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
            }).growX();
        }
    }
}
