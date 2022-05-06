package betamindy.util;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.MusicLoader.*;
import arc.audio.*;
import arc.func.*;
import arc.math.*;
import arc.util.*;
import mindustry.game.*;

import static mindustry.Vars.*;

public class MusicControl {
    public final static String musicMod = "betamindy-music";
    public static final String musicRepo = "sk7725/BetaMindyMusic";

    public static final String[] musicFiles = {"invasion"};
    public Music[] musics;

    protected @Nullable Music current = null;
    protected int cimportance = 0;
    protected @Nullable Boolp cpred = null;
    protected boolean fadeinNext = true;
    public boolean waiting = false;

    private float fade = 0f;
    private int fadeMode = 0; //1: vanilla -> 0 / 2: 0 -> custom / 3: custom -> 0 / 4: custom -> 0 but wait! there's more
    private int lastVol = -1;

    public void init(){
        musics = new Music[musicFiles.length];
        Events.on(EventType.FileTreeInitEvent.class, e -> {
            //load music here
            for(int i = 0; i < musicFiles.length; i++){
                if(tree.get(musicFiles[i]).exists()){
                    musics[i] = loadMusic(musicFiles[i]);
                }

            }
        });
    }

    public void update(){
        if(state == null || !state.isGame()){
            if(current != null) reset();
        }

        if(current != null){
            if(fadeMode == 1){
                //vanilla -> 0
                fade = Mathf.lerp(fade, 0f, 0.08f);
                Core.settings.put("musicvol", (int)(lastVol * fade));

                if(fade <= 0.01f){
                    play(current, fadeinNext, waiting);
                }
            }
            else{
                control.sound.stop();
                if(fadeMode == 2){
                    //0 -> custom
                    fade = Mathf.lerp(fade, 1f, 0.02f);
                    current.setVolume(Core.settings.getInt("musicvol") / 100f * fade);
                    if(fade > 0.99f){
                        fadeMode = 0;
                        current.setVolume(Core.settings.getInt("musicvol") / 100f);
                    }
                }
                else if(fadeMode == 3 || fadeMode == 4){
                    //custom -> 0
                    if(fade < 0.01f){
                        fade = 0f;
                        current.stop();
                        current.setLooping(false);
                        if(fadeMode == 3){
                            //end
                            reset();
                            return;
                        }
                    }
                    else{
                        fade = Mathf.lerp(fade, 0f, 0.01f);
                        current.setVolume(Core.settings.getInt("musicvol") / 100f * fade);
                    }
                }
                else{
                    current.setVolume(Core.settings.getInt("musicvol") / 100f);
                }

                if(fadeMode != 3 && shouldEnd()){
                    if(fadeMode == 0) fade = 1f;
                    fadeMode = 3;
                }
            }
        }
    }

    public void playUntil(int id, @Nullable Boolp end, int importance, boolean fadein, boolean wait){
        if(id < 0 || importance < cimportance && current != null) return; //do not use this method to shut it up!
        cpred = end;
        cimportance = Math.max(0, importance);

        if(current == null){
            fadeMode = 1;
            fade = 1f;
            fadeinNext = fadein;
            lastVol = Core.settings.getInt("musicvol");
            current = musics[id];
            waiting = wait;
        }
        else{
            play(musics[id], fadein, wait);
        }
    }

    /** Fades out the current track but leaves the music in suspense, waiting. */
    public void interrupt(){
        if(current == null || fadeMode == 1 || fadeMode == 3) return;
        if(fadeMode != 2) fade = 1f;
        fadeMode = 4;
    }

    /** Start playing the current track that has been waiting. */
    public void go(){
        if(!waiting || current == null || current.isPlaying()) return;
        waiting = false;
        if(fadeMode == 1) return; //this is the only mode that calls play() at its end
        current.play();
        current.setVolume(Core.settings.getInt("musicvol") / 100f);
        current.setLooping(true);
    }

    public void play(Music music, boolean fadein, boolean wait){
        fade = 0f;
        if(wait) fadein = false; //fadein & wait is not supported
        fadeMode = fadein ? 2 : 0;
        if(lastVol != -1){
            Core.settings.put("musicvol", lastVol);
            lastVol = -1;
        }

        current = music;
        waiting = wait;
        if(!wait){
            current.play();
            current.setVolume(fadein ? 0f : Core.settings.getInt("musicvol") / 100f);
            current.setLooping(true);
        }
    }

    public void reset(){
        if(current != null){
            current.setLooping(false);
            if(current.isPlaying()) current.stop();
        }
        current = null;
        cpred = null;
        fadeMode = 0;
        fade = 0f;
        if(lastVol != -1){
            Core.settings.put("musicvol", lastVol);
            lastVol = -1;
        }
    }

    public boolean shouldEnd(){
        return (cpred != null && cpred.get());
    }

    public Music loadMusic(String soundName){
        if(headless) return new Music();

        String name = "music/" + soundName;
        String path = tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

        Music music = new Music();
        AssetDescriptor<?> desc = Core.assets.load(path, Music.class, new MusicParameter(music));
        desc.errored = Throwable::printStackTrace;

        return music;
    }
}
