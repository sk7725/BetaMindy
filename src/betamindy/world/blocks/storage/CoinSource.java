package betamindy.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import betamindy.graphics.*;
import mindustry.gen.*;
import mindustry.world.*;

import static arc.Core.atlas;

public class CoinSource extends Block {
    public int balance = 10000;

    public TextureRegion topRegion;
    public Color topColor1 = Pal2.coin;
    public Color topColor2 = Pal2.darkCoin;
    public float spinSpeed = 0.5f;
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

    public class CoinSourceBuild extends Building implements CoinBuild{
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
        public void draw(){
            super.draw();
            Drawm.flipSprite(topRegion, x, y, 0f, (Time.time / spinSpeed) % 180f - 90f, topColor1, topColor2);
            Draw.reset();
        }
    }
}
