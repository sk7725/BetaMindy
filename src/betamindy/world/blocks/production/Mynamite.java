package betamindy.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.io.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.InputHandler;
import mindustry.ui.Bar;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.*;

import mindustry.type.Item;
import mindustry.world.blocks.distribution.Conveyor;

import mindustry.Vars;

import betamindy.content.*;
import mindustry.world.meta.values.BlockFilterValue;

import static mindustry.Vars.tilesize;

public class Mynamite extends Block {
    public int tier = 0;
    public TextureRegion topRegion;
    public float tapTime = 140;
    public int mineRadius = 2;
    public float damage = 600;
    public float damageRadius = 4 * 8;
    public int baseAmount;

    public Mynamite(String name){
        super(name);

        configurable = true;
        consumesTap = true;
        baseExplosiveness = 8;
        rebuildable = false;
        solid = true;
        targetable = true;
        sync = true;
        breakable = true;
        update = true;
        hasPower = true;
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(this.name + "-top");
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.drillTier, table -> {
            new BlockFilterValue(b -> b instanceof Floor && ((Floor) b).itemDrop != null && ((Floor) b).itemDrop.hardness <= tier).display(table.left());
            table.row();
            table.add(Core.bundle.format("stat.drillradius", mineRadius)).left();
            table.row();
        });
    }

    @Override
    public void setBars() {
        super.setBars();

        bars.add("heat", (Mynamite.MynamiteBuild entity) -> new Bar("bar.heat", Pal.lightOrange, () -> ((tapTime - entity._heat)*100/tapTime)));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, mineRadius * tilesize, Pal.placing);
    }

    public class MynamiteBuild extends Building {
        public float _heat;
        public boolean fused;
        public boolean tabbed;

        @Override
        public void placed(){
            super.placed();
            _heat = tapTime;
        }

        public void draw(){
            super.draw();
            if(fused && _heat%45<22.5) Draw.rect(topRegion, x, y);
        }

        public void drawSelect(){
            if(Vars.player.team() != team) return;
            Drawf.dashCircle(x, y, mineRadius * tilesize, team.color);
        }

        @Override
        public boolean configTapped(){
            if(fused) return false;
            configure(getPowerProduction());
            active();
            return false;
        }

        public void updateTile(){
            if(fused) {
                _heat -= delta();
                if(Mathf.chance(0.08)) MindyFx.smokeRise.at(x, y);
                if(_heat <= 0) kill();
            }
            else if(consValid() || tile.floor().attributes.get(Attribute.heat) > 0.01) active();
        }

        public void active(){
            MindySounds.tntfuse.at(x, y);
            _heat = tapTime;
            fused = true;
        }

        public Building acceptTile(Item drops, int amount){
            return Units.findAllyTile(team, x, y, 80, e->(e != null && e.block().hasItems && e.block().itemCapacity > 5 && !(e.block() instanceof Conveyor) && e.acceptStack(drops, amount, e) >= 1));
        }

        public void mineTile(Tile other, float dist){
            Item drops = other.drop();
            if(tier < drops.hardness) return;

            int amount = (int) (baseAmount+1.5+(tier - drops.hardness) * 0.6);
            amount *= (3 - dist)/3;
            if(amount <= 0) return;
            amount *= 3.3*Mathf.random()+1.1;
            
            if(acceptTile(drops, amount) == null) return;

            InputHandler.transferItemTo(
                null, drops, amount,
                other.worldx() + Mathf.range(tilesize / 2f),
                other.worldy() + Mathf.range(tilesize / 2f),
                acceptTile(drops, amount)
            );
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(!Vars.net.client())
                for(int i = -mineRadius;i <= mineRadius;i++)
                    for(int j = -mineRadius;j <= mineRadius;j++){
                        Tile other = Vars.world.tile(tile.x+i, tile.y+j);
                        if(other!=null && other.drop()!=null) mineTile(other, Math.abs(i)+Math.abs(j));
                    }

            if(Vars.state.rules.reactorExplosions) Damage.damage(x, y, damageRadius, damage);
        }

        @Override
        public void write(Writes w){
            super.write(w);
            w.bool(fused);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            fused = read.bool();
        }
    }
}
