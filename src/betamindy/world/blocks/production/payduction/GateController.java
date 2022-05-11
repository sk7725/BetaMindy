package betamindy.world.blocks.production.payduction;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import betamindy.world.blocks.production.payduction.PayloadFactory.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

/** Can be configured O or X (power or procs), forces adjacent payfactories to spit out/eat payloads */
public class GateController extends Block {
    public TextureRegion[] statusRegion = new TextureRegion[3];
    public Color[] statusColor = new Color[]{Pal.remove, Pal.accent, Pal.heal};
    public GateController(String name){
        super(name);

        update = true;
        rotate = false;
        size = 2;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 3; i++){
            statusRegion[i] = atlas.find(name + i);
        }
    }

    public class GateControllerBuild extends Building {
        public boolean open(){
            return canConsume();
        }

        public int getMode(){
            //-1: none 0: no payload/inputting 1: cooking 2: outputting
            boolean has = false;
            for(Building p : proximity){
                if(p instanceof PayloadFactoryBuild){
                    final PayloadFactoryBuild fac = ((PayloadFactoryBuild) p);
                    if(fac.gate != this) continue;
                    has = true;
                    if(fac.payload != null){
                        return open() || fac.outputting ? 2 : 1;
                    }
                }
            }
            return has ? 0 : -1;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            for(Building p : proximity){
                if(p.isValid() && p.interactable(team) && (p instanceof PayloadFactoryBuild)){
                    ((PayloadFactoryBuild) p).setGate(this);
                }
            }
        }

        @Override
        public void draw(){
            super.draw();

            if(Time.time % 90 < 30) return;
            int mode = getMode();
            if(mode >= 0){
                Draw.z(Layer.bullet - 0.01f);
                Draw.color(statusColor[mode]);
                Draw.rect(statusRegion[mode], x, y);
                Draw.color();
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Lines.stroke(1f, statusColor[2]);
            for(Building p : proximity){
                if(p.isValid() && (p instanceof PayloadFactoryBuild) && ((PayloadFactoryBuild) p).gate == this){
                    Lines.square(p.x, p.y, p.block.size * tilesize / 2f);
                }
            }
            Draw.color();
        }

        @Override
        public void drawLight(){
            if(Time.time % 90 < 30) return;
            int mode = getMode();
            if(mode >= 0){
                Drawf.light(x, y, 40f, statusColor[mode], 0.8f);
            }
        }
    }
}
