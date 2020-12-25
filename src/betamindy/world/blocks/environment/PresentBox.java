package betamindy.world.blocks.environment;

import arc.audio.Sound;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.io.*;
import betamindy.content.MindySounds;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import java.util.Locale;

import static arc.Core.atlas;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;

public class PresentBox extends Block {
    //How many colors do you want?
    //yes.
    public static final Color[] colors = {Team.sharded.color, Team.crux.color, Team.blue.color, Team.green.color, Team.purple.color, thorium.color, titanium.color, plastanium.color, pyratite.color, cryofluid.color, slag.color, blastCompound.color, Pal.place, Pal.engine, surgeAlloy.color, Pal.lancerLaser, Pal.sapBullet, Pal.heal};
    public int itemCount = 15, naughtyItemCount = 3;
    public float naughtyChance = 0.05f;
    public Item naughtyItem = coal;
    public TextureRegion baseRegion, boxRegion, ribbonRegion, ribbonRegionBack, ribbonRegionBase, topRegion;
    public Effect openEffect = Fx.none, naughtyEffect = Fx.explosion;
    public Sound openSound = MindySounds.presentbells, naughtySound = Sounds.bang;

    public PresentBox(String name){
        super(name);

        solid = true;
        update = true;
        configurable = true;
        saveConfig = false;
        drawDisabled = false;
        hasItems = true;
        itemCapacity = itemCount;

        config(Item.class, (PresentBuild entity, Item item) -> {
            entity.open = true;
            boolean naughty = item == naughtyItem;
            entity.items.add(item, naughty ? naughtyItemCount : itemCount);
            if(!Vars.headless){
                if(naughty){
                    naughtySound.at(entity);
                    naughtyEffect.at(entity);
                    Effect.shake(size, 3f, entity);
                }
                else{
                    openSound.at(entity);
                    openEffect.at(entity);
                }
            }
        });
    }

    @Override
    public void load(){
        super.load();
        baseRegion = atlas.find(name + "-base", "betamindy-present-base");
        boxRegion = atlas.find(name + "-box", "betamindy-present-box");
        topRegion = atlas.find(name + "-top");
        ribbonRegion = atlas.find(name + "-ribbon", "betamindy-present-ribbon");
        ribbonRegionBack = atlas.find(name + "-ribbon-back", "betamindy-present-ribbon-back");
        ribbonRegionBase = atlas.find(name + "-ribbon-base");
    }

    public class PresentBuild extends Building {
        public Color color1, color2;
        public boolean open;

        private float heat = 0;

        @Override
        public Building create(Block block, Team team){
            Building result = super.create(block, team);
            ((PresentBuild)result).color1 = (team != null && team != Team.derelict && Mathf.chance(0.5f)) ? team.color : colors[Mathf.random(colors.length - 1)];
            ((PresentBuild)result).color2 = colors[Mathf.random(colors.length - 1)];
            return result;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(open) heat = Mathf.lerpDelta(heat, 1f, 0.05f);
        }

        @Override
        public void draw(){
            if(open){
                Draw.rect(baseRegion, x, y);
                Draw.color(color1);
                Draw.rect(boxRegion, x, y);
                Draw.reset();
                if(heat < 0.99f){
                    drawLid(x + heat * 15f * Mathf.sign(id % 2 == 0), y + heat * ( 0.7f - heat) * 26f, heat * 360f * Mathf.randomSeed(id), 1 - heat);
                }
            }
            else{
                drawLid(x, y, rotation, 1f);
            }
        }

        public void drawLid(float x, float y, float rotation, float alpha){
            Draw.z(Layer.turret + 1f);
            Draw.color(color1, alpha);
            Draw.rect(topRegion, x, y, rotation);
            Draw.color(color2, alpha);
            Draw.rect(ribbonRegionBase, x, y, rotation);
            float r = Mathf.randomSeed(id) * 360f;
            Draw.rect(ribbonRegionBack, x, y, r + 20f + rotation);
            Draw.mixcol(Pal.shadow, 1f);
            Draw.alpha(0.21f * alpha);
            Draw.rect(ribbonRegion, x, y - 0.5f, r + rotation);
            Draw.mixcol();
            Draw.alpha(alpha);
            Draw.rect(ribbonRegion, x, y + 0.3f, r + rotation);
            Draw.reset();
        }

        @Override
        public boolean configTapped(){
            if(open){
                //Vars.control.input.frag.inv.showFor(this);
                return false;
            }
            Item item = (Vars.player.name.toLowerCase().startsWith("glen") || Mathf.chance(naughtyChance)) ? naughtyItem : Vars.content.items().random(naughtyItem);
            configure(item);
            return false;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            open = read.bool();
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(open);
        }
    }
}
