package betamindy.type.weather;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.weather.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class IonWind extends ParticleWeather {
    public Effect hitEffect = MindyFx.ionHit;
    public Color blinkColor = Color.white;
    public float blinkDuration = 45f, blinkGap = 300f;

    public IonWind(String name){
        super(name);
        color = noiseColor = Pal2.drift.cpy().lerp(Color.white, 0.5f);
        useWindVector = true;
        drawNoise = true;
        noiseLayerAlphaM = 0.25f;
        opacityMultiplier = 1f;
        sizeMin = 2f;
        sizeMax = 18f;
        minAlpha = 1f;
        maxAlpha = 1f;
        density = 2600f;
        baseSpeed = 2.4f;
        force = 0.1f;
        sound = Sounds.wind;
        soundVol = 0.5f;
        duration = 7f * Time.toMinutes;
        attrs.set(Attribute.light, 0.3f);
        attrs.set(MindyAttribute.magnetic, 1f);
    }

    @Override
    public void update(WeatherState state){
        float speed = force * state.intensity * Time.delta * 0.5f;
        if(speed > 0.001f){
            float windx = state.windVector.x * speed, windy = state.windVector.y * speed;
            float rot = state.windVector.angle();

            for(Bullet b : Groups.bullet){
                if(b.type != null && b.type.hittable){
                    b.vel.add(windx, windy);
                    if(b.type.speed < 1f || b.type.drag > 0.00001f) b.rotation(b.vel.angle());
                    if(Mathf.chance(0.1f)) hitEffect.at(b.x + Mathf.range(b.hitSize() / 2f), b.y + Mathf.range(b.hitSize() / 2f), rot, color);
                }
            }
        }
    }
    //note: Draw.blend refuses to work at all.

    @Override
    public void drawOver(WeatherState state){

        float windx, windy;
        if(useWindVector){
            float speed = baseSpeed * state.intensity;
            windx = state.windVector.x * speed;
            windy = state.windVector.y * speed;
        }else{
            windx = this.xspeed;
            windy = this.yspeed;
        }

        if(drawNoise){
            if(noise == null){
                noise = Core.assets.get("sprites/" + noisePath + ".png", Texture.class);
                noise.setWrap(Texture.TextureWrap.repeat);
                noise.setFilter(Texture.TextureFilter.linear);
            }

            float sspeed = 1f, sscl = 1f, salpha = noiseLayerAlphaM, offset = 0f;
            Color col = Tmp.c1.set(noiseColor);
            for(int i = 0; i < noiseLayers; i++){
                drawNoise(noise, noiseColor, noiseScale * sscl, state.opacity * salpha * opacityMultiplier, sspeed * (useWindVector ? 1f : baseSpeed), state.intensity, windx, windy, offset);
                sspeed *= noiseLayerSpeedM;
                salpha *= noiseLayerAlphaM;
                sscl *= noiseLayerSclM;
                offset += 0.29f;
                col.mul(noiseLayerColorM);
            }
        }

        if(drawParticles){
            drawIonParticles(region, color, sizeMin, sizeMax, density, state.intensity, state.opacity, windx, windy, minAlpha, maxAlpha, sinSclMin, sinSclMax, sinMagMin, sinMagMax, randomParticleRotation);
        }
    }

    public void drawIonParticles(TextureRegion region, Color color,
                                     float sizeMin, float sizeMax,
                                     float density, float intensity, float opacity,
                                     float windx, float windy,
                                     float minAlpha, float maxAlpha,
                                     float sinSclMin, float sinSclMax, float sinMagMin, float sinMagMax,
                                     boolean randomParticleRotation){
        rand.setSeed(0);
        Tmp.r1.setCentered(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth() / renderer.minScale(), Core.graphics.getHeight() / renderer.minScale());
        Tmp.r1.grow(sizeMax * 1.5f);
        Core.camera.bounds(Tmp.r2);
        int total = (int)(Tmp.r1.area() / density * intensity);
        Draw.color(color, opacity);

        for(int i = 0; i < total; i++){
            float scl = rand.random(0.5f, 1f);
            float scl2 = rand.random(0.5f, 1f);
            float size = rand.random(sizeMin, sizeMax);
            float x = (rand.random(0f, world.unitWidth()) + Time.time * windx * scl2);
            float y = (rand.random(0f, world.unitHeight()) + Time.time * windy * scl);
            float alpha = rand.random(minAlpha, maxAlpha);
            float rotation = randomParticleRotation ? rand.random(0f, 360f) : 0f;
            float blinkOffset = rand.random(0f, blinkGap);
            float blink = Mathf.clamp((blinkDuration - (Time.time + blinkOffset) % blinkGap) / blinkDuration);

            x += Mathf.sin(y, rand.random(sinSclMin, sinSclMax), rand.random(sinMagMin, sinMagMax));

            x -= Tmp.r1.x;
            y -= Tmp.r1.y;
            x = Mathf.mod(x, Tmp.r1.width);
            y = Mathf.mod(y, Tmp.r1.height);
            x += Tmp.r1.x;
            y += Tmp.r1.y;

            if(Tmp.r3.setCentered(x, y, size).overlaps(Tmp.r2)){
                Draw.color(color, blinkColor, blink);
                Draw.alpha(alpha * opacity);
                Draw.rect(region, x, y, size, size, rotation);
            }
        }

        Draw.reset();
    }
}
