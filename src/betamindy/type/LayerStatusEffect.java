package betamindy.type;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class LayerStatusEffect extends StatusEffect {
    public float layer = Layer.shields;
    public float opacity = 1f;
    public boolean useTeamColor = false;
    public LayerStatusEffect(String name){
        super(name);
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        Draw.z(layer);
        Draw.color();
        Draw.mixcol(useTeamColor ? unit.team.color : color, 1f);
        Draw.alpha(Vars.renderer.animateShields ? opacity : Mathf.absin(17f, opacity * 0.6f));
        Draw.rect(unit.type.shadowRegion, unit.x, unit.y, unit.rotation - 90);
        Draw.reset();
    }
}
