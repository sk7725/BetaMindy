package betamindy.ui;

import arc.*;
import arc.input.*;
import arc.util.*;
import mindustry.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.*;

public class MindyUILoader {
    public HardmodeFragment hardfrag;

    public void init(){
        AncientKoruh.load();

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
}
