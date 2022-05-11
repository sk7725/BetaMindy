package betamindy.world.blocks.production.payduction.craft;

import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;

public class CRecipe {
    public ItemStack[] in;
    public ItemStack out;
    public int cycle = 1;

    public CRecipe(ItemStack[] in, ItemStack out, int c){
        this.in = in;
        this.out = out;
        cycle = c;
    }

    public CRecipe(ItemStack[] in, ItemStack out){
        this(in, out, 1);
    }

    public CRecipe(GenericCrafter b, int c){
        this(((ConsumeItems)b.findConsumer(cons -> cons instanceof ConsumeItems)).items, b.outputItem, c);
    }

    public CRecipe(Item item, int amount, int c){
        this.out = new ItemStack(item, amount);
        cycle = c;
    }

    public void craft(Building build, int c){
        if(c % cycle != cycle - 1 || build == null || build.items == null) return;
        if(build.items.has(in)){
            if(build instanceof CraftReact){
                ((CraftReact)build).craft(in, out);
                return;
            }
            int a = build.acceptStack(out.item, out.amount, build);
            if(a < out.amount) return;

            build.items.remove(in);
            build.items.add(out.item, out.amount);
        }
    }

    public CRecipe using(ItemStack[] stacks){
        this.in = stacks;
        return this;
    }
}
