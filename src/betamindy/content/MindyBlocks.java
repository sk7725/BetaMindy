package betamindy.content;

import arc.graphics.*;
import betamindy.world.blocks.defense.turrets.PayloadTurret;
import betamindy.world.blocks.distribution.*;
import betamindy.world.blocks.environment.*;
import betamindy.world.blocks.power.*;
import betamindy.world.blocks.temporary.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.type.ItemStack.with;

public class MindyBlocks implements ContentList {
    //environment
    public static Block radiation, exoticMatter, present,
    //pistons
     piston, stickyPiston, sporeSlime, sporeSlimeSided, accel,
    //payloads
     payCannon, payCatapult, blockWorkshop, blockPacker, blockUnpacker;

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

        present = new PresentBox("present"){{
            requirements(Category.effect, with(Items.copper, 15, Items.graphite, 15));
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

        accel = new AccelBlock("accel"){{
            health = 150;
            requirements(Category.power, with(Items.titanium, 25, Items.silicon, 20, Items.plastanium, 3));
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
            requirements(Category.turret, with(Items.copper, 1500, Items.titanium, 900, Items.silicon, 650, Items.plastanium, 390, Items.phaseFabric, 180, Items.surgeAlloy, 165));
        }};

        blockWorkshop = new BetterBlockForge("block-workshop"){{
            health = 80;
            size = 5;
            minBlockSize = 3;
            maxBlockSize = 4;
            buildSpeed = 0.2f;
            liquidCapacity = 120f;
            consumes.power(3.25f);
            consumes.liquid(Liquids.cryofluid, 1f);
            requirements(Category.production, with(Items.thorium, 160, Items.phaseFabric, 60, Items.surgeAlloy, 45));
        }};

        blockPacker = new BetterBlockLoader("block-packer"){{
            health = 80;
            size = 5;
            maxBlockSize = 4;
            consumes.power(3.25f);
            requirements(Category.production, with(Items.thorium, 160, Items.plastanium, 65, Items.phaseFabric, 30));
        }};

        blockUnpacker = new BetterBlockUnloader("block-unpacker"){{
            health = 80;
            size = 5;
            maxBlockSize = 4;
            consumes.power(3.25f);
            requirements(Category.production, with(Items.thorium, 160, Items.plastanium, 30, Items.phaseFabric, 65));
        }};
    }
}
