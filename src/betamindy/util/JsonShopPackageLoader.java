package betamindy.util;

import arc.files.*;
import arc.util.*;
import betamindy.content.MindyBlocks;
import betamindy.type.*;
import betamindy.type.shop.*;
import betamindy.world.blocks.storage.*;
import mindustry.io.*;
import mindustry.type.ItemStack;
import rhino.JavaScriptException;

import static mindustry.Vars.*;

public class JsonShopPackageLoader {
    public void loadPackages(){
        mods.list().each(e -> {
            if(!e.isJava()) {
                Fi path = e.root.child("content");
                if(path != null) path = path.child("packages");
                final Fi finalPath = path;

                if(path != null){
                    path.walk(c -> {
                        if(c.extEquals("json")){
                            String name = e.meta.name + "." + c.nameWithoutExtension();
                            ShopPackageType pack = JsonIO.json.fromJson(ShopPackageType.class, c.readString());

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

                                PurchaseItem item = new JsonShopPackage().NewJsonShopPackage(name, ItemType.valueOf(itemType), items, pack.cost == null ? 69 : pack.cost, code);

                                if(item != null && shop != null){
                                    shop.purchases.add(item);
                                } else {
                                    Log.err("Package error for package " + name);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
