package betamindy.content;

import betamindy.world.blocks.distribution.*;
import betamindy.world.blocks.environment.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.type.ItemStack.with;

public class MindyBlocks implements ContentList {
    //environment
    public Block radiation, exoticMatter,
    //pistons
     piston, stickyPiston;

    @Override
    public void load() {
        //TODO: make fires more worse
        radiation = new GlowPowder("radiation", 0){{
            color1 = Pal.lancerLaser;
            color2 = Pal.heal;

            status = StatusEffects.burning;
            effect = MindyFx.directionalSmoke;
        }};

        //TODO: new effects
        exoticMatter = new GlowPowder("exotic-matter", 1){{
            color1 = Team.crux.color;
            color2 = Pal.sapBullet;
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
    }
}
