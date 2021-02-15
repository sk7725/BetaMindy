package betamindy.world.blocks.power;

import arc.graphics.g2d.Draw;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;

import javax.naming.InterruptedNamingException;

import static arc.Core.atlas;

public class ButtonTap extends PowerBlock {
    public float tapTime = 16f;
    public float powerProduction = 2*60f;

    public ButtonTap(String name){
        super(name);
        quickRotate = false;
        consumesPower = false;
        outputsPower = true;
        targetable = false;
        solid = true;
        sync = true;
        expanded = true;
        configurable = true;
        group = BlockGroup.transportation;

        flags = EnumSet.of(BlockFlag.generator);
    }

    public class ButtonTapBuild extends Building {
        public float heat = 0f;
        public boolean tapped = false;

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
        public float getPowerProduction(){
            return heat > 0.001f ? powerProduction : 0f;
        }

        @Override
        public boolean configTapped(){
            if(tapped) return false;


            configure(getPowerProduction());
            heat = tapTime;
            return false;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            tapped = read.bool();
            if(tapped) heat = 1f;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(tapped);
        }
    }
}
