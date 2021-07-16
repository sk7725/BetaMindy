package betamindy.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import betamindy.entities.bullet.*;
import betamindy.graphics.*;
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

import static betamindy.BetaMindy.omegaServer;
import static mindustry.type.ItemStack.with;

public class MindyBlocks implements ContentList {
    //environment
    public static Block radiation, exoticMatter, present, asphalt, blueice, ohno, omegaRune, crystalPyra, crystalCryo, crystalScalar, crystalVector, crystalTensor,
    //payloads
    payCannon, payCatapult, blockWorkshop, blockFactory, blockPacker, blockUnpacker, payDeconstructor, payDestroyer, payEradicator,
    //pistons
    piston, stickyPiston, pistonInfi, stickyPistonInfi, sporeSlime, sporeSlimeSided, surgeSlime, accel, cloner, spinner, spinnerInert, spinnerInfi, spinnerInertInfi,
    //effect
    silo, warehouse, pressureContainer,
    //walls
    leadWall, leadWallLarge, metaglassWall, metaglassWallLarge, siliconWall, siliconWallLarge, coalWall, coalWallLarge, pyraWall, pyraWallLarge, blastWall, blastWallLarge, cryoWall, cryoWallLarge, teamWall, spikeScrap, spikeSurge, spikePyra, spikeCryo, spikeClear,
    //drills
    drillMini, drillMega, mynamite, mynamiteLarge,
    //units
    boostPad, repairTurret, bumper, bumperPlus, bumperBlue, fan, fanMega, clearPipe, clearDuct, claw, phaseClaw,
    //logic
    linkPin, heatSink, heatFan, heatSinkLarge, messageVoid, messageSource, nullifier,
    //turrets
    hopeBringer, anchor, bermuda, propaganda, spear, justice, ray, tarnation,
    //power
    pressurePad, pressurePadLarge, button, buttonLarge, spotlight,
    //crafting
    blockFurnace, heavyFurnace, gateSwitch,
    //catalysts (pushreact & spinreact & boost)
    discharger, fireCan, campfire;

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

            color = Color.valueOf("9E78DC");
        }};

        sporeSlimeSided = new SidedSlimeBlock("spore-slime-sided", 0){{
            health = 120;
            requirements(Category.distribution, with(Items.sporePod, 3, Items.lead, 3));

            color = Color.valueOf("9E78DC");
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

        cloner = new BlockCloner("cloner"){{
            hasPower = true;
            consumes.power(0.6f);
            requirements(Category.distribution, with(Items.titanium, 30, Items.silicon, 35, Items.phaseFabric, 8));
        }};

        spinner = new Spinner("spinner"){{
            hasPower = true;
            consumes.power(0.3f);
            requirements(Category.distribution, with(Items.titanium, 30, Items.silicon, 35, Items.plastanium, 8));
        }};

        spinnerInert = new Spinner("spinner-inert"){{
            hasPower = true;
            inertia = true;
            consumes.power(0.3f);
            requirements(Category.distribution, with(Items.thorium, 30, Items.silicon, 35, MindyItems.vector, 8));
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
            requirements(Category.effect, with(Items.titanium, 5, Items.thorium, 5));
        }};

        present = new PresentBox("present"){{
            requirements(Category.effect, with(Items.copper, 15, Items.graphite, 15));
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
            consumes.power(0.1f);
            requirements(Category.logic, with(Items.copper, 40, Items.lead, 20, Items.silicon, 10));
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
            lightningChance = 0.05f;
            requirements(Category.defense, with(Items.lead, 6, Items.pyratite, 3));
        }};

        spikeCryo = new Spike("spike-cryo"){{
            health = 500;
            damage = 100f;
            damageSelf = 20f;
            breakSound = MindySounds.freeze;
            status = MindyStatusEffects.icy;
            requirements(Category.defense, with(Items.titanium, 3, Items.phaseFabric, 3, MindyItems.cryonite, 4));
        }};

        spikeClear = new Spike("spike-clear"){{
            health = 400;
            damage = 75f;
            celeste = true;
            requirements(Category.defense, with(Items.metaglass, 3, MindyItems.vector, 3));
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
        }};

        crystalTensor = new EldoofusCrystal("tensor-crystal", MindyItems.tensorRaw){{
            sprites = 6;
            updateEffect = MindyFx.sparkleZeta;
        }};

        // endgame turrets
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
    }
}
