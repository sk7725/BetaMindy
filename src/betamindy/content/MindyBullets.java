package betamindy.content;

import betamindy.entities.bullet.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;

public class MindyBullets implements ContentList {
    public static BulletType payBullet;
    @Override
    public void load(){
        payBullet = new PayloadBullet();
    }
}
