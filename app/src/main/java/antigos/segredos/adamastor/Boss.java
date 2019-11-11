package antigos.segredos.adamastor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import java.util.ArrayList;

class Boss extends GameObject{

    private final ArrayList objects;
    private final Hero hero;

    Boss(ArrayList objects){
        this.objects = objects;
        Image image = (Image) objects.get(3);
        sound = (Sound) objects.get(5);
        hero = (Hero) objects.get(7);
        unit = image.unit;
        type = "boss";
        x = 0;
        y = 0;//2 * unit;
        rect = new Rect(x, y, unit * 3, unit * 9);
        sprite = new Bitmap[1][10];
        sprite[0] = image.Boss;
        bitmap = Bitmap.createBitmap(unit * 3, unit * 9, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        hero.x = unit * 11;
        hero.start = true;
    }

    void update(){
        Level level = (Level) objects.get(6);
        //if (Math.random() * 100  < 4){
        //    Foe projectile = new Foe(objects, "projectile", 0, unit * 6);
        //    projectile.dx = (int) (unit * Math.random()) / 9;
        //    projectile.dy = -unit / 3;
        //    level.shoots.add(projectile);
        //}
        if (Rect.intersects(rect, hero.rect)) hero.alive = false;
        if (level.mainX > 650 * unit) x -= hero.dx;
        rect = new Rect(x, y, x + unit * 3, y + unit * 9);
        frame++;
        if(sprite[action].length <= frame) frame = 0;
        bitmap.eraseColor(Color.TRANSPARENT);
        canvas.drawBitmap(sprite[0][frame], 0, 0, null);
    }

    void draw(Canvas canvas){
        if (x >= 0){
            Bitmap darkness = Bitmap.createBitmap(x + 4, unit * 9, Bitmap.Config.ARGB_8888);
            darkness.eraseColor(Color.BLACK);
            canvas.drawBitmap(darkness, -2, y, null);
        }
        canvas.drawBitmap(bitmap, x, y, null);
    }
}