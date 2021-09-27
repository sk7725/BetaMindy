package betamindy.type.item;

import arc.*;
import arc.graphics.*;
import mindustry.type.*;

/** Items used for purchased blocks, that cannot be salvaged. */
public class ForeignItem extends Item {
    public ForeignItem(String name, Color color){
        super(name, color);

        details = details == null ? Core.bundle.get("item.foreign") : Core.bundle.get("item.foreign") + "\n" + details;
    }

    @Override
    public boolean unlockedNow(){
        return false;
    }

    @Override
    public boolean unlockedNowHost(){
        return false;
    }
}
