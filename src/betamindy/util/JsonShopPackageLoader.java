package betamindy.util;

import arc.files.*;
import arc.util.*;
import arc.util.serialization.*;
import betamindy.content.*;
import betamindy.type.*;
import betamindy.type.shop.*;
import betamindy.world.blocks.storage.*;
import mindustry.ctype.*;
import mindustry.io.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class JsonShopPackageLoader {
    public void loadPackages() {
        mods.list().each(e -> {
            if(!e.isJava()) {
                Fi path = e.root.child("content");
                if(path != null) path = path.child("shopItems");
                final Fi finalPath = path;

                if(path != null){
                    path.walk(c -> {
                        if(c.extEquals("json")/* ||c.extEquals("hjson")*/){
                            ShopPackageType pack;

                            /*if(c.extEquals("hjson")){
                                pack = JsonIO.json.fromJson(ShopPackageType.class, Jval.read(c.readString()).toString(Jval.Jformat.plain));
                            } else {*/
                                pack = JsonIO.json.fromJson(ShopPackageType.class, c.readString());
                            //}

                            String name = e.meta.name + "." + c.nameWithoutExtension();

                            if(pack != null){
                                String itemType = pack.itemType;
                                Shop shop = (Shop) MindyBlocks.extraShop;
                                ItemStack[] items = pack.items;

                                if(pack.shop != null) {
                                    try {
                                        shop = (Shop) content.block("betamindy-" + pack.shop);
                                    } catch(Exception ex){
                                        Log.err(e.meta.name + ": Shop \"" + pack.shop + "\" doesn't exist or can't be found. Defaulted to Extra Shop.");
                                    }
                                }

                                Fi codeFile = finalPath.child(pack.codePath + ".js");
                                String code = null;

                                if(codeFile.exists()){
                                    code = codeFile.readString();
                                    Log.info(code);
                                }

                                ItemType type = ItemType.valueOf(itemType);
                                UnlockableContent cItem = switch (type){
                                    case Block, BlockInv -> content.block(pack.item);
                                    case Liquid -> content.getByName(ContentType.liquid, pack.item);
                                    default -> null;
                                };

                                boolean abort = false;
                                if(pack.abort != null) abort = pack.abort;

                                PurchaseItem item = new JsonShopPackage().NewJsonShopPackage(name, type, items, pack.cost == null ? 69 : pack.cost, code, cItem, pack.amount, abort);

                                if(item instanceof PurchaseRunnable){
                                    Log.info("Runnables in json are not supported yet. Please wait until v7.");
                                } else {
                                    if (item != null && shop != null) {
                                        shop.jsonItems.add(item);
                                    } else {
                                        Log.err("Package error for package " + name);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
