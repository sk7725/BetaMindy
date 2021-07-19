package betamindy.util;

import arc.struct.*;
import jdk.incubator.jpackage.internal.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;

import java.util.*;

public class UnitLib {
    public static final ObjectMap<UnitType, ItemStack[]> costs = new ObjectMap<UnitType, ItemStack[]>(33);
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
        Vars.content.units().each(UnitLib::initCost);
    }

    public static void initCost(UnitType u){
        if(costs.containsKey(u)) return;
        costs.put(u, calcCost(u));
    }

    public static ItemStack[] mergeArray(ItemStack[] first, ItemStack[] second){
        ItemStack[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        return both;
    }

    /** Recursively generates a buildCost for units */
    public static ItemStack[] calcCost(UnitType u){
        if(costs.containsKey(u)) return costs.get(u);
        for(Reconstructor b : recons){
            UnitType[] r = b.upgrades.find(u0 -> u0[1] == u);
            if(r != null){
                ItemStack[] cost = calcCost(r[0]);
                if(b.consumes.has(ConsumeType.item)){
                    return mergeArray(cost, b.consumes.getItem().items);
                }
                else return cost;
            }
        }

        for(UnitFactory b : factories){
            for(UnitFactory.UnitPlan plan : b.plans){
                if(plan.unit == u) return plan.requirements;
            }
        }
        return defaultStack;
    }
}
