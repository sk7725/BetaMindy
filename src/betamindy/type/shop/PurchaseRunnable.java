package betamindy.type.shop;

import arc.func.*;
import betamindy.type.*;
import mindustry.gen.*;

public class PurchaseRunnable extends PurchaseItem {
    public Boolf<Building> purchased = e -> true;

    public PurchaseRunnable(String name, int cost){
        super(name, cost);
    }

    @Override
    public boolean purchase(Building source, Unit player){
        return purchased.get(source);
    }
}
