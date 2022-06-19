package betamindy.world.blocks.distribution;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.graphics.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;

import static arc.Core.*;
import static betamindy.world.blocks.storage.Shop.*;

public class RailSorter extends PayloadRail {
    public final static int customSorts = 5;
    public TextureRegion overRegion;
    public TextureRegion[] sorterRegions;
    public Color arrowColor = Pal2.drift;

    public RailSorter(String name){
        super(name);
        outputsPayload = true;
        outputFacing = false;
        saveConfig = true;
        configurable = true;

        config(Integer.class, (RailSorterBuild build, Integer i) -> {
            if(i >= -customSorts && i <= Category.all.length) build.sorter = i;
        });
        configClear((RailSorterBuild build) -> {
            build.sorter = 0;
        });
    }

    @Override
    public void load(){
        super.load();
        overRegion = atlas.find(name + "-over");
        sorterRegions = new TextureRegion[customSorts];
        for(int i = 0; i < 5; i++){
            sorterRegions[i] = atlas.find(name + "-sort-" + (i + 1));
        }
    }

    @Override
    public void drawPlanRegion(BuildPlan req, Eachable<BuildPlan> list){
        super.drawPlanRegion(req, list);

        Draw.rect(overRegion, req.drawx(), req.drawy());
    }

    public static boolean getSort(Payload item, int sorter){
        if(item == null || sorter == 0) return true;
        if(sorter > 0){
            //block category sort
            if(sorter > Category.all.length) return false;
            Category cat = Category.all[sorter - 1];
            return item instanceof BuildPayload bp && bp.block().category == cat;
        }
        return switch(sorter){
            case -1 -> item instanceof BuildPayload;
            case -2 -> item instanceof UnitPayload;
            case -3 -> item instanceof UnitPayload u && !u.unit.type.flying && !isNaval(u.unit.type);
            case -4 -> item instanceof UnitPayload u && u.unit.type.flying && !isNaval(u.unit.type);
            case -5 -> item instanceof UnitPayload u && isNaval(u.unit.type);
            default -> false;
        };
    }

    public TextureRegionDrawable getSortIcon(int sorter){
        if(sorter > Category.all.length || sorter == 0 || sorter < -customSorts) return Icon.settings;
        if(sorter > 0){
            //block category sort
            Category cat = Category.all[sorter - 1];
            return Vars.ui.getIcon(cat.name());
        }
        else return new TextureRegionDrawable(sorterRegions[-sorter - 1]);
    }

    public TextureRegion getSortRegion(int sorter){
        if(sorter > Category.all.length || sorter == 0 || sorter < -customSorts) return atlas.find("center");
        if(sorter < 0) return sorterRegions[-sorter - 1];
        return getSortIcon(sorter).getRegion();
    }

    public static boolean isNaval(UnitType u){
        if(!unitTypeMap.containsKey(u)) return false;
        return unitTypeMap.get(u) == 3;
    }

    public class RailSorterBuild extends PayloadRailBuild {
        public float smoothRot;
        public float controlTime = -1f;
        public int sorter = 0;
        public int setRotation = 0;

        @Override
        public void add(){
            super.add();
            smoothRot = rotdeg();
        }

        public void pickNext(){
            if(item != null && controlTime <= 0f){
                int rotations = 0;
                boolean gets = getSort(item, sorter);
                if(gets && sorter != 0){
                    //only push to front
                    rotation = setRotation;
                    onProximityUpdate();

                    if (next instanceof PayloadConveyorBuild && !(next instanceof RailSorterBuild)) {
                        next.updateTile();
                    }
                }
                else{
                    do {
                        rotation = (rotation + 1) % 4;
                        onProximityUpdate();
                        //force update to transfer if necessary
                        if (next instanceof PayloadConveyorBuild && !(next instanceof RailSorterBuild)) {
                            next.updateTile();
                        }
                        //this condition intentionally uses "accept from itself" conditions, because payload conveyors only accept during the start
                        //"accept from self" conditions are for dropped payloads and are less restrictive
                    } while ((blocked || next == null || !next.acceptPayload(next, item) || (sorter != 0 && ((gets && rotation != setRotation) || (!gets && rotation == setRotation)))) && ++rotations < 4);
                }
            }
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            super.control(type, p1, p2, p3, p4);
            if(type == LAccess.config){
                rotation = (int)p1;
                //when manually controlled, routers do not turn automatically for a while, same as turrets
                controlTime = 60f * 8f;
            }
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            if(source == this || source == null || source.isPayload()) setRotation = rotation;
            else setRotation = (relativeTo(source.tile) + 2) % 4;
            super.handlePayload(source, payload);
            pickNext();
        }

        @Override
        public void moveFailed(){
            pickNext();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            controlTime -= Time.delta;
            smoothRot = Mathf.slerpDelta(smoothRot, rotdeg(), 0.2f);
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            float dst = 0.8f;

            if(item != null || sorter == 0) Draw.mixcol(arrowColor, Math.max((dst - (Math.abs(fract() - 0.5f) * 2)) / dst, 0));
            Draw.rect(topRegion, x, y, smoothRot);
            Draw.reset();

            Draw.rect(edgeRegion, x, y);

            Draw.z(Layer.blockOver);
            if(item != null){
                item.draw();
            }
            drawRoomba();

            Draw.z(Layer.blockOver + 4f);
            Draw.rect(overRegion, x, y);

            if(sorter != 0 && !isPayload()){
                Draw.z(Layer.power - 1f);
                Draw.blend(Blending.additive);
                if(item != null) Draw.mixcol(getSort(item, sorter) ? Color.green : Color.red, 1f);
                Draw.alpha(0.4f + Mathf.absin(8f, 0.2f));
                Draw.rect(getSortRegion(sorter), x, y, 5f * size, 5f * size);
                Draw.mixcol();
                Draw.blend();
                Draw.reset();
            }
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black6, cont -> {
                ButtonGroup<ImageButton> group = new ButtonGroup<>();
                group.setMinCheckCount(0);
                cont.defaults().size(40);

                int i = 0;

                for(int j = -1; j >= -customSorts; j--){
                    sortButton(cont, group, j);

                    if(i++ % 5 == 4){
                        cont.row();
                    }
                }
                for(int j = 1; j <= Category.all.length; j++){
                    sortButton(cont, group, j);

                    if(i++ % 5 == 4){
                        cont.row();
                    }
                }

                //add extra blank spaces so it looks nice
                if(i % 5 != 0){
                    int remaining = 5 - (i % 5);
                    for(int j = 0; j < remaining; j++){
                        cont.image(Styles.black6);
                    }
                }

            });
        }

        public void sortButton(Table cont, ButtonGroup<ImageButton> group, int item){
            ImageButton button = cont.button(Tex.whiteui, Styles.clearTogglei, item < 0 ? 33 : 24, () -> {}).group(group).get(); //why the heck are custom images smaller
            button.changed(() -> configure(button.isChecked() ? item : null));
            button.getStyle().imageUp = getSortIcon(item);
            button.update(() -> button.setChecked(sorter == item));
        }

        @Override
        public Object config(){
            return sorter;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(setRotation == -1 ? rotation : setRotation);
            write.s(sorter);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            setRotation = read.b();
            sorter = read.s();
        }
    }
}
