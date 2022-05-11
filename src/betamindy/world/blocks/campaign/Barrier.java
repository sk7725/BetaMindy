package betamindy.world.blocks.campaign;

import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import betamindy.content.*;
import betamindy.graphics.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.atlas;
import static betamindy.BetaMindy.uwu;
import static mindustry.Vars.*;

public class Barrier extends Block {
    private final Rect r1 = new Rect();
    private final Rect r2 = new Rect();
    private final IntQueue tmpq = new IntQueue();
    private final IntSet visit = new IntSet();
    private final FloatSeq tmpa = new FloatSeq();

    public Color chainColor = Pal.remove;
    public Color borderColor = Pal2.esoterum;
    public TextureRegion topRegion, chain, chainEnd;

    public Effect chainBreakEffect = MindyFx.chainShatter; //played at all chain segments
    public Effect captureEffect = MindyFx.soundwaveHit;
    public Effect chainDestroyEffect = MindyFx.sniperShoot; //played at the connected block only

    public StatusEffect status = MindyStatusEffects.drift;
    public Effect unitHitEffect = MindyFx.ionBurst;
    public Sound captureSound = MindySounds.easterEgg1;
    public Sound unitHitSound = Sounds.flame2;
    public float chainScale = 1f;

    public Barrier(String name){
        super(name);
        configurable = solid = update = sync = true;
        breakable = false;
        attributes.set(MindyAttribute.pushless, 1f);

        config(Integer.class, (BarrierBuild entity, Integer value) -> {
            if(!state.isEditor()) return;
            entity.captured = false;
            Building other = world.build(value);
            if(other instanceof BarrierBuild otherb){
                boolean contains = entity.bGroup.contains(value);

                if(contains){
                    //unlink
                    entity.bGroup.removeValue(value);
                    if(otherb.bGroup.contains(entity.pos())) otherb.bGroup.removeValue(entity.pos());
                }else if(entity.bGroup.size < 2 && otherb.bGroup.size < 2 && groupValid(entity, other)){
                    if(!entity.bGroup.contains(other.pos())){
                        entity.bGroup.add(other.pos());
                    }
                    if(!otherb.bGroup.contains(entity.pos())){
                        otherb.bGroup.add(entity.pos());
                    }
                }
            }
            else{
                boolean contains = entity.links.contains(value);

                if(contains){
                    //unlink
                    entity.links.removeValue(value);
                }else if(linkValid(entity, other)){
                    if(!entity.links.contains(other.pos())){
                        entity.links.add(other.pos());
                    }
                }
            }
        });
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        chain = atlas.find(name + "-laser", "betamindy-clear-chain");
        chainEnd = atlas.find(name + "-laser-end", "betamindy-clear-chain-end");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public boolean linkValid(Building tile, Building link){
        return tile != link && link != null;
    }
    public boolean groupValid(Building tile, Building link){
        if(tile == link || link == null || tile.team != link.team || link.dead) return false;

        return /*Math.min(Math.abs(tile.tileX() - link.tileX()), Math.abs(tile.tileY() - link.tileY())) == 0 && */link instanceof BarrierBuild;
    }

    public class BarrierBuild extends Building {
        public IntSeq links = new IntSeq();
        public IntSeq bGroup = new IntSeq(); //barriers of the same group, used for drawing
        public boolean captured = false, inited = false;
        public float heat = 0f;
        private Polygon area = null;
        private float cx, cy;

        public void chainBreak(float x, float y, float x2, float y2){
            if(headless) return;
            chainDestroyEffect.at(x, y, chainColor);
            float rot = Mathf.angle(x2 - x, y2 - y);
            float len = Mathf.len(x2 - x, y2 - y);
            float c = (chain.width - 0.01f) * chainScale * Draw.scl;
            int n = (int)(len / c);
            if(n > 1){
                for(int i = 0; i < n; i++){
                    Tmp.v1.trns(rot, c * (i + 0.5f)).add(x, y);
                    chainBreakEffect.at(Tmp.v1.x, Tmp.v1.y, rot, chainColor, chain);
                }
            }
        }

        public void capture(){
            setArea(in -> {
                in.health = in.maxHealth;
                if(!visit.contains(in.pos()) && in instanceof BarrierBuild bb && !bb.captured) bb.inited = false; //reset barriers inside barrier
            });
            if(headless) return;
            captureEffect.at(x, y, chainColor);
            captureSound.at(this);
        }

        public void setArea(){
            setArea(inited ? null : in -> in.health = Float.NaN);
        }

        public void setArea(@Nullable Cons<Building> inside){
            boolean shouldInit = !inited;
            tmpq.clear();
            visit.clear();
            tmpa.clear();
            cx = cy = 0f;
            tmpq.addFirst(this.pos());
            r1.set(x, y, 0.01f, 0.01f);
            while(!tmpq.isEmpty()){
                int p = tmpq.removeFirst();
                if(visit.contains(p)) continue;
                visit.add(p);
                if(world.build(p) instanceof BarrierBuild bb){
                    tmpa.add(bb.x, bb.y);
                    cx+= bb.x; cy += bb.y;
                    if(shouldInit){
                        bb.inited = true;
                    }
                    if(inside != null){
                        r1.merge(bb.x, bb.y);
                    }
                    for(int i = 0; i < bb.bGroup.size; i++){
                        int next = bb.bGroup.get(i);
                        if(!visit.contains(next)) tmpq.addFirst(next);
                    }
                }
            }

            if(tmpa.size > 0){
                cx /= tmpa.size / 2f;
                cy /= tmpa.size / 2f;
            }
            if(tmpa.size < 6){
                area = null;
            }
            else{
                if(area == null) area = new Polygon();
                area.setVertices(tmpa.toArray());
                if(shouldInit){
                    inited = true;
                }
                if(inside != null){
                    r1.grow(1f);
                    team.data().buildingTree.intersect(r1, in -> {
                        if(kindaContains(area, in.x, in.y, 2f)) inside.get(in);
                    });
                }
            }
        }

        public boolean kindaContains(Polygon area, float x, float y, float margin){
            return area.contains(x + margin, y + margin) || area.contains(x - margin, y + margin) || area.contains(x + margin, y - margin) || area.contains(x - margin, y - margin);
        }

        @Override
        public void updateTile(){
            heat = Mathf.lerpDelta(heat, captured ? 0f : 1f, 0.04f);
            if(captured){
                enabled = false;
                return;
            }

            int broke = -1;
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(b == null || b.dead || b.team != team){
                    Tile t = world.tile(links.get(i));
                    if(t != null) chainBreak(t.worldx(), t.worldy(), x, y);
                    broke = i;
                    break;
                }
            }
            if(broke != -1){
                links.removeIndex(broke);
            }
            if(canCapture() && !state.isEditor()){
                captured = true;
                capture();
            }

            if(bGroup.size < 2) return;

            //push away all units
            if(area == null || !inited) setArea();

            if(area != null){
                r1.set(area.getBoundingRectangle()).grow(8f);
                Units.nearby(r1, u -> {
                    if(u.team == team || !kindaContains(area, u.x, u.y, 4f)) return;
                    float ang = Angles.angle(cx, cy, u.x, u.y);
                    if(!Angles.near(ang, u.vel.angle(), 100f)){
                        u.move(-u.vel.x, -u.vel.y);
                    }
                    u.vel.trns(ang, 8f);
                    u.rotation(ang);
                    if(status != null && !u.hasEffect(status)){
                        u.apply(status, 20f);
                        unitHitEffect.at(u.x, u.y, u.hitSize * 0.8f, status.color);
                        unitHitSound.at(u.x, u.y, Mathf.random(0.9f, 1.1f));
                    }

                    if(kindaContains(area, u.x, u.y, 4f)){
                        u.move(Tmp.v3.trns(ang, Time.delta)); //attempt to push it out, this will be repeated x number of nodes
                    }
                });

                Groups.bullet.intersect(r1.x, r1.y, r1.width, r1.height, bullet -> {
                    if(bullet.team == team || !bullet.type.absorbable || !area.contains(bullet.x, bullet.y)) return;
                    bullet.absorb();
                    MindyFx.powerDust.at(bullet.x, bullet.y, 5f, borderColor);
                });
            }
            /*
            r1.set(x, y, 0.1f, 0.1f);
            for(int i = 0; i < bGroup.size; i++){
                Building b = world.build(bGroup.get(i));
                if(b == null || !groupValid(this, b) || bGroup.get(i) != b.pos()) continue;
                r1.merge(b.x, b.y);
            }
            r1.getCenter(Tmp.v3);
            if(area != null && area.contains(Tmp.v3)){
                r1.grow(4f);
                Units.nearby(r1, u -> {
                    if(u.team == team || !((cx >= u.x == cx >= x) && (cy >= u.y == cy >= y))) return; //must be in the same sabunmyun
                    //Tmp.r3.setCentered(x, y, size * tilesize).merge(cx, cy);
                    u.move(-u.vel.x, -u.vel.y);
                    u.hitboxTile(r2);
                    Tmp.v3.set(Geometry.overlap(r2, r1, false));
                    Tmp.v4.set(Geometry.overlap(r2, r1, true));
                    if(Tmp.v4.len2() < Tmp.v3.len2()) Tmp.v3.set(Tmp.v4);
                    u.move(Tmp.v3);
                });

                Groups.bullet.intersect(r1.x, r1.y, r1.width, r1.height, bullet -> {
                    if(bullet.team == team || !bullet.type.absorbable || !area.contains(bullet.x, bullet.y)) return;
                    bullet.absorb();
                    MindyFx.powerDust.at(bullet.x, bullet.y, 5f, borderColor);
                });
            }*/
        }

        public boolean canCapture(){ //bfs
            if(links.size > 0) return false;
            tmpq.clear();
            visit.clear();
            tmpq.addFirst(this.pos());
            while(!tmpq.isEmpty()){
                int p = tmpq.removeFirst();
                if(visit.contains(p)) continue;
                visit.add(p);
                if(world.build(p) instanceof BarrierBuild bb){
                    if(bb.captured) return true;
                    if(bb.links.size > 0) return false;
                    for(int i = 0; i < bb.bGroup.size; i++){
                        int next = bb.bGroup.get(i);
                        if(!visit.contains(next)) tmpq.addFirst(next);
                    }
                }
            }
            return true;
        }

        public void drawDebug(){
            Tmp.c4.set(Color.red).shiftHue(Mathf.randomSeed(pos(), 0f, 360f));
            squares(this, Tmp.c4);
            if(bGroup.size < 2 || area == null) return;
            r1.set(x, y, 0.1f, 0.1f);
            for(int i = 0; i < bGroup.size; i++){
                Building b = world.build(bGroup.get(i));
                if(b == null || !groupValid(this, b) || bGroup.get(i) != b.pos()) continue;
                r1.merge(b.x, b.y);
            }
            r1.grow(8f);
            Lines.stroke(2f, Tmp.c4);
            Draw.alpha(0.2f);
            Lines.rect(r1);
            r1.getCenter(Tmp.v3);
            Lines.stroke(area.contains(Tmp.v3) ? 1f : 0.5f);
            Draw.alpha(0.75f);
            Lines.square(Tmp.v3.x, Tmp.v3.y, 2f, 45f);
            Draw.color(Color.white, 0.5f);
            Lines.circle(cx, cy, 3f);
            Lines.stroke(0.5f);
            Draw.color(Color.white, Tmp.c4, 0.5f);
            Draw.alpha(0.75f);
            Lines.polyline(area.getTransformedVertices(), area.getTransformedVertices().length, false);
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            if(heat > 0.001f){
                Draw.alpha(heat);
                Draw.rect(topRegion, x, y);
                Draw.color();
            }

            Draw.z(Layer.power);
            Draw.blend(Blending.additive);
            Draw.color(chainColor);
            for(int i = 0; i < links.size; i++){
                Building b = world.build(links.get(i));
                if(b == null || !linkValid(this, b) || links.get(i) != b.pos()) continue;
                Drawm.chain(null, chain, chainEnd, b.x, b.y, x, y, chainScale, true);
            }
            Draw.blend();

            if(heat > 0.001f && (bGroup.size == 1 || area != null)){
                Draw.z(Layer.flyingUnit + 0.1f);
                Draw.blend(Blending.additive);
                for(int i = 0; i < bGroup.size; i++){
                    Building b = world.build(bGroup.get(i));
                    if(b == null || !groupValid(this, b) || bGroup.get(i) != b.pos()) continue;
                    getDrawPos(this, Tmp.v1);
                    getDrawPos(b, Tmp.v2);
                    Drawm.border(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.c3.set(borderColor).a(0.3f * heat));
                }
                Draw.blend();
            }
            Draw.reset();

            if(renderer.drawStatus && (uwu || state.isEditor())){
                Draw.z(Layer.power + 1);
                drawDebug();
                Draw.reset();
            }
        }

        public Vec2 getDrawPos(Building b, Vec2 v){
            if(bGroup.size < 2 || area == null) return v.set(b);
            boolean xp = b.x > cx, yp = b.y > cy;
            return v.set(b).add(Mathf.sign(xp) * b.block.size * tilesize / 2f, Mathf.sign(yp) * b.block.size * tilesize / 2f);
        }

        void squares(Building b, Color color){
            float radius = b.block.size * tilesize / 2f;
            Lines.stroke(3f, Pal.gray);
            Lines.square(b.x, b.y, radius + 1f);
            Lines.stroke(1f, color);
            Lines.square(b.x, b.y, radius);
        }

        @Override
        public void drawConfigure(){
            super.drawConfigure();
            if(!state.isEditor()) return;
            /*
            float radius = (range + 0.5f) * tilesize * 2f; //radius for bGroup
            Lines.stroke(3f, Pal.gray);
            Lines.square(x, y, radius + 1f);
            Lines.stroke(1f, borderColor);
            Lines.square(x, y,  radius);

            radius = (range + 0.5f) * tilesize; //radius of protection
            Lines.stroke(3f, Pal.gray);
            Lines.square(x, y, radius + 1f);
            Lines.stroke(1f, Pal2.coin);
            Lines.square(x, y,  radius);*/
            squares(this, Pal2.coin);

            for(int i = 0; i < links.size; i++){
                Building link = world.build(links.get(i));

                if(link != this && linkValid(this, link)){
                    boolean linked = links.indexOf(link.pos()) >= 0;
                    if(linked){
                        squares(link, chainColor);
                    }
                }
            }
            for(int i = 0; i < bGroup.size; i++){
                Building link = world.build(bGroup.get(i));

                if(link != this && groupValid(this, link)){
                    boolean linked = bGroup.indexOf(link.pos()) >= 0;
                    if(linked){
                        squares(link, borderColor);
                    }
                }
            }

            Draw.reset();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(!net.active() && state.isEditor() && linkValid(this, other)){
                configure(other.pos());
                return false;
            }
            return true;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(captured);
            write.s(links.size);
            for(int i = 0; i < links.size; i++){
                write.i(links.get(i));
            }
            write.s(bGroup.size);
            for(int i = 0; i < bGroup.size; i++){
                write.i(bGroup.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            captured = read.bool();
            links.clear();
            short amount = read.s();
            for(int i = 0; i < amount; i++){
                links.add(read.i());
            }
            bGroup.clear();
            amount = read.s();
            for(int i = 0; i < amount; i++){
                bGroup.add(read.i());
            }
        }

        @Override
        public float handleDamage(float amount){
            return captured ? super.handleDamage(amount) : 0f;
        }

        @Override
        public void damage(float damage){
            if(captured) super.damage(damage);
        }

        @Override
        public boolean canPickup(){
            return false;
        }
    }
}
