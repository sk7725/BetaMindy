package betamindy.world.blocks.defense;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.distribution.*;
import betamindy.world.blocks.logic.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

public class Campfire extends Block {
    /** Idle consume chance per tick. */
    public float consumeChance = 0.005f;
    /** Consume & fireball chance when spinning per tick. */
    public float spinFireChance = 0.05f;
    /** Fireballs when pushed. */
    public int minFire = 2, maxFire = 4;
    public float inaccuracy = 15f;

    public float damage = 4f;
    public StatusEffect cozyStatus = MindyStatusEffects.cozy;
    public float statusDuration = 1200f;
    public float statusReload = 600f;

    public float effectChance = 0.12f, smokeChance = 0.06f;

    public TextureRegion[] coalRegions = new TextureRegion[3];
    public Effect fireEffect = MindyFx.fire, smokeEffect = Fx.fireSmoke, fireDustEffect = MindyFx.fireDust;
    public BulletType fireball = MindyBullets.colorFireball;

    /** Whether it can be used as an altar torch */
    public boolean isTorch = false;
    public TextureRegion torchRegion, torchHeatRegion;

    public Campfire(String name){
        super(name);
        update = true;
        sync = true;
        solid = true;
        hasItems = true;
        noUpdateDisabled = true;

        flags = EnumSet.of(BlockFlag.turret);
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 3; i++){
            coalRegions[i] = atlas.find(name + i);
        }
        if(isTorch){
            torchRegion = atlas.find(name + "-altar");
            torchHeatRegion = atlas.find(name + "-altar-heat");
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.damage, damage * (minFire + maxFire) / 2f);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("fuel", (CampfireBuild entity) -> new Bar(() -> Core.bundle.get("content.item.name"), entity::barColor, entity::fract));
    }

    public class CampfireBuild extends Building implements PushReact, SpinUpdate, SpinDraw {
        public float statusLeft = Mathf.random(statusReload);

        public void effects(float x, float y, boolean spin){
            if(Mathf.chanceDelta(effectChance)){
                if(spin) fireDustEffect.at(x + Mathf.range(size / 3f), y + Mathf.range(size / 3f), 0f, items.first());
                else fireEffect.at(x + Mathf.range(size / 3f), y + Mathf.range(size / 3f), 0f, items.first());
            }
            if(Mathf.chanceDelta(smokeChance)) smokeEffect.at(x + Mathf.range(size / 3f), y + Mathf.range(size / 3f));

            if(!headless) control.sound.loop(Sounds.fire, Tmp.v1.set(x, y), 0.1f);
        }

        public void torchEffects(){
            if(Mathf.chanceDelta(effectChance)){
                fireEffect.at(x + Mathf.range(size / 3f), y + Mathf.range(size / 3f), 0f, Color.white, null);
            }
            if(Mathf.chanceDelta(smokeChance)) smokeEffect.at(x + Mathf.range(size / 3f), y + Mathf.range(size / 3f));

            if(!headless) control.sound.loop(Sounds.fire, this, 0.1f);
        }

        public void fire(float x, float y, float r, float vel){
            if(items.empty()) return;
            Item item = items.first();
            fireball.create(this, team, x, y, r, damage * Mathf.clamp(item.hardness * 0.35f + 1f, 1f, 5f) * Mathf.clamp(item.flammability, 1f, 5f), Math.max(0.5f, vel), 1f, item);
            items.remove(item, 1);
        }

        public Color barColor(){
            if(items.empty()) return Pal.lightFlame;
            return FireColor.from(items.first());
        }

        public float fract(){
            return Mathf.clamp(items.total() / (float)itemCapacity);
        }

        @Override
        public void updateTile(){
            if(items.empty()) return;
            effects(x, y, false);

            statusLeft += delta();
            if(statusLeft > statusReload){
                statusLeft = 0f;
                Units.nearby(team, x, y, 40f * size, u -> u.apply(cozyStatus, statusDuration));
            }
            if(Mathf.chanceDelta(consumeChance)) items.remove(items.first(), 1);
        }

        @Override
        public void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot){
            if(items.empty()) return;
            effects(sx, sy, true);

            if(Mathf.chanceDelta(spinFireChance)) fire(sx, sy, absRot, Mathf.sqrt(srad / 8f));
        }

        @Override
        public void pushed(int dir){
            if(items.empty()) return;
            int rnd = Mathf.random(minFire, maxFire);

            for(int i = 0; i < rnd; i++) fire(x, y, dir * 90f + Mathf.range(inaccuracy), 1f);
        }

        @Override
        public void draw(){
            drawSpinning(x, y, 0f);
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            Draw.rect(region, x, y, dr);
            if(items.empty()) return;
            int s = (int)(Mathf.clamp(((float) items.total()) / itemCapacity) * 3f);
            if(s >= 3) s = 2;
            Draw.rect(coalRegions[s], x, y, dr);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }
    }
}
