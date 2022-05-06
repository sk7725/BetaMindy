package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import betamindy.content.*;

import static arc.Core.*;

public class ProcessorFan extends ProcessorCooler{
    public TextureRegion spinnerRegion;
    public float spinSpeed = 28f;

    public ProcessorFan(String name){
        super(name);

        canOverdrive = false;
        ambientSound = MindySounds.coolingFan;
        ambientSoundVolume = 0.5f;
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
        public float timeSpun = 0f;

        @Override
        public void updateTile(){
            timeSpun += edelta() * heat;
            super.updateTile();
        }

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
            Draw.rect(spinnerRegion, x, y, timeSpun * spinSpeed);
            if(useTopRegion) Draw.rect(topRegion, x, y);
        }

        @Override
        public boolean shouldAmbientSound(){
            return canConsume() && heat > 0.1f;
        }
    }
}
