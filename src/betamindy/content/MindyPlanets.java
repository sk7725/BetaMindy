package betamindy.content;

import arc.graphics.*;
import arc.util.*;
import betamindy.planets.*;
import mindustry.content.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

public class MindyPlanets{
    public static Planet shar, routercube;

    public static void load(){
        try{
            shar = new Planet("shar", Planets.serpulo, 1f, 2) {{
                atmosphereColor = Color.gray.cpy();
                landCloudColor = Color.clear.cpy();
                atmosphereRadOut = 0.5f;
                atmosphereRadIn = 0.05f;
                tidalLock = true;
                hasAtmosphere = true;
                generator = new SharMoonGenerator();
                meshLoader = () -> new HexMesh(this, 5);
                startSector = 15;
                bloom = false;
            }};
        }
        catch(Exception e){
            Log.info("Couldn't load BM's planet, posting crash now");
            Log.err(e);
        }

        //routercube = new Planet("routercube", Planets.serpulo, 0, 0.6f){{
        //    meshLoader = () ->
        //}}
    }
}
