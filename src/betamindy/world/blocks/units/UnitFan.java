package betamindy.world.blocks.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.util.*;
import betamindy.world.blocks.logic.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class UnitFan extends LogicSpinBlock {
    public float range = 80f;
    public float strength = 25f;
    public float liquidUse = 0.02f;

    public Effect windHitEffect = MindyFx.windHit;
    public Effect smokeEffect = MindyFx.smallPuff;
    public float smokeChance = 0.3f, smokeX = 14f, smokeY = 6f;
    public int windParticles = 7;
    public float windAlpha = 0.7f, windLineLen = 0.5f;
    public TextureRegion liquidRegion;

    public UnitFan(String name){
        super(name);

        priority = TargetPriority.turret;
        flags = EnumSet.of(BlockFlag.turret);
        ambientSound = Sounds.wind;
        ambientSoundVolume = 0.2f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.add(Stat.speed, strength, StatUnit.none);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("wind", (FanBuild entity) -> new Bar(() -> Core.bundle.get("bar.wind"), () -> Pal.lancerLaser, () -> Mathf.clamp(entity.visualWindStr() / strength)));
    }

    @Override
    public void init(){
        super.init();
        updateClipRadius(range);
    }

    @Override
    public void load(){
        super.load();
        if(hasLiquids) liquidRegion = atlas.find(name + "-liquid");
    }

    public class FanBuild extends LogicSpinBuild {
        public float windLen = 0f; //stores last wind length
        public float heat = 0f;

        @Override
        public void draw(){
            super.draw();
            if(hasLiquids && liquids.currentAmount() > 0.001f){
                Draw.z(Layer.block + 0.01f);
                Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
            }
            if(heat > 0.1f && windLen > 0f) drawWind(realRotation());
        }

        public void pushUnits(float str){
            float rot = realRotation();
            Tmp.v3.trns(rot, str * 2f);
            windLen = Useful.findPathLength(x, y, rot, range, (Building)this);
            Useful.applyLine(u -> {
                if(hasLiquids && liquids.currentAmount() >= liquidUse && u.tileOn() != null){
                    Liquid liquid = liquids.current();
                    if(liquid.effect != null) u.apply(liquid.effect, 300f);
                    Puddles.deposit(u.tileOn(), liquid, 6f);
                    Fx.hitLiquid.at(u.x, u.y, rot, liquid.color);
                }
                u.impulseNet(Tmp.v3);
            }, this, windHitEffect, x, y, rot, windLen, false);
        }

        public void drawWind(float rot){
            Draw.z(Layer.bullet - 1f);
            if(hasLiquids && liquids.currentAmount() > 0.001f) Draw.color(Color.white, liquids.current().color, Mathf.clamp(liquids.currentAmount()) / 3f);
            for(int i = 0; i < (int)(windParticles * (windLen / range)); i++){
                int c = i + (int)(Time.time / (windLen * visualWindStr() / 5f)) + id * windParticles;
                drawWindLine(rot, (Mathf.randomSeed(c, 0f, windLen) + Time.time * visualWindStr() / 5f - windLineLen * visualWindStr()) % windLen, windLen, Mathf.randomSeedRange(c + windParticles, smokeX * 0.5f));
            }
            Draw.reset();
        }

        public void drawWindLine(float rot, float d, float maxd, float off){
            drawWindLine(rot, d, maxd,off, windLineLen * visualWindStr());
        }

        public void drawWindLine(float rot, float d, float maxd, float off, float len){
            if(d + len <= 0f) return;
            float f = 1f - d/maxd;
            Draw.alpha(windAlpha * f);
            Lines.stroke(f * 0.5f + 0.3f);
            Tmp.v1.trns(rot, Math.max(0f, d) + smokeY, off);
            Tmp.v2.trns(rot, d + len + smokeY, off);
            Lines.line(x + Tmp.v1.x, y + Tmp.v1.y, x + Tmp.v2.x, y + Tmp.v2.y);
        }

        /** Accounts for timeScale, but not deltaTime. */
        public float visualWindStr(){
            return efficiency() * timeScale() * strength;
        }

        @Override
        public void updateTile(){
            super.updateTile();

            heat = Mathf.lerpDelta(heat, efficiency(), 0.1f);

            if(efficiency() > 0.1f){
                pushUnits(strength * edelta());
                if(hasLiquids && liquids.currentAmount() >= liquidUse * edelta()) liquids.remove(liquids.current(), liquidUse * edelta());

                if(hasLiquids && Mathf.chance(smokeChance / 2f * edelta() * Mathf.clamp(liquids.currentAmount() / liquidCapacity))){
                    Tmp.v1.trns(realRotation(), smokeY, Mathf.range(0.5f) * smokeX);
                    Fx.hitLiquid.at(x + Tmp.v1.x, y + Tmp.v1.y, realRotation(), liquids.current().color);
                }
                else if(Mathf.chance(smokeChance * edelta())){
                    Tmp.v1.trns(realRotation(), smokeY, Mathf.range(0.5f) * smokeX);
                    smokeEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, realRotation());
                }
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, range, Pal.placing);
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency() > 0.1f;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            if(!hasLiquids) return false;
            return liquids.current() == liquid || liquids.currentAmount() < 0.2f;
        }
    }
}
