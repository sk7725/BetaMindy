package betamindy.world.blocks.production;

import arc.audio.*;
import betamindy.content.*;
import betamindy.type.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.blocks.production.*;

public class DrinkCrafter extends Fracker {
    public Sound drinkSound = MindySounds.drink;

    public DrinkCrafter(String name){
        super(name);
        configurable = true;
        saveConfig = false;
    }

    public class DrinkCrafterBuild extends FrackerBuild {
        public boolean full(){
            return liquids.get(result) >= liquidCapacity - 0.1f;
        }

        @Override
        public boolean configTapped(){
            if(full()) configure(true);
            return false;
        }

        @Override
        public void configured(Unit builder, Object value){
            if(value instanceof Boolean){
                if((Boolean)value && builder != null && full()){
                    liquids.remove(result, liquids.get(result));
                    builder.apply(result.effect, 18000f);
                    drinkSound.at(builder);
                    Fx.bubble.at(x, y, result.color);
                }
            }
            else super.configured(builder, value);
        }
    }
}
