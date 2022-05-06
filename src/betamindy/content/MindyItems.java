package betamindy.content;

import arc.*;
import arc.graphics.*;
import betamindy.graphics.*;
import betamindy.type.item.*;
import betamindy.ui.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class MindyItems{
    public static Item bittrium, scalarRaw, scalar, vectorRaw, vector, tensorRaw, tensor, source, cryonite, spaceMatter,
    //foreign items
    wood, tungsten, starStone, zinc, pixellium, bedrock;

    public static void load(){
        //given from portal invasions
        bittrium = new AnimatedItem("bittrium", Color.valueOf("00ffff")){
            {
                charge = 10.24f;
                radioactivity = 10.24f;
                cost = 0.1f;
                hardness = 15;

                transition = 5;
                animDelay = 4f;
            }

            @Override
            public String emoji(){
                return AnucoinTex.emojiBit;
            }
        };

        //used as freezing ammo
        cryonite = new Item("cryonite", Pal2.ice){{
            flammability = -1f;
            explosiveness = 1f;
            hardness = 2;
        }};

        scalarRaw = new Item("ore-scalar", Pal2.scalar){{
            flammability = 0.45f;
            hardness = 1;
        }};

        vectorRaw = new Item("ore-vector", Pal2.vector){{
            explosiveness = 0.33f;
            hardness = 3;
        }};

        tensorRaw = new Item("ore-tensor", Pal2.zeta){{
            hardness = 5;
        }};

        scalar = new Item("scalar", Color.valueOf("ec83af")){{
            flammability = 0.45f;
            cost = 2;
        }};

        vector = new Item("vector", Color.valueOf("4664f0")){{
            explosiveness = 0.33f;
            cost = 2;
        }};

        tensor = new AnimatedItem("tensor",Color.valueOf("00bfa2")){{
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

        /* Foreign items are not available in normal gameplay; They are there to prevent the shop-only items from getting built. */
        //used for almost all low-tier blocks
        wood = new ForeignItem("wood", Pal2.path){{
            flammability = 0.7f;
            cost = 1;
        }};
        //used for wires
        tungsten = new ForeignItem("tungsten", Pal.darkerGray){{
            cost = 2;
            hardness = 3;
        }};
        //used for glowing / decorative high-tier blocks
        //no longer foreign
        starStone = new AnimatedItem("star-stone", Color.yellow){
            {
                cost = 5;
                hardness = 4;
                radioactivity = 0.628f;
                sprites = 4;
                animDelay = 5f;
                transition = 10;
            }
        };
        //starStone.details = starStone.details == null ? Core.bundle.get("item.foreign") : Core.bundle.get("item.foreign") + "\n" + starStone.details;
        zinc = new ForeignItem("zinc", Color.valueOf("ded7c3")){{
            cost = 3;
            hardness = 4;
            charge = 0.5f;
            explosiveness = 0.3f;
        }};
        //used for environmental walls turned into blocks
        bedrock = new ForeignItem("bedrock", Pal.darkestGray){{
            cost = 10;
            hardness = 10;
        }};
        //used for gamersmod floorpapers sold at ancient stores
        pixellium = new ForeignItem("pixellium", Color.pink){{
            cost = 10;
            hardness = 64;
            charge = 10.24f;
        }};

        if(!Vars.headless){
            for(Item i : Vars.content.items()){
                if(i instanceof AnimatedItem){
                    Events.run(EventType.Trigger.update, ((AnimatedItem) i)::update);
                }
            }
        }
    }
}
