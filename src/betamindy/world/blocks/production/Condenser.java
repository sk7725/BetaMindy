package betamindy.world.blocks.production;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.util.io.*;
import betamindy.ui.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

public class Condenser extends GenericCrafter {
    public Color gasColor = Pal.lightishGray;
    public float gasCapacity = 60f;
    public float gasUse = 15f;

    public Condenser(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.liquidCapacity, ("[accent]" + (int)gasCapacity) + "[] " + Core.bundle.get("unit.gasunits"));
        stats.add(Stat.input, table -> table.add(new GasDisplay(gasName(), gasColor, gasUse, stats.timePeriod, true)));
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("gas", (CondenserBuild entity) -> new Bar(this::gasName, () -> gasColor, () -> Mathf.clamp(entity.gasAmount / gasCapacity)));
    }

    public String gasName(){
        return Core.bundle.get("block." + name + ".gas", "bar.gas");
    }

    public class CondenserBuild extends GenericCrafterBuild {
        public float gasAmount = 0f;

        @Override
        public boolean canConsume(){
            return super.canConsume() && gasAmount >= gasUse;
        }

        @Override
        public void craft(){
            super.craft();
            gasAmount -= gasUse;
            if(gasAmount < 0f) gasAmount = 0f;
        }

        public void acceptGas(float amount){
            if(gasAmount >= gasCapacity) return;
            gasAmount += amount;
            if(gasAmount > gasCapacity) gasAmount = gasCapacity;
        }

        public boolean full(){
            return gasAmount >= gasCapacity;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(gasAmount);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            gasAmount = read.f();
        }
    }
}
