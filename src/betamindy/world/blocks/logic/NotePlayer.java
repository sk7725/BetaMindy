package betamindy.world.blocks.logic;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.ui.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static arc.Core.atlas;
import static mindustry.Vars.headless;

/**
 * Note Block. Ported from Commandblocks with some code references to Goobrr/Esoterum.
 * @author sunny
 * @author MeepofFaith
 */
public class NotePlayer extends Block {
    public final static int sampleOctave = 2; //C4
    public final static int octaves = 5; //C2 ~ C6
    public final static int sampleOffset = sampleOctave * 12;
    public final static int procOffset = -1000;
    public Instrument[] instruments;
    public boolean global = false; //whether this block is so loud that it plays all over serpulo

    public TextureRegion topRegion;
    public TextureRegion[] instrumentIcons;
    public final static String[] noteNames = new String[]{
            "C%d", "C%d#", "D%d",
            "D%d#", "E%d", "F%d",
            "F%d#", "G%d", "G%d#",
            "A%d", "A%d#", "B%d"
    };
    //used for black keys where "C4#" won't fit
    public final static String[] noteButtonNames = new String[]{
            "C", "C#", "D",
            "D#", "E", "F",
            "F#", "G", "G#",
            "A", "A#", "B"
    };
    public final static int[] whiteOffset = {0, 2, 4, 5, 7, 9, 11, 12, 14, 16};
    public final static int[] blackOffset = {1, 3, 0, 6, 8, 10, 0, 13, 15};

    public NotePlayer(String name){
        super(name);

        update = configurable = saveConfig = solid = true;
        group = BlockGroup.logic;

        instruments = new Instrument[]{
                new Instrument("Piano", MindySounds.piano),
                new Instrument("Bells", MindySounds.bells),
                new Instrument("Square", MindySounds.squareSample),
                new Instrument("Saw", MindySounds.sawWave),
                new Instrument("Bass", MindySounds.bass),
                new Instrument("Organ", MindySounds.organ),
                new Instrument("Wind3", Sounds.wind3)
        };

        //mode, pitch, vol
        config(byte[].class, (NotePlayerBuild build, byte[] b) -> {
            if(b.length != 3 && b.length != 4) return;

            build.setMode(b[0]);
            if(b[1] >= 0 && b[1] < octaves * 12) build.pitch = b[1];
            if(b[2] >= 0 && b[2] <= 100) build.volume = b[2];
            if(b.length == 3) build.testNote();
        });

        config(Integer.class, (NotePlayerBuild build, Integer i) -> {
            boolean test = true;
            if(i <= procOffset){
                i -= procOffset;
                test = false;
            }

            if(i >= 0){
                if(i < octaves * 12){
                    build.pitch = i;
                    if(test) build.testNote();
                }
            }
            else if(i >= -101){//-101...-1
                build.volume = -i - 1;//100...0
            }
            else{//-102, -103, ...
                build.setMode(-i - 102);
                if(test) build.testNote();
            }
        });
    }

    public static String instString(String modeName, int n){
        return pitchString(n) + " (" + modeName + ")";
    }

    public static String pitchString(int n){
        return String.format(noteNames[n % 12], n / 12 + 2);
    }

    public boolean isNoteBlock(Block other){
        return (other instanceof NotePlayer) || other.name.equals("esoterum-note-block");
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        return other.size == size && isNoteBlock(other);
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top", "betamindy-note-player-top");
        instrumentIcons = new TextureRegion[instruments.length];
        for(int i = 0; i < instruments.length; i++){
            instrumentIcons[i] = atlas.find("betamindy-instrument" + i, "betamindy-instrument-ohno");
        }
    }

    public class NotePlayerBuild extends Building {
        public int mode = 0; //instrument
        public int pitch = sampleOctave * 12;

        public int volume = 10; //only for star note players
        public float heat;
        public boolean trig;

        private int octavePage = 2; //ui only

        //sets the instrument safely
        public void setMode(int m){
            if(m >= 0 && m < instruments.length) mode = m;
        }

        //plays a note quietly for note testing
        public void testNote(){
            if(headless || !enabled) return;
            if(global && consValid()){
                playNote();
                return;
            }
            instruments[mode].at(pitch, x, y);
            effects();
        }
        //plays a note
        public void playNote(){
            if(headless) return;
            if(global){
                instruments[mode].play(pitch, volume / 10f);
            }
            else{
                instruments[mode].at(pitch, x, y);
            }
            effects();
        }

        public void effects(){
            //todo
            heat = 1f;
        }

        @Override
        public void updateTile(){
            if(heat > 0f) heat -= delta() * 0.05f;

            if(consValid()){
                if(!trig){
                    playNote();
                    trig = true;
                }
            }
            else trig = false;
        }

        @Override
        public void draw(){
            super.draw();
            if(heat > 0.1f){
                Draw.alpha(heat);
                Draw.rect(topRegion, x, y);
                Draw.alpha(1f);
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            Draw.rect(instrumentIcons[mode], x, y);
            drawPlaceText(instString(instruments[mode].name, pitch), tile.x, tile.y, true);
        }

        @Override
        public void drawConfigure(){
            super.drawConfigure();
            Draw.z(Layer.power + 1);
            Draw.rect(instrumentIcons[mode], x, y);
        }

        @Override
        public void drawStatus(){
            if(block.enableDrawStatus){
                Draw.z(Layer.power + 1);
                Draw.rect(instrumentIcons[mode], x, y);
            }
        }

        @Override
        public Object config(){
            return new byte[]{(byte) mode, (byte) pitch, (byte) volume};
        }

        String noteString(){
            return pitchString(pitch);
        }

        boolean validMode(int m){
            return m >= 0 && m < instruments.length;
        }

        void instButton(Table t, int offset, boolean enabled){
            t.table(bt -> {
                bt.center();
                Label l = bt.add(instruments[Mathf.mod(mode + offset, instruments.length)].name, Styles.outlineLabel, 1.2f).color(enabled ? Pal.accent : Color.lightGray).get();
                Image li = bt.image(instrumentIcons[Mathf.mod(mode + offset, instruments.length)]).size(32).padLeft(-22).color(enabled ? Color.white : Color.lightGray).get();
                l.update(() -> {
                    int m = Mathf.mod(mode + offset, instruments.length);
                    l.setText(instruments[m].name);
                    li.setDrawable(instrumentIcons[m]);
                });
            }).size(100, 40);
        }

        @Override
        public void buildConfiguration(Table table){
            table.clearChildren();
            octavePage = pitch / 12;
            table.table(frame -> {
                frame.table(Tex.pane, t -> {
                    t.defaults().pad(0);
                    instButton(t, -1, false);
                    t.button(Icon.left, Styles.accenti, 30, () -> {
                        configure(-102 - Mathf.mod(mode - 1, instruments.length));
                    }).size(30).color(Pal.accent);
                    instButton(t, 0 , true);
                    t.button(Icon.right, Styles.accenti, 30, () -> {
                        configure(-102 - Mathf.mod(mode + 1, instruments.length));
                    }).size(30).color(Pal.accent);
                    instButton(t, 1, false);
                }).growX();

                frame.row();
                frame.table(t -> {
                    t.setBackground(Styles.black5);
                    t.left();
                    t.defaults().pad(0).margin(0);
                    t.image().color(Pal.gray).growY().width(4).left();
                    t.add("Octave: ", Styles.outlineLabel).padLeft(5).padRight(5);

                    for(int i = 0; i < 5; i++){
                        int id = i;
                        t.button("" + (id + 2), Styles.logicTogglet, () -> {
                            int diff = id - pitch / 12;
                            if(id != octavePage){
                                configure(pitch + diff * 12);
                                octavePage = id;
                                buildConfiguration(table); //refresh
                            }
                        }).height(30).growX().padRight(3).checked(butt -> octavePage == id);
                    }
                    t.image().color(Pal.gray).growY().width(4).right();
                }).growX();

                frame.row();
                frame.table(t -> {
                    Table whites = new Table(w -> {
                        for(int i = 0; i < 10; i++){
                            int note = octavePage * 12 + whiteOffset[i];
                            w.button("\n\n\n\n\n" + (note >= octaves * 12 ? "[gray]" + pitchString(note) + "[]": pitchString(note)), MindyUILoader.whitePiano, () -> {
                                if(note < octaves * 12) configure(note);
                            }).size(40, 150).checked(butt -> note == pitch).disabled(note >= octaves * 12);
                        }
                    });

                    Table blacks = new Table(w -> {
                        w.top();
                        for(int i = 0; i < 9; i++){
                            int note = octavePage * 12 + blackOffset[i];
                            w.button(note >= octaves * 12 ? "[lightgray]" + noteButtonNames[note % 12] + "[]" : noteButtonNames[note % 12], MindyUILoader.blackPiano, () -> {
                                if(note < octaves * 12) configure(note);
                            }).size(30, 90).padLeft(5).padRight(5).top().visible(i != 2 && i != 6).checked(butt -> note == pitch).disabled(note >= octaves * 12);
                        }
                    });
                    t.stack(whites, blacks).growX();
                }).growX();

                if(global){
                    frame.row();
                    frame.table(Tex.pane, v -> {
                        Label vol = v.add("Volume: " + (volume / 10) + "." + (volume % 10), Styles.outlineLabel).padLeft(5).padRight(5).width(160).get();
                        v.slider(0, 100, 1, volume, f -> {
                            if((int)f != volume){
                                configure(-1 * (int)f - 1);
                            }
                            vol.setText("Volume: " + ((int)f / 10) + "." + ((int)f % 10));
                        }).height(40f).growX();
                    }).growX();
                }
            });
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(mode);
            write.s(pitch);
            write.b(volume);
            write.bool(trig);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            mode = read.s();
            pitch = read.s();
            volume = read.b();
            trig = read.bool();
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case heat -> heat;
                case config -> (double)(pitch / 12) + ((pitch % 12) * 0.01);
                default -> super.sense(sensor);
            };
        }

        @Override
        public Object senseObject(LAccess sensor) {
            return switch(sensor){
                //senseObject takes priority over sense unless it is a noSensed
                case config -> noSensed;
                default -> super.senseObject(sensor);
            };
        }

        //used for processor control
        public void configureP(int c){
            configure(c + procOffset);
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.config){
                //controlling capability
                if (p1 < 0.0 || p1 >= octaves - 0.1){ //octave invalid
                    return;
                }
                double rem = p1;
                int whole = (int) p1; //octave
                rem -= whole; // pitch
                rem *= 100;
                if (rem > 11.1){ // pitch invalid
                    return;
                }
                rem += 0.5; //forces typecast to work
                int pitch = whole * 12 + (int)rem;
                configure(pitch);
            }
            else if(type == LAccess.color){
                //r = instrument
                //g = pitch (int)
                //b = volume (float)
                int inst = p1 < 0.0 ? mode : ((int)p1) % instruments.length;
                int p = p2 < 0.0 ? pitch : ((int)p2) % (octaves * 12);
                int v = (p3 < 0.0 || global) ? volume : Mathf.round((float)(p3 * 10));
                if(v < 0) v = 0;
                else if(v > 100) v = 100;

                if(inst == mode){
                    if(p == pitch){
                        //configure volume
                        if(v != volume) configureP(-1 - v);
                        return;
                    }
                    else{
                        if(v == volume){
                            //configure pitch
                            configureP(p);
                            return;
                        }
                        //inst, p wrong
                    }
                }
                else if(p == pitch && v == volume){
                    configureP(-102 - inst);
                    return;
                }

                //two or more are wrong
                configure(new byte[]{(byte) inst, (byte) p, (byte) v, 1});
            }
        }

        //esoterum compatibility
        @Override
        public void overwrote(Seq<Building> builds){
            if(builds.first() instanceof NotePlayerBuild build){
                mode = build.mode;
                pitch = build.pitch;
                volume = build.volume;
            }
            else if(builds.first().block.name.equals("esoterum-note-block") && builds.first().config() instanceof IntSeq configs){
                if(configs.size == 5){
                    mode = configs.get(4) % instruments.length;
                    pitch = (configs.get(1) + configs.get(2) * 12) % (octaves * 12);
                    volume = Mathf.clamp(configs.get(3), 0, 100);
                }
            }
        }
    }

    public static class Instrument {
        public String name;
        public Sound note; //C4

        //only valid if hasOctaves is true
        public Sound[] octaves; //C2 ~ C6
        public boolean hasOctaves;

        public Instrument(String name, Sound sample){
            hasOctaves = false;
            this.name = name;
            note = sample;
        }

        public Instrument(String name, Sound[] samples){
            hasOctaves = true;
            this.name = name;
            if(samples == null) return; //headless
            note = samples[sampleOctave];
            octaves = samples;
        }

        public void at(int n, float x, float y){
            if(n < 0) return;
            if(hasOctaves && n < octaves.length * 12){
                octaves[n / 12].at(x, y, getPitch(n % 12));
            }
            else{
                note.at(x, y, getPitch(n - sampleOffset));
            }
        }

        public void play(int n, float volume){
            if(n < 0) return;
            if(hasOctaves && n < octaves.length * 12){
                octaves[n / 12].play(volume, getPitch(n % 12), 0);
            }
            else{
                note.play(volume, getPitch(n - sampleOffset), 0);
            }
        }

        public float getPitch(int offset){
            if(offset == 0) return 1;
            return (float)Math.pow(2, offset / 12.0);
        }
    }
}
