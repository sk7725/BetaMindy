package betamindy.type;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.graphics.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static mindustry.entities.Puddles.maxLiquid;

public class StarryLiquid extends AnimatedLiquid {
    public Color starColor;
    public float starDuration = 60f;

    public StarryLiquid(String name, Color color, Color starColor){
        super(name, color);
        this.starColor = starColor;
    }

    @Override
    public void drawPuddle(Puddle puddle) {
        super.drawPuddle(puddle);
        float amount = puddle.amount, x = puddle.x, y = puddle.y;
        float f = Mathf.clamp(amount / (maxLiquid / 1.5f));
        float z = Draw.z();
        Draw.z(Layer.effect);
        rand.setSeed(puddle.id);

        Draw.color(starColor);
        for(int i=0; i<6; i++){
            float t = (Time.time + rand.random(starDuration));
            boolean sp = Mathf.floor(t / (starDuration / 3f)) % 2 == 1;
            t %= starDuration;
            int ti = Mathf.floor(t / starDuration);
            t /= starDuration;

            Tmp.v1.trns(Mathf.randomSeed(ti * 20L + i * 2L, 360f), Mathf.randomSeed(ti * 20L + i * 2L + 1, f * 9f));
            float vx = x + Tmp.v1.x, vy = y + Tmp.v1.y;

            Drawm.spark(vx, vy, 2f * (1 - t) * f, 0.7f * (1 - t) * f, sp ? 45f : 0f);
        }
        Draw.z(z);
    }
}
