package betamindy.ui;

import arc.*;
import arc.graphics.*;
import arc.input.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.util.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.ui.*;
import mindustry.ui.fragments.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static betamindy.util.InventoryModule.*;
import static mindustry.game.EventType.*;
import static betamindy.BetaMindy.inventoryUI;
import static mindustry.Vars.*;

//todo
public class PlacementInvFragment extends Fragment {
    final int rowWidth = 6;
    private Table vanilla; //the 'toggler' or 'full' of the PlacementFragment.
    Table blockTable, toggler;
    ScrollPane blockPane;
    Drawable accentEdge2, pane2;
    Runnable rebuildCategory;
    Block menuHoverBlock;

    public PlacementInvFragment(){
        Events.on(WorldLoadEvent.class, event -> {
            Core.app.post(this::rebuild);
        });

        Events.on(UnlockEvent.class, event -> {
            if(event.content instanceof Block){
                rebuild();
            }
        });

        Events.run(Trigger.update, () -> {
            if(Core.input.keyTap(KeyCode.f2)){
                inventoryUI = !inventoryUI;//todo keybind
                control.input.block = null;//todo mobile button; make sure to do this too
            }
            if(inventoryUI && state.isPlaying()) updatePlans(player.unit());
        });
    }

    void refreshVanilla(){
        //anuuuuuuuuuuuuuuuuuuuuuuuuke
        Element iTable = ui.hudGroup.find(e -> e.name != null && e.name.equals("inputTable"));
        Log.info(iTable);
        if(iTable instanceof Table it){
            if(it.parent instanceof Table blocksSelect){
                Log.info(blocksSelect);

                if((blocksSelect.parent instanceof Table frame) && (frame.parent instanceof Table full)){
                    full.visible(() -> !inventoryUI && ui.hudfrag.shown);
                    vanilla = full;
                    Log.info("[accent]OH YES[]");
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

    @Override
    public void build(Group parent){
        if(accentEdge2 == null || pane2 == null){
            //load nines
            accentEdge2 = Core.atlas.drawable("betamindy-accent-edge-2");
            pane2 = Core.atlas.drawable("pane-2");
        }
        loadInventory();
        refreshVanilla();
        parent.fill(full -> {
            toggler = full;
            full.bottom().right().visible(() -> ui.hudfrag.shown && inventoryUI);

            full.table(frame -> {
                //rebuilds the inventory table with the correct recipes
                rebuildCategory = () -> {
                    blockTable.clear();
                    blockTable.top().margin(5).left();

                    int index = 0;

                    ButtonGroup<ImageButton> group = new ButtonGroup<>();
                    group.setMinCheckCount(0);

                    int n = getSize();
                    for(int i = 0; i < n; i++){
                        final int item = i;
                        Block block = block(i);
                        int amount = amount(i);
                        if(block == null || amount == 0) continue;
                        if(index++ % rowWidth == 0){
                            blockTable.row();
                        }

                        Stack sb = new Stack();
                        Table tb = new Table().right().bottom();
                        Table ib = new Table();
                        tb.label(() -> amount(item) == -1 ? "[lightgray]*[]" : amount(item) + "").touchable(Touchable.disabled);

                        ImageButton button = ib.button(new TextureRegionDrawable(block.icon(Cicon.medium)), Styles.selecti, () -> {
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
                            Color color = player.isBuilder() && amount(item) != 0 && unlocked(block) ? Color.white : Color.gray;
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
                frame.table(accentEdge2, top -> {
                    //todo refactor
                    Image image = top.image(Icon.box, Pal.accent).size(30f).padRight(9f).padTop(6f).get();
                    image.update(() -> {
                        Block h = menuHoverBlock != null ? menuHoverBlock : control.input.block;
                        if(h != null){
                            image.setDrawable(h.icon(Cicon.medium));
                            image.setColor(Color.white);
                        }
                        else{
                            image.setDrawable(Icon.box);
                            image.setColor(Pal.accent);
                        }
                    });
                    top.labelWrap(() -> menuHoverBlock == null ? (control.input.block == null ? "@ui.inventory.title" : control.input.block.localizedName) : menuHoverBlock.localizedName).growX().height(48f).color(Pal.accent);
                }).fillX().height(48f).bottom();

                frame.row();
                frame.image().color(Pal.accent).colspan(3).height(4).growX();
                frame.row();

                //inventory
                frame.table(pane2, blocksSelect -> {
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
                        bottom.table(backbutt -> {
                            backbutt.image().color(Pal.gray).height(4f).colspan(2).growX();
                            backbutt.row();
                            var b1 = backbutt.button(Icon.list, Styles.clearTransi, () -> {
                                //todo
                            }).size(48f).margin(0).disabled(b -> getSize() <= 1);
                            var b2 = backbutt.button(Icon.undo, Styles.clearTransi, () -> {
                                if(inventoryUI) inventoryUI = false;
                            }).size(48f).margin(0).color(Pal.accent);
                            if(!mobile){
                                b1.tooltip("@sort");
                                b2.tooltip("@back");
                            }
                        });
                    }).growX();
                }).fillY().bottom().touchable(Touchable.enabled).width(vanilla.getPrefWidth());

                rebuildCategory.run();
                frame.update(() -> {
                    if(state.rules.infiniteResources && Core.input.keyTap(Binding.pick) && player.isBuilder()){ //mouse eyedropper select
                        var build = world.buildWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);
                        Block tryRecipe = build == null ? null : build.block;
                        if(tryRecipe != null) InventoryModule.add(tryRecipe, 10);
                    }
                });
            });
        });
    }

    boolean unlocked(Block block){
        return block.unlockedNow();
    }
}
