package betamindy.world.blocks.defense.turrets;

import arc.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.ui.*;
import betamindy.world.blocks.storage.*;
import mindustry.content.*;
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
            stats.remove(Stat.shootRange);
            stats.add(Stat.shootRange, "@~@ @", minRange / tilesize, range / tilesize, Core.bundle.get("unit.blocks"));
        }
        stats.add(Stat.ammo, statAmmo(shootType, 0));
        stats.add(Stat.itemCapacity, Core.bundle.format("ui.anucoin.emoji", maxAmmo));
    }

    private static StatValue statAmmo(BulletType type, int indent){
        return table -> {
            table.row();
            boolean compact = indent > 0;
            if(!compact){
                table.image(AnucoinTex.uiCoin).size(3 * 8).padRight(4).right().top();
                table.add("@ui.anucoin.single").padRight(10).left().top().color(Pal2.coin);
            }
            table.table(bt -> {
                bt.left().defaults().padRight(3).left();

                if(type.damage > 0 && (type.collides || type.splashDamage <= 0)){
                    if(type.continuousDamage() > 0){
                        bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
                    }else{
                        bt.add(Core.bundle.format("bullet.damage", type.damage));
                    }
                }

                if(type.buildingDamageMultiplier != 1){
                    sep(bt, Core.bundle.format("bullet.buildingdamage", (int)(type.buildingDamageMultiplier * 100)));
                }

                if(type.splashDamage > 0){
                    sep(bt, Core.bundle.format("bullet.splashdamage", (int)type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                }

                if(!compact && !Mathf.equal(type.ammoMultiplier, 1f)){
                    sep(bt, Core.bundle.format("bullet.multiplier", (int)type.ammoMultiplier));
                }

                if(!compact && !Mathf.equal(type.reloadMultiplier, 1f)){
                    sep(bt, Core.bundle.format("bullet.reload", Strings.autoFixed(type.reloadMultiplier, 2)));
                }

                if(type.knockback > 0){
                    sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
                }

                if(type.healPercent > 0f){
                    sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
                }

                if(type.pierce || type.pierceCap != -1){
                    sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
                }

                if(type.incendAmount > 0){
                    sep(bt, "@bullet.incendiary");
                }

                if(type.homingPower > 0.01f){
                    sep(bt, "@bullet.homing");
                }

                if(type.lightning > 0){
                    sep(bt, Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
                }

                if(type.status != StatusEffects.none){
                    sep(bt, (type.minfo.mod == null ? type.status.emoji() : "") + "[stat]" + type.status.localizedName);
                }

                if(type.fragBullet != null){
                    sep(bt, Core.bundle.format("bullet.frags", type.fragBullets));
                    bt.row();

                    statAmmo(type.fragBullet, indent + 1).display(bt);
                }

                if(!compact){
                    //Tex.underline is broken
                    bt.row();
                    bt.image().color(Pal.gray).height(4).pad(-2).padBottom(0).padTop(9).fillX();
                }
            }).padTop(/*compact ? 0 : -9*/0).padLeft(indent * 8).left().get().background(null);

            table.row();
        };
    }

    private static void sep(Table table, String text){
        table.row();
        table.add(text);
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