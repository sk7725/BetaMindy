package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.graphics.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class RepairTurret extends Block{
    static final Rect rect = new Rect();

    public final int timerTarget = timers++;

    public float repairRadius = 100f;
    public float repairSpeed = 10.5f;
    public float powerUse;

    public float phaseRangeBoost = 75f;
    public float phaseBoost = 5.5f;
    public float useTime = 240f;

    public TextureRegion baseRegion, laser, laserEnd;

    public Color laserColor = Color.valueOf("e8ffd7");
    public Color phaseColor = Pal2.scalar;

    public RepairTurret(String name){
        super(name);
        update = true;
        solid = true;
        flags = EnumSet.of(BlockFlag.repair);
        hasPower = true;
        outlineIcon = true;
        group = BlockGroup.projectors;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, repairRadius / tilesize, StatUnit.blocks);
        stats.add(Stat.boostEffect, phaseRangeBoost / tilesize, StatUnit.blocks);
        stats.add(Stat.boostEffect, (1f + phaseBoost / repairSpeed) * 100f, StatUnit.percent);
    }

    @Override
    public void init(){
        consumes.powerCond(powerUse, entity -> ((RepairTurretBuild)entity).target != null);
        super.init();
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base", "block-" + size);
        laser = Core.atlas.find("laser");
        laserEnd = Core.atlas.find("laser-end");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, repairRadius, Pal.accent);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class RepairTurretBuild extends Building{
        public Healthc target;
        public float strength, rotation = 90;
        public float phaseHeat = 0f;
        public float timeUsed = 0f;

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);

            Draw.z(Layer.turret);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);

            if(target != null && Angles.angleDist(angleTo(target), rotation) < 30f){
                Draw.z(Layer.flyingUnit + 1); //above all units
                float ang = angleTo(target);
                float len = 5f;

                Draw.color(laserColor, phaseColor, phaseHeat);
                Drawf.laser(team, laser, laserEnd,
                        x + Angles.trnsx(ang, len), y + Angles.trnsy(ang, len),
                        target.x(), target.y(), strength * 1.1f);
                Draw.color();
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, repairRadius + phaseRangeBoost * phaseHeat, Tmp.c1.set(Pal.heal).lerp(phaseColor, phaseHeat));
        }

        public float getSize(Healthc unit){
            if(unit instanceof Unit) return ((Unit)unit).hitSize;
            return tilesize;
        }

        @Override
        public void updateTile(){
            boolean targetIsBeingRepaired = false;
            phaseHeat = Mathf.lerpDelta(phaseHeat, Mathf.num(hasItems && !items.empty()), 0.1f);
            float r = repairRadius + phaseRangeBoost * phaseHeat;

            if(cons.optionalValid() && efficiency() > 0){
                timeUsed += edelta();
                if(timeUsed >= useTime){
                    consume();
                    timeUsed = 0f;
                }
            }

            if(target != null && (target.dead() || target.dst(tile) - getSize(target)/2f > r || target.health() >= target.maxHealth())){
                target = null;
            }else if(target != null && consValid()){
                target.heal((repairSpeed + phaseBoost * phaseHeat) * Time.delta * strength * efficiency());
                rotation = Mathf.slerpDelta(rotation, angleTo(target), 0.5f);
                targetIsBeingRepaired = true;
            }

            if(target != null && targetIsBeingRepaired){
                strength = Mathf.lerpDelta(strength, 1f, 0.08f * Time.delta);
            }else{
                strength = Mathf.lerpDelta(strength, 0f, 0.07f * Time.delta);
            }

            if(timer(timerTarget, 20)){
                rect.setSize(r * 2).setCenter(x, y);
                target = Units.closest(team, x, y, r, Unit::damaged);
                if(target == null) target = Units.findAllyTile(team, x, y, r, Building::damaged);
            }
        }

        @Override
        public boolean shouldConsume(){
            return target != null && enabled;
        }

        @Override
        public BlockStatus status(){
            return Mathf.equal(efficiency(), 0f, 0.01f) ? BlockStatus.noInput : cons.status();
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(rotation);
            write.f(timeUsed);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            rotation = read.f();
            timeUsed = read.f();
        }
    }
}