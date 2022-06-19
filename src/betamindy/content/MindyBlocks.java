package betamindy.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import betamindy.entities.bullet.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.type.shop.*;
import betamindy.world.blocks.campaign.*;
import betamindy.world.blocks.defense.*;
import betamindy.world.blocks.defense.turrets.*;
import betamindy.world.blocks.defense.turrets.pattern.*;
import betamindy.world.blocks.distribution.*;
import betamindy.world.blocks.environment.*;
import betamindy.world.blocks.logic.*;
import betamindy.world.blocks.payloads.*;
import betamindy.world.blocks.power.*;
import betamindy.world.blocks.production.PayloadDeconstructor;
import betamindy.world.blocks.production.*;
import betamindy.world.blocks.production.payduction.*;
import betamindy.world.blocks.storage.*;
import betamindy.world.blocks.units.*;
import betamindy.world.draw.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.effect.*;
import mindustry.entities.pattern.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static betamindy.BetaMindy.*;
import static betamindy.content.ShopItems.*;
import static betamindy.util.BlockLib.*;
import static mindustry.type.ItemStack.*;

@SuppressWarnings("deprecation")
public class MindyBlocks{
    //environment
    public static Block radiation, exoticMatter, present, asphalt, blueice, ohno, omegaRune, crystalPyra, crystalCryo, crystalScalar, crystalVector, crystalTensor, crystalBittrium, crystalSpace,
    blackstone, blackstoneWall, redstone, redstoneWall, blackPine, redPine, largeTree, coffee,
            //env - shar
            borudalite, borudaliteWall, mossyBorudalite, twilightMoss, starryMoss, twilightMossWall, borudaliteDol, milksandBoulder, starBoulder, starTree, starPine, milksand, milkduneWall, starryWater, starryWaterDeep, starryMossWater, starrySandWater, starryBorudaliteWater,
            chosudalite, chosudaliteWall, bandalite, bandaliteWall, eclipseOxide, halfEclipseOxide, eclipseOxideDune,
            //lore - shar
            esotManual, esotPage1, esotPage2, fallenFlare, fallenAlpha, fallenMono, fallenHorizon, sentryGun, baseFloor1, baseFloor2, baseFloor3, baseFloor4, baseFloor5, baseGrid, labCryoFluid, esotChest, barrier,
    //ores
    oreScalar, oreVector, oreTensor, oreStarStone,
    //payloads
    payCannon, payCatapult, blockWorkshop, blockFactory, blockPacker, blockUnpacker, payDeconstructor, payDestroyer, payEradicator, grandConstructor, payloadPacker, payloadUnpacker, payloadRail, railSorter,
    //pistons
    piston, stickyPiston, pistonInfi, stickyPistonInfi, sporeSlime, sporeSlimeSided, surgeSlime, accel, cloner, spinner, spinnerInert, spinnerInfi, spinnerInertInfi, cog, titaniumCog, armoredCog, plastaniumCog, woodenCog, capacitor, megaCapacitor,
    //effect
    silo, warehouse, pressureContainer, altar, box, itemShop, unitShop, extraShop, anucoinNode, anucoinSafe, anucoinVault, tradingPost, coinSource, testShop, cafe, ancientStore, terraformer1, terraformer2, terraformer3, terraformer4, terraformerC, chest, largeChest, ionJet, ionThruster, corruptedJet,
    //walls
    leadWall, leadWallLarge, metaglassWall, metaglassWallLarge, siliconWall, siliconWallLarge, graphiteWall, graphiteWallLarge, coalWall, coalWallLarge, pyraWall, pyraWallLarge, blastWall, blastWallLarge, cryoWall, cryoWallLarge, heavyWall, teamWall, borderWall,
    spikeScrap, spikeSurge, spikePyra, spikeCryo, spikeClear, crusher, crusherPyra, crusherScalar,
    //drills
    drillMini, drillMega, mynamite, mynamiteLarge,
    //units
    boostPad, rejuvenator, bumper, bumperPlus, bumperBlue, fan, fanMega, clearPipe, clearDuct, claw, phaseClaw, driftPad, teleportPad, portalPad, yutnori,
    //logic
    linkPin, heatSink, heatFan, heatSinkLarge, messageVoid, messageSource, nullifier, pen, starPen, colorModule, strokeModule, noteBlock, starNoteBlock, sfxBlock, filterDome,
    //turrets
    hopeBringer, anchor, bermuda, propaganda, spear, justice, sting, ray, tarnation, magicTurret, credit, taxation, brokerage, mortgage, phisher, trojan, spyware, backdoor, masterkey,
            //campaign - shar
            nebula, sequence, giant, astro, dwarf, blackHole, spectrum,
    //power
    pressurePad, pressurePadLarge, button, buttonLarge, spotlight, buttonRouter, buttonDistributor, isotopeReactor,
    //crafting
    blockFurnace, heavyFurnace, gateSwitch, coffeeMachine,
            //campaign - shar
            arcKiln, electroRefiner, siliconCondenser, fusionChamber, lancerKiln, colloidMixer, scalarFurnace,
    //catalysts (pushreact & spinreact & boost)
    discharger, fireCan, campfire,
    //floorpapers
    floorRemover, metalGoldfloor, copperFloor, gamerSky, gamerGreens, gamerGrass, gamerWood, gamerIron, gamerLeaves, gamerWaterfall, gamerTrees, gamerBricks, routerFloor;

    public static void load() {
        radiation = new GlowPowder("radiation", 0){{
            color1 = Pal.lancerLaser;
            color2 = Pal.heal;

            status = MindyStatusEffects.radiation;
            effect = MindyFx.directionalSmoke;
        }};

        exoticMatter = new GlowPowder("exotic-matter", 1){{
            color1 = Team.crux.color;
            color2 = Pal.sapBullet;

            status = MindyStatusEffects.controlSwap;
            duration = 3000f;
        }};

        asphalt = new Floor("asphalt"){{
            variants = 3;
            speedMultiplier = 1.7f;
            dragMultiplier = 0.3f;
        }};

        blueice = new Floor("blueice"){{
            variants = 3;
            dragMultiplier = 0.00001f;

            attributes.set(Attribute.water, 0.9f);
        }};

        coffee = new Floor("milky-coffee"){{
            speedMultiplier = 1.7f;
            variants = 0;
            liquidDrop = MindyLiquids.coffee;
            liquidMultiplier = 0.5f;
            isLiquid = true;
            status = MindyStatusEffects.caffeinated;
            statusDuration = 120f;
            drownTime = 240f;
            cacheLayer = MindyShaders.coffeeLayer;
            albedo = 0.2f;
        }};

        ohno = new Floor("ohno"){{
            variants = 2;
            speedMultiplier = -1f;

            damageTaken = 10f;
            walkEffect = Fx.breakBlock;
            attributes.set(Attribute.light, 69420f / 5.4f);

            lightRadius = 16f;
            lightColor = Color.red;
            emitLight = true;
        }};

        blackstone = new Floor("blackstone"){{
            variants = 3;
            dragMultiplier = 2f;
        }};
        redstone = new Floor("redstone"){{
            variants = 3;
            dragMultiplier = 2f;
        }};

        blackstoneWall = new StaticWall("blackstone-wall"){{
            variants = 2;
        }};
        redstoneWall = new StaticWall("redstone-wall"){{
            variants = 2;
        }};

        blackPine = new StaticTree("black-pine"){{
            variants = 0;
        }};
        redPine = new StaticTree("red-pine"){{
            variants = 0;
        }};

        largeTree = new TreeBlock("large-tree");

        //planet shar (moon of serpulo)
        borudalite = new Floor("borudalite"){{
            variants = 13;
            albedo = 0.6f;
            attributes.set(MindyAttribute.metallic, 0.2f);
        }};
        mossyBorudalite = new Floor("mossy-borudalite"){{
            variants = 5;
            albedo = 0.5f;
            blendGroup = borudalite;
        }};
        bandalite = new Floor("bandalite"){{
            variants = 3;
            albedo = 0.5f;
            attributes.set(MindyAttribute.metallic, 0.5f);
        }};
        chosudalite = new Floor("chosudalite"){{
            variants = 3;
            albedo = 0.5f;
            attributes.set(MindyAttribute.metallic, 0.9f);
        }};

        twilightMoss = new Floor("twilight-moss"){{
            variants = 8;
            albedo = 0.2f;
        }};
        starryMoss = new Floor("starry-moss"){{
            variants = 4;
            emitLight = true;
            lightRadius = 34f;
            lightColor = Color.yellow.cpy().a(0.5f);
            albedo = 0.2f;
        }};

        milksand = new Floor("milksand"){{
            itemDrop = Items.sand; //change this?
            playerUnmineable = true;
            variants = 3;
            emitLight = true;
            lightRadius = 16f;
            lightColor = Color.sky.cpy().a(0.15f);
            albedo = 0.5f;
        }};

        halfEclipseOxide = new Floor("half-eclipse-oxide"){{
            variants = 2;
            albedo = 0.5f;
            attributes.set(MindyAttribute.magnetic, 0.4f);
        }};
        eclipseOxide = new Floor("eclipse-oxide"){{
            variants = 3;
            albedo = 0.5f;
            attributes.set(MindyAttribute.magnetic, 0.8f);
        }};

        borudaliteWall = new StaticWall("borudalite-wall"){{
            variants = 6;
            mossyBorudalite.asFloor().wall = halfEclipseOxide.asFloor().wall = this;
        }};
        bandaliteWall = new StaticWall("bandalite-wall"){{
            variants = 2;
        }};
        chosudaliteWall = new StaticWall("chosudalite-wall"){{
            variants = 2;
        }};
        twilightMossWall = new StaticWall("twilight-moss-wall"){{
            variants = 2;
        }};
        milkduneWall = new StaticWall("milkdune-wall"){{
            variants = 2;
            milksand.asFloor().wall = this;
        }};
        eclipseOxideDune = new StaticWall("eclipse-oxide-dune"){{
            variants = 2;
            eclipseOxide.asFloor().wall = this;
        }};

        starPine = new StaticTree("twilight-pine"){{
            variants = 0;//todo StarryTree class
        }};

        starryWaterDeep = new Floor("starry-water-deep"){{
            speedMultiplier = 0.5f;
            variants = 0;
            liquidDrop = MindyLiquids.siloxol;
            liquidMultiplier = 1.5f;
            isLiquid = true;
            status = StatusEffects.wet;
            statusDuration = 120f;
            drownTime = 140f;
            Log.info("[accent]<CONTENT>[]");
            cacheLayer = MindyShaders.starryLayer;
            albedo = 0.9f;
            emitLight = true;
            lightRadius = 18f;
            lightColor = Color.sky.cpy().a(0.7f);
        }};

        starryWater = new Floor("starry-water-shallow"){{
            speedMultiplier = 0.7f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = MindyLiquids.siloxol;
            isLiquid = true;
            cacheLayer = MindyShaders.starryLayer;
            albedo = 0.9f;
            emitLight = true;
            lightRadius = 18f;
            lightColor = Color.sky.cpy().a(0.6f);
        }};

        starryBorudaliteWater = new Floor("starry-borudalite-water"){{
            speedMultiplier = 0.7f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = MindyLiquids.siloxol;
            isLiquid = true;
            cacheLayer = MindyShaders.starryLayer;
            albedo = 0.9f;
            emitLight = true;
            lightRadius = 18f;
            lightColor = Color.sky.cpy().a(0.5f);
        }};

        starryMossWater = new Floor("starry-moss-water"){{
            speedMultiplier = 0.7f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = MindyLiquids.siloxol;
            isLiquid = true;
            cacheLayer = MindyShaders.starryLayer;
            albedo = 0.9f;
            emitLight = true;
            lightRadius = 18f;
            lightColor = Color.sky.cpy().a(0.5f);
        }};

        starrySandWater = new Floor("starry-sand-water"){{
            speedMultiplier = 0.7f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = MindyLiquids.siloxol;
            isLiquid = true;
            cacheLayer = MindyShaders.starryLayer;
            albedo = 0.9f;
            emitLight = true;
            lightRadius = 18f;
            lightColor = Color.sky.cpy().a(0.5f);
        }};

        borudaliteDol = new Prop("borudalite-dol"){{
            variants = 2;
            borudalite.asFloor().decoration = this;
        }};

        milksandBoulder = new Prop("milksand-boulder"){{
            variants = 2;
            milksand.asFloor().decoration = this;
        }};

        baseFloor1 = new Floor("base-floor-1", 0);
        baseFloor2 = new Floor("base-floor-2", 0);
        baseFloor3 = new Floor("base-floor-3", 0){{
            blendGroup = baseFloor1;
        }};
        baseFloor4 = new Floor("base-floor-4", 0){{
            blendGroup = baseFloor1;
        }};
        baseFloor5 = new Floor("base-floor-5", 0){{
            blendGroup = baseFloor2;
        }};
        baseGrid = new Floor("base-grid", 2){{
            blendGroup = baseFloor2;
        }};
        labCryoFluid = new Floor("lab-cryofluid"){{
            status = StatusEffects.freezing;
            statusDuration = 240f;
            speedMultiplier = 0.75f;
            variants = 0;
            liquidDrop = Liquids.cryofluid;
            liquidMultiplier = 0.5f;
            isLiquid = true;
            cacheLayer = CacheLayer.cryofluid;
            blendGroup = baseFloor2;

            emitLight = true;
            lightRadius = 25f;
            lightColor = Color.cyan.cpy().a(0.19f);
        }};
        //endregion

        //ores
        oreScalar = new OreBlock(MindyItems.scalarRaw){{
            oreDefault = false;
            oreThreshold = 0.841f;
            oreScale = 25.580953f;
        }};
        oreStarStone = new OreBlock(MindyItems.starStone){{
            oreDefault = false;
            oreThreshold = 0.93f;
            oreScale = 25.7f;
        }};
        oreVector = new OreBlock(MindyItems.vectorRaw){{
            oreDefault = false;
            oreThreshold = 0.894f;
            oreScale = 25.5813f;
        }};
        oreTensor = new OreBlock(MindyItems.tensorRaw){{
            oreDefault = false;
            oreThreshold = 0.91f;
            oreScale = 25.6628f;
        }};

        //turrets
        credit = new CoinTurret("credit"){{
            requirements(Category.turret, with(Items.lead, 20, Items.graphite, 20));

            reload = 5f;
            range = 112f;
            health = 220 * size * size;
            shootType = new CoinBulletType(7f);
            inaccuracy = 2f;
        }};

        taxation = new CoinTurret("taxation"){{
            requirements(Category.turret, with(Items.lead, 110, Items.graphite, 95, Items.titanium, 50));

            size = 2;
            reload = 45f;
            maxAmmo = 120;
            ammoPerShot = 2;
            range = 65f;
            health = 440 * size * size;

            shootType = new CoinBulletType(15f){{
                hitRange = 9f;
            }};
            inaccuracy = 1.8f;
            velocityRnd = 0.05f;
            shootSound = Sounds.shootBig;
            shake = 3f;
            shoot.shots = 15;
            shoot.shotDelay = 1f;
            shootY = 6.5f;
        }};

        brokerage = new CoinTurret("brokerage"){{
            requirements(Category.turret, with(Items.lead, 160, Items.metaglass, 100, Items.thorium, 70, Items.plastanium, 50));

            size = 2;
            maxAmmo = 180;
            ammoPerShot = 20;
            reload = 245f;
            minRange = 100f;
            hasMinRange = true;
            range = 404f;
            health = 220 * size * size;
            unitSort = (u, x, y) -> -u.maxHealth;

            shootType = new CoinBulletType(150f, 400f, Pal.lancerLaser){{
                cHitEffect = MindyFx.coinSuperHit;
                hitRange = 2f;
            }};
            inaccuracy = 0f;
            shootEffect = MindyFx.sniperShoot;
            shake = 4f;
            shootSound = Sounds.shotgun;
        }};

        anchor = new ItemTurret("anchor"){{
            requirements(Category.turret, with(Items.lead, 80, Items.graphite, 65, Items.titanium, 50));

            reload = 80f;
            shake = 3f;
            range = 160f;
            recoil = 4f;
            size = 2;
            targetAir = false;

            health = 220 * size * size;
            shootSound = Sounds.shotgun;
            placeableLiquid = true;

            ammo(
                    Items.titanium, new NavalBulletType(2.5f, 130f){{
                        drag = 0.0095f;
                        lifetime = 100f;
                        ammoMultiplier = 4f;
                        reloadMultiplier = 1.3f;
                    }},
                    Items.thorium, new NavalBulletType(2.5f, 190f){{
                        drag = 0.0095f;
                        lifetime = 100f;
                        ammoMultiplier = 5f;
                        width = 8.5f;
                        toColor = Pal.thoriumPink;
                        shootEffect = smokeEffect = Fx.thoriumShoot;
                        despawnEffect = MindyFx.thoriumDespawn;
                    }}
            );
        }};

        bermuda = new ItemTurret("bermuda"){{
            requirements(Category.turret, with(Items.lead, 220, Items.graphite, 160, Items.thorium, 105, Items.plastanium, 85));

            reload = 40f;
            shake = 3f;
            range = 230f;
            recoil = 5f;
            size = 3;
            targetAir = false;

            health = 240 * size * size;
            shootSound = Sounds.shotgun;
            placeableLiquid = true;

            ammo(
                    Items.titanium, new NavalBulletType(3.5f, 240f){{
                        length = 55f;
                        width = 8f;
                        lifetime = 120f;
                        ammoMultiplier = 4f;
                        reloadMultiplier = 1.3f;
                    }},
                    Items.thorium, new NavalBulletType(3.5f, 424f){{
                        length = 55f;
                        lifetime = 120f;
                        ammoMultiplier = 5f;
                        width = 10.5f;
                        toColor = Pal.thoriumPink;
                        shootEffect = smokeEffect = Fx.thoriumShoot;
                        despawnEffect = MindyFx.thoriumDespawn;
                    }}
            );
        }};

        propaganda = new ItemTurret("propaganda"){{
            requirements(Category.turret, with(Items.copper, 500, Items.graphite, 360, Items.metaglass, 65, Items.phaseFabric, 65));

            reload = 160f;
            range = 240f;
            recoil = 2f;
            size = 3;
            shoot.shots = 3;
            shoot.shotDelay = 10f;

            health = 140 * size * size;
            shootSound = Sounds.plasmadrop;
            heatColor = Pal.lancerLaser;
            shootY = 2f;
            coolant = consume(consumeCoolant(1f));
            coolantMultiplier = 0.4f;

            consumePower(18f);

            ammo(
                    Items.metaglass, new SoundwaveBulletType(4.5f, 60f, MindyStatusEffects.dissonance){{
                        fromColor = toColor = hitColor = Color.white;
                        lifetime = 60f;
                        ammoMultiplier = 4f;
                        reloadMultiplier = 2.5f;
                    }},
                    Items.phaseFabric, new SoundwaveBulletType(4.5f, 10f, MindyStatusEffects.controlSwap){{
                        fromColor = Items.phaseFabric.color;
                        toColor = hitColor = Pal.sapBullet;
                        lifetime = 90f;
                        ammoMultiplier = 3f;
                    }},
                    Items.surgeAlloy, new SoundwaveBulletType(4.5f, 5f, MindyStatusEffects.creativeShock){{
                        fromColor = hitColor = Pal.surge;
                        toColor = Color.orange;
                        lifetime = 110f;
                        ammoMultiplier = 5f;
                    }},
                    MindyItems.vector, new SoundwaveBulletType(4.2f, 30f, MindyStatusEffects.pause){{
                        statusDuration = 90f;
                        fromColor = hitColor = Pal2.vector;
                        toColor = Color.white;
                        lifetime = 60f;
                        ammoMultiplier = 6f;
                        reloadMultiplier = 0.45f;
                    }},
                    MindyItems.tensor, new SoundwaveBulletType(5f, 10f, MindyStatusEffects.amnesia){{
                        fromColor = MindyStatusEffects.amnesia.color;
                        toColor = hitColor = Pal2.zeta;
                        lifetime = 90f;
                        ammoMultiplier = 8f;
                    }},
                    MindyItems.source, new IdeologyBulletType(4f, 20f){{
                        fromColor = Pal.accent;
                        toColor = Pal2.source;
                        hitColor = Pal2.source;
                        lifetime = 50f;
                        ammoMultiplier = 8f;
                        reloadMultiplier = 0.65f;
                    }}
            );
        }};

        nebula = new LiquidTurret("nebula"){{
            requirements(Category.turret, with(Items.copper, 20, MindyItems.scalarRaw, 25));
            ammo(
                    MindyLiquids.siloxol, MindyBullets.starFlame
            );
            size = 1;
            reload = 5f;
            inaccuracy = 5f;
            shootCone = 50f;
            liquidCapacity = 10f;
            recoil = 0f;
            shootSound = Sounds.flame;
            shootEffect = MindyFx.shootStarFlame;
            range = 65f;
            health = 300;
            extinguish = false;
        }};

        spectrum = new ItemTurret("spectrum"){{
            requirements(Category.turret, with(Items.copper, 30, MindyItems.scalarRaw, 35, Items.silicon, 8));

            range = 130f; //must be set before ammo()
            ammo(
                    Items.copper, new ItemTrailBulletType(6, 4.5f, range, FireColor.fromMap.get(Items.copper)),
                    MindyItems.scalarRaw, new ItemTrailBulletType(9, 4f, range, Pal2.deepRed),
                    Items.titanium, new ItemTrailBulletType(20, 7f, range, Color.cyan){{
                        pierceCap = 4;
                        reloadMultiplier = 1.2f;
                    }},
                    MindyItems.scalar, new ItemTrailBulletType(29, 4f, range, Pal2.scalar){{
                        trailWidth = 1.4f;
                        pierceCap = 9;
                    }},
                    MindyItems.starStone, new ItemTrailBulletType(42, 17f, range, Pal.surge){{
                        trailWidth = 1f;
                        pierceCap = 18;
                        reloadMultiplier = 2f;
                    }}
            );
            health = 500;
            size = 1;
            reload = 15f;
            inaccuracy = 1.5f;
            shootCone = 30f;
            //shootSound = MindySounds.pewRetro; //this breaks the audio bus for some reason
        }};

        sequence = new LiquidTurret("sequence"){{
            requirements(Category.turret, with(Items.copper, 150, MindyItems.scalarRaw, 150, Items.metaglass, 40));
            ammo(
                    MindyLiquids.siloxol, MindyBullets.sequenceShot
            );
            size = 2;
            reload = 30f;
            inaccuracy = 5f;
            shootCone = 50f;
            liquidCapacity = 12f;
            shootEffect = MindyFx.sequenceShoot;
            smokeEffect = MindyFx.sequenceSmoke;
            range = 190f;
            health = 150 * size * size;
            extinguish = false;
            flags = EnumSet.of(BlockFlag.turret);
        }};

        ray = new RayTurret("ray"){{
            requirements(Category.turret, with(Items.copper, 320, Items.silicon, 255, Items.titanium, 150, MindyItems.scalar, 200));
            size = 3;
            range = 234f;
        }};

        astro = new UnitTurret("astro"){{
            requirements(Category.turret, with(Items.thorium, 12000, Items.silicon, 25500, Items.titanium, 8250, MindyItems.vector, 1000, MindyItems.tensor, 400));
            size = 6;
            shoot.firstShotDelay = 61f;
            shootType = MindyBullets.voidStar;

            consumePower(65f);
            rotateSpeed = 0.8f;
            heatColor = Color.valueOf("7474ed");
            range = 400f;
            reload = 600f;
            recoil = 10f;
            shootCone = 20f;
            shake = 5f;
            //TODO find a better sound: chargeSound = MindySounds.astroCharge;
            shootSound = MindySounds.astroShoot;
            targetAir = true;
            health = 240 * size * size;
        }};

        phisher = new HijackTurret("phisher"){{
            requirements(Category.turret, with(Items.silicon, 60, Items.titanium, 30, MindyItems.scalarRaw, 24, MindyItems.source, 12));
            size = 1;
            range = 145f;
            health = 120;
            reload = 10f;
            maxLinks = 4;
            powerUse = 4f;
            shotsMultiplier = 0.67f;

            coolant = consumeCoolant(0.1f);
        }};

        trojan = new HijackTurret("trojan"){{
            requirements(Category.turret, with(Items.silicon, 120, Items.thorium, 95, MindyItems.scalar, 50, MindyItems.source, 35));
            size = 2;
            range = 180f;
            health = 400;
            reload = 35f;

            shoot.shots = 3;
            shoot.shotDelay = 8f;

            inaccuracy = 2f;
            maxLinks = 5;
            linkRange = 7;
            powerUse = 18f;
            reloadMultiplier = 1f;
            shotsMultiplier = 0.3f;
            hijackReload = 0.3f;
            powerMultiplier = 1.2f;

            coolant = consumeCoolant(0.1f);
        }};

        spyware = new HijackTurret("spyware"){{
            requirements(Category.turret, with(Items.silicon, 420, Items.thorium, 290, Items.phaseFabric, 180, MindyItems.scalar, 150, MindyItems.source, 90));
            size = 3;
            range = 260f;
            health = 905;
            reload = 20f;
            shoot = new ShootSpread(){{
                shots = 5;
                spread = 9f;
            }};
            inaccuracy = 5f;
            maxLinks = 7;
            linkRange = 9;
            powerUse = 36f;
            reloadMultiplier = 1.5f;
            shotsMultiplier = 0.25f;
            hijackReload = 0.4f;
            chargeXRand = 0.01f;
            powerMultiplier = 1.2f;

            coolant = consumeCoolant(0.1f);
        }};

        backdoor = new HijackTurret("backdoor"){{
            requirements(Category.turret, with(Items.silicon, 640, Items.thorium, 550, Items.surgeAlloy, 350, MindyItems.scalar, 450, MindyItems.source, 200));
            size = 4;
            maxSize = 8;
            range = 390f;
            health = 1300;
            reload = 15f;
            inaccuracy = 1f;
            shoot = new ShootSpread(2, 3f);

            maxLinks = 9;
            linkRange = 12;
            powerUse = 80f;
            reloadMultiplier = 1.8f;
            shotsMultiplier = 1f;
            hijackReload = 0.6f;
            powerMultiplier = 2.5f;

            coolant = consumeCoolant(0.1f);
        }};

        masterkey = new HijackTurret("masterkey"){{
            requirements(Category.turret, BuildVisibility.sandboxOnly, with());
            size = 1;
            maxSize = 16;
            ignoreDrawSize = true;
            shoot = new ShootSpread(5, 5f);

            range = 360f;
            health = 2000;
            reload = 10f;
            maxLinks = 16;
            linkRange = 16;
            powerUse = 4f;
            reloadMultiplier = 2f;
            shotsMultiplier = 1f;
            lightColor = Pal.accent;
        }};

        //sting = new PowerTurret(I)

        payCannon = new PayloadTurret("payload-cannon"){{
            health = 2050;
            size = 5;
            recoil = 7f;
            range = 315f;
            shake = 2f;
            shootSound = Sounds.shootBig;
            shootEffect = MindyFx.cannonShoot;
            smokeEffect = Fx.shootBigSmoke2;
            safeRange = 140f;
            reload = 120f;
            rotateSpeed = 1.8f;
            maxPaySize = 3.5f;
            payloadOffset = 7f;
            shootY = 8f;
            consumePower(3.75f);
            requirements(Category.turret, with(Items.copper, 1000, Items.titanium, 750, Items.silicon, 450, Items.plastanium, 330));
        }};

        payCatapult = new PayloadTurret("payload-catapult"){{
            health = 3190;
            size = 7;
            recoil = 9f;
            range = 520f;
            shake = 4f;
            shootSound = Sounds.plasmaboom;
            shootEffect = MindyFx.cannonShoot2;
            smokeEffect = Fx.shootBigSmoke2;
            damage = 2.3f;
            maxDamagePercent = 0.75f;
            safeRange = 190f;
            reload = 250f;
            rotateSpeed = 1.3f;
            payloadOffset = 7.5f;
            shootY = 7f;
            payloadScale = 0.85f;
            maxPaySize = 14.5f; //I'm not adding a T3. ...definitely. ...definitely? ...definitely.
            consumePower(7.25f);
            shootType = MindyBullets.payBulletBig;
            homingShootType = MindyBullets.homingPayBig;
            requirements(Category.turret, with(Items.copper, 1500, Items.titanium, 900, Items.silicon, 650, Items.plastanium, 390, Items.phaseFabric, 180, Items.surgeAlloy, 165));
        }};

        payDeconstructor = new PayloadDeconstructor("payload-deconstructor"){{
            requirements(Category.units, with(Items.copper, 100, Items.titanium, 50, Items.silicon, 25));
            health = 80;
            size = 3;
            itemCapacity = 400;
            consumePower(1f);
        }};

        payEradicator = new PayloadDeconstructor("payload-eradicator"){{
            requirements(Category.units, with(Items.copper, 610, Items.titanium, 130, Items.silicon, 115, Items.phaseFabric, 35));
            health = 250;
            size = 7;
            maxPaySize = 10f;
            buildSpeed = 0.8f;
            itemCapacity = 1500;
            refundMultiplier = 2f;
            consumePower(2.35f);
            consumeLiquid(Liquids.water, 0.8f);
        }};

        grandConstructor = new Constructor("grand-constructor"){{
            requirements(Category.units, with(Items.thorium, 640, Items.silicon, 120, Items.plastanium, 320, Items.phaseFabric, 460, Items.surgeAlloy, 480));
            health = 240;
            size = 7;
            minBlockSize = 1;
            maxBlockSize = 6;
            buildSpeed = 0.6f;
            liquidCapacity = 180f;
            consumePower(4.75f);
            consumeLiquid(Liquids.cryofluid, 1.5f);
        }};

        //deprecated
        blockWorkshop = new BlockForge("block-workshop"){{
            replacement = Blocks.largeConstructor;
            health = 80;
            size = 5;
            minBlockSize = 3;
            maxBlockSize = 4;
            buildSpeed = 0.4f;
            liquidCapacity = 120f;
            consumePower(3.25f);
            consumeLiquid(Liquids.water, 1f);
            requirements(Category.crafting, BuildVisibility.hidden, with(Items.thorium, 360, Items.phaseFabric, 120, Items.surgeAlloy, 60));
        }};

        //deprecated
        blockFactory = new ConfigBlockForge("block-factory"){{
            replacement = grandConstructor;
            health = 240;
            size = 7;
            minBlockSize = 1;
            maxBlockSize = 6;
            buildSpeed = 0.6f;
            liquidCapacity = 180f;
            consumePower(4.75f);
            consumeLiquid(Liquids.cryofluid, 1.5f);
            requirements(Category.crafting, BuildVisibility.hidden, with(Items.thorium, 640, Items.silicon, 120, Items.plastanium, 320, Items.phaseFabric, 460, Items.surgeAlloy, 480));
        }};

        blockFurnace = new PayloadFurnace("block-furnace"){{
            size = 5;
            health = 1000;
            autoOutputTime = 45 * 60f;

            consumePower(2f);
            crafts(Blocks.siliconSmelter, 2, Blocks.kiln, 1, Blocks.surgeSmelter, 2);
            crafts(MindyItems.scalar, 1, 4).using(with(MindyItems.scalarRaw, 3, Items.graphite, 1, Items.thorium, 1, Items.metaglass, 1));
            requirements(Category.crafting, with(Items.copper, 410, Items.titanium, 230, Items.graphite, 60, Items.silicon, 115, Items.metaglass, 65, Items.plastanium, 30));
        }};

        heavyFurnace = new PayloadFurnace("heavy-furnace"){{
            size = 6;
        }};

        gateSwitch = new GateController("gate-switch"){{
            size = 2;
            health = 120;

            consumePower(0.1f);
            requirements(Category.crafting, with(Items.copper, 20, Items.graphite, 10, Items.silicon, 10));
        }};

        payloadPacker = new PayloadLoader("payload-packer"){{
            requirements(Category.units, with(Items.thorium, 360, Items.plastanium, 100, Items.phaseFabric, 80));
            hasPower = true;
            health = 80;
            size = 5;
            maxBlockSize = 4;
            itemsLoaded = 16;
            liquidsLoaded = 80f;
            consumePower(3.25f);
        }};

        payloadUnpacker = new PayloadUnloader("payload-unpacker"){{
            requirements(Category.units, with(Items.thorium, 360, Items.plastanium, 80, Items.phaseFabric, 100));
            hasPower = true;
            health = 80;
            size = 5;
            maxBlockSize = 4;
            offloadSpeed = 8;
            consumePower(3.25f);
        }};

        //deprecated
        blockPacker = new BlockLoader("block-packer"){{
            replacement = payloadPacker;
            health = 80;
            size = 5;
            maxBlockSize = 4;
            consumePower(3.25f);
            requirements(Category.distribution, BuildVisibility.hidden, with(Items.thorium, 360, Items.plastanium, 100, Items.phaseFabric, 80));
        }};

        //deprecated
        blockUnpacker = new BlockUnloader("block-unpacker"){{
            replacement = payloadUnpacker;
            health = 80;
            size = 5;
            maxBlockSize = 4;
            consumePower(3.25f);
            requirements(Category.distribution, BuildVisibility.hidden, with(Items.thorium, 360, Items.plastanium, 80, Items.phaseFabric, 100));
        }};

        //deprecated
        payDestroyer = new PayloadDeconstructor("payload-destroyer"){{
            health = 120;
            size = 5;
            maxPaySize = 4f;
            buildSpeed = 0.75f;
            itemCapacity = 800;
            refundMultiplier = 1.75f;
            consumePower(1.8f);
            requirements(Category.crafting, BuildVisibility.hidden, with(Items.copper, 360, Items.titanium, 95, Items.silicon, 65));
        }};

        arcKiln = new ArcCrafter("arc-kiln"){{
            requirements(Category.crafting, with(Items.copper, 30, MindyItems.scalarRaw, 15, Items.sand, 45));
            outputItem = new ItemStack(Items.metaglass, 2);
            craftTime = 130f;
            size = 2;
            hasPower = hasItems = true;
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.07f;
            baseEfficiency = 0f;
            attribute = MindyAttribute.metallic;
            boostScale = 1f;

            consumeItems(with(Items.sand, 3));
            consumePower(0.60f);
        }};

        lancerKiln = new ArcCrafter("lancer-kiln"){{
            requirements(Category.crafting, with(Items.titanium, 120, Items.metaglass, 80, MindyItems.scalar, 35, Items.silicon, 60));
            outputItem = new ItemStack(Items.metaglass, 8);
            craftTime = 185f;
            size = 3;
            hasPower = hasItems = true;
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.07f;
            baseEfficiency = 0f;
            attribute = MindyAttribute.metallic;
            boostScale = 0.45f;
            itemCapacity = 20;

            consumeItems(with(Items.sand, 10));
            consumePower(3.8f);
            boostItem = new ItemStack(Items.lead, 4);
            boostAmount = 1f;

            craftParticles = 9;
            drawLarge = true;
            sizeScl = 120f;
        }};

        electroRefiner = new LiquidRefiner("electro-refiner"){{
            requirements(Category.crafting, with(Items.copper, 40, MindyItems.scalarRaw, 25, Items.sand, 65, Items.metaglass, 20));
            hasItems = true;
            craftTime = 50f;
            outputLiquid = new LiquidStack(Liquids.water, 0.15f);
            outputsLiquid = true;
            size = 3;
            health = 420;
            hasPower = hasLiquids = true;
            craftEffect = Fx.formsmoke;
            updateEffect = Fx.none;
            drawer = new DrawMulti(new DrawDefault(), new DrawCultivator(){{
                plantColor = Liquids.water.color;
                plantColorLight = Color.royal.cpy().lerp(Color.white, 0.3f);
            }}, new DrawRegion("-top"));

            consumeLiquid(MindyLiquids.siloxol, 0.15f);
            consumePower(0.4f);
            consumeItem(Items.sand);
            gasProduce = 16f;
        }};

        fusionChamber = new ImpactCrafter("fusion-chamber"){{
            requirements(Category.crafting, with(Items.copper, 60, MindyItems.scalarRaw, 35, Items.sand, 15, Items.metaglass, 40));
            hasItems = true;
            itemDuration = 60f;

            warmupSpeed = 0.01f;
            explosionRadius = 6;
            explosionDamage = 500;
            explodeEffect = MindyFx.impactChamberExplosion;
            squareSprite = false;

            size = 2;
            health = 300;
            powerProduction = 8.5f;
            ambientSound = Sounds.pulse;
            ambientSoundVolume = 0.05f;
            consumePower(2.5f);
            consumeItem(MindyItems.scalarRaw);
            consumeLiquid(Liquids.water, 0.1f);
            outputItem = new ItemStack(Items.graphite, 1);
        }};

        siliconCondenser = new Condenser("silicon-condenser"){{
            requirements(Category.crafting, with(Items.copper, 60, Items.graphite, 35, Items.metaglass, 35));
            outputItem = new ItemStack(Items.silicon, 4);
            craftTime = 175f;
            size = 3;
            hasPower = hasItems = true;
            ambientSound = Sounds.respawning;
            ambientSoundVolume = 0.07f;
            craftEffect = MindyFx.releaseSteamSmall;
            squareSprite = false;

            drawer = new DrawMulti(new DrawDefault(), new DrawCondenser());
            ((LiquidRefiner) electroRefiner).condenser = this;
            consumeItems(with(Items.graphite, 4));
            consumePower(1.05f);
            gasCapacity = 108f;
            gasUse = 27f;
        }};

        colloidMixer = new GenericCrafter("colloid-mixer"){{
            requirements(Category.crafting, with(MindyItems.scalarRaw, 65, Items.silicon, 40, Items.titanium, 60));
            outputLiquid = new LiquidStack(MindyLiquids.colloid, 0.2f);
            craftTime = 120f;
            size = 2;
            hasPower = true;
            hasItems = true;
            hasLiquids = true;
            rotate = false;
            solid = true;
            outputsLiquid = true;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(MindyLiquids.colloid), new DrawDefault(), new DrawRegion("-top"));
            liquidCapacity = 24f;

            consumePower(1.5f);
            consumeItem(Items.titanium);
            consumeLiquid(MindyLiquids.siloxol, 0.2f);
        }};

        scalarFurnace = new NuclearCrafter("scalar-furnace"){{
            requirements(Category.crafting, with(Items.copper, 400, Items.silicon, 100, Items.graphite, 100, Items.thorium, 150, MindyItems.scalarRaw, 450));
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.24f;
            explodeEffect = MindyFx.scalarReactorExplosion;
            size = 4;
            health = 1200;
            itemDuration = 120f;
            itemCapacity = 30;
            powerProduction = 17f;
            consumeItems(with(Items.thorium, 1, Items.graphite, 1, Items.metaglass, 1, MindyItems.scalarRaw, 3));
            outputItem = new ItemStack(MindyItems.scalar, 1);
            heating = 0.01f;
            consumeLiquid(MindyLiquids.colloid, heating * 2f / coolantPower).update(false);
            coolantPower = 0.1f;

            explosionRadius = 24;
            explosionDamage = 1850;
        }};

        isotopeReactor = new IsotopeReactor("isotope-reactor"){{
            requirements(Category.power, with(Items.copper, 30, MindyItems.scalarRaw, 30));
            size = 1;
            powerProduction = 1f;
            ores.put(oreScalar, 0.9f);
            ores.put(oreStarStone, 3f);
            ores.put(oreVector, 1.2f);
            ores.put(oreTensor, 2.5f);
        }};

        piston = new Piston("piston"){{
            health = 200;
            consumePower(0.2f);
            requirements(Category.distribution, with(Items.graphite, 25, Items.silicon, 10, Items.titanium, 15));
        }};

        stickyPiston = new Piston("piston-sticky"){{
            health = 200;
            consumePower(0.2f);
            requirements(Category.distribution, with(Items.sporePod, 10, Items.graphite, 15, Items.silicon, 10, Items.titanium, 15));
            sticky = true;
        }};

        pistonInfi = new Piston("piston-infi", "betamindy-piston-arm", "betamindy-pistoninf"){{
            health = 200;
            consumePower(0.2f);
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());

            maxBlocks = 512;
        }};

        stickyPistonInfi = new Piston("piston-sticky-infi", "betamindy-piston-sticky-arm", "betamindy-pistoninf"){{
            health = 200;
            consumePower(0.2f);
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
            sticky = true;

            maxBlocks = 512;
        }};

        sporeSlime = new SlimeBlock("spore-slime", 0){{
            health = 40;
            requirements(Category.distribution, with(Items.sporePod, 6));

            color = Pal2.sporeSlime;
            instantDeconstruct = true;
        }};

        sporeSlimeSided = new SidedSlimeBlock("spore-slime-sided", 0){{
            health = 120;
            requirements(Category.distribution, with(Items.sporePod, 3, Items.lead, 3));

            color = Pal2.sporeSlime;
            instantDeconstruct = true;
        }};

        surgeSlime = new SlimeBlock("surge-slime", 0){{
            health = 120;
            requirements(Category.distribution, with(Items.sporePod, 3, Items.surgeAlloy, 3));
            hasPower = true;
            outputsPower = true;
            consumesPower = false;

            color = Color.valueOf("F3E979");
            instantDeconstruct = true;
        }};

        accel = new AccelBlock("accel"){{
            requirements(Category.power, with(Items.lead, 25, Items.silicon, 20, Items.plastanium, 3));
            health = 150;
        }};

        capacitor = new Capacitor("capacitor", 60f){{
            requirements(Category.power, with(Items.lead, 15, Items.silicon, 10, Items.graphite, 3));
            health = 150;
        }};

        megaCapacitor = new Capacitor("mega-capacitor", 300f){{
            requirements(Category.power, with(Items.lead, 35, Items.silicon, 20, Items.surgeAlloy, 3));
            health = 350;
        }};

        cog = new Spinner("cog"){{
            requirements(Category.distribution, with(Items.copper, 18, Items.graphite, 5));
            spinTime = 64f;
            maxBlocks = 14;
            drawSpinSprite = false;
            inertia = true;
            health = 45;
            consumePower(0.1f);
        }};

        titaniumCog = new Spinner("titanium-cog"){{
            requirements(Category.distribution, with(Items.titanium, 24, Items.silicon, 10, Items.graphite, 10));
            spinTime = 32f;
            maxBlocks = 16;
            drawSpinSprite = false;
            inertia = true;
            health = 65;
            consumePower(0.2f);
        }};

        plastaniumCog = new Spinner("plastanium-cog"){{
            requirements(Category.distribution, with(Items.plastanium, 28, Items.silicon, 10, Items.phaseFabric, 10));
            spinTime = 48f;
            maxBlocks = 25;
            drawSpinSprite = false;
            drawTop = true;
            inertia = true;
            health = 80;
            consumePower(0.3f);
        }};

        armoredCog = new Spinner("armored-cog"){{
            requirements(Category.distribution, with(Items.thorium, 24, Items.metaglass, 10, Items.silicon, 10, Items.graphite, 15));
            spinTime = 32f;
            maxBlocks = 20;
            drawSpinSprite = false;
            drawTop = true;
            inertia = false;
            health = 200;
            consumePower(0.2f);
        }};

        woodenCog = new Spinner("wooden-cog"){{
            requirements(Category.distribution, with(MindyItems.wood, 18, MindyItems.tungsten, 5));
            spinTime = 256f;
            maxBlocks = 18;
            drawSpinSprite = false;
            inertia = true;
            consumePower(0.1f);
        }};

        cloner = new BlockCloner("cloner"){{
            requirements(Category.distribution, with(Items.titanium, 30, Items.silicon, 35, Items.phaseFabric, 8));
            hasPower = true;
            consumePower(0.6f);
        }};

        spinner = new Spinner("spinner"){{
            requirements(Category.distribution, with(Items.thorium, 30, Items.phaseFabric, 35, MindyItems.vector, 4));
            hasPower = true;
            maxBlocks = 16;
            consumePower(0.3f);
        }};

        spinnerInert = new Spinner("spinner-inert"){{
            requirements(Category.distribution, with(Items.titanium, 60, Items.phaseFabric, 35, MindyItems.vector, 4));
            hasPower = true;
            inertia = true;
            maxBlocks = 16;
            consumePower(0.3f);
        }};

        spinnerInfi = new Spinner("spinner-infi"){{
            hasPower = true;
            maxBlocks = 120;
            consumePower(0.3f);
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
        }};

        spinnerInertInfi = new Spinner("spinner-inert-infi"){{
            hasPower = true;
            inertia = true;
            maxBlocks = 120;
            consumePower(0.3f);
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
        }};

        payloadRail = new PayloadRail("payload-rail"){{
            requirements(Category.units, with(Items.titanium, 2, Items.silicon, 1, MindyItems.scalarRaw, 5));
            canOverdrive = false;
            size = 1;
        }};

        railSorter = new RailSorter("rail-sorter"){{
            requirements(Category.units, with(Items.titanium, 3, Items.silicon, 3, MindyItems.scalarRaw, 5));
            canOverdrive = false;
            size = 1;
            roombaGroup = payloadRail;
        }};

        silo = new StorageBlock("silo"){
            public TextureRegion iconRegion;

            @Override
            public void load(){
                super.load();
                iconRegion = Drawm.getTeamRegion(this);
            }

            @Override
            public void createIcons(MultiPacker packer){
                Drawm.generateTeamRegion(packer, this);
                super.createIcons(packer);
            }

            @Override
            public TextureRegion[] icons(){
                return new TextureRegion[]{region, iconRegion};
            }

            {
                size = 4;
                itemCapacity = 2500;
                flags = EnumSet.of(BlockFlag.storage);
                requirements(Category.effect, with(Items.titanium, 400, Items.thorium, 250, Items.plastanium, 200, Items.phaseFabric, 100));
            }
        };

        warehouse = new StorageBlock("warehouse"){
            public TextureRegion iconRegion;

            @Override
            public void load(){
                super.load();
                iconRegion = Drawm.getTeamRegion(this);
            }

            @Override
            public void createIcons(MultiPacker packer){
                Drawm.generateTeamRegion(packer, this);
                super.createIcons(packer);
            }

            @Override
            public TextureRegion[] icons(){
                return new TextureRegion[]{region, iconRegion};
            }

            {
                size = 5;
                itemCapacity = 7000;
                flags = EnumSet.of(BlockFlag.storage);
                requirements(Category.effect, with(Items.titanium, 600, Items.thorium, 500, Items.plastanium, 450, Items.phaseFabric, 150, Items.surgeAlloy, 100));
            }
        };

        pressureContainer = new SuperStorageBlock("pressure-container", Blocks.container){{
            requirements(Category.effect, with(Items.titanium, 150, Items.thorium, 50));
        }};

        ionJet = new IonThruster("ion-jet"){{
            requirements(Category.effect, with(Items.titanium, 60, MindyItems.scalar, 50, Items.silicon, 50));
            size = 2;
            health = 600;
            smokeX = 9.5f;
            smokeY = 4f;
            consumePower(3f);
        }};

        ionThruster = new IonThruster("ion-thruster"){{
            requirements(Category.effect, with(Items.lead, 70, MindyItems.scalar, 150, Items.thorium, 120, Items.phaseFabric, 50, Items.surgeAlloy, 25));
            size = 3;
            health = 2400;
            range = 150f;
            strength = 0.11f;
            smokeX = 13.2f;
            smokeY = 6f;
            affectPayloads = true;
            consumePower(6f);
        }};

        corruptedJet = new IonThruster("corrupted-jet"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with(Items.lead, 8, MindyItems.scalar, 50, Items.silicon, 20, MindyItems.pixellium, 16));
            corrupted = true;
            affectPayloads = true;
            size = 1;
            health = 1500;
            range = 140f;
            strength = 0.5f;
            smokeX = 5f;
            smokeY = 1.73f;
            lightColor = Pal.accent;
            consumePower(1f);
        }};

        chest = new Chest("chest"){{
            requirements(Category.effect, with(Items.copper, 50, MindyItems.scalarRaw, 50, Items.metaglass, 10));
            size = 2;
        }};

        largeChest = new Chest("chest-large"){{
            requirements(Category.effect, with(Items.copper, 250, MindyItems.scalar, 50, Items.metaglass, 40, Items.silicon, 20));
            size = 3;
            slots = 15;
            capacity = 50;
        }};

        leadWall = new Wall("lead-wall"){{
            health = 360;
            requirements(Category.defense, with(Items.lead, 6));
        }};

        leadWallLarge = new Wall("lead-wall-large"){{
            health = 1440;
            size = 2;
            requirements(Category.defense, with(Items.lead, 24));
        }};

        metaglassWall = new ShardWall("metaglass-wall"){{
            shard = MindyBullets.glassPiece;
            amount = 5;
            inaccuracy = 36f;
            health = 380;
            requirements(Category.defense, with(Items.graphite, 3, Items.metaglass, 5));
        }};

        metaglassWallLarge = new ShardWall("metaglass-wall-large"){{
            shard = MindyBullets.glassPieceBig;
            amount = 8;
            inaccuracy = 24f;
            health = 1520;
            size = 2;
            distRand = 6.5f;
            requirements(Category.defense, with(Items.graphite, 12, Items.metaglass, 20));
        }};

        siliconWall = new Wall("silicon-wall"){{
            health = 370;
            insulated = true;
            requirements(Category.defense, with(Items.copper, 2, Items.silicon, 5));
        }};
        siliconWallLarge = new Wall("silicon-wall-large"){{
            health = 1480;
            insulated = true;
            size = 2;
            requirements(Category.defense, with(Items.copper, 8, Items.silicon, 20));
        }};

        graphiteWall = new AbsorbWall("graphite-wall"){{
            health = 420;
            requirements(Category.defense, with(Items.graphite, 6, Items.titanium, 4));
        }};

        graphiteWallLarge = new AbsorbWall("graphite-wall-large"){{
            health = 420 * 4;
            size = 2;
            requirements(Category.defense, with(Items.graphite, 24, Items.titanium, 16));
        }};

        coalWall = new IgniteWall("coal-wall"){{
            health = 240;
            baseExplosiveness = 3f;
            variants = 2;
            requirements(Category.defense, with(Items.coal, 6));
        }};
        coalWallLarge = new IgniteWall("coal-wall-large"){{
            health = 960;
            baseExplosiveness = 12f;
            size = 2;
            variants = 2;
            requirements(Category.defense, with(Items.coal, 24));
        }};

        pyraWall = new ShardWall("pyra-wall"){{
            shard = Bullets.fireball;
            amount = 6;
            health = 490;
            requirements(Category.defense, with(Items.pyratite, 6));
        }};
        pyraWallLarge = new ShardWall("pyra-wall-large"){{
            shard = Bullets.fireball;
            amount = 24;
            health = 1960;
            size = 2;
            distRand = 4.5f;
            requirements(Category.defense, with(Items.pyratite, 24));
        }};

        blastWall = new Wall("blast-wall"){{
            health = 510;
            baseExplosiveness = 12.5f;
            requirements(Category.defense, with(Items.coal, 2, Items.blastCompound, 8));
        }};
        blastWallLarge = new Wall("blast-wall-large"){{
            health = 2040;
            baseExplosiveness = 50f;
            size = 2;
            requirements(Category.defense, with(Items.coal, 8, Items.blastCompound, 32));
        }};

        cryoWall = new StatusWall("cryo-wall"){{
            puddle = MindyBullets.icyZoneSmall;
            destroyEffect = MindyFx.iceBurst;
            destroySound = MindySounds.freeze;
            health = 1600;
            requirements(Category.defense, with(MindyItems.cryonite, 6, MindyItems.vector, 2));
        }};

        cryoWallLarge = new StatusWall("cryo-wall-large"){{
            destroyEffect = MindyFx.iceBurstBig;
            shotEffect = MindyFx.spikeBig;
            destroySound = MindySounds.freeze;
            health = 6400;
            size = 2;
            requirements(Category.defense, with(MindyItems.cryonite, 24, MindyItems.vector, 8));
        }};

        heavyWall = new JointWall("heavy-wall"){{
            requirements(Category.defense, with(MindyItems.scalar, 6, Items.titanium, 6));
            health = 2000;
        }};

        borderWall = new JointWall("border-wall"){{
            requirements(Category.defense, BuildVisibility.editorOnly, with(MindyItems.tungsten, 8, Items.titanium, 6));
            health = 2048;
        }};

        teamWall = new TeamWall("team-wall"){{
            health = 360;
            requirements(Category.defense, with(Items.titanium, 6, Items.graphite, 6, Items.silicon, 12));
        }};

        drillMini = new DrillTurret("drill-mini"){{
            range = 50f;
            itemCapacity = 25;
            hasPower = true;
            consumePower(1f);
            requirements(Category.production, with(Items.copper, 30, Items.graphite, 25, Items.titanium, 10, Items.silicon, 15));
        }};

        drillMega = new DrillTurret("drill-mega"){{
            size = 3;
            range = 120f;
            itemCapacity = 50;
            mineSpeed = 4.5f;
            minDrillTier = 1;
            maxDrillTier = 6;
            hasPower = true;
            laserWidth = 1.1f;
            laserOffset = 11f;
            consumePower(5.6f);
            requirements(Category.production, with(Items.copper, 135, Items.titanium, 90, Items.silicon, 90, Items.plastanium, 45, MindyItems.vector, 15));
        }};

        rejuvenator = new RepairTurret("repair-turret"){{
            requirements(Category.units, with(Items.thorium, 365, Items.silicon, 250, Items.plastanium, 55, Items.phaseFabric, 75, MindyItems.scalar, 175));
            size = 3;
            powerUse = 16f;
            repairRadius = 170f;
            consumeItem(MindyItems.scalar).boost();
        }};

        boostPad = new BoostPad("boostpad"){{
            size = 2;
            requirements(Category.units, with(Items.lead, 24, Items.silicon, 10, Items.phaseFabric, 30));
            lightColor = Color.orange;
            lightRadius = 50f;
        }};

        driftPad = new DirectionalPad("driftpad"){{
            size = 2;
            requirements(Category.units, with(Items.copper, 24, Items.metaglass, 10, MindyItems.scalarRaw, 20));
            lightColor = Pal.lancerLaser;
            lightRadius = 50f;
            sprites = 5;

            friend = (BoostPad) boostPad;
            status = MindyStatusEffects.drift;
            duration = cooldown = 14f;
            boostEffect = MindyFx.driftBlock;
            boostSound = Sounds.flame2;
            impulseAmount = 9f;
        }};

        teleportPad = new TeleportPad("teleport-pad"){{
            requirements(Category.units, with( Items.copper, 320, Items.silicon, 150, Items.phaseFabric, 50, MindyItems.tensor, 30));
            size = 3;
            consumePower(10f);
        }};

        portalPad = new TeleportPortal("teleport-portal"){{
            requirements(Category.units, with( Items.scrap, 320, Items.copper, 250, Items.phaseFabric, 75, MindyItems.tensor, 30, MindyItems.bittrium, 55));
            size = 3;
            animateNear = false;
            heatLerp = 0.02f;
            lightColor = Color.white;
            inSound = MindySounds.easterEgg1;
            outSound = MindySounds.easterEgg2;
            teleportIn = MindyFx.unitInPortal;
            teleportOut = MindyFx.portalWaveSmall;
            teleportUnit = MindyFx.unitOutPortal;
            hasShadow = false;
        }};

        //frostPad = new FrostPad("frostpad"){

        //}

        linkPin = new LinkPinner("linkpin"){{
            requirements(Category.logic, with( Items.graphite, 30, Items.silicon, 15, Items.metaglass, 30));
        }};

        heatSink = new ProcessorCooler("heatsink"){{
            size = 2;
            requirements(Category.logic, with( Items.titanium, 70, Items.silicon, 25, Items.plastanium, 65));
        }};

        heatFan = new ProcessorFan("coolerfan"){{
            size = 3;
            boost = 3;
            maxProcessors = 5;
            consumePower(4f);
            requirements(Category.logic, with( Items.titanium, 90, Items.silicon, 50, Items.plastanium, 50, Items.phaseFabric, 25));
        }};

        heatSinkLarge = new ProcessorCooler("waterblock"){{
            size = 3;
            boost = 2;
            maxProcessors = 6;
            liquidCapacity = 640;
            acceptCoolant = true;
            //consumeLiquid(Liquids.water, 3f);
            requirements(Category.logic, with( Items.titanium, 110, Items.silicon, 50, Items.metaglass, 40, Items.plastanium, 30, Items.surgeAlloy, 15));
        }};

        nullifier = new Disabler("nullifier"){{
            consumePower(0.5f);
            requirements(Category.logic, with(Items.copper, 40, Items.lead, 20, Items.silicon, 10));
        }};

        noteBlock = new NotePlayer("note-block"){{
            consumePower(0.5f);
            requirements(Category.logic, with(Items.silicon, 5, Items.graphite, 10));
        }};

        starNoteBlock = new NotePlayer("star-note-block"){{
            global = true;
            consumePower(0.8f);
            requirements(Category.logic, with(Items.silicon, 5, Items.graphite, 10, MindyItems.starStone, 1));
        }};

        sfxBlock = new NotePlayer("sfx-block"){{
            consumePower(0.5f);
            requirements(Category.logic, with(Items.copper, 10, Items.silicon, 5, Items.plastanium, 10));
            soundEffect = Fx.mine;

            instruments = new Instrument[]{
                    new Instrument("Place", Sounds.place),
                    new Instrument("Break", Sounds.breaks),
                    new Instrument("Click", Sounds.click),
                    new Instrument("Boom", Sounds.boom),
                    new Instrument("Shoot", Sounds.shoot),
                    new Instrument("Sap", Sounds.sap),
                    new Instrument("[[[[Big Shot]]", Sounds.bigshot),
                    new Instrument("Laser", Sounds.laser),
                    new Instrument("LaserB", Sounds.laserblast),
                    new Instrument("Unlock", Sounds.unlock),
                    new Instrument("Wave", Sounds.wave),
                    new Instrument("Flame", Sounds.flame),
                    new Instrument("PDrop", Sounds.plasmadrop),
                    new Instrument("PBoom", Sounds.plasmaboom),
                    new Instrument("CBells", MindySounds.presentBells),
                    new Instrument("Push", MindySounds.pistonPush),
                    new Instrument("Shatter", MindySounds.shatter),
                    new Instrument("Wind3", Sounds.wind3)
            };
        }};

        filterDome = new FilterDome("filter-dome"){{
            requirements(Category.logic, with(Items.copper, 10, Items.silicon, 5, Items.plastanium, 10));
            size = 2;
            consumePower(0.5f);
        }};

        pen = new Pen("pen"){{
            requirements(Category.logic, with(Items.copper, 15, Items.silicon, 5, Items.metaglass, 5));
            drawLength = 125;
        }};

        colorModule = new PenColorModule("color-module"){{
            requirements(Category.logic, with(Items.copper, 15, Items.coal, 15, Items.graphite, 15));
        }};

        strokeModule = new PenStrokeModule("stroke-module"){{
            requirements(Category.logic, with(Items.copper, 15, Items.metaglass, 15, Items.phaseFabric, 15));
        }};

        starPen = new Pen("star-pen"){{
            requirements(Category.logic, with(Items.copper, 15, Items.silicon, 5, MindyItems.starStone, 5));
            drawLength = 250;
            glow = true;
        }};

        button = new ButtonTap("button"){{
            requirements(Category.power, with(Items.graphite, 5, Items.silicon, 25));
            health = 40;
            powerProduction = 1f;
        }};

        buttonLarge = new ButtonTap("button-large"){{
            requirements(Category.power, with(Items.graphite, 25, Items.silicon, 100));
            health = 160;
            size = 2;
            powerProduction = 2f;
        }};

        pressurePad = new ButtonPad("buttonpad"){{
            requirements(Category.power, with(Items.titanium, 10, Items.silicon, 15));
            health = 100;

            basicPowerProduction = 0.5f;
        }};

        pressurePadLarge = new ButtonPad("buttonpad-large"){{
            requirements(Category.power, with(Items.titanium, 50, Items.silicon, 50));
            size = 2;
            health = 400;

            basicPowerProduction = 1f;
            detectAir = true;
        }};

        buttonRouter = new ButtonRouter("buttonrouter"){{
            powerProduction = 0.3f;
            requirements(Category.power, with(Items.lead, 10, Items.silicon, 15));
        }};

        buttonDistributor = new ButtonRouter("buttonrouter-large"){{
            size = 2;
            powerProduction = 0.6f;
            duration = 3f;
            requirements(Category.power, with(Items.lead, 40, Items.silicon, 50));
        }};

        mynamite = new Mynamite("mynamite"){{
            health = 60;
            mineRadius = 2;
            tier = 2;

            consumePower(0.1f);
            requirements(Category.production, with(Items.lead, 10, Items.silicon, 20, Items.blastCompound, 25));
        }};

        mynamiteLarge = new Mynamite("mynamite-large"){{
            health = 60;
            size = 2;
            mineRadius = 5;
            tier = 6;
            minTier = 2;
            baseAmount = 3;
            canClick = false;

            smokeChance = 0.15f;
            fireEffect = Fx.burning;

            consumePower(0.4f);
            requirements(Category.production, with(Items.thorium, 45, Items.blastCompound, 100));
        }};

        bumper = new Bumper("bumper"){{
            health = 300;
            requirements(Category.units, with(Items.lead, 30, Items.graphite, 15, Items.metaglass, 15));
            size = 2;
            chanceDeflect = 10f;
        }};

        bumperBlue = new BumperBlue("bumper-blue"){{
            health = 300;
            requirements(Category.units, with(Items.copper, 30, Items.graphite, 10, Items.metaglass, 10));
            size = 2;
            chanceDeflect = 10f;
        }};

        bumperPlus = new Bumper("bumper-plus"){{
            health = 600;
            requirements(Category.units, with(Items.lead, 30, Items.plastanium, 15, Items.phaseFabric, 15));
            size = 2;
            bumpSpeedLimit = 8f;
            bumpScl = 0.8f;
            chanceDeflect = 50f;
        }};

        fan = new UnitFan("fan"){{
            size = 2;
            health = 200;
            consumePower(1.5f);
            requirements(Category.units, with(Items.titanium, 50, Items.silicon, 25, Items.metaglass, 30));
        }};

        fanMega = new UnitFan("fan-mega"){{
            size = 3;
            health = 450;
            range = 150f;
            strength = 42f;
            windParticles = 12;
            windAlpha = 0.9f;
            smokeX = 22f;
            smokeY = 6f;
            consumePower(3f);
            hasLiquids = true;
            liquidCapacity = 40f;
            requirements(Category.units, with(Items.titanium, 150, Items.silicon, 50, Items.metaglass, 60, Items.plastanium, 20));
        }};

        spikeScrap = new Spike("spike"){{
            health = 100;
            requirements(Category.defense, with(Items.scrap, 6));
        }};

        spikeSurge = new Spike("spike-surge"){{
            health = 400;
            damage = 75f;
            lightningChance = 0.05f;
            requirements(Category.defense, with(Items.titanium, 3, Items.surgeAlloy, 3));
        }};

        spikePyra = new Spike("spike-pyra"){{
            health = 200;
            damageSelf = 12f;
            status = StatusEffects.burning;
            requirements(Category.defense, with(Items.lead, 6, Items.pyratite, 3));
        }};

        spikeCryo = new Spike("spike-cryo"){{
            health = 500;
            damage = 100f;
            damageSelf = 20f;
            breakSound = MindySounds.freeze;
            status = MindyStatusEffects.icy;
            requirements(Category.defense, with(MindyItems.cryonite, 4, MindyItems.scalar, 1));
        }};

        spikeClear = new Spike("spike-clear"){{
            health = 400;
            damage = 75f;
            celeste = true;
            requirements(Category.defense, with(Items.metaglass, 4, MindyItems.vector, 1));
        }};

        crusher = new Crusher("crusher"){{
            requirements(Category.defense, with(Items.copper, 12, Items.titanium, 18, Items.silicon, 8));
            health = 350;
            consumePower(1.9f);
        }};

        crusherPyra = new Crusher("crusher-pyra"){{
            requirements(Category.defense, with(Items.pyratite, 22, Items.thorium, 18, Items.plastanium, 18));
            health = 750;
            damage = 1.8f;
            status = StatusEffects.melting;
            rotateSpeed = 8f;
            damageEffect = MindyFx.razorFast;
            effectChance = 0.7f;
            consumePower(3.8f);
        }};

        spotlight = new FloodLight("spotlight"){{
            requirements(Category.effect, BuildVisibility.lightingOnly, with(Items.graphite, 10, Items.silicon, 4));

            size = 1;
            consumePower(0.08f);
        }};

        messageSource = new MessageSource("message-source"){{
            requirements(Category.logic, with(MindyItems.bittrium, 1));
        }};

        messageVoid = new MessageBlock("message-void"){{
            requirements(Category.logic, with(MindyItems.source, 1));

            maxNewlines = 1;
            maxTextLength = 0;
        }};

        fireCan = new Campfire("fire-can"){{
            health = 100;
            itemCapacity = 15;
            buildCostMultiplier = 3f;
            isTorch = true;
            requirements(Category.effect, with(Items.copper, 12, Items.sand, 10));
        }};

        campfire = new Campfire("campfire"){{
            size = 2;
            health = 400;
            itemCapacity = 30;
            buildCostMultiplier = 3f;

            fireEffect = MindyFx.bigFire;
            fireDustEffect = MindyFx.bigFireDust;
            smokeChance = 0.15f;
            statusDuration = 2400f;
            statusReload = 360f;
            requirements(Category.effect, with(Items.copper, 48, Items.titanium, 20, Items.sand, 10));
        }};

        discharger = new Discharger("discharger"){{
            health = 80;
            buildCostMultiplier = 3f;
            consumePowerBuffered(4000f);
            requirements(Category.effect, with(Items.lead, 10, Items.graphite, 10, Items.silicon, 10));
        }};

        claw = new Claw("claw"){{
            requirements(Category.units, with(Items.copper, 15, Items.titanium, 15, Items.plastanium, 6));
        }};

        phaseClaw = new Claw("phase-claw"){{
            range = 84f;
            pullStrength = 3.6f;
            maxSize = 40f;
            maxBlockSize = 2;

            hasPower = true;
            consumePower(1f);
            requirements(Category.units, with(Items.titanium, 15, Items.thorium, 15, Items.silicon, 12, Items.phaseFabric, 5));
        }};

        clearPipe = new ClearPipe("clear-pipe"){{
            size = 2;
            hasShadow = false; //use custom shadows
            requirements(Category.units, with(Items.metaglass, 20, Items.plastanium, 8));
        }};

        clearDuct = new ClearPipe("unit-duct"){{
            size = 2;
            hasShadow = true;
            conditional = true;
            speed = 1f / 3.5f;
            ejectStrength = 8f;
            popSound = Sounds.release;
            suckSound = Sounds.respawn;
            hasPower = true;
            consumePowerCond(6f, ClearPipeBuild::isGate);
            requirements(Category.units, with(Items.metaglass, 20, Items.graphite, 8, MindyItems.vector, 2));
        }};

        omegaRune = new RuneBlock("omega-rune"){
            @Override
            public boolean isHidden(){
                return super.isHidden() || !(Vars.headless || Vars.net.client());
            }

            {
                requirements(Category.effect, with(MindyItems.scalar, 1, MindyItems.vector, 1, MindyItems.tensor, 1, MindyItems.source, 1));
            }
        };

        crystalPyra = new Crystal("pyra-crystal", Items.pyratite){{
            sprites = 5;
            status = StatusEffects.burning;
            updateEffect = Fx.burning;
        }};

        crystalCryo = new Crystal("cryo-crystal", MindyItems.cryonite){{
            sprites = 5;
            status = MindyStatusEffects.icy;
            updateEffect = MindyFx.snowflake;
        }};

        crystalScalar = new ScidustryCrystal("scalar-crystal", MindyItems.scalarRaw){{
            sprites = 4;
            status = MindyStatusEffects.starDrunk;
        }};

        crystalVector = new ScidustryCrystal("vector-crystal", MindyItems.vectorRaw){{
            alt = true;
            sprites = 6;
            status = MindyStatusEffects.pause;
        }};

        crystalTensor = new EldoofusCrystal("tensor-crystal", MindyItems.tensorRaw){{
            sprites = 6;
            updateEffect = MindyFx.sparkleZeta;
            status = MindyStatusEffects.amnesia;
        }};

        crystalSpace = new ShaderCrystal("space-crystal", MindyItems.spaceMatter){{
            sprites = 6;
            updateEffect = MindyFx.sparkleSpace;
            breakEffect = destroyEffect = MindyFx.crystalBreakSpace;
            shader = MindyShaders.space;

            color1 = Color.magenta;
            color2 = Color.yellow;
            glowOpacity = 0.75f;
            auraOpacity = 0.2f;
        }};

        crystalBittrium = new ShaderCrystal("bittrium-crystal", MindyItems.bittrium){{
            sprites = 2;
            updateEffect = MindyFx.sparkleBittrium;
            breakEffect = destroyEffect = MindyFx.crystalBreakBittrium;
            status = MindyStatusEffects.bittriumBane;
            shader = MindyShaders.bittrium;
            glowOpacity = 0.9f;
        }};

        // endgame turrets : ONLY the 5 Disaster Turrets specified in the trello!
        tarnation = new PowerTurret("tarnation"){{
            requirements(Category.turret, with(Items.lead, 6000, Items.thorium, 5500, Items.silicon, 3950, Items.plastanium, 1800, Items.surgeAlloy, 1024, MindyItems.bittrium, 128));
            range = 540f;

            shoot.firstShotDelay = 130f;
            recoil = 9f;
            reload = 290f;
            shake = 6f;
            shootEffect = MindyFx.tarnationShoot;
            smokeEffect = Fx.none;
            heatColor = Color.red;
            size = 6;
            health = 280 * size * size;
            targetAir = true;
            shootSound = Sounds.plasmadrop;
            rotateSpeed = 2f;
            unitSort = (u, x, y) -> -u.maxHealth;

            consumePower(60f);

            shootType = new ThickLightningBulletType(3048, Pal.lancerLaser){{
                buildingDamageMultiplier = 0.3f;

                //TODO: this only emits one tarnationLine effect, this should definitely be changed but I am cautious of changing effects without testing. -Anuke
                chargeEffect = new MultiEffect(MindyFx.tarnationCharge, MindyFx.tarnationLines);
            }};
        }};

        hopeBringer = new MultiTurret("hopebringer"){{
            requirements(Category.turret, with(MindyItems.bittrium, 9999));//TODO
            size = 9;
            health = 9999;
            powerUse = 90f;
            range = 600f;

            patterns = new TurretPattern[]{Patterns.starBlazing, Patterns.starBlazing, Patterns.chaosBuster, Patterns.starBlazing};
        }};

        box = new Box("box"){{
            requirements(Category.effect, BuildVisibility.shown, with(Items.graphite, 5));
            instantDeconstruct = true;
            placeablePlayer = false;
        }};

        present = new PresentBox("present"){{
            requirements(Category.effect, with(Items.copper, 15, Items.graphite, 15));
        }};

        coffeeMachine = new DrinkCrafter("cmachine"){{
            requirements(Category.crafting, with(Items.copper, 20, Items.metaglass, 10, Items.silicon, 10, MindyItems.wood, 5));
            result = MindyLiquids.coffee;
            liquidCapacity = 120f;
            itemCapacity = 30;
            ambientSound = Sounds.respawn;
            ambientSoundVolume = 0.5f;
            pumpAmount = 0.5f;
            updateEffect = Fx.pulverize;
            updateEffectChance = 0.08f;
            consumeItem(Items.scrap, 2);
            consumePower(3f);
            consumeLiquid(Liquids.water, 0.5f);
            placeablePlayer = false;
        }};

        yutnori = new Yutnori("gameyut"){{
            requirements(Category.effect, with(Items.copper, 10, Items.silicon, 10, MindyItems.wood, 40));
        }};

        altar = new Altar("altar"){{
            requirements(Category.effect, uwu ? BuildVisibility.shown : BuildVisibility.hidden, with(Items.scrap, 100));
        }};

        anucoinNode = new AnucoinNode("anucoin-node"){{
            requirements(Category.effect, with(Items.sand, 360, Items.copper, 240, Items.lead, 220, Items.graphite, 50, Items.metaglass, 50, Items.silicon, 50, Items.coal, 75));
            size = 4;
        }};

        anucoinSafe = new AnucoinVault("anucoin-safe"){{
            requirements(Category.effect, with(Items.sand, 200, Items.lead, 220, Items.titanium, 285, Items.thorium, 235, Items.graphite, 150, Items.plastanium, 75));
            size = 2;
        }};

        anucoinVault = new AnucoinVault("anucoin-vault"){{
            requirements(Category.effect, with(Items.sand, 240, Items.lead, 320, Items.titanium, 380, Items.thorium, 325, Items.silicon, 150, Items.surgeAlloy, 35));
            size = 3;
            capacity = 150000;
        }};

        itemShop = new Shop("item-shop"){{
            requirements(Category.effect, with(Items.sand , 240, Items.copper, 120, Items.titanium, 60, Items.thorium, 30, Items.plastanium, 20, Items.surgeAlloy, 10));
            size = 3;
            sellAllItems = true;
            soldBlocks = new Block[]{Blocks.container, pressureContainer, present};
            navigationBar = true;
            drawSpinSprite = true;
        }};

        unitShop = new Shop("unit-shop"){{
            requirements(Category.effect, with(Items.sand , 600, Items.lead, 520, Items.titanium, 860, Items.thorium, 200, Items.phaseFabric, 140, Items.surgeAlloy, 50));
            size = 5;
            sellAllUnits = true;
            spinShadowRadius = 18f;
        }};

        //todo balance & split & stuff
        extraShop = new Shop("extra-shop"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with(Items.copper, 1));
            size = 3;
            purchases = new PurchaseItem[]{firstAid, invincibleCore, package1, package2, package3, package4, package5, package6, package7, package8, package9, package10, package11, package12, package13};
        }};

        cafe = new Store("cafe", "drink", new PurchaseItem[]{milk, ShopItems.coffee, herbTea, flowerTea, sporeJuice, cocktail}, "snack", new PurchaseItem[]{pancake, glowstick, diodeCookie, bossCake}, "block", new PurchaseItem[]{new PurchaseInvBlock(coffeeMachine, 6280, 1)}){{
            requirements(Category.effect, with(Items.sand, 500, Items.lead, 250, Items.titanium, 250, Items.graphite, 150, Items.metaglass, 100, Items.silicon, 50));

            size = 4;
        }};

        tradingPost = new TradingPost("trading-post"){{
            requirements(Category.effect, with(Items.copper, 1));

            size = 3;
        }};

        coinSource = new CoinSource("coin-source"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with());
        }};

        //test
        floorRemover = new FloorRemover("floor-remover"){{
            requirements(Category.effect, BuildVisibility.hidden, with());
        }};
        metalGoldfloor = new DecorativeFloor("metal-golden-floor");
        routerFloor = new DecorativeFloor("router-floor");
        copperFloor = new DecorativeFloor("copper-floor");
        gamerSky = new DecorativeFloor("gamer-sky");
        gamerGreens = new DecorativeFloor("gamer-greens");
        gamerGrass = new DecorativeFloor("gamer-grass");
        gamerIron = new DecorativeFloor("gamer-iron");
        gamerWood = new DecorativeFloor("gamer-wood");
        gamerLeaves = new DecorativeFloor("gamer-leaves");
        gamerWaterfall = new DecorativeFloor("gamer-waterfall");
        gamerTrees = new DecorativeFloor("gamer-trees");
        gamerBricks = new DecorativeFloor("gamer-bricks");

        ancientStore = new DailyStore("ancient-store", "coins", new PurchaseItem[]{anucoins2000, anucoins4200, anucoins12000}){{
            requirements(Category.effect, uwu ? BuildVisibility.sandboxOnly : BuildVisibility.hidden, with(Items.copper, 1));
            size = 2;
            navigationBar = false;
            displayCurrency = MindyItems.bittrium;
            dailyItems = mergeItems(new PurchaseItem[]{
                    asPurchase(woodenCog, 1300, 10),
                    asDaily(starPen, 580, 0.1f),
                    asDaily(yutnori, 4800, 0.1f)
                    }, bitl(gamerSky, gamerGreens, gamerGrass, gamerWood, gamerIron, gamerLeaves, gamerWaterfall, gamerTrees, gamerBricks));
        }};

        testShop = new Shop("test-shop"){{
            requirements(Category.effect, uwu ? BuildVisibility.shown : BuildVisibility.debugOnly, with(Items.copper, 1));
            size = 3;
            defaultAnucoins = 1000;
            purchases = new PurchaseItem[]{package1, new ItemItem(MindyItems.source, 420, 69), new LiquidItem(Liquids.cryofluid, 5, 50f), new LiquidItem(Liquids.slag, 5, 50f), milk, ShopItems.coffee, herbTea, flowerTea, cocktail, holyRouter, new PurchaseInvBlock(starPen, 1, 1), new PurchaseInvBlock(Blocks.arc, 1699, 3), asPurchase(routerFloor, 69420, 99)};
            sellAllItems = sellAllUnits = sellAllBlocks = navigationBar = true;
            alwaysUnlocked = uwu;
        }};

        //scalar terraformer. turns stone -> dripstone & deepstone, spore -> moss
        terraformer1 = new Terraformer("terraformer-1", 1){{
            requirements(Category.effect, uwu ? BuildVisibility.shown : BuildVisibility.debugOnly, with(Items.scrap, 500, Items.coal, 500, Items.copper, 1000, Items.lead, 1000, Items.silicon, 500, Items.titanium, 500, Items.surgeAlloy, 400, Items.blastCompound, 400, MindyItems.bittrium, 100, MindyItems.bedrock, 50));
            lightColor = Pal2.scalar;
            orbColor = Color.yellow;
            ores = new Floor[]{(Floor)oreScalar};
            threshMultiplier = 0.94f;
            //stone floors
            terraFloors.put(Blocks.stone, borudalite);
            terraFloors.put(Blocks.craters, mossyBorudalite);
            terraFloors.put(Blocks.charr, mossyBorudalite);
            terraFloors.put(Blocks.dirt, borudalite);
            terraFloors.put(Blocks.mud, mossyBorudalite);
            terraFloors.put(blackstone, borudalite);
            terraFloors.put(redstone, borudalite);
            //grass floors
            terraFloors.put(Blocks.moss, twilightMoss);
            terraFloors.put(Blocks.sporeMoss, starryMoss);
            terraFloors.put(Blocks.grass, twilightMoss);

            terraFloors.put(Blocks.water, starryWater);
            terraFloors.put(Blocks.taintedWater, starryMossWater);
            terraFloors.put(Blocks.deepwater, starryWaterDeep);
            terraFloors.put(Blocks.deepTaintedWater, starryWaterDeep);
            terraFloors.put(Blocks.darksandWater, starrySandWater);
            terraFloors.put(Blocks.darksandTaintedWater, starrySandWater);
            terraFloors.put(Blocks.sandWater, starrySandWater);
            terraFloors.put(Blocks.sand, milksand);
            terraFloors.put(Blocks.darksand, milksand);
            //walls
            terraBlocks.put(Blocks.stoneWall, borudaliteWall);
            terraBlocks.put(Blocks.dirtWall, borudaliteWall);
            terraBlocks.put(blackstoneWall, borudaliteWall);
            terraBlocks.put(Blocks.sandWall, milkduneWall);
            terraBlocks.put(Blocks.duneWall, milkduneWall);
            terraBlocks.put(Blocks.sporeWall, twilightMossWall);
            terraBlocks.put(Blocks.shrubs, twilightMossWall);
            terraBlocks.put(redstoneWall, borudaliteWall);

            terraBlocks.put(Blocks.sporePine, starPine);
            terraBlocks.put(redPine, starPine);
            terraBlocks.put(blackPine, starPine);
            terraBlocks.put(Blocks.pine, starPine);
            terraBlocks.put(Blocks.whiteTree, largeTree);//
            terraBlocks.put(Blocks.whiteTreeDead, largeTree);//

            terraBlocks.put(Blocks.boulder, borudaliteDol);
            terraBlocks.put(Blocks.sandBoulder, milksandBoulder);
            terraBlocks.put(Blocks.sporeCluster, milksandBoulder);//
        }};

        //classic terraformer. turns stone -> dirt, spore -> grass
        terraformerC = new Terraformer("terraformer-c", 0){{
            requirements(Category.effect, with(Items.scrap, 512, Items.coal, 512, Items.titanium, 512, MindyItems.pixellium, 64));
            lightColor = Pal2.exp;
            orbColor = Pal.heal;
            ores = new Floor[]{(Floor) Blocks.oreScrap};
            //stone floors
            terraFloors.put(Blocks.stone, Blocks.dirt);
            terraFloors.put(Blocks.craters, Blocks.mud);
            terraFloors.put(Blocks.charr, Blocks.mud);

            terraFloors.put(blackstone, Blocks.dirt);
            terraFloors.put(redstone, Blocks.dirt);
            terraFloors.put(borudalite, Blocks.dirt);
            //grass floors
            terraFloors.put(Blocks.moss, Blocks.grass);
            terraFloors.put(Blocks.sporeMoss, Blocks.grass);
            terraFloors.put(twilightMoss, Blocks.grass);
            terraFloors.put(starryMoss, Blocks.grass);

            terraFloors.put(Blocks.taintedWater, Blocks.water);
            terraFloors.put(starryWater, Blocks.water);
            terraFloors.put(starryMossWater, Blocks.water);
            terraFloors.put(Blocks.deepTaintedWater, Blocks.deepwater);
            terraFloors.put(starryWaterDeep, Blocks.deepwater);
            terraFloors.put(Blocks.darksandTaintedWater, Blocks.sandWater);
            terraFloors.put(Blocks.darksandWater, Blocks.sandWater);
            terraFloors.put(starrySandWater, Blocks.sandWater);

            terraFloors.put(Blocks.darksand, Blocks.sand);
            terraFloors.put(milksand, Blocks.sand);
            //walls
            terraBlocks.put(Blocks.stoneWall, Blocks.dirtWall);
            terraBlocks.put(blackstoneWall, Blocks.dirtWall);
            terraBlocks.put(redstoneWall, Blocks.dirtWall);
            terraBlocks.put(borudaliteWall, Blocks.dirtWall);

            terraBlocks.put(Blocks.duneWall, Blocks.sandWall);
            terraBlocks.put(milkduneWall, Blocks.sandWall);

            terraBlocks.put(Blocks.sporeWall, Blocks.shrubs);
            terraBlocks.put(twilightMossWall, Blocks.shrubs);
            terraBlocks.put(Blocks.sporePine, Blocks.pine);
            terraBlocks.put(redPine, Blocks.pine);
            terraBlocks.put(blackPine, Blocks.pine);
            terraBlocks.put(starPine, Blocks.pine);
            terraBlocks.put(Blocks.whiteTree, largeTree);
            terraBlocks.put(Blocks.whiteTreeDead, largeTree);

            terraBlocks.put(Blocks.boulder, Blocks.router);
            terraBlocks.put(borudaliteDol, Blocks.junction);
        }};

        esotManual = new LoreManual("esot-manual"){{
            buildVisibility = uwu ? BuildVisibility.shown : BuildVisibility.editorOnly;
        }};

        esotPage1 = new ManualPiece("esot-piece-1", LorePages.esot1){{
            buildVisibility = BuildVisibility.editorOnly;
            parent = (LoreManual) esotManual;
        }};

        esotPage2 = new ManualPiece("esot-piece-2", LorePages.esot2){{
            buildVisibility = BuildVisibility.editorOnly;
            parent = (LoreManual) esotManual;
        }};

        fallenFlare = new FallenUnit("fallen-flare");
        fallenMono = new FallenUnit("fallen-mono"){{
            unitName = "mono";
        }};
        fallenHorizon = new FallenUnit("fallen-horizon"){{
            unitName = "horizon";
        }};
        fallenAlpha = new FallenUnit("fallen-alpha"){{
            unitName = "alpha";
            unitTeam = Team.derelict;
        }};

        sentryGun = new SentryTurret("sentry-gun"){{
            buildVisibility = uwu ? BuildVisibility.shown : BuildVisibility.editorOnly;
            reload = 5f;
            range = 312f;
            health = 4096;
            shootType = new InstantHitBulletType(1f){{
                hitColor = trailColor = Pal.accent.cpy().lerp(Color.orange, 0.3f);
                percentDamage = 0.15f; //six shots and ded
            }};
            inaccuracy = 0.2f;
        }};

        esotChest = new LoreChest("esot-chest"){{
            buildVisibility = uwu ? BuildVisibility.shown : BuildVisibility.editorOnly;
            category = Category.effect;
            size = 2;
            clipSize = 360f;
        }};

        barrier = new Barrier("barrier"){{
            buildVisibility = uwu ? BuildVisibility.shown : BuildVisibility.editorOnly;
            category = Category.effect;
            size = 1;
            clipSize = 360f;
        }};
    }
}