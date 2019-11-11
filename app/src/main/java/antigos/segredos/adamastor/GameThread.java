package antigos.segredos.adamastor;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.SurfaceHolder;
import java.util.ArrayList;

class GameThread extends Thread{

    final private SurfaceHolder surfaceHolder;
    private final Game game;
    boolean running;
    private final ArrayList<Object> object;
    private Sound sound;

    GameThread(SurfaceHolder surfaceHolder, Game game, ArrayList<Object> objects){
        super();
        game.object = object = objects;
        this.surfaceHolder = surfaceHolder;
        this.game = game;
        game.hero = (Hero) objects.get(7);
        game.stage = (Level) objects.get(6);
        sound = (Sound) objects.get(5);
        if (sound.musicOn) sound.musics.get(1).start();
    }

    void Reset(Point checkpoint){

        game.hero = new Hero(object);
        object.set(7, game.hero);
        game.stage =  new Level(object, checkpoint);
        object.set(6, game.stage);
        sound.musics.get(2).seekTo(0);
        sound.musics.get(1).seekTo(0);
        if (sound.musicOn) {
            if (sound.musics.get(2).isPlaying())sound.musics.get(2).pause();
            sound.musics.get(1).start();
        }
        System.out.println("----------------Starting stage again");
    }

    @Override
    public void run(){
        int FPS = 30;
        long startTime;
        long difTime;
        long sleepTime;
        long targetTime = 1000 / FPS;
        Canvas canvas;
        /*//>>>>>fps counting variables
        long totalTime = 0;
        long frameCount = 0;
        double averageFPS;*/
        while(running){
            startTime = System.nanoTime();
            canvas = null;
            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    this.game.update();
                    this.game.draw(canvas);
                }
            }
            catch (Exception e){e.printStackTrace();}
            finally {
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                difTime = (System.nanoTime() - startTime) / 1000000;
                sleepTime = targetTime - difTime;
                try{
                    sleep(sleepTime);
                }
                catch (Exception ignored){
                    ignored.printStackTrace();
                }
                /*/>>>>>>> just to count the average fps
                totalTime += System.nanoTime() - startTime;
                frameCount++;
                if (frameCount == FPS){
                    averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                    frameCount = 0;
                    totalTime = 0;
                    System.out.print("Average FPS= ");
                    System.out.println(averageFPS);
                }*/
            }
        }
    }
}
