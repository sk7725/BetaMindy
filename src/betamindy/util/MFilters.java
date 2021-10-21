package betamindy.util;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;

public class MFilters {
    public final static int vanillaFilters = 3; //just in case Anuke adds 2 more.
    public static int lastFilter = vanillaFilters - 1;
    private boolean inited = false;
    protected boolean wasPlaying;
    protected Interval timer = new Interval(1);

    public final FilterModule[] filters = {
            new FilterModule("Wet", new Filters.BiquadFilter(){{
                set(0, 500, 1);
            }}, Color.royal),
            new FilterModule("Lofi", new Filters.LofiFilter(){{
                set(8000, 5);
            }}, Pal.heal)
    };

    public MFilters(){
        setupFilters();
    }

    /**
     * @param idZero the local id of the filter, which starts from 0, no matter the number of vanilla filters.
     * @return the global id of the filter
     */
    public int fromLocalID(int idZero){
        return Mathf.clamp(vanillaFilters + idZero, vanillaFilters, lastFilter);
    }

    public void update(){
        boolean playing = Vars.state.isGame();
        //fade the filters in/out, poll every 30 ticks just in case performance is an issue
        if(timer.get(0, 30f)){
            for(FilterModule fm : filters){
                //Core.audio.soundBus.fadeFilterParam(fm.id, Filters.paramWet, fm.enabled ? 1f : 0f, 0.4f); //"paramWet" is normally the intensity of the filter
                Core.audio.fadeFilterParam(0, fm.id, Filters.paramWet, fm.enabled ? 1f : 0f, 0.4f);
                fm.enabled = false;
            }
        }
        if(playing != wasPlaying){
            wasPlaying = playing;

            if(playing){
                setupFilters();
            }
        }
    }

    protected void setupFilters(){
        //todo enable audio filters setting, dont add this to trigger.update if false
        for(FilterModule fm : filters){
            //Core.audio.soundBus.setFilter(fm.id, fm.filter);
            //Core.audio.soundBus.setFilterParam(fm.id, Filters.paramWet, 0f); //"paramWet" is normally the intensity of the filter
            Core.audio.setFilter(fm.id, fm.filter);
            Core.audio.setFilterParam(0, fm.id, Filters.paramWet, 0f);
        }
        inited = true;
    }

    public void enableFilter(int idZero){
        if(!inited || idZero < 0 || idZero >= filters.length) return;
        filters[idZero].enabled = true;
    }

    public static class FilterModule {
        public int id; //global ID.
        public String name;
        public AudioFilter filter;
        public Color color;
        //public float lastSetFrame = 0f;
        public boolean enabled = false;

        public FilterModule(String name, AudioFilter filter, Color color){
            this.name = name;
            this.filter = filter;
            this.id = ++lastFilter;
            this.color = color;
        }

        public FilterModule(String name, AudioFilter filter){
            this(name, filter, Color.white);
        }
    }
}
