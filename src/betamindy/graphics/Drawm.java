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

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import betamindy.ui.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.world.*;

import static arc.graphics.g2d.Lines.*;
import static betamindy.BetaMindy.*;
import static mindustry.Vars.*;

public class Drawm {
    private static final Vec2 vec1 = new Vec2(), vec2 = new Vec2(), vec3 = new Vec2(), vec4 = new Vec2();
    public static Color[] starColors = {Pal2.deepRed, Pal2.deepRed, Pal.remove, Color.orange, Color.yellow, Color.white, Pal.lancerLaser, Color.sky, Color.valueOf("70d2ff"), Color.royal, Pal2.deepBlue};

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

    public static void shaderRect(float x, float y, float z, TextureRegion region, float r, Shader shader){
        Draw.draw(z, () -> {
            Draw.shader(shader);
            Draw.rect(region, x, y, r);
            Draw.shader();
            Draw.reset();
        });
    }

    public static void spark(float x, float y, float size, float width, float r){
        Drawf.tri(x, y, width, size, r);
        Drawf.tri(x, y, width, size, r+180f);
        Drawf.tri(x, y, width, size, r+90f);
        Drawf.tri(x, y, width, size, r+270f);
    }

    public static void sparks(int sides, float x, float y, float size, float width, float r){
        float f = 360f / (float)sides;
        for(int i = 0; i < sides; i++){
            Drawf.tri(x, y, width, size, r + i * f);
        }
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

    /** Draws an ellipse.
     * @author MeepofFaith
     */
    public static void ellipse(float x, float y, float rad, float wScl, float hScl, float rot){
        float sides = Lines.circleVertices(rad);
        float space = 360 / sides;
        float r1 = rad - getStroke() / 2f, r2 = rad + getStroke() / 2f;

        for(int i = 0; i < sides; i++){
            float a = space * i;
            vec1.trns(rot,
                    r1 * wScl * Mathf.cosDeg(a),
                    r1 * hScl * Mathf.sinDeg(a)
            );
            vec2.trns(rot,
                    r1 * wScl * Mathf.cosDeg(a + space),
                    r1 * hScl * Mathf.sinDeg(a + space)
            );
            vec3.trns(rot,
                    r2 * wScl * Mathf.cosDeg(a + space),
                    r2 * hScl * Mathf.sinDeg(a + space)
            );
            vec4.trns(rot,
                    r2 * wScl * Mathf.cosDeg(a),
                    r2 * hScl * Mathf.sinDeg(a)
            );
            Fill.quad(
                    x + vec1.x, y + vec1.y,
                    x + vec2.x, y + vec2.y,
                    x + vec3.x, y + vec3.y,
                    x + vec4.x, y + vec4.y
            );
        }
    }

    public static void ellipse(float x, float y, float rad, float wScl, float hScl, float rot, float layerUnder, float layerOver){
        float sides = Lines.circleVertices(rad);
        float space = 360 / sides;
        float r1 = rad - getStroke() / 2f, r2 = rad + getStroke() / 2f;

        for(int i = 0; i < sides; i++){
            float a = space * i;
            Draw.z(i > sides / 2 ? layerUnder : layerOver);
            vec1.trns(rot,
                    r1 * wScl * Mathf.cosDeg(a),
                    r1 * hScl * Mathf.sinDeg(a)
            );
            vec2.trns(rot,
                    r1 * wScl * Mathf.cosDeg(a + space),
                    r1 * hScl * Mathf.sinDeg(a + space)
            );
            vec3.trns(rot,
                    r2 * wScl * Mathf.cosDeg(a + space),
                    r2 * hScl * Mathf.sinDeg(a + space)
            );
            vec4.trns(rot,
                    r2 * wScl * Mathf.cosDeg(a),
                    r2 * hScl * Mathf.sinDeg(a)
            );
            Fill.quad(
                    x + vec1.x, y + vec1.y,
                    x + vec2.x, y + vec2.y,
                    x + vec3.x, y + vec3.y,
                    x + vec4.x, y + vec4.y
            );
        }
    }

    public static void chain(TextureRegion line, TextureRegion edge, float x, float y, float x2, float y2){
        chain(line, edge, x, y, x2, y2, false);
    }

    public static void chain(TextureRegion line, TextureRegion edge, float x, float y, float x2, float y2, boolean drawLight){
        chain(null, line, edge, x, y, x2, y2, 1f, drawLight);
    }

    public static void chain(@Nullable Team team, TextureRegion line, TextureRegion edge, float x, float y, float x2, float y2, float scale, boolean drawLight){
        float rot = Mathf.angle(x2 - x, y2 - y);
        float len = Mathf.len(x2 - x, y2 - y);
        float c = (line.width - 0.01f) * scale * Draw.scl;
        int n = (int)(len / c);
        if(n > 1){
            Tmp.v1.trns(rot, c * 0.5f).add(x, y);
            Draw.rect(edge, Tmp.v1.x, Tmp.v1.y, c, edge.height * scale * Draw.scl, rot + 180);
            for(int i = 1; i < n - 1; i++){
                Tmp.v1.trns(rot, c * (i + 0.5f)).add(x, y);
                Draw.rect(line, Tmp.v1.x, Tmp.v1.y, c, line.height * scale * Draw.scl, rot);
            }
            Tmp.v1.trns(rot, c * (n - 0.5f)).add(x, y);
            Draw.rect(edge, Tmp.v1.x, Tmp.v1.y, c, edge.height * scale * Draw.scl, rot);
        }

        if(drawLight) Drawf.light(x, y, x2, y2);
    }

    public static void border(float x1, float y1, float x2, float y2, Color center){
        border(x1, y1, x2, y2, 0.4f, center, Pal2.clearWhite);
    }

    public static void border(float x1, float y1, float x2, float y2, float h, Color center, Color edge){
        float c1f = center.toFloatBits();
        float c2f = edge.toFloatBits();
        float x3 = x1 + h * (x1 - Core.camera.position.x);
        float y3 = y1 + h * (y1 - Core.camera.position.y);
        float x4 = x2 + h * (x2 - Core.camera.position.x);
        float y4 = y2 + h * (y2 - Core.camera.position.y);

        Fill.quad(x1, y1, c1f, x2, y2, c1f, x4, y4, c2f, x3, y3, c2f);
    }

    public static void koruh(float x, float y, float size, float r, char c){
        Draw.rect(AncientKoruh.eng(c), x, y, size, size, r);
    }

    public static void koruh(float x, float y, float size, float r, String str, int index){
        Draw.rect(AncientKoruh.eng(str, index), x, y, size, size, r);
    }

    public static void petal(float x, float y, float size, float r, float roll){
        flipSprite(Core.atlas.find("betamindy-petal"), x, y, r, roll, size, size, Color.white, Pal2.darkPink);
    }

    public static void coin(float x, float y, float size, float r, float roll){
        //"anucoin" is for ui, "coin" is for anything other than ui
        flipSprite(AnucoinTex.coin, x, y, r, roll, size, size, Color.white, Pal2.darkCoin);
    }

    public static void coinSimple(float x, float y, float size, float r, float roll){
        flipSpriteSimple(AnucoinTex.coin, x, y, r, roll, size, size);
    }

    public static void portal(float x, float y, float radius, Color color1, Color color2){
        portal(x, y, radius, color1, color2, 1f, 9, 64, 0f);
    }

    public static void portal(float x, float y, float radius, Color color1, Color color2, float sizeScl, int branches, int dust, float offset){
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

        int n = branches;
        Draw.color(color1);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 2f + offset, radius - 2f * sizeScl).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 12f * sizeScl), 120f * sizeScl, i * 360f / n - Time.globalTime / 2f + 100f + offset);
        }
        n = branches / 3 + 3;
        Draw.color(color2);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 3f + offset, radius - 4f * sizeScl).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 16f * sizeScl), 160f * sizeScl, i * 360f / n - Time.globalTime / 3f + 110f + offset);
        }

        n = 4;
        for(int i = 0; i < n; i++){
            Lines.stroke(Math.min(radius, 8.2f * sizeScl));
            Draw.alpha(1f - ((float)i) / n);
            Lines.circle(x, y, Math.max(1f ,radius - i * 8f * sizeScl));
        }

        Fill.light(x, y, Lines.circleVertices(radius), radius, Color.clear, color1);

        n = dust;
        float m = 11f * sizeScl;
        Draw.alpha(0.7f);
        for(int i = 0; i < n; i++){
            float s = Mathf.randomSeed(n + 17 * i) * m;
            float speed = (Mathf.randomSeed(i * n) + 0.5f) * (m + 1f - s) * 0.1f;
            Draw.color(color1, color2, Mathf.randomSeed(i - 3 * n));
            Tmp.v1.trns(i * 360f / n - Time.globalTime * speed + Mathf.randomSeedRange(n + i, 180f / n) + offset, radius + 14f * sizeScl - s * 1.5f + Mathf.randomSeedRange(n - i, 3f)).add(x, y);
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
        altarOrb(x, y, radius, fin, 45f, hardmode.getRandomColor(Tmp.c1, (int)(Time.globalTime / 45)), 4);
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
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, 6f * f1, 2.1f * radius * f1 * f2, r);

            Draw.z(Layer.effect);
            Draw.color();
            Tmp.v1.trns(r, f1 * 0.8f * radius).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, 3f * f1, 1.5f * radius * f1 * f2, r);
        }
    }

    public static void spikeRing(float x, float y, int spikes, float rotation, float radius, float width, float height, boolean invert){
        float ang = 0;
        float xAdd, yAdd;

        for(int i = 0; i < spikes; i++){
            xAdd = x + Mathf.cosDeg(ang + rotation) * radius;
            yAdd = y + Mathf.sinDeg(ang + rotation) * radius;

            Drawf.tri(xAdd, yAdd, width, height, ang + rotation + (invert ? 180f : 0f));
            ang += 360f / spikes;
        }
    }

    public static void spikeRing(float x, float y, int spikes, float rotation, float radius, float width, float height, float size){
        spikeRing(x, y, spikes, rotation, radius, width, height, false);
    }

    public static void spikeRing(float x, float y, int spikes, float rotation, float radius, float size, boolean invert){
        spikeRing(x, y, spikes, rotation, radius, size, size, invert);
    }

    public static void spikeRing(float x, float y, int spikes, float rotation, float radius, float size){
        spikeRing(x, y, spikes, rotation, radius, size, false);
    }

    public static Color starColor(float lerp){
        return Tmp.c3.lerp(starColors, lerp);
    }

    /** Generates all team regions for this block. Call #getTeamRegion(Block) afterwards to get the region. */
    public static void generateTeamRegion(MultiPacker packer, Block b){
        PixmapRegion teamr = Core.atlas.getPixmap(b.name + "-team");

        for(Team team : Team.all){
            if(team.hasPalette){
                Pixmap out = new Pixmap(teamr.width, teamr.height);
                for(int x = 0; x < teamr.width; x++){
                    for(int y = 0; y < teamr.height; y++){
                        int color = teamr.getRaw(x, y);
                        int index = color == 0xffffffff ? 0 : color == 0xdcc6c6ff ? 1 : color == 0x9d7f7fff ? 2 : -1;
                        out.setRaw(x, y, index == -1 ? teamr.getRaw(x, y) : team.palettei[index]);
                    }
                }
                packer.add(PageType.main, b.name + "-team-" + team.name, out);
            }
        }

        //force reload of team region
        b.load();
    }

    /** ONly for blocks with 2 or more team regions.
     * Generates all team regions for this region. Call #loadCustomTeamRegion(String) in load() afterwards to get the region. Must be followed by a #generateTeamRegion. */
    public static void customTeamRegion(MultiPacker packer, String name){
        PixmapRegion teamr = Core.atlas.getPixmap(name + "-team");

        for(Team team : Team.all){
            if(team.hasPalette){
                Pixmap out = new Pixmap(teamr.width, teamr.height);
                for(int x = 0; x < teamr.width; x++){
                    for(int y = 0; y < teamr.height; y++){
                        int color = teamr.getRaw(x, y);
                        int index = color == 0xffffffff ? 0 : color == 0xdcc6c6ff ? 1 : color == 0x9d7f7fff ? 2 : -1;
                        out.setRaw(x, y, index == -1 ? teamr.getRaw(x, y) : team.palettei[index]);
                    }
                }
                packer.add(PageType.main, name + "-team-" + team.name, out);
            }
        }
    }

    /** @return the sharded team texture region for this block */
    public static TextureRegion getTeamRegion(Block b){
        return Core.atlas.find(b.name + "-team-sharded");
    }

    /** Loads the custom team regions. */
    public static TextureRegion[] loadCustomTeamRegion(String name){
        TextureRegion[] ret = new TextureRegion[Team.all.length];
        TextureRegion def = Core.atlas.find(name + "-team");
        for(Team team : Team.all){
            ret[team.id] = def.found() && team.hasPalette ? Core.atlas.find(name + "-team-" + team.name, def) : def;
        }
        return ret;
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

    /** Draws a sprite that should be light-wise correct. Provided sprite must be symmetrical in shape. */
    public static void spinSprite(TextureRegion region, float x, float y, float r){
        r = Mathf.mod(r, 90f);
        Draw.rect(region, x, y, r);
        Draw.alpha(r / 90f);
        Draw.rect(region, x, y, r - 90f);
        Draw.alpha(1f);
    }

    /** Filps a sprite like a coin.
     * @param region Note that this region is flipped left-right, the y-axis being the axis.
     * @param rotation Technically the yaw.
     * @param roll Negative values tilt the sprite to the east (cw), positive to the west (ccw).
     */
    public static void flipSprite(TextureRegion region, float x, float y, float rotation, float roll){
        flipSprite(region, x, y, rotation, roll, Color.white, Pal.darkestGray);
    }

    public static void flipSprite(TextureRegion region, float x, float y, float rotation, float roll, Color lightColor, Color darkColor){
        flipSprite(region, x, y, rotation, roll, region.width * Draw.scl * Draw.xscl, region.height * Draw.scl * Draw.yscl, lightColor, darkColor);
    }

    public static void flipSprite(TextureRegion region, float x, float y, float rotation, float roll, float w, float h, Color lightColor, Color darkColor){
        roll = Mathf.wrapAngleAroundZero(Mathf.degreesToRadians * roll);

        prepareRollColor(roll, lightColor, darkColor, Mathf.clamp(Mathf.cosDeg(rotation - 45f) * 1.42f, -1f, 1f));
        Draw.rect(region, x, y, w * Mathf.cos(roll), h, rotation);
        Draw.mixcol();
    }

    public static void flipSpriteSimple(TextureRegion region, float x, float y, float rotation, float roll, float w, float h){
        roll = Mathf.wrapAngleAroundZero(Mathf.degreesToRadians * roll);
        Draw.rect(region, x, y, w * Mathf.cos(roll), h, rotation);
    }

    private static void prepareRollColor(float roll, Color lightColor, Color darkColor, float a){
        if(Mathf.zero(roll)) return;
        float f = Mathf.sin(roll) * 0.7f;
        if(roll > Mathf.pi / 2f || roll < -Mathf.pi / 2f){
            f = -f;
        }
        f *= a;

        if(f > 0){
            //dark+
            Draw.mixcol(darkColor, f);
        }
        else{
            //light+
            Draw.mixcol(lightColor, -f);
        }
    }

    /** Outlines a given textureRegion. Run in createIcons. */
    public static void outlineRegion(MultiPacker packer, TextureRegion tex, Color outlineColor, String name){
        Pixmap out = Pixmaps.outline(Core.atlas.getPixmap(tex), outlineColor, 4);
        Drawf.checkBleed(out);
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

        Pixmap out = new Pixmap(r1.width, r1.height);
        Color color1 = new Color();
        Color color2 = new Color();

        for(int x = 0; x < r1.width; x++){
            for(int y = 0; y < r1.height; y++){
                out.setRaw(x, y, color1.set(r1.getRaw(x, y)).lerp(color2.set(r2.getRaw(x, y)), f).rgba());
            }
        }

        Texture texture  = new Texture(out);
        return Core.atlas.addRegion(name + "-blended-" + (int)(f * 100), new TextureRegion(texture));
    }
}
