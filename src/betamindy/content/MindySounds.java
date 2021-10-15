package betamindy.content;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.*;
import arc.audio.*;
import betamindy.world.blocks.logic.*;
import mindustry.*;

public class MindySounds {
    public static Sound pistonPush, pistonPull, presentBells, boost, tntfuse, boing, freeze, coolingFan, pipePop, pipeSqueeze, pipeIn, easterEgg1, easterEgg2, portalOpen, portalLoop, portalClose, lightningStrike, shatter, boxOpen, astroCharge, astroShoot, drink, pianoSample, squareSample, synthSample;
    public static Sound[] piano, bells, squareWave, sawWave, bass, organ, chimes, violin, harp;

    public static void load(){
        pistonPush = loadSound("pistonpush");
        pistonPull = loadSound("pistonpull");
        presentBells = loadSound("presentbells");
        boost = loadSound("boostsound");
        tntfuse = loadSound("tntfuse");
        boing = loadSound("boing");
        freeze = loadSound("freeze");
        coolingFan = loadSound("coolingfan");
        pipePop = loadSound("pipepop");
        pipeSqueeze = loadSound("pipesqueeze");
        pipeIn = loadSound("pipein");
        easterEgg1 = loadSound("strawberrydeath");
        easterEgg2 = loadSound("strawberrypredeath");
        portalOpen = loadSound("portalopen");
        portalLoop = loadSound("portalloop");
        portalClose = loadSound("portalclose");
        lightningStrike = loadSound("lstrike");
        shatter = loadSound("shatter");
        boxOpen = loadSound("boxopen");
        astroCharge = loadSound("astroCharge");
        astroShoot = loadSound("astroShoot");
        drink = loadSound("drink");
        pianoSample = loadSound("pianoS");
        squareSample = loadSound("squareS");
        synthSample = loadSound("synthS");

        //TODO more samples
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
}
