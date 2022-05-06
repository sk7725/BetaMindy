package betamindy.content;

import arc.*;
import arc.graphics.*;
import betamindy.graphics.*;
import betamindy.type.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.type.*;

public class MindyLiquids{
    public static Liquid coffee, siloxol, colloid, tensorflow;

    public static void load(){
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

        colloid = new AnimatedLiquid("colloid", Color.valueOf("ff96df")){{
            effect = MindyStatusEffects.starDrunk;
            temperature = 0.3f;
            viscosity = 0.2f;
            heatCapacity = 1.3f;
            explosiveness = 0.5f;
            lightColor = Color.valueOf("ff96df").a(0.8f);

            animDelay = 4f;
            sprites = 6;
            transition = 4;
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
