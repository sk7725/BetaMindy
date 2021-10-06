package betamindy.world.blocks.campaign;

import arc.graphics.*;
import betamindy.content.*;
import betamindy.graphics.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.world.*;

//todo lore manual, comes with camera panning to give it attention every time it updates, intangible and stuff
//This esoterum manual is the unique manual found in the first shar sector. Not to be confused with BallisticManual (portal attack remainders) or ManualPiece (found on other sectors)
public class LoreManual extends Block {
    public Color flameColor = Pal2.esoterum;
    public Effect smokeEffect = MindyFx.smokeRise;
    public Effect flameEffect = MindyFx.manualFire;

    public LoreManual(String name){
        super(name);
        update = configurable = true;
    }
}
