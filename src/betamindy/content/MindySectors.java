package betamindy.content;

import betamindy.type.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;

import static betamindy.BetaMindy.uwu;
import static betamindy.content.MindyPlanets.shar;

public class MindySectors implements ContentList {
    public static SectorPreset
        colonyRuins;

    @Override
    public void load(){
        //region shar
        if(shar == null) return; //planet failed to load, thanks Anuke

        colonyRuins = new SpecialSectorPreset("colonyRuins", shar, 15, Icon.book){{
            alwaysUnlocked = uwu;
            addStartingItems = false;
            captureWave = 0;
            difficulty = 0;
            startWaveTimeMultiplier = 0.1f;
            useAI = false;
            showSectorLandInfo = false;
        }};
    }
}
