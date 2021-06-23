package betamindy.world.blocks.environment;

import arc.graphics.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

//looks pretty TODO
public class Crystal extends Block {
    public Item item;
    public Color color1, color2, color3;
    public Crystal(String name, Item item){
        super(name);
        update = true;
        solid = true;
        rotate = false;
        hasShadow = false;
        this.item = item;
    }

    public class CrystalBuild extends Building {

    }
}
