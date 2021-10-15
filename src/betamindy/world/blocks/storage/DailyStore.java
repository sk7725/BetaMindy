package betamindy.world.blocks.storage;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import betamindy.type.*;
import mindustry.gen.*;
import java.util.*;

public class DailyStore extends Store {
    public float averageStock = 6;
    public PurchaseItem[] dailyItems = {};

    private final Rand rand = new Rand();

    public DailyStore(String name, Object... items){
        super(name, items);
    }

    public class DailyStoreBuild extends StoreBuild {
        public int seed(){ //server and client may be in different timezones, allowing them to buy different items. [0, 372), note that some seeds are skipped because i dont care
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Time.millis());
            return c.get(Calendar.MONTH) * 31 + c.get(Calendar.DAY_OF_MONTH);
        }

        @Override
        public void altConfigured(Unit builder, int value){
            int i = -value - 1;
            if(i >= dailyItems.length) return;
            PurchaseItem item = dailyItems[i];
            if(totalCoins() >= item.cost){
                if(item.purchase(this, builder)){
                    removeCoins(item.cost);
                }
            }
        }

        @Override
        public void beforeButtons(Table tbl){
            if(dailyItems.length == 0) return;

            tbl.table(marker -> {
                marker.image().color(Color.pink).height(4f).growX();
                marker.add(Core.bundle.get("purchase.category.daily")).color(Color.pink).pad(3f);
                marker.image().color(Color.pink).height(4f).growX();
            }).fillX().growX();
            tbl.row();

            rand.setSeed(seed());
            float chance = averageStock / dailyItems.length;
            for(int i = 0; i < dailyItems.length; i++){
                if(!rand.chance(chance * dailyItems[i].scarcity)) continue;
                extraButton(tbl, dailyItems[i], -i - 1);
            }
        }
    }
}
