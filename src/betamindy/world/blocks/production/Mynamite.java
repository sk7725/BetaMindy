package betamindy.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import betamindy.world.blocks.environment.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.InputHandler;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.*;

import mindustry.type.Item;
import mindustry.world.blocks.distribution.Conveyor;

import mindustry.Vars;

import betamindy.content.*;

import static mindustry.Vars.tilesize;

public class Mynamite extends Block {
    public int tier = 0, minTier = 0;
    public float fuseTime = 140f;
    public int mineRadius = 2;
    public float damage = 600f, damageRadius = 4 * 8f;
    public int baseAmount = 0;
    public boolean canClick = true;

    public TextureRegion topRegion;

    public Effect smokeEffect = MindyFx.smokeRise;
    public Effect fireEffect = Fx.none;
    public float smokeChance = 0.08f, fireChance = 0.12f;

    public Mynamite(String name){
        super(name);

        configurable = true;
        saveConfig = false;

        baseExplosiveness = 8f;
        rebuildable = false;

        solid = true;
        sync = true;
        breakable = true;
        update = true;
        hasPower = true;

        autoResetEnabled = false;
        drawDisabled = false;
        enableDrawStatus = false;

        config(Boolean.class, (MynamiteBuild build, Boolean b) -> {
            if(b && !build.lit) build.light();
        });
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(this.name + "-top");
    }

    @Override
    public void init() {
        super.init();
        configurable = canClick;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.range, mineRadius, StatUnit.blocks);

        stats.add(Stat.drillTier, table -> {
            table.left();
            Seq<Block> list = Vars.content.blocks().select(b -> (b instanceof Floor) && ((Floor) b).itemDrop != null && ((Floor) b).itemDrop.hardness <= tier && ((Floor) b).itemDrop.hardness >= minTier);

            table.table(l -> {
                l.left();

                for(int i = 0; i < list.size; i++){
                    Block item = list.get(i);

                    l.image(item.uiIcon).size(8 * 3).padRight(2).padLeft(2).padTop(3).padBottom(3);
                    l.add(item.localizedName).left().padLeft(1).padRight(4);
                    if(i % 5 == 4){
                        l.row();
                    }
                }
            });
        });
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("heat", (Mynamite.MynamiteBuild entity) -> new Bar("bar.heat", Pal.lightOrange, () -> Mathf.clamp((fuseTime - entity.heat) / fuseTime)));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, mineRadius * tilesize, Pal.placing);
    }

    public class MynamiteBuild extends Building {
        public float heat = 0f;
        public boolean lit = false;

        @Override
        public void placed(){
            super.placed();
            heat = fuseTime;
        }

        public void draw(){
            super.draw();
            if(lit && heat % 45f < 22.5f) Draw.rect(topRegion, x, y);
        }

        public void drawSelect(){
            if(Vars.player.team() != team) return;
            Drawf.dashCircle(x, y, mineRadius * tilesize, team.color);
        }

        @Override
        public boolean configTapped(){
            if(canClick) {
                if (lit) return false;
                configure(true);
            }
            return false;
        }

        public void updateTile(){
            if(lit){
                heat -= delta();
                if(Mathf.chance(smokeChance)) smokeEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                if(fireEffect != Fx.none && Mathf.chance(fireChance)) fireEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                if(heat <= 0f) kill();
            }
            else if(canConsume() || tile.floor().attributes.get(Attribute.heat) > 0.01) light();
        }

        public void light(){
            MindySounds.tntfuse.at(x, y);
            heat = fuseTime;
            lit = true;
        }

        public Building destTile(Item drops, int amount){
            return Units.findAllyTile(team, x, y, 80, e->(e != null && e.block().hasItems && e.block().itemCapacity > 5 && !(e.block() instanceof Conveyor) && e.acceptStack(drops, amount, e) >= 1));
        }

        public void mineTile(Tile other, float dist){
            Item drops = other.drop();
            if(tier < drops.hardness || minTier > drops.hardness) return;

            int amount = (int) (baseAmount + 1.5f + (tier - drops.hardness) * 0.6);
            amount *= (3f - dist) / 3f;
            if(amount <= 0) return;
            amount *= (3.3f * Mathf.random() + 1.1f);
            
            if(destTile(drops, amount) == null) return;

            InputHandler.transferItemTo(
                null, drops, amount,
                other.worldx() + Mathf.range(tilesize / 2f),
                other.worldy() + Mathf.range(tilesize / 2f),
                destTile(drops, amount)
            );
        }

        public void mineCrystal(Tile other, Crystal c){
            Item drops = c.item;
            int amount = c.requirements[0].amount;
            amount = Mathf.random(amount / 2, amount + size * 2);
            if(amount <= 0) amount = 1;

            Building dest = destTile(drops, amount);
            if(dest != null){
                InputHandler.transferItemTo(
                        null, drops, amount,
                        other.worldx() + Mathf.range(tilesize / 2f),
                        other.worldy() + Mathf.range(tilesize / 2f),
                        dest
                );
            }
            if(other.build != null) other.build.kill();
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(!Vars.net.client()){
                for(int i = -mineRadius; i <= mineRadius; i++){
                    for(int j = -mineRadius; j <= mineRadius; j++){
                        Tile other = Vars.world.tile(tile.x + i, tile.y + j);
                        if(other == null) continue;
                        if(other.drop() != null) mineTile(other, Math.abs(i) + Math.abs(j));
                        if(other.block() instanceof Crystal) mineCrystal(other, (Crystal)other.block());
                    }
                }
            }

            if(Vars.state.rules.reactorExplosions) Damage.damage(x, y, damageRadius, damage);
        }

        @Override
        public void write(Writes w){
            super.write(w);
            w.bool(lit);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            lit = read.bool();
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case heat -> heat;
                case enabled -> lit ? 1 : 0;
                default -> super.sense(sensor);
            };
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.enabled){
                boolean shouldLight = !Mathf.zero(p1);

                //currently, turning off mynamites are not supported
                if(Vars.net.client() || lit || !shouldLight){
                    return;
                }

                configureAny(true);
            }
        }
    }
}
