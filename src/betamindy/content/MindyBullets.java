package betamindy.content;

import betamindy.entities.bullet.*;
import mindustry.content.Fx;
import mindustry.ctype.ContentList;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.*;

public class MindyBullets implements ContentList {
    public static BulletType payBullet, payBulletBig;
    @Override
    public void load(){
        payBullet = new PayloadBullet(1.6f){{
            hitEffect = Fx.mineBig;
            despawnEffect = Fx.none;
            hitColor = Pal.engine;

            lifetime = 80f;
            trailSize = 6f;
            splashDamageRadius = 30f;
        }};

        payBulletBig = new PayloadBullet(3.2f){{
            hitEffect = Fx.mineHuge;
            despawnEffect = MindyFx.payShock;
            hitColor = Pal.lancerLaser;

            lifetime = 20f;
            trailSize = 8f;
            splashDamageRadius = 50f;
            hitShake = 2.5f;
            trailEffect = MindyFx.hyperTrail;
        }};
    }
}
