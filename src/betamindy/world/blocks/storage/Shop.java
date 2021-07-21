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

public class Shop extends Block{
    public int defaultAnucoins = 500;
    public TextureRegion anucoin;

    OrderedMap<Item, Float> itemScores;
    OrderedMap<UnitType, Float> unitScores;
    OrderedMap<UnitType, Integer> unitTypeMap = new OrderedMap<>();
    BaseDialog shopDialog;

    public Shop(String name){
        super(name);

        update = solid = true;

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
                    tt.add("[#" + color + "]" + item.localizedName + "[] [accent]x20[]").growX().left();
                    tt.row();
                    tt.add(Core.bundle.get("ui.price") + ": " + price + " [accent]" + (price == 1 ? Core.bundle.get("ui.anucoin.single") : Core.bundle.get("ui.anucoin.multiple")) + "[]").left();
                }).growX();
            }, () -> {
                if(anucoins >= price){
                    anucoins -= price;
                    updateAnucoins();
                }
            }).left().growX();
            pane.row();
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

                    tbl1.pane(e -> {
                        for (Item item : Vars.content.items()) {
                            if(item == MindyItems.bittrium) continue;
                            if (itemScores.containsKey(item)) {
                                itemButton(e, item);
                            }
                        }
                    }).center().width(Core.graphics.getWidth() * (Vars.mobile ? 0.8f : 0.25f));
                }).padRight((Vars.mobile ? 0f : 60f);
                if(!Vars.mobile){
                    tbl.table(tbl1 -> {
                       tbl1.center();

                       tbl1.add(Core.bundle.get("ui.units"));
                       tbl1.row();

                       tbl1.pane(e -> {
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
                        }).center().width(Core.graphics.getWidth() / 4f);
                    });
                }
            });
            shopDialog.row();
            shopDialog.table(t -> {
                float width = 210f;
                if(Vars.mobile) width = 60f;
                t.button("@back", Icon.left, shopDialog::hide).size(210f, 64f);
                t.button(Core.bundle.get("ui.cart"), Icon.list, () -> {}).size(210f, 64f);
                t.button(Core.bundle.get("ui.sell"), Icon.add, () -> {}).size(210f, 64f);
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
