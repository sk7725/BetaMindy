package betamindy.content;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.type.item.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class MindyItems implements ContentList {
    public static Item bittrium, glennsthingy;

    public void load(){
        bittrium = new AnimatedItem("bittrium", Color.valueOf("00ffff")){{
            charge = 10.24f;
            radioactivity = 10.24f;
            cost = 0.1f;

            transition = 5;
            animDelay = 4f;
        }};

        /*
        glennsthingy = new AnimatedItem("archaic-debris", Color.valueOf("aaffff")){{
            charge = 0.4f;
            cost = 3f;

            sprites = 7;
            transition = 15;
            animDelay = 2f;
        }};
        */

        if(!Vars.headless){ //TODO animation setting
            for(Item i : Vars.content.items()){
                if(i instanceof AnimatedItem){
                    Events.run(EventType.Trigger.update, ((AnimatedItem) i)::update);
                }
            }
        }
    }
}
