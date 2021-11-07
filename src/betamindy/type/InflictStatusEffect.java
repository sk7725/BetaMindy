package betamindy.type;

import arc.*;
import arc.math.*;
import arc.scene.style.*;
import arc.util.*;
import betamindy.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.ui;

public class InflictStatusEffect extends StatusEffect {
    public float range = 80f;
    public StatusEffect inflicts;
    public float inflictDuration = 600f;
    /** Whether the allies or the enemies are inflicted. */
    public boolean ally = true;

    public Effect effect2 = Fx.none;
    public Effect onInflict = Fx.none;
    public Effect onInterval = Fx.none;
    public float effect2Chance = 0.15f;
    public float effectInterval = 60f;

    public InflictStatusEffect(String name, StatusEffect inflicts){
        super(name);
        this.inflicts = inflicts;
    }

    @Override
    public void update(Unit unit, float time){
        super.update(unit, time);
        if(ally){
            Units.nearby(unit.team, unit.x, unit.y, range, u -> {
                if(u != unit && !u.hasEffect(this)){
                    if(onInflict != Fx.none && !u.hasEffect(inflicts)) onInflict.at(u.x, u.y, inflicts.color);
                    u.apply(inflicts, inflictDuration);
                }
            });
        }
        else{
            Units.nearbyEnemies(unit.team, unit.x - range, unit.y - range, range * 2f, range * 2f, u -> {
                if(u.within(unit, range)){
                    if(onInflict != Fx.none && !u.hasEffect(inflicts)) onInflict.at(u.x, u.y, inflicts.color);
                    u.apply(inflicts, inflictDuration);
                }
            });
        }


        if(onInterval != Fx.none && Useful.interval(effectInterval, unit.id % effectInterval)){
            onInterval.at(unit.x, unit.y, range, inflicts.color);
        }
        if(effect2 != Fx.none && Mathf.chanceDelta(effect2Chance)){
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f + 4f));
            effect2.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, color);
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        //stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.add(ally || damage != 0 ? Stat.affinities : Stat.damage, table -> {
            table.left();
            table.row();
            //table.image(inflicts.icon(Cicon.medium)).size(18f);
            table.button("[accent]" + inflicts.localizedName + "[]", new TextureRegionDrawable(inflicts.uiIcon), Styles.cleart, 40f, () -> {
                ui.content.show(inflicts);
            }).left().size(180f, 46f);
            table.image().size(4f, 46f).color(ally ? Pal.accent : Pal.remove).padRight(9f).padLeft(9f);
            table.table(t -> {
                t.left();
                t.add("[lightgray]"+Stat.range.localized()+":[] "+ (int)(range / tilesize) + " " + Core.bundle.get("unit.blocks")).left();
                t.row();
                //Surprisingly, rules.weather.duration is "Duration:". I just hope it is the same for other translations..
                t.add("[lightgray]"+Core.bundle.get("rules.weather.duration")+"[] "+ (int)(inflictDuration / 60) + " " + Core.bundle.get("unit.seconds")).left();
            }).growX();
        });
    }
}
