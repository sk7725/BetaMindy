package betamindy.world.blocks.defense.turrets;

import arc.scene.ui.layout.*;
import betamindy.world.blocks.storage.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.turrets.*;

public class CoinTurret extends Turret {
    public BulletType shootType;

    public CoinTurret(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        //stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, shootType)));//todo
    }

    public class CoinTurretBuild extends TurretBuild implements CoinBuild {
        @Override
        public int coins(){
            return totalAmmo;
        }
        @Override
        public void handleCoin(Building source, int amount){
            totalAmmo += amount;
        }
        @Override
        public int requiredCoin(Building source){
            if(totalAmmo >= maxAmmo) return 0;
            return maxAmmo - totalAmmo;
        }
        @Override
        public int acceptCoin(Building source, int amount){
            if(totalAmmo >= maxAmmo) return 0;
            return Math.min(maxAmmo - totalAmmo, amount);
        }

        @Override
        public void updateTile(){
            if(unit != null){
                unit.ammo((float)unit.type().ammoCapacity * totalAmmo / maxAmmo);
            }

            //todo remove
            if(totalAmmo == 0) totalAmmo = maxAmmo;
            super.updateTile();
        }

        @Override
        public void displayBars(Table bars){
            super.displayBars(bars);

            bars.add(new Bar("stat.ammo", Pal.ammo, () -> (float)totalAmmo / maxAmmo)).growX();
            bars.row();
        }

        @Override
        public BulletType useAmmo(){
            totalAmmo -= ammoPerShot;
            totalAmmo = Math.max(totalAmmo, 0);
            return shootType;
        }

        @Override
        public boolean hasAmmo(){
            return totalAmmo >= ammoPerShot;
        }

        @Override
        public BulletType peekAmmo(){
            return shootType;
        }
    }
}