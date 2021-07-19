/*
	Copyright (c) sk7725 2020
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package betamindy.graphics;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.util.*;
import betamindy.ui.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.world.*;

import static betamindy.BetaMindy.hardmode;
import static mindustry.Vars.renderer;

public class Drawm {
    public static void construct(Building t, TextureRegion region, float rotation, float progress, float speed, float time, Color color){
        Shaders.build.region = region;
        Shaders.build.progress = progress;
        Shaders.build.color.set(color);
        Shaders.build.color.a = speed;
        Shaders.build.time = -time / 20f;

        Draw.shader(Shaders.build);
        Draw.rect(region, t.x, t.y, rotation);
        Draw.shader();

        Draw.color(color);
        Draw.alpha(speed);

        Lines.lineAngleCenter(t.x + Mathf.sin(time, 20f, Vars.tilesize / 2f * t.block.size - 2f), t.y, 90, t.block.size * Vars.tilesize - 4f);

        Draw.reset();
    }

    public static void constructLineless(float x, float y, TextureRegion region, float rotation, float progress, float speed, float time, Color color){
        Shaders.build.region = region;
        Shaders.build.progress = progress;
        Shaders.build.color.set(color);
        Shaders.build.color.a = speed;
        Shaders.build.time = -time / 20f;

        Draw.shader(Shaders.build);
        Draw.rect(region, x, y, rotation);
        Draw.shader();

        Draw.reset();
    }

    public static void spark(float x, float y, float size, float width, float r){
        Drawf.tri(x, y, width, size, r);
        Drawf.tri(x, y, width, size, r+180f);
        Drawf.tri(x, y, width, size, r+90f);
        Drawf.tri(x, y, width, size, r+270f);
    }

    public static void shard(float x, float y, float size, float width, float r){
        Drawf.tri(x, y, width, size, r);
        Drawf.tri(x, y, width, size, r+180f);
    }

    public static void drawBit(boolean bit, float x, float y, float size, float stroke){
        if(bit){
            Lines.stroke(stroke*1.2f);
            Lines.lineAngleCenter(x, y, 90f, size);
        }
        else{
            Lines.stroke(stroke);
            Lines.poly(x, y, 4, size, 45f);
        }
    }

    public static void koruh(float x, float y, float size, float r, char c){
        Draw.rect(AncientKoruh.eng(c), x, y, size, size, r);
    }

    public static void koruh(float x, float y, float size, float r, String str, int index){
        Draw.rect(AncientKoruh.eng(str, index), x, y, size, size, r);
    }

    public static void portal(float x, float y, float radius, Color color1, Color color2){
        if(Vars.renderer.bloom == null){
            Tmp.c2.set(color1).lerp(color2, Mathf.sin(Time.globalTime / 35f) * 0.5f + 0.5f);
            Tmp.c3.set(color1).lerp(color2, Mathf.cos(Time.globalTime / 35f) * 0.5f + 0.5f);
            color1 = Tmp.c2;
            color2 = Tmp.c3;
        }
        Draw.z(Layer.groundUnit - 1f);
        Draw.color(Color.black);
        Fill.circle(x, y, radius);
        Draw.z(Layer.effect);

        int n = 9;
        Draw.color(color1);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 2f, radius - 2f).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 12f), 120f, i * 360f / n - Time.globalTime / 2f + 100f);
        }
        n = 6;
        Draw.color(color2);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 3f, radius - 4f).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 16f), 160f, i * 360f / n - Time.globalTime / 3f + 110f);
        }

        n = 4;
        for(int i = 0; i < n; i++){
            Lines.stroke(Math.min(radius, 8.2f));
            Draw.alpha(1f - ((float)i) / n);
            Lines.circle(x, y, Math.max(1f ,radius - i * 8f));
        }

        Fill.light(x, y, Lines.circleVertices(radius), radius, Color.clear, color1);

        n = 64;
        float m = 11f;
        Draw.alpha(0.7f);
        for(int i = 0; i < n; i++){
            float s = Mathf.randomSeed(n + 17 * i) * m;
            float speed = (Mathf.randomSeed(i * n) + 0.5f) * (m + 1f - s) * 0.1f;
            Draw.color(color1, color2, Mathf.randomSeed(i - 3 * n));
            Tmp.v1.trns(i * 360f / n - Time.globalTime * speed + Mathf.randomSeedRange(n + i, 180f / n), radius + 14f - s * 1.5f + Mathf.randomSeedRange(n - i, 3f)).add(x, y);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, Math.min(radius, s));
        }
        Draw.color();
    }

    public static void lightningOrbOld(float x, float y, float r, Color color1, Color color2){
        Draw.z(Layer.effect - 0.001f);
        Draw.color(color1);
        Fill.circle(x, y, r * 0.6f);
        Drawf.tri(x, y, r, r * 1.6f, Time.time);
        Drawf.tri(x, y, r, r * 1.6f, Time.time + 180f);
        Draw.color(Vars.renderer.bloom == null ? color1 : color2);
        Drawf.tri(x, y, r * 0.7f, r * 1.3f, Time.time * -1.5f + 60f);
        Drawf.tri(x, y, r * 0.7f, r * 1.3f, Time.time * -1.5f + 60f + 180f);

        Draw.z(Layer.effect + 0.002f);
        Draw.color();
        Drawf.tri(x, y, r * 0.6f, r * 0.7f, Time.time * 1.7f + 60f);
        Drawf.tri(x, y, r * 0.6f, r * 0.7f, Time.time * 1.7f + 60f + 180f);
        Fill.circle(x, y, r * 0.45f);

        Draw.blend(Blending.additive);
        Lines.stroke(Math.min(1.5f, r));
        Draw.color(color1);
        Lines.poly(x, y, Mathf.random(7) + 5, r * 0.9f, Mathf.random(360f));
        Lines.stroke(Math.min(1f, r));
        Draw.color(color2);
        Lines.poly(x, y, Mathf.random(7) + 5, r * 1.1f, Mathf.random(360f));
        Draw.color();
        Draw.blend();
    }

    public static void lightningOrb(float x, float y, float radius, Color color1, Color color2){
        Draw.z(Layer.effect - 0.001f);
        Draw.color(color1);
        Fill.circle(x, y, radius);

        int n = 3;
        Draw.color(color2);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 3f, radius - 5f).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 7f), radius * 4f, i * 360f / n - Time.globalTime / 3f + 110f);
        }
        n = 4;
        Draw.color(color1);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 2f, radius - 3f).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 7f), radius * 5f, i * 360f / n - Time.globalTime / 2f + 100f);
        }

        Draw.z(Layer.effect + 0.002f);
        Draw.color();
        Drawf.tri(x, y, radius * 0.6f, radius * 1.7f, Time.time * 1.7f + 60f);
        Drawf.tri(x, y, radius * 0.6f, radius * 1.7f, Time.time * 1.7f + 60f + 180f);
        Fill.circle(x, y, radius * 0.8f);

        Draw.blend(Blending.additive);
        Lines.stroke(Math.min(1.5f, radius));
        Draw.color(color1);
        Lines.poly(x, y, Mathf.random(7) + 5, radius * 1.8f, Mathf.random(360f));
        Lines.stroke(Math.min(1f, radius));
        Draw.color(color2);
        Lines.poly(x, y, Mathf.random(7) + 5, radius * 2.2f, Mathf.random(360f));
        Draw.color();
        Draw.blend();

        if(renderer.lights.enabled()) Drawf.light(x, y, radius * 9f, color2, 1f);
    }

    public static void altarOrb(float x, float y, float radius, float fin){
        altarOrb(x, y, radius, fin, 45f, hardmode.getRandomColor(Tmp.c1, (long) Time.globalTime), 4);
    }

    public static void altarOrb(float x, float y, float radius, float f1, float interval, Color c, int spikes){
        f1 *= Mathf.sin(Time.globalTime, 20f, 0.15f) + 0.8f;
        float f2 = 1f - (Time.globalTime % interval / interval);
        Draw.z(Layer.effect - 0.01f);
        Draw.color(c);
        Fill.circle(x, y, 1.3f * radius * f1);
        Draw.z(Layer.effect);
        Draw.color();
        Fill.circle(x, y, radius * f1);
        for(int j = 0; j < spikes; j++){
            float r = Mathf.randomSeed(j + (int)(Time.globalTime / interval)) * 360f;
            Draw.z(Layer.effect - 0.01f);
            Draw.color(c);
            Tmp.v1.trns(r, f1 * 1.1f * radius).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, 6f * f1, 1.8f * radius * f1 * f2, r);

            Draw.z(Layer.effect);
            Draw.color();
            Tmp.v1.trns(r, f1 * 0.8f * radius).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, 3f * f1, 1.5f * radius * f1 * f2, r);
        }
    }

    /** Generates all team regions for this block. Call #getTeamRegion(Block) afterwards to get the region. */
    public static void generateTeamRegion(MultiPacker packer, Block b){
        PixmapRegion teamr = Core.atlas.getPixmap(b.name + "-team");

        for(Team team : Team.all){
            if(team.hasPalette){
                Pixmap out = new Pixmap(teamr.width, teamr.height, teamr.pixmap.getFormat());
                out.setBlending(Pixmap.Blending.none);
                Color pixel = new Color();
                for(int x = 0; x < teamr.width; x++){
                    for(int y = 0; y < teamr.height; y++){
                        int color = teamr.getPixel(x, y);
                        int index = color == 0xffffffff ? 0 : color == 0xdcc6c6ff ? 1 : color == 0x9d7f7fff ? 2 : -1;
                        out.draw(x, y, index == -1 ? pixel.set(teamr.getPixel(x, y)) : team.palette[index]);
                    }
                }
                packer.add(PageType.main, b.name + "-team-" + team.name, out);

                //for 6.0 compatibility only! TODO remove in 7.0
                if(Version.number <= 6){
                    Core.atlas.addRegion(b.name + "-team-" + team.name, new TextureRegion(new Texture(out)));
                }
            }
        }

        //force reload of team region
        b.load();
    }

    /** @return the sharded team texture region for this block */
    public static TextureRegion getTeamRegion(Block b){
        return Core.atlas.find(b.name + "-team-sharded");
    }

    /** Draws a sprite that should be lightwise correct, using 4 sprites each colored with a different lighting angle. */
    public static void spinSprite(TextureRegion[] sprites, float x, float y, float r){
        r = Mathf.mod(r, 360f);
        int now = (((int)(r + 45f)) / 90) % 4;

        Draw.rect(sprites[now], x, y, r);
        Draw.alpha(((r + 45f) % 90f) / 90f);
        Draw.rect(sprites[(now + 1) % 4], x, y, r);
        Draw.alpha(1f);
    }

    /** Draws a sprite that should be lightwise correct. Provided sprite must be symmetrical. */
    public static void spinSprite(TextureRegion region, float x, float y, float r){
        r = Mathf.mod(r, 90f);
        Draw.rect(region, x, y, r);
        Draw.alpha(r / 90f);
        Draw.rect(region, x, y, r - 90f);
        Draw.alpha(1f);
    }
    //PR to drills?

    /** Outlines a given textureRegion. Run in createIcons. */
    public static void outlineRegion(MultiPacker packer, TextureRegion tex, Color outlineColor, String name){
        final int radius = 4;
        PixmapRegion region = Core.atlas.getPixmap(tex);
        Pixmap out = new Pixmap(region.width, region.height);
        Color color = new Color();
        for(int x = 0; x < region.width; x++){
            for(int y = 0; y < region.height; y++){

                region.getPixel(x, y, color);
                out.draw(x, y, color);
                if(color.a < 1f){
                    boolean found = false;
                    outer:
                    for(int rx = -radius; rx <= radius; rx++){
                        for(int ry = -radius; ry <= radius; ry++){
                            if(Structs.inBounds(rx + x, ry + y, region.width, region.height) && Mathf.within(rx, ry, radius) && color.set(region.getPixel(rx + x, ry + y)).a > 0.01f){
                                found = true;
                                break outer;
                            }
                        }
                    }
                    if(found){
                        out.draw(x, y, outlineColor);
                    }
                }
            }
        }
        packer.add(MultiPacker.PageType.main, name, out);
    }

    /** Outlines a list of regions. Run in createIcons. */
    public static void outlineRegion(MultiPacker packer, TextureRegion[] textures, Color outlineColor, String name){
        for(int i = 0; i < textures.length; i++){
            outlineRegion(packer, textures[i], outlineColor, name + "-" + i);
        }
    }

    /** Lerps 2 TextureRegions. */
    public static TextureRegion blendSprites(TextureRegion a, TextureRegion b, float f, String name){
        PixmapRegion r1 = Core.atlas.getPixmap(a);
        PixmapRegion r2 = Core.atlas.getPixmap(b);

        Pixmap out = new Pixmap(r1.width, r1.height, r1.pixmap.getFormat());
        out.setBlending(Pixmap.Blending.none);
        Color color1 = new Color();
        Color color2 = new Color();

        for(int x = 0; x < r1.width; x++){
            for(int y = 0; y < r1.height; y++){

                r1.getPixel(x, y, color1);
                r2.getPixel(x, y, color2);
                out.draw(x, y, color1.lerp(color2, f));
            }
        }

        Texture texture  = new Texture(out);
        return Core.atlas.addRegion(name + "-blended-" + (int)(f * 100), new TextureRegion(texture));
    }
}
