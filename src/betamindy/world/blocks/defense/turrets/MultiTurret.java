package betamindy.world.blocks.defense.turrets;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.util.*;
import betamindy.world.blocks.defense.turrets.pattern.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.blocks.defense.turrets.*;

import static mindustry.Vars.*;

public class MultiTurret extends Turret {
    public TurretPattern[] patterns;
    public float powerUse = 1f;

    public MultiTurret(String name){
        super(name);
        hasPower = true;
        shootSound = Sounds.none;

        config(Integer.class, (MultiTurretBuild build, Integer i) -> {
            if(i != build.mode && build.shouldTurn() && i >= 0 && i < 4){
                build.mode = i;
                build.pattern().select();
                build.selectHeat = 30f;
            }
        });
    }

    @Override
    public void init(){
        consumePowerCond(powerUse, TurretBuild::isActive);
        super.init();
    }

    public class MultiTurretBuild extends TurretBuild {
        public int mode = 0; //0 ~ 3, top left down right
        public float selectHeat = 0f;

        public TurretPattern pattern(){
            return patterns[mode];
        }

        public BulletType shootType(){
            return pattern().shootType();
        }

        public float reloadTime(){
            return pattern().reloadTime();
        }

        @Override
        protected void updateShooting(){
            if(reload >= reloadTime()){
                BulletType type = peekAmmo();

                if(!pattern().override) shoot(type);
                pattern().shoot(type, this);

                reload = 0f;
            }else{
                reload += delta() * peekAmmo().reloadMultiplier * baseReloadSpeed();
            }
        }

        @Override
        public void updateTile(){
            unit.ammo(power.status * unit.type().ammoCapacity);
            if(selectHeat >= 0.1f) selectHeat -= delta();

            super.updateTile();

            if(unit != null && unit().isPlayer() && unit().getPlayer() == player) playerControl();
        }

        public void playerControl(){
            //clientside

            int input = Useful.wasd();
            if(!shouldTurn()) return;
            if(input == -1) return;
            if(input != mode) configure(input);
        }



        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case ammo -> power.status;
                case ammoCapacity -> 1;
                case config -> mode;
                default -> super.sense(sensor);
            };
        }

        @Override
        public Object senseObject(LAccess sensor) {
            return switch(sensor){
                //senseObject takes priority over sense unless it is a noSensed
                case config -> noSensed;
                default -> super.senseObject(sensor);
            };
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.config){
                int input = (int)p1;
                if(Vars.net.client() || !shouldTurn() || input < 0 || input >= 4 || input == mode || selectHeat > 0.2f){
                    return;
                }

                configureAny(input);
            }
        }

        @Override
        public BulletType useAmmo(){
            //nothing used directly
            return shootType();
        }

        @Override
        public boolean hasAmmo(){
            //you can always rotate, but never shoot if there's no power
            return true;
        }

        @Override
        public BulletType peekAmmo(){
            return shootType();
        }

        @Override
        public boolean shouldTurn(){
            return super.shouldTurn() && !pattern().charging();
        }

        //helper methods

        //TODO reimplement -Anuke
        public void doRecoil(){
            //recoil = recoilAmount;
        }


        public void settr(){
            //tr.trns(rotation, shootLength, Mathf.range(xRand));
        }

        public void doBullet(BulletType type, float rotation){
            //bullet(type, rotation);
        }

        public void playSound(Sound sound){
            //sound.at(x + tr.x, y + tr.y, 1);
        }

        public void playEffect(Effect e){
            //e.at(x + tr.x, y + tr.y, rotation);
        }

        //draw start

        public void drawArrow(float alpha){
            for(int i = 0; i < 4; i++){
                float len = size * tilesize / 2f + 8f + Mathf.absin(Time.globalTime, 8f, 3f);

                Tmp.v2.trns(i * 90f + 90f, len);
                Draw.color(Pal.gray, alpha);
                Drawf.tri(x + Tmp.v2.x, y + Tmp.v2.y, 12f + Mathf.sign(i == mode), 8.5f + Mathf.sign(i == mode), i * 90f + 90f);

                Tmp.v2.trns(i * 90f + 90f, len + 1f);
                Draw.color(i == mode ? Pal.accent : Color.white, alpha);
                Drawf.tri(x + Tmp.v2.x, y + Tmp.v2.y, 8f + Mathf.sign(i == mode), 4.5f + Mathf.sign(i == mode), i * 90f + 90f);
                Draw.color();
            }
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            drawArrow(1f);
        }

        @Override
        public void draw(){
            super.draw();
            //TODO: draw cool stuff
            if(selectHeat > 0.1f) drawArrow(selectHeat / 30f);
        }

        @Override
        public Object config(){
            return mode;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(mode);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            mode = read.b();
        }
    }
}
