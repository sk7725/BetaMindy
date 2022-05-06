package betamindy.world.blocks.units;

import arc.audio.*;
import arc.graphics.g2d.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class BoostPad extends Block {
    public StatusEffect status = MindyStatusEffects.booster;
    public float duration = 140f, cooldown = 60f;
    public boolean impulseUnit = true;
    public float impulseAmount = 13f;
    public float rangePad = 4f;

    public int animSpeed = 40;
    public int sprites = 4;
    public TextureRegion[] animRegion;

    public Sound boostSound = MindySounds.boost;
    public Effect boostEffect = MindyFx.boostBlock;

    public BoostPad(String name){
        super(name);

        update = true;
        solid = false;
        emitLight = true;
    }

    @Override
    public void load() {
        super.load();
        animRegion = new TextureRegion[sprites];
        for(int i = 0; i < sprites; i++){
            animRegion[i] = atlas.find(name + "-" + i);
        }
    }

    public class BoostPadBuild extends Building {
        float heat = 0f;

        @Override
        public void draw(){
            Draw.rect(animRegion[(int)(Time.time / animSpeed) % sprites], x, y);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(heat > 0f) heat -= delta();
            Units.nearby(team, x - (size * tilesize + rangePad) / 2f, y - (size * tilesize + rangePad) / 2f, size * tilesize + rangePad, size * tilesize + rangePad, this::boostUnit);
        }

        public void boostUnit(Unit unit) {
            if(heat <= 0f){
                boostSound.at(this);
                boostEffect.at(this);
                heat = cooldown;
            }

            if(impulseUnit && unit.vel().len2() < impulseAmount * impulseAmount * 0.9f){
                unit.impulseNet(Tmp.v1.trns(unit.rotation, unit.mass() * (impulseAmount - unit.vel.len())));
            }
            unit.apply(status, duration);
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius, lightColor, 0.5f);
        }
    }
}
