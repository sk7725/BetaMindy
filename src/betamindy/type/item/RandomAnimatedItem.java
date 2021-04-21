package betamindy.type.item;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.ui.*;

public class RandomAnimatedItem extends AnimatedItem {
    public int consecutive = 6;
    public float chance = 0.005f;
    private int left = 0;

    public RandomAnimatedItem(String name, Color color){
        super(name, color);
    }

    @Override
    public void update(){
        animateLevel = Core.settings.getInt("animlevel", 2);
        if(animateLevel >= 3){
            if(left > 0){
                if(Time.globalTime % animDelay < 1){
                    left--;
                    animIcon.set(animRegions[(int)(Mathf.random() * n)]);
                }
            }
            else{
                animIcon.set(animRegions[0]);
                if(Time.globalTime % animDelay < 1){
                    if(Mathf.chance(chance * animDelay)){
                        left = consecutive + Mathf.random(3) - 1;
                    }
                }
            }
        }
    }

    public TextureRegion randomIcon(){
        int dice = Mathf.random(n + (int)(4f / chance) - 1) + 1;
        return dice < n ? animRegions[dice] : animRegions[0];
    }

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
        }*/
        return (stack[2].getMethodName().equals("draw")) ? randomIcon() : animIcon;
    }
}
