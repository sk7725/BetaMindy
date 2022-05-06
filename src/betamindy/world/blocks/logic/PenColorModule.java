package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.ui.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;

import static arc.Core.atlas;

public class PenColorModule extends Block {
    public float radius = 20f;
    public TextureRegion topRegion;

    public PenColorModule(String name){
        super(name);
        update = true;
        configurable = true;
        saveConfig = true;

        config(Integer.class, (ColorModuleBuild tile, Integer value) -> tile.color = value);
    }

    @Override
    public void init(){
        //double needed for some reason
        lightRadius = radius*2f;
        emitLight = true;
        super.init();
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    public class ColorModuleBuild extends Building implements PenModifier {
        public int color = Color.white.rgba();

        @Override
        public void handlePen(Pen.PenBuild pen){
            pen.color = color;
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.color){
                color = Color.rgba8888((float)p1, (float)p2, (float)p3, 1f);
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void draw(){
            super.draw();
            Draw.color(color);
            Draw.rect(topRegion, x, y);
            Draw.color();
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(MindyUILoader.pane, t -> {
                TextArea a = t.add(new TextArea(Tmp.c1.set(color).toString())).size(160f, 50f).padTop(10f).get();
                a.setColor(Color.white);
                a.setMaxLength(8);
                a.changed(() -> {
                    try{
                        int n = Color.valueOf(Tmp.c2, a.getText()).rgba();
                        if(n != color) configure(n);
                    }
                    catch(Exception ignored){}
                });
                t.button(Icon.pencil, Styles.cleari, () -> {
                    Vars.ui.picker.show(Tmp.c1.set(color), true, res -> configure(res.rgba()));
                }).size(40f).padLeft(8f);
            }).height(60f);
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius * 2f, Tmp.c1.set(color), 0.5f);
        }

        @Override
        public Integer config(){
            return color;
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