package betamindy.world.blocks.power;

import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;

public class ButtonPad extends PowerBlock {
    public float pushTime = 16f;
    public float powerProduction = 2*60f;

    public ButtonPad(String name){
        super(name);
        quickRotate = false;
        consumesPower = false;
        outputsPower = true;
        targetable = false;
        solid = false;
        sync = true;
        expanded = true;
        group = BlockGroup.transportation;

        flags = EnumSet.of(BlockFlag.generator);
    }

    public class ButtonPadBuild extends Building {
        public float heat = 0f;

        @Override
        public void draw() {
            super.draw();
            Draw.rect(atlas.find(name + (heat > 0.001f ? "-trig":"")), tile.drawx(), tile.drawy());
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(heat >= 0f) heat -= edelta();
        }

        @Override
        public void unitOn(Unit unit){
            if(heat == 0) Sounds.place.at(tile.worldx(), tile.worldy(), 1.2f);
            heat = pushTime;
        }

        @Override
        public float getPowerProduction(){
            return heat > 0.001f ? powerProduction : 0f;
        }

        @Override
        public void placed() {
            super.placed();
            heat = 0;
        }
    }
}
