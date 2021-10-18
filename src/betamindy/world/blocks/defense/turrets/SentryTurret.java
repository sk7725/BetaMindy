package betamindy.world.blocks.defense.turrets;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.bullet.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;

/**
 * Fires bolts guaranteed to kill the target. Used for cutscenes or as an unmovable obstacle, intentionally OP.
 * The player will never be able to utizlixe this turret, so don't complain about it being too op ;^)
 * @author Goober
 */
public class SentryTurret extends Turret {
    public BulletType shootType;
    public TextureRegion flapRegion, leftFlap, rightFlap, holeRegion; //todo

    public SentryTurret(String name){
        super(name);
        buildVisibility = BuildVisibility.editorOnly;
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.remove("health");
    }

    public class SentryTurretBuild extends TurretBuild {
        public float activeHeat;
        public boolean loreControlled = false;

        @Override
        public void updateTile(){
            if(unit != null){
                unit.ammo(activeHeat > 0.99f ? unit.type().ammoCapacity : 0f);
            }
            activeHeat = Mathf.lerpDelta(activeHeat, enabled && loreControlled ? 1f : 0f, 0.04f);

            super.updateTile();
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
