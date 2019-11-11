package antigos.segredos.adamastor;

import android.graphics.Canvas;
import java.util.ArrayList;

class Solid extends GameObject{

    Solid(ArrayList objects, String Type, int x, int y){
        setVariables(objects, Type, x, y);
        switch (Type) {
            case "lifter":
                canvas.drawBitmap(sprite[0][0], 0, 0, null);
                break;
            case "block":
            case "BGblock":
                canvas.drawBitmap(sprite[0][0], 0, 0, null);
                break;
            case "water":
            case "BGwater":
                canvas.drawBitmap(sprite[0][0], 0, 0, null);
                break;
            case "falling rock":
                canvas.drawBitmap(sprite[0][1], 0, 2, null);
                break;
            case "projectile":
                canvas.drawBitmap(sprite[0][2], 0, 2, null);
                break;
            case "platform":
                canvas.drawBitmap(sprite[0][3], 0, 0, null);
                break;
            case "BGwall":
                canvas.drawBitmap(sprite[0][4], 0, 0, null);
                break;
            case "checkpoint":
                canvas.drawBitmap(sprite[0][5], 0, 0, null);
                break;
            case "end1":
            case "end2":
                canvas.drawBitmap(sprite[0][6], 0, 0, null);
                break;
        }
    }

    void physics(GameObject character){
        if (type.equals("water") && rect.left < character.rect.centerX() &&
                rect.right > character.rect.centerX()){
            if (character.rect.centerY() > rect.top){
                if (!character.underwater) character.dy = 0;
                character.underwater = true;
            }
            else character.underwater = false;
        }
        else if(rect.intersect(character.rect)){
            int vertical = rect.centerY() - character.rect.centerY();       // touch top or bottom
            //if it's a hero touching a coin kill the coin
            //if (type.equals("coin") && character.type.equals("hero")){
            //    alive = false;
            //}
            if (type.equals("block")){
                //if (character.type.equals("projectile")) character.death = true;
                if (character.type.equals("missile") && character.underwater) character.alive = false;
            }

            else if (type.equals("lifter") && character.dy >= -character.unit / 2) character.dy = (int)(-unit * 0.2f);
            //int horizontal = rect.centerX() - character.rect.centerX();     // touch left or right;
            if (vertical > 0 && (type.equals("block") || (type.equals("platform") && character.drop <= 0))){
                character.dy = 0;
                character.inLand = 3;
                character.y = rect.top - character.rect.height();
                character.updateRect();
            }
            else if (type.equals("block") && vertical < 0) {
                character.dy = 0;
                character.y = rect.bottom + 1;
                character.updateRect();
            }
        }
        character.updateGhost();
        // if in characters next cycle, it's inside a block
        // then he must adjust his next step and then stop
        // unless he is the hero... A hero is to cool to bump into walls
        if (type.equals("block") && rect.contains(character.ghost[0], character.ghost[1])){
            switch (character.type) {
                case "walker":
                case "jumper":
                    if (0 < character.dx) character.x = rect.left - character.unit;
                    else if (character.dx < 0) character.x = rect.right;
                    character.dx *= -1;
                    character.updateRect();
                    break;
                case "arrow":
                    character.alive = false;
                    break;
                case "projectile":
                    character.death = true;
                    break;
                case "missile":
                    character.alive = false;
                    break;
                default:
                    character.dx = 0;
                    break;
            }
        }
    }

    void draw(Canvas canvas){
        if (!death) {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }
}
