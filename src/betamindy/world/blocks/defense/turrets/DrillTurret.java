package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class DrillTurret extends BaseTurret {
    public final int timerTarget = timers++;
    public float retargetTime = 60 * 6f;

    public TextureRegion baseRegion, laser, laserEnd;
    public float laserWidth = 0.75f;
    public Sound shootSound = Sounds.minebeam;
    public float shootSoundVolume = 0.9f;

    /** Drill tiers, inclusive */
    public int minDrillTier = 0, maxDrillTier = 3;
    public float mineSpeed = 0.75f;
    public float laserOffset = 4f, shootCone = 6f;

    public @Nullable ConsumeLiquidBase consumeCoolant;

    public DrillTurret(String name){
        super(name);

        sync = true;
        hasItems = true;
        outlineIcon = true;
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base", "block-" + size);
        laser = Core.atlas.find(name + "-laser", "minelaser");
        laserEnd = Core.atlas.find(name + "-laser-end", "minelaser-end");
    }

    public boolean canDrill(Floor f){
        return f.itemDrop != null && f.itemDrop.hardness >= minDrillTier && f.itemDrop.hardness <= maxDrillTier;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.addPercent(Stat.mineSpeed, mineSpeed);
        stats.add(Stat.drillTier, table -> {
            table.left();
            Seq<Block> list = Vars.content.blocks().select(b -> (b instanceof Floor) && canDrill((Floor)b));

            table.table(l -> {
                l.left();

                for(int i = 0; i < list.size; i++){
                    Block item = list.get(i);

                    l.image(item.uiIcon).size(8 * 3).padRight(2).padLeft(2).padTop(3).padBottom(3);
                    l.add(item.localizedName).left().padLeft(1).padRight(4);
                    if(i % 5 == 4){
                        l.row();
                    }
                }
            });
        });
    }

    public class DrillTurretBuild extends BaseTurretBuild {
        public @Nullable Tile mineTile, ore;
        public @Nullable Item targetItem;
        public float mineTimer = 0f, coolant = 1f;
        protected Seq<Tile> proxOres;
        protected Seq<Item> proxItems;
        protected int targetID = -1;

        @Override
        public void created(){
            super.created();
            reMap();
        }

        public void reMap(){
            proxOres = new Seq<>();
            proxItems = new Seq<>();
            ObjectSet<Item> tempItems = new ObjectSet<>();

            Geometry.circle(tile.x, tile.y, (int)(range / tilesize + 0.5f), (x, y) -> {
                Tile other = world.tile(x, y);
                if(other != null && other.drop() != null){
                    Item drop = other.drop();
                    if(!tempItems.contains(drop)){
                        tempItems.add(drop);
                        proxItems.add(drop);
                        proxOres.add(other);
                    }
                }
            });
        }

        public void reFind(int i){
            Item item = proxItems.get(i);

            Geometry.circle(tile.x, tile.y, (int)(range / tilesize + 0.5f), (x, y) -> {
                Tile other = world.tile(x, y);
                if(other != null && other.drop() != null && other.drop() == item && other.block() == Blocks.air){
                    proxOres.set(i, other);
                }
            });
        }

        public boolean canMine(Item item){
            return item.hardness >= minDrillTier && item.hardness <= maxDrillTier && items.get(item) < itemCapacity;
        }

        @Override
        public float efficiency(){
            return super.efficiency() * coolant;
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);

            drawMine();
        }

        @Override
        public void updateTile(){
            Building core = state.teams.closestCore(x, y, team);

            //target ore
            targetMine(core);
            if(core == null || mineTile == null || !canConsume() || !Angles.within(rotation, angleTo(mineTile), shootCone) || items.get(mineTile.drop()) >= itemCapacity){
                mineTile = null;
                mineTimer = 0f;
            }

            if(mineTile != null){
                //consume coolant
                if(consumeCoolant != null){
                    float maxUsed = consumeCoolant.amount;

                    Liquid liquid = liquids.current();

                    float used = Math.min(Math.min(liquids.get(liquid), maxUsed * Time.delta), Math.max(0, (1f / coolantMultiplier) / liquid.heatCapacity));

                    liquids.remove(liquid, used);

                    if(Mathf.chance(0.06 * used)){
                        coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                    }

                    coolant = 1f + (used * liquid.heatCapacity * coolantMultiplier);
                }

                //mine tile
                Item item = mineTile.drop();
                mineTimer += Time.delta * mineSpeed * efficiency();

                if(Mathf.chance(0.06 * Time.delta)){
                    Fx.pulverizeSmall.at(mineTile.worldx() + Mathf.range(tilesize / 2f), mineTile.worldy() + Mathf.range(tilesize / 2f), 0f, item.color);
                }

                if(mineTimer >= 50f + item.hardness * 15f){
                    mineTimer = 0;

                    if(state.rules.sector != null && team() == state.rules.defaultTeam) state.rules.sector.info.handleProduction(item, 1);

                    //items are synced anyways
                    InputHandler.transferItemTo(null, item, 1,
                            mineTile.worldx() + Mathf.range(tilesize / 2f),
                            mineTile.worldy() + Mathf.range(tilesize / 2f),
                            this);
                }

                if(!headless){
                    control.sound.loop(shootSound, this, shootSoundVolume);
                }
            }

            if(timer.get(timerDump, dumpTime)) dump();
        }

        public @Nullable Item iterateMap(Building core){
            if(proxOres == null || !proxOres.any()) return null;
            Item last = null;
            targetID = -1;
            for(int i = 0; i < proxOres.size; i++){
                if(canMine(proxItems.get(i)) && (last == null || last.lowPriority || core.items.get(last) > core.items.get(proxItems.get(i)))){
                    if(proxOres.get(i).block() != Blocks.air){
                        //try to relocate its ore
                        reFind(i);
                        //if it fails, ignore the ore
                        if(proxOres.get(i).block() != Blocks.air) continue;
                    }
                    last = proxItems.get(i);
                    targetID = i;
                }
            }
            return last;
        }

        @Override
        public void removeFromProximity(){
            //reset when pushed
            targetItem = null;
            targetID = -1;
            mineTile = null;
            super.removeFromProximity();
        }

        public void targetMine(Building core){
            if(core == null) return;

            if(timer.get(timerTarget, retargetTime) || targetItem == null){
                targetItem = iterateMap(core);
            }

            //if inventory is full, do not mine.
            if(targetItem == null || items.get(targetItem) >= itemCapacity){
                mineTile = null;
            }
            else{
                if(canConsume() && timer.get(timerTarget, 60) && targetItem != null && targetID > -1){
                    ore = proxOres.get(targetID);
                }

                if(ore != null && canConsume()){
                    float dest = angleTo(ore);
                    rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
                    if(Angles.within(rotation, dest, shootCone)){
                        mineTile = ore;
                    }
                    if(ore.block() != Blocks.air){
                        if(targetID > -1) reFind(targetID);
                        targetItem = null;
                        targetID = -1;
                        mineTile = null;
                    }
                }
            }
        }

        public void drawMine(){
            if(mineTile == null) return;
            float focusLen = laserOffset / 2f + Mathf.absin(Time.time, 1.1f, 0.5f);
            float swingScl = 12f, swingMag = tilesize / 8f;
            float flashScl = 0.3f;

            float px = x + Angles.trnsx(rotation, focusLen);
            float py = y + Angles.trnsy(rotation, focusLen);

            float ex = mineTile.worldx() + Mathf.sin(Time.time + 48, swingScl, swingMag);
            float ey = mineTile.worldy() + Mathf.sin(Time.time + 48, swingScl + 2f, swingMag);

            Draw.z(Layer.flyingUnit + 0.1f);

            Draw.color(Color.lightGray, Color.white, 1f - flashScl + Mathf.absin(Time.time, 0.5f, flashScl));

            Drawf.laser(laser, laserEnd, px, py, ex, ey, laserWidth);

            Draw.color();
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(rotation);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            rotation = read.f();
        }

        @Override
        public void drawSelect(){
            if(mineTile != null){
                Lines.stroke(1f, Pal.accent);
                Lines.poly(mineTile.worldx(), mineTile.worldy(), 4, tilesize / 2f * Mathf.sqrt2, Time.time);
                Draw.color();
            }

            super.drawSelect();
        }
    }
}
