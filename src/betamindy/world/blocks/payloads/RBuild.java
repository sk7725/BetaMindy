package betamindy.world.blocks.payloads;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.world.blocks.distribution.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import static mindustry.Vars.*;

/**
 * Stores a BuildPayload with its relative coordinates to the origin block.
 */
public class RBuild {
    public BuildPayload pay; //TODO: use Building instead and refactor this shit
    public byte x, y; //should not go beyond a byte, unless tampered with via other mods
    public static final int[][] evenOffsets = {{-1, -1}, {0, -1}, {0, 0}, {-1, 0}};
    private static final Queue<Building> queue = new Queue<Building>();
    private static final Seq<Building> contacts = new Seq<Building>();

    public RBuild(BuildPayload bp, byte x, byte y) {
        pay = bp;
        this.x = x;
        this.y = y;
    }

    public static RBuild read(Reads read, byte revision) {
        return new RBuild(BetaMindy.mobileUtil.readPayload(read), read.b(), read.b());
    }

    public static void write(RBuild rp, Writes write) {
        BetaMindy.mobileUtil.writePayload(rp.pay, write);
        write.b(rp.x);
        write.b(rp.y);
    }

    public static boolean validPlace(Block type, int x, int y) {
        return validPlace(type, x, y, true);
    }

    /**
     * checks minimal place requirements, just like pistons
     */
    public static boolean validPlace(Block type, int x, int y, boolean checkUnits) {
        if (type == null) return false;

        if (checkUnits && (type.solid || type.solidifes) && Units.anyEntities(x * tilesize + type.offset - type.size * tilesize / 2f, y * tilesize + type.offset - type.size * tilesize / 2f, type.size * tilesize, type.size * tilesize)) {
            return false;
        }

        Tile tile = world.tile(x, y);

        if (tile == null) return false;

        int offsetx = -(type.size - 1) / 2;
        int offsety = -(type.size - 1) / 2;

        for (int dx = 0; dx < type.size; dx++) {
            for (int dy = 0; dy < type.size; dy++) {
                int wx = dx + offsetx + tile.x, wy = dy + offsety + tile.y;

                Tile check = world.tile(wx, wy);

                if (
                        check == null ||
                                check.solid() ||
                                !check.floor().placeableOn ||
                                (type.requiresWater && check.floor().liquidDrop != Liquids.water) ||
                                (check.floor().isDeep() && !type.floating && !type.requiresWater && !type.placeableLiquid)
                ) return false;
            }
        }

        return true;
    }

    /**
     * Places down all builds. Does not care if some die off, as it is the process of natural selection.
     *
     * @param builds a Seq of RBuilds you want to place.
     * @param root   the tile where (x, y) = (0, 0). The Seq should not include the root.
     * @param dir    rotation difference between the pickup and place.
     */
    public static void placeAll(Seq<RBuild> builds, Tile root, int dir) {
        if (root == null) return;
        builds.forEach(b -> b.placeSingle(root, dir));
    }

    /**
     * Draws all builds.
     *
     * @param builds      a Seq of RBuilds you want to draw.
     * @param ox          x of the center of spin, the root.
     * @param oy          y of the center of spin.
     * @param rotation    the angle of the blocks, absolute.
     * @param rawRotation the degrees that the blocks have turned, relative.
     */
    public static void drawAll(Seq<RBuild> builds, float ox, float oy, float rotation, float rawRotation) {
        Tmp.v2.trns(rotation, tilesize);
        builds.forEach(b -> b.draw(ox + Tmp.v2.x, oy + Tmp.v2.y, rotation, rawRotation));
    }

    public static boolean pickup(Seq<RBuild> builds, Building root, int dir, int max, Boolf<Building> bool) {
        builds.clear();
        queue.clear();
        contacts.clear();
        queue.add(root);
        do {
            Building next = queue.removeFirst();
            if (next.block.rotate) {
                //sided slime
                Tile t = next.tile.nearby(next.rotation);
                if (t == null) continue;
                Building b = t.build;
                if (b == null || b == root || !bool.get(b) || contacts.contains(b)) {
                    continue;
                }
                contacts.add(b);
                if (b.block instanceof SlimeBlock) queue.add(b);
            } else {
                //normal slime
                for (int i = 0; i < 4; i++) {
                    Tile t = next.tile.nearby(i);
                    if (t == null) continue;
                    Building b = t.build;
                    if (b == null || b == root || !bool.get(b) || contacts.contains(b)) {
                        continue;
                    }
                    contacts.add(b);
                    if (b.block instanceof SlimeBlock) queue.add(b);
                }
            }
        } while (contacts.size < max && !queue.isEmpty());

        if (contacts.size >= max) return false; //root is not counted, so size must be smaller than max!
        contacts.each(b -> {
            int ox = b.tile.x - root.tile.x;
            int oy = b.tile.y - root.tile.y;
            if (b.block.size % 2 == 0) {
                ox -= evenOffsets[dir][0];
                oy -= evenOffsets[dir][1];
            }
            b.tile.remove();
            builds.add(new RBuild(new BuildPayload(b), (byte) ox, (byte) oy));
        });
        return true;
    }

    public @Nullable
    Tile getTile(Tile root, int dir) {
        if (root == null) return null;
        Tile t = root.nearby(Geometry.d4x(dir) * x - Geometry.d4y(dir) * y, Geometry.d4x(dir) * y + Geometry.d4y(dir) * x); //don't ask
        if (t == null) return null;
        if (pay.block().size % 2 == 0) {
            return t.nearby(evenOffsets[dir][0], evenOffsets[dir][1]);
        }
        return t;
    }

    public void placeSingle(Tile root, int dir) {
        Tile t = getTile(root, dir);
        if (t == null) return;
        if (!validPlace(pay.block(), t.x, t.y)) kill(t.worldx(), t.worldy());
        else pay.place(t, pay.build.rotation + dir);
    }

    public void draw(float ox, float oy, float rotation, float rawRotation) {
        Tmp.v1.set(x * tilesize, y * tilesize).rotate(rotation);
        Draw.z(Layer.blockOver + 0.09f);
        float offset = pay.block().offset;
        ox += offset;
        oy += offset;
        Drawf.shadow(ox + Tmp.v1.x, oy + Tmp.v1.y, tilesize * pay.block().size * 2f);
        Draw.z(Layer.blockOver + 0.1f);
        Draw.rect(pay.icon(Cicon.full), ox + Tmp.v1.x, oy + Tmp.v1.y, (pay.block().rotate ? pay.build.rotation : 0) * 90f + rawRotation);
    }

    public void kill(float kx, float ky) {
        if (pay.block().size % 2 == 0) {
            kx += tilesize / 2f;
            ky += tilesize / 2f;
        }
        Fx.dynamicExplosion.at(kx, ky, pay.block().size / 1.3f); //boom!
    }
}