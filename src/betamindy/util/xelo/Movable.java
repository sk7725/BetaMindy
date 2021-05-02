package betamindy.util.xelo;

import arc.math.geom.Point2;
import arc.util.pooling.Pool;
import mindustry.gen.Building;

public interface Movable extends Pool.Poolable {

    void set(Building building, Point2 direction);

    void pushed();

    void config();
}
