package betamindy.world.blocks.payloads;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.world.blocks.distribution.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.*;

/** Stores a BuildPayload with its relative coordinates to the origin block. */
public class RBuild {
    public Building build;
    public byte x, y; //should not go beyond a byte, unless tampered with via other mods
    public static final int[][] evenOffsets = {{-1, -1}, {0, -1}, {0, 0}, {-1, 0}};
    private static final Queue<Building> queue = new Queue<Building>();
    private static final Seq<Building> contacts = new Seq<Building>();
    private static final Vec2 v1 = new Vec2();
    private static final Vec2 v2 = new Vec2();
    private static final Vec2 v3 = new Vec2();

    public RBuild(Building bp, byte x, byte y){
        build = bp;
        this.x = x;
        this.y = y;
    }

    public static RBuild read(Reads read, byte revision){
        Block block = Vars.content.block(read.s());
        Building b = block.newBuilding().create(block, Team.derelict);
        byte version = read.b();
        b.readAll(read, version);
        return new RBuild(b, read.b(), read.b());
    }

    public static void write(RBuild rp, Writes write){
        write.s(rp.build.block.id);
        write.b(rp.build.version());
        rp.build.writeAll(write);
        write.b(rp.x);
        write.b(rp.y);
    }

    public static boolean validPlace(Block type, int x, int y){
        return validPlace(type, x, y, true);
    }

    /** checks minimal place requirements, just like pistons */
    public static boolean validPlace(Block type, int x, int y, boolean checkUnits){
        if(type == null) return false;

        if(checkUnits && (type.solid || type.solidifes) && Units.anyEntities(x * tilesize + type.offset - type.size*tilesize/2f, y * tilesize + type.offset - type.size*tilesize/2f, type.size * tilesize, type.size*tilesize)){
            return false;
        }

        Tile tile = world.tile(x, y);

        if(tile == null) return false;

        int offsetx = -(type.size - 1) / 2;
        int offsety = -(type.size - 1) / 2;

        for(int dx = 0; dx < type.size; dx++){
            for(int dy = 0; dy < type.size; dy++){
                int wx = dx + offsetx + tile.x, wy = dy + offsety + tile.y;

                Tile check = world.tile(wx, wy);

                if(
                        check == null ||
                        check.solid() ||
                                !check.floor().placeableOn ||
                                (type.requiresWater && check.floor().liquidDrop != Liquids.water)||
                                (check.floor().isDeep() && !type.floating && !type.requiresWater && !type.placeableLiquid)
                ) return false;
            }
        }

        return true;
    }

    /**
     * Places down all builds. Does not care if some die off, as it is the process of natural selection.
     * @param builds a Seq of RBuilds you want to place.
     * @param root the tile where (x, y) = (0, 0). The Seq should not include the root.
     * @param dir rotation difference between the pickup and place.
     */
    public static void placeAll(Seq<RBuild> builds, Tile root, int dir, int absDir){
        if(root == null) return;
        builds.each(b -> b.placeSingle(root, dir, absDir));
    }

    /**
     * Draws all builds.
     * @param builds a Seq of RBuilds you want to draw.
     * @param ox x of the center of spin, the root.
     * @param oy y of the center of spin.
     * @param rotation the angle of the blocks, absolute.
     * @param rawRotation the degrees that the blocks have turned, relative.
     */
    public static void drawAll(Seq<RBuild> builds, float ox, float oy, float rotation, float rawRotation){
        v2.trns(rotation, tilesize);
        builds.each(b -> b.draw(ox + v2.x, oy + v2.y, rawRotation, rotation));
    }

    /**
     * Picks up builds connected via sticky blocks. Returns the weight of selected builds. Returns -1 if it fails.
     * @param builds where the result should be written.
     * @param root the (0,0) of the Rbuilds, not included in the result.
     * @param dir the angle of the spinner, absolute.
     */
    public static int pickup(Seq<RBuild> builds, Building root, int dir, int max, Boolf<Building> bool){
        builds.clear();
        queue.clear();
        contacts.clear();
        queue.add(root);
        int extra = 0;
        do{
            Building next = queue.removeFirst();
            if(next.block.rotate){
                //sided slime
                Tile t = next.tile.nearby(next.rotation);
                if(t == null) continue;
                Building b = t.build;
                if(b == null || b == root || !bool.get(b) || contacts.contains(b)){continue;}
                contacts.add(b);
                if(b instanceof HeavyBuild) extra += ((HeavyBuild) b).weight() - 1;
                if(b.block instanceof SlimeBlock) queue.add(b);
            }
            else{
                //normal slime
                for(int i = 0; i < 4; i++){
                    Tile t = next.tile.nearby(i);
                    if(t == null) continue;
                    Building b = t.build;
                    if(b == null || b == root || !bool.get(b) || contacts.contains(b)){continue;}
                    contacts.add(b);
                    if(b instanceof HeavyBuild) extra += ((HeavyBuild) b).weight() - 1;
                    if(b.block instanceof SlimeBlock) queue.add(b);
                }
            }
        }while (contacts.size + extra < max && !queue.isEmpty());

        if(contacts.size + extra >= max) return -1; //root is not counted, so size must be smaller than max!
        contacts.each(b -> {
            int ox = b.tile.x - root.tile.x;
            int oy = b.tile.y - root.tile.y;
            if(b.block.size % 2 == 0){
                ox -= evenOffsets[dir][0];
                oy -= evenOffsets[dir][1];
            }
            b.tile.remove();
            builds.add(new RBuild(b, (byte) ox, (byte) oy));
        });
        return Math.max(0, contacts.size + extra);
    }

    public @Nullable Tile getTile(Tile root, int dir, int absDir){
        if(root == null) return null;
        absDir = Mathf.mod(absDir, 4);
        Tile t = root.nearby(Geometry.d4x(dir) * x - Geometry.d4y(dir) * y, Geometry.d4x(dir) * y + Geometry.d4y(dir) * x); //don't ask
        if(t == null) return null;
        if(build.block().size % 2 == 0){
            return t.nearby(evenOffsets[absDir][0], evenOffsets[absDir][1]);
        }
        return t;
    }

    public void placeSingle(Tile root, int dir, int absDir){
        Tile t = getTile(root, dir, absDir);
        if(t == null) return;
        if(!validPlace(build.block, t.x, t.y)) kill(t.worldx(), t.worldy());
        else t.setBlock(build.block, build.team, build.rotation + dir, () -> build);
    }

    public void draw(float ox, float oy, float rawRotation, float absRotation){
        v1.set(x * tilesize, y * tilesize).rotate(rawRotation);
        Draw.z(Layer.blockOver + 0.11f);
        //Lines.stroke(1, Color.red);
        //Lines.line(ox, oy, ox + v1.x, oy + v1.y);
        if(build.block.size % 2 == 0){
            v1.add(v3.set(-4f, -4f).rotate(absRotation));

            //Lines.square(ox + v1.x, oy + v1.y, 4f, rawRotation);
            //v3.set(-4f, -4f).rotate(absRotation);
            //Lines.stroke(1, Color.cyan);
            //Lines.line(ox + v1.x, oy + v1.y, ox + v1.x + v3.x, oy + v1.y + v3.y);

            //v1.add(v3);
        }
        //Draw.color();
        Draw.z(Layer.blockOver + 0.09f);
        //float offset = build.block.offset;
        //ox += offset; oy += offset;
        Drawf.shadow(ox + v1.x, oy + v1.y, tilesize * build.block.size * 2f);
        Draw.z(Layer.blockOver + 0.1f);
        if(build instanceof SpinDraw) ((SpinDraw) build).drawSpinning(ox + v1.x, oy + v1.y, rawRotation);
        else Draw.rect(build.block.fullIcon, ox + v1.x, oy + v1.y, (build.block.rotate ? build.rotation : 0) * 90f + rawRotation);
    }

    public void kill(float kx, float ky){
        if(build.block.size % 2 == 0){
            kx += tilesize / 2f; ky += tilesize / 2f;
        }
        Fx.dynamicExplosion.at(kx, ky, build.block.size / 1.3f); //boom!
        Sounds.explosion.at(kx, ky);
    }
}