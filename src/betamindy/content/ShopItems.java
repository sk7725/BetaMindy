package betamindy.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.type.shop.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;

import static java.lang.Float.*;
import static mindustry.Vars.*;

public class ShopItems{
    public static ShopItem package1, package2, package3, package4, package5, package6, package7, package8, package9, package10, package11, package12, package13,
    holyRouter;
    public static PurchaseItem firstAid, invincibleCore, milk, coffee, herbTea, flowerTea, pancake, glowstick, sporeJuice, cocktail, bittriumWine, diodeCookie, bossCake, anucoins2000, anucoins4200, anucoins12000;

    public static void load(){
        /* packages */
        package1 = new PackageShopItem("entry-package",
                ItemStack.with(Items.copper, 100, Items.lead, 100)
        );

        package2 = new PackageShopItem("boost-package",
                ItemStack.with(Items.silicon, 1000, Items.graphite, 1000)
        );

        package3 = new PackageShopItem("ore-package",
                ItemStack.with(MindyItems.scalarRaw, 100, MindyItems.tensorRaw, 100, MindyItems.vectorRaw, 100)
        );

        package4 = new PackageShopItem("meltdown-package",
                ItemStack.with(Items.copper, 1200, Items.lead, 350, Items.graphite, 300, Items.silicon, 325, Items.surgeAlloy, 325)
        );

        package5 = new PackageShopItem("mynamite-package",
            ItemStack.with(Items.thorium, 180, Items.blastCompound, 400)
        );

        package6 = new PackageShopItem("pipes-package",
            ItemStack.with(Items.metaglass, 200, Items.graphite, 130)
        );

        package7 = new PackageShopItem("unit-package",
            ItemStack.with(Items.copper, 500, Items.lead, 500, Items.silicon, 500)
        );

        package8 = new PackageShopItem("spectre-in-a-box",
            ItemStack.with(Items.copper, 900, Items.graphite, 300, Items.thorium, 250, Items.plastanium, 175, Items.surgeAlloy, 250)
        );

        package9 = new PackageShopItem("spectre-package",
            ItemStack.with(Items.copper, 9000, Items.graphite, 3000, Items.thorium, 2500, Items.plastanium, 1750, Items.surgeAlloy, 2500)
        );

        package10 = new PackageShopItem("foundation-in-a-box",
            ItemStack.with(Items.copper, 3000, Items.lead, 3000, Items.silicon, 2000)
        );

        package11 = new PackageShopItem("nucleus-in-a-box",
            ItemStack.with(Items.copper, 8000, Items.lead, 8000, Items.silicon, 5000, Items.thorium, 4000)
        );

        package12 = new PackageShopItem("repair-kit",
                ItemStack.with(Items.copper, 110, Items.lead, 120, Items.silicon, 40)
        );

        package13 = new PackageShopItem("spore-kit",
                ItemStack.with(Items.sporePod, 69 * 420)
        );

        /* Runnables */
        firstAid = new PurchaseRunnable("first-aid", 23299){{
            purchased = e -> {
                e.team.cores().each(c -> c.health = c.maxHealth);
                return true;
            };

            unlocked = e -> {
                boolean[] ret = new boolean[]{true};

                e.team.cores().each(c -> {
                    if(c.health == c.maxHealth || isNaN(c.health)) ret[0] = false;
                });

                return ret[0];
            };
        }};

        invincibleCore = new PurchaseRunnable("invi-core", 35799){{
            purchased = e -> {
                e.team.cores().each(c -> c.health = Float.NaN);

                Time.run(60f * 10f, () -> {
                    e.team.cores().each(c -> c.health = c.maxHealth);
                });
                return true;
            };

            unlocked = e -> {
                boolean[] ret = new boolean[]{true};

                e.team.cores().each(c -> {
                    if(isNaN(c.health)) ret[0] = false;
                });

                return ret[0];
            };
        }};

        milk = new PurchaseRunnable("milk", 75){
            @Override
            public boolean purchase(Building source, Unit player){
                if(player == null || player.dead()) return false;
                player.clearStatuses();
                return true;
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
                    if(description != null){
                        tt.row();
                        tt.add(description).growX().left().color(Color.gray);
                    }
                    tt.row();
                    tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
                }).growX();
            }
        };

        coffee = new PurchaseDrink("coffee", 75, MindyStatusEffects.caffeinated);
        herbTea = new PurchaseDrink("herb-tea", 125, MindyStatusEffects.herbed);
        flowerTea = new PurchaseDrink("flower-tea", 300, MindyStatusEffects.blossoming){{
            duration = 60 * 60 * 3;
        }};

        pancake = new PurchaseDrink("pancake", 100, MindyStatusEffects.absorbing){{
            duration = 60 * 60 * 1.5f;
            drinkSound = MindySounds.boxOpen;
        }};
        glowstick = new PurchaseDrink("glowstick", 140, MindyStatusEffects.glowing){{
            drinkSound = MindySounds.shatter;
        }};
        sporeJuice = new PurchaseDrink("spore-juice", 40, MindyStatusEffects.sporeSlimed){{
            duration = 60 * 60 * 5;
        }};
        bittriumWine = new PurchaseDrink("bittrium-wine", 599, MindyStatusEffects.bittriumBane){{
            duration = 60 * 60 * 4.5f;
        }};
        diodeCookie = new PurchaseDrink("diode-cookie", 325, MindyStatusEffects.forwardBiased){{
            duration = 60 * 60 * 1.5f;
            drinkSound = MindySounds.boxOpen;
        }};
        bossCake = new PurchaseDrink("boss-cake", 800, StatusEffects.boss){{
            drinkSound = MindySounds.boxOpen;
        }};

        cocktail = new PurchaseRunnable("cocktail", 330){
            final Seq<StatusEffect> statusList = new Seq<>();
            {
                Events.on(EventType.ClientLoadEvent.class, e -> {
                    statusList.addAll(Vars.content.statusEffects()).removeAll(se -> se.permanent || se == MindyStatusEffects.portal);
                });
                Events.on(EventType.ServerLoadEvent.class, e -> {
                    statusList.addAll(Vars.content.statusEffects()).removeAll(se -> se.permanent || se == MindyStatusEffects.portal);
                });
            }

            @Override
            public boolean purchase(Building source, Unit player){
                if(player == null || player.dead()) return false;
                player.apply(statusList.random(MindyStatusEffects.ouch), 60 * 60 * 5);
                player.apply(statusList.random(MindyStatusEffects.ouch), 60 * 60 * 5);
                player.apply(statusList.random(MindyStatusEffects.ouch), 60 * 60 * 5);
                return true;
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
                    tt.add(localizedName).growX().left().color(Pal2.zeta);
                    tt.row();
                    tt.table(b -> {
                        b.left();
                        b.button("@purchase.cocktail.description", new TextureRegionDrawable(MindyStatusEffects.glitched.uiIcon), Styles.cleart, 25f, () -> {
                            ui.content.show(statusList.random(MindyStatusEffects.ouch));
                        }).left().size(180f, 27f);
                        b.add(" [lightgray](5:00)[]");
                    }).left();
                    tt.row();
                    tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
                }).growX();
            }
        };

        holyRouter = new BlockItem(Blocks.router, 15);

        anucoins2000 = new PurchaseCoin(2000, 29);
        anucoins4200 = new PurchaseCoin(4200, 49);
        anucoins12000 = new PurchaseCoin(12000, 129);
    }
}
