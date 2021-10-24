package betamindy.content;

import arc.graphics.*;
import betamindy.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;

public class MindyLiquids implements ContentList {
    public static Liquid coffee, siloxol, tensorflow; //todo animatedliquid

    @Override
    public void load(){
        coffee = new Liquid("coffee", Pal2.coffee){{
            effect = MindyStatusEffects.caffeinated;
            temperature = 0.6f;
            viscosity = 0.3f;
        }};

        siloxol = new Liquid("siloxol", Color.white.cpy().a(0.5f)){{ //Alcohol + Siloxane
            temperature = Liquids.water.temperature;
            viscosity = Liquids.water.viscosity;
        }};
    }
}
