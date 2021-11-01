package betamindy.content;

import arc.func.Cons;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.content.Blocks.*;

//Credits to younggam
@SuppressWarnings("unchecked")
public class OverWriter implements ContentList{
    public <T extends UnlockableContent> void set(UnlockableContent target, Cons<T> setter){
        setter.get((T)target);
    }

    public <T extends UnlockableContent> void setAll(Cons<T> setter, UnlockableContent... targets){
        for(UnlockableContent target : targets){
            setter.get((T)target);
        }
    }

    @Override
    public void load(){
        //technically this isnt braindy-ing,as it "adds" not "overrides" vanilla
        setAll((Block b) -> b.attributes.set(MindyAttribute.magnetic, 1f), darkMetal, metalFloor, metalFloor2, metalFloor3, metalFloor4, metalFloor5, metalFloorDamaged);
        setAll((Block b) -> b.attributes.set(MindyAttribute.pushless, 1f), thoriumWall, thoriumWallLarge);
    }
}
