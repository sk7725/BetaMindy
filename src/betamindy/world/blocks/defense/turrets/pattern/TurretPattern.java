package betamindy.world.blocks.defense.turrets.pattern;

import arc.*;
import arc.audio.*;
import arc.util.*;
import betamindy.world.blocks.defense.turrets.MultiTurret.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class TurretPattern {
    public String name;
    public BulletType shootType;
    public @Nullable BulletType chargeType;
    public int chargeDuration;
    public float reloadTime;
    /** whether to use the pattern's shoot() instead of the turret's */
    public boolean override = false;

    public Sound shootSound = Sounds.shoot;

    protected int charge = 0; //0 ~ chargeDuration

    public TurretPattern(String name, BulletType shootType){
        this.name = name;
        this.shootType = shootType;
    }

    public TurretPattern(String name){
        this.name = name;
    }

    public BulletType shootType(){
        if(chargeType == null) return shootType;
        return charge >= chargeDuration ? chargeType : shootType;
    }

    public float reloadTime(){
        return reloadTime;
    }

    public void select(){
        charge = 0;
    }

    public boolean charging(){
        return false;
    }

    public void shoot(BulletType b, MultiTurretBuild turret){
        if(chargeType == null) return;
        if(b == chargeType) charge = 0;
        else charge++;

        turret.playSound(shootSound);
    }

    public String localized(){
        return Core.bundle.get("pattern."+name);
    }
}
