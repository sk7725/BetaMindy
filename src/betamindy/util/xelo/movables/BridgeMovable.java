package betamindy.util.xelo.movables;

import arc.math.geom.*;
import betamindy.util.xelo.*;
import mindustry.gen.*;
import mindustry.world.blocks.distribution.*;

public class BridgeMovable implements Movable{

    private Point2 config;
    private Building building;

    public BridgeMovable(){
        config = new Point2();
    }

    @Override
    public void set(Building building, Point2 direction){
        int link = ((ItemBridge.ItemBridgeBuild)building).link;
        if(link == -1){
            return;
        }
        this.building = building;
        this.config.set(Point2.x(link), Point2.y(link)).add(direction);
    }

    @Override
    public void pushed(){

    }

    @Override
    public void config(){
        if(building != null){
            building.configure(config.pack());
        }
    }

    @Override
    public void reset(){
        building = null;
    }

}
