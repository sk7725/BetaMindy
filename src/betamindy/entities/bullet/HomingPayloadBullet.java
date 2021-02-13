package betamindy.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Cicon;
import mindustry.world.blocks.payloads.*;

public class HomingPayloadBullet extends PayloadBullet {
    public HomingPayloadBullet(float speed) {
        super(speed);
        homingDelay = 20f;
    }

    public HomingPayloadBullet() {
        this(1f);
    }

    @Override
    public void update(Bullet b) {
        if (b.timer(0, (3 + b.fslope() * 2f) * trailMult)) {
            if (!correctView) {
                float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
                trailEffect.at(b.x + offset, b.y + offset, b.fslope() * trailSize, b.team.color);
            } else trailEffect.at(b.x, b.y, b.fslope() * trailSize, b.team.color);
        }

        if (homingPower > 0.0001f && b.time >= homingDelay) {
            Teamc target = Units.closestTarget(b.team, b.x, b.y, homingRange, e -> e.isGrounded(), t -> true);
            if (target != null) {
                b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(target), homingPower));
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        Draw.z(Layer.darkness);

        TextureRegion icon;
        float rotation = b.rotation();

        if (b.data instanceof BuildPayload) {
            BuildPayload bp = (BuildPayload) b.data;
            icon = bp.build.block.icon(Cicon.full);
        } else if (b.data instanceof UnitPayload) {
            UnitPayload up = (UnitPayload) b.data;
            icon = up.unit.type().icon(Cicon.full);
            rotation -= 90f;
        } else return;

        if (correctView) drawNew(b, icon, rotation);
        else drawOriginal(b, icon, rotation);
    }

    @Override
    public void drawOriginal(Bullet b, TextureRegion icon, float rotation) {
        Draw.color(Pal.shadow);
        Draw.rect(icon, b.x, b.y, rotation);

        float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
        Draw.color();
        Draw.z(Layer.flyingUnit);
        float size = ((Payload) b.data).size();
        Tmp.v1.trns(b.rotation() + 180f, size * 0.6f);
        size = Mathf.absin(2f, size / 3f) + size;
        Draw.color(Pal.engine);
        Fill.circle(b.x + Tmp.v1.x + offset, b.y + Tmp.v1.y + offset, size / 4f);
        Draw.color();
        Fill.circle(b.x + Tmp.v1.x + offset, b.y + Tmp.v1.y + offset, size / 5.7f);
        Draw.rect(icon, b.x + offset, b.y + offset, rotation);

        Draw.reset();
    }

    @Override
    public void drawNew(Bullet b, TextureRegion icon, float rotation) {
        Draw.color(Pal.shadow);
        float offset = b.fin() * (1 - b.fin()) * altitude * b.lifetime / lifetime;
        Draw.rect(icon, b.x - offset, b.y - offset, rotation);

        float sizeScl = offset * 0.02f + 1f;
        Draw.color();
        Draw.z(Layer.flyingUnit);
        float size = ((Payload) b.data).size();
        Tmp.v1.trns(b.rotation() + 180f, size * 0.6f);
        size = Mathf.absin(2f, size / 3f) + size;
        Draw.color(Pal.engine);
        Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, sizeScl * size / 4f);
        Draw.color();
        Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, sizeScl * size / 5.7f);
        Draw.rect(icon, b.x, b.y, icon.width * Draw.scl * Draw.xscl * sizeScl, icon.height * Draw.scl * Draw.yscl * sizeScl, rotation);

        Draw.reset();
    }
}
