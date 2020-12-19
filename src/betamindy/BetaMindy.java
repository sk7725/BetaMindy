package betamindy;

import arc.*;
import arc.func.*;
import mindustry.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;

/*
import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ui.dialogs.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import betamindy.content.*;
import betamindy.gen.*;
import betamindy.mod.*;
import betamindy.mod.ContributorList.*;*/

public class BetaMindy extends Mod{
    public static final String githubURL = "https://github.com/sk7725/BetaMindy";

    @Override
    public void init(){
        Vars.enableConsole = true;

        if(!Vars.headless){
            //Partial credits to Project Unity
            LoadedMod mod = Vars.mods.locateMod("betamindy");
            Func<String, String> stringf = value -> Core.bundle.get("mod." + value);

            mod.meta.displayName = stringf.get(mod.meta.name + ".name");
            mod.meta.description = stringf.get(mod.meta.name + ".description");
            mod.meta.version = mod.meta.version + "\n" + stringf.get(mod.meta.name + ".short");
            mod.meta.author = "[royal]" + mod.meta.author + "[]";
        }
    }

    @Override
    public void loadContent(){
    }
}
