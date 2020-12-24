package betamindy.content;

import betamindy.entities.bullet.*;
import mindustry.ctype.ContentList;
import mindustry.entities.bullet.BulletType;

public class MindyBullets implements ContentList {
    public static BulletType payBullet;
    @Override
    public void load(){
        payBullet = new PayloadBullet();
    }
}
