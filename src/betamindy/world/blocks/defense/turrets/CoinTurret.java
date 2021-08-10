package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.scene.ui.layout.*;
import betamindy.world.blocks.storage.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;

public class CoinTurret extends Turret {
    public BulletType shootType;
    public boolean hasMinRange = false;

    public CoinTurret(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        if(hasMinRange){
            stats.remove(Stat.range);
            stats.add(Stat.range, "{0} ~ {1} {2}", minRange / tilesize, range / tilesize, Core.bundle.get("unit.blocks"));
        }
        //stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, shootType)));//todo
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        if(hasMinRange){
            Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, minRange, Pal.placing);
        }
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
        protected void findTarget(){
            if(!hasMinRange){
                super.findTarget();
                return;
            }
            if(targetAir && !targetGround){
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && !e.within(this, minRange), unitSort);
            }else{
                target = Units.bestTarget(team, x, y, range, e -> !e.dead() && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround) && !e.within(this, minRange), b -> !b.within(this, minRange), unitSort);
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, team.color);
            if(hasMinRange){
                Drawf.dashCircle(x, y, minRange, team.color);
            }
        }

        @Override
        public void displayBars(Table bars){
            super.displayBars(bars);

            bars.add(new Bar(() -> Core.bundle.format("bar.anucoin", totalAmmo), () -> Pal.ammo, () -> (float)totalAmmo / maxAmmo)).growX();
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