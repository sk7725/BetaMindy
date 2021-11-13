package betamindy.content;

import arc.*;
import arc.graphics.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.type.item.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.type.*;

public class MindyLiquids implements ContentList {
    public static Liquid coffee, siloxol, tensorflow;

    @Override
    public void load(){
        coffee = new Liquid("coffee", Pal2.coffee){{
            effect = MindyStatusEffects.caffeinated;
            temperature = 0.6f;
            viscosity = 0.3f;
        }};

        siloxol = new AnimatedLiquid("siloxol", Color.white.cpy().a(0.5f)){{ //Alcohol + Siloxane
            temperature = Liquids.water.temperature;
            viscosity = Liquids.water.viscosity;
        }};

        if(!Vars.headless){
            for(Liquid i : Vars.content.liquids()){
                if(i instanceof AnimatedLiquid al){
                    Events.run(EventType.Trigger.update, al::update);
                }
            }
        }
    }
}
