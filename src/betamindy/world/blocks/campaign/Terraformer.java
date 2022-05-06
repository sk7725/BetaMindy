package betamindy.world.blocks.campaign;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.ui.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static betamindy.graphics.Drawm.*;
import static mindustry.Vars.*;

public class Terraformer extends Block {
    public static final String[] romans = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "SUS", "SUSI", "SUSII", "SUSIII", "AMOGUS", "AMOGI"};
    public static final float drawLimit = 6500f;
    private static final Color tmpc = new Color();
    public final ObjectMap<Block, Block> terraFloors = new ObjectMap<>(); //these will be checked on floors only
    public final ObjectMap<Block, Block> terraBlocks = new ObjectMap<>(); //blocks only
    public Floor[] ores; //these will be placed on the overlay level

    public int tier = 1;
    public int tileWidth = 12; //width / 2 of the zigzag line of the terraform
    public float maxCharge = -1;
    public float powerUse = 720f;
    public int maxUses = 3;
    public float threshMultiplier = 0.89f; //the lower, the more ores it generates. [0~1], 0 just fills the entire thing with ore

    public float animDuration = 700f, midDuration = 25f, shakeAmount = 5f, ringAlignDuration = 200f, endDuration = 150f;
    public Effect explodeEffect = Fx.shockwave;//todo
    public Effect beamEffect = MindyFx.terraBeam;
    public Effect beamSmokeEffect = Fx.none;
    public float beamEffectChance = 0.08f, beamSmokeChance = 0.1f;
    public float ringRadius = 25f, ringWidth = 9f, ringThickness = 0.4f;
    public Color orbColor = Color.coral;

    public Terraformer(String name, int tier){
        super(name);
        size = 7;
        solid = true;
        update = true;
        configurable = true;
        hasPower = true;
        this.tier = tier;
        clipSize = drawLimit * 2f;
        localizedName = "[#84ff00]" + Core.bundle.get("block." + this.name + ".name", Core.bundle.get("block.betamindy-terraformer.name") + ((tier >= romans.length || tier <= 0) ? "" : " " + romans[tier])) + "[]";
    }

    @Override
    public void init(){
        consumePowerCond(powerUse, TerraformerBuild::isCharging);
        super.init();
        if(maxCharge < 0f) maxCharge = (float) (400000 << tier);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("power");
        addBar("charge", (TerraformerBuild entity) -> new Bar(() -> Core.bundle.format("bar.terraformer.charge", (int)entity.displayCharge()), () -> lightColor, () -> entity.displayCharge() / maxCharge));
        addBar("uses", (TerraformerBuild entity) -> new Bar(() -> Core.bundle.format("bar.uses", maxUses - entity.used), () -> Pal.accent, () -> ((float) maxUses - entity.used) / maxUses));
    }


    @Override
    public boolean canBreak(Tile tile){
        return super.canBreak(tile) && (tile.build instanceof TerraformerBuild terra) && !terra.terraforming && terra.used < maxUses;
    }

    public class TerraformerBuild extends Building {
        public boolean charging = false; //when true, starts charging & consuming
        public float charge = 0f; //amount of power stored
        public boolean terraforming = false;
        public int used = 0;
        public float heat = 0f;

        //below are only valid when terraforming = true
        private int progress = 0;
        private float startTime, tileOffset;
        private boolean horizontal;
        private final int[] oreSeeds = new int[ores.length];

        public void terraformStart(){
            progress = 0;
            startTime = Time.time;
            setLoadSeed(used);
            used++;
        }

        public void setLoadSeed(int used){
            tileOffset = Mathf.randomSeedRange(pos() - tier - used * 16L, 0.6f);
            horizontal = Mathf.randomSeed(pos() - tier - 69420) > 0.5f;
            if(used % 2 == 1) horizontal = !horizontal;
            for(int i = 0; i < ores.length; i++){
                oreSeeds[i] = Mathf.randomSeed(tier * 256L + i, 0, Integer.MAX_VALUE / 2);//MAX_VALUE kills the rand
            }
        }

        public void terraformLine(int offset){
            if(horizontal){
                int min = -tile.y;
                int max = world.height() - tile.y;

                for(int i = min; i <= max; i++){
                    Tile t = world.tile(tile.x + (int)(tileOffset * i) + offset, tile.y + i);
                    if(t != null) terraform(t);
                }
            }
            else{
                int min = -tile.x;
                int max = world.width() - tile.x;

                for(int i = min; i <= max; i++){
                    Tile t = world.tile(tile.x + i, tile.y + (int)(tileOffset * i) + offset);
                    if(t != null) terraform(t);
                }
            }
        }

        public void terraform(Tile t){
            if(terraFloors.containsKey(t.floor())){
                t.setFloorUnder((Floor) terraFloors.get(t.floor()));
            }
            if(terraBlocks.containsKey(t.block())){
                t.setBlock(terraBlocks.get(t.block()));
            }

            if(t.floor().isLiquid) return;
            if(t.overlay() == Blocks.tendrils) t.setOverlay(Blocks.air);
            if(t.overlay() == Blocks.air || (t.overlay() instanceof OreBlock ob) && ob.itemDrop != null && ob.itemDrop.hardness <= ores[0].itemDrop.hardness){
                for(int i = 0; i < ores.length; i++){
                    if(Simplex.noise2d(oreSeeds[i], 2, 0.3f, 1 / ores[i].oreScale, t.x + oreSeeds[i], t.y + oreSeeds[i]) >= ores[i].oreThreshold * threshMultiplier){
                        t.setOverlay(ores[i]);
                    }
                }
            }
        }

        public void flashWhite(){
            if(!headless){
                Image white = new Image();
                white.touchable = Touchable.disabled;
                white.setColor(1f, 1f, 1f, 1f);
                white.setFillParent(true);
                white.actions(Actions.fadeOut(1), Actions.remove());
                white.update(() -> {
                    if(!Vars.state.isGame()){
                        white.remove();
                    }
                });
                Core.scene.add(white);
            }
        }

        public boolean isCharging(){
            return !terraforming && charging && charge < maxCharge;
        }

        public float displayCharge(){
            if(terraforming) return maxCharge;
            if(charging) return Math.min(maxCharge, charge);
            return 0f;
        }

        public float laserAngle(){
            if(tileOffset == 0f) return horizontal ? 90f : 0f;
            return horizontal ? Mathf.angle(tileOffset, 1f) : Mathf.angle(1f, tileOffset);
        }

        @Override
        public void updateTile(){
            if(terraforming){
                if(Time.time - startTime > animDuration + progress * midDuration){
                    if(progress > tileWidth){
                        if(Time.time - startTime > animDuration + progress * midDuration + endDuration){
                            terraforming = false;
                            charge = 0f;
                            return;
                        }
                    }
                    else{
                        //start changing tiles
                        if(progress == 0){
                            flashWhite();
                            terraformLine(0);
                        }
                        else{
                            terraformLine(progress);
                            terraformLine(-progress);
                        }

                        progress++;
                    }
                }

                if(!headless && Time.time - startTime > animDuration){
                    Effect.shake(shakeAmount, shakeAmount, Core.camera.position); //always shake
                    if(Time.time - startTime < animDuration + tileWidth * midDuration){
                        if(Mathf.chanceDelta(beamEffectChance)){
                            float a = laserAngle();
                            Tmp.v1.trns(a + 90f, Mathf.range(tileWidth * tilesize * 0.6f)).add(this);
                            beamEffect.at(Tmp.v1.x, Tmp.v1.y, a, Mathf.randomBoolean() ? lightColor : orbColor);
                        }
                    }
                }
                return;
            }
            else if(used >= maxUses){
                //blow up
                charge += Time.delta * 0.002f;
                if(charge >= 1f){
                    kill();
                    return;
                }
            }
            else if(charging && charge < maxCharge){
                if(canConsume()) charge += delta() * power.status * powerUse;
                else charge -= delta() * 0.5f;
            }

            heat = Mathf.lerpDelta(heat, terraforming || canConsume() ? 1f : 0f, 0.05f);
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            if(terraforming) drawLaser();
            else if(charging){
                //draw the 5 ellipse thingy
                Draw.z(Layer.bullet - 1f);
                Draw.blend(Blending.additive);
                float f = charge / maxCharge;
                Lines.stroke(ringWidth * Mathf.clamp(f * 3f) * ringThickness);
                Draw.color(Color.white, lightColor, Mathf.clamp(f * 3f));
                Draw.alpha(0.8f);
                ellipse(x, y, ringRadius, 1f, Mathf.absin(9f, 1f) + 0.001f, Time.time / 6f, Layer.bullet - 1f, Layer.effect + 1f);
                ellipse(x, y, ringRadius, 1f, Mathf.absin(Time.time * -1f, 7f, 1f) + 0.001f, -Time.time / 5f, Layer.bullet - 1f, Layer.effect + 1f);
                if(f > 0.33f){
                    float f2 = Mathf.clamp((f - 0.33f) * 3f);
                    Lines.stroke(ringWidth * f2 * ringThickness);
                    Draw.color(Color.white, orbColor, f2);
                    Draw.alpha(0.8f);
                    ellipse(x, y, ringRadius + ringWidth, 1f, Mathf.absin(11f, 1f) + 0.001f, 0f, Layer.bullet - 1f, Layer.effect + 1f);
                    ellipse(x, y, ringRadius + ringWidth, 1f, Mathf.absin(Time.time * -1f, 13f, 1f) + 0.001f, 90f, Layer.bullet - 1f, Layer.effect + 1f);
                    if(f > 0.66f){
                        f2 = Mathf.clamp((f - 0.66f) * 3f);
                        Lines.stroke(ringWidth * f2 * ringThickness);
                        Draw.color(Color.white, lightColor, f2);
                        Draw.alpha(0.8f);
                        ellipse(x, y, ringRadius + ringWidth * 2f, 1f, Mathf.absin(6.1f, 1f) + 0.001f, Time.time / 3.7f, Layer.bullet - 1f, Layer.effect + 1f);
                    }
                }
                Draw.blend();
                Drawm.altarOrb(x, y, 11f, f, 45f, orbColor, 4);
            }
            Draw.reset();
        }

        public void drawLaser(){
            if(Time.time - startTime > animDuration){
                //start laser
                float ow = tileWidth * tilesize * (1f - Mathf.clamp((Time.time - startTime - animDuration - tileWidth * midDuration) / endDuration)) / 1.5f;
                float angle = laserAngle();
                int n = 6;

                float w = ow + ow * Mathf.absin(1.5f, 0.1f);
                float w2 = ow + ow * Mathf.absin(1.5f, 0.05f);

                Tmp.c2.set(orbColor).lerp(lightColor, Mathf.absin(Time.globalTime - n * 7f, 6f, 1f));

                Draw.z(Layer.effect);
                Lines.stroke(w2 * 2, Color.black);
                Lines.lineAngleCenter(this.x, this.y, angle, drawLimit);

                float space = ow / 2f / n;

                Draw.z(Layer.effect + 1);
                for(int i = 0; i < n; i++){
                    Lines.stroke(space);
                    Draw.color(Color.black, Tmp.c2.set(orbColor).lerp(lightColor, Mathf.absin(Time.globalTime - i * 7, 6, 1)), (i + 1f) / n);
                    Tmp.v1.trns(angle + 90, w / 2 + space * i).add(this);
                    Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, drawLimit);
                    Tmp.v1.trns(angle - 90, w / 2 + space * i).add(this);
                    Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, drawLimit);
                }

                Draw.z(Layer.effect);
                Lines.stroke(space * 1.5f, Tmp.c2);
                Tmp.v1.trns(angle + 90, w2).add(this);
                Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, drawLimit);
                Tmp.v1.trns(angle - 90, w2).add(this);
                Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, drawLimit);

                Draw.z(Layer.effect + 1);
                Draw.blend(Blending.additive);
                Lines.stroke(space * 5, Tmp.c2);
                Draw.alpha(0.35f);
                Tmp.v1.trns(angle + 90, w2 + space * 6).add(this);
                Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, drawLimit);
                Tmp.v1.trns(angle - 90, w2 + space * 6).add(this);
                Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, drawLimit);

                Lines.stroke(space * 3, Tmp.c2);
                Draw.alpha(0.65f);
                Tmp.v1.trns(angle + 90, w2 + space * 2).add(this);
                Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, drawLimit);
                Tmp.v1.trns(angle - 90, w2 + space * 2).add(this);
                Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, drawLimit);
                //end laser

                Lines.stroke(space * 7, Tmp.c2);
                Drawm.ellipse(x, y, ow + space * 18f, 1f, 0.3f, angle + 90f, Layer.bullet - 1.1f, Layer.effect + 1.1f);
                Lines.stroke(space * 10, Tmp.c2);
                Draw.alpha(0.2f);
                Drawm.ellipse(x, y, ow + space * 18f, 1f, 0.3f, angle + 90f, Layer.bullet - 1.1f, Layer.effect + 1.1f);
                Draw.blend();
                Lines.stroke(space * 4, Color.black);
                Drawm.ellipse(x, y, ow + space * 18f, 1f, 0.3f, angle + 90f, Layer.bullet - 1.1f, Layer.effect + 1.1f);

                Draw.reset();
                return;
            }
            else if(Time.time - startTime < ringAlignDuration){
                Draw.z(Layer.bullet - 1f);
                Draw.blend(Blending.additive);
                Lines.stroke(ringWidth * ringThickness, lightColor);
                Draw.alpha(0.8f);
                float fin = (Time.time - startTime) / ringAlignDuration;
                float toAngle = laserAngle() + 90f;
                ellipse(x, y, ringRadius, 1f, Mathf.lerp(Mathf.absin(9f, 1f) + 0.001f, 0.3f, fin), Mathf.lerp(startTime / 6f % 360f, toAngle, fin), Layer.bullet - 1f, Layer.effect + 1f);
                ellipse(x, y, ringRadius, 1f, Mathf.lerp(Mathf.absin(Time.time * -1f, 7f, 1f) + 0.001f, 0.3f, fin), Mathf.lerp(-startTime / 5f % 360f, toAngle, fin), Layer.bullet - 1f, Layer.effect + 1f);
                Draw.color(orbColor);
                ellipse(x, y, ringRadius + ringWidth, 1f, Mathf.lerp(Mathf.absin(11f, 1f) + 0.001f, 0.3f, fin), Mathf.lerp(0f, toAngle, fin), Layer.bullet - 1f, Layer.effect + 1f);
                ellipse(x, y, ringRadius + ringWidth, 1f, Mathf.lerp(Mathf.absin(Time.time * -1f, 13f, 1f) + 0.001f, 0.3f, fin), Mathf.lerp(90f, toAngle, fin), Layer.bullet - 1f, Layer.effect + 1f);
                Draw.color(lightColor);
                ellipse(x, y, ringRadius + ringWidth * 2f, 1f, Mathf.lerp(Mathf.absin(6.1f, 1f) + 0.001f, 0.3f, fin), Mathf.lerp(startTime / 3.7f % 360f, toAngle, fin), Layer.bullet - 1f, Layer.effect + 1f);
                Draw.blend();
            }
            else{
                Draw.z(Layer.bullet - 1f);
                float fin = (Time.time - startTime - ringAlignDuration) / (animDuration - ringAlignDuration);
                fin = 1f - (1f - fin) * (1f- fin); //this is finpow (kinda)
                float angle = laserAngle();
                Draw.blend(Blending.additive);
                Lines.stroke(ringWidth * ringThickness);
                Draw.color(lightColor, orbColor, Mathf.absin(Time.globalTime, 4.7f, 1f));
                Draw.alpha(0.8f);
                ellipse(x, y, ringRadius + ringWidth * 2f * (1f - fin), 1f, 0.3f, angle + 90f, Layer.bullet - 1f, Layer.effect + 1f);
                Tmp.v1.trns(angle, fin * 80f).add(this);
                ellipse(Tmp.v1.x, Tmp.v1.y, ringRadius + ringWidth * (1f - fin), 1f, 0.3f, angle + 90f, Layer.bullet - 1f, Layer.effect + 1f);
                Tmp.v1.trns(angle + 180f, fin * 80f).add(this);
                ellipse(Tmp.v1.x, Tmp.v1.y, ringRadius + ringWidth * (1f - fin), 1f, 0.3f, angle + 90f, Layer.bullet - 1f, Layer.effect + 1f);
                Tmp.v1.trns(angle, fin * 160f).add(this);
                ellipse(Tmp.v1.x, Tmp.v1.y, ringRadius, 1f, 0.3f, angle + 90f, Layer.bullet - 1f, Layer.effect + 1f);
                Tmp.v1.trns(angle + 180f, fin * 160f).add(this);
                ellipse(Tmp.v1.x, Tmp.v1.y, ringRadius, 1f, 0.3f, angle + 90f, Layer.bullet - 1f, Layer.effect + 1f);
                Draw.blend();

                Draw.z(Layer.effect - 0.01f);
                Lines.stroke(ringWidth * fin * ringThickness * 1.7f, orbColor);
                Draw.alpha(0.5f);
                Lines.lineAngleCenter(x, y, angle, drawLimit * 2f);
                Lines.stroke(ringWidth * fin * ringThickness);
                Draw.alpha(1f);
                Lines.lineAngleCenter(x, y, angle, drawLimit * 2f);
                Lines.stroke(ringWidth * fin * ringThickness * 0.5f, Color.white);
                Draw.z(Layer.effect);
                Lines.lineAngleCenter(x, y, angle, drawLimit * 2f);
            }

            Drawm.altarOrb(x, y, 11f, 1f, 45f, orbColor, 4);
        }

        @Override
        public void configured(Unit builder, Object value){
            if(value instanceof Boolean glenn){
                if(used >= maxUses) return;
                if(glenn && !terraforming){
                    //start charging
                    charging = true;
                }
                else if(!terraforming && charging && charge >= maxCharge && (!net.active() || (builder.isPlayer() && (builder.getPlayer().admin() || builder.getPlayer().isLocal())))){
                    //start terraforming, can only be started by admins
                    charging = false;
                    charge = 0f;
                    terraforming = true;
                    terraformStart();
                }
            }
            super.configured(builder, value);
        }

        @Override
        public void buildConfiguration(Table table){
            if(terraforming) return;
            table.table(t -> {
                t.defaults().size(265f, 40f);
                if(HardmodeFragment.background != null) t.background(HardmodeFragment.background);
                TextButton ibut = t.button("> INSTALL", Styles.cleart, () -> configure(true)).pad(4f).padBottom(4f).disabled(l -> !canConsume() || charging || terraforming).get();
                ibut.getLabel().setStyle(new Label.LabelStyle(Styles.techLabel));
                ibut.update(() -> ibut.getLabel().setColor(terraforming || (charging && charge >= maxCharge) ? Color.green : (charging ? Pal.accent : (canConsume() && heat > 0.8f) ? lightColor : Pal.gray)));
                t.row();
                t.image().color(Pal.gray).fillX().growX().height(4f).padTop(2f).padBottom(2f);
                t.row();
                TextButton ebut = t.button("> EXECUTE", Styles.cleart, () -> configure(false)).pad(4f).padBottom(4f).disabled(l -> terraforming || !charging || charge < maxCharge).get();
                ebut.getLabel().setStyle(new Label.LabelStyle(Styles.techLabel));
                ebut.update(() -> ebut.getLabel().setColor(terraforming ? Color.green : ((charging && charge >= maxCharge) ? tmpc.set(Pal.accent).lerp(Color.scarlet, Mathf.absin(Time.globalTime, 10f, 1f)) : Pal.gray)));
            });
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();

            Sounds.explosionbig.at(tile);

            if((heat < 0.5f) || !state.rules.reactorExplosions) return;

            Effect.shake(6f, 16f, x, y);
            Damage.damage(x, y, 19f * tilesize, 5000f * (tier + 1)); //death

            explodeEffect.at(x, y);
        }
    }
}
