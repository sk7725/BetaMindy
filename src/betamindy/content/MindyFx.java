package betamindy.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.*;
//I do not want my fills and lines fighting, so no wildcad imports
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;

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
    }),

    cannonShoot = new Effect(25f, e -> {
        color(Pal.engine);

        e.scaled(15f, e2 -> {
            stroke(e2.fout() * 4.1f);
            Lines.circle(e2.x, e2.y, 4f + e2.fin() * 23f);
        });

        stroke(e.fout() * 2.5f);

        randLenVectors(e.id, 18, 8f + 30f * e.finpow(), e.rotation, 130f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 6f + 1f);
        });
    }),

    cannonShoot2 = new Effect(25f, e -> {
        e.scaled(12f, b -> {
            color(Color.white, Pal.lancerLaser, b.fin());
            stroke(b.fout() * 5f + 0.2f);
            Lines.circle(b.x, b.y, b.fin() * 60f);
        });

        color(Pal.lancerLaser);

        for(int i : Mathf.signs){
            Drawf.tri(e.x, e.y, 12f * e.fout(), 95f, e.rotation + 90f * i);
            Drawf.tri(e.x, e.y, 12f * e.fout(), 60f, e.rotation + 20f * i);
            Drawf.tri(e.x, e.y, 8f * e.fout(), 40f, e.rotation + 155f * i);
        }

        stroke(e.fout() * 2.8f);

        randLenVectors(e.id, 18, 8f + 50f * e.finpow(), e.rotation, 100f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 8f + 1f);
        });
    }),

    cannonAccept = new Effect(18f, e -> {
        randLenVectors(e.id, 8, 4f + e.fin() * 12f, (x, y) -> {
            color(Pal.engine, Color.gray, e.fin());
            Fill.square(e.x + x, e.y + y, 1f + e.fout() * 2.5f, 45f);
        });
    }),

    hyperTrail = new Effect(60, e -> {
        color(e.color);
        Fill.circle(e.x, e.y, e.rotation * e.fout());
        color(Color.white, Pal.lancerLaser, e.fin());
        randLenVectors(e.id, e.id % 3, e.rotation / 1.5f + e.fin() * 16f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 2.5f);
        });
    }),

    payShock = new Effect(12, e -> {
        color(Color.white, Pal.lancerLaser, e.fin());
        stroke(e.fout() * 7f);
        Lines.poly(e.x, e.y, 12, 26f * e.fin());
    }).layer(Layer.debris);
}
