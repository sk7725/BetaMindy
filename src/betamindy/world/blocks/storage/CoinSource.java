package betamindy.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.distribution.*;
import betamindy.world.blocks.logic.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;

import static arc.Core.atlas;

public class CoinSource extends Block {
    public int balance = 10000;

    public TextureRegion topRegion;
    public Color topColor1 = Pal2.coin;
    public Color topColor2 = Pal2.darkCoin;
    public float spinSpeed = 0.5f;

    public float effectChance = 0.02f;
    public Effect coinEffect = MindyFx.coins;

    public CoinSource(String name){
        super(name);
        update = true;
        solid = true;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public class CoinSourceBuild extends Building implements CoinBuild, PushReact, SpinUpdate, SpinDraw {
        @Override
        public int coins(){
            return balance; //always maintains balance; thus it acts both as a source and a void
        }

        @Override
        public void handleCoin(Building source, int amount){}

        @Override
        public boolean outputCoin(){
            return true;
        }

        @Override
        public int acceptCoin(Building source, int amount){
            return amount;
        }

        @Override
        public void drawSpinning(float x, float y, float dr){
            Draw.rect(region, x, y, dr);
            Drawm.flipSprite(topRegion, x, y, dr + 90f, (Time.time * 6f / spinSpeed) % 180f - 90f, topColor1, topColor2);
            Draw.reset();
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Drawm.flipSprite(topRegion, x, y, 0, (Time.time / spinSpeed) % 180f - 90f, topColor1, topColor2);
            Draw.reset();
        }

        @Override
        public void spinUpdate(float sx, float sy,float srad, float absRot, float rawRot){
            if(Mathf.chanceDelta(Math.min(0.3f, effectChance * srad))) coinEffect.at(sx, sy);
        }

        @Override
        public void pushed(int dir){
            for(int i = 0 ; i < Mathf.random(2) + 1; i++) coinEffect.at(x + Mathf.range(1f), y + Mathf.range(1f));
        }
    }
}
