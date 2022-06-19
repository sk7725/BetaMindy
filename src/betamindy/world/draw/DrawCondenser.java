package betamindy.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawCondenser extends DrawBlock {
    public TextureRegion top, middle;
    public Color flameColor = Pal.turretHeat, midColor = Color.valueOf("f6f5ff"), lightFlameColor = Color.coral;
    public float flameAlphaScl = 7f, flameAlphaMag = 0.8f, centerRadScl = 3f, centerRadMag = 0.2f;

    public float alpha = 0.68f;
    public int particles = 25;
    public float particleLife = 40f, particleRad = 7f, particleLen = 4f;

    @Override
    public void draw(Building build){
        //Draw.rect(build.block.region, build.x, build.y);
        float warmup = build.warmup();
        float si = Mathf.absin(flameAlphaScl, flameAlphaMag) * warmup;

        if(warmup > 0f && midColor.a > 0.001f){
            float a = alpha * warmup;

            float base = (Time.time / particleLife);
            rand.setSeed(build.id);
            for(int i = 0; i < particles; i++){
                float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
                float angle = rand.random(360f);
                float len = particleRad * Interp.pow2Out.apply(fout);
                float roff = rand.random(0.8f, 1.1f);
                Draw.color(Tmp.c1.set(midColor).mul(rand.random(0.8f, 1f)), fin * a);
                Fill.circle(build.x + Angles.trnsx(angle, len), build.y + Angles.trnsy(angle, len), particleLen * roff * fout + 0.01f);
            }
            Draw.color(midColor, lightFlameColor, si);
            Draw.alpha(a);
            for(int i = 0; i < 3; i++){
                float r = rand.random(0.4f, 0.8f) * particleLen * (1f + Mathf.absin(centerRadScl * 1.5f, centerRadMag));
                float len = rand.random(0.25f, 0.45f) * r * (1f + Mathf.absin(centerRadScl, centerRadMag));
                float angle = rand.range(20f) + i * 120f + Time.time;
                Fill.circle(build.x + Angles.trnsx(angle, len), build.y + Angles.trnsy(angle, len), r * warmup + 0.01f);
            }

            Draw.reset();
        }
        Draw.rect(top, build.x, build.y);

        Draw.blend(Blending.additive);
        Draw.color(flameColor, si);
        Draw.rect(middle, build.x, build.y);
        Draw.blend();
        Draw.reset();
    }

    @Override
    public void load(Block block){
        top = Core.atlas.find(block.name + "-top");
        middle = Core.atlas.find(block.name + "-middle");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{block.region, top};
    }
}
