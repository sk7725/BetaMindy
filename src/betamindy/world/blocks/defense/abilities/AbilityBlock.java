package betamindy.world.blocks.defense.abilities;

import arc.struct.*;
import mindustry.entities.abilities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class AbilityBlock extends Block {
    public Ability abilityType;

    public AbilityBlock(String name){
        super(name);

        update = true;
        solid = true;
        rotate = false;
    }

    public class AbilityBuild extends Building{
        public Ability ability;

        @Override
        public void pickedUp(){
            super.pickedUp();
        }

        @Override
        public void dropped(){
            super.dropped();
        }

        @Override
        public Building create(Block block, Team team){
            Building newb = super.create(block, team);
            ((AbilityBuild)newb).ability = abilityType.copy();
            return newb;
        }
    }
}
