package betamindy.world.blocks.production.payduction;

import arc.math.*;
import arc.struct.*;
import betamindy.world.blocks.production.payduction.craft.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;

public class PayloadFurnace extends PayloadFactory {
    public Seq<CRecipe> recipe = new Seq<CRecipe>();

    public PayloadFurnace(String name){
        super(name);
    }

    public CRecipe crafts(Item item, int amount, int cycle){
        recipe.add(new CRecipe(item, amount, cycle));
        return recipe.get(recipe.size - 1);
    }

    public void crafts(Object... items){
        for(int i = 0; i < items.length; i += 2){
            recipe.add(new CRecipe((GenericCrafter) items[i], ((Number)items[i + 1]).intValue()));
        }
    }

    @Override
    public float getFuelValue(Item item){
        return item.flammability;
    }

    @Override
    public float getFuelLerp(Item item){
        return Mathf.clamp(baseHeatLerp + item.flammability * 0.001f + item.explosiveness * 0.005f - 0.001f * minItemEfficiency, baseHeatLerp, 0.005f);
    }

    public class PayloadFurnaceBuild extends PayloadFactoryBuild {
        @Override
        public void craft(Building b){
            for(CRecipe rec : recipe){
                rec.craft(b, cycle);
            }
            cycle++;
        }
    }
}
