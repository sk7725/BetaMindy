package betamindy.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.*;
import static arc.math.Angles.*;

public class MindyFx {
    public static final Effect
    directionalSmoke = new Effect(160f, e -> {
        Draw.z(Layer.flyingUnit + 0.1f);
        color(Pal.gray);
        alpha(e.fout());
        randLenVectors(e.id, 1, e.fin() * 40f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85f + e.fin() * 8.5f));
    }),

    exoticDust = new Effect(20f, e -> {
        Draw.color(Color.white, (Mathf.randomSeed(e.id + 2) > 0.5f) ? Team.crux.color : Pal.sapBullet, e.fin());
        float i = (Mathf.randomSeed(e.id) > 0.5f) ? 1f : -1f;
        Fill.square(e.x + i * 3f + -1f * 6f * i * e.finpow(), e.y, 1.4f * e.fout() * Mathf.randomSeed(e.id + 1) + 0.6f, 45f);
    });
}
