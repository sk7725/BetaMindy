package betamindy.graphics;

import arc.*;
import arc.graphics.gl.*;
import arc.scene.ui.layout.*;
import arc.util.*;

import static mindustry.Vars.*;

//partial credits to GlennFolker
public class MindyShaders {
    public static @Nullable BlockShader bittrium, dreamJelly, space;
    protected static boolean loaded;

    public static void load(){
        if(headless) return;
        bittrium = new BlockShader("bittrium");
        dreamJelly = new ResBlockShader("dreamjelly");
        space = new ResBlockShader("spacefire");
        loaded = true;
    }

    public static void dispose(){
        if(!headless && loaded){
            bittrium.dispose();
            dreamJelly.dispose();
            space.dispose();
        }
    }

    /** Shaders that get plastered on blocks, notably walls. */
    public static class BlockShader extends Shader {
        public BlockShader(String name){
            super(Core.files.internal("shaders/default.vert"),
                    tree.get("shaders/" + name + ".frag"));
        }

        @Override
        public void apply(){
            setUniformf("u_time", Time.time / Scl.scl(1f));
            //setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_offset",
                    Core.camera.position.x,
                    Core.camera.position.y
            );
        }
    }

    /** Shaders that get plastered on blocks but with differing resolution. */
    public static class ResBlockShader extends BlockShader {
        public ResBlockShader(String name){
            super(name);
        }

        @Override
        public void apply(){
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());
            setUniformf("u_offset",
                    Core.camera.position.x,
                    Core.camera.position.y
            );
        }
    }
}
