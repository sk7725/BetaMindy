package betamindy.type;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static mindustry.Vars.renderer;
import static mindustry.entities.Puddles.maxLiquid;

public class ForceLiquid extends AnimatedLiquid{
    public ForceLiquid(String name, Color color){
        super(name, color);
    }

    @Override
    public void init() {
        super.init();
        boilPoint = 5f;
    }

    @Override
    public void drawPuddle(Puddle puddle) {
        float z = Draw.z();
        float amount = puddle.amount, x = puddle.x, y = puddle.y;
        float f = Mathf.clamp(amount / (maxLiquid / 1.5f));
        float smag = puddle.tile.floor().isLiquid ? 0.8f : 0f, sscl = 25f;

        Draw.z(Layer.shields);
        Draw.color(color, 1f);
        float vx = x + Mathf.sin(Time.time + id * 532, sscl, smag), vy = y + Mathf.sin(Time.time + id * 53, sscl, smag);
        if(renderer.animateShields) {
            Fill.poly(vx, vy, 6, f * 8f);
        }else{
            Lines.stroke(1.5f);
            Draw.alpha(0.09f);
            Fill.poly(vx, vy, 6, f * 8f);
            Draw.alpha(1f);
            Lines.poly(vx, vy, 6, f * 8f);
            Draw.reset();
        }
        Draw.color();
        Draw.z(z);

        if(lightColor.a > 0.001f && f > 0){
            Drawf.light(x, y, 30f * f, lightColor, color.a * f * 0.8f);
        }
    }
}
