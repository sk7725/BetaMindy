package betamindy.world.blocks.storage;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.StatusEffects;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import java.util.concurrent.atomic.*;

public class Shop extends Block{
    public int defaultAnucoins = 500;
    public TextureRegion anucoin;

    OrderedMap<Item, Float> itemScores;
    OrderedMap<UnitType, Float> unitScores;
    OrderedMap<UnitType, Integer> unitTypeMap = new OrderedMap<>();
    BaseDialog shopDialog;

    public Shop(String name){
        super(name);

        update = solid = hasItems = outputsItems = true;

        configurable = true;
    }

    public int checkUnitType(UnitType unit){
        if(unit.flying) return 1;

        Unit u = unit.constructor.get();

        boolean water = u instanceof WaterMovec;

        return water ? 3 : 2;
    }

    public String colorToHex(Color color){
        return Integer.toHexString((int)(color.r * 255f)) + Integer.toHexString((int)(color.g * 255f)) + Integer.toHexString((int)(color.b * 255));
    }

    @Override
    public void init(){
        super.init();

        Runnable ee = () -> {
            itemScores = BetaMindy.itemScores;
            unitScores = BetaMindy.unitScores;

            for (UnitType unit : Vars.content.units()) {
                if (unitScores.containsKey(unit)) {
                    unitTypeMap.put(unit, checkUnitType(unit));
                }
            }
        };

        Events.on(EventType.ClientLoadEvent.class, e -> ee.run());

        Events.on(EventType.ServerLoadEvent.class, e -> ee.run());
    }

    @Override
    public void load() {
        super.load();

        anucoin = Core.atlas.find("betamindy-anucoin");
    }

    public class ShopBuild extends Building{
        public int anucoins = defaultAnucoins;
        public Cell<Label> anucoinString;
        float buttonWidth = 210f;
        boolean mobileUI = false;
        AtomicReference<Cell<ScrollPane>> scpane = new AtomicReference<Cell<ScrollPane>>();
        
        public void updateAnucoins(){
            anucoinString.setElement(new Label(String.valueOf(anucoins)));
        }

        public void itemButton(Table pane, Item item){
            int price = Math.max(Math.round(itemScores.get(item)), 15);
            
            pane.button(t -> {
                t.left();
                t.image(new TextureRegion(item.icon(Cicon.medium))).size(40).padRight(10f);

                t.table(tt -> {
                    tt.left();
                    String color = colorToHex(item.color);
                    tt.add("[#" + color + "]" + item.localizedName + "[] [accent]x15[]").growX().left();
                    tt.row();
                    tt.add(Core.bundle.get("ui.price") + ": " + price + " [accent]" + (price == 1 ? Core.bundle.get("ui.anucoin.single") : Core.bundle.get("ui.anucoin.multiple")) + "[]").left();
                }).growX();
            }, () -> {
                if(anucoins >= price){
                    anucoins -= price;
                    updateAnucoins();
                    items.add(item, 15);
                }
            }).left().growX();
            pane.row();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return false;
        }

        @Override
        public boolean shouldHideConfigure(Player player) {
            return true;
        }

        @Override
        public void drawConfigure() {}

        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);

            String airColor = colorToHex(StatusEffects.shocked.color);
            String groundColor = colorToHex(StatusEffects.melting.color);
            String navalColor = colorToHex(StatusEffects.wet.color);
            
            float width = Math.min(Core.graphics.getWidth(), Core.graphics.getHeight());
            float height = Math.max(Core.graphics.getWidth(), Core.graphics.getHeight());
            mobileUI = Vars.mobile;

            shopDialog = new BaseDialog(Core.bundle.get("ui.shop.title"));
            shopDialog.center();

            shopDialog.row();

            shopDialog.table(t -> {
                t.center();
                t.image(anucoin).size(30f).center().padRight(10f);
                anucoinString = t.add(String.valueOf(anucoins)).padRight(10f).center();
            });

            shopDialog.row();
            shopDialog.table(tbl -> {
                tbl.table(tbl1 -> {
                    tbl1.center();

                    tbl1.add(Core.bundle.get("ui.items"));
                    tbl1.row();

                    scpane.set(tbl1.pane(e -> {
                        for (Item item : Vars.content.items()) {
                            if(item == MindyItems.bittrium) continue;
                            if (itemScores.containsKey(item)) {
                                itemButton(e, item);
                            }
                        }
                    }).center().width(width * (mobileUI ? 0.55f : 0.25f)));
                    if(mobileUI) scpane.get().height(height / 2f * 0.55f);
                }).padRight((mobileUI ? 0f : 60f));
                
                if(mobileUI) tbl.row();
                tbl.table(tbl1 -> {
                    tbl1.center();

                    tbl1.add(Core.bundle.get("ui.units"));
                    tbl1.row();

                    scpane.set(tbl1.pane(e -> {
                        for (UnitType unit : Vars.content.units()) {
                            if (unitScores.containsKey(unit)) {
                                e.button(t -> {
                                    t.left();
                                    int price = Math.max(Math.round(unitScores.get(unit)), 15);

                                    t.image(new TextureRegion(unit.icon(Cicon.medium))).size(40).padRight(10f);

                                    t.table(tt -> {
                                        int type = unitTypeMap.get(unit);
                                        tt.left();

                                        tt.add(unit.localizedName).growX().left();
                                        tt.row();

                                        tt.add("[accent]" + Core.bundle.get("ui.type") + "[]: " + (type == 1 ? "[#" + airColor + "]" + Core.bundle.get("ui.air") : (type == 2 ? "[#" + groundColor + "]" + Core.bundle.get("ui.ground") : "[#" + navalColor + "]" + Core.bundle.get("ui.naval")))).left();
                                        tt.row();

                                        tt.add(Core.bundle.get("ui.price") + ": " + price + " [accent]" + (price == 1 ? Core.bundle.get("ui.anucoin.single") : Core.bundle.get("ui.anucoin.multiple")) + "[]").left();
                                    }).growX();
                                }, () -> {}).left().growX();
                                e.row();
                            }
                        }
                    }).center().width(width * (mobileUI ? 0.55f : 0.25f)));
                    if(mobileUI) scpane.get().height(height / 2f * 0.55f);
                });
            });
            shopDialog.row();
            shopDialog.table(t -> {
                if(mobileUI){
                    buttonWidth = (width / 2f) * 0.55f;
                }
                t.button("@back", Icon.left, shopDialog::hide).size(buttonWidth, 64f);
                t.button(Core.bundle.get("ui.sell"), Icon.add, () -> {}).size(buttonWidth, 64f);
            });

            shopDialog.addCloseListener();
            shopDialog.show();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(anucoins);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            anucoins = read.i();
        }
    }
}
