package betamindy.world.blocks.storage;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.KeyCode;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.type.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.graphics.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.production.*;

import static arc.Core.atlas;
import static mindustry.Vars.mobile;

public class Shop extends PayloadAcceptor {
    public int defaultAnucoins = 500;
    public TextureRegion coinRegion, spinRegion;
    public TextureRegion[] spinTeamRegions;
    public float spinSpeed = 0.2f;
    public float spinShadowRadius = 15f;

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
        acceptsItems = true;
        unloadable = false;
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
        Drawm.customTeamRegion(packer, name + "-spin");
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

        coinRegion = atlas.find("betamindy-anucoin");
        spinRegion = atlas.find(name + "-spin");
        spinTeamRegions = Drawm.loadCustomTeamRegion(name + "-spin");
    }

    @Override
    public TextureRegion[] icons(){
        return teamRegion.found() ? new TextureRegion[]{region, topRegion, teamRegions[Team.sharded.id], spinTeamRegions[Team.sharded.id], spinRegion} : new TextureRegion[]{region, topRegion};
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(region, req.drawx(), req.drawy());
        Draw.rect(outRegion, req.drawx(), req.drawy(), req.rotation * 90);
        Draw.rect(topRegion, req.drawx(), req.drawy());

        if(req.worldContext && Vars.player != null && teamRegion != null && teamRegion.found()){
            int tid = Vars.player.team().id;
            if(teamRegions[tid] == teamRegion) Draw.color(Vars.player.team().color);
            Draw.rect(teamRegions[tid], req.drawx(), req.drawy());
            Draw.rect(spinTeamRegions[tid], req.drawx(), req.drawy());
            Draw.rect(spinRegion, req.drawx(), req.drawy());
            Draw.color();
        }
        else if(teamRegion != null && teamRegion.found()){
            Draw.rect(teamRegions[Team.sharded.id], req.drawx(), req.drawy());
            Draw.rect(spinTeamRegions[Team.sharded.id], req.drawx(), req.drawy());
            Draw.rect(spinRegion, req.drawx(), req.drawy());
        }
    }

    public class ShopBuild extends PayloadAcceptor.PayloadAcceptorBuild<Payload> implements CoinBuild{
        public int anucoins = defaultAnucoins;
        public Cell<Label> anucoinString;
        float buttonWidth = 210f;
        public UnitType unit;
        public float scl;

        String airColor = colorToHex(StatusEffects.shocked.color);
        String groundColor = colorToHex(StatusEffects.melting.color);
        String navalColor = colorToHex(StatusEffects.wet.color);

        @Override
        public int coins(){
            return anucoins;
        }

        @Override
        public void handleCoin(Building source, int amount){
            anucoins += amount; //debts can be a thing here
        }

        @Override
        public int acceptCoin(Building source, int amount){
            return amount;
        }

        @Override
        public boolean outputCoin(){
            return anucoins > 0;
        }

        public void updateAnucoins(){
            anucoinString.setElement(new Label(String.valueOf(anucoins)));
        }

        public boolean addItemPayload(Item item, int amount){
            if(payload == null){
                payload = new BuildPayload(MindyBlocks.box, team);
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
                t.left();

                t.add(Core.bundle.has("shopItem." + shopItem.name + ".name") ? Core.bundle.get("shopItem." + shopItem.name + ".name") : shopItem.name).growX().left();

                if(Core.bundle.has("shopItem." + shopItem.name + ".description")){
                    t.row();

                    t.add(Core.bundle.get("shopItem." + shopItem.name + ".description")).growX().left().color(Color.gray);
                }

                if(shopItem.type == 0) {
                    for(ItemStack stack : shopItem.packageItems){
                        t.row();

                        t.table(tt -> {
                            tt.left();
                            tt.image(stack.item.icon(Cicon.small)).left();
                            tt.add(String.valueOf(stack.amount)).left();
                        }).left();
                    }
                }

                t.row();

                t.add(Core.bundle.get("ui.price") + ": " + price + " [accent]" + (price == 1 ? Core.bundle.get("ui.anucoin.single") : Core.bundle.get("ui.anucoin.multiple")) + "[]").left();
            }, () -> {
                if(anucoins >= price) {
                    if(shopItem.type == 0) {
                        boolean success = true;

                        for(ItemStack stack : shopItem.packageItems){
                            if(!addItemPayload(stack.item, stack.amount)) success = false;
                        }

                        if(success) {
                            anucoins -= price;
                            updateAnucoins();
                        }
                    } else if(shopItem.type == 1){
                        shopItem.purchased.get(this);
                        shopDialog.hide();

                        anucoins -= price;
                        updateAnucoins();
                    }
                }
            }).left().growX().disabled(!shopItem.unlocked.get(this));
            pane.row();
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return false;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return itemScores.containsKey(item);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            moveOutPayload();
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            drawPayload();
            Draw.z(Layer.blockOver + 0.001f);
            Draw.rect(topRegion, x, y);
            drawTeamTop();
            Draw.reset();
        }

        @Override
        public void drawTeamTop(){
            if(block.teamRegion.found()){
                float r = Time.time * spinSpeed + id * 17f;

                Draw.z(Layer.blockOver + 0.0011f);
                if(block.teamRegions[team.id] == block.teamRegion) Draw.color(team.color);
                Draw.rect(block.teamRegions[team.id], x, y);
                Draw.z(Layer.blockOver + 0.002f);
                Drawm.spinSprite(spinTeamRegions[team.id], x, y, r);

                Draw.color();
                Draw.rect(spinRegion, x, y, r);
                Draw.z(Layer.blockOver + 0.0015f);
                Drawf.shadow(x, y, spinShadowRadius);
            }

            carried = false; //why, Anuke?
        }

        @Override
        public boolean shouldHideConfigure(Player player) {
            return true;
        }

        @Override
        public void drawConfigure() {}

        public void buildSellDialog(int[] price, Seq<ItemStack> items, Runnable confirmed){
            String text1 = Core.bundle.get("ui.sellAccept") + ":";
            String text2 = Core.bundle.get("ui.sellAccept2") + " [accent]" + price[0] + " " + Core.bundle.get("ui.anucoin.multiple") + "[]";
            BaseDialog dialog = new BaseDialog(Core.bundle.get("ui.shop.title"));
            dialog.cont.add(text1).width(mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
            dialog.cont.row();
            dialog.cont.pane(p -> {
                for(ItemStack stack : items){
                    p.left();
                    p.table(t -> {
                        t.left();
                        t.table(tt -> {
                            tt.left();
                            tt.image(stack.item.icon(Cicon.medium)).left();
                            tt.add("x" + stack.amount).left();
                        }).growX().left();

                        t.add(" [accent]" + (int)Math.max((itemScores.get(stack.item) * stack.amount) / 30f, Math.max(stack.amount / 2f, 1)) + "[]").padRight(5f).left();

                        t.image(coinRegion).left();
                    }).left().growX();
                    p.row();
                }
            }).height(mobile ? 200f : 250f).width(mobile ? 400f : 500f);
            dialog.cont.row();
            dialog.cont.add(text2).width(mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
            dialog.buttons.defaults().size(200f, 54f).pad(2f);
            dialog.setFillParent(false);
            dialog.buttons.button("@cancel", dialog::hide);
            dialog.buttons.button("@ok", () -> {
                dialog.hide();
                confirmed.run();
            });
            dialog.keyDown(KeyCode.enter, () -> {
                dialog.hide();
                confirmed.run();
            });
            dialog.keyDown(KeyCode.escape, dialog::hide);
            dialog.keyDown(KeyCode.back, dialog::hide);
            dialog.show();
        }

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
                t.image(coinRegion).size(30f).center().padRight(10f);
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
                t.button(Core.bundle.get("ui.sell"), Icon.add, () -> {
                    int[] price = new int[]{0};
                    Seq<ItemStack> itemStack = new Seq<>();

                    items.each((Item ii, int aa) -> {
                        if(!itemScores.containsKey(ii)) return;

                        price[0] += (int)Math.max((itemScores.get(ii) * aa) / 30f, Math.max(aa / 2f, 1));

                        itemStack.add(new ItemStack().set(ii, aa));
                    });

                    buildSellDialog(price, itemStack, () -> {
                        for(ItemStack stack : itemStack){
                            items.remove(stack.item, stack.amount);
                        }

                        anucoins += price[0];
                        updateAnucoins();
                    });
                }).size(buttonWidth, 64f).disabled(e -> items.total() == 0);
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
