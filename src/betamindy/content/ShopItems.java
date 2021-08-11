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
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;

import static java.lang.Float.*;
import static mindustry.Vars.ui;

public class ShopItems implements ContentList {
    public static PackageShopItem package1, package2, package3, package4, package5, package6, package7, package8, package9, package10;
    public static PurchaseItem firstAids, invincibleCore, milk, coffee, herbTea, flowerTea, pancake, glowstick, sporeJuice, cocktail;

    @Override
    public void load(){
        /* packages */
        package1 = new PackageShopItem("entry-package", 250){{
            packageItems = ItemStack.with(Items.copper, 100, Items.lead, 100);
        }};

        package2 = new PackageShopItem("boost-package", 30000){{
            packageItems = ItemStack.with(Items.silicon, 1000, Items.graphite, 1000);
        }};

        package2 = new PackageShopItem("ore-package", 200){{
            packageItems = ItemStack.with(MindyItems.scalarRaw, 100, MindyItems.tensorRaw, 100, MindyItems.vectorRaw, 100);
        }};

        package3 = new PackageShopItem("meltdown-package", 4000){{
            packageItems = ItemStack.with(Items.copper, 1200, Items.lead, 350, Items.graphite, 300, Items.silicon, 325, Items.surgeAlloy, 325);
        }};

        package4 = new PackageShopItem("mynamite-package", 5500){{
            packageItems = ItemStack.with(Items.thorium, 180, Items.blastCompound, 400);
        }};

        package5 = new PackageShopItem("pipes-package", 2000){{
            packageItems = ItemStack.with(Items.metaglass, 200, Items.graphite, 130);
        }};

        package6 = new PackageShopItem("unit-package", 8000){{
            packageItems = ItemStack.with(Items.copper, 500, Items.lead, 500, Items.silicon, 500);
        }};

        package7 = new PackageShopItem("spectre-in-a-box", 13500){{
            packageItems = ItemStack.with(Items.copper, 900, Items.graphite, 300, Items.thorium, 250, Items.plastanium, 175, Items.surgeAlloy, 250);
        }};

        package8 = new PackageShopItem("spectre-package", 115250){{
            packageItems = ItemStack.with(Items.copper, 9000, Items.graphite, 3000, Items.thorium, 2500, Items.plastanium, 1750, Items.surgeAlloy, 2500);
        }};

        package9 = new PackageShopItem("foundation-in-a-box", 41000){{
            packageItems = ItemStack.with(Items.copper, 3000, Items.lead, 3000, Items.silicon, 2000);
        }};

        package10 = new PackageShopItem("nucleus-in-a-box", 120250){{
            packageItems = ItemStack.with(Items.copper, 8000, Items.lead, 8000, Items.silicon, 5000, Items.thorium, 4000);
        }};

        /* Runnables */
        firstAids = new PurchaseRunnable("first-aids", 1500){{
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

        invincibleCore = new PurchaseRunnable("invi-core", 10250){{
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

        milk = new PurchaseRunnable("milk", 15){
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

        coffee = new PurchaseDrink("coffee", 15, MindyStatusEffects.caffeinated);
        herbTea = new PurchaseDrink("herb-tea", 25, MindyStatusEffects.herbed);
        flowerTea = new PurchaseDrink("flower-tea", 60, MindyStatusEffects.blossoming){{
            duration = 60 * 60 * 3;
        }};

        pancake = new PurchaseDrink("pancake", 20, MindyStatusEffects.absorbing){{
            duration = 60 * 60 * 1.5f;
        }};
        glowstick = new PurchaseDrink("glowstick", 28, MindyStatusEffects.glowing);
        sporeJuice = new PurchaseDrink("spore-juice", 10, MindyStatusEffects.sporeSlimed){{
            duration = 60 * 60 * 5;
        }};
        //todo bane of resolution drink

        cocktail = new PurchaseRunnable("cocktail", 18){
            final Seq<StatusEffect> statusList = Vars.content.statusEffects().copy().removeAll(se -> se.permanent);
            @Override
            public boolean purchase(Building source, Unit player){
                if(player == null || player.dead()) return false;
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
                        b.button("???", Styles.cleart, () -> {
                            ui.content.show(statusList.random(MindyStatusEffects.ouch));
                        }).left().size(180f, 27f);
                        b.add(" [lightgray](5:00)[]");
                    }).left();
                    tt.row();
                    tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
                }).growX();
            }
        };
    }
}
