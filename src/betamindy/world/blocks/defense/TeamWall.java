package betamindy.world.blocks.defense;

import arc.Core;
import arc.graphics.g2d.*;
import arc.util.*;
import betamindy.graphics.*;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.*;

public class TeamWall extends Wall {
    public TextureRegion iconRegion;
    public float detectRange = 80f;

    public Interval delay = new Interval(1);
    protected Team cached = Team.derelict, cached1 = Team.derelict;
    public final float waitBetween = 360f;

    public TeamWall(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        iconRegion = Drawm.getTeamRegion(this);
    }

    @Override
    public void createIcons(MultiPacker packer){
        Drawm.generateTeamRegion(packer, this);
        super.createIcons(packer);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, iconRegion};
    }

    public class TeamWallBuild extends WallBuild {
        public Team lastAttack = Team.derelict;

        @Override
        public void drawTeam(){
            //no
        }

        @Override
        public void draw(){
            drawTeamTop();
        }

        @Override
        public boolean collision(Bullet bullet){
            if(bullet.team != team) lastAttack = bullet.team;
            return super.collision(bullet);
        }

        @Override
        public void onDestroyed(){
            @Nullable Teamc t = Units.closestTarget(team, x, y, detectRange, u -> true, b -> false);
            if(t != null) lastAttack = t.team();
            else{
                @Nullable Bullet bullet = Groups.bullet.intersect(x - detectRange, y - detectRange, detectRange*2, detectRange*2).min(b -> b.team != team && b.type().hittable, b -> b.dst2(this));
                if(bullet != null && bullet.isAdded()) lastAttack = bullet.team;
            }

            if(!Vars.headless && enabled){
                if(lastAttack != Team.derelict) alert();
                else silentAlert();
            }

            super.onDestroyed();
        }

        public void alert(){
            if(cached == lastAttack && cached1 == team && !delay.check(0, waitBetween)) return;
            cached = lastAttack;
            cached1 = team;
            delay.reset(0 ,0);
            Vars.ui.hudfrag.showToast(Icon.defense, Core.bundle.format("ui.teamalert", formatTeam(team), formatTeam(lastAttack)));
        }

        public void silentAlert(){
            if(cached == Team.derelict && cached1 == team && !delay.check(0, waitBetween)) return;
            cached = Team.derelict;
            cached1 = team;
            delay.reset(0 ,0);
            Vars.ui.hudfrag.showToast(Icon.defense, Core.bundle.format("ui.teamalertsilent", formatTeam(team)));
        }

        public String formatTeam(Team t){
            return "[#" + t.color.toString() + "]" + t.localized() + "[]";
        }
    }
}
