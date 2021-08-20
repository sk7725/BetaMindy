package betamindy.type.item;

import arc.*;
import arc.graphics.*;
import mindustry.type.*;

/** Items used for purchased blocks, that cannot be salvaged. */
//todo exclude these items from the item shop
public class ForeignItem extends Item {
    public ForeignItem(String name, Color color){
        super(name, color);

        details = details == null ? Core.bundle.get("item.foreign") : Core.bundle.get("item.foreign") + "\n" + details;
    }

    @Override
    public boolean unlockedNow(){
        return false;
    }

    //todo v7
    public boolean unlockedNowHost(){
        return false;
    }
}
