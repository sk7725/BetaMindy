package betamindy.entities.bullet;

import betamindy.content.*;
import mindustry.graphics.*;

public class CoinBulletType extends InstantHitBulletType{
    public CoinBulletType(float damage){
        super(damage);
        hitColor = trailColor = Pal.engine;
        cHitEffect = MindyFx.coinHit;
        cFailEffect = MindyFx.coinDespawn;
        //todo hitsound
    }
}
