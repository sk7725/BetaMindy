package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
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

public class PenStrokeModule extends Block {
    public int maxStroke = 8;
    public float step = 0.5f;

    public TextureRegion topRegion;
    public float radius = 20f;

    public PenStrokeModule(String name){
        super(name);

        update = true;
        configurable = true;
        saveConfig = true;
        lightColor = Pal.accent;

        config(Integer.class, (StrokeModuleBuild tile, Integer value) -> tile.stroke = Mathf.clamp(value, 0, maxStroke));
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public class StrokeModuleBuild extends Building implements PenModifier {
        public int stroke = 1;

        public float realStroke(){
            return (stroke + 1) * step;
        }

        @Override
        public void handlePen(Pen.PenBuild pen){
            pen.stroke = realStroke();
        }

        @Override
        public void draw(){
            super.draw();
            Draw.z(Layer.effect - 0.1f);
            Lines.stroke(0.3f + Mathf.absin(8f, 0.4f), lightColor);
            Lines.circle(x, y, realStroke());
            Draw.color();
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(MindyUILoader.pane, t -> {
                t.label(() -> String.format("%.1f", realStroke())).size(40f, 60f);
                t.slider(0, maxStroke, 1, stroke, f -> {
                    if((int)f != stroke) configure((int)f);
                }).size(200f, 40f);
            });
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius * 2f, lightColor, 0.9f);
        }

        @Override
        public Integer config(){
            return stroke;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(stroke);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            stroke = read.s();
        }
    }
}
