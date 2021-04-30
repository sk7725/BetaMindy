package betamindy.world.blocks.production.payduction;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.production.*;
import betamindy.world.blocks.production.payduction.GateController.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.consumers.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class PayloadFactory extends PayloadAcceptor {
    public int maxBlockSize = 2;
    public float fuelTime = 120f;
    public float baseCraftTime = 5f, baseHeatLerp = 0.001f;
    /** Damage taken by the container every tick. */
    public float damageAmount = 0.01f;
    /** Damage the factory takes if the container perishes, scaled with size. */
    public float selfDamageAmount = 100f;

    public float minItemEfficiency = 0.5f;

    public Effect catalystEffect = Fx.none;
    public Effect smokeEffect = Fx.fuelburn;
    public float smokeChance = 0.01f;
    public Effect releaseEffect = Fx.none; //TODO heated effect
    public Sound catalystSound = Sounds.combustion;
    public Sound releaseSound = Sounds.steam;

    public Color heatColor = Pal.lightPyraFlame;
    public TextureRegion shadowRegion, heatRegion, doorRegion;
    public float doorSize = 0.75f;

    /** How long should the factory keep crafting a payload before ejecting it, if no GateControllers are connected. GateControllers will always override this. Intended to be a shitty value. */
    public float autoOutputTime = 30 * 60f;

    /** Used in UI only. */
    public float maxHeat = 2f;

    public boolean defaults = false;

    public PayloadFactory(String name){
        super(name);

        update = true;
        hasItems = true;
        acceptsItems = true;
        outputsPayload = true;
        rotate = true;
        sync = true;
        ambientSound = Sounds.smelter;

        setDefaults();
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, doorRegion, inRegion, outRegion, topRegion};
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();

        bars.add("heat", entity -> new Bar(() -> Core.bundle.format("bar.efficiency", (int)(((PayloadFactoryBuild)entity).heat * 100)), () -> heatColor, ((PayloadFactoryBuild) entity)::fract));
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(region, req.drawx(), req.drawy());
        Draw.rect(doorRegion, req.drawx(), req.drawy());
        Draw.rect(outRegion, req.drawx(), req.drawy(), req.rotation * 90);
        Draw.rect(topRegion, req.drawx(), req.drawy());
    }

    protected void setDefaults(){
        if(hasItems){
            consumes.add(new ConsumeItemFilter(item -> getFuelValue(item) >= minItemEfficiency)).update(false).optional(true, false);
        }

        /*
        if(hasLiquids){
            consumes.add(new ConsumeLiquidFilter(liquid -> getLiquidEfficiency(liquid) >= minLiquidEfficiency, maxLiquidGenerate)).update(false).optional(true, false);
        }*/

        defaults = true;
    }

    public float getFuelTime(Item item){
        return fuelTime;
    }

    public float getFuelValue(Item item){
        return 0f; //base fuel value is 1
    }

    public float getFuelLerp(Item item){
        return baseHeatLerp;
    }

    @Override
    public void init(){
        if(!defaults){
            setDefaults();
        }
        super.init();
    }

    @Override
    public void load(){
        super.load();
        shadowRegion = atlas.find(name + "-shadow");
        heatRegion = atlas.find(name + "-glow");
        doorRegion = atlas.find(name + "-door");
    }

    public class PayloadFactoryBuild extends PayloadAcceptorBuild<BuildPayload>{
        public float heat = 0f; //absolute heat value
        public float fuelLeft = 0f, fuelValue = 0f, fuelLerp = baseHeatLerp;
        public float time = 0f; //increases every tick when payload is in.
        public float progress = 0f;
        public int cycle = 0;

        public float doors = 0f;
        public boolean outputting = false;

        public @Nullable GateControllerBuild gate;

        public void fuelUse(){
            //consume the item
            fuelLeft -= delta();
            if(fuelLeft > 0) return;
            if(items.total() == 0 || !consValid()){
                fuelValue = 0;
                fuelLerp = baseHeatLerp;
                return;
            }

            Item item = items.first();
            fuelLeft = getFuelTime(item);
            fuelValue = getFuelValue(item);
            fuelLerp = getFuelLerp(item);
            items.remove(item, 1);
        }

        public void craft(Building b){
            cycle++;
        }

        public void catalyst(Block b){
        }

        public boolean shouldOutput(){
            return (gate != null && gate.isValid()) ? gate.open() : time >= autoOutputTime;
        }

        public boolean isCatalyst(Block b){
            return false;
        }

        /** Used for UI. Already innately calculated otherwise. */
        public float craftTime(){
            return baseCraftTime / efficiency();
        }

        public float fract(){ return Mathf.clamp(heat / maxHeat); }

        public boolean active(){ return payload != null && !outputting && time > 5f; }

        public void setGate(GateControllerBuild g){
            if(gate == null || !gate.isValid()) gate = g;
        }

        @Override
        public float efficiency(){
            return super.efficiency() * heat;
        }

        @Override
        public void updateTile(){
            fuelUse();
            heat = Mathf.approachDelta(heat, payload == null ? 0.5f * fuelValue : fuelValue, fuelLerp);
            super.updateTile();

            if(Mathf.chance(smokeChance * heat)) smokeEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));

            if(payload != null){
                if(outputting){
                    moveOutPayload();
                    if(payload == null) outputting = false;
                }
                else if(consValid() && moveInPayload()){
                    if(isCatalyst(payload.block())){
                        catalystEffect.at(this, payload.block().size);
                        catalystSound.at(this);
                        catalyst(payload.block());
                        payload = null;
                        return;
                    }

                    if(time == 0){
                        //just got in
                        progress = 0;
                        cycle = 0;
                    }

                    time += delta();
                    progress += edelta();
                    if(progress > baseCraftTime){
                        progress %= baseCraftTime;
                        craft(payload.build);

                        float d = baseCraftTime * damageAmount;

                        if(d + 1f >= payload.build.health){
                            Fx.blockExplosion.at(this);
                            Fx.smokeCloud.at(this);
                            Sounds.explosion.at(this);

                            damage(payload.block().size * selfDamageAmount);
                            payload = null;
                            time = 0;
                            return;
                        }else payload.build.health -= d;
                    }

                    if(shouldOutput()){
                        outputting = true;
                        releaseEffect.at(x, y, 0f, payload);
                        releaseSound.at(this);
                        time = 0;
                    }
                }
            }
            else{
                outputting = false;
                time = 0;
            }

            doors = Mathf.lerpDelta(doors, active() ? 1f : 0f, 0.05f);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            if(!(payload instanceof BuildPayload)) return false;
            BuildPayload build = (BuildPayload) payload;
            return super.acceptPayload(source, payload) && ((build.block() instanceof StorageBlock) || isCatalyst(build.block())) && build.block().size <= maxBlockSize;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }

            Draw.rect(outRegion, x, y, rotdeg());

            payRotation = rotdeg();
            drawPayload();

            float f = Mathf.clamp(heat / maxHeat * (Mathf.absin(7f, 0.2f) + 1f));

            Draw.z(Layer.blockOver + 0.05f);
            Draw.color(Pal.shadow, 0.22f * (1-f));
            Draw.rect(shadowRegion, x, y);

            Draw.blend(Blending.additive);
            Draw.color(heatColor, f);
            Draw.rect(shadowRegion, x, y);
            Draw.blend();
            Draw.color();

            if(doors > 0.99f){
                Draw.rect(doorRegion, x, y);
            }
            else if(doors > 0.001f){
                f = doorSize + (1f - doorSize) * doors;
                Draw.rect(doorRegion, x, y, (float)doorRegion.width * Draw.scl * Draw.xscl * f, (float)doorRegion.height * Draw.scl * Draw.yscl * f);
            }
            Draw.rect(topRegion, x, y);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            if(gate != null && gate.isValid()){
                Lines.stroke(1f, Pal.accent);
                Lines.square(gate.x, gate.y, gate.block.size * tilesize / 2f);
                Draw.color();
            }
            else if(active()){
                int sec = (int)((autoOutputTime - time) / 60f) + 1;
                drawPlaceText(sec / 60 + ":" + String.format("%02d", sec % 60), tile.x, tile.y, true);
            }
        }
    }
}
