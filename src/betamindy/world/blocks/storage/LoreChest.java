package betamindy.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import betamindy.graphics.*;

public class LoreChest extends Chest{
    public Color chainColor = Pal2.esoterum;
    public TextureRegion chain, chainEnd;

    public LoreChest(String name){
        super(name);
        canStore = false;
        slots = 15;
        capacity = 99;
    }


}
