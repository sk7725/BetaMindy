package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;

import static arc.Core.atlas;

public class ProcessorFan extends ProcessorCooler{
    public TextureRegion spinnerRegion;
    public float spinSpeed = 1.7f;

    public ProcessorFan(String name){
        super(name);

        canOverdrive = false;
        //TODO ambientsound
    }

    @Override
    public void load(){
        super.load();
        spinnerRegion = atlas.find(name + "-spinner");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, spinnerRegion, topRegion};
    }

    public class ProcessorFanBuild extends ProcessorCoolerBuild{
        @Override
        public void draw(){
            Draw.rect(region, x, y);
            if(heat > 0.01f){
                Draw.blend(Blending.additive);
                Draw.color(heatColor, heat * Mathf.absin(9f, 1f));
                Draw.rect(heatRegion, x, y);
                Draw.blend();
                Draw.color();
            }
            Draw.rect(spinnerRegion, x, y, Time.time * spinSpeed);
            if(useTopRegion) Draw.rect(topRegion, x, y);
        }
    }
}
