package betamindy.content;

import betamindy.type.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;

public class ShopItems implements ContentList {
    public ShopItem bundle1, bundle2, bundle3;

    @Override
    public void load(){
        bundle1 = new ShopItem("entry-bundle", 250){{
            bundleItems = ItemStack.with(Items.copper, 100, Items.lead, 100);
        }};

        bundle2 = new ShopItem("boost-bundle", 1850){{
            bundleItems = ItemStack.with(Items.silicon, 1000, Items.graphite, 1000);
        }};

        bundle3 = new ShopItem("meltdown-bundle", 4000){{
            bundleItems = ItemStack.with(Items.copper, 1200, Items.lead, 350, Items.graphite, 300, Items.silicon, 325, Items.surgeAlloy, 325);
        }};
    }
}
