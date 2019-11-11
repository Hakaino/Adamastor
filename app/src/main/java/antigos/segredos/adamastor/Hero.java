package antigos.segredos.adamastor;

import android.graphics.Canvas;

import java.util.ArrayList;

class Hero extends GameObject {
    boolean roof = false;
    boolean floor = false;
    final int speed;

    Hero(ArrayList objects){
        Image image = (Image) objects.get(3);
        int x = y = image.unit * 3;
        setVariables(objects, "hero", x, y);
        speed = unit / 7;
    }

    void update(int displayHeight){
        //if hero is within 1/3 and 2/3 of the height of the screen it moves up or
        // down according to the dy else the floor will move as if the camera was changing
        float boundRation = 1f / 3;
        roof = rect.top < displayHeight * boundRation;
        floor = rect.bottom > displayHeight * (1 - boundRation);
        Action();
        if (start) dx = speed;
        if (!(roof && dy < 0) && !(floor && dy > 0)) y += dy;
        if (dy < unit / 2 && !underwater) dy += (int)(unit * 0.05f);
        if (inLand > -20)inLand--;
        if (drop > - 100) drop--;
    }

    private void Action(){
        int previous = action;
        if (!alive) action = 1;                         //dying
        else if (underwater) action = 3;                //swimming
        else if (0 < inLand && dx == 0) action = 4;     //stand
        else if (0 < inLand) action = 0;                //run
        else action = 2;                                //Jump
        boolean jumping = action == 2 && frame == sprite[2].length - 1;
        death = action == 1 && frame >= sprite[1].length - 1;
        if (!jumping) updateMedia(previous != action);
    }

    void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
    }
}
