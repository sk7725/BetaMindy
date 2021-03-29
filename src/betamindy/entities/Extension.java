package betamindy.entities;

import arc.func.*;
import arc.math.geom.*;
import arc.util.io.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;

/**
 * Same as {@link ForceDraw}, except it's not hardcoded only to serve force projectors. <br>
 * Won't add itself if the holder is null; always use {@code holder = ...} before calling {@link #add()}. <br>
 * <br>
 * See {@link ExtensionHolder} for usage and example codes.
*/
@SuppressWarnings("unchecked")
public final class Extension implements Drawc{
    public ExtensionHolder holder;

    public transient boolean added;
    public transient int id;

    public float x;
    public float y;

    protected Extension(){
        id = EntityGroup.nextId();
    }

    public static Extension create(){
        return new Extension();
    }

    @Override
    public int classId(){
        return MindyUnitTypes.classID(Extension.class);
    }

    @Override
    public void draw(){
        if(holder != null){
            holder.drawExt();
        }
    }

    @Override
    public float clipSize(){
        if(holder != null){
            return holder.clipSizeExt();
        }else{
            return 0f;
        }
    }

    @Override
    public void update(){}

    @Override
    public boolean serialize(){
        return false;
    }

    @Override
    public void add(){
        if(added || holder == null) return;

        Groups.all.add(this);
        Groups.draw.add(this);
        added = true;
    }

    @Override
    public void remove(){
        if(!added) return;

        Groups.all.remove(this);
        Groups.draw.remove(this);
        added = false;
    }

    @Override
    public boolean isLocal(){
        return ((Object)this) == player || ((Object)this) instanceof Unitc && ((Unitc)((Object)this)).controller() == player;
    }

    @Override
    public boolean isRemote(){
        return ((Object)this) instanceof Unitc && ((Unitc)((Object)this)).isPlayer() && !isLocal();
    }

    @Override
    public boolean isNull(){
        return false;
    }

    @Override
    public void write(Writes write){}

    @Override
    public void read(Reads read){
        afterRead();
    }

    @Override
    public void afterRead(){}

    @Override
    public Block blockOn(){
        Tile tile = tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    @Override
    public Floor floorOn(){
        Tile tile = tileOn();
        return tile == null || tile.block() != Blocks.air ? Blocks.air.asFloor() : tile.floor();
    }

    @Override
    public Tile tileOn(){
        return world.tileWorld(x, y);
    }

    @Override
    public int tileX(){
        return World.toTile(x);
    }

    @Override
    public int tileY(){
        return World.toTile(y);
    }

    @Override
    public boolean isAdded(){
        return added;
    }

    @Override
    public boolean onSolid(){
        Tile tile = tileOn();
        return tile == null || tile.solid();
    }

    @Override
    public int id(){
        return id;
    }

    @Override
    public void id(int id){
        this.id = id;
    }

    @Override
    public float x(){
        return x;
    }

    @Override
    public void x(float x){
        this.x = x;
    }

    @Override
    public float y(){
        return y;
    }

    @Override
    public void y(float y){
        this.y = y;
    }

    @Override
    public void set(Position pos){
        set(pos.getX(), pos.getY());
    }

    @Override
    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void trns(Position pos){
        trns(pos.getX(), pos.getY());
    }

    @Override
    public void trns(float x, float y){
        set(this.x + x, this.y + y);
    }

    @Override
    public float getX(){
        return x;
    }

    @Override
    public float getY(){
        return y;
    }

    @Override
    public <T extends Entityc> T self(){
        return (T)this;
    }

    @Override
    public <T> T as(){
        return (T)this;
    }

    @Override
    public <T> T with(Cons<T> cons){
        cons.get((T)this);
        return (T)this;
    }

    @Override
    public String toString(){
        return "Extension#" + id;
    }
}
