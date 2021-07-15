package betamindy.world.blocks.defense;

import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.*;

import static mindustry.Vars.tilesize;

public class StatusWall extends Wall {
    public StatusEffect status = MindyStatusEffects.icy;
    public float statusDuration = 600f;

    public Effect shotEffect = MindyFx.spike;
    public Effect destroyEffect = Fx.none;
    public BulletType puddle = MindyBullets.icyZone;

    public StatusWall(String name){ super(name); }

    public class StatusWallBuild extends WallBuild {
        public void reactTo(Unit unit){
            unit.apply(status, statusDuration);
            float angle = angleTo(unit);
            Tmp.v1.trns(angle, size * tilesize / 2f).add(this);
            shotEffect.at(Tmp.v1.x, Tmp.v1.y, angle, status.color);
        }

        @Override
        public void onDestroyed(){
            if(destroyEffect != null) destroyEffect.at(this);
            puddle.create(this, x, y, 0f);
            super.onDestroyed();
        }

        @Override
        public boolean collision(Bullet bullet){
            if(bullet.team != team && (bullet.owner instanceof Unit)) reactTo((Unit)bullet.owner);
            return super.collision(bullet);
        }
    }
}
