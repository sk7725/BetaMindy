package betamindy.type.shop;

import mindustry.type.*;

import java.util.*;

public class ShopPackageType {
    public String itemType, shop, item, codePath;
    public Integer cost, amount;
    public Boolean abort;
    public ItemStack[] items = {};

    @Override
    public String toString(){
        return "ShopPackageType{" +
            "itemType=" + itemType +
            "item=" + item +
            "codePath=" + codePath +
            ", cost=" + cost +
            ", amount=" + amount +
            ", abort=" + abort +
            ", shop=" + shop +
            ", items=" + Arrays.toString(items) +
        '}';
    }
}
