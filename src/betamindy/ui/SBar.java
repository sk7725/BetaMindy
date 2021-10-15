package betamindy.ui;


import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.pooling.*;
import mindustry.graphics.*;
import mindustry.ui.*;

/**
 * Bar but better.
 * @author Sharlotte
 * @author Anuke
 */
public class SBar extends Element {
    private static final Rect scissor = new Rect();

    private final Floatp fraction;
    private String name = "";
    private float value, lastValue, blink;
    private final Color blinkColor = new Color();
    private NinePatchDrawable bar, top;
    private boolean shouldBlink = false;
    private boolean exact = false;
    private boolean outline = true;
    private float spriteWidth = 8f;

    public SBar(Prov<String> name, Prov<Color> color, Floatp fraction, String sprite, int w, int h){
        this.fraction = fraction;
        try{
            lastValue = value = Mathf.clamp(fraction.get());
            bar = (NinePatchDrawable) drawable(sprite, w, w, h, h);
            top = (NinePatchDrawable) drawable(sprite + "-top", w, w, h, h);
            spriteWidth = Core.atlas.find(sprite + "-top").width;
        }catch(Exception e){ //getting the fraction may involve referring to invalid data
            lastValue = value = 0f;
        }
        update(() -> {
            try{
                this.name = name.get();
                setColor(color.get());
            }catch(Exception e){ //getting the fraction may involve referring to invalid data
                this.name = "";
            }
        });
    }

    public SBar(Prov<String> name, Prov<Color> color, Floatp fraction){
        this(name, color, fraction, "betamindy-barS", 10, 9);
    }

    public void reset(float value){
        this.value = lastValue = blink = value;
    }

    public SBar blink(Color color){
        blinkColor.set(color);
        return this;
    }

    public SBar blink(Color color, boolean auto){
        blinkColor.set(color);
        shouldBlink = auto;
        return this;
    }

    public SBar exact(boolean ex){
        exact = ex;
        return this;
    }

    public SBar outline(boolean o){
        outline = o;
        return this;
    }

    public Drawable drawable(String name, int left, int right, int top, int bottom){
        Drawable out;

        TextureAtlas.AtlasRegion region = Core.atlas.find(name);

        int[] splits = {left, right, top, bottom};
        NinePatch patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
        int[] pads = region.pads;
        if(pads != null) patch.setPadding(pads[0], pads[1], pads[2], pads[3]);
        out = new ScaledNinePatchDrawable(patch, 1f);

        return out;
    }

    public void flash(){
        blink = 1f;
    }

    @Override
    public void draw(){
        if(fraction == null) return;

        float computed;
        try{
            computed = Mathf.clamp(fraction.get());
        }catch(Exception e){ //getting the fraction may involve referring to invalid data
            computed = 0f;
        }

        if(shouldBlink && lastValue > computed){
            blink = 1f;
            lastValue = computed;
        }

        if(Float.isNaN(lastValue)) lastValue = 0;
        if(Float.isInfinite(lastValue)) lastValue = 1f;
        if(Float.isNaN(value)) value = 0;
        if(Float.isInfinite(value)) value = 1f;
        if(Float.isNaN(computed)) computed = 0;
        if(Float.isInfinite(computed)) computed = 1f;

        blink = Mathf.lerpDelta(blink, 0f, 0.1f);
        value = exact ? computed : Mathf.lerpDelta(value, computed, 0.05f);

        Draw.colorl(0.1f);
        bar.draw(x, y, width, height);
        Draw.color(color, blinkColor, blink);

        float topWidth = width * value;

        if(topWidth > spriteWidth){
            top.draw(x, y, topWidth, height);
        }else{
            if(ScissorStack.push(scissor.set(x, y, topWidth, height))){
                top.draw(x, y, spriteWidth, height);
                ScissorStack.pop();
            }
        }

        //The fuk is this, shar
        /*
        Draw.color(color, blinkColor, blink);
        float topWidthReal = width * (Math.min(value, computed));

        if(topWidthReal > spriteWidth){
            top.draw(x, y, topWidthReal, height);
        }else{
            if(ScissorStack.push(scissor.set(x, y, topWidthReal, height))){
                top.draw(x, y, spriteWidth, height);
                ScissorStack.pop();
            }
        }*/

        Draw.color();

        Font font = outline ? Fonts.outline : Fonts.def;
        GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        //font.getData().setScale(Scl.scl());
        lay.setText(font, name);
        font.setColor(Color.white);
        font.draw(name, x + width / 2f - lay.width / 2f, y + height / 2f + lay.height / 2f + 1);
        //font.getData().setScale(Scl.scl());

        Pools.free(lay);
        Draw.reset();
    }
}