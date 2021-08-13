package betamindy.world.blocks.storage;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import betamindy.graphics.*;
import betamindy.type.*;
import betamindy.ui.*;
import mindustry.Vars;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class AnucoinNode extends Block {
    /** surprisingly, this block's range is a square, not a circle */
    public int range = 15;
    public TextureRegion laser, laserEnd;
    public int initialMaxCoins = 1000000;
    public int autoTransaction = timers++;
    public float transactionInterval = 60f;

    BaseDialog bankDialog = null;
    float buttonWidth = 210f;

    private final Seq<Building> tmpe = new Seq<>();

    public AnucoinNode(String name){
        super(name);

        configurable = solid = update = expanded = sync = true;
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
    public void load() {
        super.load();

        laser = Core.atlas.find("betamindy-anuke-laser");
        laserEnd = Core.atlas.find("betamindy-anuke-laser-end");
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
            if(anucoins > maxCoins()) anucoins = maxCoins();
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
            return initialMaxCoins; //todo anusafe
        }

        public void drawLaser(Team team, float x1, float y1, float x2, float y2, int size1, int size2){
            float angle1 = Angles.angle(x1, y1, x2, y2),
                    vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                    len1 = size1 * tilesize / 2f - 1.5f, len2 = size2 * tilesize / 2f - 1.5f;

            Drawf.laser(team, laser, laserEnd, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, 0.25f);
        }

        @Override
        public boolean onConfigureTileTapped(Building other){
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
                            squares(link, (link instanceof BankLinked) ? Color.coral : Color.green);
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
                    if(linkValid(this, b)){
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

        @Override
        public void draw() {
            super.draw();

            if(Mathf.zero(Renderer.laserOpacity)) return;

            Draw.z(Layer.scorch - 1f);
            //todo paths

            Draw.reset();
        }

        public void transButton(Table pane, Building build){
            CoinBuild cb = (CoinBuild) build;

            pane.button(t -> {
                t.left();
                t.image(build.block.icon(Cicon.medium)).size(40).padRight(10f);

                t.table(tt -> {
                    tt.left();
                    tt.add(build.block.localizedName).growX().left().color(Pal2.coin);
                    tt.row();
                    tt.label(() -> Core.bundle.format("ui.trans.button", cb.coins(), cb.requiredCoin(this), cb.outputCoin())).growX().left();
                }).growX();

                t.button(Icon.upload, Styles.clearPartiali, () -> {
                    //todo configure [pos, amount] as Point2
                    int coins = cb.coins();
                    cb.handleCoin(this, -coins);
                    anucoins += coins;
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
                t.label(() -> String.valueOf(anucoins)).padRight(10f).center();
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
                    if(!linkValid(this, link)) continue;
                    transButton(tbl, link);
                }
            }));

            bankDialog.cont.add(itemPane).center().width(width * 0.6f);
            bankDialog.cont.row();
            bankDialog.cont.table(t -> {
                if(Vars.mobile){
                    buttonWidth = (width / 2f) * 0.55f;
                }
                t.button("@back", Icon.left, bankDialog::hide).size(buttonWidth, 64f);
            });
        }

        @Override
        public void buildConfiguration(Table table){
            super.buildConfiguration(table);
            rebuild();
            table.button(Icon.layers, Styles.clearTransi, 40f, () -> {
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
