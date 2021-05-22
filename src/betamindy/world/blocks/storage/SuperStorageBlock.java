package betamindy.world.blocks.storage;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import betamindy.graphics.*;
import betamindy.world.blocks.production.payduction.craft.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static mindustry.type.ItemStack.with;

public class SuperStorageBlock extends StorageBlock {
    public Block before;
    public float bonusChance = 0.1f;
    public TextureRegion iconRegion, armRegion, boostIcon;

    public SuperStorageBlock(String name, Block before){
        super(name);
        this.before = before;

        size = before.size;
        itemCapacity = before.itemCapacity;

        sync = true;
        buildVisibility = BuildVisibility.sandboxOnly;
    }

    @Override
    public void load(){
        super.load();
        iconRegion = Drawm.getTeamRegion(this);
        armRegion = atlas.find(name + "-arm");
        boostIcon = atlas.find(name + "-boost");
    }

    @Override
    public void createIcons(MultiPacker packer){
        Drawm.generateTeamRegion(packer, this);
        super.createIcons(packer);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, iconRegion};
    }

    public class SuperStorageBuild extends StorageBuild implements CraftReact{
        //TODO prevent item intake & outtake
        @Override
        public void craft(ItemStack[] in, ItemStack out){
            int amount = out.amount + (Mathf.chance(bonusChance) ? 1 : 0);
            int a = acceptStack(out.item, amount, self());
            if(a < amount) return;

            items.remove(in);
            items.add(out.item, amount);
        }

        @Override
        public void displayReact(Table t){
            t.image(boostIcon).size(18f);
            t.add("[#94fff6]" + (int)(bonusChance * 100) + "%[]").height(18f);
        }

        /*public BuildPayload openPayload(){
            //TODO
        }*/
    }
}
