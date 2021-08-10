package betamindy.world.blocks.storage;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.input.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.ui.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;

import static mindustry.Vars.*;
import static betamindy.BetaMindy.*;

public class TradingPost extends Block {

    public TradingPost(String name){
        super(name);

        update = solid = true;
        configurable = true;
        hasItems = true;
    }

    @Override
    public void setBars() {
        super.setBars();

        bars.add("anucoins", (TradingPostBuild entity) -> new Bar(
                () -> Core.bundle.format("bar.anucoin", entity.anucoins),
                () -> Color.coral,
                () -> Mathf.clamp(entity.anucoins / 500f)));
    }

    @Override
    public void load() {
        super.load();
    }

    public class TradingPostBuild extends Building implements CoinBuild{
        public int anucoins;

        @Override
        public int coins(){
            return anucoins;
        }

        @Override
        public int acceptCoin(Building source, int amount){
            return 0;
        }

        @Override
        public void handleCoin(Building source, int amount){
            anucoins += amount;
        }

        @Override
        public boolean outputCoin(){
            return anucoins > 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return itemScores.containsKey(item);
        }

        @Override
        public boolean shouldHideConfigure(Player player) {
            return true;
        }

        @Override
        public void drawConfigure(){ }

        @Override
        public void buildConfiguration(Table table) {
            int[] price = new int[]{0};
            Seq<ItemStack> itemStack = new Seq<>();

            items.each((Item ii, int aa) -> {
                if(!itemScores.containsKey(ii)) return;

                price[0] += (int)Math.max((itemScores.get(ii) * aa) / 30f, Math.max(aa / 2f, 1));

                itemStack.add(new ItemStack().set(ii, aa));
            });

            Runnable confirmed = () -> {
                for (ItemStack stack : itemStack) {
                    items.remove(stack.item, stack.amount);
                }

                anucoins += price[0];
            };

            String text1 = Core.bundle.get("ui.sellAccept") + ":";
            String text2 = Core.bundle.get("ui.sellAccept2") + " [accent]" + price[0] + " " + Core.bundle.get("ui.anucoin.multiple") + "[]";

            BaseDialog dialog = new BaseDialog(Core.bundle.get("ui.trading.title"));
            dialog.cont.add(text1).width(mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
            dialog.cont.row();
            dialog.cont.pane(p -> {
                for(ItemStack stack : itemStack){
                    p.left();
                    p.table(t -> {
                        t.left();
                        t.table(tt -> {
                            tt.left();
                            tt.image(stack.item.icon(Cicon.medium)).left();
                            tt.add("x" + stack.amount).left();
                        }).growX().left();

                        t.add(" [accent]" + (int)Math.max((itemScores.get(stack.item) * stack.amount) / 30f, Math.max(stack.amount / 2f, 1)) + "[]").padRight(5f).left();

                        t.image(AnucoinTex.uiCoin).left();
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
