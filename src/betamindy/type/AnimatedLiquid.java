package betamindy.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import betamindy.graphics.*;
import mindustry.type.*;

import static arc.Core.atlas;

public class AnimatedLiquid extends Liquid {
    public final TextureRegion animIcon = new TextureRegion();
    public TextureRegion[] animRegions;

    /** # of frames per sprite. */
    public float animDelay = 20f;
    protected int animateLevel = 2;

    /** Number of initial sprites. */
    public int sprites = 2;
    /** # of transition frames inserted between two sprites. */
    public int transition = 0;

    //set in load()
    public int n;

    public AnimatedLiquid(String name, Color color){
        super(name, color);
    }

    @Override
    public void load(){
        super.load();
        TextureRegion[] spriteArr = new TextureRegion[sprites];
        for(int i = 0; i < sprites; i++){
            spriteArr[i] = atlas.find(name + i, name);
        }

        n = sprites * (1 + transition);
        animRegions = new TextureRegion[n];
        for(int i = 0; i < sprites; i++){
            if(transition <= 0) animRegions[i] = spriteArr[i];
            else{
                //daewhanjangparty
                animRegions[i * (transition + 1)] = spriteArr[i];
                for(int j = 1; j <= transition; j++){
                    float f = (float)j / (transition + 1);
                    animRegions[i * (transition + 1) + j] = Drawm.blendSprites(spriteArr[i], spriteArr[(i >= sprites - 1) ? 0 : i + 1], f, name + i);
                }
            }
        }
        animIcon.set(animRegions[0]);
    }

    //should be called in Trigger.update
    public void update(){
        animateLevel = Core.settings.getInt("animlevel", 2);
        if(animateLevel >= 1){
            fullIcon.set(animRegions[(int) (Time.globalTime / animDelay) % n]);
            if(animateLevel >= 2) uiIcon.set(fullIcon);
        }
    }
}
