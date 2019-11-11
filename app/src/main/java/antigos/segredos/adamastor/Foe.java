package antigos.segredos.adamastor;

import android.graphics.Canvas;

import java.util.ArrayList;

class Foe extends GameObject{

    private final ArrayList objects;

    Foe(ArrayList objects, String type, int x, int y){
        this.objects = objects;
        setVariables(objects, type, x, y);
        switch (type) {
            case "projectile":
                canvas.drawBitmap(sprite[0][2], 0, 0, null);
                break;
            case "walker":
            case "jumper":
                dx = -unit / 13;
                break;
            case "arrow":
                dx = -unit / 7;
                break;
        }
    }

    void update(Hero hero){
        switch (type) {
            case "walker":
            case "jumper":
                if (type.equals("jumper") && inLand > 0 && Math.random() * 100 <= 5) Jump();
                Fall();
                inLand--;
                drop--;
                break;
            case "projectile":
                if (dy < unit / 2) dy++;
                break;
            case "missile":
                int centerX = hero.rect.centerX() - rect.centerX();
                int centerY = hero.rect.centerY() - rect.centerY();
                if (centerX != 0)dx = (unit / 20) * (centerX / Math.abs(centerX));
                if (centerY != 0)dy = (unit / 20) * (centerY / Math.abs(centerY));
                break;
            case "shooter":
                if (Math.random() * 100  < 5){
                    Level level = (Level) objects.get(6);
                    Solid projectile = new Solid(objects, "projectile", x, y - unit);
                    projectile.dx = (int) (unit * 0.25f * (Math.random() * 2 - 1));
                    projectile.dy = (int)(-unit * 0.4f);
                    level.moving.add(projectile);
                    if (sound.sfxOn) sound.soundPool.play(sound.pool[9]
                            , 1, 1, 1, 0, 1);
                }
                break;
        }
        x += dx;
        y += dy;
        updateRect();
        if (!type.equals("projectile"))Action();
        int distX = hero.rect.centerX() - rect.centerX();
        int distY = hero.rect.centerY() - rect.centerY();
        boolean dist = Math.hypot(distX, distY) < unit;
        if (alive && dist) hero.alive = false;
    }

    private void Action(){
        int previous = action;
        if (!alive ||(underwater && type.equals("acid"))) action = 1;                                      //dying
        else if ((type.equals("jumper") && inLand < 0)
                ||(underwater && type.equals("missile")))action = 2;      //jump or swim
        else action = 0;                                              //run
        death = action == 1 && frame == sprite[action].length - 1 && !type.equals("acid");
        if (!type.equals("projectile")) updateMedia(previous != action);
    }

    void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
    }
}