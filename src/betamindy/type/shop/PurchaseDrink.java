package betamindy.type.shop;

import arc.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import betamindy.content.*;
import betamindy.type.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;

import static mindustry.Vars.headless;
import static mindustry.Vars.ui;

public class PurchaseDrink extends PurchaseItem {
    public StatusEffect status;
    public float duration = 60 * 60 * 10;
    public Sound drinkSound = MindySounds.drink;

    public PurchaseDrink(String name, int cost, StatusEffect status){
        super(name, cost);
        this.status = status;
        localizedName = "[#" + status.color.toString() + "]" + local() + "[]";
    }

    @Override
    public void buildButton(Button t){
        t.left();
        TextureRegion region = Core.atlas.find("betamindy-" + name);
        if(region.found()){
            t.image(region).size(40).padRight(10f);
        }

        t.table(tt -> {
            tt.left();
            tt.add(localizedName).growX().left();
            tt.row();
            tt.table(b -> {
                b.left();
                b.button(status.localizedName, new TextureRegionDrawable(status.uiIcon), Styles.cleart, 25f, () -> {
                    ui.content.show(status);
                }).left().size(180f, 27f);
                b.add(status.permanent ? "[lightgray](--:--)[]" : " [lightgray](" + formatTime(duration) + ")[]");
            }).left();
            tt.row();
            tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
        }).growX();
    }

    @Override
    public boolean purchase(Building source, Unit player){
        if(player == null || player.dead()) return false;
        player.apply(status, status.permanent ? 1f : duration);
        if(!headless) drinkSound.at(player);
        return true;
    }

    public String formatTime(float ticks){
        int time = (int)(ticks / 60);
        if(time < 60) return "0:" + (time < 10 ? "0" : "") + time;
        int mod = time % 60;
        return (time / 60) + ":" + (mod < 10 ? "0" : "") + mod;
    }
}
