package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;

import static mindustry.Vars.*;

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
            heat = Mathf.lerpDelta(heat, canConsume() ? 1f : 0f, 0.05f);
            for(Building p : proximity){
                if(!(p.block instanceof Disabler)) p.control(LAccess.enabled, canConsume() ? 0 : 1, 0, 0, 0);
            }
            if(canConsume()) Units.nearby(x - size * tilesize / 2f, y - size * tilesize / 2f, size * tilesize, size * tilesize, u -> {
                u.apply(StatusEffects.disarmed, 20f);
            });
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
            if(!renderer.animateShields) Draw.blend(Blending.additive);
            Fill.square(x, y, 5f * heat);
            Draw.color();
            Draw.blend();
        }
    }
}
