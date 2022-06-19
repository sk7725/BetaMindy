package betamindy.world.blocks.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import betamindy.*;
import betamindy.graphics.*;
import betamindy.util.*;
import mindustry.graphics.*;

import static arc.Core.atlas;
import static betamindy.BetaMindy.hardmode;
import static mindustry.Vars.*;

public class TeleportPortal extends TeleportPad {
    public float portalRadius = 14f;
    public TextureRegion shadowRegion;

    public TeleportPortal(String name){
        super(name);
        animateNear = false;
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, portalRadius * 2f + size * tilesize * 2f);
    }

    @Override
    public void load() {
        super.load();
        shadowRegion = atlas.find(name + "-shadow");
    }

    @Override
    public void loadIcon() {
        super.loadIcon();
        uiIcon = atlas.find(name + "-icon", region);
    }

    public class TeleportPortalBuild extends TeleportPadBuild {
        @Override
        public void draw(){
            if(!hasShadow){
                Draw.z(Layer.block - 0.99f);
                Draw.color(Pal.shadow);
                Draw.rect(shadowRegion, x, y);
            }
            Draw.color();
            Draw.z(Layer.block);
            Draw.rect(region, x, y);
            int rank = Math.min(hardmode.level() / HardMode.rankLevel, hardmode.lc1.length - 1);
            Drawm.portal(x, y, heat * portalRadius, hardmode.lc1[rank], hardmode.lc2[rank], 0.35f, 5, 18, Mathf.randomSeed(id, 360f));
            Draw.reset();
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius, BetaMindy.hardmode.color(), 0.8f * heat);
        }
    }
}
