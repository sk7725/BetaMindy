package betamindy.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
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
import betamindy.world.blocks.production.*;
import betamindy.world.blocks.production.payduction.*;
import betamindy.world.blocks.storage.*;
import betamindy.world.blocks.units.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static betamindy.BetaMindy.uwu;
import static betamindy.content.ShopItems.*;
import static betamindy.util.BlockLib.*;
import static mindustry.type.ItemStack.with;

public class MindyBlocks implements ContentList {
    //environment
    public static Block radiation, exoticMatter, present, asphalt, blueice, ohno, omegaRune, crystalPyra, crystalCryo, crystalScalar, crystalVector, crystalTensor, crystalBittrium, crystalSpace,
    blackstone, blackstoneWall, redstone, redstoneWall, blackPine, redPine, largeTree, borudalite, borudaliteWall, mossyBorudalite, twilightMoss, starryMoss, twilightMossWall, starBoulder, starTree, starPine, milksand, milkduneWall,
    //ores
    oreScalar, oreVector, oreTensor,
    //payloads
    payCannon, payCatapult, blockWorkshop, blockFactory, blockPacker, blockUnpacker, payDeconstructor, payDestroyer, payEradicator,
    //pistons
    piston, stickyPiston, pistonInfi, stickyPistonInfi, sporeSlime, sporeSlimeSided, surgeSlime, accel, cloner, spinner, spinnerInert, spinnerInfi, spinnerInertInfi, cog, titaniumCog, armoredCog, plastaniumCog, woodenCog,
    //effect
    silo, warehouse, pressureContainer, altar, box, itemShop, unitShop, extraShop, anucoinNode, anucoinSafe, anucoinVault, tradingPost, coinSource, testShop, cafe, ancientStore, terraformer1, terraformer2, terraformer3, terraformer4, terraformerC,
    //walls
    leadWall, leadWallLarge, metaglassWall, metaglassWallLarge, siliconWall, siliconWallLarge, graphiteWall, graphiteWallLarge, coalWall, coalWallLarge, pyraWall, pyraWallLarge, blastWall, blastWallLarge, cryoWall, cryoWallLarge, teamWall, spikeScrap, spikeSurge, spikePyra, spikeCryo, spikeClear, crusher, crusherPyra, crusherScalar,
    //drills
    drillMini, drillMega, mynamite, mynamiteLarge,
    //units
    boostPad, repairTurret, bumper, bumperPlus, bumperBlue, fan, fanMega, clearPipe, clearDuct, claw, phaseClaw, driftPad, teleportPad, portalPad, yutnori,
    //logic
    linkPin, heatSink, heatFan, heatSinkLarge, messageVoid, messageSource, nullifier, pen, starPen, colorModule, strokeModule,
    //turrets
    hopeBringer, anchor, bermuda, propaganda, spear, justice, sting, ray, tarnation, astro, magicTurret, credit, taxation, brokerage, mortgage,
    //power
    pressurePad, pressurePadLarge, button, buttonLarge, spotlight,
    //crafting
    blockFurnace, heavyFurnace, gateSwitch, coffeeMachine,
    //catalysts (pushreact & spinreact & boost)
    discharger, fireCan, campfire,
    //floorpapers
    floorRemover, metalGoldfloor, copperFloor, gamerSky, gamerGreens, gamerGrass, gamerWood, gamerIron, gamerLeaves, gamerWaterfall, gamerTrees, gamerBricks, routerFloor;

    @Override
    public void load() {
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
        }};
        mossyBorudalite = new Floor("mossy-borudalite"){{
            variants = 5;
        }};

        twilightMoss = new Floor("twilight-moss"){{
            variants = 8;
        }};
        starryMoss = new Floor("starry-moss"){{
            variants = 4;
        }};

        milksand = new Floor("milksand"){{
            itemDrop = Items.sand; //change this?
            playerUnmineable = true;
            variants = 3;
        }};

        borudaliteWall = new StaticWall("borudalite-wall"){{
            variants = 6;
        }};
        twilightMossWall = new StaticWall("twilight-moss-wall"){{
            variants = 2;
        }};
        milkduneWall = new Floor("milkdune-wall"){{
            variants = 2;
            milksand.asFloor().wall = this;
        }};
        //endregion

        //ores
        oreScalar = new OreBlock(MindyItems.scalarRaw){{
            oreDefault = false;
            oreThreshold = 0.841f;
            oreScale = 25.580953f;
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

            reloadTime = 5f;
            range = 112f;
            health = 220 * size * size;
            shootType = new CoinBulletType(7f);
            inaccuracy = 2f;
        }};

        taxation = new CoinTurret("taxation"){{
            requirements(Category.turret, with(Items.lead, 110, Items.graphite, 95, Items.titanium, 50));

            size = 2;
            reloadTime = 45f;
            maxAmmo = 120;
            shots = 15;
            ammoPerShot = 2;
            range = 65f;
            health = 440 * size * size;

            shootType = new CoinBulletType(15f){{
                hitRange = 9f;
            }};
            inaccuracy = 1.8f;
            velocityInaccuracy = 0.05f;
            shootSound = Sounds.shootBig;
            shootShake = 3f;
            burstSpacing = 1f;
            shootLength = 6.5f;
        }};

        brokerage = new CoinTurret("brokerage"){{
            requirements(Category.turret, with(Items.lead, 160, Items.metaglass, 100, Items.thorium, 70, Items.plastanium, 50));

            size = 2;
            maxAmmo = 180;
            ammoPerShot = 20;
            reloadTime = 245f;
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
            shootShake = 4f;
            shootSound = Sounds.shotgun;
        }};

        anchor = new ItemTurret("anchor"){{
            requirements(Category.turret, with(Items.lead, 80, Items.graphite, 65, Items.titanium, 50));

            reloadTime = 80f;
            shootShake = 3f;
            range = 160f;
            recoilAmount = 4f;
            restitution = 0.1f;
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

            reloadTime = 40f;
            shootShake = 3f;
            range = 230f;
            recoilAmount = 5f;
            restitution = 0.1f;
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

            reloadTime = 160f;
            shootShake = 3f;
            range = 240f;
            recoilAmount = 2f;
            restitution = 0.1f;
            size = 3;
            cooldown = 0.03f;
            shootShake = 1f;
            burstSpacing = 10f;
            shots = 3;

            health = 140 * size * size;
            shootSound = Sounds.plasmadrop;
            heatColor = Pal.lancerLaser;
            shootShake = 0f;
            shootLength = 2f;
            coolantUsage = 1f;
            coolantMultiplier = 0.4f;

            consumes.powerCond(18f, TurretBuild::isActive);

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

        astro = new UnitTurret("astro"){
            {
                requirements(Category.turret, with(Items.thorium, 12000, Items.silicon, 25500, Items.titanium, 8250, MindyItems.bittrium, 5000));
                size = 6;
                chargeTime = 61f;
                chargeEffect = MindyFx.astroCharge;
                shootType = MindyBullets.voidStar;
                powerUse = 65f;
                rotateSpeed = 0.8f;
                heatColor = Color.valueOf("7474ed");
                range = 400f;
                reloadTime = 600f;
                recoilAmount = 10f;
                restitution = 0.03f;
                shootCone = 20f;
                shootShake = 5f;
                //TODO find a better sound: chargeSound = MindySounds.astroCharge;
                shootSound = MindySounds.astroShoot;
                targetAir = true;
                chargeEffects = 1;
                health = 240 * size * size;
            }
        };

        //sting = new PowerTurret(I)

        payCannon = new PayloadTurret("payload-cannon"){{
            health = 2050;
            size = 5;
            recoilAmount = 7f;
            range = 315f;
            shootShake = 2f;
            shootSound = Sounds.shootBig;
            shootEffect = MindyFx.cannonShoot;
            smokeEffect = Fx.shootBigSmoke2;
            safeRange = 140f;
            reloadTime = 120f;
            rotateSpeed = 1.8f;
            maxPaySize = 3.5f;
            payloadOffset = 7f;
            payloadShootOffset = 8f;
            consumes.power(3.75f);
            requirements(Category.turret, with(Items.copper, 1000, Items.titanium, 750, Items.silicon, 450, Items.plastanium, 330));
        }};

        payCatapult = new PayloadTurret("payload-catapult"){{
            health = 3190;
            size = 7;
            recoilAmount = 9f;
            range = 520f;
            shootShake = 4f;
            shootSound = Sounds.plasmaboom;
            shootEffect = MindyFx.cannonShoot2;
            smokeEffect = Fx.shootBigSmoke2;
            damage = 2.3f;
            maxDamagePercent = 0.75f;
            safeRange = 190f;
            reloadTime = 250f;
            rotateSpeed = 1.3f;
            payloadOffset = 7.5f;
            payloadShootOffset = 7f;
            payloadScale = 0.85f;
            maxPaySize = 14.5f; //I'm not adding a T3. ...definitely. ...definitely? ...definitely.
            consumes.power(7.25f);
            shootType = MindyBullets.payBulletBig;
            homingShootType = MindyBullets.homingPayBig;
            requirements(Category.turret, with(Items.copper, 1500, Items.titanium, 900, Items.silicon, 650, Items.plastanium, 390, Items.phaseFabric, 180, Items.surgeAlloy, 165));
        }};

        blockWorkshop = new BlockForge("block-workshop"){{
            health = 80;
            size = 5;
            minBlockSize = 3;
            maxBlockSize = 4;
            buildSpeed = 0.4f;
            liquidCapacity = 120f;
            consumes.power(3.25f);
            consumes.liquid(Liquids.water, 1f);
            requirements(Category.crafting, with(Items.thorium, 360, Items.phaseFabric, 120, Items.surgeAlloy, 60));
        }};

        blockFactory = new ConfigBlockForge("block-factory"){{
            health = 240;
            size = 7;
            minBlockSize = 1;
            maxBlockSize = 6;
            buildSpeed = 0.6f;
            liquidCapacity = 180f;
            consumes.power(4.75f);
            consumes.liquid(Liquids.cryofluid, 1.5f);
            requirements(Category.crafting, with(Items.thorium, 640, Items.silicon, 120, Items.plastanium, 320, Items.phaseFabric, 460, Items.surgeAlloy, 480));
        }};

        blockFurnace = new PayloadFurnace("block-furnace"){{
            size = 5;
            health = 1000;
            autoOutputTime = 45 * 60f;

            consumes.power(2f);
            crafts(Blocks.siliconSmelter, 2, Blocks.kiln, 1, Blocks.surgeSmelter, 2);
            crafts(MindyItems.scalar, 1, 4).using(with(MindyItems.scalarRaw, 5, Items.plastanium, 3, Items.thorium, 10));
            requirements(Category.crafting, with(Items.copper, 410, Items.titanium, 230, Items.graphite, 60, Items.silicon, 115, Items.metaglass, 65, Items.plastanium, 30));
        }};

        heavyFurnace = new PayloadFurnace("heavy-furnace"){{
            size = 6;
        }};

        gateSwitch = new GateController("gate-switch"){{
            size = 2;
            health = 120;

            consumes.power(0.1f);
            requirements(Category.crafting, with(Items.copper, 20, Items.graphite, 10, Items.silicon, 10));
        }};

        blockPacker = new BlockLoader("block-packer"){{
            health = 80;
            size = 5;
            maxBlockSize = 4;
            consumes.power(3.25f);
            requirements(Category.distribution, with(Items.thorium, 360, Items.plastanium, 100, Items.phaseFabric, 80));
        }};

        blockUnpacker = new BlockUnloader("block-unpacker"){{
            health = 80;
            size = 5;
            maxBlockSize = 4;
            consumes.power(3.25f);
            requirements(Category.distribution, with(Items.thorium, 360, Items.plastanium, 80, Items.phaseFabric, 100));
        }};

        payDeconstructor = new PayloadDeconstructor("payload-deconstructor"){{
            health = 80;
            size = 3;
            itemCapacity = 400;
            consumes.power(1f);
            requirements(Category.crafting, with(Items.copper, 100, Items.titanium, 50, Items.silicon, 25));
        }};

        payDestroyer = new PayloadDeconstructor("payload-destroyer"){{
            health = 120;
            size = 5;
            maxPaySize = 4f;
            buildSpeed = 0.75f;
            itemCapacity = 800;
            refundMultiplier = 1.75f;
            consumes.power(1.8f);
            requirements(Category.crafting, with(Items.copper, 360, Items.titanium, 95, Items.silicon, 65));
        }};

        payEradicator = new PayloadDeconstructor("payload-eradicator"){{
            health = 250;
            size = 7;
            maxPaySize = 10f;
            buildSpeed = 0.8f;
            itemCapacity = 1500;
            refundMultiplier = 2f;
            consumes.power(2.35f);
            consumes.liquid(Liquids.water, 0.8f);
            requirements(Category.crafting, with(Items.copper, 610, Items.titanium, 130, Items.silicon, 115, Items.phaseFabric, 35));
        }};

        piston = new Piston("piston"){{
            health = 200;
            consumes.power(1f);
            requirements(Category.distribution, with(Items.graphite, 25, Items.silicon, 10, Items.titanium, 15));
        }};

        stickyPiston = new Piston("piston-sticky"){{
            health = 200;
            consumes.power(1f);
            requirements(Category.distribution, with(Items.sporePod, 10, Items.graphite, 15, Items.silicon, 10, Items.titanium, 15));
            sticky = true;
        }};

        pistonInfi = new Piston("piston-infi", "betamindy-piston-arm", "betamindy-pistoninf"){{
            health = 200;
            consumes.power(1f);
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());

            maxBlocks = 512;
        }};

        stickyPistonInfi = new Piston("piston-sticky-infi", "betamindy-piston-sticky-arm", "betamindy-pistoninf"){{
            health = 200;
            consumes.power(1f);
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
            sticky = true;

            maxBlocks = 512;
        }};

        sporeSlime = new SlimeBlock("spore-slime", 0){{
            health = 40;
            requirements(Category.distribution, with(Items.sporePod, 6));

            color = Pal2.sporeSlime;
        }};

        sporeSlimeSided = new SidedSlimeBlock("spore-slime-sided", 0){{
            health = 120;
            requirements(Category.distribution, with(Items.sporePod, 3, Items.lead, 3));

            color = Pal2.sporeSlime;
        }};

        surgeSlime = new SlimeBlock("surge-slime", 0){{
            health = 120;
            requirements(Category.distribution, with(Items.sporePod, 3, Items.surgeAlloy, 3));
            hasPower = true;
            outputsPower = true;
            consumesPower = false;

            color = Color.valueOf("F3E979");
        }};

        accel = new AccelBlock("accel"){{
            health = 150;
            requirements(Category.power, with(Items.lead, 25, Items.silicon, 20, Items.plastanium, 3));
        }};

        cog = new Spinner("cog"){{
            requirements(Category.distribution, with(Items.copper, 18, Items.graphite, 5));
            spinTime = 64f;
            maxBlocks = 14;
            drawSpinSprite = false;
            inertia = true;
            health = 45;
            consumes.power(0.1f);
        }};

        titaniumCog = new Spinner("titanium-cog"){{
            requirements(Category.distribution, with(Items.titanium, 24, Items.silicon, 10, Items.graphite, 10));
            spinTime = 32f;
            maxBlocks = 16;
            drawSpinSprite = false;
            inertia = true;
            health = 65;
            consumes.power(0.2f);
        }};

        plastaniumCog = new Spinner("plastanium-cog"){{
            requirements(Category.distribution, with(Items.plastanium, 28, Items.silicon, 10, Items.phaseFabric, 10));
            spinTime = 48f;
            maxBlocks = 25;
            drawSpinSprite = false;
            drawTop = true;
            inertia = true;
            health = 80;
            consumes.power(0.3f);
        }};

        armoredCog = new Spinner("armored-cog"){{
            requirements(Category.distribution, with(Items.thorium, 24, Items.metaglass, 10, Items.silicon, 10, Items.graphite, 15));
            spinTime = 32f;
            maxBlocks = 20;
            drawSpinSprite = false;
            drawTop = true;
            inertia = false;
            health = 200;
            consumes.power(0.2f);
        }};

        woodenCog = new Spinner("wooden-cog"){{
            requirements(Category.distribution, with(MindyItems.wood, 18, MindyItems.tungsten, 5));
            spinTime = 256f;
            maxBlocks = 18;
            drawSpinSprite = false;
            inertia = true;
            consumes.power(0.1f);
        }};

        cloner = new BlockCloner("cloner"){{
            requirements(Category.distribution, with(Items.titanium, 30, Items.silicon, 35, Items.phaseFabric, 8));
            hasPower = true;
            consumes.power(0.6f);
        }};

        spinner = new Spinner("spinner"){{
            requirements(Category.distribution, with(Items.thorium, 30, Items.phaseFabric, 35, MindyItems.vector, 4));
            hasPower = true;
            maxBlocks = 16;
            consumes.power(0.3f);
        }};

        spinnerInert = new Spinner("spinner-inert"){{
            requirements(Category.distribution, with(Items.titanium, 60, Items.phaseFabric, 35, MindyItems.vector, 4));
            hasPower = true;
            inertia = true;
            maxBlocks = 16;
            consumes.power(0.3f);
        }};

        spinnerInfi = new Spinner("spinner-infi"){{
            hasPower = true;
            maxBlocks = 120;
            consumes.power(0.3f);
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
        }};

        spinnerInertInfi = new Spinner("spinner-inert-infi"){{
            hasPower = true;
            inertia = true;
            maxBlocks = 120;
            consumes.power(0.3f);
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
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
            breakSound = MindySounds.freeze;
            health = 1600;
            requirements(Category.defense, with(MindyItems.cryonite, 6, MindyItems.vector, 2));
        }};

        cryoWallLarge = new StatusWall("cryo-wall-large"){{
            destroyEffect = MindyFx.iceBurstBig;
            shotEffect = MindyFx.spikeBig;
            breakSound = MindySounds.freeze;
            health = 6400;
            size = 2;
            requirements(Category.defense, with(MindyItems.cryonite, 24, MindyItems.vector, 8));
        }};

        teamWall = new TeamWall("team-wall"){{
            health = 360;
            requirements(Category.defense, with(Items.titanium, 6, Items.graphite, 6, Items.silicon, 12));
        }};

        drillMini = new DrillTurret("drill-mini"){{
            range = 50f;
            itemCapacity = 25;
            hasPower = true;
            consumes.power(1f);
            requirements(Category.production, with(Items.copper, 15, Items.lead, 15, Items.graphite, 25, Items.titanium, 10, Items.silicon, 15));
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
            consumes.power(5.6f);
            requirements(Category.production, with(Items.copper, 135, Items.titanium, 90, Items.silicon, 90, Items.plastanium, 45, MindyItems.vector, 15));
        }};

        repairTurret = new RepairTurret("repair-turret"){{
            size = 3;
            powerUse = 4f;
            requirements(Category.units, with(Items.lead, 50, Items.copper, 65, Items.silicon, 50, Items.plastanium, 25, Items.phaseFabric, 15));
            consumes.item(MindyItems.scalar).boost();
        }};

        boostPad = new BoostPad("boostpad"){{
            size = 2;
            requirements(Category.units, with(Items.lead, 24, Items.silicon, 10, Items.phaseFabric, 30));
            lightColor = Color.orange;
            lightRadius = 50f;
        }};

        driftPad = new DirectionalPad("driftpad"){{
            size = 2;
            requirements(Category.units, with(Items.copper, 24, Items.metaglass, 20, Items.phaseFabric, 20));
            lightColor = Pal.lancerLaser;
            lightRadius = 50f;
            sprites = 5;

            friend = (BoostPad) boostPad;
            status = MindyStatusEffects.drift;
            duration = cooldown = 14f;
            boostEffect = MindyFx.driftBlock;
            boostSound = Sounds.flame2;//todo
            impulseAmount = 9f;
        }};

        teleportPad = new TeleportPad("teleport-pad"){{
            requirements(Category.units, with( Items.copper, 320, Items.silicon, 150, Items.phaseFabric, 50, MindyItems.tensor, 30));
            size = 3;
            consumes.power(10f);
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
            consumes.power(4f);
            requirements(Category.logic, with( Items.titanium, 90, Items.silicon, 50, Items.plastanium, 50, Items.phaseFabric, 25));
        }};

        heatSinkLarge = new ProcessorCooler("waterblock"){{
            size = 3;
            boost = 2;
            maxProcessors = 6;
            liquidCapacity = 640;
            acceptCoolant = true;
            //consumes.liquid(Liquids.water, 3f);
            requirements(Category.logic, with( Items.titanium, 110, Items.silicon, 50, Items.metaglass, 40, Items.plastanium, 30, Items.surgeAlloy, 15));
        }};

        nullifier = new Disabler("nullifier"){{
            consumes.power(0.5f);
            requirements(Category.logic, with(Items.copper, 40, Items.lead, 20, Items.silicon, 10));
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

            basicPowerProduction = 1f;
        }};

        pressurePadLarge = new ButtonPad("buttonpad-large"){{
            requirements(Category.power, with(Items.titanium, 50, Items.silicon, 50));
            size = 2;
            health = 400;

            basicPowerProduction = 2f;
            detectAir = true;
        }};

        mynamite = new Mynamite("mynamite"){{
            health = 60;
            mineRadius = 2;
            tier = 2;

            consumes.power(0.1f);
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

            consumes.power(0.4f);
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
            consumes.power(1.5f);
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
            consumes.power(3f);
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
            consumes.power(1.9f);
        }};

        crusherPyra = new Crusher("crusher-pyra"){{
            requirements(Category.defense, with(Items.pyratite, 22, Items.thorium, 18, Items.plastanium, 18));
            health = 750;
            damage = 1.8f;
            status = StatusEffects.melting;
            rotateSpeed = 8f;
            damageEffect = MindyFx.razorFast;
            effectChance = 0.7f;
            consumes.power(3.8f);
        }};

        spotlight = new FloodLight("spotlight"){{
            requirements(Category.effect, BuildVisibility.lightingOnly, with(Items.graphite, 10, Items.silicon, 4));

            size = 1;
            consumes.power(0.08f);
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
            consumes.powerBuffered(4000f);
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
            consumes.power(1f);
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
            consumes.powerCond(6f, ClearPipeBuild::isGate);
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
            destroyEffect = MindyFx.crystalBreakSpace;
            shader = MindyShaders.space;

            color1 = Color.magenta;
            color2 = Color.yellow;
            glowOpacity = 0.75f;
            auraOpacity = 0.2f;
        }};

        crystalBittrium = new ShaderCrystal("bittrium-crystal", MindyItems.bittrium){{
            sprites = 2;
            updateEffect = MindyFx.sparkleBittrium;
            destroyEffect = MindyFx.crystalBreakBittrium;
            status = MindyStatusEffects.bittriumBane;
            shader = MindyShaders.bittrium;
            glowOpacity = 0.9f;
        }};

        // endgame turrets : ONLY the 5 Disaster Turrets specified in the trello!
        tarnation = new PowerTurret("tarnation"){{
            requirements(Category.turret, with(Items.lead, 6000, Items.thorium, 5500, Items.silicon, 3950, Items.plastanium, 1800, Items.surgeAlloy, 1024, MindyItems.bittrium, 128));
            range = 540f;
            chargeTime = 130f;
            chargeMaxDelay = 100f;
            chargeEffects = 8;
            recoilAmount = 9f;
            reloadTime = 290f;
            cooldown = 0.03f;
            powerUse = 60f;
            shootShake = 6f;
            shootEffect = MindyFx.tarnationShoot;
            smokeEffect = Fx.none;
            chargeEffect = MindyFx.tarnationLines;
            chargeBeginEffect = MindyFx.tarnationCharge;
            heatColor = Color.red;
            size = 6;
            health = 280 * size * size;
            targetAir = true;
            shootSound = Sounds.plasmadrop;
            rotateSpeed = 2f;
            unitSort = (u, x, y) -> -u.maxHealth;

            shootType = new ThickLightningBulletType(3048, Pal.lancerLaser){{
                buildingDamageMultiplier = 0.3f;
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
            consumes.item(Items.scrap, 2);
            consumes.power(3f);
            consumes.liquid(Liquids.water, 0.5f);
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
            requirements(Category.effect, with(Items.copper, 1));
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

        cafe = new Store("cafe", "drink", new PurchaseItem[]{milk, coffee, herbTea, flowerTea, sporeJuice, cocktail}, "snack", new PurchaseItem[]{pancake, glowstick, diodeCookie, bossCake}, "block", new PurchaseItem[]{new PurchaseInvBlock(coffeeMachine, 6280, 1)}){{
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
            purchases = new PurchaseItem[]{package1, new ItemItem(MindyItems.source, 420, 69), new LiquidItem(Liquids.cryofluid, 5, 50f), new LiquidItem(Liquids.slag, 5, 50f), milk, coffee, herbTea, flowerTea, cocktail, holyRouter, new PurchaseInvBlock(starPen, 1, 1), new PurchaseInvBlock(Blocks.arc, 1699, 3), asPurchase(routerFloor, 69420, 99)};
            sellAllItems = sellAllUnits = sellAllBlocks = navigationBar = true;
            alwaysUnlocked = uwu;
        }};

        //scalar terraformer. turns stone -> dripstone & deepstone, spore -> moss
        terraformer1 = new Terraformer("terraformer-1", 1){{
            requirements(Category.effect, uwu ? BuildVisibility.shown : BuildVisibility.debugOnly, with(Items.copper, 1));
            lightColor = Pal2.scalar;
            orbColor = Color.yellow;
            ores = new Floor[]{(Floor)oreScalar};
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
            terraFloors.put(Blocks.taintedWater, Blocks.water);//todo v7 deeptaintedwater
            terraFloors.put(Blocks.darksandTaintedWater, Blocks.darksandWater);//
            terraFloors.put(Blocks.sandWater, Blocks.darksandWater);//
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
            terraBlocks.put(Blocks.sporePine, Blocks.pine);//
            terraBlocks.put(redPine, Blocks.pine);//
            terraBlocks.put(blackPine, Blocks.pine);//
            terraBlocks.put(Blocks.whiteTree, largeTree);//
            terraBlocks.put(Blocks.whiteTreeDead, largeTree);//
            terraBlocks.put(Blocks.boulder, crystalScalar);//
        }};

        //classic terraformer. turns stone -> dirt, spore -> grass
        terraformerC = new Terraformer("terraformer-c", 0){{
            requirements(Category.effect, with(Items.scrap, 512, Items.coal, 256, Items.titanium, 128, MindyItems.pixellium, 64));
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
            terraFloors.put(Blocks.taintedWater, Blocks.water);//todo v7 deeptaintedwater
            terraFloors.put(Blocks.darksandTaintedWater, Blocks.sandWater);
            terraFloors.put(Blocks.darksandWater, Blocks.sandWater);

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
            terraBlocks.put(Blocks.sporePine, Blocks.pine);
            terraBlocks.put(redPine, Blocks.pine);
            terraBlocks.put(blackPine, Blocks.pine);
            terraBlocks.put(Blocks.whiteTree, largeTree);
            terraBlocks.put(Blocks.whiteTreeDead, largeTree);
            terraBlocks.put(Blocks.boulder, Blocks.router);
        }};
    }
}
