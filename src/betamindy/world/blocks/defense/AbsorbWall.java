package betamindy.world.blocks.defense;

import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.*;

public class AbsorbWall extends Wall {
    public AbsorbWall(String name){
        super(name);
        flashHit = true;
        flashColor = Pal.turretHeat;
    }

    public class AbsorbWallBuild extends WallBuild {
        @Override
        public boolean collide(Bullet other){
            if(other.type.speed > 0.01f && other.team != team && health > other.damage){ //dont mess with lasery stuff
                other.hit = true;
                other.type.despawnEffect.at(other.x, other.y, other.rotation(), other.type.hitColor);
                other.remove();

                damage(other.damage);
                return false;
            }
            return super.collide(other);
        }
    }
}
