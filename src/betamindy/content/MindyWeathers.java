package betamindy.content;

import arc.util.Time;
import betamindy.type.weather.BlockWeather;
import mindustry.ctype.ContentList;
import mindustry.gen.Sounds;
import mindustry.type.Weather;
import mindustry.world.meta.Attribute;

public class MindyWeathers implements ContentList {
    public static Weather routerWeather, blockWeather;

    @Override
    public void load() {
        routerWeather = new BlockWeather("routerWeather"){{
            particleRegion = "particle";
            sizeMax = 13f;
            sizeMin = 2.6f;
            density = 1200f;
            attrs.set(Attribute.light, -0.15f);

            sound = Sounds.windhowl;
            soundVol = 0f;
            soundVolOscMag = 1.5f;
            soundVolOscScl = 1100f;
            soundVolMin = 0.02f;
        }};

        blockWeather = new BlockWeather("blockWeather"){{
            randomBlock = true;
            sizeMax = 15f;
            sizeMin = 3f;
            density = 1500f;
            attrs.set(Attribute.light, -0.3f);

            baseSpeed = 5.4f;
            sound = Sounds.windhowl;
            soundVol = 0f;
            soundVolOscMag = 1.5f;
            soundVolOscScl = 1100f;
            soundVolMin = 0.02f;
        }};
    }
}
