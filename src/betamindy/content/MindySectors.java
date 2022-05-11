package betamindy.content;

import betamindy.type.*;
import mindustry.gen.*;
import mindustry.type.*;

import static betamindy.BetaMindy.*;
import static betamindy.content.MindyPlanets.*;

public class MindySectors{
    public static SectorPreset
        colonyRuins;

    public static void load(){
        //region shar
        if(shar == null) return; //planet failed to load, thanks Anuke

        colonyRuins = new SpecialSectorPreset("colonyRuins", shar, 15, Icon.book){{
            alwaysUnlocked = uwu;
            addStartingItems = false;
            captureWave = 0;
            difficulty = 0;
            startWaveTimeMultiplier = 0.1f;
            showSectorLandInfo = false;
        }};
    }
}
