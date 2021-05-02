package betamindy.util.xelo;

import arc.func.Boolf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.pooling.Pools;
import betamindy.world.blocks.distribution.Piston;
import betamindy.world.blocks.distribution.PistonArm;
import betamindy.world.blocks.distribution.SidedSlimeBlock;
import betamindy.world.blocks.distribution.SlimeBlock;
import betamindy.world.blocks.logic.PushReact;
import mindustry.content.Liquids;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.PriorityQueue;

import static arc.math.geom.Geometry.d4;
import static mindustry.Vars.world;

//credit to xelo, modified by me
public class XeloUtil {

    public Point2[][] origins = new Point2[16][4];

    public void init() {
        for(int size = 1; size <= 16; size++) {
            int originX = 0;
            int originY = 0;
            originX += size / 2;
            originY += size / 2;
            originY -= (size - 1);
            for(int side = 0; side < 4; side++) {
                int ogx = originX;
                int ogy = originY;
                if(side != 0 && size > 1) {
                    for(int i = 1; i <= side; i++) {
                        ogx += d4(i).x * (size - 1);
                        ogy += d4(i).y * (size - 1);
                    }
                }
                origins[size - 1][side] = new Point2(ogx, ogy);
            }
        }
    }

    /**
     * returns whether a building is allowed to be pushed.
     */
    public boolean pushable(Building build) {
        return !(
                build.dead ||
                        (build.block instanceof CoreBlock) ||
                        (build.block instanceof PistonArm) ||
                        ((build.block instanceof Piston) && ((Piston.PistonBuild) build).extended)
        );
    }

    /**
     * returns whether a building is allowed to be sticked.
     */
    public boolean stickable(Building build) {
        return pushable(build);
    }

    /**
     * returns whether a block is allowed to be on this tile, disregarding existing pushable buildings and team circles
     */
    public boolean tileAvailableTo(@Nullable Tile tile, Block block) {
        if(tile == null) {
            return false;
        }
        if(tile.build != null) {
            return pushable(tile.build);
        }
        return !(
                tile.solid() ||
                        !tile.floor().placeableOn ||
                        (block.requiresWater && tile.floor().liquidDrop != Liquids.water) ||
                        (tile.floor().isDeep() && !block.floating && !block.requiresWater && !block.placeableLiquid)
        );
    }

    /**
     * returns whether a tile can be pushed in this direction, disregarding buildings.
     */
    public boolean canPush(Building build, int direction) {
        if(!pushable(build)) {
            return false;
        }

        Point2 tangent = d4(direction + 1);
        Point2 dir = d4(direction);
        Point2 origin = origins[build.block.size - 1][direction];

        for(int i = 0; i < build.block.size; i++) { // iterate over forward edge.
            Tile tile = build.tile.nearby(origin.x + tangent.x * i + dir.x, origin.y + tangent.y * i + dir.y);
            if(!tileAvailableTo(tile, build.block)) {
                return false;
            }
        }
        Tile next = build.tile.nearby(dir.x, dir.y);
        return next != null && build.block.canPlaceOn(next, build.team);
    }

    /**
     * pushes units in front of a a single building.
     *
     * @param build     the building to be pushed.
     * @param direction number from 0-4 same direction as the block rotation to push the building in.
     */
    public void pushUnits(Building build, int direction) {
        Point2 dir = d4(direction);
        float ox = dir.x * (build.block.size * 4f + 4f);
        float oy = dir.y * (build.block.size * 4f + 4f);

        //grounded units don't experience impulses anyway
        //boolean bouncy = ((build.block instanceof SlimeBlock) && (!build.block.rotate || build.rotation == direction));
        if(direction % 2 == 0) {
            //tall rectangle
            float dr = dir.x;
            Units.nearby(build.x + ox - 4f, build.y + oy - build.block.size * 4f, 8f, build.block.size * 8f, u -> {
                if(!u.isFlying() && u.x >= build.x + ox - 4f && u.x <= build.x + ox + 4f) {
                    u.move(build.x + dr * (3.8f + build.block.size * 4f + 8f) - u.x, 0f);
                    //if(bouncy) u.impulse(dr * 40f, 0f);
                }
            });
        }else {
            //wide rectangle
            float dr = dir.y;
            Units.nearby(build.x + ox - build.block.size * 4f, build.y + oy - 4f, build.block.size * 8f, 8f, u -> {
                if(!u.isFlying() && u.y >= build.y + oy - 4f && u.y <= build.y + oy + 4f) {
                    u.move(0f, build.y + dr * (3.8f + build.block.size * 4f + 8f) - u.y);
                    //if(bouncy) u.impulse(0f, dr * 40f);
                }
            });
        }
    }

    /**
     * Pushes a single building. if obstructed does not push multiple tiles
     * Used as a subroutine for the function that actually does push all obstructed tiles.
     * <p>
     * Algorithm:
     * <pre>
     * {@code
     * scan forward tiles for blockage
     * return false if a block exists in forward tiles or tile isn't allowed forward space
     * remove building
     * update building
     * }
     * </pre>
     *
     * @param build     the building to be pushed. DO NOT CALL FROM WITHIN THE BUILDING.
     * @param direction number from 0-4 same direction as the block rotation to push the building in.
     * @return if the building has been moved
     */
    public boolean pushSingle(Building build, int direction) {
        //don't move the core. >:(  BAD BAD BAD BAD
        if(build.block instanceof CoreBlock) {
            return false;
        }

        int bx = build.tile.x;
        int by = build.tile.y;

        //Player control = (build instanceof ControlBlock) ? ((ControlBlock) build).unit().getPlayer() : null;
        build.tile.remove();
        /*
        //scan forward tiles for blockage
        if(!Build.validPlace(build.block, build.team, bx+d4(direction).x, by+d4(direction).y, build.rotation, false)){
            Vars.world.tile(bx,by).setBlock(build.block, build.team, build.rotation, () -> build);
            return false;
        }*/

        //move units
        pushUnits(build, direction);

        Point2 dir = d4(direction);

        world.tile(bx + dir.x, by + dir.y).setBlock(build.block, build.team, build.rotation, () -> build);

        if(build instanceof PushReact) {
            ((PushReact) build).pushed(direction);
        }
        return true;
    }

    //projection of the block's leading edge along a direction.
    private int project(Building build, int direction) {
        Point2 dir = d4(direction);
        return (origins[build.block.size - 1][direction].x + build.tile.x) * dir.x + (origins[build.block.size - 1][direction].y + build.tile.y) * dir.y;
    }

    /**
     * Gets all buildings connected to each other in the push direction sorted.
     * If group cannot be pushed because its too large or an unpushable block exists it returns null.
     *
     * @param root        the building to be scanned from
     * @param direction   number from 0-4 same direction as the block rotation to push the building in.
     * @param max         max number of blocks to scan
     * @param pushFilter  Boolf consumer as a custom selection criteria for pushing.
     * @param stickFilter Boolf consumer as a custom selection criteria for sticking.
     */
    @Nullable
    public Seq<Building> getAllContacted(Building root, int direction, int max, Boolf<Building> pushFilter,
                                  Boolf<Building> stickFilter) {
        PriorityQueue<Building> queue = new PriorityQueue<>(10, (a, b) -> {
            //require ordering to be projection of the block's leading edge along push direction.
            return Math.round(project(a, direction) - project(b, direction));
        });
        Seq<Building> contacts = new Seq<>();

        queue.add(root);
        boolean dirty = false;

        Point2 tangent = d4(direction + 1);
        Point2 dir = d4(direction);

        while(!queue.isEmpty() && contacts.size <= max) {
            Building next = queue.poll();
            contacts.add(next);
            Point2 origin = origins[next.block.size - 1][direction];
            if(next.block instanceof SlimeBlock) {
                // iterate over forward edge.
                for(int i = 0; i < next.block.size; i++) {
                    Tile t = next.tile.nearby(origin.x + tangent.x * i + dir.x, origin.y + tangent.y * i + dir.y);
                    if(t == null) return null;
                    Building b = t.build;
                    if(b == null || queue.contains(b) || contacts.contains(b)) {
                        continue;
                    }
                    if(!pushable(b) || !pushFilter.get(b)) {
                        return null; // if a single block cannot be pushed then the entire group cannot be pushed from the root.
                    }
                    queue.add(b);
                }
                if(next.block instanceof SidedSlimeBlock) {
                    // if sided, do the sticky on one side
                    int td = next.rotation;
                    if(td != direction) {
                        tangent = d4((td + 1) % 4);
                        origin = origins[next.block.size - 1][td];
                        for(int i = 0; i < next.block.size; i++) {
                            Tile t = next.tile.nearby(origin.x + tangent.x * i + d4(td).x, origin.y + tangent.y * i + d4(td).y);
                            if(t == null) continue;
                            Building b = t.build;
                            if(b == null || queue.contains(b) || contacts.contains(b)) {
                                continue;
                            }
                            if(!stickable(b) || !stickFilter.get(b) || !pushFilter.get(b)) {
                                continue; // ignore blocks that refuse to stick.
                            }
                            // contacts is not ordered; sort at the end
                            if(td == (direction + 2) % 4) dirty = true;
                            queue.add(b);
                        }
                    }
                }else {
                    // if sticky, iterate over all 4 edges, but for 3 sides, ignore non-sticky blocks
                    for(int k = 0; k < 3; k++) {
                        // iterate over a side edge
                        int td = (direction + k + 1) % 4;
                        tangent = d4((td + 1) % 4);
                        origin = origins[next.block.size - 1][td];
                        for(int i = 0; i < next.block.size; i++) {
                            Tile t = next.tile.nearby(origin.x + tangent.x * i + d4(td).x, origin.y + tangent.y * i + d4(td).y);
                            if(t == null) continue;
                            Building b = t.build;
                            if(b == null || queue.contains(b) || contacts.contains(b)) {
                                continue;
                            }
                            if(!stickable(b) || !stickFilter.get(b) || !pushFilter.get(b)) {
                                continue; // ignore blocks that refuse to stick.
                            }
                            // contacts is not ordered; sort at the end
                            if(k == 1) dirty = true;
                            queue.add(b);

                        }
                    }
                }
            }else {
                // iterate over forward edge.
                for(int i = 0; i < next.block.size; i++) {
                    Tile t = next.tile.nearby(origin.x + tangent.x * i + dir.x, origin.y + tangent.y * i + dir.y);
                    if(t == null) return null;
                    Building b = t.build;
                    if(b == null || queue.contains(b) || contacts.contains(b)) {
                        continue;
                    }
                    if(!pushable(b) || !pushFilter.get(b)) {
                        return null; // if a single block cannot be pushed then the entire group cannot be pushed from the root.
                    }
                    queue.add(b);
                }
            }
        }
        if(contacts.size <= max) {
            if(dirty) {
                contacts.sort((a, b) -> Math.round(project(a, direction) - project(b, direction)));
            }
            return contacts;
        }else {
            return null;
        }
    }

    /**
     * Pushes a single building and pushes all blocks behind the pushed block, unlike the previous.
     */
    public boolean pushBlock(Building build, int direction, int maxBlocks, Boolf<Building> pushFilter, Boolf<Building> stickFilter) {
        Seq<Building> pushing = getAllContacted(build, direction, maxBlocks, pushFilter, stickFilter);
        if(pushing == null) {
            return false;
        }
        //scan in reverse
        for(int i = pushing.size - 1; i >= 0; i--) {
            if(!canPush(pushing.get(i), direction)) {
                return false;
            }
        }

        Point2 dir = d4(direction);

        Seq<Movable> movables = new Seq<>();

        for(int i = pushing.size - 1; i >= 0; i--) {
            Building building = pushing.get(i);

            Movable movable = Movables.get(building);

            if(movable != null) {
                movables.add(movable);

                movable.set(building, dir);
            }

            pushSingle(building, direction);

            if(movable != null) {
                movable.pushed();
            }
        }

        for(Movable movable : movables) {
            movable.config();
        }
        Pools.freeAll(movables, false);

        return true;
    }

}
