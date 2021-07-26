package betamindy.world.blocks.environment;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

public class ShaderCrystal extends Crystal{
    public Color color1 = Color.cyan;
    public Color color2 = Color.pink;
    public float animTime = 18f;
    public float auraOpacity = 0.18f;
    public @Nullable Shader shader;

    public ShaderCrystal(String name, Item item){
        super(name, item, 10);
    }

    @Override
    public int minimapColor(Tile tile){
        return Tmp.c1.set(color1).lerp(color2, Mathf.absin(tile.worldx() + tile.worldy(), 8f, 1f)).rgba();
    }

    public class ShaderCrystalBuild extends CrystalBuild {
        public Color color(){
            return Tmp.c1.set(color1).lerp(color2, Mathf.absin(Time.time + id / 3f, animTime, 1f));
        }

        @Override
        public void updateTile(){
            if(Mathf.chanceDelta(effectChance)) updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f), color());
        }

        @Override
        public void beforeDraw(){}

        @Override
        public void draw(){
            float ox = x + Mathf.randomSeedRange(id, 0.5f);
            float oy = y + Mathf.randomSeedRange(id + 628, 0.5f);
            float r = Mathf.randomSeedRange(id + 420, 15f);
            int sprite = Mathf.randomSeed(id, 0, sprites - 1);

            Draw.z(Layer.blockOver - 0.1f);
            Draw.blend(Blending.additive);
            Draw.color(color(), auraOpacity);
            Draw.rect("circle-shadow", ox, oy, sizeScl * 1.5f, sizeScl * 1.5f);
            Draw.blend();
            Draw.color();

            beforeDraw();

            if(shader != null){
                Draw.draw(Layer.blockOver + 0.01f, () -> {
                    Draw.shader(shader);
                    Draw.rect(regions[sprite], ox, oy, sizeScl, sizeScl, r);
                    Draw.shader();
                    Draw.reset();
                });
            }
            else{
                Draw.color(color());
                Draw.rect(regions[sprite], ox, oy, sizeScl, sizeScl, r);
            }

            Draw.color();
            if(Vars.renderer.drawStatus) afterDraw();

            if(Vars.renderer.bloom == null) return;
            Draw.z(Layer.bullet - 0.01f);
            Draw.color(Color.white, color(), 0.4f);
            Draw.alpha(glowOpacity);
            Draw.rect(shineRegion[sprite], ox, oy, sizeScl, sizeScl, r);
            Draw.color();
        }

        @Override
        public void drawLight(){
            super.drawLight();
            Drawf.light(x, y, 30f, Tmp.c1.set(color()).mul(0.7f), 0.25f);
            Drawf.light(x, y, 10f, Tmp.c1.set(color()).mul(0.7f), 0.95f);
        }

        @Override
        public void drawTeam(){
            if(team == Team.derelict) return;
            super.drawTeam();
        }

        @Override
        public void onDestroyed(){
            destroyEffect.at(x, y, item.color);
            Damage.status(null, x, y, 30f, status, 300f, true, true);
        }
    }
}
