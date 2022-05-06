package betamindy.world.blocks.defense.turrets;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;

/**
 * Fires bolts guaranteed to kill the target. Used for cutscenes or as an unmovable obstacle, intentionally OP.
 * The player will never be able to utizlixe this turret, so don't complain about it being too op ;^)
 * @author Goober
 */
public class SentryTurret extends Turret {
    public BulletType shootType;
    public TextureRegion flapRegion, leftFlap, rightFlap, bottomRegion, topRegion;

    public SentryTurret(String name){
        super(name);
        buildVisibility = BuildVisibility.editorOnly;
        hasShadow = false;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        leftFlap = atlas.find(name + "-left");
        rightFlap = atlas.find(name + "-right");
        bottomRegion = atlas.find(name + "-bottom");
        flapRegion = atlas.find(name + "-closed");
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("health");
    }

    public class SentryTurretBuild extends TurretBuild {
        public float activeHeat;
        public boolean loreControlled = false;

        @Override
        public void updateTile(){
            if(unit != null){
                unit.ammo(activeHeat > 0.99f ? unit.type().ammoCapacity : 0f);
            }
            activeHeat = Mathf.lerpDelta(activeHeat, enabled && loreControlled ? 1f : 0f, 0.043f);

            super.updateTile();
        }

        @Override
        public void draw(){
            //TODO please use a custom DrawTurret for this -Anuke
            TextureRegion base = ((DrawTurret)drawer).base;

            if(activeHeat > 0.95f){
                //draw normally
                Draw.z(Layer.block - 0.01f);
                Drawf.shadow(base, x - elevation, y - elevation, 0f);
                Draw.z(Layer.block);
                super.draw();
            }
            else if(activeHeat < 0.01f){
                //draw closed
                Draw.z(Layer.floor + 0.02f);
                Draw.rect(flapRegion, x, y);
            }
            else if(activeHeat < 0.6f){
                float h = activeHeat / 0.6f;
                Draw.z(Layer.floor + 0.01f);
                Draw.rect(bottomRegion, x, y);
                Draw.z(Layer.floor + 0.02f);
                drawTurret((1f - h) * 0.6f, Mathf.clamp(h * 2f - 1f));
                Draw.z(Layer.floor + 0.03f);
                float f = 1f - Mathf.clamp(h * 2f);
                Draw.rect(leftFlap, x - h * 10f, y, leftFlap.width * Draw.scl * Draw.xscl * f, leftFlap.height * Draw.scl * Draw.yscl);
                Draw.rect(rightFlap, x + h * 10f, y, rightFlap.width * Draw.scl * Draw.xscl * f, rightFlap.height * Draw.scl * Draw.yscl);
            }else{
                float h = (activeHeat - 0.6f) / 0.4f;
                Draw.z(Layer.block - 0.01f);
                Drawf.shadow(base, x - elevation * h, y - elevation * h, 0f);
                Draw.z(Layer.block);
                Draw.rect(base, x , y);
                recoilOffset.trns(rotation, -recoil);
                Drawf.shadow(region, x + recoilOffset.x - elevation * h, y + recoilOffset.y - elevation * h, rotation - 90);
                Draw.rect(region, x + recoilOffset.x, y + recoilOffset.y, rotation - 90);
            }
            Draw.z(Layer.floor + 0.04f);
            Draw.rect(topRegion, x, y);
        }

        public void drawTurret(float off, float f){
            TextureRegion base = ((DrawTurret)drawer).base;

            Draw.rect(base, x - off, y - off);
            recoilOffset.trns(rotation, -recoil);
            //Drawf.shadow(region, x + tr2.x - elevation * f - off, y + tr2.y - elevation * f - off, rotation - 90);
            Draw.rect(region, x + recoilOffset.x - off, y + recoilOffset.y - off, rotation - 90);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4){
            if(type == LAccess.shootp) loreControlled = true;
            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public BulletType useAmmo(){
            totalAmmo = 1;
            return shootType;
        }

        @Override
        public boolean hasAmmo(){
            return activeHeat > 0.99f;
        }

        @Override
        public BulletType peekAmmo(){
            return shootType;
        }

        @Override
        public float handleDamage(float amount){
            return 0f;
        }

        @Override
        public void damage(float damage){
        }

        @Override
        public boolean canPickup(){
            return false;
        }
    }
}
