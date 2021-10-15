package betamindy.world.blocks.distribution;

import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.Building;
import mindustry.world.*;
import mindustry.world.meta.*;
import betamindy.world.blocks.distribution.Piston.*;

public class PistonArm extends Block {
    public Piston pistonBlock;
    public PistonArm(Piston piston){
        super(piston.name + "-arm");
        pistonBlock = piston;

        solid = true;
        update = true;

        breakable = false;
        destructible = false;
        rebuildable = false;
        targetable = false;

        hasShadow = false;
        hasItems = false;
        hasPower = false;
        rotate = true;
        quickRotate = false;
        buildVisibility = BuildVisibility.hidden;
        enableDrawStatus = false;
        drawDisabled = false;
    }

    @Override
    public boolean isHidden(){
        return true;
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    public class PistonArmBuild extends Building {
        public @Nullable PistonBuild piston;
        @Override
        public void updateTile(){
            //NO MORE BLACKHOLES
            if(piston == null || !piston.isValid()) tile.remove();
        }
        @Override
        public void draw(){
            //does not draw
        }
        @Override
        public void display(Table table){
        }

        @Override
        public void damage(float damage){
            if(dead()) return;
            if(piston != null && piston.isValid() && !piston.dead()) piston.damage(damage);
        }
        @Override
        public void killed(){
            //For the love of routers, stop snapping off my piston arms EoD
            super.killed();
            if(piston != null && piston.isValid() && !piston.dead()) piston.killed();
        }

        @Override
        public boolean canPickup(){
            return false;
        }
    }
}