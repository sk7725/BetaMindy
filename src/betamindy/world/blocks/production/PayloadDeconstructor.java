package betamindy.world.blocks.production;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.graphics.*;
import betamindy.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;

public class PayloadDeconstructor extends PayloadBlock {
    /** The additional refundmultiplier multiplied to the refundmultiplier of the map. Note that the final refund multiplier will not exceed 1, unless the map's refund is already over 1. */
    public float refundMultiplier = 1.5f;
    public float buildSpeed = 0.6f;
    public float maxPaySize = 2.5f;
    public Color deconstructColor = Pal.remove;
    public Effect finishEffect = Fx.none;

    public final ItemStack[] defaultStack = {new ItemStack(Items.scrap, 25)};


    public PayloadDeconstructor(String name){
        super(name);

        solid = true;
        rotate = false;
        outputFacing = false;
        outputsPayload = true;
        hasItems = true;
    }

    public float realMultiplier(){
        return Math.max(1f, Vars.state.rules.deconstructRefundMultiplier);
        /*
        if(Vars.state.rules.deconstructRefundMultiplier > 1f) return Vars.state.rules.deconstructRefundMultiplier * refundMultiplier;
        return Math.min(1f, Vars.state.rules.deconstructRefundMultiplier * refundMultiplier);
        */
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("progress", (PayloadDeconBuild entity) -> new Bar("bar.progress", Pal.ammo, entity::fraction));
    }

    @Override
    public void setStats(){
        super.setStats();
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    @Override
    public void init(){
        super.init();
    }

    public ItemStack[] payloadCost(Payload pay){
        if(pay instanceof BuildPayload){
            return ((BuildPayload)pay).block().requirements;
        }
        else if(pay instanceof UnitPayload){
            return ((UnitPayload)pay).unit.type.getTotalRequirements();
        }
        return defaultStack;
    }

    public class PayloadDeconBuild<T extends Payload> extends PayloadBlockBuild<T> {
        public float progress, time, heat;
        //public int lastRot = 0;

        public float fraction(){
            return payload == null ? 0f : progress / totalProgress();
        }

        public float totalProgress(){
            if(payload instanceof BuildPayload){
                return ((BuildPayload)payload).block().buildCost;
            }
            else return payload.size() * 18f;
        }

        @Override
        public void updateTile(){
            boolean produce = payload != null && moveInPayload() && canConsume() && items.total() < itemCapacity;

            if(produce){
                progress += buildSpeed * edelta();

                if(progress >= totalProgress()){
                    consume();
                    finishEffect.at(this, payload.size());
                    ItemStack[] costs = payloadCost(payload);
                    for(ItemStack stack : costs){
                        items.add(stack.item, (int)(stack.amount * realMultiplier()));
                    }
                    payload = null;
                    progress = 0f;
                }
            }

            heat = Mathf.lerpDelta(heat, Mathf.num(produce), 0.3f);
            time += heat * edelta();

            if(timer.get(timerDump, dumpTime)) dump();
        }

        /*public void deconstruct(Unit builder, @Nullable Building core, float amount){
            wasConstructing = false;
            activeDeconstruct = true;
            float deconstructMultiplier = state.rules.deconstructRefundMultiplier;

            if(builder.isPlayer()){
                lastBuilder = builder;
            }

            if(cblock != null){
                ItemStack[] requirements = cblock.requirements;
                if(requirements.length != accumulator.length || totalAccumulator.length != requirements.length){
                    setDeconstruct(cblock);
                }

                //make sure you take into account that you can't deconstruct more than there is deconstructed
                float clampedAmount = Math.min(amount, progress);

                for(int i = 0; i < requirements.length; i++){
                    int reqamount = Math.round(state.rules.buildCostMultiplier * requirements[i].amount);
                    accumulator[i] += Math.min(clampedAmount * deconstructMultiplier * reqamount, deconstructMultiplier * reqamount - totalAccumulator[i]); //add scaled amount progressed to the accumulator
                    totalAccumulator[i] = Math.min(totalAccumulator[i] + reqamount * clampedAmount * deconstructMultiplier, reqamount);

                    int accumulated = (int)(accumulator[i]); //get amount

                    if(clampedAmount > 0 && accumulated > 0){ //if it's positive, add it to the core
                        if(core != null && requirements[i].item.unlockedNow()){ //only accept items that are unlocked
                            int accepting = Math.min(accumulated, ((CoreBuild)core).storageCapacity - core.items.get(requirements[i].item));
                            //transfer items directly, as this is not production.
                            core.items.add(requirements[i].item, accepting);
                            accumulator[i] -= accepting;
                        }else{
                            accumulator[i] -= accumulated;
                        }
                    }
                }
            }

            progress = Mathf.clamp(progress - amount);

            if(progress <= (previous == null ? 0 : previous.deconstructThreshold) || state.rules.infiniteResources){
                if(lastBuilder == null) lastBuilder = builder;
                Call.deconstructFinish(tile, this.cblock == null ? previous : this.cblock, lastBuilder);
            }
        }*/

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input
            for(int i = 0; i < 4; i++){
                if(blends(i)){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }

            if(constructing() && hasArrived()){
                Draw.draw(Layer.blockOver, () -> {
                    float constructTime = totalProgress();
                    Drawm.construct(this, payloadIcon(), payRotation - 90f, 1f - progress / constructTime, heat, time, deconstructColor);
                });
            }else{
                Draw.z(Layer.blockOver);
                //payRotation = rotdeg();

                drawPayload();
                if(heat > 0.001f){
                    Draw.draw(Layer.blockOver, () -> {
                        //aesthetics
                        Drawm.construct(this, Core.atlas.white(), 0f, 0f, heat, time, deconstructColor);
                    });
                }
            }

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
        }

        public TextureRegion payloadIcon(){
            if(payload instanceof BuildPayload){
                return ((BuildPayload)payload).build.block.fullIcon;
            }
            else if(payload instanceof UnitPayload){
                return ((UnitPayload)payload).unit.type().fullIcon;
            }
            return Core.atlas.find("error");
        }

        @Override
        public boolean shouldConsume(){
            return constructing();
        }

        public boolean constructing(){
            return payload != null;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return super.acceptPayload(source, payload) && payload.size() / Vars.tilesize <= maxPaySize;
        }

        @Override
        public @Nullable Payload takePayload(){
            return null;
        }

        @Override
        public void onRemoved(){
            payload = null;
            super.onRemoved();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
        }
    }
}
