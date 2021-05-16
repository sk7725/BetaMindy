package betamindy.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class FireColor implements ContentList {
    public static ObjectMap<Item, Color> fromMap = new ObjectMap<>();
    public static ObjectMap<Item, Color> toMap = new ObjectMap<>();

    @Override
    public void load(){
        c(MindyItems.bittrium, Color.magenta, Color.cyan);

        c(Items.copper, "66ffbf"); //Cu
        c(Items.plastanium, Pal.plastanium);
        c(Items.surgeAlloy, Pal.surge, Pal.lancerLaser);
        c(Items.sand, Color.gray, Color.gray); //extinguish
        c(Items.lead, "c2fcff"); //Pb
        c(Items.pyratite, Pal.lightPyraFlame, Pal.darkPyraFlame);
        c(Items.silicon, Color.white, Color.lightGray);
        c(Items.scrap, "ff4a80"); //Sr

        c(MindyItems.scalarRaw, "f5ddf3");
        c(MindyItems.vectorRaw, "cfddff");
        c(MindyItems.tensorRaw, "82ffe8");
        c(MindyItems.source, Color.valueOf("5eff79"), Pal.accent);
        //binlog : Pal.accent / rblog: ff4444
    }

    public static Color from(Item i){
        return fromMap.containsKey(i) ? fromMap.get(i) : Pal.lightFlame;
    }

    public static Color to(Item i){
        return fromMap.containsKey(i) ? toMap.get(i) : Pal.darkFlame;
    }

    public static Color f(Color tmp, Item i, float lerp){
        if(!fromMap.containsKey(i)) return tmp.set(Pal.lightFlame).lerp(Pal.darkFlame, lerp);
        else return tmp.set(fromMap.get(i).lerp(toMap.get(i), lerp));
    }

    public static void fset(Item i, float lerp){
        if(!fromMap.containsKey(i)) Draw.color(Pal.lightFlame, Pal.darkFlame, lerp);
        else Draw.color(fromMap.get(i), toMap.get(i), lerp);
    }

    public static void fset(Item i, float lerp, Color smoke){
        if(!fromMap.containsKey(i)) Draw.color(Pal.lightFlame, Pal.darkFlame, smoke, lerp);
        else Draw.color(fromMap.get(i), toMap.get(i), smoke, lerp);
    }

    public void c(Item i, Color a, Color b){
        fromMap.put(i, a);
        toMap.put(i, b);
    }

    public void c(Item i, Color a){
        fromMap.put(i, a);
        toMap.put(i, a.cpy().shiftValue(1f - a.value()).lerp(Color.gray, 0.7f));
    }

    public void c(Item i, String a){
        c(i, Color.valueOf(a));
    }
}
