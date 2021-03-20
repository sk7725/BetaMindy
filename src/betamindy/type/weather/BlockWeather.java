package betamindy.type.weather;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.gen.WeatherState;
import mindustry.type.weather.ParticleWeather;
import mindustry.ui.Cicon;
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.*;

public class BlockWeather extends ParticleWeather {
    public Block block = Blocks.router;
    public Team blockTeam = Team.derelict;
    public Effect blockEffect = Fx.explosion;
    public float blockDamageRad = 3 * 8f, blockDamage = 5 * block.size * block.size, blockChance = 0.2f, blockChangeDelay = 3f;
    public int rands = 0;
    public boolean randomBlock = false;

    public BlockWeather(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();

        region = Core.atlas.find(block.name);
    }

    @Override
    public void update(WeatherState state){
        float speeds = force * state.intensity;
        if(speeds > 0.001f){
            float windx = state.windVector.x * speeds, windy = state.windVector.y * speeds;

            for(Unit unit : Groups.unit){
                unit.impulse(windx, windy);
            }
        }

        float windx, windy;
        if(useWindVector){
            float speed = baseSpeed * state.intensity;
            windx = state.windVector.x * speed;
            windy = state.windVector.y * speed;
        }else{
            windx = this.xspeed;
            windy = this.yspeed;
        }

        int total = (int)(Tmp.r1.area() / density * state.intensity);

        for(int i = 0; i < total; i++){
            float scl = rand.random(0.5f, 1f);
            float scl2 = rand.random(0.5f, 1f);
            float size = rand.random(sizeMin, sizeMax);
            float x = (rand.random(0f, world.unitWidth()) + Time.time * windx * scl2);
            float y = (rand.random(0f, world.unitHeight()) + Time.time * windy * scl);
            float alpha = rand.random(minAlpha, maxAlpha);

            x += Mathf.sin(y, rand.random(sinSclMin, sinSclMax), rand.random(sinMagMin, sinMagMax));

            x -= Tmp.r1.x;
            y -= Tmp.r1.y;
            x = Mathf.mod(x, Tmp.r1.width);
            y = Mathf.mod(y, Tmp.r1.height);
            x += Tmp.r1.x;
            y += Tmp.r1.y;

            Block block1 = block;
            if(randomBlock) {
                try {
                    Block temp = Vars.content.blocks().get(Mathf.random(Vars.content.blocks().size - 1));
                    if(temp instanceof ConstructBlock || !temp.hasBuilding()) return;
                    block1 = temp;
                } catch (Throwable e) {
                    arc.util.Log.warn("@", e);
                }
            }
            try{
                rands = Mathf.random(Vars.content.blocks().size - 1);

                if(Tmp.r3.setCentered(x, y, size).overlaps(Tmp.r2) && Mathf.randomBoolean(blockChance)){
                    x = Mathf.random(1, world.tiles.width - 1);
                    y = Mathf.random(1, world.tiles.height - 1);
                    if(world.build((int)x, (int)y) == null && world.tile((int)x, (int)y) != null) {
                        world.tile((int)x, (int)y).setNet(block1, blockTeam, Mathf.random(0, 3));
                    } else {
                        Damage.damage(x * 8, y * 8, blockDamageRad, blockDamage);
                    }
                    Effect.shake(state.intensity, 30f,x * 8, y * 8);
                    blockEffect.at(x * 8, y * 8, Mathf.random(360f));
                }
            } catch(Throwable e){
                Log.warn("@", e);
            }
        }
    }

    @Override
    public void drawParticles(TextureRegion region, Color color,
                              float sizeMin, float sizeMax,
                              float density, float intensity, float opacity,
                              float windx, float windy,
                              float minAlpha, float maxAlpha,
                              float sinSclMin, float sinSclMax, float sinMagMin, float sinMagMax){
        rand.setSeed(0);
        Tmp.r1.setCentered(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth() / renderer.minScale(), Core.graphics.getHeight() / renderer.minScale());
        Tmp.r1.grow(sizeMax * 1.5f);
        Core.camera.bounds(Tmp.r2);
        int total = (int)(Tmp.r1.area() / density * intensity);
        Draw.color(color, opacity);
        if(randomBlock) {
            try {
                Block temp = block;
                if(Time.time % 60f < blockChangeDelay) temp = Vars.content.blocks().get(Mathf.random(Vars.content.blocks().size - 1));
                if((temp instanceof ConstructBlock || !temp.hasBuilding()) && Core.atlas.find(temp.name) == Core.atlas.find("error")) return;
                block = temp;
                region = block.icon(Cicon.medium);
            } catch (Throwable e) {
                arc.util.Log.warn("@", e);
            }
        }
        for(int i = 0; i < total; i++){
            float scl = rand.random(0.5f, 1f);
            float scl2 = rand.random(0.5f, 1f);
            float size = rand.random(sizeMin, sizeMax);
            float x = (rand.random(0f, world.unitWidth()) + Time.time * windx * scl2);
            float y = (rand.random(0f, world.unitHeight()) + Time.time * windy * scl);
            float alpha = rand.random(minAlpha, maxAlpha);

            x += Mathf.sin(y, rand.random(sinSclMin, sinSclMax), rand.random(sinMagMin, sinMagMax));

            x -= Tmp.r1.x;
            y -= Tmp.r1.y;
            x = Mathf.mod(x, Tmp.r1.width);
            y = Mathf.mod(y, Tmp.r1.height);
            x += Tmp.r1.x;
            y += Tmp.r1.y;

            if(Tmp.r3.setCentered(x, y, size).overlaps(Tmp.r2)){
                Draw.alpha(alpha * opacity);
                try{
                    Draw.rect(region, x, y, size, size, Time.time * 4f);
                } catch(Throwable e) {
                    Log.warn("@", e);
                }
            }
        }
    }
}
