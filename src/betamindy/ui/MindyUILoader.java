package betamindy.ui;

import arc.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.Font.*;
import arc.input.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.*;

public class MindyUILoader {
    public HardmodeFragment hardfrag;
    private static Seq<Font> fonts;

    public void init(){
        AncientKoruh.load();
        AnucoinTex.load();

        Core.app.post(() -> {
            hardfrag = new HardmodeFragment();
            hardfrag.build(ui.hudGroup);
        });

        if(!Core.settings.getBool("bloom") && !Core.settings.getBool("nobloomask", false)){
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
        if(fonts == null) fonts = Seq.with(Fonts.chat, Fonts.def, Fonts.outline);
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
        if(fonts == null) fonts = Seq.with(Fonts.chat, Fonts.def, Fonts.outline);
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

        fonts.each(f -> {
            f.getData().setGlyph(unicode, f == Fonts.outline ? oglyph : glyph);
        });
        return ((char) unicode) + "";
    }
}
