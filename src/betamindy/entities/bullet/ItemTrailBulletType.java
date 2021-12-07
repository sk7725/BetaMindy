package betamindy.entities.bullet;

import arc.graphics.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.entities.bullet.*;
import mindustry.type.*;

public class ItemTrailBulletType extends BulletType {
    public ItemTrailBulletType(float damage, float speed, float range, Color trailColor){
        super(speed, damage);
        hitColor = this.trailColor = trailColor;
        lifetime = range / speed + 2f;
        trailWidth = 1.2f;
        trailLength = 6;
        pierce = true;
        hitEffect = MindyFx.sparkleHit;
        despawnEffect = MindyFx.sparkleHit;
    }
}
