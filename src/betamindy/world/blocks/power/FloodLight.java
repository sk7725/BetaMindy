package betamindy.world.blocks.power;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.world.blocks.logic.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class FloodLight extends LogicSpinBlock{
    public float radius = 450f;
    public float stroke = 24f;
    public float brightness = 0.45f;

    //public float elevation = 1f;

    //public float rotateSpeed = 10f;
    //public float angleIncrement = 15f;
    public TextureRegion topRegion, lightRegion;

    public FloodLight(String name){
        super(name);
        configurable = true;
        outlineIcon = true;

        config(Integer.class, (FloodLightBuild tile, Integer value) -> tile.color.set(value));
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, (radius + stroke + tilesize) * 2f);
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        lightRegion = atlas.find(name + "-light");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class FloodLightBuild extends LogicSpinBuild {
        public Color color = Pal.accent.cpy();

        /*@Override
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
         */

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.color){
                color.set((float)p1, (float)p2, (float)p3, 1f);
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void draw(){
            super.draw();
            float r = realRotation();
            Draw.color(color);
            Draw.rect(topRegion, x, y, r);
            Draw.blend(Blending.additive);
            Draw.color(color, Color.white, 0.4f + Mathf.absin(17f, 0.2f));
            Draw.alpha(efficiency());
            Draw.rect(lightRegion, x, y, r);
            Draw.blend();
            Draw.color();
            drawExt();
            Draw.reset();
        }

        public void drawExt(){
            if(renderer != null){
                for(int i = -2; i <= 2; i++){
                    //GLENN: not a very good way of doing this    ~DINGUS~
                    float e = stroke * efficiency();

                    Tmp.v1.trns(realRotation(), radius, i * e / 1.5f);
                    Tmp.v2.trns(realRotation(), stroke / 2f);
                    renderer.lights.line(x + Tmp.v2.x, y + Tmp.v2.y, x + Tmp.v1.x, y + Tmp.v1.y, e, color, (brightness / (Math.abs(i) + 1f)));
                }
            }
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.pencil, () -> {
                ui.picker.show(Tmp.c1.set(color).a(0.5f), false, res -> configure(res.rgba()));
                deselect();
            }).size(40f);
        }

        @Override
        public Integer config(){
            return color.rgba();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(color.rgba());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            color.set(read.i());
        }
    }
}
