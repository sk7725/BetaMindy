package betamindy.world.blocks.environment;

import arc.func.*;
import mindustry.gen.*;
import mindustry.type.*;

public class ScidustryCrystal extends Crystal{
    /** Alternative behavior */
    public boolean alternative = false;
    public ScidustryCrystal(String name, Item item){
        super(name, item);
        hasPower = true;
        consumesPower = false;
        outputsPower = false;
        insulated = true;
    }

    public class ScidustryCrystalBuild extends CrystalBuild {
        public int connections = 0;
        public boolean output = false;
        public int in1, in2;

        @Override
        public boolean conductsTo(Building other){
            if(connections <= 1) return true;
            if(connections == 3) return output && nearby(rotation) != other;
            return output;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            connections = 0;
            in1 = in2 = -1;
            for(int i = 0; i < 4; i++){
                if(nearby(i) != null && nearby(i).power != null){
                    connections++;
                    if(in1 == -1){
                        in1 = i;
                    }
                    else in2 = i;
                }
                else{
                    rotation = i;
                }
            }
            if(connections == 3){
                rotation = (rotation + 2) % 4;
                in1 = (rotation + 1) % 4;
                in2 = (rotation + 3) % 4;
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();
        }
    }
}
