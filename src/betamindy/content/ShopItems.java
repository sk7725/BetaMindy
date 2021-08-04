package betamindy.content;

import arc.util.*;
import betamindy.type.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;

public class ShopItems implements ContentList {
    public ShopItem package1, package2, package3, package4, package5, package6, package7, package8, package9, package10;
    public ShopItem runnable1, runnable2;

    @Override
    public void load(){
        /* packages */
        package1 = new ShopItem("entry-package", 250){{
            packageItems = ItemStack.with(Items.copper, 100, Items.lead, 100);
        }};

        package2 = new ShopItem("boost-package", 30000){{
            packageItems = ItemStack.with(Items.silicon, 1000, Items.graphite, 1000);
        }};

        package2 = new ShopItem("ore-package", 200){{
            packageItems = ItemStack.with(MindyItems.scalarRaw, 100, MindyItems.tensorRaw, 100, MindyItems.vectorRaw, 100);
        }};

        package3 = new ShopItem("meltdown-package", 4000){{
            packageItems = ItemStack.with(Items.copper, 1200, Items.lead, 350, Items.graphite, 300, Items.silicon, 325, Items.surgeAlloy, 325);
        }};

        package4 = new ShopItem("mynamite-package", 5500){{
            packageItems = ItemStack.with(Items.thorium, 180, Items.blastCompound, 400);
        }};

        package5 = new ShopItem("pipes-package", 2000){{
            packageItems = ItemStack.with(Items.metaglass, 200, Items.graphite, 130);
        }};

        package6 = new ShopItem("unit-package", 8000){{
            packageItems = ItemStack.with(Items.copper, 500, Items.lead, 500, Items.silicon, 500);
        }};

        package7 = new ShopItem("spectre-in-a-box", 1250){{
            packageItems = ItemStack.with(Items.copper, 900, Items.graphite, 300, Items.thorium, 250, Items.plastanium, 175, Items.surgeAlloy, 250);
        }};

        package8 = new ShopItem("spectre-package", 7350){{
            packageItems = ItemStack.with(Items.copper, 9000, Items.graphite, 3000, Items.thorium, 2500, Items.plastanium, 1750, Items.surgeAlloy, 2500);
        }};

        package9 = new ShopItem("foundation-in-a-box", 4000){{
            packageItems = ItemStack.with(Items.copper, 3000, Items.lead, 3000, Items.silicon, 2000);
        }};

        package10 = new ShopItem("nucleus-in-a-box", 5000){{
            packageItems = ItemStack.with(Items.copper, 8000, Items.lead, 8000, Items.silicon, 5000, Items.thorium, 4000);
        }};

        /* Runnables */
        runnable1 = new ShopItem("first-aids", 1500){{
            type = 1;

            runnable = e -> e.team.cores().each(c -> c.health = c.maxHealth);

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

                Time.run(60f * 10f, () -> e.team.cores().each(c -> c.health = c.maxHealth));
            };
        }};
    }
}
