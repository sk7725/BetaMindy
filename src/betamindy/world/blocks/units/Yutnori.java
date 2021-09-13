package betamindy.world.blocks.units;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static arc.Core.atlas;
import static mindustry.Vars.headless;

public class Yutnori extends Block {
    public static final int yutSprites = 8;
    public int timerThrow = timers++;
    private static final Rand rand = new Rand();
    public float friction = 0.97f, gravity = -0.12f, throwStrength = 9f, throwDelay = 180f;

    public TextureRegion baseRegion;
    public TextureRegion[] yutRegion, backdoRegion, wordRegion; //word: mo/do/ge/geol/yut/backdo

    public Effect doGeGeolEffect = Fx.teleportOut;
    public Effect yutMoEffect = Fx.teleportActivate;
    public Effect wordEffect = Fx.none;//todo
    public Color[] colors = {Color.valueOf("03e3fc"), Color.valueOf("d66eff"), Pal.remove};//yut, mo, backdo
    public float rippleScale = 3f;
    public float yutLength = 18f; //half of the (longer) width of the yut sprite in world units

    public Yutnori(String name){
        super(name);
        update = configurable = true;
        solid = false;
        expanded = true;

        config(Integer.class, (YutnoriBuild b, Integer seed) -> {
            for(int i = 0; i < 4; i++) b.sticks[i].roll(b, seed + i, throwStrength);
            b.timer.reset(timerThrow, 0);
            b.thrown = true;
        });
    }

    @Override
    public void load(){
        super.load();
        backdoRegion = new TextureRegion[yutSprites];
        yutRegion = new TextureRegion[yutSprites];
        wordRegion = new TextureRegion[6];
        baseRegion = atlas.find(name + "-base");
        for(int i = 0; i < yutSprites; i++){
            yutRegion[i] = atlas.find(name + "-" + i);
            backdoRegion[i] = atlas.find(name + "-" + i + "-1", name + "-" + i);
        }
        for(int i = 0; i < 6; i++){
            wordRegion[i] = atlas.find(name + "-word-" + i);
        }
    }

    public class YutnoriBuild extends Building {
        public final Yut[] sticks = {new Yut(true), new Yut(), new Yut(), new Yut()};
        public boolean thrown = false;

        @Override
        public void created(){
            super.created();
            for(int i = 0; i < 4; i++) sticks[i].set(this);
        }

        @Override
        public void placed(){
            super.placed();
            for(int i = 0; i < 4; i++) sticks[i].roll(this, id - i, throwStrength / 4f);
        }

        @Override
        public void updateTile(){
            for(int i = 0; i < 4; i++) sticks[i].update();

            if(thrown && timer.check(timerThrow, throwDelay)){
                thrown = false;
                int flats = 0;
                for(int i = 0; i < 4; i++){
                    if(sticks[i].outcome) flats++;
                }
                Log.info("Yut Result:" + flats);
                if(headless) return;

                if(flats == 1 && sticks[0].outcome){
                    //backdo
                    doGeGeolEffect.at(sticks[0].x, sticks[0].y, 0f, colors[2]);
                    flats = 5;
                }
                else if(flats == 4){
                    //yut
                    for(int i = 0; i < 4; i++) yutMoEffect.at(sticks[i].x, sticks[i].y, 0f, colors[0]);
                }
                else if(flats == 0){
                    //mo
                    for(int i = 0; i < 4; i++) yutMoEffect.at(sticks[i].x, sticks[i].y, 0f, colors[1]);
                }
                else{
                    for(int i = 0; i < 4; i++){
                        if(sticks[i].outcome) doGeGeolEffect.at(sticks[i].x, sticks[i].y);
                    }
                }

                if(wordEffect != Fx.none){
                    wordEffect.at(x, y, 0f, flats == 5 ? colors[2] : (flats == 4 ? colors[0] : flats == 0 ? colors[1] : Color.white), wordRegion[flats]);
                }
            }
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            for(int i = 0; i < 4; i++) sticks[i].draw();
        }

        @Override
        public void buildConfiguration(Table table){
            super.buildConfiguration(table);
            table.button(Icon.players, Styles.clearTransi, 40f, () -> {
                configure(Mathf.rand.nextInt());
            });
        }

        public class Yut {
            public float x, y;
            protected float h, xv, yv, hv, rotation, rv, sprite, spritev;
            protected boolean determined = false;

            public boolean back = false;
            public boolean outcome = false; //true means the flat side up

            public Yut(boolean back){
                this.back = back;

            }
            public Yut(){
                this(false);
            }

            public void update(){
                x += xv * Time.delta; y += yv * Time.delta; rotation += rv * Time.delta;
                xv *= friction; yv *= friction; rv *= friction; spritev *= friction * friction;
                if(determined){
                    //lerp to determined outcome
                    sprite = Mathf.lerpDelta(sprite, outcome ? (float) (yutSprites / 2) : 0, 0.05f);
                }
                else{
                    sprite += spritev * Time.delta;
                }

                if(h > 0 || hv > 0){
                    h += hv * Time.delta;
                    hv += gravity * Time.delta;
                }
                if(h < 0){
                    h = 0;
                    if(hv < -1) hv *= -0.3f;
                    if(hv > 0.01f || !determined) hitGround();
                }
            }

            public void draw(){
                Draw.z(h > 0.2f ? Layer.turret : Layer.blockOver);
                Draw.color(Pal.shadow);
                float offset = h * 0.3f;
                int s = Mathf.mod(Mathf.roundPositive(sprite), yutSprites);
                TextureRegion region = back? backdoRegion[s] : yutRegion[s];
                Draw.rect(region, x - offset, y - offset, rotation);

                float sizeScl = offset * 0.02f + 1f;
                Draw.color();
                Draw.rect(region, x, y, region.width * Draw.scl * Draw.xscl * sizeScl, region.height * Draw.scl * Draw.yscl * sizeScl, rotation);
                Draw.reset();
            }

            public void drawOriginal(){
                //todo correctviewn't
            }

            public void roll(YutnoriBuild build, long seed, float strength){
                rand.setSeed(seed);
                outcome = rand.chance(back ? 0.65f : 0.55f);
                x = build.x;
                y = build.y;
                h = 0;
                hv = strength * 0.5f * rand.random(0.5f, 1.5f);
                xv = rand.range(strength) * 0.35f;
                yv = rand.range(strength) * 0.35f;
                rotation = rand.random(360f);
                rv = rand.range(5f);
                determined = false;
                sprite = 0;
                spritev = rand.random(0.6f);
            }

            public void hitGround(){
                if(hv < 1 && !determined){
                    determined = true;
                    spritev = 0;
                    //lerp sprite to nearest breakpoint: 0 or 4
                    sprite = sprite % yutSprites - yutSprites;
                }
                else if(!determined){
                    spritev *= Mathf.random(0.8f, 1.3f);
                }

                if(headless) return;
                Sounds.artillery.at(x, y,10f);
                Floor floor = Vars.world.floorWorld(x, y);
                if(floor.isLiquid){
                    Tmp.v1.trns(rotation, Mathf.random(yutLength));
                    floor.walkEffect.at(Tmp.v1.x, Tmp.v1.y, rippleScale, floor.mapColor);
                    Tmp.v1.trns(-rotation, Mathf.random(yutLength));
                    floor.walkEffect.at(Tmp.v1.x, Tmp.v1.y, rippleScale, floor.mapColor);
                    floor.walkSound.at(x, y, 1f, floor.walkSoundVolume);
                }else{
                    Tmp.v1.trns(rotation, Mathf.random(yutLength));
                    Fx.unitLandSmall.at(Tmp.v1.x, Tmp.v1.y, rippleScale, floor.mapColor);
                    Tmp.v1.trns(-rotation, Mathf.random(yutLength));
                    Fx.unitLandSmall.at(Tmp.v1.x, Tmp.v1.y, rippleScale, floor.mapColor);
                    Fx.unitLandSmall.at(x, y, rippleScale, floor.mapColor);
                }
            }

            public void set(Position p){
                x = p.getX();
                y = p.getY();
            }

            public void set(float x, float y){
                this.x = x;
                this.y = y;
            }
        }
    }
}
