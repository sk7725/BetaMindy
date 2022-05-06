package betamindy.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import betamindy.graphics.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.Core.atlas;

public class ResearchCenter extends Block {
    public TextureRegion lightRegion, topRegion;

    public ResearchCenter(String name){
        super(name);
        update = true;
        solid = true;
        configurable = true;
        lightColor = Color.white;
        lightRadius = 80f;
    }

    @Override
    public void load(){
        super.load();
        lightRegion = atlas.find(name + "-light");
        topRegion = atlas.find(name + "-top");
    }

    @Override
    public void createIcons(MultiPacker packer){
        Drawm.generateTeamRegion(packer, this);
        super.createIcons(packer);
    }

    @Override
    public TextureRegion[] icons(){
        return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id], topRegion} : new TextureRegion[]{region, topRegion};
    }

    public class ResearchCenterBuild extends Building implements CoinBuild {
        public float heat;
        public int anucoins = 0;
        public int currentRequired = 0; //the research cost of the current research in action

        @Override
        public void updateTile(){
            heat = Mathf.lerpDelta(heat, canConsume() ? 1f : 0f, 0.05f);
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.color(lightColor, 0.5f);
            Draw.rect(topRegion, x, y);

            Draw.z(Layer.bullet - 0.01f);
            Draw.color(lightColor, heat * (Mathf.absin(19f, 0.4f) + 0.6f));
            Draw.rect(topRegion, x, y);
            Draw.color(team.color, 1f);
            Draw.rect(lightRegion, x, y);
            Draw.color();
        }

        @Override
        public int coins(){
            return anucoins;
        }

        @Override
        public void handleCoin(Building source, int amount){
            anucoins += amount;
        }

        @Override
        public int requiredCoin(Building source){
            return Math.max(0, currentRequired - anucoins);
        }

        @Override
        public int acceptCoin(Building source, int amount){
            if(anucoins >= currentRequired) return 0;
            return currentRequired - anucoins;
        }
    }
}
