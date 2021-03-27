package betamindy.world.blocks.units;

import arc.audio.*;
import arc.graphics.g2d.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class BoostPad extends Block {
    public StatusEffect status = MindyStatusEffects.booster;
    public float duration = 140f, cooldown = 60f;
    public boolean impulseUnit = true;

    public int animSpeed = 40;
    public TextureRegion[] animRegion = new TextureRegion[4];

    public Sound boostSound = MindySounds.boost;
    public Effect boostEffect = MindyFx.boostBlock;

    public BoostPad(String name){
        super(name);

        update = true;
        solid = false;
    }

    @Override
    public void load() {
        super.load();
        for(int i = 0; i < 4; i++){
            animRegion[i] = atlas.find(name + "-" + i);
        }
    }

    public class BoostPadBuild extends Building {
        float heat = 0f;

        @Override
        public void draw(){
            Draw.rect(animRegion[((int)Time.time % 160) / animSpeed], x, y);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(heat > 0f) heat -= delta();
            Units.nearby(team, x - 2f - size * tilesize / 2f, y - 2f - size * tilesize / 2f, size * tilesize + 4f, size * tilesize + 2f, this::boostUnit);
        }

        public void boostUnit(Unit unit) {
            if(heat <= 0f){
                boostSound.at(this);
                boostEffect.at(this);
                heat = cooldown;
            }

            if(impulseUnit) unit.impulseNet(Tmp.v1.trns(unit.rotation, unit.mass() * 10f));
            unit.apply(status, duration);
        }
    }
}
