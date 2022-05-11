package betamindy.world.blocks.defense;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.world.blocks.logic.*;
import betamindy.world.blocks.storage.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static arc.math.geom.Geometry.d4x;
import static arc.math.geom.Geometry.d4y;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class Crusher extends Block {
    public float damage = 1.2f;
    public float buildingMultiplier = 0.6f;

    public float rotateSpeed = 4.5f;
    public TextureRegion topRegion, rotatorRegion, sideRegion;
    public float rotatorRadius = 3.8f;
    public Effect damageEffect = MindyFx.razor;
    public float effectChance = 0.3f;

    public @Nullable
    StatusEffect status;
    public float statusDuration = 300f;

    public Crusher(String name){
        super(name);
        rotate = true;
        update = true;
        solid = true;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        rotatorRegion = atlas.find(name + "-rotator");
        sideRegion = atlas.find(name + "-side");
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.damage, damage * 60, StatUnit.perSecond);
        stats.add(Stat.damage, Core.bundle.format("bullet.buildingdamage", buildingMultiplier * 100f));
        if(status == null) return;
        stats.add(Stat.damage, table -> {
            table.image(status.uiIcon).size(18f);
            table.add(" [accent]" + status.localizedName + "[] " + (int)(statusDuration / 60) + " " + Core.bundle.get("unit.seconds"));
        });
    }

    @Override
    public void drawPlanRegion(BuildPlan req, Eachable<BuildPlan> list){
        float x = req.drawx(), y = req.drawy();
        Draw.rect(region, x, y);
        Draw.rect(sideRegion, x, y, req.rotation * 90f);
        Tmp.v1.trns(req.rotation * 90f, size * tilesize / 2f).add(x, y);
        Draw.rect(rotatorRegion, Tmp.v1.x, Tmp.v1.y);
        Draw.rect(topRegion, Tmp.v1.x, Tmp.v1.y);
    }

    public class CrusherBuild extends Building implements PushReact {
        //damages att teams but not allied banks, storageBlocks nor cores
        private boolean adjacentBlades = false;
        public float time, heat;

        public float spinSpeed(){
            if(adjacentBlades){
                if((rotation % 2 == 1 ? tileX() : tileY()) % 2 == 0) return -rotateSpeed;
            }
            return rotateSpeed;
        }

        public void damageUnit(Unit unit){
            unit.damageContinuousPierce(damage * edelta());
            if(status != null) unit.apply(status, statusDuration);
            effects();
        }

        public void effects(){
            if(Mathf.chance(effectChance)) damageEffect.at(x + d4x(rotation) * (size * 4f + rotatorRadius), y + d4y(rotation) * (size * 4f + rotatorRadius), rotdeg());
        }

        @Override
        public void pushed(int dir){
            heat = 0.6f;
        }

        @Override
        public void updateTile(){
            if(canConsume()){
                time += spinSpeed() * edelta() * heat;
                float l = size * tilesize / 2f;
                Units.nearby(x + d4x(rotation) * l - l, y + d4y(rotation) * l - l, l * 2, l * 2, u -> {
                    if(!u.isFlying()) damageUnit(u);
                });

                Building facing = nearby(rotation);
                if(facing != null && (facing.team != team || (state.rules.reactorExplosions && !(facing.block instanceof StorageBlock || facing.block instanceof AnucoinNode)))){
                    facing.damage(damage * buildingMultiplier * edelta());
                    effects();
                }
                heat = Mathf.lerpDelta(heat, 1f, 0.05f);
            }
            else{
                heat = Mathf.lerpDelta(heat, 0f, 0.02f);
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            adjacentBlades = ((nearby((rotation + 1)%4) != null && nearby((rotation + 1)%4).block == block) || (nearby((rotation + 3)%4) != null && nearby((rotation + 3)%4).block == block));
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(sideRegion, x, y, rotdeg());
            Draw.z(Layer.groundUnit + 1f);
            Tmp.v1.trns(rotdeg(), size * tilesize / 2f).add(this);
            Draw.rect(rotatorRegion, Tmp.v1.x, Tmp.v1.y, time);
            Draw.rect(topRegion, Tmp.v1.x, Tmp.v1.y);
        }
    }
}
