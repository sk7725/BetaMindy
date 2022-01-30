package betamindy.world.blocks.defense.turrets;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.storage.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;

import static mindustry.Vars.*;

public class HijackTurret extends Turret {
    public float powerUse = 1f;

    public int linkRange = 6;
    public int maxLinks = 3;
    public boolean ignoreSize = false;

    public float shotsMultiplier = 0.33f;
    public float reloadMultiplier = 1.5f;
    public float hijackReload = 0.65f; //removed every tick for hijacked turrets
    public float chargeShootLength = -1;
    public float chargeXRand = -1;

    public Effect hijackEffect = MindyFx.sparkleCode;
    private final Seq<Building> tmpe = new Seq<>();

    public HijackTurret(String name){
        super(name);
        configurable = true;
        hasPower = true;

        config(Integer.class, (HijackTurretBuild entity, Integer value) -> {
            Building other = world.build(value);
            boolean contains = entity.links.contains(value);
            //if(entity.occupied == null) entity.occupied = new boolean[maxLinks];

            if(contains){
                //unlink
                int i = entity.links.indexOf(value);
                entity.links.removeIndex(i);
                entity.occupied.removeIndex(i);
            }else if(linkValid(entity, other) && entity.links.size < maxLinks){
                if(!entity.links.contains(other.pos())){
                    entity.links.add(other.pos());
                    entity.occupied.add(false);
                }
            }
            else{
                return;
            }
            entity.sanitize();
        });
        configClear((HijackTurretBuild entity) -> {
            entity.links.clear();
            entity.occupied.clear();
        });
    }

    @Override
    public void init(){
        consumes.powerDynamic(HijackTurretBuild::powerUsage);
        super.init();
        if(chargeShootLength < 0) chargeShootLength = size * tilesize * 0.7f;
        if(chargeXRand < 0) chargeXRand = chargeShootLength * 0.8f;
    }

    public boolean linkValid(Building tile, Building link){
        if(tile == link || link == null || tile.team != link.team || link.dead) return false;

        return (tile.block.size == link.block.size || (tile.block.size < link.block.size && ignoreSize)) && Math.max(Math.abs(tile.tileX() - link.tileX()), Math.abs(tile.tileY() - link.tileY())) <= linkRange && link instanceof TurretBuild && !(link.block instanceof HijackTurret) && !(link.block instanceof PayloadTurret) && !(link.block instanceof LaserTurret);
    }

    public class HijackTurretBuild extends TurretBuild {
        public IntSeq links = new IntSeq();
        public BoolSeq occupied = new BoolSeq();
        //public boolean[] occupied; //if the current turret is charging, this is true

        private int current = 0;

        @Override
        public void updateTile(){
            unit.ammo(power.status * unit.type().ammoCapacity);

            super.updateTile();
        }

        @Override
        public boolean hasAmmo(){
            return links.size > 0;
        }

        @Override
        public BulletType peekAmmo(){
            return Bullets.standardCopper; //nothing, actually
        }

        @Override
        protected void updateShooting(){
            reload += delta() * baseReloadSpeed();

            if(reload >= reloadTime && !charging){
                sanitize();
                tryShoot(current);
                current++;

                reload %= reloadTime;
            }

            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(b instanceof TurretBuild tb && !b.dead){
                    //b.control(LAccess.enabled, 0,0,0,0);
                    tb.reload = Math.min(0f, tb.reload - tb.delta() * tb.efficiency() * hijackReload);
                }
            }
        }

        public void tryShoot(int i){
            if(links.size <= 0) return;
            //if(occupied == null) occupied = new boolean[maxLinks];
            i %= links.size;

            Building b = world.build(links.get(i));
            if(b instanceof TurretBuild tb && tb.isValid() && !tb.dead){
                if(tb.hasAmmo() && !occupied.get(i)){
                    //try shooting
                    shoot(tb.peekAmmo(), tb, i);
                }
            }
            else{
                occupied.set(i, false);
            }
        }

        public float powerUsage(){
            if(isActive() && links.size > 0){
                Building b = world.build(links.get(current % links.size));
                if(b != null && b.block instanceof PowerTurret p) return powerUse + p.powerUse;
            }
            return powerUse;
        }

        public float realReload(float r){
            return r / (efficiency() * reloadMultiplier);
        }

        public void shoot(BulletType type, TurretBuild build, int n){
            Turret b = (Turret) build.block;
            //float xoff = (n - (links.size-1) / 2f) * ((tilesize * size) / (float)links.size);
            //when charging is enabled, use the charge shoot pattern
            if(b.chargeTime > 0){
                float xoff = Mathf.range(chargeXRand);
                float rotation = this.rotation;
                build.useAmmo();

                tr.trns(rotation, shootLength, xoff);
                b.chargeBeginEffect.at(x + tr.x, y + tr.y, rotation);
                b.chargeSound.at(x + tr.x, y + tr.y, 1);

                for(int i = 0; i < b.chargeEffects; i++){
                    Time.run(Mathf.random(b.chargeMaxDelay), () -> {
                        if(dead) return;
                        tr.trns(rotation, shootLength, xoff);
                        b.chargeEffect.at(x + tr.x, y + tr.y, rotation);
                    });
                }

                //charging = true;
                occupied.set(n, true);
                float rl = Math.max(b.chargeTime, realReload(b.reloadTime));

                Time.run(b.chargeTime, () -> {
                    if(dead) return;
                    tr.trns(rotation, shootLength, xoff);
                    //recoil = recoilAmount;
                    build.heat = 1f;
                    bullet(type, rotation + Mathf.range(inaccuracy + type.inaccuracy));
                    effects(type, build, b);
                });
                Time.run(rl, () -> {
                    occupied.set(n, false);
                });
            }else{
                //otherwise, use the normal shot pattern(s)
                occupied.set(n, true);

                if(b.alternate){
                    float i = (shotCounter % b.shots) - (b.shots-1)/2f;

                    tr.trns(rotation - 90, b.spread * i + Mathf.range(b.xRand), shootLength);
                    bullet(type, rotation + Mathf.range(inaccuracy + type.inaccuracy));
                }else{
                    tr.trns(rotation, shootLength, Mathf.range(b.xRand));
                    int sh = Mathf.ceilPositive(b.shots * shotsMultiplier);

                    for(int i = 0; i < sh; i++){
                        bullet(type, rotation + Mathf.range(inaccuracy + type.inaccuracy) + (i - (int)(sh / 2f)) * b.spread);
                    }
                }

                float rl = realReload(b.reloadTime);
                if(b.burstSpacing > 0.0001f) rl /= b.shots;
                Time.run(rl, () -> {
                    occupied.set(n, false);
                });

                shotCounter++;

                recoil = recoilAmount;
                build.heat = 1f;
                effects(type, build, b);
                build.useAmmo();
            }
        }

        public void effects(BulletType type, TurretBuild build, Turret b){
            Effect fshootEffect = b.shootEffect == Fx.none ? type.shootEffect : b.shootEffect;
            Effect fsmokeEffect = b.smokeEffect == Fx.none ? type.smokeEffect : b.smokeEffect;

            fshootEffect.at(x + tr.x, y + tr.y, rotation);
            fsmokeEffect.at(x + tr.x, y + tr.y, rotation);
            b.shootSound.at(x + tr.x, y + tr.y, Mathf.random(0.9f, 1.1f));

            hijackEffect.at(build);
            build.reload = 0f;
        }

        public void sanitize(){
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(!linkValid(this, b) || links.get(i) != b.pos()){
                    links.removeIndex(i);
                    occupied.removeIndex(i);
                    i--;
                }
            }
        }

        void squares(Building b, Color color){
            float radius = b.block.size * tilesize / 2f;
            Lines.stroke(3f, Pal.gray);
            Lines.square(b.x, b.y, radius + 1f);
            Lines.stroke(1f, color);
            Lines.square(b.x, b.y, radius);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();

            if(links.size <= 0) return;
            Draw.color(Pal2.source);
            Draw.alpha(Mathf.absin(Time.globalTime, 8f, 1f));
            Building b = world.build(links.get(current % links.size));
            if(b != null) Fill.square(b.x, b.y, b.block.size * tilesize / 2f);
            Draw.color();
        }

        @Override
        public void drawConfigure(){
            float radius = (linkRange + 0.5f) * tilesize;
            Lines.stroke(3f, Pal.gray);
            Lines.square(x, y, radius + 1f);
            Lines.stroke(1f, Pal2.source);
            Lines.square(x, y,  radius);
            squares(this, Pal2.source);

            for(int x = tile.x - linkRange; x <= tile.x + linkRange; x++){
                for(int y = tile.y - linkRange; y <= tile.y + linkRange; y++){
                    Building link = world.build(x, y);

                    if(link != this && linkValid(this, link)){
                        boolean linked = links.indexOf(link.pos()) >= 0;

                        if(linked){
                            squares(link, Color.scarlet);
                        }
                    }
                }
            }

            Draw.reset();
        }

        @Override
        public boolean onConfigureTileTapped(Building other){
            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }

            if(this == other){
                if(links.size == 0){
                    tmpe.clear();
                    for(int x = tile.x - linkRange; x <= tile.x + linkRange; x++){
                        for(int y = tile.y - linkRange; y <= tile.y + linkRange; y++){
                            Building link = world.build(x, y);

                            if(link != this && !tmpe.contains(link) && linkValid(this, link)){
                                tmpe.add(link);
                                configure(link.pos());
                            }
                        }
                    }
                }else{
                    configure(null);
                }
                deselect();
                return false;
            }

            return true;
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case ammo -> power.status;
                case ammoCapacity -> 1;
                default -> super.sense(sensor);
            };
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.s(links.size);
            for(int i = 0; i < links.size; i++){
                write.i(links.get(i));
            }
            write.s(current);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            links.clear();
            short amount = read.s();
            for(int i = 0; i < amount; i++){
                links.add(read.i());
                occupied.add(false);
            }
            current = read.s();
        }
    }
}
