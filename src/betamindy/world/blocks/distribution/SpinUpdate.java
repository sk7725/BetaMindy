package betamindy.world.blocks.distribution;

import betamindy.world.blocks.payloads.*;

/** A building that does stuff while spinning. Sizes over 1 are not supported but not unsafe. */
public interface SpinUpdate {
    void spinUpdate(float sx, float sy, float srad, float absRot, float rawRot);
}
