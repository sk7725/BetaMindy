package betamindy.content;

import betamindy.graphics.*;
import mindustry.ctype.*;
import mindustry.type.*;

public class MindyLiquids implements ContentList {
    public static Liquid coffee, tensorflow; //todo animatedliquid

    @Override
    public void load(){
        coffee = new Liquid("coffee", Pal2.coffee){{
            effect = MindyStatusEffects.caffeinated;
            temperature = 0.6f;
            viscosity = 0.3f;
        }};
    }
}
