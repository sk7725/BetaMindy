package betamindy.type;

import arc.scene.style.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;

import static arc.Core.atlas;

public class SpecialSectorPreset extends SectorPreset {
    public String spriteName = "betamindy-shar-sector";
    public @Nullable TextureRegionDrawable spriteIcon = null;

    public SpecialSectorPreset(String name, Planet planet, int sector){
        super(name, planet, sector);
    }

    public SpecialSectorPreset(String name, Planet planet, int sector, TextureRegionDrawable icon){
        super(name, planet, sector);
        this.spriteIcon = icon;
    }

    @Override
    public void loadIcon(){
        if(spriteIcon != null){
            uiIcon = fullIcon = spriteIcon.getRegion();
        }
        else if(Icon.book != null){
            uiIcon = fullIcon = atlas.find(spriteName, Icon.book.getRegion());
        }
    }
}
