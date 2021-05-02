package betamindy.world.blocks.defense;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.world.blocks.production.payduction.craft.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.world.blocks.defense.*;

public class ShardWall extends Wall {
    public BulletType shard;
    public @Nullable Effect destroyEffect;
    public int amount = 7;
    public float inaccuracy = 15f, distRand = 3f, velRand = 0.1f;

    public ShardWall(String name){
        super(name);
    }

    public class ShardWallBuild extends WallBuild {
        @Override
        public void onDestroyed() {
            if(destroyEffect != null) destroyEffect.at(this);
            for(int i = 0; i < amount; i++){
                shard.create(this, this.team,x + Mathf.random(-distRand, distRand), y + Mathf.random(-distRand, distRand), 360f * i / (float)amount + Mathf.random(-inaccuracy, inaccuracy), 1f + Mathf.random(-velRand, velRand), 1f);
            }
            super.onDestroyed();
        }
    }
}
