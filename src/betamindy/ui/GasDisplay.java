package betamindy.ui;

import arc.*;
import arc.graphics.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import betamindy.world.blocks.production.*;
import mindustry.core.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

import static mindustry.Vars.ui;

public class GasDisplay extends Table {
    public final String gasRegion = "betamindy-gas";
    public final String gas;
    public final float amount;
    public final Color color = new Color();

    public GasDisplay(String gas, Color gasColor){
        this(gas, gasColor, 0f);
    }

    public GasDisplay(String gas, Color gasColor, float amount, boolean showName){
        color.set(gasColor).lerp(Color.white, 0.5f);
        add(new GasImage(color, (int)amount));
        if(showName) add(gas).padLeft(4 + (int)amount > 99 ? 4 : 0);

        this.gas = gas;
        this.amount = amount;
    }

    public GasDisplay(String gas, Color gasColor, float amount){
        this(gas, gasColor, amount, true);
    }

    /** Displays the gas with a "/sec" qualifier based on the time period, in ticks. */
    public GasDisplay(String gas, Color gasColor, float amount, float timePeriod, boolean showName){
        color.set(gasColor).lerp(Color.white, 0.5f);
        add(new GasImage(color, (int)amount));
        add(Strings.autoFixed(amount / (timePeriod / 60f), 2) + StatUnit.perSecond.localized()).padLeft(2).padRight(5).color(Color.lightGray).style(Styles.outlineLabel);
        if(showName) add(gas).padLeft(4 + (int)amount > 99 ? 4 : 0);

        this.gas = gas;
        this.amount = amount;
    }

    public GasDisplay(Condenser target, float amount, float timePeriod, boolean showName){
        color.set(target.gasColor).lerp(Color.white, 0.5f);
        add(new GasImage(color, (int)amount));
        add(Strings.autoFixed(amount / (timePeriod / 60f), 2) + StatUnit.perSecond.localized()).padLeft(2).padRight(5).color(Color.lightGray).style(Styles.outlineLabel);
        if(showName) add(target.gasName()).padLeft(4 + (int)amount > 99 ? 4 : 0);

        add(" (to: ").color(Pal.accent);
        button(new TextureRegionDrawable(target.uiIcon), Styles.cleari, 20f, () -> {
            ui.content.show(target);
        }).size(20f).tooltip(target.localizedName);
        add(")").color(Pal.accent);

        this.gas = target.gasName();
        this.amount = amount;
    }

    public class GasImage extends Stack{

        public GasImage(Color color, int amount){

            add(new Table(o -> {
                o.left();
                Image i = new Image(Core.atlas.find(gasRegion));
                i.setColor(color);
                o.add(i).size(32f);
            }));

            if(amount > 0){
                add(new Table(t -> {
                    t.left().bottom();
                    t.add(amount > 1000 ? UI.formatAmount(amount) : amount + "").color(Pal.accent);
                    t.pack();
                }));
            }
        }

        public GasImage(Color color){
            Table t = new Table().left().bottom();
            Image i = new Image(Core.atlas.find(gasRegion));
            i.setColor(color);
            add(i);
            add(t);
        }
    }
}
