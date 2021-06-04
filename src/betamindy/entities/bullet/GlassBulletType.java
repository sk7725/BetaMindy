package betamindy.entities.bullet;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.entities.bullet.*;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

public class GlassBulletType extends BulletType {
    public String sprite;
    public int variants;
    public Color tintColor = Color.white;
    public float width = 5f, height = 7f;
    public float shrinkX = 0f, shrinkY = 0f;
    public float spinRand = 7f;

    public TextureRegion[] regions;

    public GlassBulletType(float speed, float damage, String bulletSprite, int variants){
        super(speed, damage);
        this.sprite = bulletSprite;
        this.variants = variants;
        pierce = true;
        pierceBuilding = false;
        pierceCap = 15;
        drag = 0.1f;
    }

    public GlassBulletType(float speed, float damage, String bulletSprite){
        this(speed, damage, bulletSprite, 5);
    }

    public GlassBulletType(String bulletSprite){
        this(1f, 1f, bulletSprite, 5);
    }

    @Override
    public void load() {
        regions = new TextureRegion[variants];
        for(int i = 0; i < variants; i++) regions[i] = Core.atlas.find(sprite + "-" + i);
    }

    @Override
    public void draw(Bullet b) {
        float height = this.height * ((1f - shrinkY) + shrinkY * b.fout());
        float width = this.width * ((1f - shrinkX) + shrinkX * b.fout());
        float spin = Mathf.randomSeed(b.id, spinRand * 2) - spinRand;
        float offset = -90 + Mathf.randomSeed(b.id, 360f) + b.time * spin;

        Draw.z(Layer.flyingUnit - 0.5f);
        Draw.color(tintColor, b.fout(0.9f));
        Draw.rect(regions[Mathf.randomSeed(b.id, 0, variants - 1)], b.x, b.y, width, height, b.rotation() + offset);

        Draw.reset();
    }
}
