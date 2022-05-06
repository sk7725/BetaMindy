package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.graphics.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static betamindy.BetaMindy.*;
import static mindustry.Vars.*;

public class FilterDome extends Block {
    public float range = 120f;
    public float waveInterval = 120f;
    public TextureRegion topRegion, lightRegion;

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
        updateClipRadius(range);
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        lightRegion = atlas.find(name + "-light");
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.sapBullet);
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
            if(canConsume() && within(player, r)){
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
                Draw.alpha(0.8f * (1 - f));
                Lines.circle(x, y, r * f * fr);

                Draw.z(Layer.block + 1f);
                Draw.color(realColor(), Mathf.absin(9f, fr));
                Draw.blend(Blending.additive);
                Draw.rect(topRegion, x, y);
                Draw.blend();

                Draw.z(Layer.effect);
                Draw.color(realColor());
                Lines.stroke(Mathf.absin(8f, 1.5f) * fr);
                Lines.square(x, y, size * tilesize / 2f);
                Draw.alpha(fr);
                Draw.rect(lightRegion, x, y);
                Draw.reset();
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, realColor());
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

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case heat -> heat;
                case config -> filter;
                default -> super.sense(sensor);
            };
        }

        @Override
        public Object senseObject(LAccess sensor) {
            return switch(sensor){
                //senseObject takes priority over sense unless it is a noSensed
                case config -> noSensed;
                default -> super.senseObject(sensor);
            };
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.config){
                int whole = (int)Math.round(p1);
                if(whole < 0 || whole >= filters.filters.length){
                    return;
                }
                configure(whole);
            }
        }
    }
}
