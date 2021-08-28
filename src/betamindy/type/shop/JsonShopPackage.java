package betamindy.type.shop;

import arc.func.*;
import betamindy.type.*;
import mindustry.gen.*;
import mindustry.type.*;
import rhino.*;

public class JsonShopPackage {
    public Boolf<Building> purchased, unlocked;

    public void invoke(Boolf<Building> purchased, Boolf<Building> unlocked){
        this.purchased = purchased;
        this.unlocked = unlocked;
    }

    public PurchaseItem NewJsonShopPackage(String name, ItemType itemType, ItemStack[] items, int cost, String code){
        PurchaseItem item = null;
        if(itemType == ItemType.PackageShopItem){
            item = new PackageShopItem(name, items);
        } else if(itemType == ItemType.PurchaseRunnable){
            if(code == null) return null;

            Context ctx = Context.enter();
            Scriptable scope = ctx.initStandardObjects();

            Object wrappedStock = Context.javaToJS(this, scope);
            ScriptableObject.putProperty(scope, "pkg", wrappedStock);

            ctx.evaluateString(scope, code.replace("\n", ""), "PackageScript", 1, null);

            Context.exit();

            item = new PurchaseRunnable(name, cost){{
                purchased = e -> this.purchased.get(e);

                unlocked = e -> this.unlocked.get(e);
            }};
        }
        
        return item;
    }
}
