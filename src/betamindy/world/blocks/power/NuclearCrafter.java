package betamindy.world.blocks.power;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

public class NuclearCrafter extends NuclearReactor {
    public ItemStack outputItem;
    public Effect craftEffect = Fx.mineBig;
    public Color heatColor = Pal.turretHeat;

    public NuclearCrafter(String name){
        super(name);
        lightRadius = 125f;
    }

    @Override
    public void setStats(){
        super.setStats();

        if(outputItem != null){
            stats.add(Stat.output, StatValues.items(itemDuration, outputItem));
        }
    }

    @Override
    public boolean outputsItems(){
        return outputItem != null;
    }

    public class NuclearCrafterBuild extends NuclearReactorBuild {
        public float warmup;
        @Override
        public void consume(){
            boolean valid = canConsume();
            super.consume();

            if(outputItem != null && hasItems && valid){
                for(int i = 0; i < outputItem.amount; i++){
                    offload(outputItem.item);
                }

                craftEffect.at(x, y, outputItem.item.color);
            }
        }

        @Override
        public boolean shouldConsume(){
            if(outputItem != null && hasItems){
                if(items.get(outputItem.item) + outputItem.amount > itemCapacity){
                    return false;
                }
            }
            return super.shouldConsume();
        }

        @Override
        public void updateTile(){
            if(enabled) enabled = shouldConsume();
            super.updateTile();
            dumpOutputs();
            warmup = Mathf.lerpDelta(warmup, (canConsume() && enabled && outputItem != null) ? 1f : 0f, 0.035f);
        }

        @Override
        public void draw(){
            super.draw();
            if(warmup > 0.001f){
                Draw.blend(Blending.additive);
                Draw.color(heatColor, warmup * Mathf.absin(3f, 1f));
                Draw.rect(topRegion, x, y);
                Draw.blend();
                Draw.reset();
            }
        }

        public void dumpOutputs(){
            if(hasItems && outputItem != null && timer(timerDump, dumpTime / timeScale)){
                dump(outputItem.item);
            }
        }
    }
}
