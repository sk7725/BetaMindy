package betamindy.world.blocks.power;

import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import betamindy.ui.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.power.*;

import static arc.Core.*;
import static arc.math.geom.Geometry.*;

public class Capacitor extends Battery {
    public int maxDelay = 6; //inclusive
    public float delayTicks = 8f;
    public float powerCapacity;

    public TextureRegion arrowIcon, arrowEnd, arrow, arrowOutlineEnd, arrowOutline;

    public Capacitor(String name, float powerCapacity){
        super(name);
        rotate = true;
        quickRotate = false;
        configurable = saveConfig = true;
        //insulated = true;
        this.powerCapacity = powerCapacity;
        consumePowerBuffered(powerCapacity);

        config(Integer.class, (CapacitorBuild build, Integer i) -> {
            if(i >= 1 && i <= maxDelay) build.delay = i;
        });

        configClear((CapacitorBuild build) -> {
            build.delay = 1;
        });
    }

    @Override
    public void load(){
        super.load();

        arrow = atlas.find(name + "-arrow", "betamindy-capacitor-arrow");
        arrowEnd = atlas.find(name + "-arrow2", "betamindy-capacitor-arrow2");
        arrowOutline = atlas.find(name + "-arrow-outline", "betamindy-capacitor-arrow-outline");
        arrowOutlineEnd = atlas.find(name + "-arrow2-outline", "betamindy-capacitor-arrow2-outline");
        arrowIcon = atlas.find(name + "-arrow-icon", "betamindy-capacitor-arrow-icon");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, arrowIcon};
    }

    public class CapacitorBuild extends BatteryBuild {
        public int delay = 1;
        public float charge;
        public boolean outputting = false;

        public float getPowerCap(){
            return powerCapacity;
        }

        @Override
        public void updateTile(){
            Building in = nearby((rotation + 2) % 4);
            if(in == null || in.power == null || in.power.graph.getID() == power.graph.getID()){
                outputting = false;
                power.status = 0f;
                return;
            }

            if((!(in instanceof BatteryBuild) && in.power.graph.getLastPowerProduced() + in.power.graph.getLastPowerStored() >= powerCapacity / 60f) || (in instanceof CapacitorBuild cap && cap.outputting && cap.getPowerCap() >= powerCapacity)){
                //in.power.graph.transferPower(-powerCapacity);
                if(charge >= delay * delayTicks){
                    charge = delay * delayTicks;
                    outputting = true;
                }
                else{
                    charge += delta();
                }
            }
            else{
                if(charge <= 0f){
                    charge = 0f;
                    outputting = false;
                }
                else{
                    charge -= delta();
                }
            }

            power.status = outputting ? 1f : 0f;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.z(Layer.blockOver - 0.1f);
            Draw.rect(arrowOutlineEnd, x, y, rotation * 90);
            for(int i = 0; i < delay - 1; i++){
                Draw.rect(arrowOutline, x - d4x(rotation) * i, y - d4y(rotation) * i, rotation * 90);
            }

            Draw.z(Layer.blockOver);
            for(int i = 0; i < delay - 1; i++){
                Draw.color(charge >= (delay - i) * delayTicks ? fullLightColor : emptyLightColor);
                Draw.rect(arrow, x - d4x(rotation) * i, y - d4y(rotation) * i, rotation * 90);
            }
            Draw.color(outputting ? fullLightColor : emptyLightColor);
            Draw.rect(arrowEnd, x, y, rotation * 90);
            Draw.reset();
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(MindyUILoader.pane, t -> {
                t.label(() -> String.format("%dt", delay * (int)delayTicks)).size(40f, 60f);
                t.slider(1, maxDelay, 1, delay, f -> {
                    if((int)f != delay) configure((int)f);
                }).size(200f, 40f);
            });
        }

        @Override
        public boolean conductsTo(Building other){
            return tile.nearbyBuild((rotation + 2) % 4) != other;
        }

        @Override
        public Object config(){
            return delay;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(delay);
            write.f(charge);
            write.bool(outputting);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            delay = read.s();
            charge = read.f();
            outputting = read.bool();
        }
    }
}
