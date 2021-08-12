package betamindy.world.blocks.storage;

import mindustry.world.*;

public interface BankLinked {
    boolean occupied(Tile other);
    void setLink(Tile other);
    void removeLink(Tile other);
}
