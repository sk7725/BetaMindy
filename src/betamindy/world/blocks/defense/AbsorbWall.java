package betamindy.world.blocks.defense;

import arc.math.*;
import mindustry.entities.*;
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
            if(other.type.speed > 0.01f && other.team != team){ //dont mess with lasery stuff
                other.hit = true;
                other.type.despawnEffect.at(other.x, other.y, other.rotation(), other.type.hitColor);

                damage(other.damage);
                hit = 1f;

                //create lightning if necessary
                if(lightningChance > 0f){
                    if(Mathf.chance(lightningChance)){
                        Lightning.create(team, lightningColor, lightningDamage, x, y, other.rotation() + 180f, lightningLength);
                        lightningSound.at(tile, Mathf.random(0.9f, 1.1f));
                    }
                }
                other.remove();
                return false;
            }
            return super.collide(other);
        }
    }
}
