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
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

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

    /** Generates all team regions and returns the sharded team region for icon. */
    public static @Nullable TextureRegion generateTeamRegion(Block b){
        TextureRegion shardTeamTop = null;
        PixmapRegion teamr = Core.atlas.getPixmap(b.name + "-team");

        for(Team team : Team.all){
            if(team.hasPalette){
                Pixmap out = new Pixmap(teamr.width, teamr.height);
                Color pixel = new Color();
                for(int x = 0; x < teamr.width; x++){
                    for(int y = 0; y < teamr.height; y++){
                        int color = teamr.getPixel(x, y);
                        int index = color == 0xffffffff ? 0 : color == 0xdcc6c6ff ? 1 : color == 0x9d7f7fff ? 2 : -1;
                        out.draw(x, y, index == -1 ? pixel.set(teamr.getPixel(x, y)) : team.palette[index]);
                    }
                }
                Texture texture  = new Texture(new PixmapTextureData(out, null, true, false, true));
                TextureRegion res = Core.atlas.addRegion(b.name + "-team-" + team.name, new TextureRegion(texture));

                if(team == Team.sharded){
                    shardTeamTop = res;
                }
            }
        }
        return shardTeamTop;
    }
}
