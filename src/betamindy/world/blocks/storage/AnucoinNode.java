package betamindy.world.blocks.storage;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

/** THIS IS USELESS, PLEASE IGNORE, USE AS TEMPLATE */
public class AnucoinNode extends Block {
    public int range = 60;
    public TextureRegion laser, laserEnd;

    public AnucoinNode(String name){
        super(name);

        configurable = solid = update = expanded = true;
    }

    @Override
    public void load() {
        super.load();

        laser = Core.atlas.find("betamindy-anuke-laser");
        laserEnd = Core.atlas.find("betamindy-anuke-laser-end");
    }

    public class AnucoinNodeBuild extends Building {
        public Seq<Integer> connections = new Seq<>();

        protected boolean overlaps(float srcx, float srcy, Tile other, Block otherBlock, float range){
            return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), Tmp.r1.setCentered(other.worldx() + otherBlock.offset, other.worldy() + otherBlock.offset,
                    otherBlock.size * Vars.tilesize, otherBlock.size * Vars.tilesize));
        }

        protected boolean overlaps(float srcx, float srcy, Tile other, float range){
            return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), other.getHitbox(Tmp.r1));
        }

        protected boolean overlaps(Building src, Building other, float range){
            return overlaps(src.x, src.y, other.tile(), range);
        }

        public boolean linkValid(Building tile, Building link){
            if(tile == link || link == null || tile.team != link.team) return false;

            return overlaps(tile, link, range * Vars.tilesize) && link instanceof Shop.ShopBuild;
        }

        public void drawLaser(Team team, float x1, float y1, float x2, float y2, int size1, int size2){
            float angle1 = Angles.angle(x1, y1, x2, y2),
                    vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                    len1 = size1 * Vars.tilesize / 2f - 1.5f, len2 = size2 * Vars.tilesize / 2f - 1.5f;

            Drawf.laser(team, laser, laserEnd, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, 0.25f);
        }

        @Override
        public boolean onConfigureTileTapped(Building other){
            if(other != null) {
                if (connections.indexOf(other.pos()) != -1) {
                    connections.remove(connections.indexOf(other.pos()));
                }

                if (linkValid(this, other)) {
                    connections.add(other.pos());
                    return false;
                }
            }

            return this != other;
        }

        @Override
        public void update() {
            super.update();

            Log.info("g");
            for(int pos : connections){
                Point2 point = Point2.unpack(pos);
                Tile tile = Vars.world.tile(point.x, point.y);
                if(tile != null && tile.build == null) connections.remove(connections.indexOf(pos));
            }
        }

        @Override
        public void draw() {
            super.draw();

            if(Mathf.zero(Renderer.laserOpacity)) return;

            Draw.z(Layer.power);


            for(int i = 0; i < connections.size; i++){
                int pos = connections.get(i);
                Point2 point = Point2.unpack(pos);
                Tile tile = Vars.world.tile(point.x, point.y);

                if(tile != null) {
                    Shop.ShopBuild link = (Shop.ShopBuild) tile.build;

                    if(!linkValid(this, link)) continue;

                    drawLaser(team, x, y, link.x, link.y, size, link.block.size);
                }
            }

            Draw.reset();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(connections.size);
            for(int pos : connections){
                write.i(pos);
            }
        }

        @Override
        public void read(Reads read) {
            super.read(read);

            int am = read.i();
            for(int i = 0; i < am; i++){
                connections.add(read.i());
            }
        }
    }
}
