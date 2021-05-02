package betamindy.util.xelo.movables;

import arc.math.geom.Point2;
import arc.struct.IntSeq;
import betamindy.util.xelo.Movable;
import mindustry.gen.Building;

public class PowerNodeMovable implements Movable {

    private IntSeq links;
    private Building building;

    public PowerNodeMovable() {
        links = new IntSeq();
    }

    @Override
    public void set(Building building, Point2 direction) {
        IntSeq currentLinks = building.power.links;

        if(currentLinks.size == 0) {
            return;
        }

        IntSeq links = this.links;

        this.building = building;

        links.addAll(currentLinks);

        int offX = direction.x;
        int offY = direction.y;

        for(int i = 0; i < links.size; i++) {
            int item = links.items[i];

            int x = Point2.x(item);
            int y = Point2.y(item);

            links.items[i] = Point2.pack(x + offX, y + offY);
        }
    }

    @Override
    public void pushed() {

    }

    @Override
    public void config() {
        if(building == null) {
            return;
        }
        while(!links.isEmpty()) {
            building.configure(links.pop());
        }
    }

    @Override
    public void reset() {
        links.clear();
        building = null;
    }

}
