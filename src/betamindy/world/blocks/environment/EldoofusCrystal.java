package betamindy.world.blocks.environment;

import arc.util.*;
import arc.util.io.*;
import mindustry.type.*;

public class EldoofusCrystal extends ScidustryCrystal{
    public EldoofusCrystal(String name, Item item){
        super(name, item);
    }

    public class EldoofusBuild extends ScidustryCrystalBuild {
        public boolean prev = false;
        public boolean state = false;

        @Override
        public boolean updateOutput(){
            if(connections < 1) return false;
            //if(connections == 1) return input(in1);
            if(connections == 2){
                if(input(in1) || input(in2)){
                    if(!prev){
                        prev = true;
                        return true;
                    }
                }
                else{
                    prev = false;
                }
                return false;
            }
            if(connections == 3 || connections == 1){
                if((connections == 3 && input(rotation)) || (connections == 1 && input(in1))){
                    if(!prev){
                        prev = true;
                        //toggle
                        state = !state;
                    }
                }
                else{
                    prev = false;
                }
                return state;
            }
            return ((int)(Time.time / delayTicks) & 1) == 0;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            prev = read.bool();
            state = read.bool();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(prev);
            write.bool(state);
        }
    }
}
