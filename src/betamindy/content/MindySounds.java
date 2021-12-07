package betamindy.content;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.*;
import arc.audio.*;
import betamindy.world.blocks.logic.*;
import mindustry.*;

public class MindySounds {
    public static Sound pistonPush, pistonPull, presentBells, boost, tntfuse, boing, freeze, coolingFan, pipePop, pipeSqueeze, pipeIn, easterEgg1, easterEgg2, portalOpen, portalLoop, portalClose, lightningStrike, shatter, boxOpen, astroCharge, astroShoot, drink, pianoSample, squareSample, synthSample, pewRetro;
    public static Sound[] piano, bells, squareWave, sawWave, bass, organ, chimes, violin, harp, drums;
    public static final String[] soundFiles = {"pistonpush", "pistonpull", "presentbells", "boostsound", "tntfuse", "boing", "freeze", "coolingfan", "pipepop", "pipesqueeze", "pipein", "strawberrydeath", "strawberrypredeath", "portalopen", "portalloop", "portalclose", "lstrike", "shatter", "boxopen", "astroCharge", "astroShoot", "drink", "pianoS", "squareS", "synthS", "pewretro"};
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
        pipePop = l();
        pipeSqueeze = l();
        pipeIn = l();
        easterEgg1 = l();
        easterEgg2 = l();
        portalOpen = l();
        portalLoop = l();
        portalClose = l();
        lightningStrike = l();
        shatter = l();
        boxOpen = l();
        astroCharge = l();
        astroShoot = l();
        drink = l();
        pianoSample = l();
        squareSample = l();
        synthSample = l();
        pewRetro = l();

        //credits to farmerthanos
        piano = loadNotes("piano");
        bells = loadNotes("bell"); //btw these are portal bells from the Invasion track
        violin = loadNotes("violin");
        harp = loadNotes("harp");

        //credits to MeepofFaith
        sawWave = loadNotes("saw");
        bass = loadNotes("bass");
        organ = loadNotes("organ");
        chimes = loadNotes("chime");

        //credits to MrDuck
        drums = loadNotes("drum");
    }
/*
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
    }*/

    protected static Sound l() {
        return loadSound(soundFiles[num++]);
    }

    /*protected static Sound d() {
        return disposeSound(soundFiles[num++]);
    }*/

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

    /** Go check out Goobrr/Esoterum
     * @author MeepofFaith
     */
    @Deprecated
    public static Sound[] loadNotes5(String soundName){ //legacy C2 ~ C6
        Sound[] out = new Sound[5];
        for(int i = 0; i < 5; i++){
            out[i] = loadSound("bm" + soundName + "/" + soundName + "C" + (2 + i));
        }
        return out;
    }

    public static Sound[] loadNotes(String soundName){ //current C1 ~ C7
        Sound[] out = new Sound[NotePlayer.octaves];
        for(int i = 0; i < 7; i++){
            out[i] = loadSound("bm" + soundName + "/" + soundName + "C" + (1 + i));
        }
        return out;
    }

    /*protected static Sound disposeSound(String soundName) {
        if(!Vars.headless) {
            String name = "sounds/" + soundName;
            String path = name + ".ogg";

            if(Core.assets.isLoaded(path, Sound.class)) {
                Core.assets.unload(path);
            }
        }

        return null;
    }*/
}
