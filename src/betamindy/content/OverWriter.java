package betamindy.content;

import arc.func.Cons;
import mindustry.ctype.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.content.Blocks.*;

//Credits to younggam
@SuppressWarnings("unchecked")
public class OverWriter implements ContentList{
    public <T extends UnlockableContent> void forceOverWrite(UnlockableContent target, Cons<T> setter){
        setter.get((T)target);
    }

    @Override
    public void load(){
        //region contents

        forceOverWrite(blockForge, (Block t) -> {
            t.buildVisibility = BuildVisibility.shown;
        });

        forceOverWrite(blockLoader, (Block t) -> {
            t.buildVisibility = BuildVisibility.shown;
        });

        forceOverWrite(blockUnloader, (Block t) -> {
            t.buildVisibility = BuildVisibility.shown;
        });

        //endregion
    }
}
