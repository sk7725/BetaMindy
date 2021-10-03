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
            atmosphereColor = Color.valueOf("fff4ba");
            landCloudColor = Color.clear.cpy();
            atmosphereRadOut = 0.1f;
            hasAtmosphere = false;
            generator = new SharMoonGenerator();
            meshLoader = () -> new HexMesh(this, 4);
            //accessible = false;
        }};

        //routercube = new Planet("routercube", Planets.serpulo, 0, 0.6f){{
        //    meshLoader = () ->
        //}}
    }
}
