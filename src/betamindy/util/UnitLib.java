package betamindy.util;

import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;

import java.util.*;

public class UnitLib {
    public static final ObjectMap<UnitType, ItemStack[]> costs = new ObjectMap<>(33);
    public static final ObjectIntMap<UnitType> tiers = new ObjectIntMap<>(33);
    public static UnitFactory[] factories;
    public static Reconstructor[] recons;
    public static final ItemStack[] defaultStack = {new ItemStack(Items.scrap, 25)};

    public static void init(){
        if(!costs.isEmpty()){
            Log.info("Unit costs are already initialized!");
            return;
        }
        Log.info("Unit cost start!");
        Seq<UnitFactory> facs = new Seq<UnitFactory>();
        Seq<Reconstructor> recs = new Seq<Reconstructor>();
        Vars.content.blocks().each((Block b) -> {
            if(b instanceof UnitFactory) facs.add((UnitFactory)b);
            else if(b instanceof Reconstructor) recs.add((Reconstructor)b);
        });
        factories = facs.toArray(UnitFactory.class);
        recons = recs.toArray(Reconstructor.class);
        Vars.content.units().each(UnitLib::calcCost);
    }

    public static ItemStack[] mergeArray(ItemStack[] first, ItemStack[] second){
        ItemStack[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        return both;
    }

    /** Debugging. */
    public static ItemStack[] printCost(UnitType u){
        ItemStack[] res = calcCost(u);
        Log.info("[accent]" + u.name + "[]");
        StringBuilder str = new StringBuilder();
        for(ItemStack i : res){
            if(i.item.emoji().equals("")){
                str.append(i.item.name);
            }
            else str.append(i.item.emoji());
            str.append(i.amount);
            str.append(" ");
        }
        str.append("[accent]<T");
        str.append(tiers.get(u));
        str.append(">[]");
        Log.info(str.toString());
        return res;
    }

    /** Recursively generates a buildCost for units */
    public static ItemStack[] calcCost(UnitType u){
        if(!tiers.containsKey(u)) tiers.put(u, 1);
        if(costs.containsKey(u)) return costs.get(u);
        for(Reconstructor b : recons){
            UnitType[] r = b.upgrades.find(u0 -> u0[1] == u);
            if(r != null){
                ItemStack[] cost = calcCost(r[0]);
                if(b.consumes.has(ConsumeType.item)){
                    cost = mergeArray(cost, b.consumes.getItem().items);
                }
                costs.put(u, cost);
                tiers.put(u, tiers.get(r[0]) + 1);
                return cost;
            }
        }

        for(UnitFactory b : factories){
            for(UnitFactory.UnitPlan plan : b.plans){
                if(plan.unit == u){
                    costs.put(u, plan.requirements);
                    return plan.requirements;
                }
            }
        }
        costs.put(u, defaultStack);
        return defaultStack;
    }
}
