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
            bloom = true;
            atmosphereColor = Color.valueOf("087bff");
            landCloudColor = Color.clear.cpy();
            atmosphereRadOut = 0.2f;
            hasAtmosphere = true;
            generator = new SharMoonGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            //accessible = false;
        }};

        //routercube = new Planet("routercube", Planets.serpulo, 0, 0.6f){{
        //    meshLoader = () ->
        //}}
    }
}
