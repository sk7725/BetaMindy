package betamindy.world.blocks.power;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.entities.*;
import betamindy.world.blocks.logic.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

public class FloodLight extends LogicSpinBlock{
    public float radius = 450f;
    public float stroke = 16f;
    public float brightness = 0.4f;

    //public float elevation = 1f;

    //public float rotateSpeed = 10f;
    //public float angleIncrement = 15f;
    public TextureRegion topRegion;

    public FloodLight(String name){
        super(name);
        configurable = true;
        outlineIcon = true;

        config(Integer.class, (FloodLightBuild tile, Integer value) -> tile.color = value);
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class FloodLightBuild extends LogicSpinBuild implements ExtensionHolder{
        public Extension light;
        public int color = Pal.accent.rgba();

        @Override
        public void created(){
            super.created();

            light = Extension.create();
            light.holder = this;
            light.set(x, y);
            light.add();
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            light.remove();
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.color){
                color = Color.rgba8888((float)p1, (float)p2, (float)p3, 1f);
            }

            super.control(type, p1, p2, p3, p4);
        }

        /*
        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color();

            float z = Draw.z();
            Draw.z(Layer.turret);

            float r = realRotation();
            Drawf.shadow(region, x - elevation, y - elevation, r);
            Draw.rect(region, x, y, r);

            Draw.z(z);
        }*/

        @Override
        public void draw(){
            super.draw();
            Draw.color(color);
            Draw.rect(topRegion, x, y, realRotation());
            Draw.color();
        }

        @Override
        public void drawExt(){
            if(renderer != null && (team == Team.derelict || team == player.team() || state.rules.enemyLights)){
                for(int i = -2; i <= 2; i++){
                    //TODO not a very good way of doing this    ~DINGUS~
                    float e = stroke * efficiency();

                    Tmp.v1.trns(realRotation(), radius, i * e / 1.6f);
                    Tmp.v2.trns(realRotation(), stroke / 2f);
                    renderer.lights.line(x + Tmp.v2.x, y + Tmp.v2.y, x + Tmp.v1.x, y + Tmp.v1.y, e, Tmp.c1.set(color).a(brightness), (1f / (Math.abs(i) + 1f)));
                }
            }
        }

        @Override
        public float clipSizeExt(){
            return 2f * (radius + stroke);
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.pencil, () -> {
                ui.picker.show(Tmp.c1.set(color).a(0.5f), false, res -> configure(res.rgba()));
                deselect();
            }).size(40f);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(color);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            color = read.i();
        }
    }
}
