package betamindy.content;

import arc.util.*;
import betamindy.type.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;

public class ShopItems implements ContentList {
    public ShopItem bundle1, bundle2, bundle3, bundle4, bundle5, bundle6;
    public ShopItem runnable1, runnable2;

    @Override
    public void load(){
        /* Bundles */
        bundle1 = new ShopItem("entry-bundle", 250){{
            bundleItems = ItemStack.with(Items.copper, 100, Items.lead, 100);
        }};

        bundle2 = new ShopItem("boost-bundle", 30000){{
            bundleItems = ItemStack.with(Items.silicon, 1000, Items.graphite, 1000);
        }};

        bundle2 = new ShopItem("ore-bundle", 200){{
            bundleItems = ItemStack.with(MindyItems.scalarRaw, 100, MindyItems.tensorRaw, 100, MindyItems.vectorRaw, 100);
        }};

        bundle3 = new ShopItem("meltdown-bundle", 4000){{
            bundleItems = ItemStack.with(Items.copper, 1200, Items.lead, 350, Items.graphite, 300, Items.silicon, 325, Items.surgeAlloy, 325);
        }};

        bundle4 = new ShopItem("minamite-bundle", 5500){{
            bundleItems = ItemStack.with(Items.thorium, 180, Items.blastCompound, 400);
        }};

        bundle5 = new ShopItem("pipes-bundle", 2000){{
            bundleItems = ItemStack.with(Items.metaglass, 200, Items.graphite, 130);
        }};

        bundle6 = new ShopItem("unit-bundle", 8000){{
            bundleItems = ItemStack.with(Items.copper, 500, Items.lead, 500, Items.silicon, 500);
        }};

        /* Runnables */
        runnable1 = new ShopItem("first-aids", 1500){{
            type = 1;

            runnable = e -> e.team.cores().each(c -> c.health = c.block.health);

            unlocked = e -> {
                boolean[] ret = new boolean[]{true};

                e.team.cores().each(c -> {
                    if(c.health == c.maxHealth) ret[0] = false;
                });

                return ret[0];
            };
        }};

        runnable2 = new ShopItem("invi-core", 10250){{
            type = 1;

            runnable = e -> {
                e.team.cores().each(c -> c.health = Float.NaN);

                Time.run(60f * 10f, () -> e.team.cores().each(c -> c.health = c.block.health));
            };
        }};
    }
}
