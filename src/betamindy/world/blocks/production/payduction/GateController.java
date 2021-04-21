package betamindy.world.blocks.production.payduction;

import mindustry.gen.*;
import mindustry.world.*;

/** Can be configured O or X (click or procs), forces adjacent payfactories to spit out/eat payloads */
public class GateController extends Block {
    public GateController(String name){
        super(name);

        update = true;
        rotate = true;
        quickRotate = false;
        size = 2;
    }
    public class GateControllerBuild extends Building {
        public boolean open(){
            return false; //TODO
        }
    }
}
