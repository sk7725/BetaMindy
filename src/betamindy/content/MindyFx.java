package betamindy.content;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import betamindy.graphics.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Cicon;
import mindustry.world.Block;

import static arc.graphics.g2d.Draw.*;
//I do not want my fills and lines fighting, so no wildcad imports
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.renderer;

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
    }).layer(Layer.debris),

    breakPayload = new Effect(18f, e -> {
        randLenVectors(e.id, 12, e.rotation + e.fin() * 7f, (x, y) -> {
            color(Pal.remove, Color.gray, e.fin());
            Fill.square(e.x + x, e.y + y, e.fout() * 2.5f, 45f);
        });
    }),

    glassPoof = new Effect(30f, e -> {
        color(Color.white);
        randLenVectors(e.id, 3, e.fin() * 6f, (x, y) -> {
            Drawf.tri(e.x + x, e.y + y, 3f * e.fout(), 3f * e.fout(), Mathf.randomSeed(e.id, 360f));
        });
    }),

    glassPoofBig = new Effect(30f, e -> {
        color(Color.white);
        randLenVectors(e.id, 4, e.fin() * 9f, (x, y) -> {
            Drawf.tri(e.x + x, e.y + y, 5f * e.fout(), 5f * e.fout(), Mathf.randomSeed(e.id, 360f));
        });
    }),

    boostBlock = new Effect(20, e -> {
        color(Pal2.boostColor);
        stroke(e.fout() * 4f);
        Lines.square(e.x, e.y, e.fin() * 11.5f);
    }),

    boostFire = new Effect(50, e -> {
        float len = e.finpow() * 22f;
        float ang = e.rotation + 180f + Mathf.randomSeedRange(e.id, 30f);
        color(Pal2.boostColor, Pal.lightOrange, e.fin());
        Fill.circle(e.x + Angles.trnsx(ang, len), e.y + Angles.trnsy(ang, len), 2f * e.fout());
    }),

    starPoof = new Effect(50f, e -> {
        float rot = (e.data == null) ? 0f : (float)e.data;
        color(Color.white, e.fout());

        Draw.rect("betamindy-star", e.x, e.y, Draw.scl * Draw.xscl * e.rotation * (1f + e.fin() * 0.5f), Draw.scl * Draw.yscl * e.rotation * (1f + e.fin() * 0.5f), rot);
    }),

    starFade = new Effect(30f, e -> {
        float rot = (e.data == null) ? 0f : (float)e.data;
        color(Tmp.c1.set(e.color).shiftHue(Time.globalTime * 1.2f), e.fout() * 0.8f);

        rect("betamindy-star", e.x, e.y, scl * xscl * e.rotation, scl * yscl * e.rotation, rot);
    }).layer(Layer.space - 0.02f),

    smokeRise = new Effect(150f, 150f, e -> {
        color(Color.gray, Pal.darkishGray.cpy().a(0), e.fin());
        float size = 7 + e.fin()*8;
        rect("circle", e.x+e.fin()*26, e.y+e.fin() * 30, size, size);
    }),

    blueBumperBonk = new Effect(25f, 60f, e -> {
        color(Color.white, e.fout());
        Tmp.v1.trns(e.rotation, e.finpow() * 20f);
        rect("betamindy-bumper-blue", e.x + Tmp.v1.x, e.y + Tmp.v1.y);
    }).layer(Layer.blockOver - 0.01f),

    smallPuff = new Effect(15f, e -> {
       color(Color.white);
       Tmp.v1.trns(e.rotation, e.fin() * 7f);
       Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 1.5f);
    }),

    windHit = new Effect(8f, e -> {
        float size = (e.data == null) ? 8f : (float)e.data;
        float offset = Mathf.randomSeedRange(e.id, size) / 2.5f;
        Tmp.v1.trns(e.rotation, Math.abs(offset) * 0.5f + e.fin() * 8f + Mathf.randomSeed(e.id + 1) * size / 3f, offset);
        stroke(e.fout() * 1.2f, Color.white);
        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout());
    }),

    blockFalling = new Effect(60 * 8f, 600f, e -> {
        if(!(e.data instanceof Block)) return;
        float size = 8 * ((Block)e.data).size * (1 + e.fout());

        float xoff = (Mathf.randomSeed(e.id + 1) - 0.5f) * (Core.graphics.getWidth() / renderer.minScale());
        float yoff = (Mathf.randomSeed(e.id + 2) + 0.5f) * (Core.graphics.getHeight() / renderer.minScale());
        float x = e.x + xoff * e.fout() * 2.4f;
        float y = e.y + yoff * e.fout() * 2.4f;

        color();
        rect(((Block)e.data).icon(Cicon.medium), x, y, size, size, Time.time * 4f);//shar wtf
    }).layer(Layer.flyingUnit + 1f),

    omegaShine = new Effect(96f, e -> { //69 is too short :P
        color();
        rect("betamindy-omega-effect", e.x, e.y, 12f * e.fout(), 12f * e.fout(), Mathf.randomSeed(e.id) * 360f);
    }),

    powerDust = new Effect(45f, e -> {
        color(e.color, Color.white, e.fout());
        Fill.square(e.x + Mathf.range(e.fout()), e.y + Mathf.range(e.fout()), e.fout() * 1.5f, 45f);
    }),

    fire = new Effect(50f, e -> {
        FireColor.fset(e.data == null ? Items.coal : (Item)e.data, e.fin());

        randLenVectors(e.id, 2, 2f + e.fin() * 9f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.2f + e.fslope() * 1.5f);
        });

        color();
        Drawf.light(Team.derelict, e.x, e.y, 20f * e.fslope(), e.data == null ? Pal.lightFlame : FireColor.from((Item)e.data), 0.5f);
    }),

    bigFire = new Effect(60f, e -> {
        FireColor.fset(e.data == null ? Items.coal : (Item)e.data, e.fin());

        randLenVectors(e.id, 4, 2f + e.fin() * 11f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.5f + e.fslope() * 3f);
        });

        color();
        Drawf.light(Team.derelict, e.x, e.y, 40f * e.fslope(), e.data == null ? Pal.lightFlame : FireColor.from((Item)e.data), 0.5f);
    }),

    ballfire = new Effect(25f, e -> {
        FireColor.fset(e.data == null ? Items.coal : (Item)e.data, e.fin());

        randLenVectors(e.id, 2, 2f + e.fin() * 7f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.2f + e.fout() * 1.5f);
        });
    }),

    fireDust = new Effect(25f, e -> {
        FireColor.fset(e.data == null ? Items.coal : (Item)e.data, e.fin());

        randLenVectors(e.id, 3, 2f + e.fin() * 9f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.2f + e.fout() * 1.3f);
        });

        color();
        Drawf.light(Team.derelict, e.x, e.y, 18f * e.fout(), e.data == null ? Pal.lightFlame : FireColor.from((Item)e.data), 0.5f);
    }),

    bigFireDust = new Effect(30f, e -> {
        FireColor.fset(e.data == null ? Items.coal : (Item)e.data, e.fin());

        randLenVectors(e.id, 6, 2f + e.fin() * 11f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.5f + e.fout() * 2.5f);
        });

        color();
        Drawf.light(Team.derelict, e.x, e.y, 35f * e.fslope(), e.data == null ? Pal.lightFlame : FireColor.from((Item)e.data), 0.5f);
    }),

    question = new Effect(25f, e -> {
        color(e.color);
        rect("betamindy-question-effect", e.x, e.y, 7f * e.fslope(), 7f * e.fslope());
    }),

    forbidden = new Effect(60f, e -> {
        float f = Mathf.clamp(e.fout() * 3f - 2f);
        Tmp.v1.set(Mathf.range(2f), Mathf.range(2f)).scl(f).add(e.x, e.y);

        stroke(3f * e.fout(), e.color);
        lineAngleCenter(Tmp.v1.x, Tmp.v1.y, 45f, 16f);
        lineAngleCenter(Tmp.v1.x, Tmp.v1.y, -45f, 16f);
    }),

    snowflake = new Effect(30f, e -> {
        color(Color.white, e.fout(0.4f));
        stroke(Mathf.randomSeed(e.id + 2) * 0.5f + 0.6f);
        float r = Mathf.randomSeed(e.id) * 360f + e.finpow() * 45f;
        float l = Mathf.randomSeed(e.id + 1) * 1.5f + 2.2f;
        for(int i = 0; i < 3; i++) lineAngleCenter(e.x, e.y, r + 60f * i, l);
    }),

    iceBurst = new Effect(45f, e -> {
        color(Pal2.ice, Color.white, e.fin());
        stroke(5f * (1f - e.finpow()));
        poly(e.x, e.y, 6, e.finpow() * 5f);

        stroke(e.fout() * 1.5f);
        randLenVectors(e.id, 3, 2f + 4f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 2.5f + 1f);
        });
    }),

    iceBurstBig = new Effect(55f, e -> {
        color(Pal2.ice, Color.white, e.fin());
        stroke(7f * (1f - e.finpow()));
        poly(e.x, e.y, 6, e.finpow() * 10f);

        stroke(e.fout() * 1.5f);
        randLenVectors(e.id, 6, 4f + 10f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5f + 1f);
        });
    }),

    energyDespawn = new Effect(35f, e -> {
        color(Color.white, Pal.lancerLaser, e.fin());
        stroke(e.fout() * 1.3f);
        Tmp.v1.trns(e.rotation + 180f, 8f).add(e.x, e.y);

        circle(Tmp.v1.x, Tmp.v1.y, e.finpow() * 14f);

        randLenVectors(e.id, 14, e.fin() * 25f, (x, y) -> {
            lineAngle(Tmp.v1.x + x, Tmp.v1.y + y, Mathf.angle(x, y), e.fout() * 6f + 1f);
        });
    }),

    thoriumDespawn = new Effect(35f, e -> {
        color(Color.white, Pal.thoriumPink, e.fin());
        stroke(e.fout() * 1.5f);
        Tmp.v1.trns(e.rotation + 180f, 8f).add(e.x, e.y);

        circle(Tmp.v1.x, Tmp.v1.y, e.finpow() * 17f);

        randLenVectors(e.id, 14, e.fin() * 27f, (x, y) -> {
            lineAngle(Tmp.v1.x + x, Tmp.v1.y + y, Mathf.angle(x, y), e.fout() * 6f + 1f);
        });
    });
}
