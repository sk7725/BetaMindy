package betamindy.entities.bullet;

import arc.graphics.g2d.*;
import betamindy.content.*;
import betamindy.world.blocks.environment.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.tilesize;

public class CrystalBulletType extends ArtilleryBulletType {
    public Crystal crystal;
    public int trailLen = 6;
    public float trailWidth = 4.5f;
    public Effect placeEffect = MindyFx.placeShine;

    public CrystalBulletType(Crystal block){
        super(4f, 1f);
        splashDamage = 1f;
        splashDamageRadius = 8f;
        crystal = block;
        collidesAir = false;
        collidesGround = false;
        collides = false;
        scaleLife = true;
        hittable = false;
        absorbable = false;
        reflectable = false;
        frontColor = lightColor = hitColor = trailColor = crystal.item.color;
        backColor = crystal.item.color.cpy().mul(0.7f);
        lightOpacity = 0.9f;
        despawnEffect = hitEffect = Fx.mineBig;
        trailEffect = crystal.updateEffect;
        trailChance = 0.1f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new Trail(trailLen);
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof Trail){
            ((Trail)b.data).draw(frontColor, trailWidth);
        }
        Draw.color(frontColor);
        Fill.square(b.x, b.y, trailWidth / 1.414f, b.rotation() + 45f);
        Draw.color();
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.data instanceof Trail){
            ((Trail)b.data).update(b.x, b.y);
        }
        else b.data = new Trail(trailLen);
    }

    @Override
    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);
        Tile tile = Vars.world.tileWorld(x, y);
        if(tile != null && Build.validPlace(crystal, Vars.state.rules.defaultTeam, tile.x, tile.y, 0, false)){
            placeEffect.at(tile.worldx(), tile.worldy(), tilesize * crystal.size, frontColor);
            if(!Vars.net.client()) tile.setNet(crystal, Team.derelict, 0);
        }
    }

    @Override
    public void despawned(Bullet b){
        MindyFx.trailFade.at(b.x, b.y, trailWidth, frontColor, b.data);
        super.despawned(b);
    }
}
