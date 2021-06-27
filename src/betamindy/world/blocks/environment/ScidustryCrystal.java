package betamindy.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.power.*;

public class ScidustryCrystal extends Crystal{
    /** Alternative behavior. */
    public boolean alt = false;
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

        public float inputValue(int r){
            Building b = nearby(r);
            if(b == null || b.power == null) return 0;
            return b.power.graph.getLastPowerProduced() - b.power.graph.getLastPowerNeeded();
        }

        public boolean input(int r){
            Building b = nearby(r);
            if(b == null || b.power == null) return false;
            return b.power.graph.getLastPowerProduced() - b.power.graph.getLastPowerNeeded() > 0.016f;
        }

        public boolean updateOutput(){
            if(connections < 1) return false;
            if(connections == 1) return input(in1);
            if(connections == 2) return input(in1) || input(in2);
            if(connections == 3) return input(rotation);
            return (input(0) || input(2)) && (input(1) || input(3));
        }

        public boolean updateOutputAlt(){
            if(connections < 1) return false;
            if(connections == 1) return !input(in1);
            if(connections == 2) return input(in1) && input(in2);
            if(connections == 3) return !input(rotation);
            return (input(0) && input(2)) || (input(1) && input(3));
        }

        @Override
        public void updateTile(){
            super.updateTile();
            boolean next = alt ? updateOutputAlt() : updateOutput();
            if(next != output){
                output = next;
                PowerGraph newGraph = new PowerGraph();
                int oldGraph = power.graph.getID();
                //reflow from crystal
                newGraph.reflow(this);

                for(Building p : proximity){
                    if(p.power != null && p.power.graph.getID() == oldGraph){
                        //reflow from previously connected but now unconnected tiles
                        PowerGraph og = new PowerGraph();
                        og.reflow(p);
                    }
                }
            }
        }

        @Override
        public void beforeDraw(){
            if(output && connections > 0 && !Mathf.zero(power.graph.getSatisfaction())){
                Draw.z(Layer.bullet - 0.01f);
                Draw.mixcol(Tmp.c1.set(item.color).mul(1.2f), 0.7f);
            }
            else super.beforeDraw();
        }
    }
}
