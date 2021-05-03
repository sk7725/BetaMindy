package betamindy.util.xelo;

import arc.math.geom.*;
import arc.util.pooling.*;
import mindustry.gen.*;

public interface Movable extends Pool.Poolable{

    void set(Building building, Point2 direction);

    void pushed();

    void config();
}
