package betamindy.world.blocks.campaign;

import arc.graphics.*;
import arc.util.*;
import betamindy.world.blocks.defense.*;
import betamindy.world.blocks.defense.Campfire.*;
import mindustry.gen.*;
import mindustry.world.*;

//initiator
public class Altar extends Block {
    //can be overriden for higher tier. Note that it does not need to be 4.
    public int[][] torchOffset = {{3, 0}, {0, 3}, {-3, 0}, {0, -3}};
    public Color color1 = Color.pink;
    public Color color2 = Color.cyan;

    public Altar(String name){
        super(name);

        update = true;
        solid = false;
        rotate = false;
        size = 3;
    }

    public class AltarBuild extends Building{

        public @Nullable Tile torch(int i){
            if(i >= torchOffset.length) return null;
            Tile t = tile.nearby(torchOffset[i][0], torchOffset[i][1]);
            if(t == null || !(t.block() instanceof Campfire) || !((Campfire)t.block()).isTorch) return null;
            return t;
        }
    }
}
