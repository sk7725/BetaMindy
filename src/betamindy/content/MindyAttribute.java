package betamindy.content;

import arc.util.*;
import mindustry.ctype.*;
import mindustry.world.meta.*;

public class MindyAttribute {
    public static final Attribute
    /** Heavy metal content. Used for blocks that make lead from scratch. */
    metallic = Attribute.add("metallic"),
    /** Magnetic coverage. Used for dynamo generators. */
    magnetic = Attribute.add("magnetic"),
    /** Blocks flagged with this attribute (>= 0.5f) cannot be pushed by pistons. */
    pushless = Attribute.add("pushless");
}
