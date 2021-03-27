package betamindy.world.blocks.units;

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
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;

public class UnitFan extends LogicSpinBlock {
    public float range = 80f;
    public float strength = 25f;

    public Effect windHitEffect = MindyFx.windHit;
    public Effect smokeEffect = MindyFx.smallPuff;
    public float smokeChance = 0.3f, smokeX = 14f, smokeY = 6f;
    public int windParticles = 7;
    public float windAlpha = 0.7f, windLineLen = 0.5f;

    public UnitFan(String name){
        super(name);

        priority = TargetPriority.turret;
        flags = EnumSet.of(BlockFlag.turret);
        expanded = true;
        //TODO ambientsound
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    public class FanBuild extends LogicSpinBuild {
        public float windLen = 0f; //stores last wind length
        public float heat = 0f;

        public void pushUnits(float str){
            float rot = realRotation();
            Tmp.v3.trns(rot, str * 2f);
            windLen = Useful.findPathLength(x, y, rot, range, (Building)this);
            Useful.applyLine(u -> u.impulseNet(Tmp.v3), this, windHitEffect, x, y, rot, windLen, false);
        }

        public void drawWind(float rot){
            Draw.z(Layer.bullet - 1f);
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
                if(Mathf.chance(smokeChance * edelta())){
                    Tmp.v1.trns(realRotation(), smokeY, Mathf.range(0.5f) * smokeX);
                    smokeEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, realRotation());
                }
            }
        }

        @Override
        public void draw(){
            super.draw();
            if(heat > 0.1f && windLen > 0f) drawWind(realRotation()); //TODO welp, there's no avoiding it. This time for sure, I need to use annos.
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Drawf.dashCircle(x, y, range, Pal.placing);
        }
    }
}
