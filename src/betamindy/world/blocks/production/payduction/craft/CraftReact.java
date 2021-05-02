package betamindy.world.blocks.production.payduction.craft;

import arc.scene.ui.layout.*;
import mindustry.type.*;

public interface CraftReact {
    void craft(ItemStack[] in, ItemStack out);
    void displayReact(Table t);
}
