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

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.*;
import betamindy.*;
import betamindy.content.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.type.Liquid;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import java.util.*;

import static mindustry.Vars.*;

public class Useful {
    private static Tile furthest;
    private static final Rect rect = new Rect();
    private static final Rect hitrect = new Rect();
    private static final Vec2 tr = new Vec2();
    private static final Seq<Unit> units = new Seq<>();
    private static final TextField scrollLocker = (Vars.headless) ? null : new TextField();
    //private static IntSet collidedBlocks = new IntSet();

    private static Vec2 cameraPos = new Vec2();
    private static boolean camLock;

    /** Returns true once every few ticks. Unreliable, use only in trivial stuff like graphics. */
    public static boolean interval(float duration, float offset){
        if(Time.time + offset < Time.delta) return false;
        return ((int) ((Time.time + offset - Time.delta) / duration)) < ((int) ((Time.time + offset) / duration));
    }

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

    public static Liquid getBestCoolant(){
        return content.liquids().max(Comparator.comparingDouble(liquid -> liquid.heatCapacity));
    }

    public static boolean jolly(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Time.millis());
        if(c.get(Calendar.MONTH) != Calendar.DECEMBER) return false;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return d >= 23; //12/23 ~ 12/31
    }

    /** -1null 0W 1A 2S 3D */
    public static byte wasd(){
        float ya = Core.input.axis(Binding.move_y);
        float xa = Core.input.axis(Binding.move_x);
        if(BetaMindy.mui.touchpad != null) BetaMindy.mui.touchpad.showStart();

        if(Math.abs(ya) > 0.2f){
            if(ya > 0) return 0;
            return 2;
        }
        if(Math.abs(xa) > 0.2f){
            if(xa > 0) return 3;
            return 1;
        }
        return -1;
    }

    /** -1null 0D 1W 2A 3S */
    public static byte dwas(){
        float ya = Core.input.axis(Binding.move_y);
        float xa = Core.input.axis(Binding.move_x);
        if(BetaMindy.mui.touchpad != null) BetaMindy.mui.touchpad.showStart();

        if(Math.abs(ya) > 0.2f){
            if(ya > 0) return 1;
            return 3;
        }
        if(Math.abs(xa) > 0.2f){
            if(xa > 0) return 0;
            return 2;
        }
        return -1;
    }

    public static void lockCam(Vec2 pos){
        if(headless) return;
        if(control.input instanceof DesktopInput) ((DesktopInput)control.input).panning = false;
        if(!camLock){
            cameraPos.set(Core.camera.position);
            camLock = true;
        }

        cameraPos.lerp(pos, (Core.settings.getBool("smoothcamera") ? 0.08f : 1f) * Time.delta);
        Core.camera.position.set(cameraPos);
        //Core.camera.update(); no effect
    }

    public static void unlockCam(){
        if(headless) return;
        Core.app.post(() -> {
            //if(control.input instanceof DesktopInput) ((DesktopInput)control.input).panning = false;
            camLock = false;
        });
    }

    public static void cutscene(Vec2 pos){
        if(headless) return;
        if(control.input instanceof DesktopInput) ((DesktopInput)control.input).panning = true;
        if(!Core.scene.hasField()) Core.scene.setKeyboardFocus(scrollLocker);
        if(!camLock){
            cameraPos.set(Core.camera.position);
            camLock = true;
        }
        cameraPos.lerp(pos, (Core.settings.getBool("smoothcamera") ? 0.08f : 1f) * Time.delta);
        Core.camera.position.set(cameraPos);
    }

    public static void cutsceneEnd(){
        if(headless) return;
        if(control.input instanceof DesktopInput) ((DesktopInput)control.input).panning = false;
        if(Core.scene.getKeyboardFocus() != null && Core.scene.getKeyboardFocus().equals(scrollLocker)) Core.scene.setKeyboardFocus(null);
        Core.app.post(() -> {
            camLock = false;
        });
    }

    public static boolean dumpPlayerUnit(UnitPayload u, Player player){
        if(u.unit.type == null) return true;

        if(!Units.canCreate(u.unit.team, u.unit.type)){
            return false;
        }

        //check if unit can be dumped here
        EntityCollisions.SolidPred solid = u.unit.solidity();
        if(solid != null){
            int tx = u.unit.tileX(), ty = u.unit.tileY();
            boolean nearEmpty = !solid.solid(tx, ty);
            for(Point2 p : Geometry.d4){
                nearEmpty |= !solid.solid(tx + p.x, ty + p.y);
            }

            //cannot dump on solid blocks
            if(!nearEmpty) return false;
        }

        //no client dumping
        if(Vars.net.client()) return true;

        //prevents stacking
        u.unit.vel.add(Mathf.range(0.5f), Mathf.range(0.5f));
        u.unit.add();
        Events.fire(new EventType.UnitUnloadEvent(u.unit));
        player.unit(u.unit);
        if(net.active()) Call.unitControl(player, u.unit);
        u.unit.apply(MindyStatusEffects.ouch, 10f);

        return true;
    }

    public static void lightningCircle(float x, float y, float r, int n, Color color){
        if(n < 3) return;
        float start = Mathf.random(360f);
        float end = start + Mathf.range(70f) + 180f;
        Seq<Vec2> lines = new Seq<>();
        float mid = n / 2f - 0.5f;

        lines.add(new Vec2().trns(start, r).add(x, y));
        for(int i = 1; i < n - 1; i++){
            lines.add(new Vec2().trns(((i <= (int)mid) ? start : end) + Mathf.range(30f), r * (Math.abs((float)i - mid) / mid + Mathf.range(0.4f / mid))).add(x, y));
        }
        lines.add(new Vec2().trns(end, r).add(x, y));
        Fx.lightning.at(x, y, start, color, lines);
    }
}
