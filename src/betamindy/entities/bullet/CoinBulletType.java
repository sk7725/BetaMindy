package betamindy.entities.bullet;

import arc.graphics.*;
import betamindy.content.*;
import mindustry.graphics.*;

public class CoinBulletType extends InstantHitBulletType{
    public CoinBulletType(float damage, float pierceDamage, Color color){
        super(damage);
        this.pierceDamage = pierceDamage;
        hitColor = trailColor = color;
        cHitEffect = MindyFx.coinHit;
        cFailEffect = MindyFx.coinDespawn;
        //todo hitsound
    }

    public CoinBulletType(float damage){
        this(damage, damage, Pal.engine);
    }
}
