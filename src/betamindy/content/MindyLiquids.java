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

        siloxol = new AnimatedLiquid("siloxol", Pal2.siloxol.cpy().a(0.7f)){{ //Alcohol + Siloxane
            effect = MindyStatusEffects.starDrunk;
            temperature = Liquids.water.temperature;
            viscosity = 0.6f;
            heatCapacity = 0.2f;
            explosiveness = 0.8f;
            lightColor = Pal2.siloxol.cpy().a(0.8f);
            barColor = Pal2.siloxol;
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
