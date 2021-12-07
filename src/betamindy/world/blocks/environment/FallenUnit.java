package betamindy.world.blocks.environment;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class FallenUnit extends Prop {
    public String unitName = "flare";
    public Team unitTeam = Team.crux;
    public UnitType unit;

    public Effect smokeEffect = MindyFx.smokeRise;
    public Effect flameEffect = Fx.fire;
    public float effectChance = 0.18f;
    public float smokeChance = 0.1f;

    public FallenUnit(String name){
        super(name);
        variants = 0;
        destroyEffect = breakEffect = Fx.explosion;
        destroySound = breakSound = Sounds.explosion;
    }

    public void initUnit(){
        if(unit == null){
            unit = Vars.content.getByName(ContentType.unit, unitName);
            if(unit == null) unit = UnitTypes.flare;
        }
    }

    @Override
    public void init(){
        super.init();
        initUnit();
    }

    @Override
    public void load(){
        initUnit();
        region = unit.fullIcon;
        fullIcon = unit.fullIcon;
        uiIcon = unit.uiIcon;
    }

    @Override
    public TextureRegion[] icons(){
        initUnit();
        return new TextureRegion[]{unit.fullIcon};
    }

    @Override
    public void drawBase(Tile tile){
        float r = Mathf.randomSeed(tile.pos(), 360f);

        //soft shadow
        Draw.color(0, 0, 0, 0.4f);
        float rad = 1.6f;
        float size = Math.max(unit.region.width, unit.region.height) * Draw.scl;
        Draw.rect(unit.softShadowRegion, tile.worldx(), tile.worldy(), size * rad * Draw.xscl, size * rad * Draw.yscl, r - 90);
        Draw.color();

        //region
        Draw.rect(unit.fullIcon, tile.worldx(), tile.worldy(), r - 90);

        if(!Vars.state.isGame()) return;

        //cell
        if(unit.drawCell){
            float f = 1f - ((Time.time + r) % 50f) / 50f;
            int i = (int)((Time.time + r) / 50f);
            Draw.color(Tmp.c1.set(Color.black).lerp(unitTeam.color, f * Mathf.randomSeed(tile.pos() + i, 0.8f)));
            Draw.rect(unit.cellRegion, tile.worldx(), tile.worldy(), r - 90);
            Draw.color();
        }

        Draw.color(Pal.rubble, 0.3f);
        Tmp.v1.set(Mathf.randomSeedRange(tile.pos() + 3, unit.hitSize / 3f), Mathf.randomSeedRange(tile.pos() + 4, unit.hitSize / 3f));
        Draw.rect(Tmp.v1.x > Tmp.v1.y ? "scorch-0-0" : "scorch-0-1", tile.worldx() + Tmp.v1.x, tile.worldy() + Tmp.v1.y, r * 17.1f);
        Draw.reset();

        if(!Vars.state.isPaused()){
            if(tile.pos() % 3 > 0 && Mathf.chanceDelta(effectChance)){
                Tmp.v1.trns(r * -8.9f, unit.hitSize / 4f).add(tile.worldx(), tile.worldy());
                flameEffect.at(Tmp.v1.x, Tmp.v1.y, Pal.lightFlame);
            }
            if(Mathf.chanceDelta(smokeChance)){
                Tmp.v1.rnd(unit.hitSize / 2f).add(tile.worldx(), tile.worldy());
                smokeEffect.at(Tmp.v1.x, Tmp.v1.y, Pal.gray);
            }
        }
    }
}
