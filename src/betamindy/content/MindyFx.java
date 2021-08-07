package betamindy.content;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import betamindy.*;
import betamindy.graphics.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Cicon;
import mindustry.world.Block;

import static arc.graphics.g2d.Draw.*;
//I do not want my fills and lines fighting, so no wildcad imports
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static betamindy.graphics.Drawm.shard;
import static betamindy.graphics.Drawm.spark;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class MindyFx {
    private static final int[] vgld = {0}; //VERY_GOOD_LANGUAGE_DESIGN
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
        color(Color.gray, Tmp.c1.set(Pal.darkishGray).a(0f), e.fin());
        float size = 7f + e.fin()*8f;
        rect("circle", e.x+e.fin()*26f, e.y+e.fin() * 30f, size, size);
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
        Rand rand = new Rand((long) (e.id + Time.time * 10));
        float dx = rand.chance(0.5d) ? rand.random(-e.fout(), e.fout()) : -rand.random(-e.fout(), e.fout());
        float dy = rand.chance(0.5d) ? rand.random(-e.fout(), e.fout()) : -rand.random(-e.fout(), e.fout());
        Fill.square(e.x + dx, e.y + dy, e.fout() * 1.5f, 45f);
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
                Drawf.light(null, lines.get(i).x, lines.get(i).y, lines.get(i+1).x, lines.get(i+1).y, 40f, e.color, 0.9f);
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
                Drawf.light(null, lines.get(i).x, lines.get(i).y, lines.get(i+1).x, lines.get(i+1).y, 40f, e.color, 0.9f * e.fout());
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
        }
    }),

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
    });
}
