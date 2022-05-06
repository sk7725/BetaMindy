package betamindy.world.blocks.environment;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class GlowPowder extends Block {
    public Color color1, color2;
    /**0 for circles, 1 for squares.*/
    public final int shape;
    public @Nullable StatusEffect status;
    public @Nullable Effect effect;
    public float range = 40f;
    public float duration = 600f;
    public float effectChance = 0.005f;

    public GlowPowder(String name, int shape){
        super(name);
        this.shape = shape;
        solid = false;
        buildVisibility = BuildVisibility.editorOnly;
        update = true;
        targetable = false;
        breakable = false;
        destructible = false;
        rebuildable = false;
        hasColor = false;
        hasShadow = false;
        fillsTile = false;
    }

    @Override
    public boolean isHidden(){
        return true;
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    @Override
    public int minimapColor(Tile tile){
        return Tmp.c1.set(color1).lerp(color2, Mathf.absin(tile.worldx() + tile.worldy(), 8f, 1f)).rgba();
    }

    public class GlowPowderBuild extends Building{
        /**Seconds before it goes away. Negative number to disable entirely*/
        public float lifetime = -1f;
        @Override
        public void updateTile(){
            if(lifetime >= 0f){
                lifetime -= delta() / 60f;
                if(lifetime <= 0f) tile.removeNet();
            }

            if(status != null){
                Units.nearby(x - range / 2f, y - range / 2f, range, range, u -> u.apply(status, duration));
            }

            if(effect != null && Mathf.chance(effectChance)){
                effect.at(this);
            }
        }

        @Override
        public void draw(){
            Draw.z(Layer.bullet - 0.0001f);
            Draw.color(color1, color2, Mathf.absin((float)Time.time + 7f * id, 16f, 1f));

            float scl = (lifetime < -0.5f) ? 1f : Mathf.clamp(lifetime / 2f);

            for(int i = 0; i < Mathf.randomSeed(id + 10) * 6 + 4; i++){
                if(shape == 0) Fill.circle(x + Mathf.randomSeed(id + i) * 10f - 5f, y + Mathf.randomSeed(id + i + 1) * 10f - 5f, Mathf.randomSeed(id + i + 2) * 3f * scl);
                else Fill.square(x + Mathf.randomSeed(id + i) * 10f - 5f, y + Mathf.randomSeed(id + i + 1) * 10f - 5f, Mathf.randomSeed(id + i + 2) * 3f * scl);
            }
        }

        @Override
        public void drawTeam(){
            //no u
        }

        /*
        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation){
            return super.init(tile, Team.derelict, shouldAdd, rotation);
        }*/

        @Override
        public void drawLight(){
            Drawf.light(x, y, range * (Mathf.absin(19f, 0.3f) + 0.7f), color2, 0.6f);
        }
    }
}
