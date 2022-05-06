package betamindy.world.blocks.storage;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import betamindy.graphics.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.world;

public class AnucoinVault extends Block {
    public int capacity = 50000;

    public AnucoinVault(String name){
        super(name);
        update = solid = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.itemCapacity, Core.bundle.format("ui.anucoin.emoji", capacity));
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("anucoins", (AnucoinVaultBuild entity) -> new Bar(
                () -> Core.bundle.format("bar.anucoin", entity.totalCoins()),
                () -> Color.coral,
                () -> Mathf.clamp(entity.fract())));
    }

    /*
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
    }

    @Override
    public void createIcons(MultiPacker packer){
        Drawm.generateTeamRegion(packer, this);
        super.createIcons(packer);
    }*/

    public class AnucoinVaultBuild extends Building implements CoinBuild, BankLinked{
        public int anubank = -1;

        @Override
        public int coins(){
            return 0;
        }

        @Override
        public void handleCoin(Building source, int amount){}

        @Override
        public int acceptCoin(Building source, int amount){
            return 0;
        }

        @Override
        public boolean outputCoin(){
            return false;
        }

        @Override
        public boolean occupied(Tile other){
            return getLink() != null && world.build(anubank) != other.build;
        }

        @Override
        public void setLink(Tile other){
            if(other.build == null) return;
            anubank = other.build.pos();
        }

        @Override
        public void removeLink(Tile other){
            if(world.build(anubank) == other.build) anubank = -1;
        }

        public int totalCoins(){
            AnucoinNode.AnucoinNodeBuild bank = getLink();
            if(bank == null) return 0;
            return bank.coins();
        }

        public float fract(){
            AnucoinNode.AnucoinNodeBuild bank = getLink();
            if(bank == null) return 0;
            return Mathf.clamp(bank.coins() / (float)bank.maxCoins());
        }

        public AnucoinNode.AnucoinNodeBuild getLink(){
            if(anubank == -1) return null;
            if(world.build(anubank) instanceof AnucoinNode.AnucoinNodeBuild bank) return bank;
            return null;
        }

        @Override
        public void pickedUp(){
            super.pickedUp();
            anubank = -1;
        }
    }
}
