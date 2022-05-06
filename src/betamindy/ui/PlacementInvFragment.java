package betamindy.ui;

import arc.*;
import arc.graphics.*;
import arc.input.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.util.*;
import betamindy.world.blocks.storage.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.ui.*;
import mindustry.world.*;

import static betamindy.BetaMindy.*;
import static betamindy.util.InventoryModule.*;
import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

public class PlacementInvFragment {
    final int rowWidth = 6;
    public boolean chest = false;

    boolean lastShow = false; //what inventoryUI was before chest was set to true
    private Table vanilla; //the 'toggler' or 'full' of the PlacementFragment.
    Table blockTable, toggler;
    ScrollPane blockPane;
    Runnable rebuildCategory;
    Image sideButton;

    Block menuHoverBlock;
    float vanillaWidth = 314f;
    Team lastTeam = Team.sharded;

    public PlacementInvFragment(){
        Events.on(WorldLoadEvent.class, event -> {
            reset();
            inventoryUI = false;
            Core.app.post(this::rebuild);
        });

        Events.on(UnlockEvent.class, event -> {
            if(event.content instanceof Block){
                rebuild();
            }
        });

        Events.run(Trigger.update, () -> {
            if(Core.input.keyTap(KeyCode.f2)){
                //todo keybind
                toggle();
            }
            if(inventoryUI && state.isPlaying()) updatePlans(player.unit());
        });
    }

    void refreshVanilla(){
        //anuuuuuuuuuuuuuuuuuuuuuuuuke
        Element iTable = ui.hudGroup.find(e -> e.name != null && e.name.equals("inputTable"));
        //Log.info(iTable);
        if(iTable instanceof Table it){
            if(it.parent instanceof Table blocksSelect){
                //Log.info(blocksSelect);

                if((blocksSelect.parent instanceof Table frame) && (frame.parent instanceof Table full)){
                    full.visible(() -> !inventoryUI && ui.hudfrag.shown);
                    vanilla = full;
                    vanillaWidth = frame.getPrefWidth() / Scl.scl(1f); //table's getPrefWidth is scaled by scl. We undo this scl because width() applies the scl again.
                    Log.info("[accent]OH YES[]");
                    Log.info(vanillaWidth);
                }
            }
        }
    }

    void rebuild(){
        Core.app.post(() -> {
            refreshVanilla();
            Group group = toggler.parent;
            int index = toggler.getZIndex();
            toggler.remove();
            build(group);
            toggler.setZIndex(index);
        });
    }

    public void refreshInventory(){
        if(rebuildCategory == null) return;
        rebuildCategory.run();
    }

    public void toggle(){
        if(chest && (control.input.config.getSelected() instanceof Chest.ChestBuild)){
            control.input.config.getSelected().deselect();
            chest = false; //only happens if F2 is pressed
        }
        if(net.active()) inventoryUI = false; //todo remove after figuring the sync out
        else inventoryUI = !inventoryUI;
        control.input.block = null;
        if(sideButton != null) sideButton.setDrawable(inventoryUI ? ui.getIcon(ui.hudfrag.blockfrag.currentCategory.name()): Icon.box);
    }

    public void openChest(){
        if(!chest){
            chest = true;
            lastShow = inventoryUI;
        }
        inventoryUI = true;
    }

    public void storeChest(Block block, int amount){
        if(block == null) return;
        amount = Math.min(amount, teams[player.team().id].amount(block));
        if(amount <= 0) return;
        if(control.input.config.getSelected() instanceof Chest.ChestBuild ch){
            ch.storeChest(block, amount);
        }
    }

    public void build(Group parent){
        loadInventory(state.isMenu()? Team.sharded : player.team());
        refreshVanilla();
        parent.fill(full -> {
            toggler = full;
            full.bottom().right().visible(() -> ui.hudfrag.shown);

            full.table(MindyUILoader.buttonEdge2, side -> {
                side.bottom().defaults().pad(0);
                sideButton = side.image(Icon.box).color(Pal2.inventory).size(35f).touchable(Touchable.enabled).tooltip("@ui.inventory.short").get();
                sideButton.clicked(this::toggle);
                side.row();
                side.image().color(Pal.gray).height(4f).growX().margin(0).pad(0).padTop(4);
            }).visible(() -> inventoryUI || (control.input.block != null && getSize() > 0)).size(65f, 56f).bottom().pad(0).margin(0);

            full.table(frame -> {
                //rebuilds the inventory table with the correct recipes
                rebuildCategory = () -> {
                    blockTable.clear();
                    blockTable.top().margin(5).left();

                    int index = 0;

                    ButtonGroup<ImageButton> group = new ButtonGroup<>();
                    group.setMinCheckCount(0);

                    int n = getSize(true);
                    int team = player.team().id;

                    for(int i = 0; i < n; i++){
                        final int item = i;
                        Block block = teams[team].block(i);
                        int amount = teams[team].amountOf(i);
                        if(block == null || amount == 0) continue;
                        if(index++ % rowWidth == 0){
                            blockTable.row();
                        }

                        Stack sb = new Stack();
                        Table tb = new Table().right().bottom();
                        Table ib = new Table();
                        tb.label(() -> (teams[team] == null || teams[team].amountOf(item) == -1) ? "[lightgray]*[]" : teams[team].amountOf(item) + "").touchable(Touchable.disabled);

                        ImageButton button = ib.button(new TextureRegionDrawable(block.uiIcon), Styles.selecti, () -> {
                            if(unlocked(block)){
                                if(Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(block.name) != 0){
                                    Core.app.setClipboardText((char)Fonts.getUnicode(block.name) + "");
                                    ui.showInfoFade("@copied");
                                }else{
                                    control.input.block = control.input.block == block ? null : block;
                                }
                            }
                        }).size(46f).group(group).name("block-" + block.name).get();
                        button.resizeImage(32f);

                        button.update(() -> { //color unplacable things gray
                            Color color = player.isBuilder() && teams[team] != null && teams[team].amountOf(item) != 0 && unlocked(block) ? Color.white : Color.gray;
                            button.forEach(elem -> elem.setColor(color));
                            button.setChecked(control.input.block == block);
                        });

                        button.hovered(() -> menuHoverBlock = block);
                        button.exited(() -> {
                            if(menuHoverBlock == block){
                                menuHoverBlock = null;
                            }
                        });

                        sb.add(ib);
                        sb.add(tb);
                        blockTable.add(sb).size(46f);
                    }
                    //add missing elements to even out table size
                    if(index < 6){
                        for(int i = 0; i < 6-index; i++){
                            blockTable.add().size(46f);
                        }
                    }
                    blockTable.act(0f);
                    blockPane.setScrollYForce(0);
                    Core.app.post(() -> {
                        blockPane.setScrollYForce(0);
                        blockPane.act(0f);
                        blockPane.layout();
                    });
                };

                //top
                frame.table(MindyUILoader.accentEdge2, top -> {
                    //todo refactor
                    Image image = top.image(Icon.box, Pal2.inventory).size(30f).padRight(9f).padTop(6f).get();
                    image.update(() -> {
                        Block h = menuHoverBlock != null ? menuHoverBlock : control.input.block;
                        if(h != null){
                            image.setDrawable(h.uiIcon);
                            image.setColor(Color.white);
                        }
                        else{
                            image.setDrawable(Icon.box);
                            image.setColor(Pal2.inventory);
                        }
                    });
                    top.labelWrap(() -> menuHoverBlock == null ? (control.input.block == null ? "@ui.inventory.title" : control.input.block.localizedName) : menuHoverBlock.localizedName).growX().height(48f).color(Pal2.inventory);
                }).fillX().height(48f).bottom();

                frame.row();
                frame.image().color(Pal2.inventory).colspan(3).height(4).growX();
                frame.row();

                //inventory
                frame.table(MindyUILoader.pane2, blocksSelect -> {
                    blocksSelect.margin(4).marginTop(0);
                    blockPane = blocksSelect.pane(blocks -> blockTable = blocks).height(194f).update(pane -> {
                        if(pane.hasScroll()){
                            Element result = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
                            if(result == null || !result.isDescendantOf(pane)){
                                Core.scene.setScrollFocus(null);
                            }
                        }
                    }).grow().get();
                    blockPane.setStyle(Styles.smallPane);
                    blocksSelect.row();
                    blocksSelect.table(bottom -> {
                        bottom.table(control.input::buildPlacementUI).name("inputTable2").growX();
                        bottom.image().color(Pal.gray).width(4f).growY();
                        Table backB = new Table(backbutt -> {
                            backbutt.image().color(Pal.gray).height(4f).colspan(2).growX();
                            backbutt.row();
                            var b1 = backbutt.button(Icon.list, Styles.cleari, () -> {
                                //todo
                            }).size(48f).margin(0);
                            var b2 = backbutt.button(Icon.undo, Styles.cleari, () -> {
                                if(inventoryUI){
                                    toggle();
                                }
                            }).size(48f).margin(0).color(Pal2.inventory);
                            if(!mobile){
                                b1.tooltip("@sort");
                                b2.tooltip("@back");
                            }
                        });
                        Table chestB = new Table(backbutt -> {
                            backbutt.image().color(Pal.gray).height(4f).colspan(2).growX();
                            backbutt.row();
                            var b1 = backbutt.button(Icon.up, MindyUILoader.clearAccenti, () -> {
                                storeChest(control.input.block, 1);
                            }).size(48f).margin(0);
                            var b2 = backbutt.button(Icon.upload, MindyUILoader.clearAccenti, () -> {
                                storeChest(control.input.block, InventoryModule.maxAmount);
                            }).size(48f).margin(0);
                            if(!mobile){
                                b1.tooltip("@storeone");
                                b2.tooltip("@storeall");
                            }
                        });
                        backB.visible(() -> (!chest || control.input.block == null));
                        chestB.visible(() -> (chest && control.input.block != null && (control.input.config.getSelected() instanceof Chest.ChestBuild cb && cb.shouldShowChest())));
                        bottom.stack(backB, chestB);
                    }).growX();
                }).fillY().bottom().touchable(Touchable.enabled).width(vanillaWidth);

                rebuildCategory.run();
                frame.update(() -> {
                    if(state.rules.infiniteResources && Core.input.keyTap(Binding.pick) && player.isBuilder()){ //mouse eyedropper select
                        var tile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);
                        Block tryRecipe = tile == null ? null : tile.block();
                        if(tryRecipe != null && tryRecipe != Blocks.air && (uwu || tile.build != null)) InventoryModule.add(tryRecipe, 10, player.team());
                    }
                    if(player.team() != lastTeam){
                        refreshInventory();
                        lastTeam = player.team();
                    }
                    if(chest && !(control.input.config.getSelected() instanceof Chest.ChestBuild)){
                        chest = false;
                        inventoryUI = !lastShow; //toggle fixes the !
                        toggle();
                    }
                });
            }).visible(() -> inventoryUI);
        });
    }

    boolean unlocked(Block block){
        return block.unlockedNow();
    }
}
