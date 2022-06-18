package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.graphics.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class HijackTurret extends Turret {
    public float powerUse = 1f;

    public int linkRange = 6;
    public int maxLinks = 3;
    //public boolean ignoreSize = false;
    public int minSize = -1, maxSize = -1;
    public boolean ignoreDrawSize = false;

    public float shotsMultiplier = 0.33f;
    public float reloadMultiplier = 1.5f;
    public float powerMultiplier = 1f;
    public float hijackReload = 0.1f; //removed every tick for hijacked turrets
    public float chargeShootLength = -1;
    public float chargeXRand = -1;

    public Effect hijackEffect = MindyFx.sparkleCode;
    public TextureRegion rawRegion;
    private final Seq<Building> tmpe = new Seq<>();

    public HijackTurret(String name){
        super(name);
        configurable = true;
        hasPower = true;
        lightColor = Pal2.source;
        lightRadius = 30f;
        liquidCapacity = 30f * size;

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
        consumePowerDynamic(HijackTurretBuild::powerUsage);
        super.init();
        if(chargeShootLength < 0) chargeShootLength = size * tilesize * 0.7f;
        if(chargeXRand < 0) chargeXRand = chargeShootLength * 0.8f;
        if(minSize < 0) minSize = Math.max(1, size - 1);
        if(maxSize < 0) maxSize = size;
        clipSize = Math.max(clipSize, (linkRange + 9) * tilesize * 2f);
    }

    @Override
    public void load(){
        super.load();
        rawRegion = atlas.find(name + "-raw", name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        float radius = (linkRange + 0.5f) * tilesize;
        Lines.stroke(3f, Pal.gray);
        Lines.square(x * tilesize + offset, y * tilesize + offset, radius + 1f);
        Lines.stroke(1f, Pal2.source);
        Lines.square(x * tilesize + offset, y * tilesize + offset,  radius);
        Draw.color();
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.reload, "[green]" + Iconc.turret + Mathf.round(reloadMultiplier * 100) + "%[]");
        stats.add(Stat.shots, "[coral]" + Iconc.turret + Mathf.round(shotsMultiplier * 100) + "%[]");
        if(shoot.shots > 1){
            stats.add(Stat.shots, "[green]x" + shoot.shots + "[]");
        }
        stats.add(Stat.linkRange, linkRange, StatUnit.blocks);
        stats.add(Stat.linkRange, "@ "+Iconc.turret + " ([green]@x@[] ~ [green]@x@[])", maxLinks, minSize, minSize, maxSize, maxSize);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("links", (HijackTurretBuild entity) -> new Bar(() -> Core.bundle.format("bar.iconlinks", entity.links.size, maxLinks, Iconc.turret), () -> Pal2.source, () -> entity.links.size / (float)maxLinks));
    }

    public boolean linkValid(Building tile, Building link){
        if(tile == link || link == null || tile.team != link.team || link.dead) return false;

        return (minSize <= link.block.size && link.block.size <= maxSize) && Math.max(Math.abs(tile.tileX() - link.tileX()), Math.abs(tile.tileY() - link.tileY())) <= linkRange && link instanceof TurretBuild && !(link.block instanceof HijackTurret) && !(link.block instanceof PayloadTurret) && !(link.block instanceof LaserTurret);
    }

    public class HijackTurretBuild extends TurretBuild {
        public IntSeq links = new IntSeq();
        public BoolSeq occupied = new BoolSeq();
        //public boolean[] occupied; //if the current turret is charging, this is true

        public int current = 0;
        public float progress = 0f, warmup = 0f, heatup = 0f;
        private TextureRegion prevTurret = rawRegion, curTurret = rawRegion;

        @Override
        public void updateTile(){
            unit.ammo(power.status * unit.type().ammoCapacity);
            warmup = Mathf.lerpDelta(warmup, power.status, 0.08f);
            heatup = Mathf.lerpDelta(heatup, wasShooting ? power.status : 0f, 0.03f);

            super.updateTile();

            if(!headless && !wasShooting && curTurret != rawRegion && heatup < 0.2f){
                prevTurret = curTurret;
                curTurret = rawRegion;
                heat = 1f;
                progress = 0f;
            }
        }

        @Override
        public boolean hasAmmo(){
            return links.size > 0;
        }

        @Override
        public BulletType peekAmmo(){
            return Bullets.placeholder; //nothing, actually
        }

        /*TODO: re-implement this -Anuke

        @Override
        protected void updateShooting(){
            reloadCounter += delta() * baseReloadSpeed();

            if(reloadCounter >= reload && !charging){
                sanitize();

                if(tryShoot(current)){
                    reloadCounter %= reload;
                    if(!headless){
                        Block b = links.size == 0 ? block : world.tile(links.get(current % links.size)).block();
                        prevTurret = curTurret;
                        curTurret = b.region.found() ? b.region : region;
                        heat = 1f;
                        progress = 0f;
                    }
                }
                current++;
            }

            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(b instanceof TurretBuild tb && !b.dead){
                    //b.control(LAccess.enabled, 0,0,0,0);
                    tb.reloadCounter = Math.max(0f, tb.reloadCounter - tb.delta() * tb.efficiency() * hijackReload);
                }
            }
        }

        //return true to use up reload
        public boolean tryShoot(int i){
            if(links.size <= 0) return true;
            //if(occupied == null) occupied = new boolean[maxLinks];
            i %= links.size;

            Building b = world.build(links.get(i));
            if(b instanceof TurretBuild tb && tb.isValid() && !tb.dead){
                if(tb.hasAmmo() && !occupied.get(i)){
                    //try shooting
                    shoot(tb.peekAmmo(), tb, i);
                    return true;
                }
            }
            else{
                occupied.set(i, false);
            }
            return false;
        }*/



        public float powerUsage(){
            if(isActive() && links.size > 0){
                Building b = world.build(links.get(current % links.size));
                if(b != null && b.block.consumesPower) return powerUse + b.block.consPower.usage;
            }
            return powerUse;
        }

        public float realReload(float r){
            return r / (efficiency() * reloadMultiplier);
        }
        /*

        public void shoot(BulletType type, TurretBuild build, int n){
            shoot(type, build, n, true, shots == 1 ? 0f : (-(int) (shots / 2f)) * spread);

            if(burstSpacing > 0.0001f){
                for(int i = 1; i < shots; i++){
                    final int ii = i;
                    Time.run(burstSpacing * i, () -> {
                        if(dead || !build.hasAmmo()) return;
                        shoot(type, build, n, false, (ii - (int)(shots / 2f)) * spread);
                    });
                }
            }
            else if(shots > 1){
                for(int i = 1; i < shots; i++){
                    if(dead || !build.hasAmmo()) return;
                    shoot(type, build, n, false, (i - (int)(shots / 2f)) * spread);
                }
            }
        }

        public void shoot(BulletType type, TurretBuild build, int n, boolean setOccupied, float rotOffset){
            Turret b = (Turret) build.block;
            //float xoff = (n - (links.size-1) / 2f) * ((tilesize * size) / (float)links.size);
            //consume coolant
            float coolant = 1f;
            if(setOccupied){
                float maxUsed = consumes.<ConsumeLiquidBase>get(ConsumeType.liquid).amount * realReload(b.reload);
                Liquid liquid = liquids.current();

                float used = Math.min(liquids.get(liquid), maxUsed) * baseReloadSpeed();
                coolant += (used/maxUsed) * liquid.heatCapacity * coolantMultiplier;
                liquids.remove(liquid, used);

                if(Mathf.chance(0.06 * used)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }
            }

            //when charging is enabled and is visible, use the charge shoot pattern
            if(b.chargeTime > 0 && ((b.chargeEffects > 0 && b.chargeEffect != Fx.none) || b.chargeBeginEffect != Fx.none)){
                float xoff = Mathf.range(chargeXRand);
                float rotation = this.rotation + rotOffset;
                build.useAmmo();

                tr.trns(rotation, b.shootLength, xoff);
                b.chargeBeginEffect.at(x + tr.x, y + tr.y, rotation);
                b.chargeSound.at(x + tr.x, y + tr.y, 1);

                for(int i = 0; i < b.chargeEffects; i++){
                    Time.run(Mathf.random(b.chargeMaxDelay), () -> {
                        if(dead) return;
                        tr.trns(rotation, b.shootLength, xoff);
                        b.chargeEffect.at(x + tr.x, y + tr.y, rotation);
                    });
                }

                //charging = true;
                if(setOccupied) occupied.set(n, true);
                float rl = Math.max(b.chargeTime, realReload(b.reload) / coolant);

                Time.run(b.chargeTime, () -> {
                    if(dead) return;
                    tr.trns(rotation, b.shootLength, xoff);
                    build.heat = 1f;
                    bullet(type, rotation + Mathf.range(inaccuracy + type.inaccuracy));
                    effects(type, build, b);
                    heat = 1f;
                    recoil = recoilAmount;
                });
                Time.run(rl, () -> {
                    if(setOccupied && occupied.size > n) occupied.set(n, false);
                });
            }else{
                //otherwise, use the normal shot pattern(s)
                if(setOccupied) occupied.set(n, true);

                if(b.alternate && b.burstSpacing <= 0.0001f){
                    float i = (shotCounter % b.shots) - (b.shots-1)/2f;

                    tr.trns(rotation - 90 + rotOffset, b.spread * i + Mathf.range(b.xRand), b.shootLength);
                    bullet(type, rotation + Mathf.range(inaccuracy + type.inaccuracy) + rotOffset);
                }else{
                    tr.trns(rotation + rotOffset, b.shootLength, Mathf.range(b.xRand));
                    int sh = Mathf.ceilPositive(b.shots * shotsMultiplier);
                    //if(b.burstSpacing > 0.0001f) sh = 1;
                    float inac = inaccuracy;
                    if(b.burstSpacing > 0.0001f) inac += b.shots;

                    for(int i = 0; i < sh; i++){
                        bullet(type, rotation + Mathf.range(inac + type.inaccuracy) + (i - (int)(sh / 2f)) * b.spread + rotOffset);
                    }
                }

                float rl = realReload(b.reload + b.chargeTime) / coolant;
                //if(b.burstSpacing > 0.0001f) rl /= b.shots;
                Time.run(rl, () -> {
                    if(setOccupied && occupied.size > n) occupied.set(n, false);
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

            hijackEffect.at(build.x + Mathf.range(b.size * tilesize / 3f), build.y + Mathf.range(b.size * tilesize / 3f));
            //build.reload = 0f;
        }

        @Override
        protected void bullet(BulletType type, float angle){
            float rangem = Math.min((powerMultiplier - 1f) * 0.5f + 1f, 1.5f);
            float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / (type.range() * rangem), minRange / type.range(), range / type.range()) : 1f;

            type.create(this, team, x + tr.x, y + tr.y, angle, powerMultiplier * type.damage, rangem + Mathf.range(velocityInaccuracy), lifeScl, null);
        }*/

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

        @Override
        public void draw(){
            //TODO please use a custom DrawTurret for this -Anuke
            TextureRegion base = ((DrawTurret)drawer).base;

            Draw.rect(base, x, y);
            Draw.color();

            //Draw.z(Layer.effect + 4f);
            recoilOffset.trns(rotation, -recoil);

            progress += Math.max(1f, edelta());
            //float f = Mathf.clamp(progress * (burstSpacing > 0.001f ? shots : 1f) / reload);
            float f = Mathf.clamp(progress / Math.min(reload, 9f));

            /*
            Draw.mixcol(Tmp.c1.set(lightColor).mul(0.2f), 0.9f);
            Draw.alpha(warmup * (Mathf.random(0.5f,0.8f)));
            rectSquished(prevTurret, x + recoilOffset.x, y + recoilOffset.y, rotation - 90f, 1 - f);
            rectSquished(f);
            Draw.blend(Blending.additive);
            Draw.color(lightColor, warmup);
            rectSquished(prevTurret, x + recoilOffset.x, y + recoilOffset.y, rotation - 90f, 1 - f);
            rectSquished(f);*/

            Draw.z(Layer.turret + 1f);
            Draw.mixcol(Tmp.c1.set(lightColor).lerp(Color.white, Mathf.absin(5, 0.5f)), 1);
            float o = (1-f) * 0.7f;
            rectSquished(prevTurret, x + recoilOffset.x + o, y + recoilOffset.y + o, rotation - 90f, 1 - f);
            rectSquished(prevTurret, x + recoilOffset.x - o, y + recoilOffset.y - o, rotation - 90f, 1 - f);
            o = f * 0.7f;
            rectSquished(curTurret, x + recoilOffset.x + o, y + recoilOffset.y + o, rotation - 90f, f);
            rectSquished(curTurret, x + recoilOffset.x - o, y + recoilOffset.y - o, rotation - 90f, f);
            Draw.mixcol();

            Draw.z(Layer.turret + 1.1f);
            Draw.alpha(Mathf.absin(5, 0.2f) + 0.8f);
            rectSquished(prevTurret, x + recoilOffset.x, y + recoilOffset.y, rotation - 90f, 1 - f);
            rectSquished(f);
            Draw.color();

            if(heat > 0.01f){
                Draw.blend(Blending.additive);
                int c = (int)(Mathf.randomSeed(current) * 3f);
                Draw.mixcol(c == 0 ? Color.magenta : c == 1 ? Color.yellow : Color.cyan, 1f);
                Draw.color(Color.white, heat * Mathf.random());
                recoilOffset.trns(rotation, -recoil * 0.5f, heat * 3f * f);
                rectSquished(f);
                Draw.mixcol(c == 0 ? Color.cyan : c == 1 ? Color.magenta : Color.yellow, 1f);
                recoilOffset.trns(rotation, -recoil * 0.5f, -heat * 3f * f);
                rectSquished(f);
                Draw.blend();
            }
            Draw.reset();

            if(warmup > 0.01f){
                Draw.color(team.color, lightColor, Mathf.absin(4f, 1f));
                Draw.alpha(Mathf.absin(Time.time + id * 51f + 1.7f, 13f, 0.4f) + 0.6f);
                Lines.stroke((size / 2f + 1f) * warmup * (0.3f + 0.3f * heatup));
                Drawm.ellipse(x, y, size * tilesize * (0.55f + heatup * 0.1f) * warmup, 1f, Mathf.absin(9.3f, 1f) + 0.001f, (Time.time + 30f) / 2f + id * 17f, Layer.turret + 0.6f, Layer.turret + 1.5f);
            }
            Draw.reset();
            drawLinks();
        }

        public void drawLinks(){
            if(Mathf.zero(Renderer.laserOpacity) || links.size == 0) return;

            Draw.z(Layer.scorch - 1f);
            float f0 = warmup * 0.5f + 0.5f;
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(!linkValid(this, b) || links.get(i) != b.pos()) continue;
                float angle = angleTo(b);

                boolean horizontal = Angles.near(angle, 0, 45) || Angles.near(angle, 180, 45);
                float fromX = x, fromY = y, toX = b.x, toY = b.y;
                float off = horizontal ? toY - fromY : toX - fromX;


                float len = (horizontal ? toX - fromX : toY - fromY) / 2f;
                len -= Math.abs(off) / 3f;

                off = off / (linkRange * 2f) * (size - 0.2f);
                if(horizontal) fromY += off;
                else fromX += off;
                Tmp.v1.set(!horizontal ? fromX : fromX + len, horizontal ? fromY : fromY + len);
                Tmp.v2.set(!horizontal ? toX : fromX + len, horizontal ? toY : fromY + len);

                Draw.z(Layer.scorch - 2f);
                Lines.stroke(3f);
                Draw.color(Pal.gray, Renderer.laserOpacity);
                Lines.line(fromX, fromY, Tmp.v1.x, Tmp.v1.y);
                Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
                Lines.line(Tmp.v2.x, Tmp.v2.y, toX, toY);

                Draw.z(Layer.debris - 1f);
                Lines.stroke(1f);
                Draw.color(Color.acid, Renderer.laserOpacity * (occupied.get(i) ? 0.5f : 1f) * f0);
                Lines.line(fromX, fromY, Tmp.v1.x, Tmp.v1.y);
                Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
                Lines.line(Tmp.v2.x, Tmp.v2.y, toX, toY);
            }

            Draw.reset();
        }

        private void rectSquished(TextureRegion region, float x, float y, float rot, float squish){
            float maxw = size * tilesize * 4.1f;
            if(ignoreDrawSize) maxw = 99 * tilesize;
            Draw.rect(region, x, y, Math.min(region.width, maxw) * Draw.scl * Draw.xscl * squish, Math.min(region.height, maxw) * Draw.scl * Draw.yscl, rot);
        }

        private void rectSquished(float f){
            rectSquished(curTurret, x + recoilOffset.x, y + recoilOffset.y, rotation - 90f, f);
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
            //Draw.color(Pal2.source);
            //Draw.alpha(0.5f);
            Building b = world.build(links.get(current % links.size));
            if(b != null) Drawf.selected(b, Tmp.c3.set(Pal2.source).a(0.5f));
            Draw.color();
        }

        @Override
        public void drawLight(){
            super.drawLight();
            Drawf.light(x, y, lightRadius * warmup, lightColor, 0.5f);
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
        public boolean onConfigureBuildTapped(Building other){
            if(!headless && !mobile && Core.input.keyDown(Binding.control)) return true;

            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }

            if(this == other && (!mobile || unit != null && unit().isPlayer() && unit().getPlayer() == player)){ //mobile uses double tap to control
                if(links.size == 0){
                    tmpe.clear();
                    for(int x = tile.x - linkRange; x <= tile.x + linkRange; x++){
                        for(int y = tile.y - linkRange; y <= tile.y + linkRange; y++){
                            Building link = world.build(x, y);

                            if(link != null && link != this && !tmpe.contains(link) && linkValid(this, link)){
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
            write.s(links.size == 0 ? 0 : current % links.size);
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
