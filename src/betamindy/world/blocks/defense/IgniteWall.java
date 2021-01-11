package betamindy.world.blocks.defense;

import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;

public class IgniteWall extends Wall {
    public float fireLifetime = 12000f;

    public IgniteWall(String name){
        super(name);
    }

    public class IgniteWallBuild extends WallBuild {
        @Override
        public void onDestroyed(){
            tile.getLinkedTilesAs(block, this::burnTile);
            super.onDestroyed();
        }

        public void burnTile(Tile tile){
            if(tile == null || Vars.net.client()) return;
            Fires.create(tile);
            @Nullable Fire fire = Fires.get(tile.x, tile.y);
            if(fire != null) fire.lifetime = fireLifetime;
        }
    }
}
