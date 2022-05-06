package betamindy.world.blocks.storage;

import arc.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.ui.*;
import betamindy.util.*;
import betamindy.util.InventoryModule.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

/** Stores inventory. */
public class Chest extends Block {
    public final int rowWidth = 5;
    /** Max amount of different stacks this chest can hold. */
    public int slots = 10;
    /** Max amount of blocks per stack. Must be under 100. */
    public int capacity = 25;
    public boolean canStore = true;
    public boolean canTake = true;

    private final IntSeq tmpi = new IntSeq(3);
    Runnable rebuildTable;

    public Chest(String name){
        super(name);
        solid = true;
        update = true;
        configurable = true;
        //while sync = true would make sense, the packet of this bad boy is Very Big so...
        flags = EnumSet.of(BlockFlag.storage);

        config(IntSeq.class, (ChestBuild build, IntSeq i) -> {
            if(i.size == 3 && i.get(0) >= 0 && i.get(0) < Team.all.length){
                //team id, block id, amount: + if storing, - if taking
                Team team = Team.get(i.get(0));
                int id = i.get(1);
                Block block = Vars.content.getByID(ContentType.block, id);
                int amount = i.get(2);
                if(block == null) return;
                if(amount > 0 && InventoryModule.teams[team.id] != null){
                    if(!build.shouldShowChest()) return;
                    //store
                    amount = Math.min(amount, InventoryModule.teams[team.id].amount(block)); //cap max amount to existing items
                    amount = Math.min(amount, capacity - build.inventory.amount(block)); //cap max amount to available space
                    if(amount > 0 && InventoryModule.add(block, -amount, team)){
                        build.inventory.add(block, amount);
                    }
                }
                else if(amount < 0){
                    if(!canTake) return;
                    //take
                    amount = -amount;
                    if(InventoryModule.teams[team.id] == null) InventoryModule.loadInventory(team);
                    amount = Math.min(build.inventory.amount(block), amount);
                    amount = Math.min(amount, InventoryModule.maxAmount - InventoryModule.teams[team.id].amount(block));
                    if(amount > 0 && build.inventory.add(block, -amount)){
                        InventoryModule.add(block, amount, team);
                    }
                }

                if(rebuildTable != null && control.input.config.getSelected() == build.self()){
                    rebuildTable.run();
                }
            }
        });
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.itemCapacity, slots * capacity, StatUnit.blocks);
    }

    public class ChestBuild extends Building {
        public Inventory inventory = new Inventory(null);
        public @Nullable Block selected = null; //current selected block

        public void storeChest(Block block, int amount){
            tmpi.clear();
            tmpi.add(team.id, block.id, amount);
            configure(tmpi);
        }

        public void takeChest(Block block, int amount){
            tmpi.clear();
            tmpi.add(team.id, block.id, -amount);
            configure(tmpi);
        }

        public boolean shouldShowChest(){
            return canStore;
        }

        @Override
        public void buildConfiguration(Table table){
            rebuildTable = () -> buildConfiguration(table);
            if(selected != null && inventory.amount(selected) == 0){
                selected = null;
            }
            table.clearChildren();
            Core.app.post(() -> {
                BetaMindy.mui.invfrag.openChest();
            });
            table.table(Tex.pane, t -> {
                ScrollPane blockPane = t.pane(blockTable -> {
                    blockTable.top().left();
                    int index = 0;

                    ButtonGroup<ImageButton> group = new ButtonGroup<>();
                    group.setMinCheckCount(0);

                    int n = inventory.getSize();

                    for(int i = 0; i < n; i++){
                        final int item = i;
                        Block block = inventory.block(i);
                        int amount = inventory.amountOf(i);
                        if(block == null || amount == 0) continue;
                        if(index++ % rowWidth == 0){
                            blockTable.row();
                        }

                        Stack sb = new Stack();
                        Table tb = new Table().right().bottom();
                        Table ib = new Table();
                        tb.label(() -> (inventory.amountOf(item) == -1) ? "[lightgray]*[]" : inventory.amountOf(item) + "").touchable(Touchable.disabled);

                        ImageButton button = ib.button(new TextureRegionDrawable(block.uiIcon), Styles.selecti, () -> {
                            selected = selected == block ? null : block;
                        }).size(46f).group(group).name("block-" + block.name).get();
                        button.resizeImage(32f);

                        button.update(() -> {
                            button.setChecked(selected == block);
                        });

                        sb.add(ib);
                        sb.add(tb);
                        blockTable.add(sb).size(46f);
                    }
                    //add missing elements to even out table size
                    if(index < rowWidth){
                        for(int i = 0; i < rowWidth-index; i++){
                            blockTable.add().size(46f);
                        }
                    }
                    blockTable.act(0f);
                }).update(pane -> {
                    if(pane.hasScroll()){
                        Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                        if(result == null || !result.isDescendantOf(pane)){
                            Core.scene.setScrollFocus(null);
                        }
                    }
                }).grow().get();
                blockPane.setScrollYForce(0);
                Core.app.post(() -> {
                    blockPane.setScrollYForce(0);
                    blockPane.act(0f);
                    blockPane.layout();
                });

                if(canTake){
                    t.table(side -> {
                        var b1 = side.button(Icon.down, MindyUILoader.clearAccenti, () -> {
                            takeChest(selected, 1);
                        }).disabled(butt -> selected == null).size(48f);
                        side.row();
                        var b2 = side.button(Icon.download, MindyUILoader.clearAccenti, () -> {
                            takeChest(selected, capacity);
                        }).disabled(butt -> selected == null).size(48f);
                        if(!mobile){
                            b1.tooltip("@takeone");
                            b2.tooltip("@takeall");
                        }
                    }).width(50f).growY();
                }
            });
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.str(inventory.compressed());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            Inventory.uncompress(inventory, read.str());
        }
    }
}
