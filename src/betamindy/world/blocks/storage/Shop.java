package betamindy.world.blocks.storage;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.type.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.production.*;

public class Shop extends PayloadAcceptor {
    public int defaultAnucoins = 500;
    public TextureRegion anucoin;
    /** 0 = item, 1 = unit, 3 = extra */
    public int shopType = 0;

    OrderedMap<Item, Float> itemScores;
    OrderedMap<UnitType, Float> unitScores;
    OrderedMap<String, ShopItem> shopItems;
    OrderedMap<UnitType, Integer> unitTypeMap = new OrderedMap<>();
    BaseDialog shopDialog;

    public Shop(String name){
        super(name);

        update = solid = hasItems = outputsPayload = sync = rotate = configurable = true;
        acceptsItems = false;

        itemCapacity = 0;
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
    public void createIcons(MultiPacker packer){
        Drawm.generateTeamRegion(packer, this);
        super.createIcons(packer);
    }

    @Override
    public void init(){
        super.init();

        Runnable ee = () -> {
            itemScores = BetaMindy.itemScores;
            unitScores = BetaMindy.unitScores;
            shopItems = BetaMindy.shopItems;

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

    public class ShopBuild extends PayloadAcceptor.PayloadAcceptorBuild<Payload>{
        public int anucoins = defaultAnucoins;
        public Cell<Label> anucoinString;
        float buttonWidth = 210f;
        public UnitType unit;
        public float scl;

        String airColor = colorToHex(StatusEffects.shocked.color);
        String groundColor = colorToHex(StatusEffects.melting.color);
        String navalColor = colorToHex(StatusEffects.wet.color);
        
        public void updateAnucoins(){
            anucoinString.setElement(new Label(String.valueOf(anucoins)));
        }

        public boolean addItemPayload(Item item, int amount){
            if(payload == null){
                payload = new BuildPayload(Blocks.container, team);
            }

            if(payload instanceof BuildPayload){
                ((BuildPayload)payload).build.items.add(item, amount);
                return true;
            }

            return false;
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
                    if(addItemPayload(item, 15)){
                        anucoins -= price;
                        updateAnucoins();

                        payVector.setZero();
                        payRotation = rotdeg();
                    }
                }
            }).left().growX();
            pane.row();
        }

        public void unitButton(Table pane, UnitType unit){
            int price = Math.max(Math.round(unitScores.get(unit)), 15);

            pane.button(t -> {
                t.left();

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
            }, () -> {
                if(anucoins >= price && payload == null) {
                    anucoins -= price;
                    updateAnucoins();
                    payload = new UnitPayload(unit.create(team));

                    payVector.setZero();
                    payRotation = rotdeg();
                }
            }).left().growX();
            pane.row();
        }

        public void extraButton(Table pane, ShopItem shopItem){
            int price = shopItem.cost;

            pane.button(t -> {
                int type = shopItem.type;

                t.left();

                t.add(shopItem.name).growX().left();
                t.row();

                t.add(Core.bundle.get("ui.price") + ": " + price + " [accent]" + (price == 1 ? Core.bundle.get("ui.anucoin.single") : Core.bundle.get("ui.anucoin.multiple")) + "[]").left();
            }, () -> {
                if(anucoins >= price) {
                    if(shopItem.type == 0) {
                        boolean success = true;
                        for(ItemStack stack : shopItem.bundleItems){
                            if(!addItemPayload(stack.item, stack.amount)) success = false;
                        }

                        if(success) {
                            anucoins -= price;
                            updateAnucoins();
                        }
                    } else if(shopItem.type == 1){
                        shopItem.runnable.get(this);

                        anucoins -= price;
                        updateAnucoins();
                    }
                }
            }).left().growX().disabled(!shopItem.unlocked.get(this));
            pane.row();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return false;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            moveOutPayload();
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
            
            float width = Math.min(Core.graphics.getWidth(), Core.graphics.getHeight());
            float height = Math.max(Core.graphics.getWidth(), Core.graphics.getHeight());

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
                if(shopType == 0) {
                    tbl.table(tbl1 -> {
                        tbl1.center();

                        tbl1.add(Core.bundle.get("ui.items"));
                        tbl1.row();

                        tbl1.pane(e -> {
                            for (Item item : Vars.content.items()) {
                                if (item == MindyItems.bittrium) continue;
                                if (itemScores.containsKey(item)) {
                                    itemButton(e, item);
                                }
                            }
                        }).center().width(width * 0.6f);
                    });
                } else if(shopType == 1) {
                    tbl.table(tbl1 -> {
                        tbl1.center();

                        tbl1.add(Core.bundle.get("ui.units"));
                        tbl1.row();

                        tbl1.pane(e -> {
                            for (UnitType unit : Vars.content.units()) {
                                if (unitScores.containsKey(unit)) {
                                    unitButton(e, unit);
                                }
                            }
                        }).center().width(width * 0.6f);
                    });
                } else if(shopType == 2) {
                    tbl.table(tbl1 -> {
                        tbl1.center();

                        tbl1.add(Core.bundle.get("ui.extra"));
                        tbl1.row();

                        tbl1.pane(e -> {
                            for (ShopItem shopItem : BetaMindy.shopItems.values()) {
                                extraButton(e, shopItem);
                            }
                        }).center().width(width * 0.6f);
                    });
                }
            });
            shopDialog.row();
            shopDialog.table(t -> {
                if(Vars.mobile){
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
