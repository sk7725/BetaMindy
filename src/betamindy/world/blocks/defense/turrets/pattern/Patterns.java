package betamindy.world.blocks.defense.turrets.pattern;

import arc.graphics.Color;
import betamindy.content.*;
import betamindy.entities.bullet.RainbowLaser;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;

public class Patterns {
    public static TurretPattern
    starBlazing = new TurretPattern("star-blazing", MindyBullets.bigStar){{
        chargeType = MindyBullets.biggerStar;
        chargeDuration = 20;
        reloadTime = 6f;
    }},

    chaosBuster = new ChaosBusterPattern("chaos-buster"){{
        shootType = new BasicBulletType(8f, 80){{
            hitSize = 5;
            width = 16f;
            height = 23f;
            shootEffect = Fx.shootBig;
            pierceCap = 2;
            pierceBuilding = true;
            knockback = 0.7f;
        }};
        chargeType = Bullets.fireball;//TODO: temp
        chargeDuration = 10;
        reloadTime = 90f;

        chargeType = new RainbowLaser(){{
            rainbowSpeed = 3f;

            length = 460 * 1.5f;
            damage = 560 * 1.5f;
            width = 75 * 1.25f;

            lifetime = 65 * 1.25f;

            largeHit = true;

            /* do i need to add healing?
            healPercent = 25f;
            collidesTeam = true;
            */
            sideAngle = 15f;
            sideWidth = 0f;
            sideLength = 0f;
            colors = new Color[]{Pal.heal.cpy().a(0.4f), Pal.heal, Pal.heal};
        }};
    }};
}
