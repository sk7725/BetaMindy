package betamindy.type.shop;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.*;
import betamindy.*;
import betamindy.graphics.*;
import betamindy.world.blocks.storage.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.state;

public class BlockItem extends ShopItem{
    public Block item;

    public BlockItem(Block item, int score){
        super(item.name, score);
        this.item = item;

        localizedName = "[#" + blockColor(item).toString() + "]" + item.localizedName + "[]";
        unlocked = e -> ((Shop.ShopBuild)e).payload == null && (item.unlocked() || state.rules.infiniteResources);
    }
/*
    public BlockItem(Block item){
        this(item, getScore(item));
    }*/
    //todo refuses to work as itemScore is still null

    public static int getScore(Block block){
        float score = 15f;
        if(block.requirements.length == 0) return 30;
        for(ItemStack stack : block.requirements){
            if(!BetaMindy.itemScores.containsKey(stack.item)) continue;
            score += Math.round(BetaMindy.itemScores.get(stack.item)) * stack.amount / 15f;
        }
        return (int)score;
    }

    public static Color blockColor(Block block){
        if(block instanceof Router) return Color.red;
        if(block instanceof StorageBlock) return Pal.engine;
        if(block.requirements.length > 0 && (block instanceof Wall)){
            if(block.requirements.length > 1 && block.requirements[0].item == Items.coal) return block.requirements[1].item.color;
            return block.requirements[0].item.color;
        }
        if(block instanceof PowerDistributor) return Color.yellow;
        if(block instanceof BaseTurret) return Pal.ammo;
        if(block.category == Category.logic) return Pal.sapBullet;
        if(block.hasLiquids) return Liquids.cryofluid.color;
        if(block.hasColor) return block.mapColor;
        return Pal.accent;
    }

    @Override
    public boolean shop(Shop.ShopBuild source){
        if(source.payload == null){
            source.payload = new BuildPayload(item, source.team);
            return true;
        }
        else return false;
    }

    @Override
    public void buildButton(Button t){
        boolean unlocked = item.unlocked() || state.rules.infiniteResources;

        t.left();
        t.image(unlocked ? item.uiIcon : Icon.lock.getRegion()).size(40).padRight(10f).color(unlocked ? Color.white : Pal2.locked);

        if(unlocked){
            t.table(tt -> {
                tt.left();

                Label text = new Label(localizedName);
                text.setWrap(true);

                tt.add(text).growX().left();
                tt.row();
                tt.add(Core.bundle.get("ui.price") + ": " + Core.bundle.format("ui.anucoin.emoji", cost)).left();
            }).growX();
        }
    }
}
