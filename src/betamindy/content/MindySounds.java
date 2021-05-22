package betamindy.content;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.*;
import arc.audio.*;
import mindustry.*;

public class MindySounds {
    public static Sound pistonPush, pistonPull, presentBells, boost, tntfuse, boing, freeze, coolingFan;
    public static final String[] soundFiles = {"pistonpush", "pistonpull", "presentbells", "boostsound", "tntfuse", "boing", "freeze", "coolingfan"};
    private static int num = 0;

    public static void load() {
        num = 0;
        pistonPush = l();
        pistonPull = l();
        presentBells = l();
        boost = l();
        tntfuse = l();
        boing = l();
        freeze = l();
        coolingFan = l();
    }

    public static void dispose() {
        num = 0;
        pistonPush = d();
        pistonPull = d();
        presentBells = d();
        boost = d();
        tntfuse = d();
        boing = d();
        freeze = d();
        coolingFan = d();
    }

    protected static Sound l() {
        return loadSound(soundFiles[num++]);
    }

    protected static Sound d() {
        return disposeSound(soundFiles[num++]);
    }

    protected static Sound loadSound(String soundName) {
        if(!Vars.headless) {
            String name = "sounds/" + soundName;
            String path = name + ".ogg";

            Sound sound = new Sound();

            AssetDescriptor<?> desc = Core.assets.load(path, Sound.class, new SoundLoader.SoundParameter(sound));
            desc.errored = Throwable::printStackTrace;

            return sound;
        } else {
            return new Sound();
        }
    }

    protected static Sound disposeSound(String soundName) {
        if(!Vars.headless) {
            String name = "sounds/" + soundName;
            String path = name + ".ogg";

            if(Core.assets.isLoaded(path, Sound.class)) {
                Core.assets.unload(path);
            }
        }

        return null;
    }
}
