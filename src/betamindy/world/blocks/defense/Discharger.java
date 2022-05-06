package betamindy.world.blocks.defense;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.world.blocks.distribution.*;
import betamindy.world.blocks.logic.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.power.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;

public class Discharger extends Battery {
    /** Lightning damage. */
    public float damage = 15f;
    /** Amount of lightning. */
    public int amount = 2;
    public float reloadTime = 4f;
    public float inaccuracy = 15f;

    /** Charge use per spinning shot. Fixed. */
    public float spinCharge = 20f;
    /** Lightning length while spinning. Scales with speed (angle speed * radius) */
    public int spinLength = 5;
    public float spinScale = 0.18f;

    /** Charge use per push. */
    public float pushCharge = spinCharge * 2f;
    /** Lightning length when pushed. */
    public int pushLength = 10;

    public Color lightningColor = Pal.lancerLaser;
    public TextureRegion shieldRegion;
    public Sound shootSound = Sounds.spark;

    public Effect shootEffect = MindyFx.powerDust;
    public float effectChance = 0.02f;

    public Discharger(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        shieldRegion = atlas.find(name + "-shield");
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.damage, damage * amount);
    }

    @Override
    public void setBars(){
        super.setBars();
        if(hasPower && consPower != null){
            removeBar("power");
            ConsumePower cons = consPower;
            boolean buffered = cons.buffered;
            float capacity = cons.capacity;

            addBar("power", entity -> new Bar(() -> buffered ? Core.bundle.format("bar.poweramount", Float.isNaN(entity.power.status * capacity) ? "<ERROR>" : (int)(entity.power.status * capacity)) :
                    Core.bundle.get("bar.power"), () -> lightningColor, () -> Mathf.zero(cons.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f ? 1f : entity.power.status));
        }
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public class DischargerBuild extends BatteryBuild implements PushReact, SpinUpdate, SpinDraw {
        public float heat = 0f;
        public float reload = 0f;

        public float useCharge(float a){
            float use = Math.min(power.status, a / consPower.capacity);
            power.status -= use;
            return Mathf.clamp(use * consPower.capacity / a);
        }

        public boolean noCharge(){
            return Mathf.equal(power.status, 0f, 0.001f);
        }

        public void updateVars(){
            heat = Mathf.lerpDelta(heat, 0f, 0.05f);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            updateVars();
        }

        @Override
        public void pushed(int dir){
            if(noCharge()) return;
            float f = useCharge(pushCharge);
            heat = 1f;
            shootSound.at(this);

            int rnd = Mathf.random(2);
            for(int i = 0; i < amount; i++) Lightning.create(team, lightningColor, damage, x + Mathf.range(4f), y + Mathf.range(4f), dir * 90f + Mathf.range(inaccuracy), Mathf.round(pushLength * f));
            for(int i = 0 ; i < rnd + amount; i++) shootEffect.at(x + Mathf.range(size / 2f), y + Mathf.range(size / 2f), lightningColor);
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            updateVars();
            reload += delta();
            if(!noCharge() && Mathf.chanceDelta(Math.min(0.3f, effectChance * srad))) shootEffect.at(sx + Mathf.range(size / 2f), sy + Mathf.range(size / 2f), lightningColor);
            if(noCharge() || reload < reloadTime) return;
            float f = useCharge(spinCharge);
            reload = 0f;
            heat = 1f;
            shootSound.at(sx, sy, 1f, 0.1f);

            for(int i = 0; i < amount; i++) Lightning.create(team, lightningColor, damage, sx + Mathf.range(4f), sy + Mathf.range(4f), absRot + Mathf.range(inaccuracy), Mathf.round(spinLength * (1f + srad * spinScale / 8f) * f));
        }

        @Override
        public void draw(){
            drawSpinning(x, y, 0f);
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            Draw.rect(region, x, y, dr);

            Draw.mixcol(Color.white, Mathf.absin(5f, 0.8f));
            Draw.alpha(power.status);
            Draw.rect(topRegion, x, y, dr);
            Draw.mixcol();
            Draw.color();

            if(heat > 0.01f){
                //Draw.z(Layer.blockOver);
                Draw.color(lightningColor, heat);
                Draw.blend(Blending.additive);
                Draw.rect(shieldRegion, x + Mathf.range(heat * 0.8f), y + Mathf.range(heat * 0.8f), dr);
                Draw.color();
                Draw.blend();
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            reload = read.f();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(reload);
        }
    }
}
