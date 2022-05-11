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

import static betamindy.graphics.Drawm.ellipse;
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
    public float beamWidth = 1.2f, ringRadius = 14f, ringWidth = 5f;

    public TextureRegion baseRegion, laser, laserEnd, laserTop, laserTopEnd;

    public Color laserColor = Color.valueOf("98ffa9"), laserTopColor = Color.white.cpy();
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
        consumePowerCond(powerUse, entity -> ((RepairTurretBuild)entity).target != null);
        clipSize = Math.max(clipSize, (phaseRangeBoost + repairRadius + tilesize) * 2);
        super.init();
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base", "block-" + size);
        laser = Core.atlas.find("laser-white");
        laserEnd = Core.atlas.find("laser-white-end");
        laserTop = Core.atlas.find("laser-top");
        laserTopEnd = Core.atlas.find("laser-top-end");
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
        public Healthc target, lastTarget;
        public float strength, rotation = 90;
        public float phaseHeat = 0f;
        public float timeUsed = 0f;
        private final Vec2 lastPos = new Vec2();

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);

            Draw.z(Layer.turret);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);

            if(/*(target != null && Angles.angleDist(angleTo(target), rotation) < 30f) || */lastTarget != null && strength > 0.01f){
                float ang = angleTo(target != null ? target : lastPos);
                float len = 5f;

                Draw.color(laserColor, phaseColor, phaseHeat);
                //cool stuff
                Draw.alpha(0.8f * Math.min(1f, strength * 2f));
                float z = Layer.flyingUnit - 0.1f;
                float s = 8f;
                if(lastTarget instanceof Unit unit){
                    z = unit.isFlying() ? (unit.type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : Layer.groundUnit;
                    s = unit.hitSize() / 2f;
                }
                else if(lastTarget instanceof Building b){
                    z = Layer.block;
                    s = b.block.size * tilesize / 2f;
                }

                float tx = target == null ? lastPos.x : target.x();
                float ty = target == null ? lastPos.y : target.y();

                Lines.stroke(ringWidth * strength);
                ellipse(tx, ty, ringRadius * strength + s, 1f, Mathf.absin(9f, 1f) + 0.001f, Time.time / 12f, z - 1f, Layer.flyingUnit + 1.2f);
                ellipse(tx, ty, ringRadius * strength + s, 1f, Mathf.absin(Time.time * -1f, 7f, 1f) + 0.001f, -Time.time / 11f, z - 1f, Layer.flyingUnit + 1.2f);

                Draw.z(Layer.flyingUnit + 1);
                Draw.alpha(1f);
                Drawf.laser(laser, laserEnd, x + Angles.trnsx(ang, len), y + Angles.trnsy(ang, len), tx, ty, strength * beamWidth);
                Draw.z(Layer.flyingUnit + 1.1f);
                Draw.color(laserTopColor);
                Drawf.laser(laserTop, laserTopEnd, x + Angles.trnsx(ang, len), y + Angles.trnsy(ang, len), tx, ty, strength * beamWidth);
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

            if(optionalEfficiency > 0){
                timeUsed += edelta() * optionalEfficiency;
                if(timeUsed >= useTime){
                    consume();
                    timeUsed = 0f;
                }
            }

            if(target != null && (target.dead() || target.dst(tile) - getSize(target)/2f > r || target.health() >= target.maxHealth())){
                target = null;
            }else if(target != null && canConsume()){
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

            if(target != null){
                lastPos.set(target);
                lastTarget = target;
            }
            else if(strength <= 0.01f) lastTarget = null;
        }

        @Override
        public boolean shouldConsume(){
            return target != null && enabled;
        }

        @Override
        public BlockStatus status(){
            return Mathf.equal(efficiency(), 0f, 0.01f) ? BlockStatus.noInput : super.status();
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