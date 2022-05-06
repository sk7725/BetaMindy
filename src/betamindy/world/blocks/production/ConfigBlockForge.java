package betamindy.world.blocks.production;

import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.Vars;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import betamindy.world.blocks.payloads.*;

@Deprecated
public class ConfigBlockForge extends BlockForge{
    protected int[] minSizes = {1, 3, 5};
    protected int[] maxSizes = {2, 4, 6};
    public ConfigBlockForge(String name){
        super(name);
        minBlockSize = 1;
    }

    @Override
    public void init(){
        super.init();
        maxSizes[2] = maxBlockSize;
    }

    public class ConfigForgeBuild extends BlockForgeBuild{
        private int selection = 0;
        @Override
        public void buildConfiguration(Table table){
            if(recipe != null) selection = recipe.size <= 2 ? 0 : (recipe.size <= 4 ? 1 : 2);
            rebuild(table);
        }

        public void rebuild(Table table){
            table.clearChildren();
            table.top();
            table.table(t -> {
                //float w = 172f;
                t.top();
                t.button("1~2", Styles.flatTogglet, () -> {
                    selection = 0;
                    rebuild(table);
                }).update(b -> {
                    b.setChecked(selection == 0);
                }).size(58f, 40f);
                t.button("3~4", Styles.flatTogglet, () -> {
                    selection = 1;
                    rebuild(table);
                }).update(b -> {
                    b.setChecked(selection == 1);
                }).size(58f, 40f);
                t.button("5+", Styles.flatTogglet, () -> {
                    selection = 2;
                    rebuild(table);
                }).update(b -> {
                    b.setChecked(selection == 2);
                }).size(56f, 40f);
            }).top();
            table.row();
            table.table(t2 -> {
                t2.top().left();
                Seq<Block> blocks = Vars.content.blocks().select(b -> b.isVisible() && b.size >= minSizes[selection] && b.size <= maxSizes[selection]);

                ItemSelection.buildTable(t2, blocks, () -> recipe, this::configure);
            }).top().left().height(200f);
        }
    }
}
