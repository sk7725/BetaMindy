package betamindy.world.blocks.units;

import arc.graphics.g2d.*;
import arc.math.*;
import betamindy.*;
import betamindy.graphics.*;
import betamindy.util.*;
import mindustry.graphics.*;

import static betamindy.BetaMindy.hardmode;

public class TeleportPortal extends TeleportPad {
    public float portalRadius = 14f;
    public TeleportPortal(String name){
        super(name);
        animateNear = false;
    }

    public class TeleportPortalBuild extends TeleportPadBuild {
        @Override
        public void draw(){
            Draw.rect(region, x, y);
            int rank = Math.min(hardmode.level() / HardMode.rankLevel, hardmode.lc1.length - 1);
            Drawm.portal(x, y, heat * portalRadius, hardmode.lc1[rank], hardmode.lc2[rank], 0.35f, 5, 18, Mathf.randomSeed(id, 360f));
            Draw.reset();
        }

        @Override
        public void drawLight(){
            Drawf.light(team, x, y, lightRadius, BetaMindy.hardmode.color(), 0.8f * heat);
        }
    }
}
