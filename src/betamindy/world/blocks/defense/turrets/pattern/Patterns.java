package betamindy.world.blocks.defense.turrets.pattern;

import betamindy.content.*;
import mindustry.content.*;

public class Patterns {
    public static TurretPattern
            starBlazing = new TurretPattern("star-blazing", MindyBullets.bigStar) {{
        chargeType = MindyBullets.biggerStar;
        chargeDuration = 20;
        reloadTime = 6f;
    }},

    chaosBuster = new ChaosBusterPattern("chaos-buster") {{
        shootType = Bullets.standardThorium;
        chargeType = Bullets.fireball;//TODO: temp
        chargeDuration = 10;
        reloadTime = 90f;
    }};
}
