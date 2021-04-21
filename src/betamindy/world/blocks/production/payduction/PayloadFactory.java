package betamindy.world.blocks.production.payduction;

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
    public float baseCraftTime = 60f, baseHeatLerp = 0.01f;
    /** Damage taken by the container every tick. */
    public float damageAmount = 0.1f;
    /** Damage the factory takes if the container perishes, scaled with size. */
    public float selfDamageAmount = 500f;

    public float minItemEfficiency = 0.5f;

    public Effect catalystEffect = Fx.none;
    public Effect smokeEffect = Fx.fuelburn;
    public float smokeChance = 0.01f;
    public Effect releaseEffect; //TODO heated effect
    public Sound catalystSound = Sounds.combustion;
    public Sound releaseSound = Sounds.steam;

    public Color heatColor = Pal.turretHeat;
    public TextureRegion shadowRegion, heatRegion;

    /** How long should the factory keep crafting a payload before ejecting it, if no GateControllers are connected. GateControllers will always override this. Intended to be a shitty value. */
    public float autoOutputTime = 30 * 60f;

    public boolean defaults = false;

    public PayloadFactory(String name){
        super(name);

        update = true;
        hasItems = true;
        acceptsItems = true;
        outputsPayload = true;
        outputFacing = true;
        sync = true;
        ambientSound = Sounds.smelter;

        setDefaults();
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, inRegion, outRegion, topRegion};
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setBars(){
        super.setBars();

        bars.add("progress", entity -> new Bar("bar.heat", heatColor, () -> ((PayloadFactoryBuild)entity).heat));
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(region, req.drawx(), req.drawy());
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
        shadowRegion = atlas.find(name + "-shadpw");
        heatRegion = atlas.find(name + "-heat");
    }

    public class PayloadFactoryBuild extends PayloadAcceptorBuild<BuildPayload>{
        public float heat = 0f; //absolute heat value
        public float fuelLeft = 0f, fuelValue = 0f, fuelLerp = 0.01f;
        public float time = 0f; //increases every tick when payload is in.
        public float progress = 0f;
        public int cycle = 0;
        public boolean outputting = false;

        public @Nullable GateControllerBuild gate; //TODO

        public void fuelUse(){
            //consume the item
            if(fuelLeft > 0) return;
            if(items.total() == 0 || !consValid()){
                fuelValue = 0;
                fuelLerp = baseHeatLerp;
                return;
            }

            Item item = items.take();
            fuelLeft = getFuelTime(item);
            fuelValue = getFuelValue(item);
            fuelLerp = getFuelLerp(item);
        }

        public void craft(Building b){
            cycle++;
        }

        public void catalyst(Block b){

        }

        public boolean shouldOutput(){
            return (gate != null) ? gate.open() : time >= autoOutputTime;
        }

        public boolean isCatalyst(Block b){
            return false;
        }

        /** Used for UI. Already innately calculated otherwise. */
        public float craftTime(){
            return baseCraftTime / efficiency();
        }

        @Override
        public float efficiency(){
            return super.efficiency() * heat;
        }

        @Override
        public void updateTile(){
            fuelUse();
            heat = Mathf.lerpDelta(heat, payload == null ? 0.5f * fuelValue : fuelValue, fuelLerp);
            super.updateTile();

            if(Mathf.chance(smokeChance * heat)) smokeEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));

            if(payload != null && consValid() && moveInPayload()){
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

                if(outputting){
                    moveOutPayload();
                    return;
                }

                time += delta();
                progress += edelta();
                if(progress > baseCraftTime){
                    progress %= baseCraftTime;
                    craft(payload.build);

                    payload.build.damage(baseCraftTime * damageAmount);
                    if(payload.build.health <= 0f){
                        Fx.blockExplosion.at(this);
                        Fx.smokeCloud.at(this);
                        Sounds.explosion.at(this);

                        damage(payload.block().size * selfDamageAmount);
                        payload = null;
                        time = 0;
                        return;
                    }
                }

                if(shouldOutput()){
                    outputting = true;
                    releaseEffect.at(x, y, 0f, payload);
                    releaseSound.at(this);
                    time = 0;
                }
            }
            else{
                if(payload == null) outputting = false;
            }
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

            Draw.z(Layer.blockOver);
            payRotation = rotdeg();
            drawPayload();

            float f = Mathf.clamp(heat / 2f * (Mathf.absin(7f, 0.2f) + 1f));

            Draw.z(Layer.blockOver + 0.1f);
            Draw.color(Pal.shadow, 0.22f * (1-f));
            Draw.rect(shadowRegion, x, y);
            Draw.color(heatColor, f);
            Draw.rect(heatRegion, x, y);
            Draw.color();
            Draw.rect(topRegion, x, y);
        }
    }
}
