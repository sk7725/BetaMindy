package betamindy.world.blocks.logic;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.logic.LogicBlock.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class LinkPinner extends Block {
    public float range = 8 * 10;
    public TextureRegion laser, laserEnd;
    public Color color = Pal.sapBullet;

    public LinkPinner(String name){
        super(name);
        update = true;
        solid = true;
        rotate = false;
        configurable = true;
        saveConfig = false;

        config(Integer.class, (LinkPinnerBuild entity, Integer i) -> {
            Building other = world.build(i);
            if(other == null) return;
            if(entity.link != null && entity.link.id == other.id){
                //unlink
                entity.link = null;
            }
            else if(entity.validLink(other)){
                //link
                entity.linkTo((LogicBuild) other);
            }
        });

        config(Point2.class, (LinkPinnerBuild entity, Point2 i) -> {
            if(entity.link == null){
                entity.cachePos = Point2.pack(i.x + entity.tileX(), i.y + entity.tileY());
                entity.cacheLink = entity.cachePos != entity.pos();
            }
        });
    }

    @Override
    public void init(){
        super.init();

        clipSize = Math.max(clipSize, range * 2f + size * tilesize * 2f);
    }

    @Override
    public void load() {
        super.load();
        laser = atlas.find(name + "-laser", "betamindy-linkpin-laser");
        laserEnd = atlas.find(name + "-laser-end", "betamindy-linkpin-laser-end");
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.linkRange, range / 8, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.circles(x*tilesize + offset, y*tilesize + offset, range);
    }

    protected void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2){
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f - 1.5f, len2 = size2 * tilesize / 2f - 1.5f;

        Drawf.laser(laser, laserEnd, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, 0.25f);
    }

    public class LinkPinnerBuild extends Building implements PushReact{
        public @Nullable LogicBuild link;
        public float heat = 0f;
        public boolean changed = false;

        protected boolean cacheLink = false;
        protected int cachePos = 0;

        @Override
        public void updateTile(){
            if(link != null && changed){
                link.updateCode(link.code);
                changed = false;
            }

            super.updateTile();
            if(heat > 0.01f) heat -= delta();

            if(link == null && cacheLink && !Vars.net.client()){
                //there is a remembered link, try to connect to it
                Building b = world.build(cachePos);
                if(b != null && !(b.block instanceof ConstructBlock)){
                    if(b instanceof LogicBuild){
                        //attempt reconnection
                        if(validLink(b)){
                            linkTo((LogicBuild) b);
                        }
                    }
                    else{
                        cacheLink = false;
                    }
                }
            }

            if(link != null && !validLink()){
                //sever the line but remember the destination
                cacheLink = true;
                cachePos = link.pos();
                link = null;
            }
        }

        public void linkTo(LogicBuild other){
            link = other;
            cacheLink = false;
        }

        public void pushed(int dir){
            if(link != null && enabled && link.links.any()){
                link.links.each(l -> {
                    l.x += Geometry.d4x(dir);
                    l.y += Geometry.d4y(dir);
                });
                heat = 30f;
                changed = true;
            }
        }

        @Override
        public void draw() {
            super.draw();
            if(enabled && link != null && link.isValid()){
                Draw.z(Layer.blockOver);
                Draw.color(color);
                Draw.blend(Blending.additive);
                //Lines.square(x, y, 4f);

                float o = (Time.time * 0.1f) % 7f - 3.5f;
                Lines.stroke(1f - Math.abs(o / 3.5f));
                Lines.line(x - 3.5f, y + o , x + 3.5f, y + o );
                Lines.line(x + o , y - 3.5f, x + o , y + 3.5f);

                Draw.color();
                Draw.blend();
            }
            if(link != null && link.isValid()){
                Draw.z(Layer.power);
                drawLaser(x, y, link.x, link.y, size, link.block.size);

                if(heat > 0.01f){
                    Draw.color(color, Color.white, heat / 30f);
                    Draw.alpha(heat / 30f);
                    Lines.stroke(0.8f);
                    link.links.each(l -> {
                        if(l.active) Lines.square(l.x * tilesize, l.y * tilesize, 6f, 45f);
                    });
                }
                Draw.reset();
            }
        }

        public boolean validLink(Building other){
            return other != null && other.isValid() && other.team == team && other.within(this, range + other.block.size*tilesize/2f) && (other instanceof LogicBuild);
        }

        public boolean validLink(){
            return link != null && link.isValid() && link.team == team && link.within(this, range + link.block.size*tilesize/2f);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(this == other){
                deselect();
                return false;
            }

            if(validLink(other)){
                configure(other.pos());
                return false;
            }

            return super.onConfigureBuildTapped(other);
        }

        @Override
        public void drawConfigure(){
            super.drawConfigure();

            Drawf.circles(x, y, range);
            if(link == null) return;

            Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.accent);
            LogicLink cursor = null;
            Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);

            for(LogicLink l : link.links){
                if(cursor == null && hoverTile != null && l.x == hoverTile.x && l.y == hoverTile.y) cursor = l;
                else drawLink(l);
            }

            //draw top text on separate layer
            for(LogicLink l : link.links){
                if(cursor == null) drawLinkText(l);
            }

            if(cursor != null){
                drawLink(cursor);
                drawLinkText(cursor);
            }
        }

        public void drawLink(LogicLink l){
            Building build = world.build(l.x, l.y);
            if(build != null) Drawf.square(build.x, build.y, build.block.size * tilesize / 2f + 1f, l.active ? Pal.place : Pal.darkerGray);
            else Drawf.square(l.x * tilesize, l.y * tilesize, tilesize / 2f + 1f, l.active ? Pal.place : Pal.darkerGray);
        }
        public void drawLinkText(LogicLink l){
            Building build = world.build(l.x, l.y);
            if(build != null) build.block.drawPlaceText(l.name, build.tileX(), build.tileY(), link.validLink(build));
            else drawPlaceText(l.name, l.x, l.y, false);
        }

        @Override
        public Point2 config(){
            if(link == null) return Point2.unpack(pos());
            return Point2.unpack(link.pos()).sub(tile.x, tile.y);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(link != null);
            if(link != null){
                write.i(link.pos());
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(read.bool()){
                cachePos = read.i();
                cacheLink = true;
            }
        }

        @Override
        public Object senseObject(LAccess sensor){
            switch(sensor){
                case config: return link;
                default: return super.senseObject(sensor);
            }
        }
    }
}
