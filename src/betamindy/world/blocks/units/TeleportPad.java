package betamindy.world.blocks.units;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;

import static arc.Core.atlas;
import static mindustry.Vars.*;

/** Similar functionality and visuals to the pu's TeleUnit, but the code is 70% made from scratch.
 * This can support multiple instances and also uses a seq instead of a linked list due to pistons etc. frequently jumbling the order. This does mean that tp is O(n).
 * Does not teleport payloads, for this download pu!
 * @author sunny
 * @author younggam
 */

public class TeleportPad extends Block {
    private static boolean eventInit = false, nearBool = false;
    public static final OrderedSet<TeleportPadBuild> pads = new OrderedSet<>();

    public TextureRegion topRegion, lightRegion, arrowRegion;
    public Color disabledColor = Color.coral;
    public float heatLerp = 0.04f;
    public boolean animateNear = true;

    public Effect teleportIn = MindyFx.teleportUnit;
    public Effect teleportOut = Fx.teleportActivate;
    public Effect teleportUnit = Fx.none;
    public Sound inSound = Sounds.plasmadrop;
    public Sound outSound = Sounds.lasercharge2;

    public TeleportPad(String name){
        super(name);
        update = configurable = true;
        solid = false;
        lightColor = Pal.lancerLaser;
        lightRadius = 80f;
        if(!eventInit){
            eventInit = true;
            pads.orderedItems().ordered = false;
            Events.run(EventType.WorldLoadEvent.class, () -> {
                pads.clear();
                Log.info("[accent]<WLE>");
            });
        }
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        lightRegion = atlas.find(name + "-light");
        arrowRegion = atlas.find("transfer-arrow");
    }

    public class TeleportPadBuild extends Building {
        private boolean preLoad = true;
        public float heat, warmup;

        public void teleport(Unit unit, @Nullable Player p){
            TeleportPadBuild dest = nextPad(this);
            if(dest == this) return;
            if(!headless) teleportIn.at(unit.x, unit.y, unit.rotation, lightColor, unit.type);
            unit.set(dest.x, dest.y);
            unit.snapInterpolation();
            unit.set(dest.x, dest.y);
            if(!headless){
                effects(dest, unit.hitSize, p == player, unit);
                if(p == player){
                    Core.camera.position.set(dest.x, dest.y);
                    Core.app.post(() -> Core.camera.position.set(dest.x, dest.y));
                }
                dest.warmup = 1f;
                warmup = 1f;
            }
        }

        public TeleportPadBuild nextPad(TeleportPadBuild prev){
            if(pads.isEmpty()) return prev;
            //Log.info("NEXTPAD START"+pads.toString());
            Seq<TeleportPadBuild> arr = pads.orderedItems();
            int gid = prev.power == null ? -1 : prev.power.graph.getID();
            //Log.info("GID:"+gid);
            TeleportPadBuild dest = prev;
            int dpos = prev.pos();
            int ppos = dpos;
            for(int i = 0; i < arr.size; i++){
                TeleportPadBuild next = arr.get(i);
                //Log.info("NEXT:"+next);
                if(next.id != prev.id && next.team == prev.team && !next.dead() && next.enabled){
                    if(prev.power == null){
                        if(next.power == null && scorePos(ppos, next.pos(), dpos)){
                            dest = next;
                            dpos = next.pos();
                        }
                    }
                    else if(next.power != null && gid == next.power.graph.getID() && scorePos(ppos, next.pos(), dpos)){
                        dest = next;
                        dpos = next.pos();
                    }
                }
            }
            return dest;
        }

        public boolean scorePos(int prev, int next, int dest){
            if(dest > prev) return next < dest && next > prev;
            return next < dest || next > prev;
        }

        @Override
        public void created(){
            super.created();
            //Log.info("CREATED:" + id);
            pads.add(this);
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            pads.remove(this);
        }

        @Override
        public void updateTile(){
            if(preLoad){ //WLE is called after created(), so re-invoke this after WLE once
                //Log.info("UPDATE-CREATED:" + id);
                pads.add(this);
                preLoad = false;
            }

            heat = Mathf.lerpDelta(heat, canConsume() ? 1f : 0f, heatLerp);
            if(animateNear && !headless){
                nearBool = false;
                Units.nearby(team, x, y, 80f, u -> {
                    if(nearBool) return;
                    if(u.isPlayer() || !u.isFlying()) nearBool = true;
                });
                warmup = Mathf.lerpDelta(warmup, nearBool ? 1f : 0f, 0.05f);
            }
        }

        @Override
        public void unitOn(Unit unit){
            if(!canConsume()) return;
            if(unit.hasEffect(MindyStatusEffects.ouch) || unit.isPlayer()) return;
            unit.apply(MindyStatusEffects.ouch, 120f);
            teleport(unit, null);
        }

        protected void effects(TeleportPadBuild dest, float hitSize, boolean isPlayer, Unit unit){
            if(isPlayer){
                inSound.at(dest, Mathf.random() * 0.2f + 1f);
                outSound.at(this, Mathf.random() * 0.2f + 0.7f);
            }else{
                inSound.at(this, Mathf.random() * 0.2f + 1f);
                outSound.at(dest, Mathf.random() * 0.2f + 0.7f);
            }
            teleportOut.at(dest.x, dest.y, hitSize, lightColor);
            if(teleportUnit != Fx.none) teleportUnit.at(dest.x, dest.y, 0f, lightColor, unit);
        }

        protected boolean inRange(Player player){
            return player.unit() != null && player.unit().isValid() && Math.abs(player.unit().x - x) <= 2.5f * tilesize && Math.abs(player.unit().y - y) <= 2.5f * tilesize;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.blend(Blending.additive);
            Draw.color(lightColor, (Mathf.absin(11f, 0.5f) + 0.5f) * heat);
            Draw.rect(topRegion, x, y);
            Draw.blend();

            if(heat > 0.01f){
                Draw.z(Layer.bullet);
                Draw.color(enabled ? lightColor : disabledColor, heat);
                Draw.rect(lightRegion, x, y);
                if(animateNear && warmup > 0.01f){
                    Draw.color(lightColor);
                    Lines.stroke((Mathf.absin(17f, 1f) + 0.5f) * warmup * heat);
                    Lines.square(x, y, 8.5f, Time.globalTime / 2f);
                    Lines.square(x, y, 8.5f, -1 * Time.globalTime / 2f);
                }
            }
            Draw.color();
        }

        @Override
        public void drawSelect(){
            Draw.color(canConsume() ? (inRange(player) ? Color.orange : Pal.accent) : Pal.darkMetal);
            float length = tilesize * size / 2f + 3f + Mathf.absin(5f, 2f);
            Draw.rect(arrowRegion, x + length, y, 180f);
            Draw.rect(arrowRegion, x, y + length, 270f);
            Draw.rect(arrowRegion, x - length, y, 0f);
            Draw.rect(arrowRegion, x, y - length, 90f);
            Draw.color();
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius * (animateNear ? 0.5f + 0.5f * warmup : 1f), lightColor, 0.8f * heat);
        }

        @Override
        public boolean canConsume(){
            return power == null ? enabled : power.status > 0.98f;
        }

        @Override
        public void configured(Unit builder, Object value){
            if(builder != null && builder.isPlayer() && !(builder instanceof BlockUnitc)) teleport(builder, builder.getPlayer());
        }

        @Override
        public boolean shouldShowConfigure(Player player){
            return canConsume() && inRange(player);
        }

        @Override
        public boolean configTapped(){
            if(!canConsume() || !inRange(player)) return false;
            configure(null);
            Sounds.click.at(this);
            return false;
        }

        @Override
        public boolean canPickup(){
            return false;
        }
    }
}
