package antigos.segredos.adamastor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import java.util.ArrayList;

class Level {
    private final int unit;
    private int mainY;
    private int lastRow = 0;
    int  mainX;
    private final Image image;
    private final char[][] list;
    private final ArrayList objects;
    final Rect Jump_rect, Drop_rect, Pause_rect;
    Point checkpoint = new Point(0, 0);
    ArrayList<Foe> foes = new ArrayList<>();
    private ArrayList<Solid> solids = new ArrayList<>();
    ArrayList<Solid> moving = new ArrayList<>();
    boolean stop = false;
    private final Point size;
    private Boss boss = null;

    Level(ArrayList objects, Point cp){
        this.objects = objects;
        image = (Image) objects.get(3);
        size = (Point) objects.get(4);
        this.unit = image.unit;
        //main_rect = new Rect(-unit, 0, size.x + unit, size.y);
        int stage = (int) objects.get(1);
        int file;
        if (stage == 1) file = R.raw.stage1;
        else if (stage == 2) file = R.raw.stage2;
        else if (stage == 3) file = R.raw.stage3;
        else if (stage == 4) file = R.raw.stage4;
        else if (stage == 5) file = R.raw.stage5;
        else if (stage == 6) file = R.raw.stage6;
        else if (stage == 7) file = R.raw.stage7;
        else file = R.raw.stage8;
        list = new FileReader(image.resources, file).newLevel();
        //get the interface elements ready
        int down = size.y - unit * 2;
        int right = size.x - unit * 2;
        Jump_rect = new Rect(0, down, unit * 2, unit * 2);
        Drop_rect = new Rect(right, down, unit * 2, unit * 2);
        Pause_rect = new Rect(0, 0, unit * 2, unit * 2);
        mainX = cp.x;
        mainY = 3 * unit + cp.y;
        if (stage == 4 || stage == 8) {
            boss = new Boss(objects);
        }
    }

    void update(Hero hero){
        if (boss != null) boss.update();
        hero.updateRect();
        for (Foe foe : foes) foe.update(hero);
        for (Solid solid : solids) {  //solid update
            boolean change = solid.type.equals("water") || solid.type.equals("BGwater")||
                    solid.type.equals("lifter");
            if (change) solid.updateMedia(false);
            solid.updateRect();
            solid.physics(hero);
            if (solid.rect.intersect(hero.rect)){
                if (solid.type.equals("end1") || solid.type.equals("end2")) stop = true;
                else if (solid.type.equals("checkpoint")){
                    if (solid.sound.sfxOn) solid.sound.soundPool.play(solid.sound.pool[11]
                            , 1, 1, 1, 0, 1);
                    if (boss == null) checkpoint = new Point(mainX + solid.x - 3 * unit, mainY - 3 * unit);
                    else  checkpoint = new Point(mainX + solid.x - 14 * unit, mainY - 3 * unit);
                    solid.death = true;
                }
            }
                //if (solid.type.equals("coin")) {
                //    coins++;
                //    setCoinsInStage(coins);
                //    solid.death = true;
                //}
                //else if...
            else {
                for (Foe foe : foes) if (!foe.type.equals("acid")) solid.physics(foe);
                for (Solid rock : moving) if(solid.rect.intersect(rock.rect)) {
                    if (solid.type.equals("block") || solid.type.equals("platform")){
                        rock.y = solid.y - rock.rect.height();
                        if (rock.type.equals("falling rock")){
                            rock.type = "block";
                            if (rock.sound.sfxOn) rock.sound.soundPool.play(rock.sound.pool[12]
                                    , 1, 1, 1, 0, 1);
                        }
                        else {
                            rock.death = true;
                            if (rock.sound.sfxOn) rock.sound.soundPool.play(rock.sound.pool[10]
                                    , 1, 1, 1, 0, 1);
                        }
                    }
                    else if (solid.type.equals("lifter")) rock.dy -= 3;
                }
                if (solid.rect.right < 0) solid.death = true;
            }
        }

        int mainDy = 0;
        if (hero.roof || hero.floor){
            if (hero.dy == 0){
                if (hero.roof) mainDy--;
                else mainDy++;
                }
            else if (hero.inLand <= 0) mainDy = hero.dy;
        }
        if (boss != null){
            if (hero.dx == 0) boss.x += hero.speed;
            boss.y -= mainDy;
        }
        for (Foe foe : foes){
            foe.x -= hero.dx;
            foe.y -= mainDy;
        }
        for (Solid solid :solids) {
            solid.x -= hero.dx;
            solid.y -= mainDy;
        }
        for (Solid solid : moving) {
            if (solid.type.equals("falling rock") && solid.start){
                solid.dy++;
                solid.y += solid.dy;
                for (Foe foe : foes) if (solid.rect.intersect(foe.rect)) {
                    if (foe.type.equals("acid")) foe.death = true;
                    else foe.alive = false;
                }
                if (solid.rect.intersect(hero.rect)) hero.alive = false;
            }
            else if (solid.type.equals("projectile")){
                solid.dy++;
                solid.x += solid.dx;
                solid.y += solid.dy;
                if (solid.rect.intersect(hero.rect)) hero.alive = false;
            }
            solid.x -= hero.dx;
            solid.y -= mainDy;
            solid.updateRect();
        }
        mainX += hero.dx;
        mainY += mainDy;
        maker();
        takeThrash();
    }

    void draw(Canvas canvas, int Height){
        //for(Solid solid : backGround) if (solid.rect.bottom > 0 && Height > solid.y) solid.draw(canvas);
        for(Solid solid : solids) if (solid.rect.bottom > 0 && Height > solid.y) solid.draw(canvas);
        for(Solid solid : moving) if (solid.rect.bottom > 0 && Height > solid.y) solid.draw(canvas);
        for (Foe foe : foes) if (foe.rect.bottom > 0 && Height > foe.y)foe.draw(canvas);
        if (boss != null) boss.draw(canvas);

        canvas.drawBitmap(image.Buttons[0], Jump_rect.left, Jump_rect.top, null);
        canvas.drawBitmap(image.Buttons[1], Drop_rect.left, Drop_rect.top, null);
        canvas.drawBitmap(image.Buttons[2], Pause_rect.left, Pause_rect.top, null);
        //canvas.drawBitmap(Info_img, Info_rect.left, Info_rect.top, null);
    }

    private void maker(){
        String mov = "abcdef 247";
        while (lastRow <= (mainX + size.x) / image.unit + 2){
            //the if statement is only used for developing purposes
            if (lastRow > mainX / image.unit){
                boolean Top = false, Water = false;
                // background
                int Bottom = 0;
                for (int y = list.length - 1; y > 0; y--) {
                    char X = list[y][lastRow];
                    if (X != ' ') {
                        Bottom = y;
                        break;
                    }
                }
                for (int y = 0; y < list.length; y++){
                    if (lastRow < list[y].length){
                        char itemType = list[y][lastRow];
                        int X = (lastRow - 1) * image.unit - mainX;
                        int Y = y * image.unit - mainY;
                        // objects
                        if (mov.contains("" + itemType)){
                            if (y > Bottom) solids.add(new Solid(objects, "BGblock", X, Y));    //below
                            else if (Water) solids.add(new Solid(objects, "BGwater", X, Y));    //Water
                            else if (Top) solids.add(new Solid(objects, "BGwall", X, Y));       //middle
                            else  solids.add(new Solid(objects, "BGblock", X, Y));              //above
                        }
                        if (itemType != ' '){
                            Top = true;
                            if (itemType == (int) '1') solids.add(new Solid(objects, "block", X, Y));
                            else if (itemType == (int) '2') solids.add(new Solid(objects, "platform", X, Y));
                            else if (itemType == (int) '4') moving.add(new Solid(objects, "falling rock", X, Y));
                            else if (itemType == (int) '5') solids.add(new Solid(objects, "lifter", X, Y));
                            else if (itemType == (int) '6') {
                                solids.add(new Solid(objects, "water", X, Y));
                                Water = true;
                            }
                            else if (itemType == (int) '7') solids.add(new Solid(objects, "checkpoint", X, Y));
                            else if (itemType == (int) 'a') foes.add(new Foe(objects, "walker", X, Y));
                            else if (itemType == (int) 'b'){
                                Foe acid = new Foe(objects, "acid", X, Y);
                                acid.underwater = Water;
                                foes.add(acid);
                            }
                            else if (itemType == (int) 'c') foes.add(new Foe(objects, "arrow", X, Y));
                            else if (itemType == (int) 'd') foes.add(new Foe(objects, "jumper", X, Y));
                            else if (itemType == (int) 'e') foes.add(new Foe(objects,"shooter", X, Y));
                            else if (itemType == (int) 'f') foes.add(new Foe(objects, "missile", X, Y));
                            else if (itemType == (int) 'z') solids.add(new Solid(objects, "end2", X, Y));
                            else if (itemType == (int) 'Z') solids.add(new Solid(objects, "end1", X, Y));
                        }
                    }
                }
            }
            lastRow++;
        }
    }

    private void takeThrash() {
        for (Foe foe: foes) if (foe.rect.right < 0 ||
                foe.rect.left > 4 * image.unit + size.x) foe.death = true;
        for (Solid solid: solids) if (solid.rect.right < 0) solid.death = true;
        for (Solid solid: moving) if (solid.rect.right < 0 ||
                solid.rect.left > 4 * image.unit + size.x) solid.death = true;
        ArrayList<Foe> newFoes = new ArrayList<>();
        ArrayList<Solid> newSolids = new ArrayList<>();
        ArrayList<Solid> newMoving = new ArrayList<>();
        for (Foe foe : foes) if (!foe.death) newFoes.add(foe);
        for (Solid solid :solids) if (!solid.death) newSolids.add(solid);
        for (Solid solid :moving) if (!solid.death) {
            if (solid.type.equals("block")) newSolids.add(solid);
            else newMoving.add(solid);
        }
        foes = (ArrayList<Foe>) newFoes.clone();
        solids = (ArrayList<Solid>) newSolids.clone();
        moving = (ArrayList<Solid>) newMoving.clone();
        newFoes.clear();
        newSolids.clear();
        newMoving.clear();
    }
}
