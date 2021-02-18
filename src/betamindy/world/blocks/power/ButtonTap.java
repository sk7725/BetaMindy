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

//gk Tkr rkfdjvgodigksp enlwlfurh
//TODO heal shar
public class ButtonTap extends PowerBlock {
    public float tapTime = 16f;
    public float powerProduction = 2 * 60f;

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
            Draw.rect(atlas.find(name + (heat > 0.001f ? "-trig":"")), x, y);//어떤 약을 빠셨나요 그것이 궁금합니다 4
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(heat >= 0f) heat -= edelta();
        }

        @Override
        public float getPowerProduction(){
            return heat > 0.001f ? powerProduction : 0f;
        }//어떤 약을 빠셨나요 그것이 궁금합니다 3

        @Override
        public boolean configTapped(){
            if(tapped) return false;

            //어떤 약을 빠셨나요 그것이 궁금합니다 // 샤로인요
            configure(getPowerProduction());
            heat = tapTime;
            return false;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            tapped = read.bool();
            if(tapped) heat = 1f;//어떤 약을 빠셨나요 그것이 궁금합니다 2
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(tapped);
        }
    }
}
