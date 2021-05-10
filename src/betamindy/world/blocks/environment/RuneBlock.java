package betamindy.world.blocks.environment;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.world.blocks.distribution.*;
import betamindy.world.blocks.logic.*;
import betamindy.world.blocks.payloads.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.Core.atlas;

public class RuneBlock extends Block {
    public TextureRegion glowRegion;
    public Effect glowEffect = MindyFx.omegaShine;
    public float glowChance = 0.02f;

    protected Color glowColor = Pal.lancerLaser.cpy();

    public RuneBlock(String name){
        super(name);
        update = true;
        solid = true;
        rotate = false;
    }

    @Override
    public void load(){
        super.load();
        glowRegion = atlas.find(name + "-glow");
    }

    public class RuneBuild extends Building implements SpinDraw, SpinUpdate, PushReact {
        @Override
        public void draw(){
            drawSpinning(x, y, 0f);
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            Draw.rect(region, x, y, dr);

            Draw.color(glowColor.shiftHue(Time.delta * 0.1f), Mathf.absin(Time.globalTime + Mathf.randomSeed(id) * 69, 9f, 0.7f));
            Draw.blend(Blending.additive);
            Draw.rect(glowRegion, x, y, dr);
            Draw.color();
            Draw.blend();
        }

        @Override
        public void spinUpdate(float sx, float sy,float srad, float absRot, float rawRot){
            if(Mathf.chanceDelta(Math.min(0.3f, glowChance * srad))) glowEffect.at(sx + Mathf.range(4f), sy + Mathf.range(4f));
        }

        @Override
        public void pushed(int dir){
            for(int i = 0 ; i < Mathf.random(2) + 1; i++) glowEffect.at(x + Mathf.range(4f), y + Mathf.range(4f));
        }
    }
}
