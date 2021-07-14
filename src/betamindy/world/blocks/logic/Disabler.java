package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import betamindy.world.blocks.production.payduction.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;

import static mindustry.Vars.tilesize;

public class Disabler extends Block {
    public Color zoneColor = Pal.health;
    public Disabler(String name){
        super(name);
        update = true;
        solid = false;
    }

    public class DisablerBuild extends Building {
        public float heat = 0f;

        @Override
        public void updateTile(){
            heat = Mathf.lerpDelta(heat, consValid() ? 1f : 0f, 0.05f);
            for(Building p : proximity){
                if(!(p.block instanceof Disabler)) p.control(LAccess.enabled, consValid() ? 0 : 1, 0, 0, 0);
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Lines.stroke(1f, Pal.remove);
            for(Building p : proximity){
                if(p.isValid() && !(p.block instanceof Disabler)){
                    Lines.square(p.x, p.y, p.block.size * tilesize / 2f);
                }
            }
            Draw.color();
        }

        @Override
        public void draw(){
            super.draw();
            if(heat < 0.001f) return;
            Draw.z(Layer.buildBeam);
            Draw.color(zoneColor);
            Fill.square(x, y, 5f * heat);
            Draw.color();
        }

        @Override
        public void unitOn(Unit unit){
            unit.apply(StatusEffects.disarmed, 20f);
        }
    }
}
