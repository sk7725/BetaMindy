package betamindy.content;

import arc.func.Cons;
import arc.struct.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.content.Blocks.*;

//Credits to younggam
@SuppressWarnings("unchecked")
public class OverWriter{
    public static <T extends UnlockableContent> void set(UnlockableContent target, Cons<T> setter){
        setter.get((T)target);
    }

    public static <T extends UnlockableContent> void setAll(Cons<T> setter, UnlockableContent... targets){
        for(UnlockableContent target : targets){
            setter.get((T)target);
        }
    }

    public static void load(){
        //technically this isnt braindy-ing,as it "adds" not "overrides" vanilla
        setAll((Block b) -> b.attributes.set(MindyAttribute.magnetic, 1f), darkMetal, metalFloor, metalFloor2, metalFloor3, metalFloor4, metalFloor5, metalFloorDamaged);
        setAll((Block b) -> b.attributes.set(MindyAttribute.pushless, 1f), thoriumWall, thoriumWallLarge);

        if(Version.build <= 135){
            //Anuke new release when
            weathers().each(weather -> weather.attrs.get(Attribute.water));//invoke each weather
        }
        if(Version.build >= 135 || Version.build == -1) return;
        //re-set the vanilla attributes because its wiped
        set(taintedWater, (Block b) -> b.attributes.set(Attribute.spores, 0.15f));
        set(deepTaintedWater, (Block b) -> b.attributes.set(Attribute.spores, 0.15f));
        set(darksandTaintedWater, (Block b) -> b.attributes.set(Attribute.spores, 0.1f));
        set(slag, (Block b) -> b.attributes.set(Attribute.heat, 0.85f));
        set(basalt, (Block b) -> b.attributes.set(Attribute.water, -0.25f));
        set(hotrock, (Block b) -> b.attributes.set(Attribute.heat, 0.5f));
        set(hotrock, (Block b) -> b.attributes.set(Attribute.water, -0.5f));
        set(magmarock, (Block b) -> b.attributes.set(Attribute.heat, 0.75f));
        set(magmarock, (Block b) -> b.attributes.set(Attribute.water, -0.75f));
        set(sand, (Block b) -> b.attributes.set(Attribute.oil, 0.7f));
        set(darksand, (Block b) -> b.attributes.set(Attribute.oil, 1.5f));
        set(mud, (Block b) -> b.attributes.set(Attribute.water, 1f));
        set(grass, (Block b) -> b.attributes.set(Attribute.water, 0.1f));
        set(salt, (Block b) -> b.attributes.set(Attribute.water, -0.3f));
        set(salt, (Block b) -> b.attributes.set(Attribute.oil, 0.3f));
        set(snow, (Block b) -> b.attributes.set(Attribute.water, 0.2f));
        set(ice, (Block b) -> b.attributes.set(Attribute.water, 0.4f));
        set(iceSnow, (Block b) -> b.attributes.set(Attribute.water, 0.3f));
        set(shale, (Block b) -> b.attributes.set(Attribute.oil, 1.6f));
        set(moss, (Block b) -> b.attributes.set(Attribute.spores, 0.15f));
        set(sporeMoss, (Block b) -> b.attributes.set(Attribute.spores, 0.3f));
    }

    public static Seq<Weather> weathers(){
        return Vars.content.getBy(ContentType.weather);
    }
}
