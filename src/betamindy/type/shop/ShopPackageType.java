package betamindy.type.shop;

import mindustry.type.*;

import java.util.*;

public class ShopPackageType {
    public String itemType, shop;
    public String codePath;
    public Integer cost;
    public ItemStack[] items = {};

    @Override
    public String toString(){
        return "ShopPackageType{" +
            "itemType=" + itemType +
            ", cost=" + cost +
            ", shop=" + shop +
            ", items=" + Arrays.toString(items) +
        '}';
    }
}
