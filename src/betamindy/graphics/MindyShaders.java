package betamindy.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

//partial credits to GlennFolker
public class MindyShaders {
    public static @Nullable BlockShader bittrium, dreamJelly, space;
    public static @Nullable ModSurfaceShader starryWater, coffee;

    public static CacheLayer.ShaderLayer starryLayer, coffeeLayer;
    protected static boolean loaded;

    public static void load(){
        if(!headless){
            bittrium = new BlockShader("bittrium");
            dreamJelly = new ResBlockShader("dreamjelly");
            space = new ResBlockShader("spacefire");
            starryWater = new SpaceSurfaceShader("starwater");
            coffee = new ModSurfaceShader("coffee");
            loaded = true;
        }
        Log.info("[accent]<FTE + POST (CACHELAYER)>[]");
        starryLayer = new CacheLayer.ShaderLayer(starryWater);
        coffeeLayer = new CacheLayer.ShaderLayer(coffee);
        CacheLayer.add(starryLayer);
        CacheLayer.add(coffeeLayer);
    }

    public static void dispose(){
        if(!headless && loaded){
            bittrium.dispose();
            dreamJelly.dispose();
            space.dispose();
            starryWater.dispose();
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

    /** SurfaceShader but uses a mod fragment asset. */
    public static class ModSurfaceShader extends Shader{
        Texture noiseTex;

        public ModSurfaceShader(String frag){
            super(Core.files.internal("shaders/screenspace.vert"),
                    tree.get("shaders/" + frag + ".frag"));
            loadNoise();
        }

        public ModSurfaceShader(String vertRaw, String fragRaw){
            super(vertRaw, fragRaw);
            loadNoise();
        }

        public String textureName(){
            return "noise";
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }

        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
    }

    public static class SpaceSurfaceShader extends ModSurfaceShader {
        Texture texture;
        String texName;

        public SpaceSurfaceShader(String frag, String space, boolean internal){
            super(frag);

            this.texName = space;

            if(texture == null){
                texture = new Texture(internal ? Core.files.internal("sprites/"+space+".png"): tree.get("shaders/"+space+".png")); //sprites don't get added to the tree - glennn
                texture.setFilter(Texture.TextureFilter.linear);
                texture.setWrap(Texture.TextureWrap.mirroredRepeat);
            }
            //Core.assets.load("shaders/"+space+".png", Texture.class).loaded = t -> { };
        }

        public SpaceSurfaceShader(String frag, String space){
            this(frag, space, false);
        }

        public SpaceSurfaceShader(String frag){
            this(frag, "space", true);
        }

        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);

            setUniformf("u_ccampos", Core.camera.position);
            setUniformf("u_resolutionSpace", Core.graphics.getWidth(), Core.graphics.getHeight());
            setUniformf("u_time", Time.time);

            texture.bind(1);
            renderer.effectBuffer.getTexture().bind(0);

            setUniformi("u_stars", 1);
        }
    }
}
