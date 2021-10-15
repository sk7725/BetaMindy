package betamindy.world.blocks.defense.turrets;

import mindustry.entities.*;
import mindustry.world.blocks.defense.turrets.*;

/* Removes build targeting */
public class UnitTurret extends PowerTurret {
    public UnitTurret(String name){
        super(name);
    }

    public class UnitTurretBuild extends PowerTurret.PowerTurretBuild {
        @Override
        protected void findTarget() {
            target = Units.bestTarget(team, x, y, range, e -> !e.dead(), b -> false, unitSort);
        }
    }
}
