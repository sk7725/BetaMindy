package betamindy.type.shop;

import mindustry.type.*;

import java.util.*;

public class ShopPackageType {
    public String itemType, shop, item, codePath;
    public Integer cost, amount;
    public ItemStack[] items = {};

    @Override
    public String toString(){
        return "ShopPackageType{" +
            "itemType=" + itemType +
            ", cost=" + cost +
            ", amount=" + amount +
            ", shop=" + shop +
            ", items=" + Arrays.toString(items) +
        '}';
    }
}
