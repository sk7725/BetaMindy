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
    public static Item bittrium, scalarRaw, scalar, vectorRaw, vector, tensorRaw, tensor, source, cryonite, spaceMatter;

    public void load(){
        bittrium = new AnimatedItem("bittrium", Color.valueOf("00ffff")){{
            charge = 10.24f;
            radioactivity = 10.24f;
            cost = 0.1f;
            hardness = 15;

            transition = 5;
            animDelay = 4f;
        }};

        cryonite = new Item("cryonite", Pal2.ice){{
            flammability = -1f;
            explosiveness = 1f;
            hardness = 2;
        }};

        scalarRaw = new Item("ore-scalar", Pal2.scalar){{
            flammability = 0.45f;
            hardness = 5;
        }};

        vectorRaw = new Item("ore-vector", Pal2.vector){{
            explosiveness = 0.33f;
            hardness = 6;
        }};

        tensorRaw = new Item("ore-tensor", Pal2.zeta){{
            hardness = 7;
        }};

        scalar = new Item("scalar", Color.valueOf("ec83af")){{
            flammability = 0.45f;
            cost = 2;
        }};

        vector = new Item("vector", Color.valueOf("4664f0")){{
            explosiveness = 0.33f;
            cost = 2;
        }};

        tensor = new AnimatedItem("tensor",Color.valueOf("7ef2cf")){{
            hardness = 8;
            cost = 3;

            animDelay = 5f;
            sprites = 5;
            transition = 30; //I hope this doesn't kill devices
        }};

        source = new RandomAnimatedItem("source", Color.valueOf("0ddd33")){{
            charge = 1f;
            radioactivity = 0.97f;
            cost = 4;
            hardness = 8;

            animDelay = 4f;
            sprites = 10;
        }};

        //todo animation
        spaceMatter = new Item("space-matter", Color.valueOf("7c067c")){{
            hardness = 9;
            cost = 4;
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

        if(!Vars.headless){
            for(Item i : Vars.content.items()){
                if(i instanceof AnimatedItem){
                    Events.run(EventType.Trigger.update, ((AnimatedItem) i)::update);
                }
            }
        }
    }
}
