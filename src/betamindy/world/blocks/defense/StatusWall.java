package betamindy.world.blocks.defense;

import arc.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;

public class StatusWall extends Wall {
    public StatusEffect status = MindyStatusEffects.icy;
    public float statusDuration = 600f;

    public Effect shotEffect = MindyFx.spike;
    public BulletType puddle = MindyBullets.icyZone;

    public StatusWall(String name){ super(name); }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.abilities, table -> {
            table.image(status.uiIcon).size(18f);
            table.add(" [accent]" + status.localizedName + "[] " + (int)(statusDuration / 60) + " " + Core.bundle.get("unit.seconds"));
        });
    }

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

        @Override
        public void drawLight(){
            super.drawLight();
            Drawf.light(x, y, 16f * size, status.color, 0.2f);
        }
    }
}
