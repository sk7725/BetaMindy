package betamindy.util;

import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

//exists to provide functions that are not public in the Mobile version, for some reason.

public class MobileFunctions {
    public final int payloadUnit = 0, payloadBlock = 1;

    public void writePayload(@Nullable Payload payload, Writes write){
        if(payload == null){
            write.bool(false);
        }else{
            write.bool(true);
            payload.write(write);
        }
    }

    @Nullable
    public <T extends Payload> T readPayload(Reads read){
        boolean exists = read.bool();
        if(!exists) return null;

        byte type = read.b();
        if(type == payloadBlock){
            Block block = Vars.content.block(read.s());
            BuildPayload payload = new BuildPayload(block, Team.derelict);
            byte version = read.b();
            payload.build.readAll(read, version);
            return (T)payload;
        }else if(type == payloadUnit){
            byte id = read.b();
            if(EntityMapping.map(id) == null) throw new RuntimeException("No type with ID " + id + " found.");
            Unit unit = (Unit)EntityMapping.map(id).get();
            unit.read(read);
            return (T)new UnitPayload(unit);
        }
        throw new IllegalArgumentException("Unknown payload type: " + type);
    }
}
