package betamindy.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.payloads.*;

public class HomingPayloadBullet extends PayloadBullet{
    public HomingPayloadBullet(float speed){
        super(speed);
        homingDelay = 20f;
    }
    public HomingPayloadBullet(){
        this(1f);
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(homingPower > 0.0001f && b.time >= homingDelay){
            Teamc target = Units.closestTarget(b.team, b.x, b.y, homingRange, Flyingc::isGrounded, t -> true);
            if(target != null){
                b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(target), homingPower));
            }
        }
    }

    public void drawEngines(Bullet b){
        float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
        float sizeScl = offset * 0.02f + 1f;
        Draw.color();
        Draw.z(Layer.flyingUnit - 1f);
        float size = ((Payload)b.data).size();
        Tmp.v1.trns(b.rotation() + 180f, size * 0.6f);
        size = Mathf.absin(2f, size / 3f) + size;
        Draw.color(Pal.engine);
        Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, sizeScl * size / 4f);
        Draw.color();
        Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, sizeScl * size / 5.7f);

        Draw.reset();
    }

    @Override
    public void drawNew(Bullet b, TextureRegion icon, float rotation){
        drawEngines(b);
        super.drawNew(b, icon, rotation);
    }

    @Override
    public void drawBuild(Bullet b, BuildPayload bp){
        drawEngines(b);
        super.drawBuild(b, bp);
    }
}
