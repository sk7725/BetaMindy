package betamindy.world.blocks.storage;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.graphics.*;
import betamindy.ui.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class AnucoinNode extends Block {
    /** surprisingly, this block's range is a square, not a circle */
    public int range = 15;
    public TextureRegion laser, laserEnd;
    public int initialMaxCoins = 100000;
    public int autoTransaction = timers++;
    public float transactionInterval = 60f;

    BaseDialog bankDialog = null;

    private final Seq<Building> tmpe = new Seq<>();

    public AnucoinNode(String name){
        super(name);

        configurable = solid = update = sync = true;
        config(Integer.class, (AnucoinNodeBuild entity, Integer value) -> {
            Building other = world.build(value);
            boolean contains = entity.links.contains(value);

            if(contains){
                //unlink
                entity.links.removeValue(value);
                if(other instanceof BankLinked bl){
                    bl.removeLink(entity.tile);
                }
            }else if(linkValid(entity, other)){
                if(!entity.links.contains(other.pos())){
                    if(other instanceof BankLinked bl){
                        if(bl.occupied(entity.tile)) return;
                        bl.setLink(entity.tile);
                    }
                    entity.links.add(other.pos());
                }
            }
            else{
                return;
            }

            entity.refresh();
            entity.sanitize();
        });
        config(Point2.class, (AnucoinNodeBuild entity, Point2 trans) -> {
            Building other = world.build(trans.x);
            boolean contains = entity.links.contains(trans.x) && linkValid(entity, other);

            if(contains){
                //withdraw
                CoinBuild cb = (CoinBuild) other;
                if(!cb.outputCoin()) return;
                cb.handleCoin(entity, -trans.y);
                entity.anucoins += trans.y;
            }
        });
        configClear((AnucoinNodeBuild entity) -> {
            for(int i = 0; i < entity.links.size; i++){
                if(world.build(entity.links.get(i)) instanceof BankLinked bl){
                    bl.removeLink(entity.tile);
                }
            }
            entity.links.clear();
            entity.refresh();
        });
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, (range + 9) * tilesize * 2f); //9 is max block size / 2 + 1
    }

    @Override
    public void load(){
        super.load();

        laser = Core.atlas.find("betamindy-path");
        laserEnd = Core.atlas.find("betamindy-path-end");
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.linkRange, range, StatUnit.blocks);
        stats.add(Stat.itemCapacity, Core.bundle.format("ui.anucoin.emoji", initialMaxCoins));
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("anucoins", (AnucoinNodeBuild entity) -> new Bar(
                () -> Core.bundle.format("bar.anucoin", entity.anucoins),
                () -> Color.coral,
                () -> Mathf.clamp(entity.anucoins / (float)entity.maxCoins())));
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, teamRegions[Team.sharded.id]};
    }

    @Override
    public void createIcons(MultiPacker packer){
        Drawm.generateTeamRegion(packer, this);
        super.createIcons(packer);
    }

    public boolean linkValid(Building tile, Building link){
        if(tile == link || link == null || tile.team != link.team || link.dead) return false;

        return Math.max(Math.abs(tile.tileX() - link.tileX()), Math.abs(tile.tileY() - link.tileY())) <= range && link instanceof CoinBuild;
    }

    public class AnucoinNodeBuild extends Building implements CoinBuild{
        public IntSeq links = new IntSeq();
        public int anucoins = 0;

        @Override
        public int coins(){
            return anucoins;
        }

        @Override
        public void handleCoin(Building source, int amount){
            anucoins += amount; //debts can be a thing here
            //if(anucoins > maxCoins()) anucoins = maxCoins();
        }

        @Override
        public int acceptCoin(Building source, int amount){
            if(anucoins >= maxCoins()) return 0;
            return Math.min(maxCoins() - anucoins, amount);
        }

        @Override
        public boolean outputCoin(){
            return anucoins > 0;
        }

        public int maxCoins(){
            int c = initialMaxCoins;
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(linkValid(this, b) && links.get(i) == b.pos() && b.block instanceof AnucoinVault vault){
                    c += vault.capacity;
                }
            }
            return c;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }

            if(this == other){
                if(links.size == 0){
                    tmpe.clear();
                    for(int x = tile.x - range; x <= tile.x + range; x++){
                        for(int y = tile.y - range; y <= tile.y + range; y++){
                            Building link = world.build(x, y);

                            if(link != this && !tmpe.contains(link) && linkValid(this, link)){
                                tmpe.add(link);
                                configure(link.pos());
                            }
                        }
                    }
                }else{
                    configure(null);
                }
                deselect();
                return false;
            }

            return true;
        }

        void squares(Building b, Color color){
            float radius = b.block.size * tilesize / 2f;
            Lines.stroke(3f, Pal.gray);
            Lines.square(b.x, b.y, radius + 1f);
            Lines.stroke(1f, color);
            Lines.square(b.x, b.y, radius);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            float radius = (range + 0.5f) * tilesize;
            Lines.stroke(3f, Pal.gray);
            Lines.square(x, y, radius + 1f);
            Lines.stroke(1f, Pal2.coin);
            Lines.square(x, y,  radius);
            Draw.reset();
        }

        @Override
        public void drawConfigure(){
            float radius = (range + 0.5f) * tilesize;
            Lines.stroke(3f, Pal.gray);
            Lines.square(x, y, radius + 1f);
            Lines.stroke(1f, Pal2.coin);
            Lines.square(x, y,  radius);
            squares(this, Pal2.coin);

            for(int x = tile.x - range; x <= tile.x + range; x++){
                for(int y = tile.y - range; y <= tile.y + range; y++){
                    Building link = world.build(x, y);

                    if(link != this && linkValid(this, link)){
                        boolean linked = links.indexOf(link.pos()) >= 0;

                        if(linked){
                            squares(link, (link instanceof BankLinked) ? ((link.block instanceof AnucoinVault) ? Pal2.coin : Color.coral) : Color.green);
                        }
                    }
                }
            }

            Draw.reset();
        }

        @Override
        public void updateTile(){
            if(timer.get(autoTransaction, transactionInterval)){
                for(int i = 0; i < links.size; i++){
                    Building b = world.build(links.get(i));
                    if(linkValid(this, b) && links.get(i) == b.pos()){
                        CoinBuild cb = (CoinBuild) b;
                        int need = cb.requiredCoin(this);
                        if(need > 0 && anucoins > 0){
                            need = Math.min(need, anucoins);
                            anucoins -= need;
                            cb.handleCoin(this, need);
                        }
                    }
                }
            }
        }

        public void sanitize(){
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(!linkValid(this, b) || links.get(i) != b.pos()){
                    links.removeIndex(i);
                    i--;
                }
            }
        }

        @Override
        public void draw(){
            super.draw();

            if(Mathf.zero(Renderer.laserOpacity)) return;

            Draw.z(Layer.scorch - 1f);
            Draw.mixcol(Pal2.path, 1f);
            Lines.stroke(22f);
            //credits to Yuria Shikibe
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(!linkValid(this, b) || links.get(i) != b.pos()) continue;
                //float targetOffset = b.block.size / 2f * tilesize + 1f;
                float angle = angleTo(b);

                //boolean right = Mathf.equal(angle, 0, 90);
                //boolean up = Mathf.equal(angle, 90, 90);

                boolean horizontal = Mathf.equal(angle, 0, 45) || Mathf.equal(angle, 180, 45);
                /*
                float
                        fromX = x + Mathf.num(horizontal) * Mathf.sign(right) * offset, toX = b.x + Mathf.num(!horizontal) * Mathf.sign(!right) * targetOffset,
                        fromY = y + Mathf.num(!horizontal) * Mathf.sign(up) * offset, toY = b.y + Mathf.num(horizontal) * Mathf.sign(!up) * targetOffset;*/
                float fromX = x, fromY = y, toX = b.x, toY = b.y;

                Tmp.v1.set(horizontal ? toX : fromX, !horizontal ? toY : fromY);

                Lines.line(laser, fromX, fromY, Tmp.v1.x, Tmp.v1.y, false);
                Lines.line(laser, Tmp.v1.x, Tmp.v1.y,toX, toY, false);
                //Fill.square(Tmp.v1.x, Tmp.v1.y, 0.5f);
                //Drawf.laser(null, laser, laserEnd, fromX, fromY, Tmp.v1.x, Tmp.v1.y);
                //Drawf.laser(null, laser, laserEnd, toX, toY, Tmp.v1.x, Tmp.v1.y);
                Draw.rect(laserEnd, Tmp.v1.x, Tmp.v1.y);
                Draw.rect(laserEnd, toX, toY);
            }

            Draw.reset();
        }

        public void transButton(Table pane, Building build){
            if(build.block instanceof AnucoinVault) return;
            CoinBuild cb = (CoinBuild) build;

            pane.button(t -> {
                t.left().touchable(() -> Touchable.childrenOnly);
                t.image(build.block.uiIcon).size(40).padRight(10f);

                t.table(tt -> {
                    tt.left();
                    tt.add(build.block.localizedName + Strings.format(" [gray](@,@)[]", build.tileX(), build.tileY())).growX().left().color(build instanceof BankLinked ? Color.coral : Pal2.coin);
                    tt.row();
                    tt.label(() -> Core.bundle.format("ui.trans.button", cb.coins(), cb.requiredCoin(this), cb.outputCoin())).growX().left();
                }).fillX();

                t.button(Icon.upload, Styles.clearNonei, () -> {
                    //configure [pos, bank receiving amount] as Point2
                    if(build.dead || !links.contains(build.pos())){
                        Vars.ui.showInfoFade("@ui.trans.error");
                        return;
                    }
                    int coins = Math.min(cb.coins(), maxCoins() - anucoins);//bank receiving is +, bank withdrawing is -. In this case, this is +
                    if(coins > 0) configure(new Point2(build.pos(), coins));
                }).size(40f).color(Color.green).disabled(b -> !cb.outputCoin());
                //todo strict transaction
            }, () -> {
            }).left().growX();
            pane.row();
        }

        public void refresh(){
            if(!headless && bankDialog != null && bankDialog.isShown()) rebuild();
        }

        public void rebuild(){
            float width = Math.min(Core.graphics.getWidth(), Core.graphics.getHeight());

            if(bankDialog == null){
                bankDialog = new BaseDialog(localizedName);
                bankDialog.cont.center().top();
                bankDialog.addCloseButton();
            }
            bankDialog.cont.clearChildren();
            bankDialog.cont.table(t -> {
                t.center().top();
                Image image = t.image(AnucoinTex.uiCoin).size(30f).center().padRight(10f).get();
                image.clicked(() -> {
                    if(Core.input.keyDown(KeyCode.shiftLeft) || mobile){
                        if(Core.app.getClipboardText().equals(AnucoinTex.emoji)){
                            Core.app.setClipboardText(AnucoinTex.emojiBit);
                            Vars.ui.showInfoFade("[pink]"+Core.bundle.get("copied")+"[]");
                        }
                        else{
                            Core.app.setClipboardText(AnucoinTex.emoji);
                            Vars.ui.showInfoFade("@copied");
                        }
                    }
                });
                t.label(() -> anucoins + "[lightgray] / " + maxCoins() + "[]").padRight(10f).center();
                t.button(Icon.refresh, Styles.cleari, 25f, this::refresh).size(25f).padRight(10f);
            });

            bankDialog.cont.row();
            ScrollPane itemPane = new ScrollPane(new Table(tbl -> {
                tbl.center().top();
                tbl.table(marker -> {
                    marker.image().color(Pal2.coin).height(4f).growX();
                    marker.add("@ui.transaction").color(Pal2.coin).pad(3f);
                    marker.image().color(Pal2.coin).height(4f).growX();
                }).fillX().growX();
                tbl.row();
                for(int i = 0; i < links.size; i++){
                    Building link = world.build(links.get(i));
                    if(!linkValid(this, link) || links.get(i) != link.pos()) continue;
                    transButton(tbl, link);
                }
            }));

            bankDialog.cont.add(itemPane).center().width(width * 0.6f);
        }

        @Override
        public void buildConfiguration(Table table){
            super.buildConfiguration(table);
            rebuild();
            table.button(Icon.layers, Styles.cleari, 40f, () -> {
                rebuild();
                bankDialog.show();
            });
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.s(links.size);
            for(int i = 0; i < links.size; i++){
                write.i(links.get(i));
            }
            write.i(anucoins);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            links.clear();
            short amount = read.s();
            for(int i = 0; i < amount; i++){
                links.add(read.i());
            }
            anucoins = read.i();
        }
    }
}
