package betamindy.util;

import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

//exists to provide functions that are not public in the Mobile version, for some reason.
@Deprecated //TODO remove later
public class MobileFunctions {
    @Deprecated
    public void writePayload(@Nullable Payload payload, Writes write){
        TypeIO.writePayload(write, payload);
    }

    @Nullable
    @Deprecated
    public <T extends Payload> T readPayload(Reads read){
        return (T)TypeIO.readPayload(read);
    }
}
