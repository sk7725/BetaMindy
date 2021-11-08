package betamindy.type.weather;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.*;
import arc.util.Time;
import arc.util.Tmp;
import betamindy.*;
import betamindy.content.MindyFx;
import betamindy.world.blocks.campaign.*;
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
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.*;

public class BlockWeather extends ParticleWeather {
    public Block block = Blocks.router;
    public Team blockTeam = Team.derelict;
    public Effect blockEffect = Fx.explosion, blockFallingEffect = MindyFx.blockFalling;
    public float blockDamageRad = 3 * 8f, blockDamage = 50 * block.size * block.size, blockChance = 0.2f, blockChangeDelay = 3f;
    public boolean randomBlock = false;

    //private Block tempBlock = Blocks.router;

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

        rand.setSeed((long) Time.time);
        for(int i = 0; i < total; i++){
            float scl = rand.random(0.5f, 1f);
            float scl2 = rand.random(0.5f, 1f);
            float size = rand.random(sizeMin, sizeMax);
            float x = (rand.random(0f, world.unitWidth()) + Time.time * windx * scl2);
            float y = (rand.random(0f, world.unitHeight()) + Time.time * windy * scl);

            x += Mathf.sin(y, rand.random(sinSclMin, sinSclMax), rand.random(sinMagMin, sinMagMax));

            x -= Tmp.r1.x;
            y -= Tmp.r1.y;
            x = Mathf.mod(x, Tmp.r1.width);
            y = Mathf.mod(y, Tmp.r1.height);
            x += Tmp.r1.x;
            y += Tmp.r1.y;

            Block block1 = block;
            if(randomBlock) {
                Block temp = Vars.content.blocks().get(Mathf.random(Vars.content.blocks().size - 1));
                if(temp instanceof ConstructBlock || !temp.hasBuilding() || temp.isHidden() || temp instanceof CoreBlock || temp instanceof Altar || temp.size > 4) return;
                block1 = temp;
            }
            if(Tmp.r3.setCentered(x, y, size).overlaps(Tmp.r2) && Mathf.randomBoolean(blockChance * state.intensity)){
                final int x1 = Mathf.random(1, world.tiles.width - 1);
                final int y1 = Mathf.random(1, world.tiles.height - 1);
                if(world.tile(x1, y1) == null) return;
                final Block block2 = block1;
                blockFallingEffect.at(x1 * 8, y1 * 8, Time.time * 4f, block1);
                Time.run(blockFallingEffect.lifetime, () -> {
                    if(world.build(x1, y1) == null) {
                        world.tile(x1, y1).setNet(block2, blockTeam, Mathf.random(0, 3));
                        if(world.tile(x1, y1).build != null) world.tile(x1, y1).build.placed();
                    }else{
                        Damage.damage(x1 * 8, y1 * 8, blockDamageRad, blockDamage * state.intensity);
                    }

                    Effect.shake(Math.min(20f, state.intensity * block2.size), 10f,x1 * 8, y1 * 8);
                    blockEffect.at(x1 * 8, y1 * 8, Mathf.random(360f));
                });
            }
        }
    }

    public Block rollBlock(){
        return rollBlock((int)Mathf.random() * 255);
    }

    public Block rollBlock(int seed){
        int rand = (int)(Mathf.randomSeed(seed) * BetaMindy.visibleBlockList.size);
        return BetaMindy.visibleBlockList.get(rand);
    }

    public TextureRegion rollIcon(int seed){
        return rollBlock(seed).fullIcon;
    }

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

        if(drawParticles){
            drawBlocks(region, color, sizeMin, sizeMax, density, state.intensity, state.opacity, windx, windy, minAlpha, maxAlpha, sinSclMin, sinSclMax, sinMagMin, sinMagMax);
        }
    }

    public void drawBlocks(TextureRegion region, Color color,
                              float sizeMin, float sizeMax,
                              float density, float intensity, float opacity,
                              float windx, float windy,
                              float minAlpha, float maxAlpha,
                              float sinSclMin, float sinSclMax, float sinMagMin, float sinMagMax){
        Tmp.r1.setCentered(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth() / renderer.minScale(), Core.graphics.getHeight() / renderer.minScale());
        Tmp.r1.grow(sizeMax * 1.5f);
        Core.camera.bounds(Tmp.r2);
        int total = (int)(Tmp.r1.area() / density * intensity);
        Draw.color(color, opacity);
        rand.setSeed(0);
        for(int i = 0; i < total; i++){
            float scl = rand.random(0.5f, 1f);
            float scl2 = rand.random(0.5f, 1f);
            float size = rand.random(sizeMin, sizeMax);
            float x = (rand.random(0f, world.unitWidth()) + Time.time * windx * scl2);
            float y = (rand.random(0f, world.unitHeight()) + Time.time * windy * scl);
            float alpha = rand.random(minAlpha, maxAlpha);
            float r = rand.random(360f);

            x += Mathf.sin(y, rand.random(sinSclMin, sinSclMax), rand.random(sinMagMin, sinMagMax));

            x -= Tmp.r1.x;
            y -= Tmp.r1.y;
            x = Mathf.mod(x, Tmp.r1.width);
            y = Mathf.mod(y, Tmp.r1.height);
            x += Tmp.r1.x;
            y += Tmp.r1.y;

            if(Tmp.r3.setCentered(x, y, size).overlaps(Tmp.r2)){
                Draw.alpha(alpha * opacity);
                Draw.rect(randomBlock ? rollIcon(i) : region, x, y, size, size, Time.time * 4f + r);
            }
        }
    }
}
