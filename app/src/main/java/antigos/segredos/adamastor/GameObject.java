package antigos.segredos.adamastor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;

abstract class GameObject{
    int  drop = 0, dx = 0, dy = 0, unit, inLand = 0, frame = 0, action = 0, x, y;
    final int[] ghost = new int[2];
    boolean alive = true, death = false, start = false, underwater = false;
    Bitmap[][] sprite;
    Bitmap bitmap;
    Canvas canvas;
    Rect rect;
    String type;
    Sound sound;
    //private int[] mPool;

    void setVariables(ArrayList objects, String Type, int X, int Y){
        type = Type;
        sound = (Sound) objects.get(5);
        Image image = (Image) objects.get(3);
        unit = image.unit;
        x = X;
        y = Y;
        switch (type) {
            case "platform":
            //case "coin":
            case "falling rock":
            case "BGwall":
            case "end1":
            case "end2":
            case "checkpoint":
            case "block":
            case "BGblock":
            case "projectile":
                sprite = image.Block;
                break;
            case "BGwater":
            case "water":
                sprite = image.Water;
                break;
            case "lifter":
                sprite = image.Lifter;
                break;
            case "hero":
                sprite = image.Hero;
                break;
            case "acid":
                sprite = image.Acid;
                break;
            case "walker":
                sprite = image.Walker;
                break;
            case "arrow":
                sprite = image.Arrow;
                break;
            case "jumper":
                sprite = image.Jumper;
                break;
            case "shooter":
                sprite = image.Shooter;
                break;
            case "missile":
                sprite = image.Missile;
                break;
        }
        unit = image.unit;
        bitmap = Bitmap.createBitmap(unit, unit, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        rect = new Rect(x, y, x + unit, y + unit);
    }

    void updateRect(){
        rect.set(x, y, x + unit, y + unit);
    }

    void updateGhost(){
        //only right motion is defined now
        if (0 < dx) ghost[0] = rect.right + dx;
        else if (dx < 0) ghost[0] = rect.left + dx;
        ghost[1] = y + unit / 2;
    }

    void Jump(){
            dy = (int)(-unit * 0.4f);
            if (sound.sfxOn){
                if (type.equals("hero")) sound.soundPool.play(sound.pool[2]
                    , 1, 1, 1, 0, 1);
            else if (type.equals("missile")) sound.soundPool.play(sound.pool[12]
                    , 1, 1, 1, 0, 1);
        }
    }

    void Drop() {
        drop = (int) (unit * 0.4f);
    }

    void Fall() {
        if (dy < unit / 2) dy += (int)(unit * 0.04f);
    }

    void updateMedia(boolean change){
        if (change && sound.sfxOn) updateSfx();
        frame++;
        if(sprite[action].length <= frame || change) frame = 0;
        bitmap.eraseColor(Color.TRANSPARENT);
        canvas.drawBitmap(sprite[action][frame], 0, 0, null);
    }

    private void updateSfx(){
        int sfx = 0;
        switch (type){
            case "hero":
                if (!alive) sfx = sound.pool[1];
                else if (underwater) sfx = sound.pool[3];
                break;
            case "walker":
                if (!alive) sfx = sound.pool[4];
                break;
            case "arrow":
                if (!alive) sfx = sound.pool[5];
                break;
            case "jumper":
                if (!alive) sfx = sound.pool[6];
                break;
            case "shooter":
                if (!alive) sfx = sound.pool[8];
                break;
            case "missile":
                if (!alive) sfx = sound.pool[10];
                break;
        }
        if (sound.sfxOn) sound.soundPool.play(sfx, 1, 1, 1, 0, 1);
    }
}
