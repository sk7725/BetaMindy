package betamindy.world.blocks.storage;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import betamindy.BetaMindy;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Cicon;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.ModsDialog;
import mindustry.world.*;

public class Shop extends Block{
    public int defaultAnucoins = 0;
    OrderedMap<Item, Float> itemScores;
    OrderedMap<UnitType, Float> unitScores;
    BaseDialog shopDialog;

    public Shop(String name){
        super(name);

        update = solid = true;

        configurable = true;
    }

    @Override
    public void load() {
        super.load();

        itemScores = BetaMindy.itemScores;
        unitScores = BetaMindy.unitScores;
    }

    public class ShopBuild extends Building{
        public float anucoins = defaultAnucoins;

        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);

            shopDialog = new BaseDialog(Core.bundle.get("ui.shop.title"));
            shopDialog.center();

            shopDialog.row();
            shopDialog.table(tbl -> {
                tbl.pane(e -> {
                    for (Item item : Vars.content.items()) {
                        if (itemScores.containsKey(item)) {
                            e.button(t -> {
                                t.left();
                                int price = Math.max(Math.round(itemScores.get(item) / 2f), 10);

                                t.table(tt -> {
                                    tt.left();
                                    tt.image(new TextureRegion(item.icon(Cicon.medium))).size(40).padRight(20f);
                                    tt.add(item.localizedName).color(item.color);
                                }).growX();

                                t.row();

                                t.table(tt -> {
                                    tt.left();
                                    tt.add(price + " " + (price == 1 ? Core.bundle.get("ui.anucoin.single") : Core.bundle.get("ui.anucoin.multiple"))).left();
                                }).growX();
                            }, () -> {
                            }).left().growX();
                            e.row();
                        }
                    }
                }).center().width(Core.graphics.getWidth() / 4f);

                tbl.pane(e -> {
                    for (UnitType unit : Vars.content.units()) {
                        if (unitScores.containsKey(unit)) {
                            e.button(t -> {
                                t.left();
                                int price = Math.max(Math.round(unitScores.get(unit)), 10);

                                t.table(tt -> {
                                    tt.left();
                                    tt.image(new TextureRegion(unit.icon(Cicon.medium))).size(40).padRight(20f);
                                    tt.add(unit.localizedName);
                                }).growX();

                                t.row();

                                t.table(tt -> {
                                    tt.left();
                                    tt.add(price + " " + (price == 1 ? Core.bundle.get("ui.anucoin.single") : Core.bundle.get("ui.anucoin.multiple"))).left();
                                }).growX();
                            }, () -> {
                            }).left().growX();
                            e.row();
                        }
                    }
                }).center().width(Core.graphics.getWidth() / 4f);
            });

            shopDialog.addCloseListener();
            table.button(Icon.file, shopDialog::show);
        }
    }
}
