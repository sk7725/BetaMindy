package betamindy.content;

import arc.func.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import betamindy.entities.*;
import mindustry.ctype.*;
import mindustry.gen.*;

@SuppressWarnings("unchecked")
public class MindyUnitTypes implements ContentList{
    private static Entry<Class<? extends Entityc>, Prov<? extends Entityc>>[] types = new Entry[]{
        prov(GhostEntity.class, GhostEntity::create)
    };

    private static ObjectIntMap<Class<? extends Entityc>> idMap = new ObjectIntMap<>();

    /**
     * Internal function to flatmap {@code Class -> Prov} into an {@link Entry}.
     * @author GlennFolker
     */
    private static <T extends Entityc> Entry<Class<T>, Prov<T>> prov(Class<T> type, Prov<T> prov){
        Entry<Class<T>, Prov<T>> entry = new Entry<>();
        entry.key = type;
        entry.value = prov;
        return entry;
    }

    /**
     * Setups all entity IDs and maps them into {@link EntityMapping}.
     * @author GlennFolker
     */
    private static void setupID(){
        for(
            int i = 0,
            j = 0,
            len = EntityMapping.idMap.length;
            
            i < len;
            
            i++
        ){
            if(EntityMapping.idMap[i] == null){
                idMap.put(types[j].key, i);
                EntityMapping.idMap[i] = types[j].value;

                if(++j >= types.length) break;
            }
        }
    }

    /**
     * Retrieves the class ID for a certain entity type.
     * @author GlennFolker
     */
    public static <T extends Entityc> int classID(Class<T> type){
        return idMap.get(type, -1);
    }

    @Override
    public void load(){
        setupID();
    }
}
