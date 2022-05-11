package betamindy.world.blocks.power;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static mindustry.Vars.world;

public class IsotopeReactor extends PowerGenerator {
    public ObjectFloatMap<Block> ores = new ObjectFloatMap<>();
    public TextureRegion topRegion, lightRegion;
    public Effect generateEffect = Fx.generatespark;
    public Color heatColor = Color.valueOf("ff9b59");

    private static final Seq<Block> list = new Seq<>();

    public IsotopeReactor(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, 100f * size * 2);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.input, table -> {
            table.left();
            list.clear();
            for(Block b : ores.keys()){
                list.add(b);
            }

            list.sort(Structs.comparingFloat(a -> ores.get(a, 0f)));

            table.table(l -> {
                l.left();

                for(int i = 0; i < list.size; i++){
                    Block item = list.get(i);

                    l.image(item.uiIcon).size(32f);
                    l.add(" " + (int)(ores.get(item, 0f) * 100) + "%").color(item.mapColor);
                    if(i < list.size - 1) l.add(" / ");
                }
            });
        });
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        lightRegion = atlas.find(name + "-light");
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return getSum(tile) > 0.00001f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Tile t = world.tile(x, y);
        if(t != null) drawPlaceText(Core.bundle.formatFloat("bar.efficiency", getSum(t) * 100, 1), x, y, valid);
    }

    public float getEfficiency(Block b){
        return ores.get(b, 0f);
    }

    public float getSum(Tile tile){
        return tile.getLinkedTilesAs(this, tempTiles).sumf(t -> getEfficiency(t.overlay()));
    }

    public class IsotopeReactorBuild extends GeneratorBuild {
        public float sum, maxf, heat;
        public Block maxB = null;

        @Override
        public void updateTile(){
            productionEfficiency = sum + MindyAttribute.magnetic.env();

            if(productionEfficiency > 0.1f && Mathf.chance(0.05 * delta())){
                generateEffect.at(x + Mathf.range(3f), y + Mathf.range(3f), maxB == null ? Pal.lightishGray : maxB.mapColor);
            }
            heat = Mathf.lerpDelta(heat, productionEfficiency > 0.1f ? 1f : 0f, 0.05f);
        }

        @Override
        public void draw(){
            super.draw();
            Draw.color(heatColor);
            Draw.alpha(heat * 0.4f + Mathf.absin(Time.time, 8f, 0.6f) * heat);
            Draw.rect(topRegion, x, y);
            Draw.reset();

            if(maxB != null){
                Draw.z(Layer.bullet - 0.001f);
                Draw.color(maxB.mapColor, heat);
                Draw.rect(lightRegion, x, y);
                Draw.reset();
            }
        }

        @Override
        public void drawLight(){
            if(maxB != null) Drawf.light(x, y, heat * (40f + Mathf.absin(10f, 5f)) * Math.min(productionEfficiency, 2f) * size, maxB.mapColor, 0.4f);
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            maxf = 0f;
            sum = tile.getLinkedTilesAs(block, tempTiles).sumf(t -> {
                float g = getEfficiency(t.overlay());
                if(g > 0.0001f && (maxB == null || g > maxf)){
                    maxf = g;
                    maxB = t.overlay();
                }
                return g;
            });
        }
    }
}
