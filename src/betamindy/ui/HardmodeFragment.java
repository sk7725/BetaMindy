package betamindy.ui;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import betamindy.graphics.*;
import betamindy.util.*;
import mindustry.graphics.*;
import mindustry.ui.*;

import static betamindy.BetaMindy.*;

public class HardmodeFragment{
    private Table next;
    private SBar mainBar;
    public static Drawable background;

    public void build(Group parent){
        next = new Table();
        next.left();
        mainBar = new SBar(() -> hardmode.barText(), () -> hardmode.isBoss() && hardmode.portal != null && hardmode.portal.state == 5 ? Pal.health : hardmode.color(), () -> Mathf.clamp(hardmode.barVal())).exact(true).blink(Color.white);
        background = nine("betamindy-kback2", 16, 0.5f);
        parent.fill(table -> {
            table.name = "hardmodebar";
            table.left();
            table.table(t -> {
                t.background(background);
                t.top();
                t.image().color(Pal.accent).pad(0f).height(3f).fillX();
                t.row();
                t.image().color(Pal.accentBack).pad(0f).padTop(-0.1f).height(3f).fillX();
                t.row();

                t.add("Invasion", Styles.techLabel).color(Pal2.portal).pad(4f);
                t.row();

                t.image().color(Pal.accent).pad(0f).height(3f).fillX();
                t.row();
                t.image().color(Pal.accentBack).pad(0f).padTop(-0.1f).height(3f).fillX();
                t.row();

                t.add(mainBar).size(220f, 45f).pad(4f);
                t.row();
                t.table(p -> {
                    p.left();
                    p.defaults().pad(0f).padLeft(4f).padRight(4f);
                    p.label(() -> Core.bundle.format("ui.hardmode.lv", hardmode.level())).size(70f, 26f);
                    p.add("@ui.next").size(50f, 26f);
                    p.add(next).growX().height(26f);
                }).growX().height(27f);
            }).size(240f ,132f).visible(() -> hardmode.portal != null).left();
        });
    }

    public void nextWave(@Nullable HardMode.Portal po){
        mainBar.reset(1f);
        mainBar.flash();
        next.clearChildren();
        if(po != null) po.buildNext(next);
    }

    public void reset(){
        mainBar.reset(0f);
        next.clearChildren();
    }

    public Drawable nine(String name, int border, float scale){
        Drawable out;

        TextureAtlas.AtlasRegion region = Core.atlas.find(name);

        NinePatch patch = new NinePatch(region, border, border, border, border);
        int[] pads = region.pads;
        if(pads != null) patch.setPadding(pads[0], pads[1], pads[2], pads[3]);
        out = new ScaledNinePatchDrawable(patch, scale);

        return out;
    }
}
