package betamindy.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.entities.bullet.*;
import betamindy.graphics.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.world;

public class MindyBullets{
    public static BulletType payBullet, payBulletBig, homingPay, homingPayBig, glassPiece, glassPieceBig, bigStar, smallStar, biggerStar, colorFireball, icyZone, icyZoneSmall, voidStar, starFlame, sequenceShot;

    public static void load(){
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

        glassPiece = new GlassBulletType(4f, 30f, "betamindy-glass"){{
            trailColor = Color.white;
            trailParam = 0.8f;
            trailChance = 0.04f;
            lifetime = 45f;
            hitEffect = Fx.none;
            width = 6f; height = 6f;

            despawnEffect = Fx.none;
        }};

        glassPieceBig = new GlassBulletType(5f, 65f, "betamindy-glassbig"){{
            trailColor = Color.white;
            trailParam = 1.8f;
            trailChance = 0.04f;
            lifetime = 50f;
            hitEffect = Fx.none;
            width = 8f; height = 8f;

            despawnEffect = Fx.none;
        }};

        smallStar = new BasicBulletType(3f, 60f, "betamindy-starsmall"){{
            frontColor = Color.white;
            backColor = Color.white;
            pierce = true;
            pierceCap = 10;
            hitEffect = Fx.none;
            despawnEffect = Fx.mineBig;
            lifetime = 80f;
            width = 16f;
            height = 16f;
            spin = 0.05f;
        }};

        bigStar = new FallingStar(2f, 360f){{
            splashDamageRadius = 60f;
            splashDamage = 360f;
            inaccuracy = 26f;
            fragBullet = smallStar;
            fragBullets = 7;

            trailEffect = Fx.none;
            hitShake = 3f;
            size = 250f;
            fallTime = 270f;
        }};

        biggerStar = new FallingStar(1.7f, 360f){{
            splashDamageRadius = 90f;
            splashDamage = 500f;
            inaccuracy = 20f;
            fragBullet = smallStar;
            fragBullets = 15;
            fragShots = 3;

            trailEffect = Fx.none;
            hitShake = 4f;
            size = 400f;
            fallTime = 650f;

            shiny = true;
        }};

        colorFireball = new BulletType(1f, 4){
            {
                pierce = true;
                collidesTiles = false;
                collides = false;
                drag = 0.03f;
                hitEffect = despawnEffect = Fx.none;
            }

            @Override
            public void init(Bullet b){
                b.vel.setLength(0.6f + Mathf.random(2f));
                if(!(b.data instanceof Item)) b.data(Items.coal);
            }

            @Override
            public void draw(Bullet b){
                FireColor.fset((Item)b.data, b.fin(), Color.gray);
                Fill.circle(b.x, b.y, 3f * b.fout());
                Draw.reset();
            }

            @Override
            public void update(Bullet b){
                if(Mathf.chance(0.04 * Time.delta)){
                    Tile tile = world.tileWorld(b.x, b.y);
                    if(tile != null){
                        Fires.create(tile);
                    }
                }

                if(Mathf.chance(0.1 * Time.delta)){
                    Fx.fireballsmoke.at(b.x, b.y);
                }

                if(Mathf.chance(0.1 * Time.delta)){
                    MindyFx.ballfire.at(b.x, b.y, 0f, b.data);
                }
            }
        };

        icyZone = new StatusBulletType(MindyStatusEffects.icy, 65f){{
            lifetime = 600f;
        }};

        icyZoneSmall = new StatusBulletType(MindyStatusEffects.icy, 25f){{
            lifetime = 300f;
        }};

        starFlame = new BulletType(3.35f, 30f){{
            ammoMultiplier = 3f;
            hitSize = 8f;
            lifetime = 18f;
            pierce = true;
            collidesAir = true;
            shootEffect = MindyFx.shootStarFlame;
            hitEffect = MindyFx.hitFlameStar;
            despawnEffect = Fx.none;
            keepVelocity = false;
            hittable = false;
        }};

        sequenceShot = new SequenceBulletType(1f, 14f){{
            lifetime = 120f;
            ammoMultiplier = 0.25f;
        }};

        voidStar = new BasicBulletType(){
            {
                collides = false;
                lifetime = 280;
                speed = 2;
                damage = 0;
                despawnEffect = MindyFx.voidStarDespawn;
                chargeEffect = MindyFx.astroCharge;
            }

            public float dist(Bullet b, float x, float y){
                return Mathf.sqrt((float)(Math.pow(Math.abs(b.x - x), 2f) + Math.pow(Math.abs(b.y - y), 2f)));
            }

            @Override
            public void draw(Bullet b){
                Draw.color(Color.white);
                Fill.circle(b.x, b.y, 16 + Mathf.sinDeg(b.fin() * 360));
                Drawm.spikeRing(b.x, b.y, 10, b.fin() * 240f, 16f + Mathf.sinDeg(b.fin() * 360f * 2f), 8f);

                Draw.color(Pal.surge);
                Fill.circle(b.x, b.y, 13);

                Draw.color(Color.black);
                Fill.circle(b.x, b.y, 12 + Mathf.sinDeg(b.fin() * 720));

                Draw.color(Color.white);
                Drawm.spikeRing(b.x, b.y, 10, b.fin() * 240f, 15f + Mathf.sinDeg(b.fin() * 360f * 2f), 6f, true);

                Draw.reset();
            }

            @Override
            public void update(Bullet b){
                Units.nearbyEnemies(b.team, b.x - 120f, b.y - 120f, 240f, 240f, e -> {
                    float dist = dist(b, e.x, e.y);
                    if(e.dead() || dist > 120f) return;

                    Lightning.create(b, Color.white, (120f - dist), b.x, b.y, Angles.angle(b.x, b.y, e.x, e.y), (int)(dist / 2f));
                });
            }
        };
    }
}
