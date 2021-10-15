package betamindy.world.blocks.defense.turrets.pattern;

import arc.audio.*;
import arc.math.*;
import arc.util.*;
import betamindy.world.blocks.defense.turrets.MultiTurret.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class ChaosBusterPattern extends TurretPattern{
    public int shots = 4, burstShots = 4;
    public float spread = 15f, chargeTime = 70f, burstSpacing = 7f;

    public Effect chargeEffect = Fx.greenLaserCharge;//TODO
    public Sound chargeSound = Sounds.lasercharge;
    public Sound chargeShootSound = Sounds.laserblast;

    protected boolean charging;

    public ChaosBusterPattern(String name){
        super(name);

        override = true;
    }

    @Override
    public float reloadTime(){
        return reloadTime - ((float)charge / chargeDuration) * 40f;
    }

    @Override
    public boolean charging(){
        return charging;
    }

    @Override
    public void select() {
        super.select();
        charging = false;
    }

    @Override
    public void shoot(BulletType b, MultiTurretBuild turret){
        if(charge >= chargeDuration){
            shootLaser(b, turret);
        }
        else shootBurst(b, shots + charge % 2, turret);

        if(chargeType == null) return;
        if(b == chargeType) charge = 0;
        else charge++;
    }

    public void shootBurst(BulletType b, int shots, MultiTurretBuild turret){
        for(int i = 0; i < burstShots; i++){
            Time.run(burstSpacing * i, () -> {
                if(!turret.isValid() || !turret.hasAmmo()) return;

                turret.doRecoil();
                turret.settr();

                for(int j = 0; j < shots; j++){
                    turret.doBullet(b, turret.rotation + (j - (shots - 1) / 2f) * spread);
                }
                turret.heat = 1f;

                turret.playSound(shootSound);
            });
        }
    }

    public void shootLaser(BulletType b, MultiTurretBuild turret){
        turret.settr();
        turret.playSound(chargeSound);
        turret.playEffect(chargeEffect);

        charging = true;

        Time.run(chargeTime, () -> {
            if(!turret.isValid()) return;
            turret.settr();
            turret.doRecoil();
            turret.heat = 1f;
            turret.doBullet(b, turret.rotation);
            turret.playSound(chargeShootSound);
            charging = false;
        });
    }
}
