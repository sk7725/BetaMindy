package betamindy.content;

import arc.*;
import arc.func.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.SettingsDialog.*;
import arc.scene.ui.layout.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class SettingAdder {
    public void init(){
        BaseDialog dialog = new BaseDialog("Progressed Materials");

        dialog.addCloseButton();
        dialog.cont.center().pane(p -> {
            checkSetting(p, "bm-nonmoddedservers");
            checkSetting(p, "bm-slimeeffect");
            checkSetting(p, "bm-accelballs", true);
            checkSetting(p, "bm-correctview", true);
            sliderSetting(p, "bm-animlevel", 2, 0, 3, i -> Core.bundle.get("slider.level." + i, "" + i));
        }).growY().width(mobile ? graphics.getWidth() : graphics.getWidth() / 3f);

        ui.settings.shown(() -> {
            Table settingUi = (Table)((Group)((Group)(ui.settings.getChildren().get(1))).getChildren().get(0)).getChildren().get(0); //This looks so stupid lol
            settingUi.row();
            settingUi.button(bundle.get("setting.bm-title"), Styles.cleart, dialog::show);
        });
    }

    public void checkSetting(Table table, String key, boolean def, Boolc changed){
        CheckBox box = new CheckBox(bundle.get("setting." + key + ".name"));

        box.update(() -> box.setChecked(settings.getBool(key, def)));

        box.changed(() -> {
            settings.put(key, box.isChecked());
            if(changed != null){
                changed.get(box.isChecked());
            }
        });

        box.left();
        table.add(box).left().padTop(3f);
        table.row();
    }

    public void checkSetting(Table table, String key, boolean def){
        checkSetting(table, key, def, null);
    }

    public void checkSetting(Table table, String key){
        checkSetting(table, key, false);
    }

    public void sliderSetting(Table table, String key, int def, int min, int max, int step, StringProcessor s){
        Slider slider = new Slider(min, max, step, false);
        String title = bundle.get("setting." + key + ".name");

        slider.setValue(settings.getInt(key, def));

        Label label = new Label(title);
        slider.changed(() -> {
            settings.put(key, (int)slider.getValue());
            label.setText(title + ": " + s.get((int)slider.getValue()));
        });

        slider.change();

        table.table(t -> {
            t.left().defaults().left();
            t.add(label).minWidth(label.getPrefWidth() / Scl.scl(1f) + 50);
            t.add(slider).width(180);
        }).left().padTop(3);

        table.row();
    }

    public void sliderSetting(Table table, String key, int def, int min, int max, StringProcessor s){
        sliderSetting(table, key, def, min, max, 1, s);
    }
}
