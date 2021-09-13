package betamindy.type;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import betamindy.*;
import mindustry.gen.*;
import mindustry.type.*;

import static arc.Core.atlas;

public abstract class PurchaseItem {
    public String name;
    public String localizedName, description;
    public int cost = 10;
    /** Whether to leave the purchase dialog after purchasing this item. */
    public boolean abort = false;
    public float scarcity = 1f;

    public Boolf<Building> unlocked = e -> true;

    public PurchaseItem(String name, int cost){
        this.name = name;
        this.cost = cost;

        localizedName = local();
        description = Core.bundle.getOrNull("purchase." + name + ".description");
    }

    public String local(){
        return Core.bundle.get("purchase." + name + ".name", name);
    }

    public boolean purchase(Building source, Unit player){
        return false;
    }

    public void buildButton(Button t){
        t.add(localizedName).growX().left();

        if(description != null){
            t.row();

            Label text = new Label(description);
            text.setWrap(true);

            t.add(text).growX().left().color(Color.gray);
        }
        t.row();
        t.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
    }
}
