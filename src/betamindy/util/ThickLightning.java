package betamindy.util;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

/** modified copy of Lightning class, with modifications.
 * @author Anuke (Anuken)
 */


public class ThickLightning {
    private static final Rand random = new Rand();
    private static final Rect rect = new Rect();
    private static final Seq<Unitc> entities = new Seq<>();
    private static final IntSet hit = new IntSet();
    private static final int maxChain = 8;
    private static final float hitRange = 60f;
    private static boolean bhit = false;
    private static int lastSeed = 0;

    /** Create a lighting branch at a location. Use Team.derelict to damage everyone. */
    public static void create(Team team, Color color, float damage, float x, float y, float targetAngle, int length){
        createLightningInternal(null, lastSeed++, team, color, damage, x, y, targetAngle, length);
    }

    /** Create a lighting branch at a location. Uses bullet parameters. */
    public static void create(Bullet bullet, Color color, float damage, float x, float y, float targetAngle, int length){
        createLightningInternal(bullet, lastSeed++, bullet.team, color, damage, x, y, targetAngle, length);
    }

    private static void createLightningInternal(@Nullable Bullet hitter, int seed, Team team, Color color, float damage, float x, float y, float rotation, int length){
        random.setSeed(seed);
        hit.clear();

        BulletType hitCreate = hitter == null || hitter.type.lightningType == null ? Bullets.damageLightning : hitter.type.lightningType;
        Seq<Vec2> lines = new Seq<>();
        bhit = false;

        //is this necessary?
        lines.add(new Vec2(x, y));

        for(int i = 0; i < length / 2; i++){
            hitCreate.create(null, team, x, y, rotation, damage, 1f, 1f, hitter);
            lines.add(new Vec2(x + Mathf.range(15f), y + Mathf.range(15f)));

            if(lines.size > 1){
                bhit = false;
                Vec2 from = lines.get(lines.size - 2);
                Vec2 to = lines.get(lines.size - 1);
                world.raycastEach(World.toTile(from.getX()), World.toTile(from.getY()), World.toTile(to.getX()), World.toTile(to.getY()), (wx, wy) -> {

                    Tile tile = world.tile(wx, wy);
                    if(tile != null && tile.build != null && tile.solid() && tile.team() != team){ //it is blocked by all blocks, not just insulated ones; This just looks way better.
                        bhit = true;
                        //snap it instead of removing
                        lines.get(lines.size - 1).set(wx * tilesize, wy * tilesize);
                        return true;
                    }
                    return false;
                });
                if(bhit) break;
            }

            rect.setSize(hitRange).setCenter(x, y);
            entities.clear();
            if(hit.size < maxChain){
                Units.nearbyEnemies(team, rect, u -> {
                    if(!hit.contains(u.id()) && (hitter == null || u.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround))){
                        entities.add(u);
                    }
                });
            }

            Unitc furthest = Geometry.findFurthest(x, y, entities);

            if(furthest != null){
                hit.add(furthest.id());
                x = furthest.x();
                y = furthest.y();
            }else{
                rotation += random.range(20f);
                x += Angles.trnsx(rotation, hitRange / 2f);
                y += Angles.trnsy(rotation, hitRange / 2f);
            }
        }

        MindyFx.thickLightning.at(x, y, rotation, color, lines);
        // fInaL OR effECtiVElY fINAL
        float finalRotation = rotation;
        float finalX = x;
        float finalY = y;
        Time.run(MindyFx.thickLightning.lifetime, () -> {
            MindyFx.thickLightningFade.at(finalX, finalY, finalRotation, color, lines);
            int n = Mathf.random(5);
            if(bhit){
                for(int j = 0; j < n; j++) Lightning.create(team, color, damage * 0.2f, lines.peek().x, lines.peek().y, finalRotation + Mathf.range(30f), length / 3);
                MindyFx.thickLightningStrike.at(lines.peek().x, lines.peek().y, finalRotation, color);
            }
            else{
                for(int j = 0; j < n; j++) Lightning.create(team, color, damage * 0.2f, lines.peek().x, lines.peek().y, Mathf.random(360f), length / 3);
                MindyFx.thickLightningHit.at(lines.peek().x, lines.peek().y, finalRotation, color);
            }

            if(!headless){
                Effect.shake(6f, 5.5f, finalX, finalY);
                MindySounds.lightningStrike.at(finalX, finalY, 1f + Mathf.range(0.1f), 3f);
            }
        });
    }
}
