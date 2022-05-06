package betamindy.content;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import betamindy.*;
import betamindy.entities.bullet.*;
import betamindy.graphics.*;
import betamindy.world.blocks.logic.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.Block;

import java.util.*;

import static arc.graphics.g2d.Draw.*;
//I do not want my fills and lines fighting, so no wildcad imports
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.angle;
import static arc.math.Angles.randLenVectors;
import static betamindy.BetaMindy.hardmode;
import static betamindy.graphics.Drawm.shard;
import static betamindy.graphics.Drawm.spark;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class MindyFx {
    private static final int[] vgld = {0}; //VERY_GOOD_LANGUAGE_DESIGN
    static final Vec2[] vecs = new Vec2[]{new Vec2(), new Vec2(), new Vec2(), new Vec2()};
    private static final Rand rand = new Rand();

    public static final Effect
    directionalSmoke = new Effect(160f, e -> {
        Draw.z(Layer.flyingUnit + 0.1f);
        color(Pal.gray);
        alpha(e.fout());
        randLenVectors(e.id, 1, e.fin() * 40f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85f + e.fin() * 8.5f));
    }),

    //note: this was copy-pasted from old mindustry source, not sure why you need it but this seemed like the easiest solution -Anuke
    blockExplosion = new Effect(30, e -> {
        e.scaled(7, i -> {
            stroke(3.1f * i.fout());
            Lines.circle(e.x, e.y, 3f + i.fin() * 14f);
        });

        color(Color.gray);

        randLenVectors(e.id, 6, 2f + 19f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3f + 0.5f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout());
        });

        color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
        stroke(1.7f * e.fout());

        randLenVectors(e.id + 1, 9, 1f + 23f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f);
        });
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

    driftBlock = new Effect(35f, e -> {
        color(Pal.lancerLaser);
        stroke(e.fout() * 4f);
        Tmp.v1.trns(e.rotation, e.finpow() * 8f);
        Lines.square(Tmp.v1.x + e.x, Tmp.v1.y + e.y, e.fin() * 8f, e.finpow() * 135f);
    }),

    driftFire = new Effect(50, e -> {
        float len = e.finpow() * 22f;
        float ang = e.rotation + 180f + Mathf.randomSeedRange(e.id, 30f);
        color(e.color, Color.white, e.fin());
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
        color(Color.gray, Tmp.c2.set(Pal.darkishGray).a(0f), e.fin());
        float size = 7f + e.fin()*8f;
        rect("circle", e.x+e.fin()*26f, e.y+e.fin() * 30f, size, size);
    }),

    manualFire = new Effect(170f, e -> {
        color(e.color, e.fout());
        randLenVectors(e.id, e.id % 2 + 1, 1.5f + 4f * e.fin(), (x, y) -> {
            Fill.square(e.x + e.fin() * 13f + x, e.y + e.fin() * 9f + y, 0.2f + e.fout());
        });
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
        rect(((Block)e.data).fullIcon, x, y, size, size, Time.time * 4f);//shar wtf
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
        Drawf.light(e.x, e.y, 20f * e.fslope(), e.data == null ? Pal.lightFlame : FireColor.from((Item)e.data), 0.5f);
    }),

    bigFire = new Effect(60f, e -> {
        FireColor.fset(e.data == null ? Items.coal : (Item)e.data, e.fin());

        randLenVectors(e.id, 4, 2f + e.fin() * 11f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.5f + e.fslope() * 3f);
        });

        color();
        Drawf.light(e.x, e.y, 40f * e.fslope(), e.data == null ? Pal.lightFlame : FireColor.from((Item)e.data), 0.5f);
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
        Drawf.light(e.x, e.y, 18f * e.fout(), e.data == null ? Pal.lightFlame : FireColor.from((Item)e.data), 0.5f);
    }),

    bigFireDust = new Effect(30f, e -> {
        FireColor.fset(e.data == null ? Items.coal : (Item)e.data, e.fin());

        randLenVectors(e.id, 6, 2f + e.fin() * 11f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.5f + e.fout() * 2.5f);
        });

        color();
        Drawf.light(e.x, e.y, 35f * e.fslope(), e.data == null ? Pal.lightFlame : FireColor.from((Item)e.data), 0.5f);
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
    }),

    suckSmoke = new Effect(12f, e -> {
        color();
        alpha(e.finpow());
        randLenVectors(e.id, 1, e.fout() * 13f + 6f, e.rotation, 40f, (x, y) -> {
            Fill.square(e.x + x, e.y + y, e.fout() * 2f, 45);
        });
    }),

    mineHugeButHuger = new Effect(50f, e -> {
        stroke(e.fout(), Color.white);
        circle(e.x, e.y, 8f * e.fin());

        randLenVectors(e.id, 12, 7f + e.fin() * 7f, (x, y) -> {
            color(e.color, Color.lightGray, e.fin());
            Fill.square(e.x + x, e.y + y, e.fout() * 2.5f + 0.5f, 45);
        });
    }),

    pipePop = new Effect(40f, e -> {
        randLenVectors(e.id, 10, 8f + e.fin() * 18f, e.rotation, 15f, (x, y) -> {
            color(e.color, Color.lightGray, e.fin());
            Fill.square(e.x + x, e.y + y, e.fout() * 2f + 0.5f, 45);
        });
    }),

    bigBoiPipePop = new Effect(50f, e -> {
        stroke(e.fout(), Color.white);
        circle(e.x, e.y, 8f + 16f * e.finpow());

        randLenVectors(e.id, 12, 7f + e.fin() * 24f, e.rotation, 60f, (x, y) -> {
            color(e.color, Color.lightGray, e.fin());
            Fill.square(e.x + x, e.y + y, e.fout() * 2.5f + 1f, 45);
        });
    }),

    ideologied = new Effect(18f, e -> {
        color(Pal.accent, Pal.remove, e.fin());
        Fill.square(e.x, e.y, 1.5f * e.fout(), 45f);
    }),

    soundwaveHit = new Effect(20f, e -> {
        stroke(e.fout() * 2f, e.color);
        circle(e.x, e.y, e.fslope() * 15f);
        circle(e.x, e.y, e.finpow() * 15f);
    }),

    prePoof = new Effect(60f, e -> {
        if(!(e.data instanceof TextureRegion)) return;
        Tmp.v1.trns(e.rotation, e.finpow() * 65f).add(e.x, e.y);
        color();
        boolean b = Time.globalTime % 30f > 15f;
        z(b ? Layer.effect : Layer.effect + 1f);
        mixcol(Color.white, b ? 1f : 0f);
        Draw.rect((TextureRegion) e.data, Tmp.v1.x, Tmp.v1.y, e.rotation + 90f);
        mixcol();
    }),

    poof = new Effect(70f, e -> {
        color(Time.globalTime % 30f > 15f ? Color.white : e.color);
        float r = (e.rotation - 2f + 4f * e.fin()) * e.fout(0.6f);
        for(int i = 0; i < 8; i++){
            Tmp.v1.trns(i * 45f - e.finpow() * 50f, e.finpow() * 40f).add(e.x, e.y);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, r);
        }
    }),

    sparkle = new Effect(55f, e -> {
        color(e.color);
        vgld[0] = 0;
        Angles.randLenVectors(e.id, e.id % 3 + 1, 8f, (x, y) -> {
            vgld[0]++;
            spark(e.x+x, e.y+y, e.fout()*2.5f, 0.5f+e.fout(), e.id * vgld[0]);
        });
    }),

    sparkleZeta = new Effect(60f, e -> {
        color(Pal2.zeta);
        Lines.stroke(e.fout());
        Angles.randLenVectors(e.id, e.id % 3 + 2, 7f + 5f * e.fin(), (x, y) -> {
            Lines.poly(e.x+x, e.y+y, 4, e.fin()*0.6f+0.45f);
        });
    }),

    sparkleCode = new Effect(60f, e -> {
        color(Pal2.source);
        vgld[0] = e.id;
        Angles.randLenVectors(e.id, 5, 7f + 5f * e.fin(), (x, y) -> {
            vgld[0]++;
            Drawm.drawBit(vgld[0] % 2 == 1, e.x+x, e.y+y, 1f, e.fout());
        });
    }),

    sparkleBittrium = new Effect(60f, e -> {
        color(Color.cyan, Color.pink, e.fin());
        vgld[0] = e.id;
        Angles.randLenVectors(e.id, 2, 7f + 5f * e.fin(), (x, y) -> {
            vgld[0]++;
            Drawm.drawBit(vgld[0] % 2 == 1, e.x+x, e.y+y, 1f, e.fout());
        });

        color(Color.cyan, Color.pink, e.fout());
        Lines.stroke(e.fout());
        Angles.randLenVectors(e.id - 1, e.id % 3 + 1, 7f + 5f * e.fin(), (x, y) -> {
            Lines.poly(e.x+x, e.y+y, 4, e.fin()*0.6f+0.45f);
        });
    }),

    sparkleSpace = new Effect(60f, e -> {
        vgld[0] = e.id;
        Lines.stroke(e.fslope());
        Angles.randLenVectors(e.id, 3, 7f + 5f * e.fin(), (x, y) -> {
            vgld[0]++;
            color(Tmp.c1.fromHsv((Mathf.randomSeed(vgld[0], -60f, 130f) + 360f) % 360f, 0.8f, 1f).a(1f));
            Lines.poly(e.x+x, e.y+y, 4, e.fslope()*0.7f+0.55f * Mathf.randomSeed(vgld[0]));
        });
    }),

    sparkleHit = new Effect(55f, e -> {
        color(e.color);
        vgld[0] = 0;
        Angles.randLenVectors(e.id, e.id % 2 + 1, 6f * e.fin() + 2f, (x, y) -> {
            vgld[0]++;
            spark(e.x+x, e.y+y, e.fout()*2.5f, 0.5f+e.fout(), e.id * vgld[0]);
        });
        e.scaled(18f, s -> {
            stroke(0.8f, e.color);
            Angles.randLenVectors(e.id, 3, 15f * s.fin() + 0.1f, (x, y) -> {
                lineAngle(e.x+x, e.y+y, angle(x, y), 5.4f * s.fout() + 0.01f);
            });
        });
    }),

    starSparkle = new Effect(35f, e -> {
        color(Color.white, Color.yellow, Mathf.randomSeed(e.id));
        vgld[0] = 0;
        Angles.randLenVectors(e.id, e.id % 3 == 0 ? 2 : 1, 8f, (x, y) -> {
            vgld[0]++;
            spark(e.x+x, e.y+y, e.fout()*3.5f, 0.8f+e.fout(), e.id * vgld[0]);
        });
    }),

    crystalBreak = new Effect(90f, e -> {
        e.scaled(25f, s -> {
            color(Color.white, e.color, s.finpow());
            vgld[0] = 0;
            randLenVectors(e.id, e.id % 4 + 4, 2f + 19f * s.finpow(), (x, y) -> {
                vgld[0]++;
                shard(e.x + x, e.y + y, 5f * s.fout() + Mathf.randomSeed(e.id + vgld[0], 8f), 4f * s.fout(), Mathf.angle(x, y));
            });
        });

        color(Color.white, e.color, e.fin());
        randLenVectors(e.id, 20, 4f + 13f * e.finpow(), 6f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 0.6f);
        });
    }),

    crystalBreakBittrium = new Effect(90f, e -> {
        e.scaled(25f, s -> {
            vgld[0] = 0;
            randLenVectors(e.id, e.id % 4 + 4, 2f + 19f * s.finpow(), (x, y) -> {
                vgld[0]++;
                color(Color.white, vgld[0] % 2 == 0 ? Color.cyan : Color.pink, s.finpow());
                shard(e.x + x, e.y + y, 5f * s.fout() + Mathf.randomSeed(e.id + vgld[0], 8f), 4f * s.fout(), Mathf.angle(x, y));
            });
        });

        vgld[0] = e.id;
        randLenVectors(e.id, 20, 4f + 13f * e.finpow(), 6f, (x, y) -> {
            vgld[0]++;
            color(Color.white, vgld[0] % 2 == 0 ? Color.cyan : Color.pink, e.fin());
            Fill.square(e.x + x, e.y + y, e.fout() * 0.6f, 0f);
        });
    }),

    crystalBreakSpace = new Effect(90f, e -> {
        e.scaled(25f, s -> {
            vgld[0] = e.id;
            randLenVectors(e.id, e.id % 4 + 4, 2f + 19f * s.finpow(), (x, y) -> {
                vgld[0]++;
                color(Tmp.c1.fromHsv((Mathf.randomSeed(vgld[0], -60f, 130f) + 360f) % 360f, 0.8f, 1f).a(1f));
                shard(e.x + x, e.y + y, 5f * s.fout() + Mathf.randomSeed(e.id + vgld[0], 8f), 4f * s.fout(), Mathf.angle(x, y));
            });
        });

        color();
        randLenVectors(e.id, 20, 4f + 13f * e.finpow(), 6f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 0.6f);
        });
    }),

    portalUnitDespawn = new Effect(40f, e -> {
        if(!(e.data instanceof TextureRegion)) return;
        color();
        mixcol(e.color, e.fin());
        alpha(e.fout(0.5f));
        Draw.rect((TextureRegion) e.data, e.x, e.y, e.rotation);
        mixcol();
    }),

    portalCoreKill = new Effect(90f, e -> {
        color(e.color, Color.white, e.fin());
        alpha(e.fout());
        Fill.square(e.x, e.y, e.rotation / 2f);
        alpha(1f);
        vgld[0] = e.id;
        Angles.randLenVectors(e.id, 5, 7f + 5f * e.fin(), (x, y) -> {
            vgld[0]++;
            Fill.square(e.x + x, e.y + y, e.fout() * (0.5f * Mathf.randomSeed(vgld[0]) * 0.3f) * e.rotation / 3f);
        });
    }),

    portalShockwave = new Effect(50f, 200f, e -> {
        color(Color.white, e.color, e.fin());
        stroke(e.fout() * 2f);
        circle(e.x, e.y, e.finpow() * 200f);
    }),

    portalSpawn = new Effect(20f, e -> {
        stroke(e.fout() * 2f);
        color(Color.white, e.color, e.fin());
        circle(e.x, e.y, e.fslope() * e.rotation);
        circle(e.x, e.y, e.finpow() * e.rotation);
    }),

    thickLightning = new Effect(12f, 1300f, e -> {
        if(!(e.data instanceof Seq)) return;
        Seq<Vec2> lines = e.data();
        int n = Mathf.clamp(1 + (int)(e.fin() * lines.size), 1, lines.size);
        for(int i = 2; i >= 0; i--){
            stroke(4.5f * (i / 2f + 1f));
            color(i == 0 ? Color.white : e.color);
            alpha(i == 2 ? 0.5f : 1f);

            beginLine();
            for(int j = 0; j < n; j++){
                linePoint(lines.get(j).x, lines.get(j).y);
            }
            endLine(false);
        }

        if(renderer.lights.enabled()){
            for(int i = 0; i < n - 1; i++){
                Drawf.light(lines.get(i).x, lines.get(i).y, lines.get(i+1).x, lines.get(i+1).y, 40f, e.color, 0.9f);
            }
        }
    }),

    thickLightningFade = new Effect(80f, 1300f, e -> {
        if(!(e.data instanceof Seq)) return;
        Seq<Vec2> lines = e.data();
        for(int i = 2; i >= 0; i--){
            stroke(4.5f * (i / 2f + 1f) * e.fout());
            color(i == 0 ? Color.white : e.color);
            alpha((i == 2 ? 0.5f : 1f) * e.fout());

            beginLine();
            for(Vec2 p : lines){
                linePoint(p.x, p.y);
            }
            endLine(false);
        }

        if(renderer.lights.enabled()){
            for(int i = 0; i < lines.size - 1; i++){
                Drawf.light(lines.get(i).x, lines.get(i).y, lines.get(i+1).x, lines.get(i+1).y, 40f, e.color, 0.9f * e.fout());
            }
        }
    }),

    thickLightningStrike = new Effect(80f, 100f, e -> {
        color(Color.white, e.color, e.fin());

        for(int i = 2; i >= 0; i--){
            float s = 4.5f * (i / 2f + 1f) * e.fout();
            color(i == 0 ? Color.white : e.color);
            alpha((i == 2 ? 0.5f : 1f) * e.fout());
            for(int j = 0; j < 3; j++){
                Drawf.tri(e.x, e.y, 2f * s, (s + 65f + Mathf.randomSeed(e.id - j, 95f)) * e.fout(), e.rotation + Mathf.randomSeedRange(e.id + j, 80f) + 180f);
            }
        }

        Draw.z(Layer.effect - 0.001f);
        stroke(3f * e.fout(), e.color);
        float r = 55f * e.finpow();
        Fill.light(e.x, e.y, circleVertices(r), r, Tmp.c4.set(e.color).a(0f), Tmp.c3.set(e.color).a(e.fout()));
        circle(e.x, e.y, r);
        if(renderer.lights.enabled()) Drawf.light(e.x, e.y, r * 3.5f, e.color, e.fout(0.5f));
    }).layer(Layer.effect + 0.001f),

    thickLightningHit = new Effect(80f, 100f, e -> {
        color(Color.white, e.color, e.fin());

        for(int i = 2; i >= 0; i--){
            float s = 4.5f * (i / 2f + 1f) * e.fout();
            color(i == 0 ? Color.white : e.color);
            alpha((i == 2 ? 0.5f : 1f) * e.fout());
            for(int j = 0; j < 6; j++){
                Drawf.tri(e.x, e.y, 2f * s, (s + 35f + Mathf.randomSeed(e.id - j, 95f)) * e.fout(), Mathf.randomSeedRange(e.id + j, 360f));
            }
        }

        Draw.z(Layer.effect - 0.001f);
        stroke(3f * e.fout(), e.color);
        float r = 55f * e.finpow();
        Fill.light(e.x, e.y, circleVertices(r), r, Tmp.c4.set(e.color).a(0f), Tmp.c3.set(e.color).a(e.fout()));
        circle(e.x, e.y, r);
        if(renderer.lights.enabled()) Drawf.light(e.x, e.y, r * 3.5f, e.color, e.fout(0.5f));
    }).layer(Layer.effect + 0.001f),

    lightningOrbCharge = new Effect(90f, e -> {
        color(e.color, Color.white, e.fin());
        stroke(5f * e.fin());
        circle(e.x, e.y, e.fout() * e.rotation * 5f);

        color();
        stroke(2.5f * e.fin());

        randLenVectors(e.id, 14, e.fout() * e.rotation * 3.5f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 10f);
        });
    }),

    lightningOrbDespawn = new Effect(120f, e -> {
        if(!(e.data instanceof Color)) return;
        Color color2 = e.data();
        Drawm.lightningOrb(e.x, e.y, e.rotation * e.fout(), e.color, color2);
    }),

    placeShine = new Effect(30f, e -> {
        color(e.color);
        stroke(e.fout());
        square(e.x, e.y, e.rotation / 2f + e.fin() * 3f);
        spark(e.x, e.y, 25f, 15f * e.fout(), e.finpow() * 90f);
    }),

    trailFade = new Effect(18f, e -> {
        if(!(e.data instanceof Trail)) return;
        Trail t = e.data();
        t.draw(e.color, e.rotation * e.fout());
    }),

    tarnationCharge = new Effect(130f, 180f, e -> {
        color(Pal.lancerLaser, Color.white, e.fin());
        stroke(5f * e.fin());
        circle(e.x, e.y, e.fout() * 15f * 5f);

        Drawm.lightningOrb(e.x, e.y, 10f * e.finpow(), Pal.lancerLaser, Pal.sapBullet);
    }),

    tarnationLines = new Effect(15f, e -> {
        color();
        stroke(2.5f * e.fin());

        randLenVectors(e.id, 5, e.fout() * 15f * 3.5f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 10f);
        });
    }),

    tarnationShoot = new Effect(40f, 240f, e -> {
        stroke(4f * e.fout(), Pal.sapBullet);
        circle(e.x, e.y, 10f + e.fin() * 40f);
        color(Pal.lancerLaser);
        Fill.circle(e.x, e.y, 10f * e.fout(0.5f));
        spark(e.x, e.y, e.finpow() * 40f + 28f, 18f * e.fout(), Mathf.randomSeed(e.id, 360f));
        color();
        Fill.circle(e.x, e.y, 10f * 0.8f * e.fout(0.5f));
        spark(e.x, e.y, e.finpow() * 40f + 20f, 12f * e.fout(), Mathf.randomSeed(e.id, 360f));
        Draw.blend(Blending.additive);
        Lines.stroke(1.5f * e.fout());
        Draw.color(Pal.lancerLaser);
        Lines.poly(e.x, e.y, Mathf.random(7) + 11, 10f * 1.8f + e.finpow() * 90f, Mathf.random(360f));
        Lines.stroke(e.fout());
        Draw.color(Pal.sapBullet);
        Lines.poly(e.x, e.y, Mathf.random(7) + 11, 10f * 2.2f + e.finpow() * 110f, Mathf.random(360f));
        Draw.color();
        Draw.blend();
    }),

    spike = new Effect(14f, e -> {
        color(Color.white, e.color, e.fin());
        Fill.square(e.x, e.y, 1.6f * e.fout(), e.rotation + 45f);
        Tmp.v1.trns(e.rotation, e.fin() * 10f).add(e.x, e.y);
        Fill.square(Tmp.v1.x, Tmp.v1.y, 1.6f * e.fout(), e.rotation + 45f);
    }),

    spikeBig = new Effect(18f, e -> {
        color(Color.white, e.color, e.fin());
        Fill.square(e.x, e.y, 3.2f * e.fout(), e.rotation + 45f);
        Tmp.v1.trns(e.rotation, e.fin() * 16f).add(e.x, e.y);
        Fill.square(Tmp.v1.x, Tmp.v1.y, 2.6f * e.fout(), e.rotation + 45f);
    }),

    zoneStart = new Effect(15f, e -> {
        Fill.light(e.x, e.y, circleVertices(e.rotation), e.rotation, Pal2.clearWhite, Tmp.c4.set(Color.white).a(e.fout()));
    }),

    altarDust = new Effect(45f, e -> {
        color(BetaMindy.hardmode.getRandomColor(Tmp.c2, e.id), Color.white, e.fin());
        Tmp.v1.trns(e.rotation + Mathf.sin(e.fin() * 10f), e.fin() * 18f).add(e.x, e.y);
        Fill.square(Tmp.v1.x, Tmp.v1.y, 0.8f * e.fout(), 45f);
    }),

    altarDustSmall = new Effect(30f, e -> {
        color(BetaMindy.hardmode.getRandomColor(Tmp.c2, e.id), Color.white, e.fin());
        Tmp.v1.trns(e.rotation + Mathf.sin(e.fin() * 10f), e.fin() * 16f).add(e.x, e.y);
        Fill.square(Tmp.v1.x, Tmp.v1.y, 0.5f * e.fout(), 45f);
    }),

    altarOrbDespawn = new Effect(60f, e -> {
        float f = e.fout() * e.rotation;
        Drawm.altarOrb(e.x, e.y, 7.5f, f);
    }),

    coreHeal = new Effect(100f, e -> {
        color(Pal.heal, Color.yellow, e.fin());
        float r = e.rotation * tilesize / 2f;
        stroke(e.fout() * 2f);
        square(e.x, e.y, r);

        stroke(1.5f);
        alpha(e.fout());
        randLenVectors(e.id, 14, r, (x, y) -> {
            lineAngleCenter(e.x + x, e.y + y + 10f * e.finpow(), 0f, 4.5f);
            lineAngleCenter(e.x + x, e.y + y + 10f * e.finpow(), 90f, 4.5f);
        });
    }),

    openBox = new Effect(70f, e -> {
        color(Color.white, e.fout(0.5f));
        float heat = e.fin();
        rect("betamindy-box-lid0", e.x + heat * 15f , e.y + heat * (0.7f - heat) * 27f, (heat + 1f) * 360f * Mathf.randomSeed(e.id));
        rect("betamindy-box-lid1", e.x + heat * -15f, e.y + heat * (0.7f - heat) * 27f, (heat + 1f) * 360f * Mathf.randomSeed(e.id+1));
    }).layer(Layer.turret),

    despawnBox = new Effect(60f, e -> {
        if((int)(Time.globalTime / 10f) % 2 == 0) return;
        if(e.data instanceof TextureRegion reg){
            rect(reg, e.x, e.y);
        }
        else{
            rect(MindyBlocks.box.region, e.x, e.y);
        }
    }).layer(Layer.blockOver),

    unitShinyTrail = new Effect(60f, e -> {
        if(e.data instanceof UnitType unit){
            float z = unit.flying ? (unit.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : unit.groundLayer + Mathf.clamp(unit.hitSize / 4000f, 0, 0.01f);
            z(z - 0.002f);
            blend(Blending.additive);
            mixcol(e.color, 1f);
            alpha(e.fout() * 0.7f);
            rect(unit.shadowRegion, e.x, e.y, e.rotation - 90);
            reset();
            blend();
            Drawf.light(e.x, e.y, unit.hitSize * 1.3f, e.color, e.fout());
            //Drawf.light(e.x, e.y, unit.shadowRegion, e.color, e.fout() * 0.7f);
        }
    }).followParent(false).rotWithParent(false),

    sparkTrail = new Effect(90f, e -> {
        blend(Blending.additive);
        color();
        alpha(e.fout());
        float r = Mathf.sin(17f, 3f);
        Drawm.spark(e.x, e.y, (6f - Math.abs(r)) * e.rotation * e.fout() / 8f, 0.25f * e.rotation * e.fout(), r * 15f);
        reset();
        blend();
    }),

    sparkTrailHigh = new Effect(90f, e -> {
        blend(Blending.additive);
        color();
        alpha(e.fout());
        float r = Mathf.sin(17f, 3f);
        spark(e.x, e.y, (6f - Math.abs(r)) * e.rotation * e.fout() / 8f, 0.25f * e.rotation * e.fout(), r * 15f);
        reset();
        blend();
    }).layer(Layer.flyingUnit),

    unitBittTrail = new Effect(90f, e -> {
        if(e.data instanceof UnitType unit){
            float z = unit.flying ? (unit.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : unit.groundLayer + Mathf.clamp(unit.hitSize / 4000f, 0, 0.01f);
            z(z - 0.002f);
            blend(Blending.additive);
            mixcol(Tmp.c1.set(Color.cyan).lerp(Color.pink, Mathf.absin(Time.time - e.time, 5f, 1f)), 1f);
            alpha(e.fout() * 0.7f);
            rect(unit.shadowRegion, e.x, e.y, e.rotation - 90);
            reset();
            blend();
            Drawf.light(e.x, e.y, unit.hitSize * 1.3f, Color.cyan, e.fout());
            //Drawf.light(e.x, e.y, unit.shadowRegion, Color.cyan, e.fout() * 0.7f);
        }
    }),

    herbSteam = new Effect(35f, e -> {
        color(MindyStatusEffects.herbed.color, Color.lightGray, 0.7f * e.fin() + 0.3f);

        randLenVectors(e.id, 2, 2f + e.fin() * 7f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.2f + e.fslope() * 1.5f);
        });
    }),

    cherrySteam = new Effect(35f, e -> {
        color(Color.pink, Color.lightGray, 0.7f * e.fin() + 0.3f);

        randLenVectors(e.id, 2, 2f + e.fin() * 7f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.2f + e.fslope() * 1.5f);
        });
    }),

    petals = new Effect(40f, e -> {
        color(Color.white, e.fout(0.5f));
        vgld[0] = e.id;
        randLenVectors(e.id, 3, 8f + e.fin() * 3f, (x, y) -> {
            vgld[0]++;
            Drawm.petal(e.x + x, e.y + y, 4f, Mathf.randomSeed(vgld[0], 360f), e.fout() * 150f * Mathf.randomSeed(-vgld[0], 0.5f, 2.5f));
        });
    }).layer(Layer.flyingUnit + 0.1f),

    perfume = new Effect(100f, e -> {
        color(e.color, 0.7f * e.fout());
        float r = e.rotation * e.finpow();
        stroke(Math.min(r, 10f * e.fout() + 7f));
        circle(e.x, e.y, r);
    }).layer(Layer.flyingUnitLow - 0.5f),

    pretty = new Effect(50f, e -> {
        vgld[0] = e.id;
        blend(Blending.additive);
        randLenVectors(e.id, e.id % 2 + 1, 10f, (x, y) -> {
            vgld[0]++;
            color(Tmp.c1.set(e.color).shiftHue(Mathf.randomSeed(vgld[0], 50f)), e.fout());
            Fill.circle(e.x + x, e.y + y, Mathf.randomSeed(-vgld[0], 3f, 5f));
        });
        blend();
    }).layer(Layer.flyingUnit + 0.2f),

    lightFade = new Effect(90f, 120f, e -> {
        float x = e.x;
        float y = e.y;
        if(e.data instanceof Unit u){
            x = u.x;
            y = u.y;
        }
        Drawf.light(x, y, e.rotation * e.fout(0.1f), e.color, e.fout(0.1f));
    }),

    empBlast = new Effect(25f, 200f, e -> {
        color(e.color, Color.white, e.fout());
        stroke(e.fout()*4.5f);
        int rand = Mathf.random(10, 15);
        poly(e.x, e.y, rand, e.fin()*e.rotation, e.fout()*300f);
        stroke(e.fout()*2f);
        poly(e.x, e.y, rand, e.fin()*e.rotation*0.85f, e.fout()*300f);
        Drawf.light(e.x, e.y, e.fin() * e.rotation * 1.5f, e.color, e.fout(0.9f));
    }),

    decay = new Effect(20f, e -> {
        vgld[0] = e.id;
        randLenVectors(e.id, 3, 2f + e.finpow() * 14f, (x, y) -> {
            vgld[0]++;
            colorl(Mathf.randomSeed(vgld[0], 0.3f, 0.7f));
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
        });
    }).layer(Layer.flyingUnit + 0.1f),

    undecay = new Effect(20f, e -> {
        vgld[0] = e.id;
        randLenVectors(e.id, 3, 2f + e.fout() * 14f, (x, y) -> {
            vgld[0]++;
            colorl(Mathf.randomSeed(vgld[0], 0.3f, 0.7f));
            color(getColor(), Pal.heal, e.fin());
            Fill.circle(e.x + x, e.y + y, e.fslope() * 1.5f);
        });
    }).layer(Layer.flyingUnit + 0.1f),

    slimeBreak = new Effect(23f, e -> {
        vgld[0] = e.id;
        randLenVectors(e.id, 6, 4f + e.finpow() * 15f, (x, y) -> {
            vgld[0]++;
            color(e.color);
            Fill.circle(e.x + x, e.y + y, e.fout() * (1f + 2.5f * Mathf.randomSeed(vgld[0])));
        });
    }).layer(Layer.shields),

    lineShot = new Effect(14f, 600f, e -> {
        if(!(e.data instanceof Position)) return;

        Position pos = e.data();

        color(e.color);
        stroke(0.8f * e.fout());
        line(e.x, e.y, pos.getX(), pos.getY());
    }).layer(Layer.bullet),

    coinHit = new Effect(25, e -> {
        e.scaled(14, s -> {
            color(e.color);
            stroke(s.fout() * 1.2f);

            randLenVectors(e.id, 5, s.finpow() * 12f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                lineAngle(e.x + x, e.y + y, ang, s.fout() * 4 + 1f);
            });
        });

        z(Layer.effect + 2f);
        color();
        alpha(e.fout(0.75f));
        Tmp.v1.trns(Mathf.randomSeed(e.id + 1, -15f, 15f) + e.rotation, 15f * e.finpow()).add(e.x, e.y);
        Drawm.coin(Tmp.v1.x, Tmp.v1.y, 3f, e.finpow() * 117f, e.finpow() * 180f);
        mixcol(Color.white, 1f);
        alpha(Mathf.clamp(e.fout() * 2f - 1f));
        Drawm.coinSimple(Tmp.v1.x, Tmp.v1.y, 3f, e.finpow() * 117f, e.finpow() * 180f);
        reset();
        Drawf.light(Tmp.v1.x, Tmp.v1.y, 3f, e.color, e.fout(0.75f));
    }),

    coinSuperHit = new Effect(35, e -> {
        color(Color.white, e.color, e.fin());
        stroke(e.fout() * 1.2f);
        circle(e.x, e.y, e.finpow() * 7f + 1f);
        color(e.color);

        randLenVectors(e.id, 7, e.finpow() * 15f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1f);
        });

        z(Layer.effect + 2f);

        randLenVectors(-e.id, 7, e.finpow() * 25f + 4f, e.rotation, 20f, (x, y) -> {
            color();
            alpha(e.fout(0.75f));
            Drawm.coin(e.x + x, e.y + y, 3f, e.finpow() * 117f, e.finpow() * 180f);
            mixcol(Color.white, 1f);
            alpha(Mathf.clamp(e.fout() * 2f - 1f));
            Drawm.coinSimple(e.x + x, e.y + y, 3f, e.finpow() * 117f, e.finpow() * 180f);
            reset();
            Drawf.light(e.x + x, e.y + y, 3f, e.color, e.fout(0.75f));
        });
    }),

    coinDespawn = new Effect(20, e -> {
        e.scaled(14f, s -> {
            color(e.color);

            randLenVectors(e.id, 4, s.finpow() * 5f, (x, y) -> {
                Fill.square(e.x + x, e.y + y, s.fout(), 45f);
            });
        });

        color();
        Tmp.v1.trns(e.rotation, 21f * e.fin()).add(e.x, e.y);
        mixcol(e.color, 1f);
        alpha(e.fout(0.4f));
        Drawm.coinSimple(Tmp.v1.x, Tmp.v1.y, 3f, e.rotation + 90f, e.finpow() * 180f);

        /*
        z(Layer.bullet - 2f);
        reset();
        alpha(e.fout(0.75f));
        Drawm.coin(Tmp.v1.x, Tmp.v1.y, 3f, e.rotation + 90f, e.finpow() * 180f);
        mixcol();
        Drawf.light(Tmp.v1.x, Tmp.v1.y, 3f, e.color, e.fout(0.75f));
        */
    }).layer(Layer.bullet),

    coins = new Effect(30f, e -> {
        color(Color.white, e.fout(0.5f));
        vgld[0] = e.id;
        randLenVectors(e.id, e.id % 3 + 2, 8f + e.fin() * 3f, (x, y) -> {
            vgld[0]++;
            Drawm.coin(e.x + x, e.y + y, 4f, Mathf.randomSeed(vgld[0], 360f), e.fout() * 150f * Mathf.randomSeed(-vgld[0], 0.5f, 2.5f));
        });
    }).layer(Layer.flyingUnit + 0.1f),

    astroCharge = new Effect(60f, e -> {
        Draw.color(Color.white);
        Fill.circle(e.x, e.y, 16f * e.fin());
        Drawm.spikeRing(e.x, e.y, 10, e.fin() * 240f, 16f * e.fin(), 8f);

        Draw.color(Pal.surge);
        Fill.circle(e.x, e.y, 13f * e.fin());

        Draw.color(Color.black);
        Fill.circle(e.x, e.y, 12f * e.fin());

        Draw.color(Color.white);
        Drawm.spikeRing(e.x, e.y, 10, e.fin() * 240f, 15f * e.fin(), 6f, true);

        Draw.reset();
    }),

    voidStarDespawn = new Effect(60, e -> {
        Draw.color(Color.white);
        Fill.circle(e.x, e.y, 16 * e.fout());
        Drawm.spikeRing(e.x, e.y, 10, e.fin() * 240f, 16 * e.fout(), 8f);

        Draw.color(Pal.surge);
        Fill.circle(e.x, e.y, 13 * e.fout());

        Draw.color(Color.black);
        Fill.circle(e.x, e.y, 12 * e.fout());

        Draw.color(Color.white);
        Drawm.spikeRing(e.x, e.y, 10, e.fin() * 240f, 13 * e.fout(), 6f, true);

        Draw.reset();
    }).layer(Layer.bullet),

    sniperShoot = new Effect(50f, e -> {
        float r = Mathf.randomSeed(e.id, 360f);
        color(Pal.lancerLaser);
        spark(e.x, e.y, e.finpow() * 16f + 8f, 4f * e.fout(), r);
        stroke(1.5f * e.fout());
        circle(e.x, e.y, e.finpow() * 16f + 1f);
        color();
        spark(e.x, e.y, e.finpow() * 8f + 4f, 2f * e.fout(), r);
    }),

    buildLaser = new Effect(40f, e -> {
        if(e.data instanceof Unit u){
            float tx = e.x;
            float ty = e.y;

            Lines.stroke(1f, e.color);
            float focusLen = u.type.buildBeamOffset + Mathf.absin(Time.time, 3f, 0.6f);
            float px = u.x + Angles.trnsx(u.rotation, focusLen);
            float py = u.y + Angles.trnsy(u.rotation, focusLen);

            float sz = Vars.tilesize * e.rotation / 2f;
            float ang = u.angleTo(tx, ty);

            vecs[0].set(tx - sz, ty - sz);
            vecs[1].set(tx + sz, ty - sz);
            vecs[2].set(tx - sz, ty + sz);
            vecs[3].set(tx + sz, ty + sz);

            Arrays.sort(vecs, Structs.comparingFloat(vec -> -Angles.angleDist(u.angleTo(vec), ang)));

            Vec2 close = Geometry.findClosest(u.x, u.y, vecs);

            float x1 = vecs[0].x, y1 = vecs[0].y,
                    x2 = close.x, y2 = close.y,
                    x3 = vecs[1].x, y3 = vecs[1].y;

            Draw.alpha(e.fout());

            Fill.square(e.x, e.y, e.rotation * tilesize/2f);

            if(renderer.animateShields){
                if(close != vecs[0] && close != vecs[1]){
                    Fill.tri(px, py, x1, y1, x2, y2);
                    Fill.tri(px, py, x3, y3, x2, y2);
                }else{
                    Fill.tri(px, py, x1, y1, x3, y3);
                }
            }else{
                Lines.line(px, py, x1, y1);
                Lines.line(px, py, x3, y3);
            }

            Fill.square(px, py, 1.8f + Mathf.absin(Time.time, 2.2f, 1.1f), e.rotation + 45);

            Draw.reset();
        }
    }).layer(Layer.buildBeam),

    placeBlockBlue = new Effect(16, e -> {
        color(Pal.lancerLaser);
        stroke(3f - e.fin() * 2f);
        Lines.square(e.x, e.y, tilesize / 2f * e.rotation + e.fin() * 3f);
    }),

    razor = new Effect(25f, e -> {
        stroke(e.fout());
        color(Color.orange, Pal.lightishGray, e.fin());

        randLenVectors(e.id, 1 + e.id % 2, 14f * e.finpow(), e.rotation + 90f, 25f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3.5f);
        });
        randLenVectors(e.id, 2 - e.id % 2, 14f * e.finpow(), e.rotation - 90f, 25f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3.5f);
        });
    }),

    razorFast = new Effect(15f, e -> {
        stroke(e.fout());
        color(Color.orange, Pal.lightishGray, e.fin());

        randLenVectors(e.id, 1 + e.id % 2, 14f * e.finpow(), e.rotation + 90f, 25f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3.5f);
        });
        randLenVectors(e.id, 2 - e.id % 2, 14f * e.finpow(), e.rotation - 90f, 25f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3.5f);
        });
    }),

    portalWaveSmall = new Effect(50f, 100f, e -> {
        color(Color.white, BetaMindy.hardmode.color(), e.fin());
        stroke(e.fout() * 2f);
        circle(e.x, e.y, e.finpow() * 50f);
    }),

    teleportUnit = new Effect(20f, e -> {
        TextureRegion region = e.data instanceof UnitType u? u.fullIcon : UnitTypes.alpha.fullIcon;
        color();
        mixcol(e.color, 1f);
        rect(region, e.x, e.y, region.width * scl * xscl * e.fout(), region.height * scl * yscl * (1f + e.finpow() * 2f), e.rotation - 90f);
        mixcol();
    }),

    unitInPortal = new Effect(40f, e -> {
        TextureRegion region = e.data instanceof UnitType u? u.fullIcon : UnitTypes.alpha.fullIcon;
        color(Color.white, e.fout(0.3f));
        float f = e.fout() * 0.5f + 0.5f;
        rect(region, e.x, e.y, region.width * scl * xscl * f, region.height * scl * yscl * f, e.rotation - 90f);
    }).layer(Layer.groundUnit - 0.1f),

    unitOutPortal = new Effect(60f, e -> {
        if(e.data instanceof Unit unit){
            if(unit.type().flying && !unit.type.lowAltitude) z(Layer.flyingUnit + 1f);
            else z(Layer.effect + 0.0001f);
            color();
            mixcol(Tmp.c1.set(Color.white).lerp(hardmode.color(), e.fin()), 1f);
            alpha(e.fout(0.4f));
            rect(unit.icon(), unit.x, unit.y, unit.rotation - 90f);
            reset();
        }
    }),

    yutGyulGwaEffect = new Effect(350f, 800f, e -> {
        e.scaled(60f, s -> {
            color(Color.white, e.color, s.finpow());
            vgld[0] = 0;
            randLenVectors(e.id, e.id % 4 + 12, 4f + 49f * s.finpow(), (x, y) -> {
                vgld[0]++;
                spark(e.x + x, e.y + y, 8f * s.fout() + Mathf.randomSeed(e.id + vgld[0], 8f), 3.8f * s.fout(), Mathf.angle(x, y));
            });
        });

        if(e.data instanceof TextureRegion region){
            Draw.z(Layer.endPixeled + 0.01f);
            color(Color.white, e.fout(0.9f));
            float f = Math.max(0f, e.fout() * 8f - 7f);
            mixcol(Color.white, f);
            rect(region, e.x, e.y, region.width * scl * xscl * (1f + f * 2f), region.height * scl * yscl * (1 - f), 0f);
            mixcol();
        }
    }),

    terraBeam = new Effect(40f, 4000f, e -> {
        blend(Blending.additive);
        stroke(Mathf.randomSeed(e.id, 2f, 16f) * e.fout(), e.color);
        lineAngleCenter(e.x, e.y, e.rotation, 4000);
        blend();
    }),

    noteRipple = new Effect(20f, e -> {
        stroke(e.fout() * 1.1f, NotePlayer.noteColor((int)e.rotation));
        circle(e.x, e.y, 4.8f * e.finpow() + 1f);
    }),

    sequenceStarHit = new Effect(25f, e -> {
        float r = (e.data instanceof SequenceBulletType type) ? type.realRadius(e.rotation / 100f) : 9f;
        float a = Mathf.randomSeed(e.id, 360f);
        Color c = Drawm.starColor(e.rotation / 100f);
        stroke(e.fout() * r);
        Draw.color(Color.white, c, e.fin());
        Lines.circle(e.x, e.y, r - Lines.getStroke() / 2);
        for(int i = 0; i < 4; i++){
            Tmp.v1.trns(a + i * 90f, r - Lines.getStroke() / 2f - 0.1f).add(e.x, e.y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, 6, e.fout() * 25f, a + i * 90f);
        }
        Draw.z(Layer.bullet - 1);
        Draw.color(c, e.fout());
        Draw.blend(Blending.additive);
        Draw.rect("circle-shadow", e.x, e.y, r * 5f, r * 5f, 0);
        Draw.blend();
    }),

    sequenceShoot = new Effect(17f, e -> {
        float r = Mathf.randomSeed(e.id, 360f);
        color(Drawm.starColors[0]);
        randLenVectors(e.id, 3, e.finpow() * 17f + 2f, e.rotation, 30f, (x, y) -> {
            spark(e.x + x, e.y + y, e.fout() * 5f, 3.5f * e.fout() + 2f, r + Angles.angle(x, y));
        });

        stroke(4f * e.fout());
        circle(e.x, e.y, e.finpow() * 7.5f);
    }).layer(Layer.bullet - 0.01f),

    sequenceSmoke = new Effect(30f, e -> {
        color(Pal.lightishGray, Pal2.clearWhite, e.fin());
        alpha(0.6f * e.fout());
        vgld[0] = 0;
        randLenVectors(e.id, 10, e.finpow() * 17f + 6f, e.rotation, 110f, (x, y) -> {
            vgld[0]++;
            Fill.circle(e.x + x, e.y + y, (0.5f + e.fin()) * Mathf.randomSeed(e.id + vgld[0], 2f, 9f));
        });
    }).layer(Layer.bullet - 0.011f),

    shootStarFlame = new Effect(43f, 80f, e -> {
        randLenVectors(e.id, 8, e.finpow() * 63f, e.rotation, 11f, (x, y) -> {
            color(Drawm.starColor(Mathf.clamp(Mathf.len(x, y) / 63f)));
            Fill.circle(e.x + x, e.y + y, 0.15f + e.fout() * 1.6f);
        });

        z(Layer.effect + 1f);
        blend(Blending.additive);
        randLenVectors(e.id, 8, e.finpow() * 63f, e.rotation, 11f, (x, y) -> {
            float r = 0.65f + e.fout() * 1.6f;
            color(Drawm.starColor(Mathf.clamp(Mathf.len(x, y) / 63f)), e.fout());
            rect("circle-shadow", e.x + x, e.y + y, r * 4.4f, r * 4.4f, 0);
        });
        blend();
    }),

    hitFlameStar = new Effect(14, e -> {
        color(Color.white, Drawm.starColor(e.fin()), e.fin());
        stroke(0.5f + e.fout());

        randLenVectors(e.id, 2, 1f + e.fin() * 15f, e.rotation, 50f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f);
        });
    }),

    ionBurst = new Effect(20f, e -> {
        color(Color.white, e.color, e.fin());
        stroke(e.fout() * 3.5f);
        circle(e.x, e.y, e.rotation * e.finpow());
        stroke(e.fout() * 2f);
        randLenVectors(e.id, 12, 1f + e.fin() * e.rotation * 1.8f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 4f + 1f);
        });
    }),

    ionBurstSmall = new Effect(30f, e -> {
        color(Color.white, Pal2.deepBlue, e.fin());
        stroke(e.fout() * 3.5f);
        circle(e.x, e.y, 40f * e.finpow());
        stroke(e.fout() * 2f);
        randLenVectors(e.id, 12, 1f + e.fin() * 40f * 1.8f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 4f + 1f);
        });
    }),

    chainBreak = new Effect(50f, e -> {
        if(e.data instanceof TextureRegion region){
            blend(Blending.additive);
            color(Color.white, e.color, e.fin());
            alpha(e.fout(0.7f));
            randLenVectors(e.id, 1, 1f + e.finpow() * 20f, (x, y) -> {
                rect(region, e.x + x, e.y + y, e.rotation + Mathf.randomSeedRange(e.id * 17, 100f) * e.finpow());
            });
            blend();
        }
    }).layer(Layer.power),

    chainShatter = new Effect(65f, e -> {
        e.scaled(30f, s -> {
            z(Layer.flyingUnit);
            blend(Blending.additive);
            color(e.color, s.fout(0.7f));
            vgld[0] = 0;
            randLenVectors(e.id, e.id % 4 + 5, 2f + 16f * s.finpow(), (x, y) -> {
                vgld[0]++;
                shard(e.x + x, e.y + y, 3f + Mathf.randomSeed(e.id + vgld[0], 6f), 4f * s.fout(), Mathf.angle(x, y));
            });
            blend();
            z(Layer.effect);
        });

        color(Color.white, e.color, e.fin());
        randLenVectors(e.id + 1, 7, 3f + 9f * e.finpow(), (x, y) -> {
            float ang = Mathf.angle(x, y);
            Fill.square(e.x + x, e.y + y, e.fout() * 0.8f, 45f);
        });
    }),

    ionJet = new Effect(22f, e -> {
        blend(Blending.additive);
        color(e.color, Mathf.clamp(e.fin() * 8f) * e.fout(0.5f));
        Tmp.v1.trns(e.rotation, e.fin() * 13f);
        float s = (e.fout() + 1f) * Mathf.randomSeed(e.id, 3f, 9f);
        rect("circle-shadow", e.x + Tmp.v1.x, e.y + Tmp.v1.y, s, s);
        blend();
    }).layer(Layer.bullet - 2f),

    ionHit = new Effect(17f, e -> {
        blend(Blending.additive);
        color(e.color, Mathf.clamp(e.fin() * 8f) * e.fout());
        Tmp.v1.trns(e.rotation, e.fin() * 6f);
        float s = e.fout() + 2.1f;
        rect("circle-shadow", e.x + Tmp.v1.x, e.y + Tmp.v1.y, s, s);
        blend();
    }).layer(Layer.bullet - 2f),

    ionHitPayload = new Effect(17f, e -> {
        blend(Blending.additive);
        color(e.color, Mathf.clamp(e.fin() * 8f) * e.fout());
        Tmp.v1.trns(e.rotation, e.fin() * 11f);
        float s = (e.fout() + 1f) * Mathf.randomSeed(e.id, 3f, 7f);
        rect("circle-shadow", e.x + Tmp.v1.x, e.y + Tmp.v1.y, s, s);
        blend();
    }).layer(Layer.flyingUnit + 3f),

    ionBurn = new Effect(45, e -> {
        randLenVectors(e.id, 7, 3f + e.fin() * 5f, (x, y) -> {
            color(Color.white, Pal.accent, Color.gray, e.fin());
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.2f);
        });
    }),

    smolSquare = new Effect(25f, e -> {
        color(e.color);
        Fill.square(e.x, e.y, e.fout() * 1.3f + 0.01f, 45f);
    }),

    releaseSteam = new Effect(40f, e -> {
        color(Pal2.siloxol, Pal2.clearWhite, e.fin());
        alpha(e.fout());
        vgld[0] = 0;
        randLenVectors(e.id, 9, e.finpow() * 6f + 0.1f, (x, y) -> {
            vgld[0]++;
            Fill.circle(e.x + x, e.y + y, (0.2f + e.fin()) * Mathf.randomSeed(e.id + vgld[0], 1f, 6f));
        });
    }).layer(Layer.bullet - 0.011f),

    releaseSteamSmall = new Effect(40f, e -> {
        color(Pal2.siloxol, Pal2.clearWhite, e.fin());
        alpha(e.fout());
        vgld[0] = 0;
        randLenVectors(e.id, 6, e.finpow() * 4f + 0.1f, (x, y) -> {
            vgld[0]++;
            Fill.circle(e.x + x, e.y + y, (0.2f + e.fin()) * Mathf.randomSeed(e.id + vgld[0], 1f, 4f));
        });
    }).layer(Layer.bullet - 0.011f),

    impactChamberExplosion = new Effect(30, 300f, b -> {
        float intensity = 3f;
        float baseLifetime = 25f + intensity * 15f;
        b.lifetime = 50f + intensity * 64f;

        color(Pal.lighterOrange);
        alpha(0.8f);
        for(int i = 0; i < 5; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(0.25f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.8f * intensity), 15f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    float rad = fout * ((2f + intensity) * 1.35f);

                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 2.6f, Pal.lighterOrange, 0.7f);
                });
            });
        }

        b.scaled(baseLifetime, e -> {
            Draw.color();
            e.scaled(5 + intensity * 2f, i -> {
                stroke((3.1f + intensity/5f) * i.fout());
                Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
            });

            color(Color.white, Pal.lighterOrange, e.fin());
            stroke((2f * e.fout()));

            Draw.z(Layer.effect + 0.001f);
            randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 30f * intensity, (x, y, in, out) -> {
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
            });
        });
    }),

    scalarReactorExplosion = new Effect(30, 500f, b -> {
        float intensity = 6.8f;
        float baseLifetime = 25f + intensity * 11f;
        b.lifetime = 50f + intensity * 65f;

        alpha(0.7f);
        for(int i = 0; i < 4; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(0.4f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                color(Pal.lighterOrange, Pal2.scalar, Pal2.scalar2, e.fin());
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.9f * intensity), 22f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    float rad = fout * ((2f + intensity) * 2.35f);

                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 2.5f, Pal2.scalar, 0.5f);
                });
            });
        }

        b.scaled(baseLifetime, e -> {
            Draw.color();
            e.scaled(5 + intensity * 2f, i -> {
                stroke((3.1f + intensity/5f) * i.fout());
                Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
            });

            color(Pal.lighterOrange, Pal2.scalar, e.fin());
            stroke((2f * e.fout()));

            Draw.z(Layer.effect + 0.001f);
            randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 28f * intensity, (x, y, in, out) -> {
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
            });
        });
    });
}
