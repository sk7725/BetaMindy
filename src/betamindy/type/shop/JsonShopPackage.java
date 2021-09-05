package betamindy.type.shop;

import arc.func.*;
import arc.util.Log;
import betamindy.type.*;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.Block;
//import rhino.*;

public class JsonShopPackage {
    public Boolf<Building> purchased, unlocked;

    public void invoke(Boolf<Building> purchased, Boolf<Building> unlocked){
        this.purchased = purchased;
        this.unlocked = unlocked;
    }

    public PurchaseItem NewJsonShopPackage(String name, ItemType itemType, ItemStack[] items, Integer cost, String code, UnlockableContent cItem, Integer amount, boolean iAbort){
        PurchaseItem item;

        //For later fixing and usage at stable v7
        /*if(code == null) return null;

        Context ctx = Context.enter();
        Scriptable scope = ctx.initStandardObjects();

        Object wrappedStock = Context.javaToJS(this, scope);
        ScriptableObject.putProperty(scope, "pkg", wrappedStock);

        ctx.evaluateString(scope, code.replace("\n", ""), "PackageScript", 1, null);

        Context.exit();

        item = new PurchaseRunnable(name, cost){{
            purchased = e -> this.purchased.get(e);

            unlocked = e -> this.unlocked.get(e);
        }};*/

        item = switch (itemType) {
            case Package -> new PackageShopItem(name, items){{abort = iAbort;}};
            case Runnable -> new PurchaseRunnable(null, 0){{abort = iAbort;}};
            case Block -> new BlockItem((Block) cItem, cost){{abort = iAbort;}};
            case BlockInv -> new PurchaseInvBlock((Block) cItem, cost, amount){{abort = iAbort;}};
            case Liquid -> new LiquidItem((Liquid) cItem, cost, amount){{abort = iAbort;}};
        };
        
        return item;
    }
}
