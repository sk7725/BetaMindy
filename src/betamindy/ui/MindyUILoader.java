package betamindy.ui;

import arc.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.Font.*;
import arc.input.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.*;
import betamindy.*;
import betamindy.graphics.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static arc.Core.atlas;
import static betamindy.BetaMindy.uwu;
import static mindustry.Vars.*;

public class MindyUILoader {
    public HardmodeFragment hardfrag;
    public PlacementInvFragment invfrag;
    public TouchpadFragment touchpad;
    private static Seq<Font> fonts;

    public static Drawable accentEdge2, pane, pane2, buttonEdge2;
    public static TextButton.TextButtonStyle whitePiano, blackPiano;
    public static ImageButton.ImageButtonStyle clearAccenti;

    public void init(){
        AncientKoruh.load();
        AnucoinTex.load();

        accentEdge2 = atlas.drawable("betamindy-blue-edge-2");
        pane = atlas.drawable("pane");
        pane2 = atlas.drawable("pane-2");
        buttonEdge2 = atlas.drawable("button-edge-2");
        whitePiano = new TextButton.TextButtonStyle(atlas.drawable("betamindy-keyw"), atlas.drawable("betamindy-keyw-down"), atlas.drawable("betamindy-keyw-checked"), Fonts.outline);
        blackPiano = new TextButton.TextButtonStyle(atlas.drawable("betamindy-keyb"), atlas.drawable("betamindy-keyb-down"), atlas.drawable("betamindy-keyb-checked"), Fonts.def);
        clearAccenti = new ImageButton.ImageButtonStyle(Styles.cleari);
        clearAccenti.imageUpColor = Pal.accent;
        clearAccenti.imageOverColor = Pal2.coin;
        clearAccenti.imageDownColor = Pal.accentBack;
        clearAccenti.imageDisabledColor = Pal.gray;

        Core.app.post(() -> {
            hardfrag = new HardmodeFragment();
            hardfrag.build(ui.hudGroup);
            invfrag = new PlacementInvFragment();
            invfrag.build(ui.hudGroup);
            if(Core.settings.getBool("touchpadenable")){
                touchpad = new TouchpadFragment();
                touchpad.build(ui.hudGroup);
            }
            if(uwu){
                Core.app.post(() -> Core.app.post(() -> Core.app.post(() -> {
                    if(Core.input.keyDown(KeyCode.down) && Core.input.keyDown(KeyCode.shiftRight)){
                        //delete campaign for testing
                        BaseDialog dialog = new BaseDialog("@mod.betamindy.name");
                        dialog.cont.add("[scarlet]Debug key input detected:\nDelete modded campaign data?").width(Vars.mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
                        dialog.buttons.defaults().size(200f, 54f).pad(2f);
                        dialog.setFillParent(false);
                        dialog.cont.row();
                        dialog.buttons.button("@cancel", dialog::hide);
                        dialog.buttons.button("@ok", () -> {
                            dialog.hide();
                            BetaMindy.clearCampaign();
                        });
                        dialog.show();
                    }
                })));
            }
        });

        //if(uwu) Core.settings.put("nomusicask", false);
        if((mods.getMod(MusicControl.musicMod) == null || !mods.getMod(MusicControl.musicMod).enabled()) && !Core.settings.getBool("nomusicask", false)){
            Core.app.post(() -> {
                BaseDialog dialog = new BaseDialog("@mod.betamindy.name");
                dialog.cont.add(Core.bundle.format("ui.musicmodplease", MusicControl.musicRepo)).width(Vars.mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
                dialog.buttons.defaults().size(200f, 54f).pad(2f);
                dialog.setFillParent(false);
                dialog.cont.row();
                dialog.cont.check("@ui.notagain", false, b -> {
                    if(b) Core.settings.put("nomusicask", true);
                }).left().padTop(8f);
                dialog.buttons.button("@cancel", dialog::hide);
                dialog.buttons.button("@ok", () -> {
                    dialog.hide();

                    Core.app.post(() -> {
                        if(mods.getMod(MusicControl.musicMod) == null){
                            //get mod
                            ui.mods.show();
                            Reflect.invoke(ui.mods, "githubImportMod", new Object[]{MusicControl.musicRepo, false}, String.class, boolean.class);
                        }
                        else{
                            //enable & update mod
                            mods.setEnabled(mods.getMod(MusicControl.musicMod), true);
                            ui.mods.show();
                        }
                    });
                });
                dialog.keyDown(KeyCode.escape, dialog::hide);
                dialog.keyDown(KeyCode.back, dialog::hide);
                dialog.show();
            });
        }
        else if(!Core.settings.getBool("bloom") && !Core.settings.getBool("nobloomask", false)){
            Core.app.post(() -> {
                BaseDialog dialog = new BaseDialog("@mod.betamindy.name");
                dialog.cont.add(Core.bundle.format("ui.bloomplease", Core.bundle.get("setting.bloom.name"))).width(Vars.mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
                dialog.buttons.defaults().size(200f, 54f).pad(2f);
                dialog.setFillParent(false);
                dialog.cont.row();
                dialog.cont.check("@ui.notagain", false, b -> {
                    if(b) Core.settings.put("nobloomask", true);
                }).left().padTop(8f);
                dialog.buttons.button("@cancel", dialog::hide);
                dialog.buttons.button("@ok", () -> {
                    dialog.hide();
                    Core.settings.put("bloom", true);
                    renderer.toggleBloom(true);
                });
                dialog.keyDown(KeyCode.escape, dialog::hide);
                dialog.keyDown(KeyCode.back, dialog::hide);
                dialog.show();
            });
        }
    }

    public static String addEmoji(TextureRegion region, int unicode){
        if(fonts == null) fonts = Seq.with(Fonts.def, Fonts.outline);
        int size = (int) (Fonts.def.getData().lineHeight / Fonts.def.getData().scaleY);

        Glyph glyph = new Glyph();
        glyph.id = unicode;
        glyph.srcX = 0;
        glyph.srcY = 0;
        glyph.width = size;
        glyph.height = (int) ((float) region.height / region.width * size);
        glyph.u = region.u;
        glyph.v = region.v2;
        glyph.u2 = region.u2;
        glyph.v2 = region.v;
        glyph.xoffset = 0;
        glyph.yoffset = -size;
        glyph.xadvance = size;
        glyph.kerning = null;
        glyph.fixedWidth = true;
        glyph.page = 0;
        fonts.each(f -> f.getData().setGlyph(unicode, glyph));
        return ((char) unicode) + "";
    }

    public static String addOutlineEmoji(TextureRegion region, TextureRegion outline, int unicode){
        if(fonts == null) fonts = Seq.with(Fonts.def, Fonts.outline);
        int size = (int) (Fonts.def.getData().lineHeight / Fonts.def.getData().scaleY);

        Glyph glyph = new Glyph();
        glyph.id = unicode;
        glyph.srcX = 0;
        glyph.srcY = 0;
        glyph.width = size;
        glyph.height = (int) ((float) region.height / region.width * size);
        glyph.u = region.u;
        glyph.v = region.v2;
        glyph.u2 = region.u2;
        glyph.v2 = region.v;
        glyph.xoffset = 0;
        glyph.yoffset = -size;
        glyph.xadvance = size;
        glyph.kerning = null;
        glyph.fixedWidth = true;
        glyph.page = 0;

        Glyph oglyph = new Glyph();
        oglyph.id = unicode;
        oglyph.srcX = 0;
        oglyph.srcY = 0;
        oglyph.width = size;
        oglyph.height = (int) ((float) outline.height / outline.width * size);
        oglyph.u = outline.u;
        oglyph.v = outline.v2;
        oglyph.u2 = outline.u2;
        oglyph.v2 = outline.v;
        oglyph.xoffset = 0;
        oglyph.yoffset = -size;
        oglyph.xadvance = size;
        oglyph.kerning = null;
        oglyph.fixedWidth = true;
        oglyph.page = 0;

        fonts.each(f -> f.getData().setGlyph(unicode, f == Fonts.outline ? oglyph : glyph));
        return ((char) unicode) + "";
    }
}
