package betamindy.world.blocks.power;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import betamindy.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.power.*;

import static mindustry.Vars.*;

public class FloodLight extends LightBlock{
    public float radius = 450f;
    public float stroke = 8f;

    public float elevation = -1f;

    public float rotateSpeed = 10f;
    public float angleIncrement = 15f;

    public TextureRegion baseRegion;

    public FloodLight(String name){
        super(name);

        outlineIcon = true;

        config(Boolean.class, (FloodLightBuild tile, Boolean angle) -> {
            tile.targetRotation += angleIncrement * Mathf.sign(angle);
        });
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find("block-" + size);
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class FloodLightBuild extends LightBuild implements ExtensionHolder{
        protected Extension light;

        public float targetRotation = 90;
        public float rotation = 90;

        @Override
        public void created(){
            super.created();

            light = Extension.create();
            light.holder = this;
            light.set(x, y);
            light.add();
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            light.remove();
        }

        @Override
        public void updateTile(){
            super.updateTile();
            rotation = Mathf.approachDelta(rotation, targetRotation, rotateSpeed * efficiency());
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color();

            float z = Draw.z();
            Draw.z(Layer.turret);

            Drawf.shadow(region, x - elevation, y - elevation, rotation - 90);
            Draw.rect(region, x, y, rotation - 90);

            Draw.z(z);
        }

        @Override
        public void drawExt(){
            if(renderer != null && (team == Team.derelict || team == player.team() || state.rules.enemyLights)){
                for(int i = -1; i < 2; i++){
                    Tmp.v1.trns(rotation + (i * 90f / stroke), radius);
                    renderer.lights.line(x, y, x + Tmp.v1.x, y + Tmp.v1.y, stroke, Tmp.c1.set(color), 0.5f + Mathf.slope(0.5f + (i / 2f)) * 0.5f);
                }
            }
        }

        @Override
        public float clipSizeExt(){
            return radius * 2f;
        }

        @Override
        public void drawLight(){
            //do nothing, it's overriden in drawExt()
        }

        @Override
        public void buildConfiguration(Table table){
            table
                .button(Icon.left, () -> configure(Boolean.valueOf(true)))
                .size(40f);

            super.buildConfiguration(table);

            table
                .button(Icon.right, () -> configure(Boolean.valueOf(false)))
                .size(40f);
        }
    }
}
