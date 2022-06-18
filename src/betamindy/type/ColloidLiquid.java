package betamindy.type;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static mindustry.entities.Puddles.*;

public class ColloidLiquid extends AnimatedLiquid {
    public float spotDuration = 90f;
    public Color inColor;
    public ColloidLiquid(String name, Color color, Color inColor){
        super(name, color);
        this.inColor = inColor;
    }

    @Override
    public void drawPuddle(Puddle puddle) {
        float z = Draw.z();
        float amount = puddle.amount, x = puddle.x, y = puddle.y;
        float f = Mathf.clamp(amount / (maxLiquid / 1.5f));
        float smag = puddle.tile.floor().isLiquid ? 0.8f : 0f, sscl = 25f;
        rand.setSeed(puddle.id);

        Draw.color(Tmp.c1.set(color).shiftValue(-0.05f));
        Fill.circle(x + Mathf.sin(Time.time + id * 532, sscl, smag), y + Mathf.sin(Time.time + id * 53, sscl, smag), f * 8f);

        float length = f * 6f;

        Draw.z(z + 0.1f);
        Draw.color(inColor);
        for(int i = 0; i < 6; i++){
            Tmp.v1.trns(rand.random(360f) + Time.time * 0.2f * rand.random(3f, 7f) * (rand.nextBoolean() ? 1 : -1), rand.random(length));
            float vx = x + Tmp.v1.x, vy = y + Tmp.v1.y;
            float t = (Time.time + rand.random(spotDuration)) % spotDuration;
            t = Math.abs((t * 2f) - spotDuration) / spotDuration;

            Fill.circle(
                    vx + Mathf.sin(Time.time + i * 532, sscl, smag),
                    vy + Mathf.sin(Time.time + i * 53, sscl, smag),
                    f * rand.random(1f, 3.5f) * t);

        }

        Draw.z(z + 0.15f);
        Draw.color(inColor, color, 0.2f);
        Draw.alpha(0.5f);
        for(int i = 0; i < 4; i++){
            Tmp.v1.trns(rand.random(360f) + Time.time * 0.2f * rand.random(1.5f, 5f) * (rand.nextBoolean() ? 1 : -1), rand.random(length));
            float vx = x + Tmp.v1.x, vy = y + Tmp.v1.y;
            float sd = spotDuration * 1.7f;
            float t = (Time.time + rand.random(sd * 1.7f)) % sd;
            t = Math.abs((t * 2f) - sd) / sd;

            Fill.circle(
                    vx + Mathf.sin(Time.time + i * 532, sscl, smag),
                    vy + Mathf.sin(Time.time + i * 53, sscl, smag),
                    f * rand.random(2f, 4.5f) * t);

        }

        Draw.color();
        Draw.z(z);

        if(lightColor.a > 0.001f && f > 0){
            Drawf.light(x, y, 30f * f, lightColor, color.a * f * 0.8f);
        }
    }
}
