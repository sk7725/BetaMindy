package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.graphics.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static betamindy.BetaMindy.*;
import static mindustry.Vars.*;

public class FilterDome extends Block {
    public float range = 120f;
    public float waveInterval = 120f;
    public TextureRegion topRegion;

    public FilterDome(String name){
        super(name);
        update = solid = saveConfig = configurable = true;
        config(Integer.class, (FilterDomeBuild build, Integer i) -> {
            if(i >= 0 && i < filters.filters.length) build.setFilter(i);
        });
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, range * 2f + 16f);
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.logicBlocks);
    }

    public class FilterDomeBuild extends Building {
        public int filter = 0;
        public float r = 0f, heat = 0f;
        public final Color lastColor = Pal2.clearWhite.cpy();

        public float realRange(){
            return efficiency() * range;
        }

        public void setFilter(int i){
            if(filter == i) return;
            heat = 1f;
            lastColor.set(filters.filters[filter].color);
            filter = i;
        }

        public Color realColor(){
            Tmp.c2.set(filters.filters[Math.min(filters.filters.length - 1, filter)].color);
            if(heat > 0.05f) Tmp.c2.lerp(lastColor, heat);
            return Tmp.c2;
        }

        @Override
        public void updateTile(){
            if(headless || player.unit() == null) return;
            r = Mathf.lerpDelta(r, realRange(), 0.05f);
            if(heat > 0.05f) heat = Mathf.lerpDelta(heat, 0f, 0.09f);
            if(consValid() && within(player, r)){
                BetaMindy.filters.enableFilter(filter);
            }
        }

        @Override
        public void draw(){
            super.draw();
            if(r > 0.1f){
                float fr = r / range;
                Draw.z(Layer.shields);
                Lines.stroke(5f * fr);
                Lines.circle(x, y, r);
                Draw.color(realColor(), 0.9f);
                Lines.stroke(10f * fr);
                Lines.circle(x, y, r);
                Draw.alpha(0.5f);
                Lines.stroke(14f * fr);
                Lines.circle(x, y, r);
                float f = Time.time % waveInterval / waveInterval;
                Lines.stroke(14f * (1f - f));
                Draw.alpha(1 - f);
                Lines.circle(x, y, r * f * fr);
                Draw.z(Layer.effect);
                Draw.color(realColor());
                Lines.stroke(Mathf.absin(8f, 1.5f) * fr);
                Lines.square(x, y, size * tilesize / 2f);
                Draw.alpha(fr);
                Draw.rect(topRegion, x, y);
                Draw.reset();
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, r, realColor());
            drawPlaceText(filters.filters[filter].name, tile.x, tile.y, true);
            Draw.reset();
        }

        @Override
        public void buildConfiguration(Table table){
            //todo better ui
            table.table(Tex.pane, t -> {
                t.button(Icon.downOpen, Styles.cleari, () -> {
                    configure(filter - 1);
                }).size(45).disabled(but -> filter <= 0);
                t.button(Icon.upOpen, Styles.cleari, () -> {
                    configure(filter + 1);
                }).size(45).disabled(but -> filter >= filters.filters.length - 1);
            });
        }

        @Override
        public Object config(){
            return filter;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(filter);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            filter = read.s();
        }
    }
}
