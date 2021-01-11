package betamindy.content;

import arc.graphics.Color;
import betamindy.entities.bullet.*;
import mindustry.content.Fx;
import mindustry.ctype.ContentList;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.*;

public class MindyBullets implements ContentList {
    public static BulletType payBullet, payBulletBig, homingPay, homingPayBig, glassPiece, glassPieceBig;
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

        homingPay = new HomingPayloadBullet(1.6f){{
            hitEffect = Fx.mineBig;
            despawnEffect = Fx.none;
            hitColor = Pal.engine;

            lifetime = 80f;
            trailSize = 6f;
            splashDamageRadius = 30f;

            homingPower = 0.03f;
            homingRange = 120f;
        }};

        homingPayBig = new HomingPayloadBullet(3.2f){{
            hitEffect = Fx.mineHuge;
            despawnEffect = MindyFx.payShock;
            hitColor = Pal.lancerLaser;

            lifetime = 20f;
            trailSize = 8f;
            splashDamageRadius = 50f;
            hitShake = 2.5f;
            trailEffect = MindyFx.hyperTrail;

            homingPower = 0.01f;
            homingRange = 120f;
        }};

        glassPiece = new GlassBullet(4f, 30f, "betamindy-glass"){{
            trailColor = Color.white;
            trailParam = 0.8f;
            trailChance = 0.04f;
            lifetime = 45f;
            hitEffect = Fx.none;
            width = 6f; height = 6f;

            despawnEffect = Fx.none;
        }};

        glassPieceBig = new GlassBullet(5f, 65f, "betamindy-glassbig"){{
            trailColor = Color.white;
            trailParam = 1.8f;
            trailChance = 0.04f;
            lifetime = 50f;
            hitEffect = Fx.none;
            width = 8f; height = 8f;

            despawnEffect = Fx.none;
        }};
    }
}
