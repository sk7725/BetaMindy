/*
	Copyright (c) sk7725 2021
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package betamindy.util;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class Useful {
    private static Tile furthest;
    private static final Rect rect = new Rect();
    private static final Rect hitrect = new Rect();
    private static final Vec2 tr = new Vec2();
    private static final Seq<Unit> units = new Seq<>();
    //private static IntSet collidedBlocks = new IntSet();

    /** Applies stuff to units in a line. Does not affect buildings. Anuke why do you do this to me */
    public static void applyLine(Cons<Unit> acceptor, @Nullable Building source, Effect effect, float x, float y, float angle, float length, boolean wall){
        if(wall) length = findPathLength(x, y, angle, length, source);

        //collidedBlocks.clear();
        tr.trns(angle, length);

        /*Intc2 collider = (cx, cy) -> {
            Building tile = world.build(cx, cy);
            boolean collide = tile != null && collidedBlocks.add(tile.pos());

            if(hitter.damage > 0){
                float health = !collide ? 0 : tile.health;

                if(collide && tile.team != team && tile.collide(hitter)){
                    tile.collision(hitter);
                    hitter.type.hit(hitter, tile.x, tile.y);
                }

                //try to heal the tile
                if(collide && hitter.type.testCollision(hitter, tile)){
                    hitter.type.hitTile(hitter, tile, health, false);
                }
            }
        };

        if(hitter.type.collidesGround){
            seg1.set(x, y);
            seg2.set(seg1).add(tr);
            world.raycastEachWorld(x, y, seg2.x, seg2.y, (cx, cy) -> {
                collider.get(cx, cy);

                for(Point2 p : Geometry.d4){
                    Tile other = world.tile(p.x + cx, p.y + cy);
                    if(other != null && (large || Intersector.intersectSegmentRectangle(seg1, seg2, other.getBounds(Tmp.r1)))){
                        collider.get(cx + p.x, cy + p.y);
                    }
                }
                return false;
            });
        }*/

        rect.setPosition(x, y).setSize(tr.x, tr.y);
        float x2 = tr.x + x, y2 = tr.y + y;

        if(rect.width < 0){
            rect.x += rect.width;
            rect.width *= -1;
        }

        if(rect.height < 0){
            rect.y += rect.height;
            rect.height *= -1;
        }

        float expand = 3f;

        rect.y -= expand;
        rect.x -= expand;
        rect.width += expand * 2;
        rect.height += expand * 2;

        Cons<Unit> cons = e -> {
            e.hitbox(hitrect);

            Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitrect.grow(expand * 2));

            if(vec != null){
                effect.at(vec.x, vec.y, angle, e.hitSize);
                acceptor.get(e);
            }
        };

        units.clear();

        Units.nearby(rect, u -> {
            if(u.checkTarget(true, true)){
                units.add(u);
            }
        });

        //units.sort(u -> u.dst2(hitter)); //is this needed?
        units.each(cons);
    }

    /** Finds the distance to a solid wall. */
    public static float findPathLength(float x, float y, float angle, float length, @Nullable Building source){
        Tmp.v1.trns(angle, length);

        furthest = null;

        boolean found = world.raycast(World.toTile(x), World.toTile(y), World.toTile(x + Tmp.v1.x), World.toTile(y + Tmp.v1.y),
                (tx, ty) -> (furthest = world.tile(tx, ty)) != null && furthest.solid() && (furthest.build == null || furthest.build != source));

        return found && furthest != null ? Math.max(6f, Mathf.dst(x, y, furthest.worldx(), furthest.worldy())) : length;
    }
}
