package betamindy.type.item;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.graphics.*;
import mindustry.*;
import mindustry.type.*;
import mindustry.ui.*;

import static arc.Core.*;

public class RandomAnimatedItem extends Item{
    public final TextureRegion animIcon = new TextureRegion();
    public TextureRegion[] animRegions;

    public int consecutive = 6;
    public float chance = 0.005f;
    private int left = 0;

    public int n;

    public RandomAnimatedItem(String name, Color color){
        super(name, color);
    }

    @Override
    public void load(){
        super.load();
        TextureRegion[] spriteArr = new TextureRegion[frames];
        for(int i = 0; i < frames; i++){
            spriteArr[i] = atlas.find(name + i, name);
        }

        n = frames * (1 + transitionFrames);
        animRegions = new TextureRegion[n];
        for(int i = 0; i < frames; i++){
            if(transitionFrames <= 0) animRegions[i] = spriteArr[i];
            else{
                //daewhanjangparty
                animRegions[i * (transitionFrames + 1)] = spriteArr[i];
                for(int j = 1; j <= transitionFrames; j++){
                    float f = (float)j / (transitionFrames + 1);
                    animRegions[i * (transitionFrames + 1) + j] = Drawm.blendSprites(spriteArr[i], spriteArr[(i >= frames - 1) ? 0 : i + 1], f, name + i);
                }
            }
        }
        animIcon.set(animRegions[0]);
    }

    public void update(){
        int animateLevel = Core.settings.getInt("animlevel", 2);
        if(animateLevel >= 1){
            if(left > 0){
                if(Time.globalTime % frameTime < Time.delta){
                    left--;
                    fullIcon.set(animRegions[(int)(Mathf.random() * n)]);
                }
            }
            else{
                fullIcon.set(animIcon);
                if(Time.globalTime % frameTime < Time.delta){
                    if(Mathf.chance(chance * frameTime)){
                        left = consecutive + Mathf.random(3) - 1;
                    }
                }
            }
            if(animateLevel >= 2) uiIcon.set(fullIcon);
        }
    }

    public TextureRegion randomIcon(){
        int dice = Mathf.random(n + (int)(4f / chance) - 1) + 1;
        return dice < n ? animRegions[dice] : animRegions[0];
    }

    /*
    @Override
    public TextureRegion icon(Cicon icon){
        if(animateLevel <= 0) return super.icon(icon);
        if(animateLevel <= 2) return randomIcon();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace(); //Credit to @GlennFolker

        /*
        if(Vars.state.isPaused()) return randomIcon();
        Log.info("###START###");
        for(int i = 0; i < stack.length; i++){
            Log.info(i + " | " + stack[i].getMethodName() + " | " + stack[i].getClassName());
        }/
        return (stack[2].getMethodName().equals("draw")) ? randomIcon() : animIcon;
    }*/
}
