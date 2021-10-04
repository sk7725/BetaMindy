package betamindy.content;

import arc.graphics.*;
import betamindy.graphics.*;
import betamindy.planets.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

public class MindyPlanets implements ContentList {
    public static Planet shar, routercube;

    @Override
    public void load(){
        shar = new Planet("shar", Planets.serpulo, 0, 0.9f){{
            atmosphereColor = Color.gray.cpy();
            landCloudColor = Color.clear.cpy();
            atmosphereRadOut = 0.5f;
            atmosphereRadIn = 0.05f;
            tidalLock = true;
            hasAtmosphere = true;
            generator = new SharMoonGenerator();
            meshLoader = () -> new HexMesh(this, 5);
            //accessible = false;
        }};

        //routercube = new Planet("routercube", Planets.serpulo, 0, 0.6f){{
        //    meshLoader = () ->
        //}}
    }
}
