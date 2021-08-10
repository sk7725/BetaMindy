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
    public TextureRegion icon;
    public int cost = 10;
    /** Whether to leave the purchase dialog after purchasing this item. */
    public boolean abort = false;

    public Boolf<Building> unlocked = e -> true;

    public PurchaseItem(String name, int cost){
        this.name = name;
        this.cost = cost;

        localizedName = local();
        description = Core.bundle.getOrNull("shopItem." + name + ".description");
    }

    public void load(){
        if(icon == null){
            icon = atlas.find("betamindy-" + name);
        }
    }

    public String local(){
        return Core.bundle.has("shopItem." + name + ".name") ? Core.bundle.get("shopItem." + name + ".name") : name;
    }

    public boolean purchase(Building source){
        return false;
    }

    public void buildButton(Button t){
        t.add(localizedName).growX().left();

        if(description != null){
            t.row();
            t.add(description).growX().left().color(Color.gray);
        }
        t.row();
        t.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
    }
}
