package betamindy.ui;

import arc.input.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TouchpadFragment{
    Touchpad touchpad;
    public final static float size = 160f, thresh = 0.15f, timeout = 60 * 5;
    boolean pw, pa, ps, pd;
    float lastShown, lastStarted;
    final String keyboardKey = "keyboard", alwaysKey = "touchpadalways";

    public void showStart(){
        if(!showing()) lastStarted = Time.globalTime;
        lastShown = Time.globalTime;
    }

    public float alpha(){
        if(alwaysShow()) return 1f;
        if(!showing()) return 0f;
        float stime = Time.globalTime - lastStarted;
        if(stime < 15f) return Math.max(stime / 15f, 0f);
        return Mathf.clamp((timeout - (Time.globalTime - lastShown)) / 60f);
    }

    public boolean showing(){
        return (Time.globalTime - lastShown < timeout);
    }

    public boolean alwaysShow(){
        return settings.getBool(alwaysKey) || (mobile && settings.getBool(keyboardKey));
    }

    public void build(Group parent){
        TextureRegionDrawable knobby = new TextureRegionDrawable(atlas.find("check-on-over"));
        knobby.setMinHeight(size * 0.5f);
        knobby.setMinWidth(size * 0.5f);

        parent.fill(full -> {
            full.bottom().left().visible(() -> alwaysShow() || showing());
            touchpad = new Touchpad(size * 0.15f * Scl.scl(1f), new Touchpad.TouchpadStyle(atlas.getDrawable("betamindy-pad-back"), knobby));
            touchpad.changed(() -> {
                float x = touchpad.getKnobPercentX();
                float y = touchpad.getKnobPercentY();
                KeyboardDevice key = input.getKeyboard();

                //i hope nobody reads this
                if(x > thresh){
                    pd = true;
                    key.keyDown(KeyCode.d);
                }
                else if(pd){
                    pd = false;
                    key.keyUp(KeyCode.d);
                }
                if(x < -thresh){
                    pa = true;
                    key.keyDown(KeyCode.a);
                }
                else if(pa){
                    pa = false;
                    key.keyUp(KeyCode.a);
                }

                if(y > thresh){
                    pw = true;
                    key.keyDown(KeyCode.w);
                }
                else if(pw){
                    pw = false;
                    key.keyUp(KeyCode.w);
                }
                if(y < -thresh){
                    ps = true;
                    key.keyDown(KeyCode.s);
                }
                else if(ps){
                    ps = false;
                    key.keyUp(KeyCode.s);
                }
            });

            touchpad.update(() -> {
                touchpad.setColor(1f, 1f, 1f, alpha());
            });

            full.add(touchpad).size(size).padBottom(70f).padLeft(20f);
        });
    }
}
